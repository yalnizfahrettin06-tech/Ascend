package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
    seri: Int, rekor: Int, gorulen: Int, favoriSayisi: Int, acikKategori: Int,
    haftalik: List<Boolean> = List(7) { false },
    onFavoriler: () -> Unit = {}, onPlan: () -> Unit = {}, onSettings: () -> Unit = {},
    name: String = "", planOzeti: String = "",
) {
    val dil = LocalConfiguration.current.locales[0].language
    val sonHafta = List(7) { haftalik.getOrElse(it) { false } }
    val aktifGun = sonHafta.count { it }
    val sayilar = NumberFormat.getIntegerInstance(Locale.forLanguageTag(dil))
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()
        .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (name.isBlank()) cevir(dil, "Senin", "You") else name, color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
                Text(cevir(dil, "Kendi yolun. Kendi hızın.", "Your path. Your pace."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onSettings, modifier = Modifier.testTag("profile-settings")) { Icon(AzimIkon.Ayarlar, cevir(dil, "Ayarlar", "Settings"), Modifier.size(22.dp), tint = Renk.metin) }
        }
        Surface(onClick = onPlan, color = Renk.yuzey, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().testTag("profile-plan")) {
            Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Yukselis, null, Modifier.size(30.dp), tint = Renk.accent)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(cevir(dil, "Sana göre plan", "Your personal plan"), color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                    Text(planOzeti.ifBlank { cevir(dil, "İhtiyaçların değiştikçe yeniden düzenle.", "Adjust it as your needs change.") }, color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                }
                Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
            }
        }
        Surface(onClick = onFavoriler, color = Renk.zemin, modifier = Modifier.fillMaxWidth().testTag("profile-saved")) {
            Row(Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Ayrac, null, Modifier.size(22.dp), tint = Renk.metin)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(cevir(dil, "Kaydedilenler", "Saved quotes"), color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                    Text(cevir(dil, "${sayilar.format(favoriSayisi)} söz sende kaldı", "${sayilar.format(favoriSayisi)} quotes to revisit"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                }
                Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
            }
        }
        HorizontalDivider(color = Renk.kenarlik)
        Column(Modifier.fillMaxWidth().testTag("journey-week"), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cevir(dil, "Haftanın izi", "Your weekly trail"), Modifier.weight(1f), color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                Text(cevir(dil, "$aktifGun / 7 gün", "$aktifGun / 7 days"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            }
            HaftaninGunleri(dil, sonHafta)
            Text(if (seri > 1) cevir(dil, "Üst üste $seri gündür kendine bir an ayırdın.", "You made a moment for yourself $seri days in a row.") else cevir(dil, "Her gelişin küçük bir adım. Ara vermek de yolun parçası.", "Every visit is a small step. Pauses belong here too."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider(color = Renk.kenarlik)
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(cevir(dil, "Biriken küçük adımlar", "Small steps collected"), color = Renk.metin, style = MaterialTheme.typography.titleMedium)
            val olcumler = listOf(gorulen to cevir(dil, "Okunan söz", "Quotes read"), rekor to cevir(dil, "En uzun seri · gün", "Longest streak · days"), acikKategori to cevir(dil, "Açık konu", "Unlocked topics"))
            if (buyukYazi) Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                olcumler.forEach { (sayi, baslik) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(baslik, Modifier.weight(1f), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                        Text(sayilar.format(sayi), color = Renk.metin, fontSize = 24.sp)
                    }
                }
            } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                olcumler.forEach { (sayi, baslik) -> YolculukSayaci(sayilar.format(sayi), baslik, Modifier.weight(1f)) }
            }
        }
        Spacer(Modifier.height(4.dp))
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
            Column(Modifier.weight(1f).clearAndSetSemantics { contentDescription = aciklama }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tarih.format(kisaGun), color = Renk.metinIkincil, fontSize = 10.sp, lineHeight = 13.sp, textAlign = TextAlign.Center)
                Box(Modifier.size(30.dp).background(if (aktif) Renk.accent else Renk.yuzey, CircleShape)
                    .then(if (index == 6 && !aktif) Modifier.border(1.dp, Renk.accent, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
                    if (aktif) Icon(AzimIkon.Tik, null, Modifier.size(16.dp), tint = if (Renk.karanlikMi) Renk.zemin else Color.White)
                    else Text(tarih.dayOfMonth.toString(), color = Renk.metinIkincil, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun YolculukSayaci(sayi: String, baslik: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(sayi, color = Renk.metin, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Medium)
        Text(baslik, color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
    }
}
