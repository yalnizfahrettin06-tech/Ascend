package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.BuildConfig
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.notif.TeslimatYardimi

/** Reading preferences and reminder controls, with a persistent way back. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyarlarEkrani(
    tema: TemaModu,
    dil: String,
    gunlukAdet: Int,
    baslangic: Int,
    bitis: Int,
    haptik: Boolean,
    dinamikRenk: Boolean,
    palet: Palet,
    hatirlaticiAcik: Boolean,
    bildirimIzni: Boolean,
    hatirlaticiSec: (Boolean) -> Unit,
    temaSec: (TemaModu) -> Unit,
    dilSec: (String) -> Unit,
    adetSec: (Int) -> Unit,
    saatSec: (Int, Int) -> Unit,
    haptikSec: (Boolean) -> Unit,
    dinamikSec: (Boolean) -> Unit,
    paletSec: (Palet) -> Unit,
    seciliKategoriSayisi: Int,
    geri: () -> Unit,
) {
    var kurulum by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = geri, modifier = Modifier.size(48.dp)) {
                Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), Modifier.size(24.dp), tint = Renk.metin)
            }
            Text(stringResource(R.string.ayarlar), fontFamily = LoraSerif, fontSize = 28.sp,
                color = Renk.metin, modifier = Modifier.weight(1f).semantics { heading() })
        }
        HorizontalDivider(color = Renk.kenarlik)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(Modifier.fillMaxWidth().padding(top = 16.dp).heightIn(min = 88.dp)) {
                KlasikGorsel(KlasikMotif.ARCH, Modifier.align(Alignment.CenterEnd).size(88.dp), opacity = .13f)
                Column(Modifier.fillMaxWidth(.8f).align(Alignment.CenterStart), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text(cevir(dil, "SANA AİT BİR ALAN", "A SPACE OF YOUR OWN"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.4.sp)
                    Text(cevir(dil, "Görünümü ve gününün ritmini sana göre düzenle.", "Make the page and your daily rhythm feel like yours."),
                        color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                }
            }
            AyarBolumu("01", stringResource(R.string.gorunum))
            SecimSatiri(stringResource(R.string.tema),
                listOf(TemaModu.SISTEM to stringResource(R.string.tema_sistem),
                    TemaModu.AYDINLIK to stringResource(R.string.tema_aydinlik),
                    TemaModu.KARANLIK to stringResource(R.string.tema_karanlik),
                    TemaModu.OLED to stringResource(R.string.tema_oled)), tema, temaSec)
            SecimSatiri(stringResource(R.string.renk_paleti),
                listOf(Palet.MERMER, Palet.MONO).map { it to it.etiket(dil) }, guncelPalet(palet), paletSec)
            SecimSatiri(stringResource(R.string.dil), listOf("tr" to "Türkçe", "en" to "English"), dil, dilSec)

            AyarBolumu("02", stringResource(R.string.bildirimler))
            AyarAnahtari(stringResource(R.string.asc_hatirlaticilar), hatirlaticiAcik, hatirlaticiSec)
            if (hatirlaticiAcik && !bildirimIzni) {
                val context = LocalContext.current
                Text(stringResource(R.string.asc_bildirim_engelli), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = { TeslimatYardimi.bildirimAyarlariniAc(context) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp)) { Text(stringResource(R.string.sistem_bildirim_ayarlari)) }
            }
            if (hatirlaticiAcik) BildirimPlani(
                adet = gunlukAdet, baslangic = baslangic, bitis = bitis,
                seciliKategoriSayisi = seciliKategoriSayisi, adetDegisti = adetSec,
                araligiDegisti = saatSec, dil = dil)
            OutlinedButton(onClick = { kurulum = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(14.dp)) {
                Text(cevir(dil, "Ayrıntılı bildirim kurulumu", "Detailed notification setup"))
            }
            AyarAnahtari(stringResource(R.string.haptik), haptik, haptikSec)

            AyarBolumu("03", stringResource(R.string.hakkinda))
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.surum), Modifier.weight(1f), color = Renk.metin, style = MaterialTheme.typography.bodyMedium)
                Text(BuildConfig.VERSION_NAME, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider(color = Renk.kenarlik)
            Row(Modifier.fillMaxWidth().padding(bottom = 28.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Yukselis, null, Modifier.size(18.dp), tint = Renk.accent)
                Text(cevir(dil, "Bir söz. Bir adım.", "One quote. One step."), fontFamily = LoraSerif, fontSize = 17.sp, color = Renk.metinIkincil)
            }
        }
    }
    if (kurulum) ModalBottomSheet(onDismissRequest = { kurulum = false }, containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.9f)) {
            Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(cevir(dil, "Bildirim kurulumu", "Notification setup"), fontFamily = LoraSerif, fontSize = 26.sp,
                    color = Renk.metin, modifier = Modifier.weight(1f).semantics { heading() })
                IconButton(onClick = { kurulum = false }, modifier = Modifier.size(48.dp)) {
                    Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), tint = Renk.metin)
                }
            }
            HorizontalDivider(color = Renk.kenarlik, modifier = Modifier.padding(top = 12.dp))
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp)) {
                BildirimKurulumu(dil, bildirimIzni) { hatirlaticiSec(true) }
            }
            TextButton(onClick = { kurulum = false }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                Text(cevir(dil, "Tamam", "Done"))
            }
        }
    }
}

@Composable
private fun AyarBolumu(number: String, title: String) {
    Column(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        HorizontalDivider(color = Renk.kenarlik)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(number, color = Renk.accent, fontFamily = LoraSerif, fontSize = 16.sp)
            Text(title, color = Renk.metin, fontFamily = LoraSerif, fontSize = 25.sp,
                modifier = Modifier.weight(1f).semantics { heading() })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> SecimSatiri(baslik: String, secenekler: List<Pair<T, String>>, secili: T, sec: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(baslik, style = MaterialTheme.typography.bodyMedium, color = Renk.metinIkincil)
        FlowRow(modifier = Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            secenekler.forEach { (deger, etiket) -> AyarSecenegi(etiket, deger == secili) { sec(deger) } }
        }
    }
}

@Composable
private fun AyarSecenegi(text: String, selected: Boolean, choose: () -> Unit) {
    Row(Modifier.heightIn(min = 48.dp).clip(RoundedCornerShape(12.dp))
        .background(if (selected) Renk.yuzey else Renk.zemin)
        .border(if (selected) 1.5.dp else 1.dp, if (selected) Renk.metin else Renk.kenarlik, RoundedCornerShape(12.dp))
        .selectable(selected, role = Role.RadioButton, onClick = choose)
        .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, modifier = Modifier.weight(1f, fill = false), color = Renk.metin, style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        if (selected) Icon(AzimIkon.Tik, null, Modifier.size(16.dp), tint = Renk.metin)
    }
}

@Composable
private fun AyarAnahtari(label: String, enabled: Boolean, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(label, Modifier.weight(1f), color = Renk.metin, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = enabled, onCheckedChange = change, modifier = Modifier.semantics { contentDescription = label })
    }
}
