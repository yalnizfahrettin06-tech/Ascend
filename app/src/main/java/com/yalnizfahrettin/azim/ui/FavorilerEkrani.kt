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
    kesfet: () -> Unit, paylas: (Soz) -> Unit = {}, onBack: (() -> Unit)? = null, embedded: Boolean = false,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(favoriler, query, dil) { LibraryQuery.saved(favoriler, query, dil) }
    LazyColumn(
        Modifier.fillMaxSize().background(Renk.zemin).then(if (embedded) Modifier else Modifier.statusBarsPadding()).testTag("saved-list"),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (!embedded) item {
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
        itemsIndexed(results, key = { _, soz -> soz.kimlik }) { _, soz ->
            Surface(shape = RoundedCornerShape(22.dp),color = Renk.yuzey,modifier = Modifier.fillMaxWidth().testTag("saved-quote-${soz.kimlik}")) {
                Column {
                    Column(Modifier.clickable(onClickLabel = cevir(dil,"Oku","Read")) { oku(soz) }.testTag("saved-read-${soz.kimlik}")) {
                        EditorialPhoto(EditorialArt.saved(soz.kategori),Modifier.fillMaxWidth().aspectRatio(2.65f).testTag("saved-art-${soz.kimlik}"))
                        Column(Modifier.padding(start = 18.dp,end = 18.dp,top = 18.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(Kategoriler.bul(soz.kategori)?.ad(dil).orEmpty(),color = Renk.metinIkincil,fontSize = 12.sp)
                            Text(soz.metin(dil),color = Renk.metin,fontFamily = LoraSerif,fontSize = 21.sp,lineHeight = 29.sp,maxLines = 5,overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Text(soz.sunumEtiketi(dil),color = Renk.metinIkincil,fontSize = 11.sp,lineHeight = 16.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp,vertical = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { paylas(soz) },modifier = Modifier.heightIn(min = 48.dp)) {
                            Icon(AzimIkon.Paylas,null,Modifier.size(19.dp)); Spacer(Modifier.width(8.dp)); Text(cevir(dil,"Paylaş","Share"))
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { cikar(soz.kimlik) },modifier = Modifier.size(48.dp)) {
                            Icon(AzimIkon.KalpDolu,cevir(dil,"Kaydedilenlerden çıkar","Remove from saved"),Modifier.size(22.dp),tint = Renk.metin)
                        }
                    }
                }
            }
        }
    }
}
