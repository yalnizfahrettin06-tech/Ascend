package com.yalnizfahrettin.azim.core

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/*
 * ETKİLEŞİM KATMANI (rapor 2.1 ve 2.2)
 *
 * Önceki sürümde hiç haptik geri bildirim yoktu ve basma efekti sistem
 * varsayılanıydı. Premium his büyük ölçüde dokunuşta yaşar; bu kategorideki
 * her ciddi uygulamada (Headspace, Calm, Duolingo) bu katman vardır.
 *
 * Burada iki şey var:
 *  - `AzimBasma`: basınca %96'ya küçülüp yay ile geri dönen dokunma
 *  - Haptik: kullanıcı ayarlardan kapatabilir, varsayılan açık
 */

val LocalHaptikAcik = compositionLocalOf { true }

@Composable
fun HaptikSaglayici(acik: Boolean, icerik: @Composable () -> Unit) =
    CompositionLocalProvider(LocalHaptikAcik provides acik, content = icerik)

/** Hafif dokunuş — seçim, sekme değişimi, düğme. */
@Composable
fun hafifDokunus(): () -> Unit {
    val haptik = LocalHapticFeedback.current
    val acik = LocalHaptikAcik.current
    return remember(acik) {
        { if (acik) haptik.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    }
}

/** Belirgin dokunuş — favori ekleme, kilit açma, kilometre taşı. */
@Composable
fun guclüDokunus(): () -> Unit {
    val haptik = LocalHapticFeedback.current
    val acik = LocalHaptikAcik.current
    return remember(acik) {
        { if (acik) haptik.performHapticFeedback(HapticFeedbackType.LongPress) }
    }
}

/**
 * Tıklanabilir + basma animasyonu + haptik, tek modifier'da.
 *
 * @param guclu true ise belirgin haptik (favori, kilit açma gibi anlar)
 */
fun Modifier.azimTikla(
    guclu: Boolean = false,
    etiket: String? = null,
    tikla: () -> Unit,
): Modifier = composed {
    val kaynak = remember { MutableInteractionSource() }
    val basili by kaynak.collectIsPressedAsState()
    val olcek by animateFloatAsState(
        targetValue = if (basili) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "basma",
    )
    val dokun = if (guclu) guclüDokunus() else hafifDokunus()
    this
        .scale(olcek)
        .clickable(
            interactionSource = kaynak,
            indication = null,
            onClickLabel = etiket,
        ) {
            dokun()
            tikla()
        }
}
