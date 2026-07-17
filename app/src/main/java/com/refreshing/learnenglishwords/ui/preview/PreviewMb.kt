package com.refreshing.learnenglishwords.ui.preview

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showSystemUi = true,
    name = "A Default",
    device = Devices.DEFAULT,
)
@Preview(
    showSystemUi = true,
    name = "A Large Font",
    device = Devices.DEFAULT,
    fontScale = 2.5f,
)
@Preview(
    showSystemUi = true,
    name = "Fold",
    device = Devices.PIXEL_FOLD,
)
annotation class PreviewMb
