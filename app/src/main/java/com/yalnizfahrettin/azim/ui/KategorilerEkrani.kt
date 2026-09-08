package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.util.Locale

@Composable
fun KilitDialog(kategori: Kategori, dil: String, kapat: () -> Unit, demoAc: () -> Unit, proAc: () -> Unit, hata: String? = null) {
    AlertDialog(onDismissRequest = kapat, title = { Text(kategori.ad(dil)) }, text = {
        Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(cevir(dil, "10 özgün söz · Yalnız bu kategori açılır", "10 original quotes · Unlocks this topic only"), color = Renk.accent)
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
    LaunchedEffect(acilacakGrup) {
        if (sonAcilisGrubu != acilacakGrup) {
            grupKey = acilacakGrup
            sonAcilisGrubu = acilacakGrup
        }
    }
    val locale = Locale.forLanguageTag(dil)
    val query = arama.trim().lowercase(locale)
    val kategoriler = Kategoriler.tumAltlar.filter { k ->
        (grupKey == null || k.grup == grupKey) && (filtre != "selected" || k.anahtar in secili) &&
        (filtre != "open" || k.anahtar in acik) &&
        (query.isBlank() || k.ad(dil).lowercase(locale).contains(query) || k.adEn.lowercase(locale).contains(query))
    }
    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("category-grid"),
        contentPadding = PaddingValues(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cevir(dil, "İlhamını keşfet.", "Find your inspiration."), Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge, color = Renk.metin)
                    TextButton(onClick = proAc) { Text(if (pro) "PRO DEMO" else "PRO") }
                }
                Text(cevir(dil, "Küçük konular. Sana yakın sözler.", "Focused topics. Words that resonate."), color = Renk.metinIkincil)
                OutlinedTextField(arama, { arama = it }, placeholder = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers")) },
                    modifier = Modifier.fillMaxWidth().testTag("category-search"), singleLine = true, shape = RoundedCornerShape(20.dp),
                    leadingIcon = { Icon(AzimIkon.Kesfet, null, Modifier.size(20.dp)) },
                    trailingIcon = { if (arama.isNotEmpty()) IconButton(onClick = { arama = "" }) { Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search")) } })
                SeciliKonular(secili, dil, null)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("all", "selected", "open")) { f ->
                        FilterChip(filtre == f, onClick = { filtre = f }, label = { Text(when (f) {
                            "selected" -> cevir(dil, "Seçtiklerim", "Selected")
                            "open" -> cevir(dil, "Açık konular", "Unlocked")
                            else -> cevir(dil, "Tüm konular", "All topics")
                        }) }, shape = RoundedCornerShape(50))
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(grupKey == null, onClick = { grupKey = null }, label = { Text(cevir(dil, "Tümü", "All")) }, shape = RoundedCornerShape(50)) }
                    items(Kategoriler.gruplar, key = { it.anahtar }) { g ->
                        FilterChip(grupKey == g.anahtar, onClick = { grupKey = g.anahtar }, label = { Text(g.ad(dil)) }, shape = RoundedCornerShape(50))
                    }
                }
                Text(cevir(dil, "${kategoriler.size} kategori · Her birinde 10 özgün söz", "${kategoriler.size} topics · 10 original quotes each"), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
            }
        }
        items(kategoriler, key = { it.anahtar }) { kat ->
            val secildi = kat.anahtar in secili
            val acildi = kat.anahtar in acik
            val kenar by animateColorAsState(if (secildi) Renk.accent else Color.Transparent, label = "topic-selection")
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).border(if (secildi) 2.dp else 0.dp, kenar, RoundedCornerShape(24.dp))
                .clickable(role = Role.Button) { detayKey = kat.anahtar }.testTag("category-${kat.anahtar}").semantics {
                    stateDescription = if (secildi) cevir(dil, "Bildirimlerinde seçili", "Selected for reminders") else if (acildi) cevir(dil, "Açık, seçilmedi", "Unlocked, not selected") else cevir(dil, "Kilitli", "Locked")
                }) {
                KategoriGorseli(kat.anahtar, Modifier.matchParentSize())
                Column(Modifier.fillMaxWidth().heightIn(min = 190.dp).padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Surface(color = Color.Black.copy(alpha = .4f), shape = RoundedCornerShape(50)) {
                            if (secildi) Text("✓", Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Color.White)
                            else Icon(if (acildi) AzimIkon.Kesfet else AzimIkon.Kilit, null, Modifier.padding(8.dp).size(16.dp), tint = Color.White)
                        }
                    }
                    Spacer(Modifier.height(52.dp))
                    Text(kat.ad(dil), color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(if (secildi) cevir(dil, "Bildirimlerinde ✓", "In your reminders ✓") else if (acildi) cevir(dil, "10 söz · Açık", "10 quotes · Unlocked") else cevir(dil, "10 söz · Kilitli", "10 quotes · Locked"), color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (kategoriler.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
            Column(Modifier.padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(AzimIkon.Kesfet, null, Modifier.size(32.dp), tint = Renk.accent)
                Text(cevir(dil, "Burada henüz bir konu yok.", "No topics here yet."), Modifier.padding(12.dp), color = Renk.metin)
                TextButton(onClick = { arama = ""; filtre = "all"; grupKey = null }) { Text(cevir(dil, "Tüm konuları göster", "Show all topics")) }
            }
        }
    }
    val kat = Kategoriler.bul(detayKey ?: "")
    if (kat != null) ModalBottomSheet(onDismissRequest = { detayKey = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.fillMaxWidth().heightIn(min = 152.dp).clip(RoundedCornerShape(24.dp))) {
                KategoriGorseli(kat.anahtar, Modifier.matchParentSize())
                Column(Modifier.fillMaxWidth().heightIn(min = 152.dp).padding(18.dp), verticalArrangement = Arrangement.Bottom) {
                    Text(kat.ad(dil), color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
            }
            Text(cevir(dil, "10 özgün söz · Kategori önizlemesi", "10 original quotes · Topic preview"), color = Renk.accent, style = MaterialTheme.typography.labelMedium)
            Sozler.kategoriden(kat.anahtar).take(2).forEach { soz ->
                Surface(color = Renk.yuzey, shape = RoundedCornerShape(18.dp)) { Text(soz.metin(dil), Modifier.padding(18.dp), color = Renk.metin, style = MaterialTheme.typography.bodyLarge) }
            }
            if (kat.grup in setOf("filozoflar", "tasavvuf", "inanc")) Text(cevir(dil, "Bu gelenekten ilham alan özgün Ascend düşünceleri; doğrudan alıntı veya kutsal metin değildir.", "Original Ascend reflections inspired by this tradition, not direct quotations or sacred texts."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            if (kat.anahtar in acik) {
                val secildi = kat.anahtar in secili
                Button(onClick = { sec(kat.anahtar) }, enabled = !secildi || secili.size > 1, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(18.dp)) {
                    Text(if (secildi) cevir(dil, "Bildirim konularından çıkar", "Remove from reminder topics") else cevir(dil, "Bildirim konularıma ekle", "Add to my reminder topics"))
                }
                Text(if (secildi && secili.size == 1) cevir(dil, "Son konunu kaldırmadan önce başka bir konu seç.", "Choose another topic before removing your last one.") else cevir(dil, "Bu seçim ana akışını ve bildirimlerini birlikte düzenler.", "This choice updates your home feed and reminders together."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            } else {
                Button(onClick = { detayKey = null; kilidiAc(kat) }, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(18.dp)) { Text(cevir(dil, "Bu kategoriyi aç · Demo", "Unlock this topic · Demo")) }
                OutlinedButton(onClick = { detayKey = null; proAc() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(cevir(dil, "Pro ile tümüne eriş", "Get all topics with Pro")) }
            }
            TextButton(onClick = { detayKey = null }, modifier = Modifier.fillMaxWidth()) { Text(cevir(dil, "Tamam", "Done")) }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeciliKonular(secili: Set<String>, dil: String, duzenle: (() -> Unit)?, modifier: Modifier = Modifier) {
    var tumu by rememberSaveable { mutableStateOf(false) }
    val konular = Kategoriler.tumAltlar.filter { it.anahtar in secili }
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(20.dp), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cevir(dil, "Bildirim konuların", "Your reminder topics"), Modifier.weight(1f), color = Renk.metin, style = MaterialTheme.typography.titleSmall)
                duzenle?.let { TextButton(onClick = it, contentPadding = PaddingValues(horizontal = 8.dp)) { Text(cevir(dil, "Düzenle", "Edit")) } }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                (if (tumu) konular else konular.take(4)).forEach { k ->
                    Surface(color = Renk.accentZemin, shape = RoundedCornerShape(50)) { Text(k.ad(dil), Modifier.padding(horizontal = 10.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall, color = Renk.accent) }
                }
                if (konular.size > 4) TextButton(onClick = { tumu = !tumu }) { Text(if (tumu) cevir(dil, "Daha az", "Less") else "+${konular.size - 4}") }
            }
        }
    }
}
