package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Kategoriler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProEkrani(
    dil: String,
    acik: Boolean,
    kaydediliyor: Boolean = false,
    hata: String? = null,
    kapat: () -> Unit,
    degistir: (Boolean) -> Unit,
) {
    val kategoriSayisi = Kategoriler.tumAltlar.size
    ModalBottomSheet(
        onDismissRequest = { if (!kaydediliyor) kapat() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Renk.zemin,
        dragHandle = null,
    ) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f).testTag("pro-sheet")) {
            Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                ProRozeti(metin = "PRO DEMO")
                Spacer(Modifier.weight(1f))
                IconButton(onClick = kapat, enabled = !kaydediliyor, modifier = Modifier.testTag("pro-close")) { Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), tint = Renk.metin) }
            }
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))) {
                    AtmosferResmi(Atmosfer.ZIRVE, Modifier.matchParentSize(), .28f)
                    Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(AzimIkon.Dag, null, Modifier.size(34.dp), tint = Color(0xFFE7CAA1))
                        Spacer(Modifier.height(12.dp))
                        Text(cevir(dil, "İlhamın tamamı.", "More room for inspiration."), color = Color.White, fontFamily = LoraSerif, fontSize = 29.sp, lineHeight = 36.sp)
                        Text(cevir(dil, "Tüm konular. Kendi tarzında paylaşımlar.", "Every topic. Share in your own style."), color = Color.White.copy(alpha = .90f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                ProOzelligi(AzimIkon.Kesfet, cevir(dil, "$kategoriSayisi kategorinin tamamı", "All $kategoriSayisi categories"), cevir(dil, "Motivasyondan felsefeye, tüm sözlere eriş.", "From motivation to philosophy, explore every quote."))
                ProOzelligi(AzimIkon.Dag, cevir(dil, "Bütün arka planlar", "Every background"), cevir(dil, "Sahneler, renkler ve kendi fotoğrafların.", "Scenes, colors and your own photos."))
                ProOzelligi(AzimIkon.Paylas, cevir(dil, "Gelişmiş paylaşım araçları", "Advanced sharing tools"), cevir(dil, "Video, süre, farklı boyutlar ve yazı stilleri.", "Video, duration, layouts and type styles."))
                Surface(color = Renk.yuzey, shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(cevir(dil, "Bu bir test demosu", "This is a test demo"), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
                        Text(cevir(dil, "Bildirim konularını yine sen seçersin.", "You still choose your notification topics."), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                        if (acik) Text(cevir(dil, "Demo kapanınca Pro araçları kilitlenir. Tek tek açtığın kategoriler sende kalır.", "Turning off the demo locks Pro tools. Categories you unlocked individually stay available."), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                hata?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }) }
                Text(cevir(dil, "Ödeme alınmaz. Abonelik başlatılmaz.", "No payment is taken. No subscription starts."), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                Button(
                    onClick = { degistir(!acik) }, enabled = !kaydediliyor,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag(if (acik) "pro-demo-disable" else "pro-demo-enable"),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    if (kaydediliyor) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(if (acik) cevir(dil, "Pro demosunu kapat", "Turn off Pro demo") else cevir(dil, "Pro demosunu aç", "Enable Pro demo"))
                }
                Text(cevir(dil, "İstediğin zaman değiştirebilirsin.", "You can change this anytime."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ProRozeti(modifier: Modifier = Modifier, metin: String = "PRO") {
    Text(
        metin, modifier.clip(RoundedCornerShape(7.dp)).background(Renk.accent.copy(alpha = .15f)).padding(horizontal = 7.dp, vertical = 4.dp),
        color = Renk.accent, fontSize = 9.sp, lineHeight = 11.sp, letterSpacing = .8.sp, fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ProOzelligi(ikon: ImageVector, baslik: String, aciklama: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(Renk.yuzey), contentAlignment = Alignment.Center) {
            Icon(ikon, null, Modifier.size(21.dp), tint = Renk.accent)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(baslik, style = MaterialTheme.typography.titleMedium, color = Renk.metin)
            Text(aciklama, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
        }
    }
}
