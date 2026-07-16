package com.refreshing.learnenglishwords.feature.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.domain.catalog.BootstrapCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val bootstrapCatalog: BootstrapCatalog,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StartupUiState>(StartupUiState.Loading)
    val uiState: StateFlow<StartupUiState> = _uiState.asStateFlow()

    private val _effects = Channel<StartupEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        bootstrap()
    }

    fun onIntent(intent: StartupIntent) {
        when (intent) {
            StartupIntent.RetryClicked -> bootstrap()
        }
    }

    private fun bootstrap() {
        _uiState.value = StartupUiState.Loading
        viewModelScope.launch {
            try {
                bootstrapCatalog.run()
                _uiState.value = StartupUiState.Ready
                _effects.send(StartupEffect.NavigateToTopics)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = StartupUiState.Error(
                    message = e.message ?: "Failed to load catalog",
                )
            }
        }
    }
}
