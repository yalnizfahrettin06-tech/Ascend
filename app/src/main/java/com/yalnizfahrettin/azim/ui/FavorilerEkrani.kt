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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@Composable
fun FavorilerEkrani(favoriler: List<Soz>, dil: String, cikar: (String) -> Unit, oku: (Soz) -> Unit, kesfet: () -> Unit, paylas: (Soz) -> Unit = {}) {
    LazyColumn(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(cevir(dil, "Sende kalan sözler.", "Words you keep."), style = MaterialTheme.typography.headlineLarge, color = Renk.metin)
            Text(cevir(dil, "${favoriler.size} söz · Yeniden oku, kendinden bir iz kat, paylaş.", "${favoriler.size} quotes · Revisit, personalize and share."), color = Renk.metinIkincil, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
        }
        if (favoriler.isEmpty()) item {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))) {
                AtmosferResmi(Atmosfer.DENIZ, Modifier.matchParentSize())
                Column(Modifier.padding(26.dp).heightIn(min = 320.dp), verticalArrangement = Arrangement.Center) {
                    Icon(AzimIkon.Kalp, null, Modifier.size(40.dp), tint = Color.White)
                    Spacer(Modifier.height(22.dp))
                    Text(cevir(dil, "Bazı sözler\nseninle kalır.", "Some words\nstay with you."), color = Color.White, fontFamily = LoraSerif, fontSize = 28.sp, lineHeight = 36.sp)
                    Text(cevir(dil, "Kalbe dokun, burada yeniden bul.", "Tap the heart and find them here."), color = Color.White, modifier = Modifier.padding(vertical = 16.dp))
                    Button(onClick = kesfet) { Text(cevir(dil, "İlk sözünü bul", "Find your first quote")) }
                }
            }
        }
        items(favoriler, key = { it.kimlik }) { soz ->
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))) {
                AtmosferResmi(Atmosfer.grup(Kategoriler.bul(soz.kategori)?.grup), Modifier.matchParentSize(), .4f)
                Column(Modifier.padding(22.dp)) {
                    Text(soz.metin(dil), color = Color.White, fontFamily = LoraSerif, fontSize = 21.sp, lineHeight = 30.sp, modifier = Modifier.clickable { oku(soz) })
                    Text(soz.yazar, color = Color.White, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp))
                    Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { paylas(soz) }) { Icon(AzimIkon.Paylas, null, tint = Color.White, modifier = Modifier.size(18.dp)); Text(cevir(dil, "  Paylaş", "  Share"), color = Color.White) }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { cikar(soz.kimlik) }) { Icon(AzimIkon.KalpDolu, cevir(dil, "Kaydedilenlerden çıkar", "Remove from saved"), tint = Color.White) }
                    }
                }
            }
        }
    }
}
