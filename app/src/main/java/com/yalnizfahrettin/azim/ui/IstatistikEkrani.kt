package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontStyle
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
    name: String = "", planOzeti: String = "", onHistory: () -> Unit = {}, onSeries: () -> Unit = {}, embedded: Boolean = false,
) {
    val dil = LocalConfiguration.current.locales[0].language
    val sonHafta = List(7) { haftalik.getOrElse(it) { false } }
    val aktifGun = sonHafta.count { it }
    val sayilar = NumberFormat.getIntegerInstance(Locale.forLanguageTag(dil))
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    Column(Modifier.fillMaxSize().background(Renk.zemin).then(if(embedded) Modifier else Modifier.statusBarsPadding())
        .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        if(!embedded) MarkaBasligi(yatayBosluk = 0.dp) {
            Text(if (name.isBlank()) cevir(dil, "Senin", "You") else name,
                fontFamily = LoraSerif, fontSize = 28.sp, lineHeight = 36.sp, modifier = Modifier.weight(1f))
            IconButton(onClick = onSettings, modifier = Modifier.size(48.dp).testTag("profile-settings")) {
                Icon(AzimIkon.Ayarlar, cevir(dil, "Ayarlar", "Settings"), Modifier.size(22.dp))
            }
        }
        Surface(onClick = onFavoriler, color = Renk.zemin, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().testTag("profile-saved")) {
            Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Ayrac, null, Modifier.size(22.dp), tint = Renk.metin)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(cevir(dil, "Kaydedilenler", "Saved quotes"), color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                    Text(cevir(dil, "${sayilar.format(favoriSayisi)} söz sende kaldı", "${sayilar.format(favoriSayisi)} quotes to revisit"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                }
                Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
            }
        }
        HorizontalDivider(color = Renk.kenarlik)
        Column(Modifier.fillMaxWidth().background(Renk.yuzey, RoundedCornerShape(24.dp)).padding(18.dp).testTag("journey-week"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(cevir(dil, "Bu hafta", "This week"), color = Renk.metin, fontFamily = ArayuzFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp)
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(sayilar.format(aktifGun), color = Renk.metin, fontFamily = ArayuzFont, fontWeight = FontWeight.Medium, fontSize = 32.sp, lineHeight = 38.sp)
                Text(cevir(dil, "gün · son 7 günde", "days here · past 7 days"), color = Renk.metinIkincil,
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).padding(bottom = 5.dp))
            }
            HaftaninGunleri(dil, sonHafta)
            Text(if (seri > 1) cevir(dil, "Üst üste $seri gündür kendine bir an ayırdın.", "You made a moment for yourself $seri days in a row.")
                else cevir(dil, "Her gelişin küçük bir adım. Ara vermek de yolun parçası.", "Every visit is a small step. Pauses belong here too."),
                color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }

    }
}

@Composable
private fun HaftaninGunleri(dil: String, haftalik: List<Boolean>) {
    val bugun = LocalDate.now()
    val locale = Locale.forLanguageTag(dil)
    val tamTarih = DateTimeFormatter.ofPattern("d MMMM, EEEE", locale)
    val kisaGun = DateTimeFormatter.ofPattern("EE", locale)
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(7) { index ->
            val tarih = bugun.minusDays((6 - index).toLong())
            val aktif = haftalik[index]
            val aciklama = tarih.format(tamTarih) + ", " + cevir(dil, if (aktif) "ziyaret ettin" else "ziyaret yok", if (aktif) "visited" else "no visit")
            Column(Modifier.weight(1f).clearAndSetSemantics { contentDescription = aciklama },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(9.dp)) {
                // Narrow calendars use locale-aware initials; TalkBack retains the complete date.
                Text(if (buyukYazi) tarih.format(kisaGun).take(1) else tarih.format(kisaGun), color = Renk.metinIkincil,
                    fontSize = 10.sp, lineHeight = 13.sp, letterSpacing = 0.sp, textAlign = TextAlign.Center)
                Box(Modifier.size(30.dp).background(if (aktif) Renk.metin else Renk.zemin, CircleShape)
                    .border(1.dp, if (aktif) Renk.metin else Renk.kenarlik, CircleShape), contentAlignment = Alignment.Center) {
                    if (aktif) Icon(AzimIkon.Tik, null, Modifier.size(16.dp), tint = Renk.zemin)
                    else Text(tarih.dayOfMonth.toString(), color = Renk.metinIkincil, fontSize = 11.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Medium)
                }
                Box(Modifier.size(3.dp).background(if (index == 6) Renk.accent else androidx.compose.ui.graphics.Color.Transparent, CircleShape))
            }
        }
    }
}

