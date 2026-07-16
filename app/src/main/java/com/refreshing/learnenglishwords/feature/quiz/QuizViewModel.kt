package com.refreshing.learnenglishwords.feature.quiz

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.core.model.QuizTask
import com.refreshing.learnenglishwords.core.model.WordSnapshot
import com.refreshing.learnenglishwords.domain.analytics.AnalyticsTracker
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
import com.refreshing.learnenglishwords.domain.quiz.BuildQuizSession
import com.refreshing.learnenglishwords.domain.quiz.EvaluateQuizAnswer
import com.refreshing.learnenglishwords.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: CatalogRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val buildQuizSession: BuildQuizSession,
    private val evaluateQuizAnswer: EvaluateQuizAnswer,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val subtopicUid: String =
        Uri.decode(checkNotNull(savedStateHandle["subtopicUid"]))

    // subtopicKey is the last segment of "topicKey/subtopicKey"
    private val subtopicKey: String = subtopicUid.substringAfterLast("/")

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _effects = Channel<QuizEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    // In-memory queue; not persisted (process death = rebuilt from saved progress)
    private val queue = ArrayDeque<QuizTask>()
    private var questionsAnswered = 0
    private var mistakeCount = 0
    private val newlyLearnedKeys = mutableSetOf<Triple<String, String, String>>()
    private var initialQueueSize = 0

    init {
        viewModelScope.launch { initSession() }
    }

    private suspend fun initSession() {
        val settings = settingsRepository.observeSettings().first()
        val availableLangs = settingsRepository.observeAvailableLanguages().first()
        val mainLang = catalogRepository.observeMainLanguage().first()
        // Quiz uses a single language chosen in Settings
        val orderedSelected = listOf(settings.quizLanguage).filter { it in availableLangs }

        val wordEntries = catalogRepository.observeWordEntries(subtopicUid).first()
        val progressList = progressRepository.getProgressForSubtopic(subtopicUid)

        val progressByWord = progressList.groupBy { it.wordUid }.mapValues { (_, rows) ->
            rows.associate { (it.sourceLanguage to it.targetLanguage) to it }
        }

        val snapshots = wordEntries.map { entry ->
            WordSnapshot(
                wordUid = entry.wordUid,
                position = entry.position,
                translations = entry.translations,
                progressByDirection = progressByWord[entry.wordUid] ?: emptyMap(),
            )
        }

        val tasks = buildQuizSession.build(
            words = snapshots,
            mainLanguage = mainLang,
            orderedSelectedLanguages = orderedSelected,
            direction = settings.translationDirection,
            order = settings.quizOrder,
        )

        queue.addAll(tasks)
        initialQueueSize = tasks.size

        if (tasks.isEmpty()) {
            _uiState.value = QuizUiState(isLoading = false, isEmpty = true)
        } else {
            analyticsTracker.trackQuizStart(subtopicKey)
            showCurrentTask()
        }
    }

    fun onIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.OptionSelected -> handleAnswer(intent.option)
            QuizIntent.ContinueClicked -> advance()
            QuizIntent.CloseClicked -> {
                if (_uiState.value.isComplete || queue.isEmpty()) {
                    viewModelScope.launch { _effects.send(QuizEffect.NavigateBack) }
                } else {
                    _uiState.value = _uiState.value.copy(showCloseConfirm = true)
                }
            }
            QuizIntent.CloseConfirmed ->
                viewModelScope.launch { _effects.send(QuizEffect.NavigateBack) }
            QuizIntent.CloseDismissed ->
                _uiState.value = _uiState.value.copy(showCloseConfirm = false)
        }
    }

    private fun handleAnswer(selected: String) {
        val task = queue.firstOrNull() ?: return
        val isCorrect = selected.trim().lowercase() == task.correctAnswer.trim().lowercase()
        questionsAnswered++
        analyticsTracker.trackQuizAnswer(subtopicKey, isCorrect)

        viewModelScope.launch {
            val existing = progressRepository.getProgressForSubtopic(subtopicUid)
                .find { it.wordUid == task.wordUid && it.sourceLanguage == task.sourceLanguage && it.targetLanguage == task.targetLanguage }

            val updated = evaluateQuizAnswer.evaluate(
                existing = existing,
                wordUid = task.wordUid,
                sourceLanguage = task.sourceLanguage,
                targetLanguage = task.targetLanguage,
                learningRevision = 1,
                isCorrect = isCorrect,
            )
            progressRepository.upsertProgress(updated)

            if (isCorrect) {
                if (updated.isLearned) {
                    newlyLearnedKeys += Triple(task.wordUid, task.sourceLanguage, task.targetLanguage)
                }
                queue.removeFirst()
            } else {
                mistakeCount++
                queue.removeFirst()
                val insertAt = Random.nextInt(3, 8).coerceAtMost(queue.size)
                queue.add(insertAt, task)
            }

            _uiState.value = _uiState.value.copy(
                selectedOption = selected,
                isCorrect = isCorrect,
            )
        }
    }

    private fun advance() {
        if (queue.isEmpty()) {
            analyticsTracker.trackQuizComplete(subtopicKey)
            _uiState.value = _uiState.value.copy(
                isComplete = true,
                currentTask = null,
                selectedOption = null,
                isCorrect = null,
                questionsAnswered = questionsAnswered,
                mistakeCount = mistakeCount,
                newlyLearnedCount = newlyLearnedKeys.size,
                remainingCount = 0,
            )
        } else {
            showCurrentTask()
        }
    }

    private fun showCurrentTask() {
        val task = queue.firstOrNull() ?: return
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            currentTask = task,
            currentIndex = questionsAnswered + 1,
            totalTasks = questionsAnswered + queue.size,
            selectedOption = null,
            isCorrect = null,
        )
    }
}
