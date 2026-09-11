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

/** Five quiet pages. Reminder permission is optional and only requested by an explicit tap. */
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
        AnimatedContent(step, transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(100)) },
            modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds(), label = "onboarding-step") { shownStep ->
            BoxWithConstraints(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).heightIn(min = maxHeight).testTag("onboarding-scroll")
                .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, if (shownStep == 0) Alignment.CenterVertically else Alignment.Top)) {
                PlanSection(shownStep, dil)
                when (shownStep) {
                    0 -> PlanWelcome(dil)
                    1 -> PlanIntroduction(dil)
                    2 -> PlanFrequency(profile, dil) { update(profile.copy(dailyCount = it)) }
                    3 -> PlanHours(profile, dil) { hourDialog = it }
                    4 -> PlanPermission(profile, dil, bildirimIzni, previewAccess) { permissionHelp = true }
                }
            }
            }
        }
        HorizontalDivider(color = Renk.kenarlik.copy(alpha = .25f))
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
            if (step < 4) TextButton(onClick = { finish(false) }, enabled = !kaydediliyor, modifier = Modifier.testTag("onboarding-quick-start")) { Text(cevir(dil, "Şimdilik bildirimsiz başla", "Start without reminders")) }
            if (step == 4) TextButton(onClick = { finish(false) }, enabled = !kaydediliyor, modifier = Modifier.testTag("onboarding-finish-without-reminders")) {
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
        0 -> cevir(dil, "HOŞ GELDİN", "WELCOME")
        1 -> cevir(dil, "BİR SÖZ. BİR ADIM.", "ONE QUOTE. ONE STEP.")
        2, 3 -> cevir(dil, "GÜNÜNÜN RİTMİ", "YOUR DAILY RHYTHM")
        else -> cevir(dil, "SEN İSTEDİĞİNDE", "WHEN YOU CHOOSE")
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
        KlasikGorsel(KlasikMotif.COLUMN, Modifier.matchParentSize().offset(x = 80.dp, y = (-16).dp), opacity = .16f)
        Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PlanTitle(cevir(dil, "Kendi hızında. Bir adım yukarı.", "At your pace. One step higher."),
                cevir(dil, "Bazen bir olumlama, bazen yeni bir bakış. Günün içinde kendine küçük bir an ayır.",
                    "An affirmation or a fresh perspective. Make a little room for yourself in the day."))
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(AzimIkon.Patika, null, Modifier.size(22.dp), tint = Renk.accent)
        Text(cevir(dil, "Kendi hızında · Hesap gerekmez", "At your pace · No account needed"),
            color = Renk.metin, style = MaterialTheme.typography.bodyMedium)
    }
    Text(cevir(dil, "Kısa bir tanıtım, istersen gün içine yayılan hatırlatmalar. Hemen başlayabilir, ayarları sonra değiştirebilirsin.",
        "A brief introduction, then optional reminders. Start now and change your settings later."),
        color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanTitle(title: String, description: String, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(title, modifier.semantics { heading() }, color = Renk.metin, fontFamily = LoraSerif,
        fontSize = 30.sp, lineHeight = 37.sp, fontWeight = FontWeight.Normal, letterSpacing = (-0.4).sp)
    Text(description, color = Renk.metinIkincil, fontSize = 14.sp, lineHeight = 22.sp)
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
private fun PlanFrequency(profile: PersonalProfile, dil: String, changed: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Gününe kaç kez eşlik edelim?", "How often should we check in?"),
        cevir(dil, "Az ya da çok, ritim senin. Bildirim iznini son adımda soracağız.", "A little or often, it is your rhythm. We will ask for notification permission at the end."))
    Spacer(Modifier.height(12.dp))
    Surface(color = Renk.yuzey, border = BorderStroke(1.dp, Renk.kenarlik), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(cevir(dil, "GÜNLÜK RİTMİN", "YOUR DAILY RHYTHM"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(onClick = { changed(profile.dailyCount - 1) }, enabled = profile.dailyCount > 1,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını azalt", "Fewer reminders") }) { Icon(AzimIkon.Eksi, null, Modifier.size(24.dp)) }
                Text("${profile.dailyCount}", color = Renk.metin, fontFamily = LoraSerif, fontSize = 48.sp,
                    modifier = Modifier.testTag("reminder-count").semantics { liveRegion = LiveRegionMode.Polite })
                OutlinedIconButton(onClick = { changed(profile.dailyCount + 1) }, enabled = profile.dailyCount < 7,
                    modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını artır", "More reminders") }) { Icon(AzimIkon.Arti, null, Modifier.size(24.dp)) }
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
            Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(AzimIkon.Saat, null, Modifier.size(22.dp), tint = Renk.metinIkincil)
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

@Composable
private fun PlanIntroduction(dil: String) {
    PlanTitle(cevir(dil, "Sende kalan bir söz.", "Words that stay with you."),
        cevir(dil, "Ana ekranda rastgele bir sözle karşılaş. Yeni bir söz için kaydır.",
            "Meet a random quote on the home screen. Swipe for another."))
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Renk.kenarlik)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(cevir(dil, "Her şeyi bugün bitirmek zorunda değilsin.", "You do not have to finish everything today."),
                fontFamily = LoraSerif, fontSize = 25.sp, lineHeight = 34.sp, color = Renk.metin)
            Text(cevir(dil, "Ascend · Örnek söz", "Ascend · Sample quote"), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
    listOf(
        AzimIkon.Kalp to cevir(dil, "Sende kalan sözleri kaydet, istersen paylaş.", "Save words that stay with you, or share them."),
        AzimIkon.Kitap to cevir(dil, "Keşfet’te konuları kendin seçip oku.", "Choose topics to read in Explore.")
    ).forEach { (icon, text) ->
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = Renk.metinIkincil)
            Text(text, color = Renk.metinIkincil, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
    Text(cevir(dil, "Bildirim konularını daha sonra Keşfet’ten değiştirebilirsin. Bildirimler isteğe bağlı.",
        "Change reminder topics later in Explore. Reminders are optional."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun PlanPermission(profile: PersonalProfile, dil: String, allowed: Boolean, access: Set<String>, help: () -> Unit) {
    PlanTitle(cevir(dil, "İyi bir söz seni bulsun.", "Let the right words find you."),
        cevir(dil, "Günde ${profile.dailyCount} kez, ${planHour(profile.startHour)}–${planHour(profile.endHour)} arasında.", "${profile.dailyCount} times a day between ${planHour(profile.startHour)} and ${planHour(profile.endHour)}."))
    val sample = PersonalPlan.feed(profile, PersonalPlan.initialCategories(profile, access), access).firstOrNull()
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Renk.kenarlik)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(AzimIkon.Yukselis, null, Modifier.size(20.dp), tint = Renk.metin)
                Text("ascend", fontFamily = LoraSerif, fontSize = 20.sp, color = Renk.metin, modifier = Modifier.weight(1f))
                Text(cevir(dil, "önizleme", "preview"), fontSize = 11.sp, color = Renk.metinIkincil)
            }
            HorizontalDivider(color = Renk.kenarlik)
            Text(sample?.metin(dil).orEmpty(), color = Renk.metin, fontSize = 16.sp, lineHeight = 24.sp)
            Text(cevir(dil, "Tamamını okumak için bildirimi genişlet.", "Expand the notification to read it in full."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
        }
    }
    if (allowed) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
        Icon(AzimIkon.Tik, null, Modifier.size(18.dp), tint = Renk.accent)
        Text(cevir(dil, "Bildirim iznin açık. Hazırsın.", "Notifications are allowed. You are ready."), color = Renk.metin, style = MaterialTheme.typography.labelLarge)
    }
    Text(cevir(dil, "Bildirimler senin seçimin. İzin vermeden de sözleri okuyabilir, kaydedebilir ve paylaşabilirsin.", "Reminders are optional. Read, save and share quotes without granting permission."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
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
