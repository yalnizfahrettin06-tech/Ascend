package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*

enum class KlasikMotif { BUST, COLUMN, ARCH }

/** Compact, opaque brand surface. Its ornament never participates in measurement. */
@Composable
fun MarkaBasligi(modifier: Modifier = Modifier, sutun: Boolean = false, content: @Composable RowScope.() -> Unit) {
    Box(modifier.fillMaxWidth().background(Renk.marka).clipToBounds()) {
        if (sutun) Box(Modifier.matchParentSize()) {
            Image(painterResource(R.drawable.art_laurel_column), null,
                Modifier.align(Alignment.CenterEnd).requiredSize(140.dp).offset(x = 24.dp, y = (-20).dp),
                contentScale = ContentScale.Fit, alpha = .12f, colorFilter = ColorFilter.tint(Renk.markaUstu))
        }
        CompositionLocalProvider(LocalContentColor provides Renk.markaUstu) {
            Row(Modifier.fillMaxWidth().heightIn(min = 68.dp).padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically, content = content)
        }
    }
}

/** Decorative only. A neutral tonal floor protects ink even in overlapping art.
 * At the maximum .55 opacity, each layer can darken white by at most .165.
 * Body and secondary labels are placed on their own opaque reading surface.
 */
@Composable
fun KlasikGorsel(motif: KlasikMotif, modifier: Modifier = Modifier, opacity: Float = .16f) {
    val source = when (motif) {
        KlasikMotif.BUST -> R.drawable.art_roman_bust
        KlasikMotif.COLUMN -> R.drawable.art_laurel_column
        KlasikMotif.ARCH -> R.drawable.art_marble_arch
    }
    val matrix = ColorMatrix(floatArrayOf(
        .3f, 0f, 0f, 0f, 178.5f,
        0f, .3f, 0f, 0f, 178.5f,
        0f, 0f, .3f, 0f, 178.5f,
        0f, 0f, 0f, 1f, 0f,
    ))
    val softEdge = if (motif == KlasikMotif.BUST) Modifier
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val imageHeight = minOf(size.height, size.width * 1.5f)
            val imageTop = (size.height - imageHeight) / 2f
            drawRect(Brush.verticalGradient(listOf(Color.White, Color.Transparent),
                startY = imageTop + imageHeight * .72f, endY = imageTop + imageHeight),
                blendMode = BlendMode.DstIn)
        } else Modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val imageWidth = minOf(size.width, size.height / 1.5f)
            val left = (size.width - imageWidth) / 2f
            drawRect(Brush.horizontalGradient(0f to Color.Transparent, .18f to Color.White,
                .85f to Color.White, 1f to Color.Transparent, startX = left, endX = left + imageWidth),
                blendMode = BlendMode.DstIn)
            drawRect(Brush.verticalGradient(.0f to Color.White, .8f to Color.White, 1f to Color.Transparent),
                blendMode = BlendMode.DstIn)
        }
    Image(painterResource(source), contentDescription = null, modifier = modifier.then(softEdge),
        contentScale = ContentScale.Fit,
        alpha = if (Renk.karanlikMi) opacity.coerceIn(0f, .07f) else opacity.coerceIn(0f, .55f),
        colorFilter = if (Renk.karanlikMi) null else ColorFilter.colorMatrix(matrix))
}

@Composable
fun EditoryalBaslik(etiket: String, baslik: String, aciklama: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.padding(top = 7.dp).width(22.dp).height(1.dp).background(Renk.metinIkincil))
            Text(etiket, color = Renk.metinIkincil, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.3.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(baslik, color = Renk.metin, fontFamily = LoraSerif, fontSize = 32.sp, lineHeight = 40.sp,
            modifier = Modifier.semantics { heading() })
        if (!aciklama.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(aciklama, color = Renk.metinIkincil, fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}
