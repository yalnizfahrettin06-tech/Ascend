package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
        } else Modifier
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
