package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KisiselPlanPaneli(
    profil: PersonalProfile?, secili: Set<String>, acik: Set<String>, dil: String,
    adet: Int, bas: Int, bit: Int, bildirimAcik: Boolean, kapat: () -> Unit,
    duzenle: () -> Unit, konular: () -> Unit, ayarlar: () -> Unit, proAc: () -> Unit,
) {
    val etkin = PersonalPlan.effectiveCategories(profil, secili, acik)
    // Catalog order remains stable when DataStore returns a differently ordered Set.
    val oncelikli = Kategoriler.tumAltlar.filter { it.anahtar in secili && it.anahtar in etkin }
    val yeniOneriler = profil?.let(PersonalPlan::recommendedCategories).orEmpty().filterNot { it in acik }.take(3)
    ModalBottomSheet(onDismissRequest = kapat, containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().testTag("personal-plan-panel").verticalScroll(rememberScrollState())
            .navigationBarsPadding().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EditoryalBaslik(cevir(dil, "SANA AİT BİR YÖN", "A DIRECTION OF YOUR OWN"),
                    cevir(dil, "Planım", "My plan"), modifier = Modifier.weight(1f))
                IconButton(onClick = kapat, modifier = Modifier.size(48.dp).testTag("plan-close")) {
                    Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), Modifier.size(22.dp), tint = Renk.metin)
                }
            }
            Text(if (profil == null) cevir(dil, "Birkaç yanıtla sana daha yakın bir başlangıç.", "A few answers for a start that feels like you.")
                else cevir(dil, "Seçimlerin yön verir. Planın değişebilir.", "Your choices guide the way. Your plan can change."),
                color = Renk.metinIkincil, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodyMedium)
            Box(Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp)) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    profil?.let {
                        PersonalPlan.summary(it, dil).forEach { line ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                                Box(Modifier.padding(top = 10.dp).width(20.dp).height(1.dp).background(Renk.accent))
                                Text(line, color = Renk.metin, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            }
                        }
                    } ?: Text(cevir(dil, "Neye ihtiyacın olduğunu birlikte bulalım.", "Let’s find what you need."),
                        color = Renk.metin, fontFamily = LoraSerif, fontSize = 22.sp, lineHeight = 30.sp)
                }
            }
            Button(onClick = duzenle, shape = RoundedCornerShape(50), modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("plan-edit")) {
                Text(if (profil == null) cevir(dil, "Kendi planımı oluştur", "Create my plan") else cevir(dil, "Yanıtlarımı düzenle", "Edit my answers"))
            }
            PlanBolumu("01", cevir(dil, "Bildirim karışımın", "Your reminder mix"))
            if (oncelikli.isEmpty()) {
                Text(cevir(dil, "Seçtiğin konular şu anki tercihlerinle örtüşmüyor. Yanıtlarını veya konularını düzenleyebilirsin.",
                    "Your selected topics do not match your current preferences. You can adjust your answers or topics."),
                    color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("plan-priorities-empty"))
            } else FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                oncelikli.forEach { kategori ->
                    Surface(color = Renk.zemin, border = BorderStroke(1.dp, Renk.kenarlik), shape = RoundedCornerShape(50)) {
                        Text(kategori.ad(dil), color = Renk.metin, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp))
                    }
                }
            }
            Text(cevir(dil, "${oncelikli.size} öncelikli konu · ${etkin.size} açık konu planına uygun",
                "${oncelikli.size} priority topics · ${etkin.size} unlocked topics fit your plan"),
                color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp))
            TextButton(onClick = konular, modifier = Modifier.heightIn(min = 48.dp).testTag("plan-categories"), contentPadding = PaddingValues(vertical = 10.dp)) {
                Text(cevir(dil, "Konuları gör ve değiştir", "View and change topics"), modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Icon(AzimIkon.Ileri, null, Modifier.size(18.dp))
            }
            PlanBolumu("02", cevir(dil, "Günün ritmi", "Your daily rhythm"))
            Surface(onClick = ayarlar, color = Renk.zemin, border = BorderStroke(1.dp, Renk.kenarlik),
                shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().testTag("plan-settings")) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(if (bildirimAcik) cevir(dil, "Günde $adet küçük hatırlatma", "$adet small reminders a day") else cevir(dil, "Bildirimler kapalı", "Reminders are off"),
                            color = Renk.metin, style = MaterialTheme.typography.bodyMedium)
                        Text("%02d:00 – %02d:00".format(bas, bit % 24), color = Renk.metin, fontFamily = LoraSerif, fontSize = 24.sp, lineHeight = 31.sp)
                    }
                    Icon(AzimIkon.Ileri, null, Modifier.size(20.dp), tint = Renk.metin)
                }
            }
            if (yeniOneriler.isNotEmpty()) {
                PlanBolumu("03", cevir(dil, "Yoluna ekleyebilirsin", "More for your path"))
                Text(yeniOneriler.mapNotNull { Kategoriler.bul(it)?.ad(dil) }.joinToString(" · "),
                    color = Renk.metin, fontFamily = LoraSerif, fontSize = 20.sp, lineHeight = 28.sp)
                Text(cevir(dil, "Bu konular henüz kilitli. Keşfet'te tek tek açabilir veya Pro demosunu deneyebilirsin.",
                    "These topics are locked. Unlock them individually in Discover, or try the Pro demo."),
                    color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp))
                TextButton(onClick = proAc, modifier = Modifier.heightIn(min = 48.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
                    Text(cevir(dil, "Pro demosunu incele", "Explore Pro demo"))
                }
            }
            Text(cevir(dil, "Kendi hızında, her gün yeniden.", "At your pace, a new start each day."), color = Renk.metinIkincil,
                fontFamily = LoraSerif, fontStyle = FontStyle.Italic, fontSize = 16.sp, modifier = Modifier.padding(top = 24.dp, bottom = 28.dp))
        }
    }
}

@Composable
private fun PlanBolumu(sira: String, baslik: String) {
    HorizontalDivider(Modifier.padding(top = 24.dp, bottom = 20.dp), color = Renk.kenarlik)
    Row(Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(sira, color = Renk.metinIkincil, style = MaterialTheme.typography.labelMedium)
        Text(baslik, color = Renk.metin, fontFamily = LoraSerif, fontSize = 22.sp, lineHeight = 30.sp, modifier = Modifier.weight(1f))
    }
}
