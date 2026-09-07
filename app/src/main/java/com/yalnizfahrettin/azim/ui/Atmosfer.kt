package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*

enum class Atmosfer(val res: Int, val tr: String, val en: String) {
    ZIRVE(R.drawable.scene_summit, "Zirve", "Summit"),
    DENIZ(R.drawable.scene_sea, "Gün batımı", "Sunset"),
    BILGELIK(R.drawable.scene_wisdom, "Bilgelik", "Wisdom"),
    ORMAN(R.drawable.scene_forest, "Orman", "Forest");
    fun ad(dil: String) = if (dil == "en") en else tr
    companion object {
        fun grup(anahtar: String?) = when (anahtar) {
            "olumlamalar", "iliskiler", "zihin" -> DENIZ
            "filozoflar", "tasavvuf", "inanc" -> BILGELIK
            "disiplin", "ogrenme", "is" -> ORMAN
            else -> ZIRVE
        }
    }
}

@Composable
fun AtmosferResmi(atmosfer: Atmosfer, modifier: Modifier = Modifier, karartma: Float = .20f) {
    Box(modifier) {
        Image(painterResource(atmosfer.res), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = karartma), Color.Black.copy(alpha = karartma + .08f), Color.Black.copy(alpha = .58f)))))
    }
}

@Composable
fun KucukBaslik(metin: String, modifier: Modifier = Modifier) {
    Text(metin, modifier, style = MaterialTheme.typography.titleMedium, color = Renk.metin)
}

@Composable
fun YuvarlakIkon(ikon: ImageVector, aciklama: String, tikla: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = Renk.yuzeyYuksek, shape = CircleShape, modifier = modifier) {
        IconButton(onClick = tikla) { Icon(ikon, aciklama, tint = Renk.metin, modifier = Modifier.size(22.dp)) }
    }
}

fun cevir(dil: String, tr: String, en: String) = if (dil == "en") en else tr
