package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun IstatistikEkrani(
    seri: Int,
    rekor: Int,
    gorulen: Int,
    favoriSayisi: Int,
    acikKategori: Int,
    haftalik: List<Boolean> = List(7) { false },
) {
    val dil = LocalConfiguration.current.locales[0].language
    val sonHafta = List(7) { haftalik.getOrElse(it) { false } }
    val aktifGun = sonHafta.count { it }
    val sayilar = NumberFormat.getIntegerInstance(Locale.forLanguageTag(dil))
    Column(
        Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(cevir(dil, "Kendi yolunda.", "Your own path."), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
            Text(cevir(dil, "Küçük anlar, sana ait bir yolculuk.", "Small moments. A journey of your own."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
        }
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).testTag("journey-summit")) {
            AtmosferResmi(Atmosfer.ZIRVE, Modifier.matchParentSize(), .22f)
            Column(Modifier.fillMaxWidth().heightIn(min = 284.dp).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cevir(dil, "SON 7 GÜNDE", "OVER THE LAST 7 DAYS"), color = Color.White.copy(alpha = .88f), fontSize = 11.sp, letterSpacing = 1.8.sp, modifier = Modifier.weight(1f))
                    Icon(AzimIkon.Dag, null, Modifier.size(26.dp), tint = Color(0xFFE7CAA1))
                }
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(sayilar.format(aktifGun), color = Color.White, fontSize = 68.sp, lineHeight = 76.sp, fontFamily = LoraSerif)
                    Text(cevir(dil, "gün buradaydın", if (aktifGun == 1) "day here" else "days here"), color = Color.White, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 12.dp).weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    cevir(dil, "Her dönüş, kendine ayırdığın bir an.", "Every return is a moment for yourself."),
                    color = Color.White.copy(alpha = .94f), fontFamily = LoraSerif, fontSize = 18.sp, lineHeight = 26.sp,
                    modifier = Modifier.widthIn(max = 260.dp),
                )
            }
        }
        Surface(color = Renk.yuzey, shape = RoundedCornerShape(24.dp), modifier = Modifier.testTag("journey-week")) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(cevir(dil, "Haftanın izi", "Your weekly trail"), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
                    Text(cevir(dil, "Ascend’e geldiğin günler", "The days you visited Ascend"), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                }
                HaftaninGunleri(dil, sonHafta)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(AzimIkon.Patika, null, Modifier.size(20.dp), tint = Renk.accent)
                    Text(
                        if (seri > 0) cevir(dil, if (seri == 1) "Bugün buradasın." else "Üst üste $seri gündür buradasın.", if (seri == 1) "You are here today." else "$seri days in a row.")
                        else cevir(dil, "Yeni bir söz, yeni bir başlangıç.", "A new quote. A fresh beginning."),
                        color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(cevir(dil, "Yol boyunca birikenler", "Collected along the way"), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                YolculukSayaci(sayilar.format(gorulen), cevir(dil, "Okunan söz", "Quotes read"), AzimIkon.Kitap, Modifier.weight(1f))
                YolculukSayaci(sayilar.format(favoriSayisi), cevir(dil, "Biriktirdiğin söz", "Saved quotes"), AzimIkon.Ayrac, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                YolculukSayaci(sayilar.format(rekor), cevir(dil, "En uzun seri · gün", "Longest streak · days"), AzimIkon.Patika, Modifier.weight(1f))
                YolculukSayaci(sayilar.format(acikKategori), cevir(dil, "Açık kategori", "Unlocked categories"), AzimIkon.Kesfet, Modifier.weight(1f))
            }
        }
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            YolIzi(Modifier.width(128.dp).height(32.dp))
            Text(cevir(dil, "Kendi hızında devam et.", "Keep going at your own pace."), color = Renk.metinIkincil, fontFamily = LoraSerif, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HaftaninGunleri(dil: String, haftalik: List<Boolean>) {
    val bugun = LocalDate.now()
    val locale = Locale.forLanguageTag(dil)
    val tamTarih = DateTimeFormatter.ofPattern("d MMMM, EEEE", locale)
    val kisaGun = DateTimeFormatter.ofPattern("EE", locale)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(7) { index ->
            val tarih = bugun.minusDays((6 - index).toLong())
            val aktif = haftalik[index]
            val aciklama = tarih.format(tamTarih) + ", " + cevir(dil, if (aktif) "ziyaret ettin" else "ziyaret yok", if (aktif) "visited" else "no visit")
            Column(
                Modifier.weight(1f).clearAndSetSemantics { contentDescription = aciklama },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(tarih.format(kisaGun), color = Renk.metinIkincil, fontSize = 10.sp, lineHeight = 13.sp, textAlign = TextAlign.Center)
                Box(
                    Modifier.size(30.dp).background(if (aktif) Renk.accent else Renk.yuzeyYuksek, CircleShape)
                        .then(if (index == 6 && !aktif) Modifier.border(1.dp, Renk.accent, CircleShape) else Modifier),
                    contentAlignment = Alignment.Center,
                ) {
                    if (aktif) Icon(AzimIkon.Tik, null, Modifier.size(16.dp), tint = if (Renk.karanlikMi) Renk.zemin else Color.White)
                    else Text(tarih.dayOfMonth.toString(), color = Renk.metinIkincil, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Box(Modifier.size(3.dp).background(if (index == 6) Renk.accent else Color.Transparent, CircleShape))
            }
        }
    }
}

@Composable
private fun YolculukSayaci(sayi: String, baslik: String, ikon: ImageVector, modifier: Modifier = Modifier) {
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(22.dp), modifier = modifier) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(34.dp).background(Renk.accent.copy(alpha = .10f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(ikon, null, Modifier.size(18.dp), tint = Renk.accent)
            }
            Text(sayi, color = Renk.metin, fontSize = 29.sp, lineHeight = 36.sp, fontWeight = FontWeight.Medium)
            Text(baslik, color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun YolIzi(modifier: Modifier = Modifier) {
    val renk = Renk.accent.copy(alpha = .5f)
    Canvas(modifier) {
        val yol = Path().apply {
            moveTo(0f, size.height * .8f)
            cubicTo(size.width * .25f, size.height * .8f, size.width * .25f, size.height * .15f, size.width * .5f, size.height * .35f)
            cubicTo(size.width * .75f, size.height * .6f, size.width * .8f, size.height * .2f, size.width, size.height * .2f)
        }
        drawPath(yol, renk, style = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(renk, 3.dp.toPx(), Offset(size.width * .5f, size.height * .35f))
    }
}
