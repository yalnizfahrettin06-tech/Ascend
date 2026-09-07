package com.yalnizfahrettin.azim.ui

import android.graphics.DashPathEffect
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.LoraSerif
import com.yalnizfahrettin.azim.core.Olcu
import com.yalnizfahrettin.azim.core.Renk
import com.yalnizfahrettin.azim.core.Yaricap
import com.yalnizfahrettin.azim.data.Soz
import kotlinx.coroutines.delay

/*
 * ONBOARDING SAHNELERİ
 *
 * Plandaki §3.5'ten kalan üç madde burada:
 *  1) Logo çizimi — harflerin konturu soldan sağa çiziliyor
 *  2) Zemin hareketi — yalnız onboarding'de, çok yavaş radyal gradyan
 *  3) Kapanış — "Hazırım" sonrası ilk sözün doğuşu
 */

/**
 * "ASCEND" yazısının kendini çizmesi.
 *
 * Metin bir Path'e dönüştürülüp DashPathEffect ile kısmi olarak gösteriliyor:
 * kesik çizgi aralığı ilerlemeye göre büyüdükçe kontur soldan sağa "çiziliyor"
 * izlenimi veriyor. Çizim bitince dolgu belirir ve altındaki accent çizgi
 * genişler.
 */
@Composable
fun LogoCizimi(modifier: Modifier = Modifier, calisiyor: Boolean = true) {
    val ctx = LocalContext.current
    val lora = remember {
        runCatching {
            androidx.core.content.res.ResourcesCompat.getFont(ctx, R.font.lora)
        }.getOrNull() ?: android.graphics.Typeface.SERIF
    }

    var basla by remember { mutableStateOf(false) }
    LaunchedEffect(calisiyor) { if (calisiyor) { delay(120); basla = true } }

    val cizim by animateFloatAsState(
        if (basla) 1f else 0f,
        tween(1100, easing = LinearEasing),
        label = "cizim",
    )
    val dolgu by animateFloatAsState(
        if (cizim > 0.85f) 1f else 0f,
        tween(520),
        label = "dolgu",
    )
    val cizgi by animateFloatAsState(
        if (cizim > 0.9f) 1f else 0f,
        tween(600),
        label = "altcizgi",
    )

    val metinRengi = Renk.metin
    val accent = Renk.accent

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.fillMaxWidth().height(64.dp)) {
            drawIntoCanvas { tuval ->
                val boya = android.graphics.Paint().apply {
                    isAntiAlias = true
                    typeface = lora
                    textSize = size.height * 0.72f
                    textAlign = android.graphics.Paint.Align.CENTER
                    letterSpacing = 0.12f
                }
                val yol = Path()
                boya.getTextPath("ASCEND", 0, 6, size.width / 2f, size.height * 0.78f, yol)

                // Dolgu: çizim bitince belirir
                if (dolgu > 0f) {
                    boya.style = android.graphics.Paint.Style.FILL
                    boya.color = metinRengi.copy(alpha = dolgu).toArgb()
                    tuval.nativeCanvas.drawPath(yol, boya)
                }

                // Kontur: ilerlemeye göre açığa çıkar
                if (cizim < 1f) {
                    val uzunluk = yolUzunlugu(yol)
                    boya.style = android.graphics.Paint.Style.STROKE
                    boya.strokeWidth = size.height * 0.035f
                    boya.color = metinRengi.toArgb()
                    boya.pathEffect = DashPathEffect(
                        floatArrayOf(uzunluk * cizim, uzunluk), 0f,
                    )
                    tuval.nativeCanvas.drawPath(yol, boya)
                }
            }
        }
        Spacer(Modifier.height(Olcu.md))
        Box(
            Modifier
                .fillMaxWidth(0.16f * cizgi)
                .height(2.dp)
                .clip(RoundedCornerShape(Yaricap.hap))
                .background(accent),
        )
    }
}

/** Path'in tüm konturlarının toplam uzunluğu. */
private fun yolUzunlugu(yol: Path): Float {
    val olcer = PathMeasure(yol, false)
    var toplam = 0f
    do { toplam += olcer.length } while (olcer.nextContour())
    return toplam.coerceAtLeast(1f)
}

/**
 * Onboarding zemin hareketi.
 *
 * Çok yavaş kayan radyal gradyan. YALNIZ ONBOARDING'DE — eski sürümün
 * "aurora" hatası ana ekranda sürekli açık durmasıydı; burada yalnız ilk
 * kurulum ekranlarında, birkaç dakikalığına.
 */
@Composable
fun OnboardingZemini(modifier: Modifier = Modifier) {
    val gecis = rememberInfiniteTransition(label = "zemin")
    val kayma by gecis.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "kayma",
    )
    val accent = Renk.accent
    Canvas(modifier) {
        val merkez = Offset(
            size.width * (0.25f + 0.5f * kayma),
            size.height * (0.18f + 0.12f * kayma),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.075f), Color.Transparent),
                center = merkez,
                radius = size.minDimension * 0.9f,
            ),
            size = size,
        )
    }
}

/**
 * Kapanış sahnesi — "Hazırım" sonrası ilk sözün doğuşu.
 *
 * Doğrudan ana ekrana atlamak yerine uygulamanın vaadi ilk saniyede teslim
 * ediliyor: söz kelime kelime beliriyor, sonra arayüz devralıyor.
 */
@Composable
fun KapanisSahnesi(soz: Soz?, dil: String, bitti: () -> Unit) {
    val kelimeler = remember(soz, dil) {
        soz?.metin(dil)?.split(" ") ?: emptyList()
    }
    var gorunen by remember { mutableIntStateOf(0) }
    var yazarGorundu by remember { mutableStateOf(false) }

    LaunchedEffect(kelimeler) {
        delay(260)
        for (i in kelimeler.indices) {
            gorunen = i + 1
            delay(115)
        }
        yazarGorundu = true
        delay(1100)
        bitti()
    }

    val yazarAlfa by animateFloatAsState(
        if (yazarGorundu) 1f else 0f, tween(520), label = "yazar",
    )

    Box(
        Modifier.fillMaxSize().background(Renk.zemin),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(horizontal = Olcu.x3),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = kelimeler.take(gorunen).joinToString(" "),
                fontFamily = LoraSerif,
                style = MaterialTheme.typography.displaySmall,
                color = Renk.metin,
                textAlign = TextAlign.Center,
            )
            if (soz != null) {
                Spacer(Modifier.height(Olcu.xxl))
                Text(
                    "— ${soz.yazar}",
                    fontFamily = LoraSerif,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Renk.metinIkincil.copy(alpha = yazarAlfa),
                )
            }
        }
    }
}
