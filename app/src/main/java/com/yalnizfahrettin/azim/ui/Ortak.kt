package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.core.Olcu
import com.yalnizfahrettin.azim.core.Renk
import com.yalnizfahrettin.azim.core.Yaricap

/** Bölüm başlığı — küçük, harf aralıklı, nötr. Accent KULLANMAZ. */
@Composable
fun BolumBasligi(metin: String, modifier: Modifier = Modifier) {
    Text(
        text = metin.uppercase(),
        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
        color = Renk.metinSonuk,
        modifier = modifier.padding(horizontal = Olcu.xl, vertical = Olcu.md),
    )
}

/**
 * Standart yüzey kartı.
 * Eski sürümde kartlar kalın accent kenarlıkla çiziliyordu ve ekranı bölüyordu.
 * Burada kenarlık 1dp ve nötr; ayrım yüzey tonundan geliyor.
 */
@Composable
fun Kart(
    modifier: Modifier = Modifier,
    icerik: @Composable () -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Yaricap.lg))
            .background(Renk.yuzey)
            .border(1.dp, Renk.kenarlik, RoundedCornerShape(Yaricap.lg))
            .padding(Olcu.lg),
    ) { icerik() }
}

/** Boş durum — ikon + başlık + açıklama. Her ekranda aynı. */
@Composable
fun BosDurum(
    ikon: ImageVector,
    baslik: String,
    aciklama: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().padding(Olcu.x3),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(ikon, null, tint = Renk.metinSonuk, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(Olcu.lg))
        Text(
            baslik,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = Renk.metin,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Olcu.sm))
        Text(
            aciklama,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = Renk.metinIkincil,
            textAlign = TextAlign.Center,
        )
    }
}

/** İnce ilerleme çubuğu — dolu kısım accent'in tek meşru kullanımlarından biri. */
@Composable
fun IlerlemeCubugu(oran: Float, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(Yaricap.hap))
            .background(Renk.kenarlik),
    ) {
        Box(
            Modifier
                .fillMaxWidth(oran.coerceIn(0f, 1f))
                .height(4.dp)
                .clip(RoundedCornerShape(Yaricap.hap))
                .background(Renk.accent),
        )
    }
}

/** Ayar satırı — sol etiket, sağ değer/kontrol. */
@Composable
fun AyarSatiri(
    baslik: String,
    altBaslik: String? = null,
    sag: @Composable () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = Olcu.xl, vertical = Olcu.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                baslik,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = Renk.metin,
            )
            if (altBaslik != null) {
                Text(
                    altBaslik,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Renk.metinSonuk,
                )
            }
        }
        Spacer(Modifier.size(Olcu.lg))
        sag()
    }
}
