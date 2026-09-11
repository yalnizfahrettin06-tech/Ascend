package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FavorilerEkrani(favoriler: List<Soz>, dil: String, cikar: (String) -> Unit, oku: (Soz) -> Unit,
    kesfet: () -> Unit, paylas: (Soz) -> Unit = {}, onBack: (() -> Unit)? = null,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(favoriler, query, dil) { LibraryQuery.saved(favoriler, query, dil) }
    LazyColumn(
        Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("saved-list"),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onBack?.let {
                    OutlinedIconButton(onClick = it, modifier = Modifier.padding(top = 17.dp).size(48.dp),
                        border = BorderStroke(1.dp, Renk.kenarlik)) {
                        Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), Modifier.size(22.dp), tint = Renk.metin)
                    }
                }
                EditoryalBaslik(
                    cevir(dil, "KİŞİSEL KOLEKSİYON", "PERSONAL COLLECTION"),
                    cevir(dil, "Kaydedilenler", "Saved quotes"),
                    modifier = Modifier.weight(1f),
                )
            }
            Text(cevir(dil, "${favoriler.size} söz · Yeniden dönmek istediğinde burada.", "${favoriler.size} quotes · Here when you want to return."),
                color = Renk.metinIkincil, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(Modifier.padding(top = 22.dp), color = Renk.kenarlik)
        }
        if (favoriler.isNotEmpty()) item {
            OutlinedTextField(value = query, onValueChange = { query = it }, singleLine = true,
                label = { Text(cevir(dil, "Kaydedilenlerde ara", "Search saved quotes")) },
                leadingIcon = { Icon(AzimIkon.Ara, null) },
                trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) {
                    Icon(AzimIkon.Kapat, cevir(dil, "Aramayı temizle", "Clear search"))
                } }, modifier = Modifier.fillMaxWidth().testTag("saved-search"), shape = RoundedCornerShape(14.dp))
            if (results.isEmpty()) Text(cevir(dil, "Bu aramayla eşleşen kayıtlı söz yok.", "No saved quotes match this search."),
                color = Renk.metinIkincil, modifier = Modifier.padding(top = 16.dp).testTag("saved-no-results"))
        }
        if (favoriler.isEmpty()) item {
            Box(Modifier.fillMaxWidth()) {
                KlasikGorsel(KlasikMotif.COLUMN, Modifier.align(Alignment.BottomEnd).width(132.dp).height(270.dp), opacity = .13f)
                Column(Modifier.fillMaxWidth().padding(vertical = 30.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Icon(AzimIkon.Ayrac, null, Modifier.size(30.dp), tint = Renk.metin)
                    Text(cevir(dil, "Bazı sözler\nseninle kalır.", "Some words\nstay with you."), color = Renk.metin,
                        fontFamily = LoraSerif, fontSize = 29.sp, lineHeight = 36.sp)
                    Text(cevir(dil, "Bir sözü kaydet. Yeniden okumak ya da paylaşmak için burada bul.", "Save a quote. Find it here to read again or share."),
                        color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = kesfet, shape = RoundedCornerShape(50), modifier = Modifier.heightIn(min = 52.dp)) {
                        Text(cevir(dil, "İlk sözünü bul", "Find your first quote"))
                    }
                }
            }
        }
        itemsIndexed(results, key = { _, soz -> soz.kimlik }) { index, soz ->
            Column(Modifier.fillMaxWidth().testTag("saved-quote-${soz.kimlik}"), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text((index + 1).toString().padStart(2, '0'), color = Renk.metinIkincil, style = MaterialTheme.typography.labelMedium)
                    Box(Modifier.width(26.dp).height(1.dp).background(Renk.kenarlikGuclu))
                    Text(Kategoriler.bul(soz.kategori)?.ad(dil).orEmpty(), color = Renk.metinIkincil,
                        style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
                Text(soz.metin(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 33.sp)
                Text(soz.imza(dil), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { oku(soz) }, modifier = Modifier.heightIn(min = 48.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Icon(AzimIkon.Kitap, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(cevir(dil, "Oku", "Read"))
                    }
                    TextButton(onClick = { paylas(soz) }, modifier = Modifier.heightIn(min = 48.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Icon(AzimIkon.Paylas, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(cevir(dil, "Paylaş", "Share"))
                    }
                    IconButton(onClick = { cikar(soz.kimlik) }, modifier = Modifier.size(48.dp)) {
                        Icon(AzimIkon.AyracDolu, cevir(dil, "Kaydedilenlerden çıkar", "Remove from saved"), Modifier.size(22.dp), tint = Renk.metin)
                    }
                }
                HorizontalDivider(color = Renk.kenarlik)
            }
        }
        if (favoriler.isNotEmpty()) item {
            Text(cevir(dil, "Her dönüşte yeni bir anlam.", "A new meaning each time you return."), color = Renk.metinIkincil,
                fontFamily = LoraSerif, fontStyle = FontStyle.Italic, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
        }
    }
}
