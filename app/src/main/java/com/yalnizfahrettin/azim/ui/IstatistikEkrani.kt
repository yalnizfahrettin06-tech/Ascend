package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.AzimIkon
import com.yalnizfahrettin.azim.core.Olcu
import com.yalnizfahrettin.azim.core.Renk
import com.yalnizfahrettin.azim.core.Yaricap

/*
 * İSTATİSTİK
 *
 * Eskisinde etiket yazımı tutarsızdı: "Bugün", "favoriler", "Gün", "En çok"
 * yan yana duruyordu ve sayılar accent renkteydi — hiçbiri diğerinden daha
 * önemli olmadığı halde hepsi bağırıyordu.
 *
 * Burada sayılar nötr ve büyük, etiketler küçük ve sönük; accent yalnızca
 * seri alevinde. Ekran boşken de dolu gibi görünsün diye kutular kalıyor,
 * "Henüz veri yok" gibi ayrı bir boş durum bloğu gerekmiyor.
 */
@Composable
fun IstatistikEkrani(
    seri: Int,
    rekor: Int,
    gorulen: Int,
    favoriSayisi: Int,
    acikKategori: Int,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Renk.zemin).statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Olcu.xl),
    ) {
        Spacer(Modifier.height(Olcu.xxl))
        Text(
            stringResource(R.string.istatistik_baslik),
            style = MaterialTheme.typography.headlineSmall,
            color = Renk.metin,
        )
        Spacer(Modifier.height(Olcu.lg))
        Text(stringResource(R.string.asc_yolculuk_alt), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(Olcu.xl))

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Yaricap.lg))
                .background(Renk.yuzey)
                .border(1.dp, Renk.kenarlik, RoundedCornerShape(Yaricap.lg))
                .padding(Olcu.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                AzimIkon.Alev, null,
                tint = Renk.accent, modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(Olcu.lg))
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$seri",
                        style = MaterialTheme.typography.displaySmall,
                        color = Renk.metin,
                    )
                    Spacer(Modifier.width(Olcu.xs))
                    Text(
                        stringResource(R.string.gun),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Renk.metinIkincil,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                Text(
                    "${stringResource(R.string.en_uzun_seri)}: $rekor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Renk.metinSonuk,
                )
            }
        }

        Spacer(Modifier.height(Olcu.lg))
        Row(Modifier.fillMaxWidth()) {
            SayiKutusu(Modifier.weight(1f), gorulen, stringResource(R.string.toplam_gorulen))
            Spacer(Modifier.width(Olcu.md))
            SayiKutusu(Modifier.weight(1f), favoriSayisi, stringResource(R.string.favori_sayisi))
        }
        Spacer(Modifier.height(Olcu.md))
        Row(Modifier.fillMaxWidth()) {
            SayiKutusu(Modifier.weight(1f), acikKategori, stringResource(R.string.acik_kategori))
            Spacer(Modifier.width(Olcu.md))
            Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(Olcu.x5))
    }
}

@Composable
private fun SayiKutusu(modifier: Modifier, deger: Int, etiket: String) {
    Column(
        modifier
            .clip(RoundedCornerShape(Yaricap.md))
            .background(Renk.yuzey)
            .border(1.dp, Renk.kenarlik, RoundedCornerShape(Yaricap.md))
            .padding(Olcu.lg),
    ) {
        Text("$deger", style = MaterialTheme.typography.headlineSmall, color = Renk.metin)
        Spacer(Modifier.height(Olcu.xs))
        Text(etiket, style = MaterialTheme.typography.labelSmall, color = Renk.metinSonuk)
    }
}
