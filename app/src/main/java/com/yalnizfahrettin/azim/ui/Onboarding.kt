package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.notif.BildirimZamanlari
import com.yalnizfahrettin.azim.notif.TeslimatYardimi
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** A skippable conversation; each explicit answer participates in the real local plan. */
@Composable
fun Onboarding(
    dil: String, kaydediliyor: Boolean = false, hata: String? = null,
    bildirimIzni: Boolean = false, izinIste: () -> Unit = {},
    initialDraft: PersonalProfile = PersonalProfile(), draftChanged: (PersonalProfile) -> Unit = {},
    finishProfile: ((PersonalProfile, Boolean) -> Unit)? = null,
    editing: Boolean = false, onCancel: () -> Unit = {},
    previewAccess: Set<String> = Erisim.ucretsizKategoriler,
    bitir: (Set<String>, Int, Int, Int, Boolean) -> Unit,
) {
    var encoded by rememberSaveable { mutableStateOf(initialDraft.encode()) }
    val profile = remember(encoded) { PersonalProfile.decode(encoded) }
    val step = profile.step
    var namePending by remember { mutableStateOf(false) }
    var hourDialog by rememberSaveable { mutableIntStateOf(0) }
    var permissionHelp by rememberSaveable { mutableStateOf(false) }
    fun update(value: PersonalProfile, debounce: Boolean = false) {
        encoded = value.encode(); namePending = debounce
        if (!debounce) draftChanged(value)
    }
    LaunchedEffect(encoded, namePending) {
        if (namePending) { delay(220); draftChanged(profile); namePending = false }
    }
    fun move(next: Int) = update(profile.copy(step = next.coerceIn(0, PersonalPlan.LAST_STEP)))
    fun finish(reminders: Boolean) {
        if (finishProfile != null) finishProfile(profile, reminders)
        else bitir(PersonalPlan.initialCategories(profile, previewAccess), profile.dailyCount, profile.startHour, profile.endHour, reminders)
    }
    BackHandler(step > 0 || editing || kaydediliyor) {
        if (!kaydediliyor) { if (step > 0) move(step - 1) else onCancel() }
    }
    val question = PersonalPlan.questions.getOrNull(step - 2)
    Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().imePadding().testTag("onboarding-root")) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            if (step > 0 || editing) IconButton(onClick = { if (step > 0) move(step - 1) else onCancel() }, enabled = !kaydediliyor,
                modifier = Modifier.testTag("onboarding-back")) { Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), tint = Renk.metin) }
            else Spacer(Modifier.width(12.dp))
            Text("ascend", style = MaterialTheme.typography.titleMedium, letterSpacing = 1.sp, color = Renk.metin, modifier = Modifier.weight(1f))
            Text("${step + 1} / ${PersonalPlan.LAST_STEP + 1}", style = MaterialTheme.typography.labelSmall, color = Renk.metinIkincil)
            if (step == 1 || question != null) TextButton(enabled = !kaydediliyor, onClick = {
                update((if (step == 1) profile.copy(name = "") else profile.skip(question!!.id)).copy(step = step + 1))
            }, modifier = Modifier.testTag("onboarding-skip")) { Text(cevir(dil, "Atla", "Skip")) }
            else Spacer(Modifier.width(12.dp))
        }
        // Material's indicator expands its screen-reader bounds beyond its painted height.
        // Keep a distinct physical row so layout tests measure the visible divider, while
        // retaining the indicator's accessible progress semantics unchanged.
        Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(2.dp).testTag("onboarding-progress")) {
            LinearProgressIndicator(progress = { (step + 1f) / (PersonalPlan.LAST_STEP + 1) },
                modifier = Modifier.fillMaxSize(), color = Renk.metin, trackColor = Renk.kenarlik)
        }
        AnimatedContent(step, transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(100)) },
            modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds(), label = "onboarding-step") { shownStep ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("onboarding-scroll")
                .padding(horizontal = 28.dp, vertical = 28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (shownStep) {
                    0 -> PlanWelcome(dil)
                    1 -> {
                        PlanTitle(cevir(dil, "Sana nasıl hitap edelim?", "What should we call you?"),
                            cevir(dil, "İstersen yalnızca adın. Karşılamanda kullanacağız.", "Just a first name, if you like. We will use it to greet you."))
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = profile.name, onValueChange = { update(profile.copy(name = it.take(40)), debounce = true) },
                            singleLine = true, label = { Text(cevir(dil, "Adın (isteğe bağlı)", "Name (optional)")) },
                            shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().testTag("onboarding-name"))
                    }
                    in 2..14 -> {
                        val q = PersonalPlan.questions[shownStep - 2]
                        PlanTitle(q.title(dil), q.hint(dil), Modifier.testTag("onboarding-question-${q.id}"))
                        Spacer(Modifier.height(8.dp))
                        q.options.forEach { option ->
                            PlanChoice(option.label(dil), option.id in profile.answer(q.id), "onboarding-option-${q.id}-${option.id}",
                                role = if (q.multiple) Role.Checkbox else Role.RadioButton) { update(profile.choose(q.id, option.id, q.multiple)) }
                        }
                        if (q.multiple) Text(cevir(dil, "Birden fazla seçebilirsin", "You can choose more than one"), color = Renk.metinIkincil, style = MaterialTheme.typography.labelSmall)
                    }
                    15 -> PlanFrequency(profile, dil) { update(profile.copy(dailyCount = it)) }
                    16 -> PlanHours(profile, dil) { hourDialog = it }
                    17 -> PlanPreview(profile, dil, previewAccess)
                    18 -> PlanAccess(dil)
                    19 -> PlanPermission(profile, dil, bildirimIzni, previewAccess) { permissionHelp = true }
                }
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            hata?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Button(onClick = {
                if (step < PersonalPlan.LAST_STEP) move(step + 1)
                else if (bildirimIzni) finish(true) else izinIste()
            }, enabled = !kaydediliyor, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("onboarding-next")) {
                Text(if (kaydediliyor) cevir(dil, "Kaydediliyor…", "Saving…") else when (step) {
                    0 -> cevir(dil, "Yolunu oluştur", "Build your path")
                    17 -> cevir(dil, "Bu planla devam et", "Continue with this plan")
                    19 -> if (bildirimIzni) cevir(dil, if (editing) "Planımı güncelle" else "Ascend’e başla", if (editing) "Update my plan" else "Start Ascend") else cevir(dil, "Bildirimlere izin ver", "Allow notifications")
                    else -> cevir(dil, "Devam", "Continue")
                })
            }
            if (step == 0) TextButton(onClick = { move(17) }, enabled = !kaydediliyor, modifier = Modifier.testTag("onboarding-quick-start")) { Text(cevir(dil, "Hızlı başla, sonra kişiselleştir", "Start now, personalize later")) }
            if (step == PersonalPlan.LAST_STEP) TextButton(onClick = { finish(false) }, enabled = !kaydediliyor, modifier = Modifier.testTag("onboarding-finish-without-reminders")) {
                Text(cevir(dil, "Şimdilik bildirimsiz devam et", "Continue without reminders"))
            }
        }
    }
    if (hourDialog != 0) PlanHourDialog(profile, hourDialog == 1, dil, { hourDialog = 0 }) { hour ->
        update(if (hourDialog == 1) profile.copy(startHour = hour) else profile.copy(endHour = hour)); hourDialog = 0
    }
    if (permissionHelp) PlanPermissionHelp(dil) { permissionHelp = false }
}

@Composable
private fun ColumnScope.PlanWelcome(dil: String) {
    Spacer(Modifier.height(18.dp))
    Icon(AzimIkon.Yukselis, null, Modifier.size(104.dp).align(Alignment.CenterHorizontally), tint = Renk.metin)
    Spacer(Modifier.height(12.dp))
    PlanTitle(cevir(dil, "Kendi hızında.\nBir adım yukarı.", "At your pace.\nOne step higher."),
        cevir(dil, "Bazen bir olumlama. Bazen yeni bir bakış. Sana eşlik edecek sözleri birlikte bulalım.", "Sometimes an affirmation. Sometimes a new perspective. Let’s find the words that meet you where you are."))
    Spacer(Modifier.height(12.dp))
    Text(cevir(dil, "Kısa sorular · Sana göre bir plan", "Small questions · A plan that fits you"), color = Renk.metin, style = MaterialTheme.typography.labelLarge)
    Text(cevir(dil, "Her soruyu atlayabilirsin. Hesap gerekmez; yanıtların uygulamada saklanır.", "Skip any question. No account needed; your answers are saved in the app."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun PlanTitle(title: String, description: String, modifier: Modifier = Modifier) {
    Text(title, modifier.semantics { heading() }, color = Renk.metin, fontSize = 30.sp, lineHeight = 37.sp, fontWeight = FontWeight.SemiBold)
    Text(description, color = Renk.metinIkincil, fontSize = 15.sp, lineHeight = 23.sp)
}

@Composable
private fun PlanChoice(label: String, selected: Boolean, tag: String, role: Role = Role.RadioButton, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(if (selected) Renk.yuzeyYuksek else Renk.zemin)
        .border(if (selected) 1.5.dp else 1.dp, if (selected) Renk.metin else Renk.kenarlik, RoundedCornerShape(18.dp))
        .selectable(selected, role = role, onClick = onClick).testTag(tag).padding(horizontal = 18.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), color = Renk.metin, fontSize = 16.sp, lineHeight = 22.sp)
        if (selected) Icon(AzimIkon.Tik, null, Modifier.size(21.dp), tint = Renk.metin)
        else Box(Modifier.size(20.dp).border(1.dp, Renk.metinIkincil, CircleShape))
    }
}

@Composable
private fun PlanFrequency(profile: PersonalProfile, dil: String, changed: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Gününe kaç kez eşlik edelim?", "How often should we check in?"),
        cevir(dil, "Az ya da çok, ritim senin. Bildirim iznini son adımda soracağız.", "A little or often, it is your rhythm. We will ask for notification permission at the end."))
    Spacer(Modifier.height(20.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = { changed(profile.dailyCount - 1) }, enabled = profile.dailyCount > 1,
            modifier = Modifier.size(54.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını azalt", "Fewer reminders") }) { Text("−", fontSize = 28.sp) }
        Column(Modifier.width(112.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${profile.dailyCount}", color = Renk.metin, fontSize = 56.sp, modifier = Modifier.testTag("reminder-count"))
            Text(cevir(dil, "kez / gün", "times / day"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
        OutlinedIconButton(onClick = { changed(profile.dailyCount + 1) }, enabled = profile.dailyCount < 7,
            modifier = Modifier.size(54.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını artır", "More reminders") }) { Text("+", fontSize = 28.sp) }
    }
    Spacer(Modifier.height(24.dp))
    Text(cevir(dil, "Başlangıç için 3 kısa mola öneriyoruz. İstediğin zaman değiştirebilir veya kapatabilirsin.", "Start with three small pauses. Change the rhythm or turn reminders off anytime."), color = Renk.metinIkincil, textAlign = TextAlign.Center)
}

private fun planHour(hour: Int) = "%02d:00".format(Locale.ROOT, hour % 24)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanHours(profile: PersonalProfile, dil: String, select: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Günün hangi saatlerinde?", "Which hours work for you?"),
        cevir(dil, "Seçtiğin aralığın dışında sessiz kalırız.", "We stay quiet outside your chosen window."))
    Spacer(Modifier.height(12.dp))
    listOf(Triple(1, cevir(dil, "Başlangıç", "From"), profile.startHour), Triple(2, cevir(dil, "Bitiş", "Until"), profile.endHour)).forEach { (id, label, hour) ->
        Surface(onClick = { select(id) }, shape = RoundedCornerShape(18.dp), color = Renk.yuzey,
            modifier = Modifier.fillMaxWidth().testTag(if (id == 1) "reminder-start" else "reminder-end")) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(label, Modifier.weight(1f), color = Renk.metinIkincil)
                Text(planHour(hour), color = Renk.metin, fontSize = 26.sp)
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    Text(cevir(dil, "Günün küçük durakları", "Your small pauses"), color = Renk.metin, style = MaterialTheme.typography.labelLarge)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.testTag("reminder-preview-times")) {
        BildirimZamanlari.hesapla(LocalDateTime.of(2000, 1, 1, 0, 0), profile.dailyCount, profile.startHour, profile.endHour).forEach {
            Text(it.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)), color = Renk.metin,
                modifier = Modifier.background(Renk.yuzey, RoundedCornerShape(12.dp)).padding(12.dp))
        }
    }
    Text(cevir(dil, "Yaklaşık saatler. Cihazın güç tasarrufu teslimatı geciktirebilir.", "Approximate times. Device power saving can delay delivery."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanPreview(profile: PersonalProfile, dil: String, access: Set<String>) {
    PlanTitle(cevir(dil, "İşte senin başlangıç çizgin.", "Your starting point."),
        cevir(dil, "Yanıtların bu planı oluşturdu. İstediğin zaman yeniden şekillendirebilirsin.", "Your answers shaped this plan. Change it whenever you need."))
    PersonalPlan.summary(profile, dil).dropLast(1).take(4).forEach { line ->
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.metin)
            Text(line, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
        }
    }
    val starters = PersonalPlan.initialCategories(profile, access)
    Text(cevir(dil, "Planındaki konular", "Topics in your plan"), style = MaterialTheme.typography.labelLarge, color = Renk.metin)
    Text(starters.joinToString(" · ") { Kategoriler.bul(it)?.ad(dil) ?: it }, color = Renk.metinIkincil)
    PersonalPlan.feed(profile, starters, access).firstOrNull()?.let { sample ->
        Surface(color = Renk.yuzey, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(cevir(dil, "İlk sözün", "Your first words"), color = Renk.metinIkincil, style = MaterialTheme.typography.labelSmall)
                Text(sample.metin(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 32.sp)
                Text(sample.sunumEtiketi(dil), color = Renk.metinIkincil, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    val extras = PersonalPlan.recommendedCategories(profile).filterNot { it in access }.take(3)
    if (extras.isNotEmpty()) Text(cevir(dil, "Sonra keşfedebilirsin: ", "Explore later: ") + extras.joinToString(" · ") { Kategoriler.bul(it)?.ad(dil) ?: it } +
        cevir(dil, ". Bu konular ayrıca açılır.", ". These topics unlock separately."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanAccess(dil: String) {
    PlanTitle(cevir(dil, "Burada yerin var.", "There is room for you here."), cevir(dil, "Başlamak için ödeme gerekmez.", "You do not need to pay to begin."))
    val lines = listOf(
        cevir(dil, "Ücretsiz bir başlangıç", "A free beginning") to cevir(dil, "6 tam kategori, günlük planın ve üç temel paylaşım arka planı.", "Six complete categories, your daily plan and three basic sharing backgrounds."),
        cevir(dil, "Yeni bir konu, sen istediğinde", "A new topic, when you choose") to cevir(dil, "Kategorileri tek tek açabilirsin. Bu testte reklam yerine açıkça belirtilen Google yönlendirmesi deneniyor.", "Unlock individual categories. In this test, a clearly marked Google visit stands in for an ad."),
        cevir(dil, "Pro ile tamamı", "Everything with Pro") to cevir(dil, "Tüm kategoriler ve gelişmiş paylaşım araçları. Bu sürümde yalnızca demo; ödeme veya abonelik yok.", "All categories and advanced sharing tools. Demo only in this version, with no payment or subscription."))
    lines.forEachIndexed { index, (title, body) ->
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            Text("0${index + 1}", color = Renk.metinIkincil, style = MaterialTheme.typography.labelLarge)
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(title, color = Renk.metin, style = MaterialTheme.typography.titleMedium)
                Text(body, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    Text(cevir(dil, "Pro'yu daha sonra Keşfet bölümünden deneyebilirsin.", "Try the Pro demo later from Explore."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanPermission(profile: PersonalProfile, dil: String, allowed: Boolean, access: Set<String>, help: () -> Unit) {
    PlanTitle(cevir(dil, "İyi bir söz seni bulsun.", "Let the right words find you."),
        cevir(dil, "Planından günde ${profile.dailyCount} kez, ${planHour(profile.startHour)}–${planHour(profile.endHour)} arasında.", "From your plan, ${profile.dailyCount} times a day between ${planHour(profile.startHour)} and ${planHour(profile.endHour)}."))
    val sample = PersonalPlan.feed(profile, PersonalPlan.initialCategories(profile, access), access).firstOrNull()
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("ascend · " + cevir(dil, "önizleme", "preview"), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
            Text(sample?.metin(dil).orEmpty(), color = Renk.metin, style = MaterialTheme.typography.bodyLarge)
            Text(cevir(dil, "Tamamını okumak için bildirimi genişlet.", "Expand the notification to read it in full."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
    if (allowed) Text(cevir(dil, "Bildirim iznin açık. Hazırsın.", "Notifications are allowed. You are ready."), color = Renk.metin, style = MaterialTheme.typography.labelLarge)
    Text(cevir(dil, "Bildirimler senin seçimin. İzin vermeden de planını kullanabilirsin.", "Reminders are your choice. Your plan also works without permission."), color = Renk.metinIkincil)
    TextButton(onClick = help, modifier = Modifier.heightIn(min = 48.dp)) { Text(cevir(dil, "Bildirim görünümü ve cihaz ayarları", "Notification appearance and device settings")) }
}

@Composable
private fun PlanHourDialog(profile: PersonalProfile, start: Boolean, dil: String, close: () -> Unit, select: (Int) -> Unit) {
    AlertDialog(onDismissRequest = close, title = { Text(if (start) cevir(dil, "Başlangıç saatini seç", "Choose start hour") else cevir(dil, "Bitiş saatini seç", "Choose end hour")) },
        text = { Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val hours = if (start) (0 until profile.endHour).toList() else (profile.startHour + 1..24).toList()
            hours.forEach { hour -> PlanChoice(planHour(hour) + if (hour == 24) cevir(dil, " · Gece yarısı", " · Midnight") else "",
                hour == if (start) profile.startHour else profile.endHour, "hour-choice-$hour") { select(hour) } }
        } }, confirmButton = { TextButton(onClick = close) { Text(cevir(dil, "Vazgeç", "Cancel")) } })
}

@Composable
private fun PlanPermissionHelp(dil: String, close: () -> Unit) {
    val ctx = LocalContext.current
    AlertDialog(onDismissRequest = close, title = { Text(cevir(dil, "Bildirim yardımcısı", "Notification help")) },
        text = { Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(cevir(dil, "Tam söz için bildirimi aşağı doğru genişlet. Kilit ekranı ve açılır pencere görünümü cihazının ayarlarına bağlıdır.", "Expand a notification to read the full quote. Lock screen and pop-up appearance depend on your device settings."))
            Text(cevir(dil, "Samsung'da: Ayarlar → Bildirimler → Ascend → Bildirim açılır pencere stili → Ayrıntılı. Önceki One UI sürümlerinde stil genel Bildirimler menüsündedir.", "On Samsung: Settings → Notifications → Ascend → Notification pop-up style → Detailed. Earlier One UI versions keep the style in the general Notifications menu."))
            Text(cevir(dil, "Menü adları değişebilir. Ascend bu görünüm ayarını kendiliğinden değiştiremez.", "Menu names can vary. Ascend cannot change this appearance setting automatically."), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { TeslimatYardimi.bildirimAyarlariniAc(ctx) }) { Text(cevir(dil, "Cihaz ayarlarını aç", "Open device settings")) }
        } }, confirmButton = { TextButton(onClick = close) { Text(cevir(dil, "Anladım", "Got it")) } })
}

fun baslangicAdi(key: String, dil: String) = Kategoriler.bul(key)?.ad(dil) ?: key

@Composable
fun AnaDugme(metin: String, tikla: () -> Unit) {
    Button(onClick = tikla, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(28.dp)) { Text(metin) }
}
