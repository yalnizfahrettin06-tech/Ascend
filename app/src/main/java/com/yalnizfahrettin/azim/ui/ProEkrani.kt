package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                Box(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(AzimIkon.Yukselis, null, Modifier.size(32.dp), tint = Renk.metin)
                        Box(Modifier.width(32.dp).height(1.dp).background(Renk.accent))
                        Text(cevir(dil, "İlhamın tamamı.", "More room for inspiration."), color = Renk.metin, fontFamily = ArayuzFont,
                            fontSize = 32.sp, lineHeight = 39.sp, modifier = Modifier.semantics { heading() })
                        Text(cevir(dil, "Tüm konular. Kendi tarzında paylaşımlar.", "Every topic. Share in your own style."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                HorizontalDivider(color = Renk.kenarlik)
                ProOzelligi(AzimIkon.Kesfet, cevir(dil, "$kategoriSayisi kategorinin tamamı", "All $kategoriSayisi categories"), cevir(dil, "Motivasyondan felsefeye, tüm sözlere eriş.", "From motivation to philosophy, explore every quote."))
                HorizontalDivider(color = Renk.kenarlik)
                ProOzelligi(AzimIkon.Dag, cevir(dil, "Bütün arka planlar", "Every background"), cevir(dil, "Sahneler, renkler ve kendi fotoğrafların.", "Scenes, colors and your own photos."))
                HorizontalDivider(color = Renk.kenarlik)
                ProOzelligi(AzimIkon.Paylas, cevir(dil, "Gelişmiş paylaşım araçları", "Advanced sharing tools"), cevir(dil, "Video, süre, farklı boyutlar ve yazı stilleri.", "Video, duration, layouts and type styles."))
                ProOzelligi(AzimIkon.Kitap, cevir(dil, "Kişisel temalar ve tüm widget’lar", "Personal themes and every widget"), cevir(dil, "Roma, Koyu Atlı Yolcu ve kendi görünümünle ana ekran widget’ları.", "Rome, Dark Rider and home screen widgets in your own style."))
                Surface(color = Renk.yuzey, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Renk.kenarlik)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(cevir(dil, "Bu bir test demosu", "This is a test demo"), fontFamily = ArayuzFont, fontSize = 21.sp, color = Renk.metin)
                        Text(cevir(dil, "Bildirim konularını yine sen seçersin.", "You still choose your notification topics."), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                        if (acik) Text(cevir(dil, "Demo kapanınca Pro araçları kilitlenir. Tek tek açtığın kategoriler sende kalır.", "Turning off the demo locks Pro tools. Categories you unlocked individually stay available."), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                    }
                }
            }
            HorizontalDivider(color = Renk.kenarlik)
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                hata?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }) }
                Text(cevir(dil, "Ödeme alınmaz. Abonelik başlatılmaz.", "No payment is taken. No subscription starts."), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil, textAlign = TextAlign.Center)
                Button(
                    onClick = { degistir(!acik) }, enabled = !kaydediliyor,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag(if (acik) "pro-demo-disable" else "pro-demo-enable"),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    if (kaydediliyor) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(if (acik) cevir(dil, "Pro demosunu kapat", "Turn off Pro demo") else cevir(dil, "Pro demosunu aç", "Enable Pro demo"), textAlign = TextAlign.Center)
                }
                Text(cevir(dil, "İstediğin zaman değiştirebilirsin.", "You can change this anytime."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ProRozeti(modifier: Modifier = Modifier, metin: String = "PRO") {
    Text(
        metin, modifier.clip(RoundedCornerShape(6.dp)).background(Renk.yuzey)
            .border(1.dp, Renk.accent.copy(alpha = .35f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 5.dp),
        color = Renk.accent, fontSize = 10.sp, lineHeight = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun ProOzelligi(ikon: ImageVector, baslik: String, aciklama: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Icon(ikon, null, Modifier.size(23.dp), tint = Renk.metin)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(baslik, fontFamily = ArayuzFont, fontSize = 22.sp, lineHeight = 28.sp, color = Renk.metin)
            Text(aciklama, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
        }
    }
}
