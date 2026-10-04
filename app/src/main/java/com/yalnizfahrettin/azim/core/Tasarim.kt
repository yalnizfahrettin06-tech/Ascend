package com.yalnizfahrettin.azim.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R



@Immutable
data class AzimRenkleri(
    val zemin: Color,
    val yuzey: Color,
    val yuzeyYuksek: Color,
    val kenarlik: Color,
    val kenarlikGuclu: Color,
    val metin: Color,
    val metinIkincil: Color,
    val metinSonuk: Color,
    val accent: Color,
    val accentSonuk: Color,
    val accentZemin: Color,

    val accentDerin: Color,
    val karanlikMi: Boolean,
    val marka: Color = Color(0xFF343432),
    val markaUstu: Color = Color(0xFFFFF9F5),
    // Scoped refinement surfaces; do not recolor global primary buttons.
    val koleksiyonYuzeyi: Color = yuzey,
    val markaYuzeyi: Color = accentZemin,
    val markaSessizYuzeyi: Color = yuzey,
    val markaBasiliYuzeyi: Color = accentZemin,
)






val LoraSerif = FontFamily(
    Font(R.font.lora, FontWeight.Normal),
    Font(R.font.lora, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.lora_italic, FontWeight.Normal, FontStyle.Italic),
)


@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val ArayuzFont = FontFamily(
    Font(R.font.inter, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.inter, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.inter, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.inter, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

private fun uiType(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = ArayuzFont, fontWeight = weight, fontSize = size.sp, lineHeight = height.sp)

val AzimTipografi = Typography(
    displayLarge = uiType(40, 48, FontWeight.SemiBold),
    displayMedium = uiType(36, 44, FontWeight.SemiBold),
    displaySmall = uiType(32, 38, FontWeight.SemiBold),
    headlineLarge = uiType(32, 38, FontWeight.SemiBold),
    headlineMedium = uiType(28, 34, FontWeight.SemiBold),
    headlineSmall = uiType(24, 30, FontWeight.SemiBold),
    titleLarge = uiType(20, 28, FontWeight.SemiBold),
    titleMedium = uiType(16, 22, FontWeight.Medium),
    titleSmall = uiType(14, 20, FontWeight.Medium),
    bodyLarge = uiType(16, 24), bodyMedium = uiType(15, 23), bodySmall = uiType(12, 18),
    labelLarge = uiType(15, 20, FontWeight.Medium),
    labelMedium = uiType(13, 18, FontWeight.Medium), labelSmall = uiType(12, 16),
)

val LocalAzimRenk = staticCompositionLocalOf { paletiCoz(Palet.MERMER, karanlik = false, oled = false) }

enum class TemaModu { SISTEM, AYDINLIK, KARANLIK, OLED }

fun com.yalnizfahrettin.azim.data.ThemeMode.asTemaModu(): TemaModu = when (this) {
    com.yalnizfahrettin.azim.data.ThemeMode.SYSTEM -> TemaModu.SISTEM
    com.yalnizfahrettin.azim.data.ThemeMode.LIGHT -> TemaModu.AYDINLIK
    com.yalnizfahrettin.azim.data.ThemeMode.DARK -> TemaModu.KARANLIK
}


@Composable
fun AzimTema(
    modu: TemaModu = TemaModu.SISTEM,
    palet: Palet = Palet.MERMER,
    dinamik: Boolean = false,
    icerik: @Composable () -> Unit,
) {
    val sistemKaranlik = isSystemInDarkTheme()
    val karanlik = when (modu) {
        TemaModu.SISTEM -> sistemKaranlik
        TemaModu.AYDINLIK -> false
        else -> true
    }
    val renk = paletiCoz(palet, karanlik, oled = modu == TemaModu.OLED)
    val ctx = androidx.compose.ui.platform.LocalContext.current
    // Keep the serialized dynamic preference for compatibility; wallpaper colors
    // cannot override the deliberately restricted v8 palette.
    val base = if (renk.karanlikMi) darkColorScheme() else lightColorScheme()
    val onAccent = if (renk.karanlikMi) Color(0xFF121416) else Color.White
    val m3 = base.copy(
        primary = renk.marka, onPrimary = renk.markaUstu,
        primaryContainer = renk.accentZemin, onPrimaryContainer = renk.metin,
        secondary = renk.accent, onSecondary = onAccent,
        secondaryContainer = renk.accentZemin, onSecondaryContainer = renk.metin,
        tertiary = renk.accent, onTertiary = onAccent,
        tertiaryContainer = renk.accentZemin, onTertiaryContainer = renk.metin,
        background = renk.zemin, onBackground = renk.metin,
        surface = renk.yuzey, onSurface = renk.metin,
        surfaceVariant = renk.yuzeyYuksek, onSurfaceVariant = renk.metinIkincil,
        surfaceTint = Color.Transparent,
        outline = renk.kenarlikGuclu, outlineVariant = renk.kenarlik,
        inverseSurface = renk.metin, inverseOnSurface = renk.zemin,
        inversePrimary = renk.accentZemin,
        surfaceBright = renk.zemin, surfaceDim = renk.yuzeyYuksek,
        surfaceContainerLowest = renk.zemin, surfaceContainerLow = renk.yuzey,
        surfaceContainer = renk.yuzey, surfaceContainerHigh = renk.yuzeyYuksek,
        surfaceContainerHighest = renk.yuzeyYuksek,
        error = if (renk.karanlikMi) Color(0xFFFFB4A9) else Color(0xFFB3261E),
        onError = onAccent, errorContainer = renk.accentZemin, onErrorContainer = renk.metin,
    )
    val view = androidx.compose.ui.platform.LocalView.current
    val activity = generateSequence(ctx) { (it as? android.content.ContextWrapper)?.baseContext }.filterIsInstance<android.app.Activity>().firstOrNull()
    SideEffect { activity?.let {
        @Suppress("DEPRECATION")
        it.window.navigationBarColor = renk.zemin.toArgb()
        if (android.os.Build.VERSION.SDK_INT >= 29) it.window.isNavigationBarContrastEnforced = false
        it.window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(renk.zemin.toArgb()))
        androidx.core.view.WindowCompat.getInsetsController(it.window, view).apply {
        isAppearanceLightStatusBars = !renk.karanlikMi
        isAppearanceLightNavigationBars = !renk.karanlikMi
    } } }
    CompositionLocalProvider(LocalAzimRenk provides renk) {
        MaterialTheme(colorScheme = m3, typography = AzimTipografi, shapes = androidx.compose.material3.Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        ), content = icerik)
    }
}




val Renk: AzimRenkleri
    @Composable get() = LocalAzimRenk.current
