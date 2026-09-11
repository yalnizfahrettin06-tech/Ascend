package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KisiselPlanPaneli(
    profil: PersonalProfile?, secili: Set<String>, acik: Set<String>, dil: String,
    adet: Int, bas: Int, bit: Int, bildirimAcik: Boolean, kapat: () -> Unit,
    konular: () -> Unit,
    saveContent: suspend (Map<String, Set<String>>) -> Unit,
    saveRhythm: suspend (Int, Int, Int, Boolean) -> Unit,
    pausedUntil: Long = 0L, hiddenCount: Int = 0,
    pause: suspend (Boolean) -> Unit = {}, restoreHidden: suspend () -> Unit = {},
) {
    var page by rememberSaveable { mutableStateOf("main") }
    var draft by rememberSaveable { mutableStateOf((profil ?: PersonalProfile()).encode()) }
    val preferences = PersonalProfile.decode(draft)
    var count by rememberSaveable { mutableIntStateOf(adet) }
    var start by rememberSaveable { mutableIntStateOf(bas) }
    var end by rememberSaveable { mutableIntStateOf(bit) }
    var enabled by rememberSaveable { mutableStateOf(bildirimAcik) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(pausedUntil) {
        now = System.currentTimeMillis()
        if(pausedUntil > now) { kotlinx.coroutines.delay(pausedUntil - now); now = System.currentTimeMillis() }
    }
    val paused = pausedUntil > now
    fun action(block: suspend () -> Unit) {
        saving = true; error = null
        scope.launch {
            try { block() }
            catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { error = cevir(dil, "Kaydedilemedi. Yeniden dene.", "Could not save. Try again.") }
            finally { saving = false }
        }
    }
    fun back() { if (!saving) { if (page == "main") kapat() else { page = "main"; error = null } } }
    ModalBottomSheet(onDismissRequest = { if (!saving) kapat() }, containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        BackHandler(page != "main" || saving) { back() }
        Column(Modifier.fillMaxWidth().testTag("personal-plan-panel").navigationBarsPadding()
            .verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(when(page) { "content" -> cevir(dil, "İçerik tercihleri", "Content preferences")
                    "rhythm" -> cevir(dil, "Bildirim saatleri", "Reminder schedule")
                    else -> cevir(dil, "Planım", "My plan") }, fontFamily = LoraSerif, fontSize = 28.sp,
                    color = Renk.metin, modifier = Modifier.weight(1f).semantics { heading() })
                IconButton(onClick = { back() }, enabled = !saving, modifier = Modifier.testTag("plan-close")) {
                    Icon(if (page == "main") AzimIkon.Kapat else AzimIkon.Geri,
                        cevir(dil, if (page == "main") "Kapat" else "Geri", if (page == "main") "Close" else "Back"))
                }
            }
            if (page == "main") {
                Text(cevir(dil, "Yalnızca değiştirmek istediğin ayarı aç.", "Open just the setting you want to change."), color = Renk.metinIkincil)
                PlanSettingRow(cevir(dil, "Bildirim konuları", "Reminder topics"),
                    cevir(dil, "${secili.size} konu seçili", "${secili.size} topics selected"), "plan-categories", konular)
                PlanSettingRow(cevir(dil, "Saat ve sıklık", "Time and frequency"),
                    if (paused && bildirimAcik) cevir(dil, "Yarına kadar ara verildi", "Paused until tomorrow") else if (bildirimAcik) cevir(dil, "Günde $adet kez", "$adet times daily") + " · %02d:00–%02d:00".format(bas, bit % 24)
                    else cevir(dil, "Bildirimler kapalı", "Reminders off"), "plan-settings") {
                    count = adet; start = bas; end = bit; enabled = bildirimAcik; page = "rhythm"
                }
                PlanSettingRow(cevir(dil, "İçerik sınırları", "Content boundaries"),
                    cevir(dil, "Görmek istemediklerini düzenle", "Choose what to leave out"), "plan-edit") {
                    draft = (profil ?: PersonalProfile()).encode(); page = "content"
                }
            } else if (saving) {
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (page == "rhythm") {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(cevir(dil, "Hatırlatmalar", "Reminders"), Modifier.weight(1f))
                        Switch(enabled, { enabled = it }, modifier = Modifier.testTag("plan-reminders-toggle"))
                    }
                    if (bildirimAcik) {
                        if (paused) Text(cevir(dil, "Gece yarısından sonra normal saat planın yeniden başlayacak.",
                            "Your regular schedule resumes after midnight."), color = Renk.metinIkincil)
                        TextButton(onClick = { action { pause(!paused) } }, modifier = Modifier.testTag("plan-pause")) {
                            Text(cevir(dil, if(paused) "Arayı bitir" else "Bugün ara ver", if(paused) "Resume reminders" else "Pause for today"))
                        }
                    }
                    BildirimPlani(count, start, end, secili.size, { count = it }, { b, e -> start = b; end = e }, dil = dil)
                    Text(cevir(dil, "Saatlerini değiştirmek konularını değiştirmez. Bildirimleri açarken gerekirse cihaz izni istenir.",
                        "Changing the schedule keeps your topics. Enabling reminders may request device permission."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(cevir(dil, "Ana akış rastgele kalır. İçerik türü ve dışlamalar ana akışta ve bildirimlerde geçerlidir. Keşfet’te konuları kendin açıp okuyabilirsin.",
                        "Home stays random. Content type and exclusions apply to home and reminders. You can still browse topics yourself in Explore."), color = Renk.metinIkincil)
                    listOf("avoid", "spirituality", "format", "discovery").forEach { key ->
                        val q = PersonalPlan.questions.first { it.id == key }
                        HorizontalDivider(color = Renk.kenarlik)
                        Text(when(key) {
                            "avoid" -> cevir(dil, "Görmek istemediğin konular", "Topics to leave out")
                            "spirituality" -> cevir(dil, "Manevi içerikler", "Spiritual content")
                            "format" -> cevir(dil, "İçerik türü", "Content type")
                            else -> cevir(dil, "Bildirimlerde çeşitlilik", "Variety in reminders")
                        }, color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                        q.options.forEach { option ->
                            val selected = option.id in preferences.answer(key) || (preferences.answer(key).isEmpty() && option.id == when(key) {
                                "format" -> "mixed"; "spirituality" -> "no"; "discovery" -> "balanced"; else -> ""
                            })
                            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                .then(if(q.multiple) Modifier.toggleable(selected, role = Role.Checkbox) {
                                    draft = preferences.choose(key, option.id, true).encode()
                                } else Modifier.selectable(selected, role = Role.RadioButton) {
                                    draft = preferences.choose(key, option.id).encode()
                                }).testTag("content-$key-${option.id}"), verticalAlignment = Alignment.CenterVertically) {
                                if(q.multiple) Checkbox(selected, null) else RadioButton(selected, null)
                                Spacer(Modifier.width(8.dp))
                                Text(option.label(dil), color = Renk.metin, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    if(hiddenCount > 0) TextButton(onClick = { action { restoreHidden() } }, modifier = Modifier.testTag("restore-hidden")) {
                        Text(cevir(dil, "$hiddenCount gizlenen sözü geri getir", "Restore $hiddenCount hidden quotes"))
                    }
                    if (PersonalPlan.homeCategories(preferences, acik).isEmpty()) Text(cevir(dil,
                        "Bu tercihlere uygun açık konu yok. Kaydedersen ana akış boş kalır; tercihini değiştirebilir veya Keşfet’ten konu açabilirsin.",
                        "No unlocked topics match. Saving leaves home empty; change preferences or unlock a topic in Explore."), color = Renk.metinIkincil)
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }) }
                Button(onClick = {
                    saving = true; error = null
                    scope.launch {
                        try {
                            if(page == "content") saveContent(preferences.answers)
                            else saveRhythm(count, start, end, enabled)
                            page = "main"
                        } catch (_: Exception) { error = cevir(dil, "Kaydedilemedi. Yeniden dene.", "Could not save. Try again.") }
                        finally { saving = false }
                    }
                }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("plan-save"), shape = RoundedCornerShape(26.dp)) {
                    Text(cevir(dil, "Kaydet", "Save"))
                }
                TextButton(onClick = { back() }, modifier = Modifier.fillMaxWidth()) { Text(cevir(dil, "Vazgeç", "Cancel")) }
            }
        }
    }
}

@Composable
private fun PlanSettingRow(title: String, summary: String, tag: String, open: () -> Unit) {
    Surface(onClick = open, color = Renk.zemin, shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Renk.kenarlik), modifier = Modifier.fillMaxWidth().testTag(tag)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                Text(summary, color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            }
            Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
        }
    }
}
