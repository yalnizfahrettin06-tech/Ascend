package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*

/**
 * Seri kilometre taşları (rapor 4.1).
 *
 * Önceki sürümde sayaç sessizce artıyordu. Küçük başarıları görünür
 * kutlamak, bu kategorideki elde tutmanın en ucuz ve en etkili aracı.
 * Eski sürümdeki konfeti/kutlama katmanının hatası kutlamanın kendisi
 * değil, ana ekranda sürekli açık durmasıydı — burada yalnız eşik
 * geçildiğinde bir kez görünür.
 */
object Kilometre {
    val esikler = listOf(3, 7, 14, 30, 60, 100, 365)

    /** [seri] bir eşiği yeni geçtiyse o eşiği döner. */
    fun yeniEsik(seri: Int, kutlanan: Int): Int? =
        esikler.lastOrNull { it <= seri && it > kutlanan }
}

@Composable
fun KilometreKutlamasi(gun: Int, kapat: () -> Unit) {
    val dokun = guclüDokunus()
    var gorundu by remember { mutableStateOf(false) }
    val olcek by animateFloatAsState(
        if (gorundu) 1f else 0.7f,
        spring(dampingRatio = 0.5f, stiffness = 260f),
        label = "kutlama",
    )
    LaunchedEffect(Unit) { gorundu = true; dokun() }

    Dialog(onDismissRequest = kapat) {
        Column(
            Modifier
                .scale(olcek)
                .clip(RoundedCornerShape(Yaricap.lg))
                .background(Renk.yuzeyYuksek)
                .padding(Olcu.x3),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                AzimIkon.Alev, null,
                tint = Renk.accent,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.height(Olcu.lg))
            Text(
                "$gun",
                fontFamily = LoraSerif,
                style = MaterialTheme.typography.displaySmall,
                color = Renk.metin,
            )
            Text(
                stringResource(R.string.gun),
                style = MaterialTheme.typography.labelSmall,
                color = Renk.metinSonuk,
            )
            Spacer(Modifier.height(Olcu.lg))
            Text(
                stringResource(R.string.kilometre_tebrik),
                style = MaterialTheme.typography.bodyLarge,
                color = Renk.metinIkincil,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Olcu.xxl))
            AnaDugme(stringResource(R.string.devam_et), kapat)
        }
    }
}
