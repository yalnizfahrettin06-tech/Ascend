package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun IstatistikEkrani(seri: Int, rekor: Int, gorulen: Int, favoriSayisi: Int, acikKategori: Int, haftalik: List<Boolean> = List(7) { false }) {
    val dil = LocalConfiguration.current.locales[0].language
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(cevir(dil, "Kendi yolunda.", "Your own path."), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))) {
            AtmosferResmi(Atmosfer.ORMAN, Modifier.matchParentSize(), .3f)
            Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(AzimIkon.Alev, null, Modifier.size(28.dp), tint = Color(0xFFE5C49A))
                Text("$seri", color = Color.White, fontSize = 64.sp, fontFamily = LoraSerif)
                Text(cevir(dil, "gündür buradasın", "days in a row"), color = Color.White)
                Spacer(Modifier.height(18.dp))
                Text(cevir(dil, "Her dönüş, kendine ayırdığın bir an.", "Each visit is a moment for yourself."), color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
        }
        KucukBaslik(cevir(dil, "Son 7 gün", "The last 7 days"))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(7) { i ->
                val aktif = haftalik.getOrElse(i) { false }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(36.dp).background(if (aktif) Renk.accentZemin else Renk.yuzey, CircleShape), contentAlignment = Alignment.Center) {
                        if (aktif) Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.accent) else Text("·", color = Renk.metinIkincil)
                    }
                    Text(LocalDate.now().minusDays((6 - i).toLong()).format(DateTimeFormatter.ofPattern("EE", Locale.forLanguageTag(dil))), style = MaterialTheme.typography.labelSmall, color = Renk.metinIkincil, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        listOf(listOf(gorulen to cevir(dil, "Okunan söz", "Quotes read"), favoriSayisi to cevir(dil, "Kaydedilen", "Saved")), listOf(rekor to cevir(dil, "En uzun seri", "Longest streak"), acikKategori to cevir(dil, "Açık başlık", "Open topics"))).forEach { satir ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                satir.forEach { (sayi, baslik) -> Surface(Modifier.weight(1f), color = Renk.yuzey, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(20.dp)) { Text("$sayi", color = Renk.metin, fontSize = 28.sp); Text(baslik, color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall) }
                } }
            }
        }
    }
}
