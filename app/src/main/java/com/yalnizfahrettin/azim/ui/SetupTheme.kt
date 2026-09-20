package com.yalnizfahrettin.azim.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.toArgb
import com.yalnizfahrettin.azim.core.*

/** A dark brand shell without changing the user's persisted home appearance. */
@Composable
fun SetupTheme(content: @Composable () -> Unit) {
    val outer = Renk
    val view = LocalView.current
    val context = LocalContext.current
    val activity = generateSequence(context) { (it as? android.content.ContextWrapper)?.baseContext }
        .filterIsInstance<android.app.Activity>().firstOrNull()
    DisposableEffect(activity, outer.karanlikMi, outer.zemin) {
        onDispose { activity?.window?.let { window ->
            @Suppress("DEPRECATION")
            window.navigationBarColor = outer.zemin.toArgb()
            window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(outer.zemin.toArgb()))
            androidx.core.view.WindowCompat.getInsetsController(window,view).apply {
                isAppearanceLightStatusBars = !outer.karanlikMi
                isAppearanceLightNavigationBars = !outer.karanlikMi
            }
        } }
    }
    AzimTema(modu = TemaModu.KARANLIK, palet = Palet.MONO, icerik = content)
}
