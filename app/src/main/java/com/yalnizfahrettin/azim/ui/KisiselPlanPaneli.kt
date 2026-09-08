package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
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
    val oncelikli = secili.intersect(etkin)
    val yeniOneriler = profil?.let(PersonalPlan::recommendedCategories).orEmpty().filterNot { it in acik }.take(3)
    ModalBottomSheet(onDismissRequest = kapat, containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().testTag("personal-plan-panel").verticalScroll(rememberScrollState()).navigationBarsPadding().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(cevir(dil, "Kendi yolun.", "Your own path."), color = Renk.metin, style = MaterialTheme.typography.headlineLarge)
                    Text(if (profil == null) cevir(dil, "Birkaç yanıtla sana daha yakın bir başlangıç.", "A few answers for a start that feels like you.")
                        else cevir(dil, "Seçimlerin yön verir. Planın değişebilir.", "Your choices guide the way. Your plan can change."),
                        color = Renk.metinIkincil, modifier = Modifier.padding(top = 8.dp))
                }
                IconButton(onClick = kapat, modifier = Modifier.testTag("plan-close")) {
                    Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), tint = Renk.metin)
                }
            }
            Spacer(Modifier.height(24.dp))
            profil?.let {
                PersonalPlan.summary(it, dil).forEach { line ->
                    Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("—", color = Renk.metinIkincil)
                        Text(line, color = Renk.metin, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = duzenle, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("plan-edit")) {
                Text(if (profil == null) cevir(dil, "Kendi planımı oluştur", "Create my plan") else cevir(dil, "Yanıtlarımı düzenle", "Edit my answers"))
            }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Renk.kenarlik)
            Text(cevir(dil, "Bildirim karışımın", "Your reminder mix"), color = Renk.metin, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                oncelikli.forEach { key ->
                    Surface(color = Renk.yuzeyYuksek, shape = RoundedCornerShape(10.dp)) {
                        Text(Kategoriler.bul(key)?.ad(dil).orEmpty(), color = Renk.metin, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }
                }
            }
            Text(cevir(dil, "${oncelikli.size} öncelikli konu · ${etkin.size} açık konu planına uygun",
                "${oncelikli.size} priority topics · ${etkin.size} unlocked topics fit your plan"),
                color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 12.dp))
            TextButton(onClick = konular, modifier = Modifier.testTag("plan-categories")) { Text(cevir(dil, "Konuları gör ve değiştir", "View and change topics")) }
            Surface(onClick = ayarlar, color = Renk.yuzeyYuksek, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().testTag("plan-settings")) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(if (bildirimAcik) cevir(dil, "Günde $adet küçük hatırlatma", "$adet small reminders a day") else cevir(dil, "Bildirimler kapalı", "Reminders are off"), color = Renk.metin)
                        Text("%02d:00 – %02d:00".format(bas, bit % 24), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("↗", color = Renk.metin)
                }
            }
            if (yeniOneriler.isNotEmpty()) {
                Text(cevir(dil, "Yoluna ekleyebilirsin", "More for your path"), color = Renk.metin, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
                Text(yeniOneriler.mapNotNull { Kategoriler.bul(it)?.ad(dil) }.joinToString(" · "), color = Renk.metinIkincil, modifier = Modifier.padding(vertical = 10.dp))
                Text(cevir(dil, "Bu konular henüz kilitli. Keşfet'te tek tek açabilir veya Pro demosunu deneyebilirsin.", "These topics are locked. Unlock them individually in Discover, or try the Pro demo."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = proAc) { Text(cevir(dil, "Pro demosunu incele", "Explore Pro demo")) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
