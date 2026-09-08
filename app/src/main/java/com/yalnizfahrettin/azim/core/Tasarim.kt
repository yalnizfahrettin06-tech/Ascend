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
)



object Olcu {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val x3 = 32.dp
    val x4 = 40.dp
    val x5 = 48.dp
}


object Yaricap {
    val sm = 12.dp
    val md = 18.dp
    val lg = 24.dp
    val xl = 30.dp
    val hap = 999.dp
}

val LoraSerif = FontFamily(
    Font(R.font.lora, FontWeight.Normal),
    Font(R.font.lora, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.lora_italic, FontWeight.Normal, FontStyle.Italic),
)


val AzimTipografi = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp),
    displaySmall = TextStyle(
        fontFamily = LoraSerif, fontWeight = FontWeight.Normal,
        fontSize = 27.sp, lineHeight = 36.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = LoraSerif, fontWeight = FontWeight.Normal,
        fontSize = 22.sp, lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 12.sp,
        lineHeight = 14.sp, letterSpacing = 1.2.sp,
    ),
)

val LocalAzimRenk = staticCompositionLocalOf { paletiCoz(Palet.MONO, karanlik = true, oled = false) }

enum class TemaModu { SISTEM, AYDINLIK, KARANLIK, OLED }


@Composable
fun AzimTema(
    modu: TemaModu = TemaModu.SISTEM,
    palet: Palet = Palet.MONO,
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
    val dinamikDestekli = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
    val m3 = if (dinamik && dinamikDestekli) {
        if (renk.karanlikMi) androidx.compose.material3.dynamicDarkColorScheme(ctx)
        else androidx.compose.material3.dynamicLightColorScheme(ctx)
    } else if (renk.karanlikMi) {
        darkColorScheme(
            secondary = renk.accent, secondaryContainer = renk.accentZemin, onSecondaryContainer = renk.metin,
            surfaceVariant = renk.yuzeyYuksek, onSurfaceVariant = renk.metinIkincil,
            primary = renk.accent, background = renk.zemin, surface = renk.yuzey,
            onPrimary = if (renk.karanlikMi) Color(0xFF121416) else Color.White, onBackground = renk.metin, onSurface = renk.metin,
        )
    } else {
        lightColorScheme(
            secondary = renk.accent, secondaryContainer = renk.accentZemin, onSecondaryContainer = renk.metin,
            surfaceVariant = renk.yuzeyYuksek, onSurfaceVariant = renk.metinIkincil,
            primary = renk.accent, background = renk.zemin, surface = renk.yuzey,
            onPrimary = if (renk.karanlikMi) Color(0xFF121416) else Color.White, onBackground = renk.metin, onSurface = renk.metin,
        )
    }
    val view = androidx.compose.ui.platform.LocalView.current
    val activity = generateSequence(ctx) { (it as? android.content.ContextWrapper)?.baseContext }.filterIsInstance<android.app.Activity>().firstOrNull()
    SideEffect { activity?.let {
        it.window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(renk.zemin.toArgb()))
        androidx.core.view.WindowCompat.getInsetsController(it.window, view).apply {
        isAppearanceLightStatusBars = !renk.karanlikMi
        isAppearanceLightNavigationBars = !renk.karanlikMi
    } } }
    CompositionLocalProvider(LocalAzimRenk provides renk) {
        MaterialTheme(colorScheme = m3, typography = AzimTipografi, content = icerik)
    }
}


@Composable
fun zeminFircasi(): Brush {
    val r = LocalAzimRenk.current
    return Brush.verticalGradient(
        0f to r.yuzey.copy(alpha = if (r.karanlikMi) 0.55f else 0.7f),
        0.32f to r.zemin,
        1f to r.zemin,
    )
}


@Composable
fun accentIzi(): Brush {
    val r = LocalAzimRenk.current
    return Brush.radialGradient(
        listOf(r.accent.copy(alpha = if (r.karanlikMi) 0.055f else 0.05f), Color.Transparent),
    )
}


val Renk: AzimRenkleri
    @Composable get() = LocalAzimRenk.current
