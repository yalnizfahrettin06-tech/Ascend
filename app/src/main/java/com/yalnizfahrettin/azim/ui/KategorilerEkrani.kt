package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.util.Locale

@Composable
fun KilitDialog(kategori: Kategori, dil: String, kapat: () -> Unit, demoAc: () -> Unit, proAc: () -> Unit, hata: String? = null) {
    val sozSayisi = Sozler.kategoriden(kategori.anahtar).size
    AlertDialog(onDismissRequest = kapat, title = { Text(kategori.ad(dil)) }, text = {
        Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(cevir(dil, "$sozSayisi özgün söz · Yalnız bu kategori açılır", "$sozSayisi original quotes · Unlocks this topic only"), color = Renk.accent)
            Text(cevir(dil, "GEÇİCİ DEMO", "TEMPORARY DEMO"), style = MaterialTheme.typography.labelSmall)
            Text(cevir(dil, "Bu sürümde gerçek reklam yok. Google.com tarayıcıda açılır. Uygulamaya döndüğünde bu kategori açılır ve bildirim konularına eklenir.", "There is no real ad in this version. Google.com opens in your browser. Returning unlocks this topic and adds it to your reminder topics."))
            TextButton(onClick = proAc) { Text(cevir(dil, "Tüm kategoriler için Pro’yu keşfet", "Explore Pro for all topics")) }
            hata?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }, confirmButton = { TextButton(onClick = demoAc) { Text(cevir(dil, "Demo bağlantısını aç", "Open demo link")) } },
    dismissButton = { TextButton(onClick = kapat) { Text(cevir(dil, "Vazgeç", "Cancel")) } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategorilerEkrani(secili: Set<String>, acik: Set<String>, dil: String, sec: (String) -> Unit,
    kilidiAc: (Kategori) -> Unit, pro: Boolean, proAc: () -> Unit, acilacakGrup: String? = null,
) {
    var grupKey by rememberSaveable { mutableStateOf(acilacakGrup) }
    var sonAcilisGrubu by rememberSaveable { mutableStateOf(acilacakGrup) }
    var arama by rememberSaveable { mutableStateOf("") }
    var filtre by rememberSaveable { mutableStateOf("all") }
    var detayKey by rememberSaveable { mutableStateOf<String?>(null) }
    var alanlarAcik by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(acilacakGrup) {
        if (sonAcilisGrubu != acilacakGrup) { grupKey = acilacakGrup; sonAcilisGrubu = acilacakGrup }
    }
    val locale = Locale.forLanguageTag(dil)
    val query = arama.trim().lowercase(locale)
    val kategoriler = Kategoriler.tumAltlar.filter { k ->
        (grupKey == null || k.grup == grupKey) && (filtre != "selected" || k.anahtar in secili) &&
        (filtre != "open" || k.anahtar in acik) &&
        (query.isBlank() || k.ad(dil).lowercase(locale).contains(query) || k.adEn.lowercase(locale).contains(query))
    }
    LazyColumn(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("category-grid"),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditoryalBaslik(cevir(dil, "02 / İLHAM KÜTÜPHANESİ", "02 / LIBRARY OF INSPIRATION"),
                        cevir(dil, "Keşfet", "Explore"), modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = proAc, modifier = Modifier.padding(top = 14.dp).heightIn(min = 48.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp), shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Renk.kenarlik)) {
                        Text(if (pro) "PRO DEMO" else "PRO", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Text(cevir(dil, "Bir konuya uğra. Yeni bir bakış bul.", "Visit a topic. Find a new perspective."),
                    color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(arama, { arama = it }, placeholder = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers")) },
                    modifier = Modifier.fillMaxWidth().testTag("category-search"), singleLine = true, shape = RoundedCornerShape(50),
                    leadingIcon = { Icon(AzimIkon.Ara, null, Modifier.size(20.dp)) },
                    trailingIcon = { if (arama.isNotEmpty()) IconButton(onClick = { arama = "" }) { Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search")) } })
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.padding(top = 9.dp).width(26.dp).height(1.dp).background(Renk.accent))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(cevir(dil, "${secili.size} konuya öncelik veriyorsun", "${secili.size} topics have priority"),
                            color = Renk.metin, style = MaterialTheme.typography.bodyMedium)
                        Text(cevir(dil, "Bildirim karışımını bu seçimler ve kişisel planın oluşturur.", "These choices and your personal plan shape your reminders."),
                            color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("all", "selected", "open")) { f ->
                        FilterChip(filtre == f, onClick = { filtre = f }, label = { Text(when (f) {
                            "selected" -> cevir(dil, "Seçtiklerim", "Selected")
                            "open" -> cevir(dil, "Açık", "Unlocked")
                            else -> cevir(dil, "Tümü", "All")
                        }) }, shape = RoundedCornerShape(50))
                    }
                    item { FilterChip(grupKey != null, onClick = { alanlarAcik = true }, label = { Text(Kategoriler.grupBul(grupKey ?: "")?.ad(dil) ?: cevir(dil, "Alanlar", "Areas")) }, shape = RoundedCornerShape(50)) }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(cevir(dil, "${kategoriler.size} konu", "${kategoriler.size} topics"), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
                    HorizontalDivider(Modifier.weight(1f), color = Renk.kenarlik)
                }
            }
        }
        items(kategoriler, key = { it.anahtar }) { kat ->
            val secildi = kat.anahtar in secili
            val acildi = kat.anahtar in acik
            Column {
                Row(Modifier.fillMaxWidth().heightIn(min = 82.dp).clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button) { detayKey = kat.anahtar }.testTag("category-${kat.anahtar}").semantics {
                        stateDescription = if (secildi) cevir(dil, "Bildirimlerinde seçili", "Selected for reminders") else if (acildi) cevir(dil, "Açık, seçilmedi", "Unlocked, not selected") else cevir(dil, "Kilitli", "Locked")
                    }.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text((Kategoriler.tumAltlar.indexOf(kat) + 1).toString().padStart(2, '0'), color = Renk.metinIkincil,
                        fontFamily = LoraSerif, fontSize = 13.sp, modifier = Modifier.clearAndSetSemantics {})
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(kat.ad(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 21.sp, lineHeight = 28.sp)
                        Text(Kategoriler.grupBul(kat.grup)?.ad(dil).orEmpty() + " · " + if (secildi) cevir(dil, "Seçili", "Selected") else if (acildi) cevir(dil, "Açık", "Unlocked") else cevir(dil, "Demo ile aç", "Unlock with demo"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    }
                    Box(Modifier.size(30.dp).then(if (secildi) Modifier.border(1.dp, Renk.metin, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
                        Icon(if (secildi) AzimIkon.Tik else if (acildi) AzimIkon.Ileri else AzimIkon.Kilit, null, Modifier.size(18.dp), tint = if (secildi) Renk.metin else Renk.metinIkincil)
                    }
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
        }
        if (kategoriler.isEmpty()) item {
            Column(Modifier.fillMaxWidth().padding(vertical = 36.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                KlasikGorsel(KlasikMotif.ARCH, Modifier.width(94.dp).height(112.dp), opacity = .19f)
                Text(cevir(dil, "Bu aramada bir konu yok.", "No topics match this search."), color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 31.sp)
                TextButton(onClick = { arama = ""; filtre = "all"; grupKey = null }) { Text(cevir(dil, "Tüm konuları göster", "Show all topics")) }
            }
        }
    }
    if (alanlarAcik) ModalBottomSheet(onDismissRequest = { alanlarAcik = false }, containerColor = Renk.zemin) {
        LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            item { EditoryalBaslik(cevir(dil, "KÜTÜPHANE / ALANLAR", "LIBRARY / AREAS"), cevir(dil, "Bir alan seç", "Choose an area"), modifier = Modifier.padding(bottom = 18.dp)) }
            item {
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .selectable(selected = grupKey == null, role = Role.RadioButton, onClick = { grupKey = null; alanlarAcik = false })
                    .padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(cevir(dil, "Tüm alanlar", "All areas"), Modifier.weight(1f), color = Renk.metin)
                    if (grupKey == null) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.metin)
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
            items(Kategoriler.gruplar, key = { it.anahtar }) { g ->
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .selectable(selected = grupKey == g.anahtar, role = Role.RadioButton, onClick = { grupKey = g.anahtar; alanlarAcik = false })
                    .padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(g.ad(dil), Modifier.weight(1f), color = Renk.metin)
                    if (grupKey == g.anahtar) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.metin)
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
        }
    }
    val kat = Kategoriler.bul(detayKey ?: "")
    if (kat != null) ModalBottomSheet(onDismissRequest = { detayKey = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        val sozler = Sozler.kategoriden(kat.anahtar)
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(Modifier.fillMaxWidth()) {
                KlasikGorsel(if (kat.grup == "filozoflar") KlasikMotif.BUST else KlasikMotif.ARCH,
                    Modifier.align(Alignment.CenterEnd).width(90.dp).height(125.dp), opacity = .12f)
                EditoryalBaslik(Kategoriler.grupBul(kat.grup)?.ad(dil).orEmpty(), kat.ad(dil), modifier = Modifier.fillMaxWidth())
            }
            Text(cevir(dil, "${sozler.size} özgün söz · Bir göz at", "${sozler.size} original quotes · Take a look"), color = Renk.metinIkincil, style = MaterialTheme.typography.labelMedium)
            sozler.take(2).forEach { soz ->
                HorizontalDivider(color = Renk.kenarlik)
                Text(soz.metin(dil), Modifier.padding(vertical = 6.dp), color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 33.sp)
            }
            HorizontalDivider(color = Renk.kenarlik)
            if (kat.grup in setOf("filozoflar", "tasavvuf", "inanc")) Text(cevir(dil, "Bu gelenekten ilham alan özgün Ascend düşünceleri; doğrudan alıntı veya kutsal metin değildir.", "Original Ascend reflections inspired by this tradition, not direct quotations or sacred texts."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
            if (kat.anahtar in acik) {
                val secildi = kat.anahtar in secili
                Button(onClick = { sec(kat.anahtar) }, enabled = !secildi || secili.size > 1, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(50)) {
                    Text(if (secildi) cevir(dil, "Bildirim konularından çıkar", "Remove from reminder topics") else cevir(dil, "Bildirim konularıma ekle", "Add to my reminder topics"))
                }
                Text(if (secildi && secili.size == 1) cevir(dil, "Son konunu kaldırmadan önce başka bir konu seç.", "Choose another topic before removing your last one.")
                    else cevir(dil, "Bu seçim konuya öncelik verir; planındaki tür ve kaçınma tercihleri korunur.", "This choice gives the topic priority; your format and avoidance preferences still apply."),
                    color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            } else {
                Button(onClick = { detayKey = null; kilidiAc(kat) }, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(50)) { Text(cevir(dil, "Bu kategoriyi aç · Demo", "Unlock this topic · Demo")) }
                OutlinedButton(onClick = { detayKey = null; proAc() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(cevir(dil, "Pro ile tümüne eriş", "Get all topics with Pro")) }
            }
            TextButton(onClick = { detayKey = null }, modifier = Modifier.fillMaxWidth()) { Text(cevir(dil, "Tamam", "Done")) }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun SeciliKonular(secili: Set<String>, dil: String, duzenle: (() -> Unit)?, modifier: Modifier = Modifier) {
    val konular = Kategoriler.tumAltlar.filter { it.anahtar in secili }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(cevir(dil, "Bildirim konuların", "Your reminder topics"), Modifier.weight(1f), color = Renk.metin, style = MaterialTheme.typography.titleSmall)
            duzenle?.let { TextButton(onClick = it) { Text(cevir(dil, "Düzenle", "Edit")) } }
        }
        Text(konular.take(3).joinToString(" · ") { it.ad(dil) } + if (konular.size > 3) " +${konular.size - 3}" else "", color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
    }
}
