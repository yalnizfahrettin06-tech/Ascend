package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@Composable
fun Onboarding(dil: String, kaydediliyor: Boolean = false, hata: String? = null,
    bildirimIzni: Boolean = false, izinIste: () -> Unit = {},
    bitir: (Set<String>, Int, Int, Int, Boolean) -> Unit,
) {
    var adim by rememberSaveable { mutableIntStateOf(0) }
    var secili by rememberSaveable { mutableStateOf(Baslangic.varsayilan) }
    var adet by rememberSaveable { mutableIntStateOf(3) }
    var bas by rememberSaveable { mutableIntStateOf(9) }
    var bit by rememberSaveable { mutableIntStateOf(21) }
    val scroll = rememberScrollState()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val view = androidx.compose.ui.platform.LocalView.current
    val renk = Renk
    val activity = generateSequence(ctx) { (it as? android.content.ContextWrapper)?.baseContext }.filterIsInstance<android.app.Activity>().firstOrNull()
    DisposableEffect(adim, renk.karanlikMi) {
        val controller = activity?.let { androidx.core.view.WindowCompat.getInsetsController(it.window, view) }
        controller?.isAppearanceLightStatusBars = adim != 0 && !renk.karanlikMi
        controller?.isAppearanceLightNavigationBars = adim != 0 && !renk.karanlikMi
        onDispose {
            controller?.isAppearanceLightStatusBars = !renk.karanlikMi
            controller?.isAppearanceLightNavigationBars = !renk.karanlikMi
        }
    }
    LaunchedEffect(adim) { scroll.scrollTo(0) }
    BackHandler(adim > 0 || kaydediliyor) { if (!kaydediliyor) adim-- }
    Box(Modifier.fillMaxSize().background(Renk.zemin)) {
        if (adim == 0) AtmosferResmi(Atmosfer.ZIRVE, Modifier.matchParentSize(), .12f)
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (adim > 0) IconButton(onClick = { adim-- }, enabled = !kaydediliyor) { Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), tint = Renk.metin) }
                Text("ASCEND", fontSize = 18.sp, letterSpacing = 4.sp, color = if (adim == 0) Color.White else Renk.metin, modifier = Modifier.weight(1f))
                Text("0${adim + 1} / 04", fontSize = 12.sp, color = if (adim == 0) Color.White else Renk.metinIkincil)
            }
            if (adim > 0) Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(4) { i -> Box(Modifier.weight(1f).height(3.dp).background(if (i <= adim) Renk.accent else Renk.kenarlik, RoundedCornerShape(4.dp))) }
            }
            Column(Modifier.weight(1f).verticalScroll(scroll).padding(horizontal = 24.dp, vertical = if (adim == 0) 8.dp else 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (adim) {
                    0 -> {
                        Spacer(Modifier.height(36.dp))
                        Text(cevir(dil, "KÜÇÜK BİR SÖZ. YENİ BİR BAKIŞ.", "A FEW WORDS. A FRESH PERSPECTIVE."), color = Color.White, fontSize = 10.sp, letterSpacing = 1.6.sp)
                        Text(cevir(dil, "Her güne\nbir kıvılcım.", "A spark for\nevery day."), color = Color.White, fontFamily = LoraSerif, fontSize = 42.sp, lineHeight = 50.sp, modifier = Modifier.semantics { heading() })
                        Text(cevir(dil, "Motivasyon, olumlama ve felsefe.\nTam ihtiyacın olan anda.", "Motivation, affirmations and philosophy.\nWhen you need them most."), color = Color.White, fontSize = 16.sp, lineHeight = 25.sp)
                        Spacer(Modifier.height(135.dp))
                        Surface(color = Color(0xD91A2630), shape = RoundedCornerShape(20.dp)) {
                            Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Icon(AzimIkon.Alev, null, tint = Color(0xFFE5C49A))
                                Column {
                                    Text(cevir(dil, "Gün içinde yanında", "With you throughout the day"), color = Color.White, fontWeight = FontWeight.Medium)
                                    Text(cevir(dil, "Seçtiğin konular, senin belirlediğin saatler.", "Your topics. Your schedule."), color = Color(0xFFD4DEE4), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    1 -> {
                        OnboardingBaslik(cevir(dil, "Sana ne ilham versin?", "What inspires you?"), cevir(dil, "Akışını ve bildirimlerini birlikte seçelim.", "Shape your feed and your reminders."))
                        Baslangic.konular.chunked(2).forEach { cift ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                cift.forEach { key ->
                                    val kat = Kategoriler.bul(key)!!
                                    val aktif = key in secili
                                    Box(Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).border(if (aktif) 2.dp else 0.dp, if (aktif) Renk.accent else Color.Transparent, RoundedCornerShape(22.dp)).toggleable(aktif, role = Role.Checkbox) { secili = Baslangic.secimiDegistir(secili, key) }) {
                                        AtmosferResmi(Atmosfer.grup(kat.grup), Modifier.matchParentSize(), .28f)
                                        Column(Modifier.fillMaxWidth().heightIn(min = 132.dp).padding(14.dp)) {
                                            if (aktif) Icon(AzimIkon.Tik, cevir(dil, "Seçili", "Selected"), Modifier.size(20.dp).align(Alignment.End), tint = Color.White)
                                            else Box(Modifier.size(20.dp).align(Alignment.End).border(1.5.dp, Color.White, androidx.compose.foundation.shape.CircleShape))
                                            Spacer(Modifier.height(42.dp))
                                            Text(baslangicAdi(key, dil), color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                        }
                                    }
                                }
                            }
                        }
                        Text(if (secili.isEmpty()) cevir(dil, "En az bir konu seç.", "Choose at least one topic.") else cevir(dil, "${secili.size} konu seçili · Sonra değiştirebilirsin", "${secili.size} topics selected · Change them anytime"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    }
                    2 -> {
                        OnboardingBaslik(cevir(dil, "Günün ritmini sen seç.", "Set your daily rhythm."), cevir(dil, "Bir sabah dürtüsü, öğlen bir nefes, akşam yeni bir bakış.", "A morning spark, a midday pause, an evening perspective."))
                        BildirimOnizlemesi(dil)
                        Spacer(Modifier.height(4.dp))
                        BildirimPlani(adet, bas, bit, secili.size, { adet = it }, { b, s -> bas = b; bit = s }, modifier = Modifier)
                    }
                    3 -> {
                        OnboardingBaslik(cevir(dil, "İlhamı kaçırma.", "Let inspiration find you."), cevir(dil, "İzni aç, sözleri bildirimde rahatça oku.", "Enable notifications and make room for the whole quote."))
                        BildirimKurulumu(dil, bildirimIzni, izinIste)
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                hata?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = { if (adim < 3) adim++ else bitir(secili, adet, bas, bit, true) },
                    enabled = !kaydediliyor && (adim != 1 || secili.isNotEmpty()) && (adim != 3 || bildirimIzni),
                    shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    colors = if (adim == 0) ButtonDefaults.buttonColors(containerColor = Color(0xFFE5C49A), contentColor = Color(0xFF14212B)) else ButtonDefaults.buttonColors(),
                ) { Text(if (kaydediliyor) cevir(dil, "Kaydediliyor…", "Saving…") else when (adim) { 0 -> cevir(dil, "Kendi yolunu oluştur →", "Make it yours →"); 3 -> cevir(dil, "Ascend’e başla", "Start Ascend"); else -> cevir(dil, "Devam", "Continue") }) }
                if (adim == 3) TextButton(onClick = { bitir(secili, adet, bas, bit, false) }, enabled = !kaydediliyor, modifier = Modifier.heightIn(min = 48.dp)) { Text(cevir(dil, "Şimdilik bildirimsiz devam et", "Continue without reminders")) }
                if (adim == 0) Text(cevir(dil, "Hesap gerekmez · Kendi hızında", "No account needed · At your own pace"), Modifier.padding(top = 12.dp), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

fun baslangicAdi(key: String, dil: String) = when (key) {
    "motivasyon" -> cevir(dil, "Motivasyon", "Motivation")
    "ozsefkat" -> cevir(dil, "Olumlamalar", "Affirmations")
    "marcus" -> cevir(dil, "Felsefe", "Philosophy")
    "derin_odak" -> cevir(dil, "Odak", "Focus")
    "azim" -> cevir(dil, "Azim", "Persistence")
    else -> cevir(dil, "İç huzur", "Inner peace")
}

@Composable
private fun OnboardingBaslik(baslik: String, alt: String) {
    Text(baslik, color = Renk.metin, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.semantics { heading() })
    Text(alt, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun AnaDugme(metin: String, tikla: () -> Unit) {
    Button(onClick = tikla, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(18.dp)) { Text(metin) }
}
