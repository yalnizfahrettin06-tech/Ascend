package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Sozler
import kotlin.math.roundToInt

/*
 * BİLDİRİM PLANI — ayarlarda ve onboarding'de aynı bileşen
 *
 * Önceden adet ve saat aralığı ayrı satırlardaydı ve aralık dört ayrı −/+
 * düğmesiyle kontrol ediliyordu; hantaldı ve kullanıcı sonucu göremiyordu.
 *
 * Burada üçü bir arada:
 *  1) Adet (1-7)
 *  2) Saat aralığı (çift uçlu kaydırıcı)
 *  3) CANLI ÖNİZLEME — gerçek dağıtım mantığıyla gün çizgisi üzerinde
 *     noktalar. Soyut bir sayı yerine "günüm böyle görünecek".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BildirimPlani(
    adet: Int,
    baslangic: Int,
    bitis: Int,
    seciliKategoriSayisi: Int,
    adetDegisti: (Int) -> Unit,
    araligiDegisti: (Int, Int) -> Unit,
    havuzaGit: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {

        // --- adet ---
        Text(
            stringResource(R.string.gunluk_adet),
            style = MaterialTheme.typography.bodyLarge,
            color = Renk.metin,
        )
        Spacer(Modifier.height(Olcu.md))
        val adetEtiketi = stringResource(R.string.gunluk_adet)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Slider(value = adet.toFloat(), onValueChange = { adetDegisti(it.roundToInt()) },
                valueRange = 1f..7f, steps = 5, modifier = Modifier.weight(1f).semantics { contentDescription = adetEtiketi })
            Text("$adet", color = Renk.accent, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 16.dp))
        }
        Spacer(Modifier.height(Olcu.xxl))

        // --- saat aralığı ---
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.saat_araligi),
                style = MaterialTheme.typography.bodyLarge,
                color = Renk.metin,
                modifier = Modifier.weight(1f),
            )
            Text(
                "%02d:00 – %02d:00".format(baslangic, bitis),
                style = MaterialTheme.typography.labelLarge,
                color = Renk.accent,
            )
        }
        Spacer(Modifier.height(Olcu.sm))
        RangeSlider(
            value = baslangic.toFloat()..bitis.toFloat(),
            onValueChange = { aralik ->
                val b = aralik.start.roundToInt().coerceIn(0, 22)
                val s = aralik.endInclusive.roundToInt().coerceIn(b + 1, 24)
                araligiDegisti(b, s)
            },
            valueRange = 0f..24f,
            steps = 23,
            colors = SliderDefaults.colors(
                thumbColor = Renk.accent,
                activeTrackColor = Renk.accent,
                inactiveTrackColor = Renk.kenarlik,
                activeTickColor = Renk.accentSonuk,
                inactiveTickColor = Renk.kenarlik,
            ),
        )

        Spacer(Modifier.height(Olcu.md))

        // --- canlı önizleme ---
        GunOnizlemesi(adet, baslangic, bitis)

        // --- havuz dengesi ---
        if (seciliKategoriSayisi > 0) {
            Spacer(Modifier.height(Olcu.lg))
            Text(stringResource(R.string.asc_plan_notu), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Günün çizgisi ve üzerine yayılmış bildirim noktaları.
 * Dağılım Planlayici ile aynı mantık: aralık N dilime bölünür, her
 * bildirim diliminin ortasına düşer.
 */
@Composable
private fun GunOnizlemesi(adet: Int, baslangic: Int, bitis: Int) {
    val genislik = (bitis - baslangic).coerceAtLeast(1)
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(30.dp)) {
            // gün çizgisi
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(Yaricap.hap))
                    .background(Renk.kenarlik),
            )
            BoxWithConstraintsSarmal { toplamEn ->
                repeat(adet) { i ->
                    val dilim = genislik.toFloat() / adet
                    val saat = baslangic + dilim * i + dilim / 2f
                    val oran = ((saat - baslangic) / genislik).coerceIn(0f, 1f)
                    val yer by animateFloatAsState(oran, tween(320), label = "nokta$i")
                    Box(
                        Modifier
                            .offset(x = (toplamEn - 10.dp) * yer)
                            .size(10.dp)
                            .align(Alignment.CenterStart)
                            .clip(CircleShape)
                            .background(Renk.accent),
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Text(
                "%02d:00".format(baslangic),
                style = MaterialTheme.typography.labelSmall,
                color = Renk.metinSonuk,
                modifier = Modifier.weight(1f),
            )
            Text(
                "%02d:00".format(bitis),
                style = MaterialTheme.typography.labelSmall,
                color = Renk.metinSonuk,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsSarmal(icerik: @Composable BoxScope.(androidx.compose.ui.unit.Dp) -> Unit) {
    androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize()) {
        icerik(maxWidth)
    }
}

/**
 * Bildirim sayısı ↔ kategori dengesi.
 *
 * Kullanıcı 3 kategori seçip günde 7 bildirim isterse havuz küçük kalır ve
 * aynı sözler döner. Seçimi sessizce değiştirmek yerine SONUCU görünür
 * kılıyoruz: kaç gün tekrarsız gidileceğini söylüyoruz.
 */
@Composable
private fun HavuzDengesi(adet: Int, kategoriSayisi: Int, havuzaGit: (() -> Unit)?) {
    val sozSayisi = remember(kategoriSayisi) { Sozler.tumu().size }
    val tekrarsizGun = if (adet <= 0) 0 else sozSayisi / adet
    val yetersiz = tekrarsizGun < 7

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Yaricap.md))
            .background(if (yetersiz) Renk.accentZemin else Renk.yuzey)
            .then(if (yetersiz && havuzaGit != null) Modifier.azimTikla(tikla = havuzaGit) else Modifier)
            .padding(Olcu.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (yetersiz) {
                stringResource(R.string.havuz_yetersiz, kategoriSayisi, adet)
            } else {
                stringResource(R.string.havuz_dengeli, kategoriSayisi, sozSayisi, adet, tekrarsizGun)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (yetersiz) Renk.accent else Renk.metinSonuk,
            modifier = Modifier.weight(1f),
        )
    }
}
