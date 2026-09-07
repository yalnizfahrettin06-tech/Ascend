package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Kategori
import com.yalnizfahrettin.azim.data.KategoriGrubu
import com.yalnizfahrettin.azim.data.Kategoriler

/*
 * KATEGORİLER — grup + alt kategori
 *
 * Önceden 35 kategori düz bir liste hâlindeydi ve her biri ayrı ayrı
 * reklamla açılıyordu. 60 alt kategoriye çıkınca bu hem okunmaz hem de
 * açma açısından işkence olurdu.
 *
 * Yeni yapı: 11 grup listelenir, gruba dokununca altları açılır.
 * Kilit GRUP seviyesinde — bir reklam, o grubun tamamı ömür boyu.
 */
@Composable
fun KategorilerEkrani(
    secili: Set<String>,
    acikGruplar: Set<String>,
    dil: String,
    sec: (String) -> Unit,
    kilidiAc: (KategoriGrubu) -> Unit,
    acilacakGrup: String? = null,
) {
    var genisleyen by rememberSaveable { mutableStateOf<String?>(acilacakGrup ?: "olumlamalar") }
    LaunchedEffect(acilacakGrup) { acilacakGrup?.let { genisleyen = it } }

    val acikSayi = acikGruplar.size
    val toplamSayi = Kategoriler.gruplar.size

    LazyColumn(
        Modifier.fillMaxSize().background(zeminFircasi()).statusBarsPadding(),
        contentPadding = PaddingValues(bottom = Olcu.x5),
    ) {
        item {
            Column(Modifier.padding(horizontal = Olcu.xl).padding(top = Olcu.xxl)) {
                Text(
                    stringResource(R.string.kategoriler_baslik),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Renk.metin,
                )
                Spacer(Modifier.height(Olcu.xs))
                Text(
                    stringResource(R.string.asc_konular_alt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Renk.metinIkincil,
                )
                Spacer(Modifier.height(Olcu.xl))
                Text(
                    stringResource(R.string.kategori_acik, acikSayi, toplamSayi),
                    style = MaterialTheme.typography.labelSmall,
                    color = Renk.metinSonuk,
                )
                Spacer(Modifier.height(Olcu.sm))
                IlerlemeCubugu(acikSayi.toFloat() / toplamSayi)
                Spacer(Modifier.height(Olcu.xl))
            }
        }
        items(Kategoriler.gruplar, key = { it.anahtar }) { grup ->
            GrupSatiri(
                grup = grup,
                acikMi = grup.anahtar in acikGruplar,
                genisMi = genisleyen == grup.anahtar,
                secili = secili,
                dil = dil,
                basildi = {
                    if (grup.anahtar in acikGruplar) {
                        genisleyen = if (genisleyen == grup.anahtar) null else grup.anahtar
                    } else {
                        kilidiAc(grup)
                    }
                },
                altSecildi = sec,
            )
        }
    }
}

@Composable
private fun GrupSatiri(
    grup: KategoriGrubu,
    acikMi: Boolean,
    genisMi: Boolean,
    secili: Set<String>,
    dil: String,
    basildi: () -> Unit,
    altSecildi: (String) -> Unit,
) {
    val seciliAlt = grup.altlar.count { it.anahtar in secili }
    val ok by animateFloatAsState(if (genisMi) 90f else 0f, tween(220), label = "ok")

    Column(Modifier.padding(horizontal = Olcu.xl, vertical = Olcu.xs)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Yaricap.md))
                .background(if (genisMi) Renk.yuzeyYuksek else Renk.yuzey)
                .azimTikla(etiket = grup.ad(dil), tikla = basildi)
                .padding(horizontal = Olcu.lg, vertical = Olcu.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    grup.ad(dil),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (acikMi) Renk.metin else Renk.metinSonuk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (acikMi) {
                        stringResource(R.string.grup_secili, seciliAlt, grup.altlar.size)
                    } else {
                        stringResource(R.string.grup_kilitli, grup.altlar.size)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Renk.metinSonuk,
                )
            }
            if (acikMi) {
                Icon(
                    AzimIkon.Geri, null,
                    tint = Renk.metinSonuk,
                    modifier = Modifier.size(18.dp).rotate(ok + 180f),
                )
            } else {
                Icon(
                    AzimIkon.Kilit,
                    contentDescription = stringResource(R.string.kilitli),
                    tint = Renk.metinSonuk,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = genisMi && acikMi,
            enter = expandVertically(tween(240)) + fadeIn(tween(240)),
            exit = shrinkVertically(tween(180)) + fadeOut(tween(120)),
        ) {
            Column(Modifier.padding(top = Olcu.xs)) {
                grup.altlar.forEach { alt ->
                    AltSatir(alt, alt.anahtar in secili, dil) { altSecildi(alt.anahtar) }
                }
            }
        }
    }
}

@Composable
private fun AltSatir(kat: Kategori, seciliMi: Boolean, dil: String, tikla: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = Olcu.md, top = Olcu.xs, bottom = Olcu.xs)
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(Yaricap.sm))
            .background(if (seciliMi) Renk.accentZemin else Color.Transparent)
            .toggleable(value = seciliMi, role = Role.Checkbox, onValueChange = { tikla() })
            .padding(horizontal = Olcu.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            kat.ad(dil),
            style = MaterialTheme.typography.bodyMedium,
            color = if (seciliMi) Renk.metin else Renk.metinIkincil,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (seciliMi) {
            Box(
                Modifier.size(20.dp).clip(CircleShape).background(Renk.accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AzimIkon.Tik, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(12.dp))
            }
        } else {
            Box(
                Modifier.size(20.dp).clip(CircleShape)
                    .border(1.5.dp, Renk.kenarlikGuclu, CircleShape),
            )
        }
    }
}

/** Grup kilidini açma onayı. */
@Composable
fun KilitDialog(grup: KategoriGrubu, dil: String, kapat: () -> Unit, hazir: Boolean, izle: () -> Unit) {
    AlertDialog(
        onDismissRequest = kapat,
        containerColor = Renk.yuzeyYuksek,
        title = { Text(grup.ad(dil), color = Renk.metin) },
        text = {
            Text(
                if (hazir) stringResource(R.string.grup_ac_aciklama, grup.altlar.size) else stringResource(R.string.asc_reklam_yok),
                color = Renk.metinIkincil,
            )
        },
        confirmButton = {
            TextButton(onClick = if (hazir) izle else kapat) {
                Text(stringResource(if (hazir) R.string.izle else R.string.asc_geri), color = Renk.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = kapat) {
                Text(stringResource(R.string.vazgec), color = Renk.metinSonuk)
            }
        },
    )
}
