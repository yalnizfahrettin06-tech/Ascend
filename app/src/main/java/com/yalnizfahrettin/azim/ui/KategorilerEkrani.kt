package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@Composable
fun KilitDialog(grup: KategoriGrubu, dil: String, kapat: () -> Unit, hazir: Boolean, izle: () -> Unit) {
    AlertDialog(onDismissRequest = kapat, title = { Text(grup.ad(dil)) },
        text = { Text(if (hazir) cevir(dil, "Bir reklam izleyerek bu koleksiyonu açabilirsin.", "Watch an ad to unlock this collection.") else cevir(dil, "Bu koleksiyon şu anda kilitli. Reklamla açma bu sürümde kullanılamıyor. Ücretsiz koleksiyonları keşfetmeye devam edebilirsin.", "This collection is locked. Ad unlocking is unavailable in this version. You can keep exploring free collections.")) },
        confirmButton = { TextButton(onClick = if (hazir) izle else kapat) { Text(if (hazir) cevir(dil, "İzle", "Watch") else cevir(dil, "Tamam", "Done")) } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategorilerEkrani(secili: Set<String>, acikGruplar: Set<String>, dil: String, sec: (String) -> Unit,
    kilidiAc: (KategoriGrubu) -> Unit, acilacakGrup: String? = null,
) {
    var grupKey by rememberSaveable { mutableStateOf<String?>(null) }
    var arama by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(acilacakGrup) { if (acilacakGrup in acikGruplar) grupKey = acilacakGrup }
    val gruplar = Kategoriler.gruplar.filter { it.ad(dil).contains(arama, true) || it.altlar.any { k -> k.ad(dil).contains(arama, true) } }
    LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding(), contentPadding = PaddingValues(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(cevir(dil, "İlhamını keşfet.", "Find your inspiration."), style = MaterialTheme.typography.headlineLarge, color = Renk.metin)
            Text(cevir(dil, "Olumlamadan felsefeye, sana açılan dünyalar.", "From affirmations to philosophy, a world of ideas."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(arama, { arama = it }, label = { Text(cevir(dil, "Konu veya düşünür ara", "Search topics or thinkers")) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp))
            Text(cevir(dil, "${secili.size} başlık akışında ve bildirimlerinde", "${secili.size} topics in your feed and reminders"), color = Renk.accent, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 8.dp))
        } }
        items(gruplar, key = { it.anahtar }) { g ->
            val acik = g.anahtar in acikGruplar
            val sayi = Sozler.tumu().count { Kategoriler.bul(it.kategori)?.grup == g.anahtar }
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).clickable { if (acik) grupKey = g.anahtar else kilidiAc(g) }) {
                AtmosferResmi(Atmosfer.grup(g.anahtar), Modifier.matchParentSize(), .32f)
                Column(Modifier.fillMaxWidth().heightIn(min = 178.dp).padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Icon(if (acik) AzimIkon.Kesfet else AzimIkon.Kilit, null, Modifier.size(20.dp), tint = Color.White) }
                    Spacer(Modifier.height(52.dp))
                    Text(g.ad(dil), color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(if (acik) cevir(dil, "$sayi söz · ${g.altlar.count { it.anahtar in secili }} seçili", "$sayi quotes · ${g.altlar.count { it.anahtar in secili }} selected") else cevir(dil, "$sayi söz · Kilitli", "$sayi quotes · Locked"), color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (gruplar.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) { Text(cevir(dil, "Bu aramada bir konu bulunamadı.", "No topics match your search."), color = Renk.metinIkincil) }
    }
    val grup = Kategoriler.gruplar.find { it.anahtar == grupKey }
    if (grup != null) ModalBottomSheet(onDismissRequest = { grupKey = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        Column(Modifier.fillMaxWidth().heightIn(max = 570.dp).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(grup.ad(dil), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
            Text(cevir(dil, "Seçtiğin başlıklar akışına ve bildirimlerine katılır.", "Selected topics appear in your feed and reminders."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            grup.altlar.forEach { kat ->
                val adet = Sozler.kategoriden(kat.anahtar).size
                val secildi = kat.anahtar in secili
                Surface(color = if (secildi) Renk.accentZemin else Renk.yuzey, shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).toggleable(secildi, enabled = adet > 0, role = Role.Checkbox) { sec(kat.anahtar) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(kat.ad(dil), color = Renk.metin, style = MaterialTheme.typography.bodyLarge)
                            Text(if (adet > 0) cevir(dil, "$adet söz", "$adet quotes") else cevir(dil, "Yeni içerik hazırlanıyor", "New content coming"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                        }
                        Checkbox(secildi, null, enabled = adet > 0)
                    }
                }
            }
            Text(cevir(dil, "Akışın boş kalmasın diye son seçili başlık korunur.", "The last selected topic stays on to keep your feed available."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            Button(onClick = { grupKey = null }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text(cevir(dil, "Tamam", "Done")) }
            Spacer(Modifier.height(16.dp))
        }
    }
}
