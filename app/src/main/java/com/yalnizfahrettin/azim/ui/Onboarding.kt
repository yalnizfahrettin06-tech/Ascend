package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current
    val largeText = LocalDensity.current.fontScale > 1.35f
    fun update(value: PersonalProfile, debounce: Boolean = false) {
        encoded = value.encode(); namePending = debounce
        if (!debounce) draftChanged(value)
    }
    LaunchedEffect(encoded, namePending) {
        if (namePending) { delay(220); draftChanged(profile); namePending = false }
    }
    fun move(next: Int) {
        focus.clearFocus()
        keyboard?.hide()
        update(profile.copy(step = next.coerceIn(0, PersonalPlan.LAST_STEP)))
    }
    fun finish(reminders: Boolean) {
        if (finishProfile != null) finishProfile(profile, reminders)
        else bitir(PersonalPlan.initialCategories(profile, previewAccess), profile.dailyCount, profile.startHour, profile.endHour, reminders)
    }
    BackHandler(step > 0 || editing || kaydediliyor) {
        if (!kaydediliyor) { if (step > 0) move(step - 1) else onCancel() }
    }
    val question = PersonalPlan.questions.getOrNull(step - 2)
    Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().imePadding().testTag("onboarding-root")) {
        Row(Modifier.fillMaxWidth().background(Renk.marka).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (step > 0 || editing) IconButton(onClick = { if (step > 0) move(step - 1) else onCancel() }, enabled = !kaydediliyor,
                modifier = Modifier.testTag("onboarding-back")) { Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), tint = Renk.markaUstu) }
            else Spacer(Modifier.width(12.dp))
            Box(Modifier.weight(1f)) {
                if (largeText) Icon(AzimIkon.Yukselis, "Ascend", Modifier.size(24.dp), tint = Renk.markaUstu)
                else Text("ascend", fontFamily = LoraSerif, fontSize = 24.sp, fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.5).sp, color = Renk.markaUstu)
            }
            Text("${step + 1} / ${PersonalPlan.LAST_STEP + 1}", style = MaterialTheme.typography.labelSmall, color = Renk.markaUstu)
            if (step == 1 || question != null) TextButton(enabled = !kaydediliyor, onClick = {
                focus.clearFocus(); keyboard?.hide()
                update((if (step == 1) profile.copy(name = "") else profile.skip(question!!.id)).copy(step = step + 1))
            }, modifier = Modifier.testTag("onboarding-skip")) { Text(cevir(dil, "Atla", "Skip"), color = Renk.markaUstu) }
            else Spacer(Modifier.width(12.dp))
        }
        // Material's indicator expands its screen-reader bounds beyond its painted height.
        // Keep a distinct physical row so layout tests measure the visible divider, while
        // retaining the indicator's accessible progress semantics unchanged.
        Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(2.dp).testTag("onboarding-progress")) {
            LinearProgressIndicator(progress = { (step + 1f) / (PersonalPlan.LAST_STEP + 1) },
                modifier = Modifier.fillMaxSize(), color = Renk.accent, trackColor = Renk.kenarlik)
        }
        AnimatedContent(step, transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(100)) },
            modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds(), label = "onboarding-step") { shownStep ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("onboarding-scroll")
                .padding(horizontal = 24.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PlanSection(shownStep, dil)
                when (shownStep) {
                    0 -> PlanWelcome(dil)
                    1 -> {
                        PlanTitle(cevir(dil, "Sana nasıl hitap edelim?", "What should we call you?"),
                            cevir(dil, "İstersen yalnızca adın. Karşılamanda kullanacağız.", "Just a first name, if you like. We will use it to greet you."))
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = profile.name, onValueChange = { update(profile.copy(name = it.take(40)), debounce = true) }, enabled = !kaydediliyor,
                            singleLine = true, label = { Text(cevir(dil, "Adın (isteğe bağlı)", "Name (optional)")) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { move(2) }),
                            shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().testTag("onboarding-name"))
                    }
                    in 2..14 -> {
                        val q = PersonalPlan.questions[shownStep - 2]
                        PlanTitle(q.title(dil), q.hint(dil), Modifier.testTag("onboarding-question-${q.id}"))
                        if (q.multiple) Text(cevir(dil, "${profile.answer(q.id).size} seçili · Birden fazla seçebilirsin", "${profile.answer(q.id).size} selected · Choose more than one"),
                            color = Renk.metinIkincil, style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.testTag("onboarding-selection-count").semantics { liveRegion = LiveRegionMode.Polite })
                        Column(if (q.multiple) Modifier else Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            q.options.forEach { option ->
                                PlanChoice(option.label(dil), option.id in profile.answer(q.id), "onboarding-option-${q.id}-${option.id}",
                                    role = if (q.multiple) Role.Checkbox else Role.RadioButton) { update(profile.choose(q.id, option.id, q.multiple)) }
                            }
                        }
                    }
                    15 -> PlanFrequency(profile, dil) { update(profile.copy(dailyCount = it)) }
                    16 -> PlanHours(profile, dil) { hourDialog = it }
                    17 -> PlanPreview(profile, dil, previewAccess)
                    18 -> PlanAccess(dil)
                    19 -> PlanPermission(profile, dil, bildirimIzni, previewAccess) { permissionHelp = true }
                }
            }
        }
        HorizontalDivider(color = Renk.kenarlik.copy(alpha = .65f))
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            hata?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp).semantics { liveRegion = LiveRegionMode.Polite }) }
            Button(onClick = {
                if (step < PersonalPlan.LAST_STEP) move(step + 1)
                else if (bildirimIzni) finish(true) else izinIste()
            }, enabled = !kaydediliyor, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("onboarding-next")) {
                Text(if (kaydediliyor) cevir(dil, "Kaydediliyor…", "Saving…") else when (step) {
                    0 -> cevir(dil, "Yolunu oluştur", "Build your path")
                    17 -> cevir(dil, "Bu planla devam et", "Continue with this plan")
                    19 -> if (bildirimIzni) cevir(dil, if (editing) "Planımı güncelle" else "Ascend’e başla", if (editing) "Update my plan" else "Start Ascend") else cevir(dil, "Bildirimlere izin ver", "Allow notifications")
                    else -> cevir(dil, "Devam", "Continue")
                }, textAlign = TextAlign.Center)
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
private fun PlanSection(step: Int, dil: String) {
    val chapter = when (step) {
        0 -> cevir(dil, "YENİ BİR BAŞLANGIÇ", "A NEW BEGINNING")
        in 1..7 -> cevir(dil, "01 · SENİ TANIYALIM", "01 · GETTING TO KNOW YOU")
        in 8..14 -> cevir(dil, "02 · SANA YAKIN OLAN", "02 · WHAT MATTERS TO YOU")
        15, 16 -> cevir(dil, "03 · SENİN RİTMİN", "03 · YOUR RHYTHM")
        else -> cevir(dil, "04 · SENİN YOLUN", "04 · YOUR PATH")
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.width(24.dp).height(1.dp).background(Renk.accent))
        Text(chapter, color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.4.sp,
            modifier = Modifier.weight(1f).testTag("onboarding-chapter"))
    }
}

@Composable
private fun ColumnScope.PlanWelcome(dil: String) {
    Box(Modifier.fillMaxWidth()) {
        KlasikGorsel(KlasikMotif.COLUMN, Modifier.matchParentSize().offset(x = 100.dp, y = (-40).dp), opacity = .35f)
        Column(Modifier.padding(vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PlanTitle(cevir(dil, "Kendi hızında. Bir adım yukarı.", "At your pace. One step higher."),
                cevir(dil, "Bazen bir olumlama. Bazen yeni bir bakış. Sana eşlik edecek sözleri birlikte bulalım.",
                    "Sometimes an affirmation. Sometimes a new perspective. Let’s find the words that meet you where you are."))
        }
    }
    HorizontalDivider(color = Renk.kenarlik)
    Text(cevir(dil, "20 küçük adım · Sana göre bir başlangıç", "20 small steps · A beginning that fits you"),
        color = Renk.accent, style = MaterialTheme.typography.bodyMedium)
    Text(cevir(dil, "Her soruyu atlayabilirsin. Hesap gerekmez; yanıtların uygulamada saklanır.",
        "Skip any question. No account needed; your answers are saved in the app."),
        color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanTitle(title: String, description: String, modifier: Modifier = Modifier) {
    Text(title, modifier.semantics { heading() }, color = Renk.metin, fontFamily = LoraSerif,
        fontSize = 32.sp, lineHeight = 39.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.4).sp)
    Text(description, color = Renk.metinIkincil, fontSize = 14.sp, lineHeight = 22.sp)
}

@Composable
private fun PlanChoice(label: String, selected: Boolean, tag: String, role: Role = Role.RadioButton, onClick: () -> Unit) {
    val fill by animateColorAsState(if (selected) Renk.accentZemin else Renk.zemin, tween(140), label = "choice-fill")
    val markShape = if (role == Role.Checkbox) RoundedCornerShape(5.dp) else CircleShape
    val interaction = if (role == Role.Checkbox) Modifier.toggleable(selected, role = role, onValueChange = { onClick() })
        else Modifier.selectable(selected, role = role, onClick = onClick)
    Row(Modifier.fillMaxWidth().heightIn(min = 60.dp).clip(RoundedCornerShape(14.dp)).background(fill)
        .border(if (selected) 1.5.dp else 1.dp, if (selected) Renk.accent else Renk.kenarlik, RoundedCornerShape(14.dp))
        .then(interaction).testTag(tag).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), color = Renk.metin, fontSize = 16.sp, lineHeight = 23.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        Box(Modifier.size(22.dp).clip(markShape).then(if (selected) Modifier.background(Renk.accent)
            else Modifier.border(1.dp, Renk.metinIkincil, markShape)), contentAlignment = Alignment.Center) {
            if (selected) Icon(AzimIkon.Tik, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun PlanFrequency(profile: PersonalProfile, dil: String, changed: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Gününe kaç kez eşlik edelim?", "How often should we check in?"),
        cevir(dil, "Az ya da çok, ritim senin. Bildirim iznini son adımda soracağız.", "A little or often, it is your rhythm. We will ask for notification permission at the end."))
    Spacer(Modifier.height(12.dp))
    Surface(color = Renk.yuzey, border = BorderStroke(1.dp, Renk.kenarlik), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 26.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(cevir(dil, "GÜNLÜK RİTMİN", "YOUR DAILY RHYTHM"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(onClick = { changed(profile.dailyCount - 1) }, enabled = profile.dailyCount > 1,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını azalt", "Fewer reminders") }) { Text("−", fontSize = 28.sp) }
                Text("${profile.dailyCount}", color = Renk.metin, fontFamily = LoraSerif, fontSize = 64.sp,
                    modifier = Modifier.testTag("reminder-count").semantics { liveRegion = LiveRegionMode.Polite })
                OutlinedIconButton(onClick = { changed(profile.dailyCount + 1) }, enabled = profile.dailyCount < 7,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını artır", "More reminders") }) { Text("+", fontSize = 28.sp) }
            }
            Text(cevir(dil, "küçük durak / gün", "small pauses / day"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
    Text(cevir(dil, "Başlangıç için 3 kısa mola öneriyoruz. İstediğin zaman değiştirebilir veya kapatabilirsin.", "Start with three small pauses. Change the rhythm or turn reminders off anytime."),
        color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
}

private fun planHour(hour: Int) = "%02d:00".format(Locale.ROOT, hour % 24)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanHours(profile: PersonalProfile, dil: String, select: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Günün hangi saatlerinde?", "Which hours work for you?"),
        cevir(dil, "Seçtiğin aralığın dışında sessiz kalırız.", "We stay quiet outside your chosen window."))
    Spacer(Modifier.height(12.dp))
    listOf(Triple(1, cevir(dil, "Başlangıç", "From"), profile.startHour), Triple(2, cevir(dil, "Bitiş", "Until"), profile.endHour)).forEach { (id, label, hour) ->
        Surface(onClick = { select(id) }, shape = RoundedCornerShape(14.dp), color = Renk.zemin, border = BorderStroke(1.dp, Renk.kenarlik),
            modifier = Modifier.fillMaxWidth().testTag(if (id == 1) "reminder-start" else "reminder-end")) {
            Row(Modifier.padding(horizontal = 18.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(label, Modifier.weight(1f), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                Text(planHour(hour), color = Renk.metin, fontFamily = LoraSerif, fontSize = 28.sp)
                Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    HorizontalDivider(color = Renk.kenarlik)
    Text(cevir(dil, "Günün küçük durakları", "Your small pauses"), color = Renk.metin, fontFamily = LoraSerif, fontSize = 22.sp)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.testTag("reminder-preview-times")) {
        BildirimZamanlari.hesapla(LocalDateTime.of(2000, 1, 1, 0, 0), profile.dailyCount, profile.startHour, profile.endHour).forEach {
            Text(it.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)), color = Renk.metin,
                modifier = Modifier.background(Renk.yuzey, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 9.dp))
        }
    }
    Text(cevir(dil, "Yaklaşık saatler. Cihazın güç tasarrufu teslimatı geciktirebilir.", "Approximate times. Device power saving can delay delivery."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanPreview(profile: PersonalProfile, dil: String, access: Set<String>) {
    PlanTitle(cevir(dil, "İşte senin başlangıç çizgin.", "Your starting point."),
        cevir(dil, "Başlangıç tercihlerin hazır. Sonraki iki adımda erişim seçeneklerini ve bildirim iznini göreceksin.", "Your starting preferences are ready. Next, review access options and notification permission."))
    PersonalPlan.summary(profile, dil).dropLast(1).take(4).forEachIndexed { index, line ->
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Text("0${index + 1}", color = Renk.accent, fontFamily = LoraSerif, fontSize = 17.sp)
            Text(line, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        }
    }
    val starters = PersonalPlan.initialCategories(profile, access)
    HorizontalDivider(color = Renk.kenarlik)
    Text(cevir(dil, "Planındaki konular", "Topics in your plan"), fontFamily = LoraSerif, fontSize = 22.sp, color = Renk.metin)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("onboarding-plan-topics")) {
        starters.forEach { key ->
            Text(Kategoriler.bul(key)?.ad(dil) ?: key, color = Renk.accent, fontSize = 13.sp, lineHeight = 20.sp,
                modifier = Modifier.background(Renk.accentZemin, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp))
        }
    }
    PersonalPlan.feed(profile, starters, access).firstOrNull()?.let { sample ->
        Surface(color = Renk.yuzey, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Renk.kenarlik)) {
            Box(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(cevir(dil, "İLK SÖZÜN", "YOUR FIRST WORDS"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.5.sp)
                    Text(sample.metin(dil), color = Renk.metin, fontFamily = LoraSerif, fontSize = 25.sp, lineHeight = 34.sp)
                    Box(Modifier.width(28.dp).height(1.dp).background(Renk.accent))
                    Text(sample.sunumEtiketi(dil), color = Renk.metinIkincil, style = MaterialTheme.typography.labelSmall, modifier = Modifier.testTag("onboarding-preview-source"))
                }
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
            Text("0${index + 1}", color = Renk.accent, fontFamily = LoraSerif, fontSize = 23.sp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(title, color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 29.sp)
                Text(body, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (index < lines.lastIndex) HorizontalDivider(color = Renk.kenarlik)
    }
    Text(cevir(dil, "Pro demosuna daha sonra kilitli bir konunun içinden ulaşabilirsin.", "Find the Pro demo later inside any locked topic."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanPermission(profile: PersonalProfile, dil: String, allowed: Boolean, access: Set<String>, help: () -> Unit) {
    PlanTitle(cevir(dil, "İyi bir söz seni bulsun.", "Let the right words find you."),
        cevir(dil, "Planından günde ${profile.dailyCount} kez, ${planHour(profile.startHour)}–${planHour(profile.endHour)} arasında.", "From your plan, ${profile.dailyCount} times a day between ${planHour(profile.startHour)} and ${planHour(profile.endHour)}."))
    val sample = PersonalPlan.feed(profile, PersonalPlan.initialCategories(profile, access), access).firstOrNull()
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Renk.kenarlik)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(AzimIkon.Yukselis, null, Modifier.size(20.dp), tint = Renk.metin)
                Text("ascend", fontFamily = LoraSerif, fontSize = 22.sp, color = Renk.metin, modifier = Modifier.weight(1f))
                Text(cevir(dil, "önizleme", "preview"), style = MaterialTheme.typography.labelSmall, color = Renk.metinIkincil)
            }
            HorizontalDivider(color = Renk.kenarlik)
            Text(sample?.metin(dil).orEmpty(), color = Renk.metin, fontFamily = LoraSerif, fontSize = 23.sp, lineHeight = 31.sp)
            Text(cevir(dil, "Tamamını okumak için bildirimi genişlet.", "Expand the notification to read it in full."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
    if (allowed) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
        Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.accent)
        Text(cevir(dil, "Bildirim iznin açık. Hazırsın.", "Notifications are allowed. You are ready."), color = Renk.metin, style = MaterialTheme.typography.labelLarge)
    }
    Text(cevir(dil, "Bildirimler senin seçimin. İzin vermeden de planını kullanabilirsin.", "Reminders are your choice. Your plan also works without permission."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
    TextButton(onClick = help, modifier = Modifier.heightIn(min = 48.dp)) { Text(cevir(dil, "Bildirim görünümü ve cihaz ayarları", "Notification appearance and device settings")) }
}

@Composable
private fun PlanHourDialog(profile: PersonalProfile, start: Boolean, dil: String, close: () -> Unit, select: (Int) -> Unit) {
    AlertDialog(onDismissRequest = close, title = { Text(if (start) cevir(dil, "Başlangıç saatini seç", "Choose start hour") else cevir(dil, "Bitiş saatini seç", "Choose end hour"), fontFamily = LoraSerif) },
        text = { Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()).selectableGroup(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val hours = if (start) (0 until profile.endHour).toList() else (profile.startHour + 1..24).toList()
            hours.forEach { hour -> PlanChoice(planHour(hour) + if (hour == 24) cevir(dil, " · Gece yarısı", " · Midnight") else "",
                hour == if (start) profile.startHour else profile.endHour, "hour-choice-$hour") { select(hour) } }
        } }, confirmButton = { TextButton(onClick = close) { Text(cevir(dil, "Vazgeç", "Cancel")) } })
}

@Composable
private fun PlanPermissionHelp(dil: String, close: () -> Unit) {
    val ctx = LocalContext.current
    AlertDialog(onDismissRequest = close, title = { Text(cevir(dil, "Bildirim yardımcısı", "Notification help"), fontFamily = LoraSerif) },
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
