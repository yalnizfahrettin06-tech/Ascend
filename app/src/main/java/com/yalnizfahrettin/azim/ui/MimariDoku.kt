package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.core.Renk

/** Quiet architectural light. This layer never intercepts input or accessibility. */
@Composable
fun MimariIsik(modifier: Modifier = Modifier) {
    val ink = Renk.metin
    Canvas(modifier) {
        for (n in 0..3) {
            val x = size.width * (n * .42f - .6f)
            val shadow = Path().apply {
                moveTo(x, 0f); lineTo(x + size.width * .10f, 0f)
                lineTo(x + size.width * .95f, size.height * .60f)
                lineTo(x + size.width * .75f, size.height * .60f); close()
            }
            drawPath(shadow, Brush.linearGradient(listOf(ink.copy(alpha = .035f), Color.Transparent),
                Offset(x, 0f), Offset(x + size.width, size.height * .6f)))
        }
    }
}

@Composable
fun KoleksiyonRolefi(key: String, modifier: Modifier = Modifier) {
    val ink = Renk.metin
    Box(modifier) {
        if (key == "azim") Canvas(Modifier.matchParentSize()) {
            for (n in 0..3) {
                val w = size.width * (.16f + n * .10f)
                val h = size.height * .085f
                val y = size.height - (4 - n) * h
                drawRect(Brush.verticalGradient(listOf(ink.copy(alpha = .045f), Color.Transparent), y, y + h),
                    Offset(size.width - w, y), Size(w, h))
                drawLine(ink.copy(alpha = .07f), Offset(size.width - w, y), Offset(size.width, y), .6.dp.toPx())
            }
        } else KlasikGorsel(when (key) {
            "olumlamalar", "iliskiler", "inanc" -> KlasikMotif.COLUMN
            "disiplin", "filozoflar", "is" -> KlasikMotif.COLUMN
            else -> KlasikMotif.ARCH
        }, Modifier.align(Alignment.BottomEnd).offset(x = 30.dp, y = 20.dp).width(120.dp).fillMaxHeight(.88f), opacity = .32f)
    }
}
