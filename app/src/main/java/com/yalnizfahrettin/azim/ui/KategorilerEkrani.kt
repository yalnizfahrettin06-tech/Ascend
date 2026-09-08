package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cevir(dil, "Keşfet", "Explore"), Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge, color = Renk.metin)
                    TextButton(onClick = proAc) { Text(if (pro) "PRO DEMO" else "PRO", style = MaterialTheme.typography.labelMedium) }
                }
                Text(cevir(dil, "Bir konuya uğra. Yeni bir bakış bul.", "Visit a topic. Find a new perspective."), color = Renk.metinIkincil)
                OutlinedTextField(arama, { arama = it }, placeholder = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers")) },
                    modifier = Modifier.fillMaxWidth().testTag("category-search"), singleLine = true, shape = RoundedCornerShape(14.dp),
                    leadingIcon = { Icon(AzimIkon.Ara, null, Modifier.size(20.dp)) },
                    trailingIcon = { if (arama.isNotEmpty()) IconButton(onClick = { arama = "" }) { Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search")) } })
                TextButton(onClick = { filtre = if (filtre == "selected") "all" else "selected" }, contentPadding = PaddingValues(0.dp)) {
                    Icon(AzimIkon.Yukselis, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(cevir(dil, "Bildirim konuların · ${secili.size}", "Your reminder topics · ${secili.size}"))
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
                Text(cevir(dil, "${kategoriler.size} konu", "${kategoriler.size} topics"), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
                Spacer(Modifier.height(2.dp))
            }
        }
        items(kategoriler, key = { it.anahtar }) { kat ->
            val secildi = kat.anahtar in secili
            val acildi = kat.anahtar in acik
            Column {
                Row(Modifier.fillMaxWidth().heightIn(min = 76.dp).clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button) { detayKey = kat.anahtar }.testTag("category-${kat.anahtar}").semantics {
                        stateDescription = if (secildi) cevir(dil, "Bildirimlerinde seçili", "Selected for reminders") else if (acildi) cevir(dil, "Açık, seçilmedi", "Unlocked, not selected") else cevir(dil, "Kilitli", "Locked")
                    }.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(kat.ad(dil), color = Renk.metin, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        Text(Kategoriler.grupBul(kat.grup)?.ad(dil).orEmpty() + " · " + if (secildi) cevir(dil, "Seçili", "Selected") else if (acildi) cevir(dil, "Açık", "Unlocked") else cevir(dil, "Demo ile aç", "Unlock with demo"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(if (secildi) AzimIkon.Tik else if (acildi) AzimIkon.Ileri else AzimIkon.Kilit, null, Modifier.size(20.dp), tint = Renk.metinIkincil)
                }
                HorizontalDivider(color = Renk.kenarlik.copy(alpha = .55f))
            }
        }
        if (kategoriler.isEmpty()) item {
            Column(Modifier.fillMaxWidth().padding(vertical = 36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(cevir(dil, "Bu aramada bir konu yok.", "No topics match this search."), color = Renk.metin)
                TextButton(onClick = { arama = ""; filtre = "all"; grupKey = null }) { Text(cevir(dil, "Tüm konuları göster", "Show all topics")) }
            }
        }
    }
    if (alanlarAcik) ModalBottomSheet(onDismissRequest = { alanlarAcik = false }, containerColor = Renk.zemin) {
        LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)) {
            item { Text(cevir(dil, "Bir alan seç", "Choose an area"), style = MaterialTheme.typography.headlineSmall, color = Renk.metin, modifier = Modifier.padding(bottom = 16.dp)) }
            item { TextButton(onClick = { grupKey = null; alanlarAcik = false }, modifier = Modifier.fillMaxWidth()) { Text(cevir(dil, "Tüm alanlar", "All areas")) } }
            items(Kategoriler.gruplar, key = { it.anahtar }) { g ->
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable { grupKey = g.anahtar; alanlarAcik = false }.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(g.ad(dil), Modifier.weight(1f), color = Renk.metin)
                    if (grupKey == g.anahtar) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.accent)
                }
            }
        }
    }
    val kat = Kategoriler.bul(detayKey ?: "")
    if (kat != null) ModalBottomSheet(onDismissRequest = { detayKey = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(kat.ad(dil), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
            Text(cevir(dil, "10 özgün söz · Bir göz at", "10 original quotes · Take a look"), color = Renk.metinIkincil, style = MaterialTheme.typography.labelMedium)
            Sozler.kategoriden(kat.anahtar).take(2).forEach { soz ->
                Surface(color = Renk.yuzey, shape = RoundedCornerShape(16.dp)) { Text(soz.metin(dil), Modifier.padding(18.dp), color = Renk.metin, style = MaterialTheme.typography.bodyLarge) }
            }
            if (kat.grup in setOf("filozoflar", "tasavvuf", "inanc")) Text(cevir(dil, "Bu gelenekten ilham alan özgün Ascend düşünceleri; doğrudan alıntı veya kutsal metin değildir.", "Original Ascend reflections inspired by this tradition, not direct quotations or sacred texts."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            if (kat.anahtar in acik) {
                val secildi = kat.anahtar in secili
                Button(onClick = { sec(kat.anahtar) }, enabled = !secildi || secili.size > 1, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp), shape = RoundedCornerShape(50)) {
                    Text(if (secildi) cevir(dil, "Bildirim konularından çıkar", "Remove from reminder topics") else cevir(dil, "Bildirim konularıma ekle", "Add to my reminder topics"))
                }
                Text(if (secildi && secili.size == 1) cevir(dil, "Son konunu kaldırmadan önce başka bir konu seç.", "Choose another topic before removing your last one.") else cevir(dil, "Seçimini istediğin zaman değiştirebilirsin.", "You can change your choice any time."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
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
