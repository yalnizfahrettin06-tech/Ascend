package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@Composable
fun FavorilerEkrani(favoriler: List<Soz>, dil: String, cikar: (String) -> Unit, oku: (Soz) -> Unit,
    kesfet: () -> Unit, paylas: (Soz) -> Unit = {}, onBack: (() -> Unit)? = null,
) {
    LazyColumn(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                onBack?.let { IconButton(onClick = it) { Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), tint = Renk.metin) } }
                Text(cevir(dil, "Kaydedilenler", "Saved quotes"), style = MaterialTheme.typography.headlineLarge, color = Renk.metin)
            }
            Text(cevir(dil, "${favoriler.size} söz · Yeniden dönmek istediğinde burada.", "${favoriler.size} quotes · Here when you want to return."), color = Renk.metinIkincil, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
        }
        if (favoriler.isEmpty()) item {
            Column(Modifier.fillMaxWidth().padding(vertical = 50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Icon(AzimIkon.Ayrac, null, Modifier.size(40.dp), tint = Renk.metinIkincil)
                Text(cevir(dil, "Bazı sözler seninle kalır.", "Some words stay with you."), color = Renk.metin, fontFamily = LoraSerif, fontSize = 22.sp, lineHeight = 30.sp)
                Text(cevir(dil, "Bir sözü kaydet. Yeniden okumak ya da paylaşmak için burada bul.", "Save a quote. Find it here to read again or share."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                Button(onClick = kesfet, shape = RoundedCornerShape(50), modifier = Modifier.heightIn(min = 50.dp)) { Text(cevir(dil, "İlk sözünü bul", "Find your first quote")) }
            }
        }
        items(favoriler, key = { it.kimlik }) { soz ->
            Surface(color = Renk.yuzey, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(soz.metin(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 20.sp, lineHeight = 29.sp)
                    Text(soz.imza(dil), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { oku(soz) }) { Text(cevir(dil, "Oku", "Read")) }
                        TextButton(onClick = { paylas(soz) }) { Icon(AzimIkon.Paylas, null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(6.dp)); Text(cevir(dil, "Paylaş", "Share")) }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { cikar(soz.kimlik) }) { Icon(AzimIkon.AyracDolu, cevir(dil, "Kaydedilenlerden çıkar", "Remove from saved"), Modifier.size(22.dp)) }
                    }
                }
            }
        }
    }
}
