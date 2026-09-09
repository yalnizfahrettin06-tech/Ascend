package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.Renk
import com.yalnizfahrettin.azim.core.AzimIkon
import com.yalnizfahrettin.azim.notif.BildirimZamanlari
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** One editor shared by onboarding and settings; the preview uses the actual scheduler. */
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
    dil: String = LocalConfiguration.current.locales[0].language,
) {
    // 0 = closed, 1 = start hour, 2 = end hour. Survives activity recreation.
    var saatSecimi by rememberSaveable { mutableIntStateOf(0) }
    val buyukYazi = LocalDensity.current.fontScale > 1.35f
    val saatler = remember(adet, baslangic, bitis) {
        BildirimZamanlari.hesapla(LocalDateTime.of(2000, 1, 1, 0, 0), adet, baslangic, bitis)
            .map { it.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)) }
    }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(color = Renk.yuzey, shape = RoundedCornerShape(24.dp)) {
            if (buyukYazi) Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(cevir(dil, "Günlük bildirim", "Daily reminders"), color = Renk.metin,
                    style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                AdetKontrolu(adet, dil, adetDegisti, Modifier.fillMaxWidth())
            } else Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(cevir(dil, "Günlük bildirim", "Daily reminders"), color = Renk.metin,
                        style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                    Text(cevir(dil, "Günde 1–7 kez", "1–7 times a day"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                }
                AdetKontrolu(adet, dil, adetDegisti)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(cevir(dil, "Hangi saatler arasında?", "Between which hours?"), color = Renk.metin,
                style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
            if (buyukYazi) Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SaatKarti(cevir(dil, "Başlangıç", "From"), saatMetni(baslangic),
                    cevir(dil, "Başlangıç saatini seç", "Choose start hour"), false,
                    Modifier.fillMaxWidth().testTag("reminder-start"), dil) { saatSecimi = 1 }
                SaatKarti(cevir(dil, "Bitiş", "Until"), saatMetni(bitis),
                    cevir(dil, "Bitiş saatini seç", "Choose end hour"), bitis == 24,
                    Modifier.fillMaxWidth().testTag("reminder-end"), dil) { saatSecimi = 2 }
            } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SaatKarti(cevir(dil, "Başlangıç", "From"), saatMetni(baslangic),
                    cevir(dil, "Başlangıç saatini seç", "Choose start hour"), false,
                    Modifier.weight(1f).testTag("reminder-start"), dil) { saatSecimi = 1 }
                SaatKarti(cevir(dil, "Bitiş", "Until"), saatMetni(bitis),
                    cevir(dil, "Bitiş saatini seç", "Choose end hour"), bitis == 24,
                    Modifier.weight(1f).testTag("reminder-end"), dil) { saatSecimi = 2 }
            }
            Text(cevir(dil, "Bu aralığın dışında bildirim göndermeyiz.", "No reminders outside this window."),
                color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }

        Surface(color = Renk.yuzey, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(cevir(dil, "Günün planı", "Your daily plan"), color = Renk.metin,
                    style = MaterialTheme.typography.labelLarge)
                FlowRow(Modifier.fillMaxWidth().testTag("reminder-preview-times"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    saatler.forEach { saat ->
                        Text(saat, color = Renk.accent, style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.background(Renk.zemin.copy(alpha = .48f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp))
                    }
                }
                Text(cevir(dil, "Yaklaşık saatlerdir; cihazın güç tasarrufu teslimatı geciktirebilir.",
                    "Times are approximate; device power saving can delay delivery."),
                    color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (seciliKategoriSayisi > 0 && havuzaGit != null) {
            TextButton(onClick = havuzaGit, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(cevir(dil, "$seciliKategoriSayisi bildirim konusunu düzenle", "Edit $seciliKategoriSayisi reminder topics"))
            }
        }
    }

    if (saatSecimi != 0) {
        val baslangicSeciliyor = saatSecimi == 1
        SaatSecimDialogu(
            baslik = if (baslangicSeciliyor) cevir(dil, "Başlangıç saatini seç", "Choose start hour") else cevir(dil, "Bitiş saatini seç", "Choose end hour"),
            saatler = if (baslangicSeciliyor) (0 until bitis).toList() else (baslangic + 1..24).toList(),
            secili = if (baslangicSeciliyor) baslangic else bitis, dil = dil,
            kapat = { saatSecimi = 0 },
        ) { saat ->
            if (baslangicSeciliyor) araligiDegisti(saat, bitis) else araligiDegisti(baslangic, saat)
            saatSecimi = 0
        }
    }
}

private fun saatMetni(saat: Int) = "%02d:00".format(Locale.ROOT, saat % 24)

@Composable
private fun AdetKontrolu(adet: Int, dil: String, degisti: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        AdetDugmesi("−", cevir(dil, "Bildirim sayısını azalt", "Fewer reminders"), adet > 1) { degisti(adet - 1) }
        Box(Modifier.width(44.dp).testTag("reminder-count"), contentAlignment = Alignment.Center) {
            AnimatedContent(adet, transitionSpec = { fadeIn(tween(140)) togetherWith fadeOut(tween(100)) }, label = "reminder-count") { deger ->
                Text("$deger", color = Renk.metin, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Medium)
            }
        }
        AdetDugmesi("+", cevir(dil, "Bildirim sayısını artır", "More reminders"), adet < 7) { degisti(adet + 1) }
    }
}

@Composable
private fun AdetDugmesi(metin: String, aciklama: String, etkin: Boolean, tikla: () -> Unit) {
    val renk = if (etkin) Renk.metin else Renk.metinSonuk
    OutlinedIconButton(onClick = tikla, enabled = etkin, modifier = Modifier.size(52.dp).semantics { contentDescription = aciklama }, shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (etkin) Renk.kenarlikGuclu else Renk.kenarlik)) {
        Icon(if (metin == "+") AzimIkon.Arti else AzimIkon.Eksi, null, Modifier.size(24.dp), tint = renk)
    }
}

@Composable
private fun SaatKarti(etiket: String, saat: String, aciklama: String, geceYarisi: Boolean,
    modifier: Modifier, dil: String, tikla: () -> Unit) {
    Surface(onClick = tikla, modifier = modifier.semantics { contentDescription = aciklama },
        color = Renk.yuzey, shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Renk.kenarlik)) {
        Column(Modifier.padding(16.dp).heightIn(min = 70.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(etiket, color = Renk.metinIkincil, style = MaterialTheme.typography.labelLarge)
            Text(saat, color = Renk.metin, fontSize = 25.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium)
            Text(if (geceYarisi) cevir(dil, "Gece yarısı", "Midnight") else cevir(dil, "Değiştir", "Change"),
                color = Renk.accent, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SaatSecimDialogu(baslik: String, saatler: List<Int>, secili: Int, dil: String, kapat: () -> Unit, sec: (Int) -> Unit) {
    val sutun = if (LocalDensity.current.fontScale > 1.35f) 2 else 3
    AlertDialog(onDismissRequest = kapat, shape = RoundedCornerShape(26.dp), containerColor = Renk.yuzey,
        title = { Text(baslik, color = Renk.metin, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(Modifier.heightIn(max = 340.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(cevir(dil, "Saat başı seçilir. Bitiş, başlangıçtan sonra olmalıdır.",
                    "Choose a whole hour. The end must follow the start."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                saatler.chunked(sutun).forEach { satir ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        satir.forEach { saat ->
                            val aktif = saat == secili
                            Column(Modifier.weight(1f).heightIn(min = 54.dp).clip(RoundedCornerShape(13.dp))
                                .background(if (aktif) Renk.accentZemin else Renk.zemin)
                                .border(1.dp, if (aktif) Renk.accent else Renk.kenarlik, RoundedCornerShape(13.dp))
                                .selectable(aktif, role = Role.RadioButton, onClick = { sec(saat) })
                                .testTag("hour-choice-$saat").padding(horizontal = 3.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(saatMetni(saat), color = if (aktif) Renk.accent else Renk.metin, style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                                if (saat == 24) Text(cevir(dil, "Gece yarısı", "Midnight"), color = Renk.metinIkincil,
                                    style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                        }
                        repeat(sutun - satir.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = kapat, modifier = Modifier.heightIn(min = 48.dp)) { Text(cevir(dil, "Vazgeç", "Cancel")) } },
    )
}
