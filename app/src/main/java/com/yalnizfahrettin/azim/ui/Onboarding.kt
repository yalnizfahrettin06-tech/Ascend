package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.yalnizfahrettin.azim.R
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
import androidx.compose.ui.text.font.FontFamily
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

/** Five quiet pages with a single footer action and a live reminder preview. */
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
    var encoded by rememberSaveable { mutableStateOf(initialDraft.copy(step = if (initialDraft.setupVersion >= 2) initialDraft.step.coerceIn(0, 4) else when (initialDraft.step) { 15 -> 2; 16 -> 3; in 17..19 -> 4; else -> 0 }, setupVersion = 2).encode()) }
    val profile = remember(encoded) { PersonalProfile.decode(encoded) }
    val step = profile.step
    var hourDialog by rememberSaveable { mutableIntStateOf(0) }
    var permissionHelp by rememberSaveable { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current
    val largeText = LocalDensity.current.fontScale > 1.35f
    fun update(value: PersonalProfile) {
        encoded = value.encode()
        draftChanged(value)
    }
    fun move(next: Int) {
        focus.clearFocus()
        keyboard?.hide()
        update(profile.copy(step = next.coerceIn(0, 4)))
    }
    fun finish(reminders: Boolean) {
        if (finishProfile != null) finishProfile(profile, reminders)
        else bitir(PersonalPlan.initialCategories(profile, previewAccess), profile.dailyCount, profile.startHour, profile.endHour, reminders)
    }
    BackHandler(step > 0 || editing || kaydediliyor) {
        if (!kaydediliyor) { if (step > 0) move(step - 1) else onCancel() }
    }
    Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().imePadding().testTag("onboarding-root")) {
        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (step > 0 || editing) IconButton(onClick = { if (step > 0) move(step - 1) else onCancel() }, enabled = !kaydediliyor,
                modifier = Modifier.testTag("onboarding-back")) { Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"), tint = Renk.metin) }
            else Spacer(Modifier.width(12.dp))
            Box(Modifier.weight(1f)) {
                if (largeText) Icon(AzimIkon.Yukselis, "Ascend", Modifier.size(24.dp), tint = Renk.metin)
                else Text("ascend", fontFamily = LoraSerif, fontSize = 24.sp, fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.5).sp, color = Renk.metin)
            }
            Text("${step + 1} / 5", fontSize = 12.sp, color = Renk.metinIkincil)
            Spacer(Modifier.width(12.dp))
        }
        val progress = (step + 1f) / 5
        val visibleProgress by animateFloatAsState(progress, tween(180), label = "onboarding-progress")
        Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(2.dp)
            .clip(CircleShape).background(Renk.kenarlik).testTag("onboarding-progress")
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) }) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(visibleProgress).background(Renk.accent, CircleShape))
        }
        AnimatedContent(step, transitionSpec = { (slideInHorizontally(tween(380)) { if (targetState > initialState) it / 5 else -it / 5 } + fadeIn(tween(320))) togetherWith
                (slideOutHorizontally(tween(280)) { if (targetState > initialState) -it / 6 else it / 6 } + fadeOut(tween(220))) },
            modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds(), label = "onboarding-step") { shownStep ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("onboarding-scroll")
                .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                when (shownStep) {
                    0 -> PlanWelcome(dil)
                    1 -> PlanIntroduction(dil)
                    2 -> PlanFrequency(profile, dil, previewAccess) { update(profile.copy(dailyCount = it)) }
                    3 -> PlanHours(profile, dil, previewAccess) { hourDialog = it }
                    4 -> PlanPermission(profile, dil, bildirimIzni, previewAccess) { permissionHelp = true }
                }
            }
        }
        HorizontalDivider(color = Renk.kenarlik.copy(alpha = .25f))
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).testTag("onboarding-footer"), horizontalAlignment = Alignment.CenterHorizontally) {
            hata?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp).semantics { liveRegion = LiveRegionMode.Polite }) }
            Button(onClick = {
                if (step < 4) move(step + 1)
                else if (bildirimIzni) finish(true) else izinIste()
            }, enabled = !kaydediliyor, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("onboarding-next")) {
                Text(if (kaydediliyor) cevir(dil, "Kaydediliyor…", "Saving…") else when (step) {
                    0 -> cevir(dil, "Ascend’i tanı", "Meet Ascend")
                    1 -> cevir(dil, "Hatırlatmaları ayarla", "Set up reminders")
                    4 -> if (bildirimIzni) cevir(dil, if (editing) "Planımı güncelle" else "Ascend’e başla", if (editing) "Update my plan" else "Start Ascend") else cevir(dil, "Bildirimlere izin ver", "Allow notifications")
                    else -> cevir(dil, "Devam", "Continue")
                }, textAlign = TextAlign.Center)
            }

        }
    }
    if (hourDialog != 0) PlanHourDialog(profile, hourDialog == 1, dil, { hourDialog = 0 }) { hour ->
        update(if (hourDialog == 1) profile.copy(startHour = hour) else profile.copy(endHour = hour)); hourDialog = 0
    }
    if (permissionHelp) PlanPermissionHelp(dil) { permissionHelp = false }
}

@Composable
private fun silverBrush() = Brush.linearGradient(if (Renk.karanlikMi)
    listOf(Color(0xFF33383D), Color(0xFF202428), Color(0xFF3B4146))
    else listOf(Color(0xFFE1E4E6), Color(0xFFF7F8F8), Color(0xFFC9CED2)))

@Composable
private fun OnboardingArtwork(height: Int, dil: String, quote: Boolean = false) {
    Box(Modifier.fillMaxWidth().heightIn(min = height.dp).clip(RoundedCornerShape(28.dp))
        .background(silverBrush()).border(1.dp, Renk.kenarlik, RoundedCornerShape(28.dp))) {
        Image(painterResource(R.drawable.art_roman_home_v9), null, Modifier.matchParentSize(),
            contentScale = ContentScale.Crop, alignment = Alignment.CenterEnd,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }))
        Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(
            Renk.zemin.copy(alpha = .92f), Renk.zemin.copy(alpha = .02f)))))
        Column(Modifier.align(Alignment.CenterStart).fillMaxWidth(.62f).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(if (quote) cevir(dil, "BİR SÖZ. BİR ADIM.", "ONE QUOTE. ONE STEP.") else "A S C E N D",
                color = Renk.metinIkincil, fontSize = 10.sp)
            Text(if (quote) cevir(dil, "Her şeyi bugün bitirmek zorunda değilsin.", "You do not have to finish everything today.")
                else cevir(dil, "Kendi hızında.", "At your pace."),
                fontFamily = LoraSerif, fontSize = 25.sp, lineHeight = 32.sp, color = Renk.metin)
            if (quote) Icon(AzimIkon.Kalp, null, Modifier.size(22.dp), tint = Renk.metin)
        }
    }
}

@Composable
private fun PlanSection(step: Int, dil: String) {
    val chapter = when (step) {
        0 -> cevir(dil, "HOŞ GELDİN", "WELCOME")
        1 -> cevir(dil, "BİR SÖZ. BİR ADIM.", "ONE QUOTE. ONE STEP.")
        2, 3 -> cevir(dil, "GÜNÜNÜN RİTMİ", "YOUR DAILY RHYTHM")
        else -> cevir(dil, "BİLDİRİMLERİN", "YOUR REMINDERS")
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.width(24.dp).height(1.dp).background(Renk.accent))
        Text(chapter, color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.4.sp,
            modifier = Modifier.weight(1f).testTag("onboarding-chapter"))
    }
}

@Composable
private fun ColumnScope.PlanWelcome(dil: String) {
    OnboardingArtwork(height = 260, dil = dil)
    PlanTitle(cevir(dil, "Gününe iyi bir söz.", "A little inspiration, every day."),
        cevir(dil, "Seçtiğin saatlerde sana eşlik eden sözler. Kendine ayırdığın küçük bir an.",
            "Words that meet you at your chosen hours. A small moment for yourself."), editorial = true)
    Text(cevir(dil, "Sana ait bir ritim · Hesap gerekmez", "Your own rhythm · No account needed"),
        color = Renk.metinIkincil, fontSize = 13.sp)

}

@Composable
private fun PlanTitle(title: String, description: String, modifier: Modifier = Modifier, editorial: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(title, modifier.semantics { heading() }, color = Renk.metin,
        fontFamily = if (editorial) LoraSerif else FontFamily.SansSerif,
        fontSize = if (editorial) 30.sp else 26.sp, lineHeight = if (editorial) 38.sp else 33.sp,
        fontWeight = if (editorial) FontWeight.Normal else FontWeight.Medium)
    Text(description, color = Renk.metinIkincil, fontSize = 15.sp, lineHeight = 23.sp)
    }
}

@Composable
private fun PlanChoice(label: String, selected: Boolean, tag: String, role: Role = Role.RadioButton, onClick: () -> Unit) {
    val fill by animateColorAsState(if (selected) Renk.accentZemin else Renk.zemin, tween(140), label = "choice-fill")
    val stroke by animateColorAsState(if (selected) Renk.accent else Renk.kenarlik, tween(140), label = "choice-stroke")
    val markShape = if (role == Role.Checkbox) RoundedCornerShape(5.dp) else CircleShape
    val interaction = if (role == Role.Checkbox) Modifier.toggleable(selected, role = role, onValueChange = { onClick() })
        else Modifier.selectable(selected, role = role, onClick = onClick)
    Row(Modifier.fillMaxWidth().heightIn(min = 60.dp).clip(RoundedCornerShape(14.dp)).background(fill)
        .border(if (selected) 1.5.dp else 1.dp, stroke, RoundedCornerShape(14.dp))
        .then(interaction).testTag(tag).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), color = Renk.metin, fontSize = 16.sp, lineHeight = 23.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        Box(Modifier.size(22.dp).border(1.dp, if (selected) Renk.accent else Renk.metinIkincil, markShape), contentAlignment = Alignment.Center) {
            if (selected) Icon(AzimIkon.Tik, null, Modifier.size(15.dp), tint = Renk.accent)
        }
    }
}

@Composable
private fun PlanFrequency(profile: PersonalProfile, dil: String, access: Set<String>, changed: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Gününe kaç kez eşlik edelim?", "How often should we check in?"),
        cevir(dil, "Az ya da çok, ritim senin. Bildirim iznini son adımda soracağız.", "A little or often, it is your rhythm. We will ask for notification permission at the end."))
    Surface(color = Color.Transparent, border = BorderStroke(1.dp, Renk.kenarlik), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().background(silverBrush()).padding(horizontal = 16.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(cevir(dil, "GÜNLÜK RİTMİN", "YOUR DAILY RHYTHM"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(onClick = { changed(profile.dailyCount - 1) }, enabled = profile.dailyCount > 1,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını azalt", "Fewer reminders") }) { Icon(AzimIkon.Eksi, null, Modifier.size(24.dp)) }
                AnimatedContent(profile.dailyCount, label = "count-change") { count ->
                    Text("$count", color = Renk.metin, fontFamily = FontFamily.SansSerif, fontSize = 36.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.testTag("reminder-count").semantics { liveRegion = LiveRegionMode.Polite })
                }
                OutlinedIconButton(onClick = { changed(profile.dailyCount + 1) }, enabled = profile.dailyCount < 7,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını artır", "More reminders") }) { Icon(AzimIkon.Arti, null, Modifier.size(24.dp)) }
            }
            Text(cevir(dil, "küçük durak / gün", "small pauses / day"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
    PlanLivePreview(profile, dil, access)
}

private fun planHour(hour: Int) = "%02d:00".format(Locale.ROOT, hour % 24)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanHours(profile: PersonalProfile, dil: String, access: Set<String>, select: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Günün hangi saatlerinde?", "Which hours work for you?"),
        cevir(dil, "Seçtiğin aralığın dışında sessiz kalırız.", "We stay quiet outside your chosen window."))
    listOf(Triple(1, cevir(dil, "Başlangıç", "From"), profile.startHour), Triple(2, cevir(dil, "Bitiş", "Until"), profile.endHour)).forEach { (id, label, hour) ->
        Surface(onClick = { select(id) }, shape = RoundedCornerShape(14.dp), color = Renk.yuzey, border = BorderStroke(1.dp, Renk.kenarlik),
            modifier = Modifier.fillMaxWidth().testTag(if (id == 1) "reminder-start" else "reminder-end")) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(AzimIkon.Saat, null, Modifier.size(22.dp), tint = Renk.metinIkincil)
                Text(label, Modifier.weight(1f), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                Text(planHour(hour), color = Renk.metin, fontFamily = FontFamily.SansSerif, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
            }
        }
    }
    PlanLivePreview(profile, dil, access)
}

@Composable
private fun PlanIntroduction(dil: String) {
    PlanTitle(cevir(dil, "Sende kalan bir söz.", "Words that stay with you."),
        cevir(dil, "Ana ekranda rastgele bir sözle karşılaş. Yeni bir söz için kaydır.",
            "Meet a random quote on the home screen. Swipe for another."))
    OnboardingArtwork(height = 260, dil = dil, quote = true)
    Text(cevir(dil, "Kaydır, keşfet. Sende kalan sözü kaydet veya paylaş.",
        "Swipe to explore. Save or share the words that stay with you."),
        color = Renk.metinIkincil, fontSize = 15.sp, lineHeight = 23.sp)

}

/** Uses the same schedule as delivery; this card never sends a notification. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanLivePreview(profile: PersonalProfile, dil: String, access: Set<String>) {
    val times = remember(profile.dailyCount, profile.startHour, profile.endHour) {
        BildirimZamanlari.hesapla(LocalDateTime.of(2000, 1, 1, 0, 0), profile.dailyCount, profile.startHour, profile.endHour)
            .map { it.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)) }
    }
    val sample = remember(profile, access, dil) {
        PersonalPlan.feed(profile, PersonalPlan.initialCategories(profile, access), access).firstOrNull()?.metin(dil)
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(cevir(dil, "Bildirim önizlemen", "Your reminder preview"), color = Renk.metin,
            fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Surface(onClick = { expanded = !expanded }, color = Color.Transparent, shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Renk.kenarlik), modifier = Modifier.fillMaxWidth().animateContentSize(tween(320)).testTag("live-reminder-preview")) {
            Column(Modifier.background(silverBrush()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(AzimIkon.Saat, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
                    Text("Ascend", Modifier.weight(1f), color = Renk.metin, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(times.firstOrNull().orEmpty(), color = Renk.metinIkincil, fontSize = 13.sp,
                        modifier = Modifier.testTag("live-reminder-time").semantics { liveRegion = LiveRegionMode.Polite })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.art_roman_home_v9), null,
                    Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterEnd, colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }))
                Text(sample ?: cevir(dil, "Kendine küçük bir an ayır.", "Take a small moment for yourself."),
                    color = Renk.metin, fontSize = 15.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f),
                    maxLines = if (expanded) Int.MAX_VALUE else 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Text(cevir(dil, if (expanded) "Örnek · Küçültmek için dokun" else "Örnek · Tamamını görmek için dokun",
                    if (expanded) "Sample · Tap to collapse" else "Sample · Tap to expand"), color = Renk.metinIkincil, fontSize = 12.sp)
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.testTag("reminder-preview-times")) {
            times.forEach { Text(it, color = Renk.metinIkincil, fontSize = 13.sp) }
        }
        Text(cevir(dil, "Günde ${profile.dailyCount} bildirim · Saatler yaklaşık",
            "${profile.dailyCount} reminders daily · Approximate times"), color = Renk.metinIkincil, fontSize = 12.sp,
            modifier = Modifier.testTag("live-reminder-summary"))
    }
}

@Composable
private fun PlanPermission(profile: PersonalProfile, dil: String, allowed: Boolean, access: Set<String>, help: () -> Unit) {
    PlanTitle(cevir(dil, "İyi bir söz seni bulsun.", "Let the right words find you."),
        cevir(dil, "Günde ${profile.dailyCount} kez, ${planHour(profile.startHour)}–${planHour(profile.endHour)} arasında.", "${profile.dailyCount} times a day between ${planHour(profile.startHour)} and ${planHour(profile.endHour)}."))
    PlanLivePreview(profile, dil, access)
    if (allowed) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
        Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.accent)
        Text(cevir(dil, "Bildirim iznin açık. Hazırsın.", "Notifications are allowed. You are ready."), color = Renk.metin, style = MaterialTheme.typography.labelLarge)
    }
    Text(cevir(dil, "Planının çalışması için bildirim iznini aç.", "Allow notifications so your reminder plan can work."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
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
