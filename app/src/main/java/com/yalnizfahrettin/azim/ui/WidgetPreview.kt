package com.yalnizfahrettin.azim.ui

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.yalnizfahrettin.azim.widget.WidgetSecimi
import com.yalnizfahrettin.azim.widget.WidgetTasarimi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WidgetPreviewState {
    var bitmap by mutableStateOf<Bitmap?>(null)
        internal set
    var failed by mutableStateOf(false)
        internal set
    internal var attempt by mutableIntStateOf(0)
    fun retry() { attempt++ }
}

/** All preview surfaces use the exact renderer used by the installed widget. */
@Composable
fun rememberWidgetPreview(config: WidgetSecimi, text: String, height: Int = 540): WidgetPreviewState {
    val context = LocalContext.current
    val scale = LocalDensity.current.fontScale
    val state = remember(config, text, height, scale) { WidgetPreviewState() }
    LaunchedEffect(state, state.attempt) {
        state.failed = false
        try {
            state.bitmap = withContext(Dispatchers.Default) {
                WidgetTasarimi.render(context, config, text, "Ascend", 1080, height)
            }
        } catch(e: CancellationException) { throw e }
        catch(_: Exception) { state.failed = true }
    }
    return state
}
