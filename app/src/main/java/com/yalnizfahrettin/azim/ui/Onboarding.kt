package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.notif.BildirimZamanlari
import com.yalnizfahrettin.azim.notif.TeslimatYardimi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Five distinct experiences. Content and its action are measured as one scrollable group. */
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
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val viewport = maxHeight
            val stageHeight = (viewport.value * .43f).coerceIn(210f, 310f).dp
            AnimatedContent(step, transitionSpec = {
                (slideInHorizontally(tween(360)) { if (targetState > initialState) it / 6 else -it / 6 } + fadeIn(tween(280))) togetherWith
                    (slideOutHorizontally(tween(240)) { if (targetState > initialState) -it / 8 else it / 8 } + fadeOut(tween(180)))
            }, modifier = Modifier.fillMaxSize().clipToBounds(), label = "onboarding-step") { shownStep ->
                // Center the complete composition, never a detached footer. Short screens scroll;
                // tall screens enlarge the visual stage without stretching copy-to-action spacing.
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("onboarding-scroll"),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(Modifier.widthIn(max = 480.dp).fillMaxWidth().heightIn(min = viewport)
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.Center) {
                        Column(Modifier.fillMaxWidth().testTag("onboarding-body"), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            when (shownStep) {
                                0 -> PlanWelcome(dil, stageHeight)
                                1 -> PlanIntroduction(dil, stageHeight)
                                2 -> PlanFrequency(profile, dil, stageHeight) { update(profile.copy(dailyCount = it)) }
                                3 -> PlanHours(profile, dil) { hourDialog = it }
                                4 -> PlanPermission(profile, dil, bildirimIzni, previewAccess, stageHeight) { permissionHelp = true }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Column(Modifier.fillMaxWidth().testTag("onboarding-footer")) {
                            hata?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp).semantics { liveRegion = LiveRegionMode.Polite }) }
                            Button(onClick = {
                                if (step < 4) move(step + 1)
                                else if (bildirimIzni) finish(true) else izinIste()
                            }, enabled = !kaydediliyor, shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("onboarding-next")) {
                                Text(if (kaydediliyor) cevir(dil, "Kaydediliyor…", "Saving…") else when (step) {
                                    0 -> cevir(dil, "Bir sözle başla", "Begin with a quote")
                                    1 -> cevir(dil, "Ritmimi ayarla", "Set my rhythm")
                                    4 -> if (bildirimIzni) cevir(dil, if (editing) "Planımı güncelle" else "Ascend’e başla", if (editing) "Update my plan" else "Start Ascend") else cevir(dil, "Bildirimlere izin ver", "Allow notifications")
                                    else -> cevir(dil, "Devam", "Continue")
                                }, textAlign = TextAlign.Center, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
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
private fun PlanWelcome(dil: String, stageHeight: Dp) {
    PlanTitle(cevir(dil, "Kendine açılan\nküçük bir alan.", "A little space\nfor yourself."),
        cevir(dil, "Günün içinde dur. Bir sözle kendine dön.", "Pause in your day. Find a moment in a quote."))
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val scale by animateFloatAsState(if (entered) 1f else 1.07f, tween(1100), label = "gateway-reveal")
    Box(Modifier.fillMaxWidth().height(stageHeight).clip(RoundedCornerShape(topStart = 64.dp, topEnd = 64.dp, bottomEnd = 22.dp, bottomStart = 22.dp))
        .background(Renk.yuzey).testTag("welcome-gateway")) {
        Image(painterResource(R.drawable.art_onboarding_gateway), null, Modifier.fillMaxSize().graphicsLayer { scaleX = scale; scaleY = scale },
            contentScale = ContentScale.Crop, colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }))
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(AzimIkon.Esik, null, Modifier.size(24.dp), tint = Renk.metinIkincil)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(cevir(dil, "Senin saatlerinde, senin ritminde.", "Your hours. Your rhythm."), color = Renk.metin, fontSize = 14.sp)
            Text(cevir(dil, "Hesap oluşturman gerekmez.", "No account needed."), color = Renk.metinIkincil, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlanTitle(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, Modifier.semantics { heading() }, color = Renk.metin, fontFamily = ArayuzFont,
            fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold)
        Text(description, color = Renk.metinIkincil, fontSize = 14.sp, lineHeight = 21.sp)
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

/** One decision, one visual: the count directly changes the silver dial. */
@Composable
private fun PlanFrequency(profile: PersonalProfile, dil: String, stageHeight: Dp, changed: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Gününde kaç küçük durak?", "How many moments in your day?"),
        cevir(dil, "Sana uygun bildirim sayısını seç.", "Choose how often you would like a reminder."))
    val ink = Renk.metin
    val edge = Renk.kenarlik
    val fill = silverBrush()
    val animatedCount by animateFloatAsState(profile.dailyCount.toFloat(), tween(350), label = "rhythm-dots")
    Box(Modifier.fillMaxWidth().heightIn(min = stageHeight).testTag("frequency-dial"), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(stageHeight.coerceAtMost(280.dp)).clearAndSetSemantics { }) {
            val radius = size.minDimension * .40f
            drawCircle(fill, radius * .85f)
            drawCircle(edge.copy(alpha = .65f), radius, style = Stroke(1.dp.toPx()))
            drawCircle(edge.copy(alpha = .35f), radius * 1.12f, style = Stroke(1.dp.toPx()))
            for (i in 0..6) {
                val theta = (-90 + i * 360.0 / 7) * Math.PI / 180
                val center = Offset(this.center.x + cos(theta).toFloat() * radius, this.center.y + sin(theta).toFloat() * radius)
                val active = (animatedCount - i).coerceIn(0f, 1f)
                drawCircle(edge, 5.dp.toPx(), center)
                if (active > 0f) drawCircle(ink, (3f + active * 3f).dp.toPx(), center)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            AnimatedContent(profile.dailyCount, label = "count-change") { count ->
                Text("$count", color = Renk.metin, fontFamily = ArayuzFont, fontWeight = FontWeight.Medium, fontSize = 68.sp, lineHeight = 78.sp,
                    modifier = Modifier.testTag("reminder-count").semantics { liveRegion = LiveRegionMode.Polite })
            }
            Text(cevir(dil, "bildirim / gün", "reminders / day"), color = Renk.metinIkincil, fontSize = 13.sp)
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = { changed(profile.dailyCount - 1) }, enabled = profile.dailyCount > 1,
            modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını azalt", "Fewer reminders") }) {
            Icon(AzimIkon.Eksi, null, Modifier.size(22.dp))
        }
        Text(cevir(dil, "Az", "Less") + "     ·     " + cevir(dil, "Çok", "More"), Modifier.padding(horizontal = 28.dp),
            color = Renk.metinIkincil, fontSize = 13.sp)
        OutlinedIconButton(onClick = { changed(profile.dailyCount + 1) }, enabled = profile.dailyCount < 7,
            modifier = Modifier.size(52.dp).semantics { contentDescription = cevir(dil, "Bildirim sayısını artır", "More reminders") }) {
            Icon(AzimIkon.Arti, null, Modifier.size(22.dp))
        }
    }
    Text(cevir(dil, "1–7 bildirim. Daha sonra değiştirebilirsin.", "1–7 reminders. You can change this later."),
        Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp)
}

private fun planHour(hour: Int) = "%02d:00".format(Locale.ROOT, hour % 24)
private fun previewTimes(profile: PersonalProfile) = BildirimZamanlari.hesapla(
    LocalDateTime.of(2000, 1, 1, 0, 0), profile.dailyCount, profile.startHour, profile.endHour)

/** A day diagram replaces the duplicated notification and schedule summaries. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanHours(profile: PersonalProfile, dil: String, select: (Int) -> Unit) {
    PlanTitle(cevir(dil, "Hangi saatler sana ait?", "Which hours work for you?"),
        cevir(dil, "Bu aralıkta hatırlatır, dışında sessiz kalırız.", "Reminders inside this window. Quiet outside it."))
    val times = remember(profile.dailyCount, profile.startHour, profile.endHour) { previewTimes(profile) }
    val ink = Renk.metin
    val edge = Renk.kenarlik
    val begin by animateFloatAsState(profile.startHour / 24f, tween(400), label = "day-start")
    val end by animateFloatAsState(profile.endHour / 24f, tween(400), label = "day-end")
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(silverBrush()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(cevir(dil, "GÜNÜNÜN AKIŞI", "YOUR DAY"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.6.sp)
        Canvas(Modifier.fillMaxWidth().height(116.dp).testTag("schedule-day-visual").clearAndSetSemantics { }) {
            val y = size.height * .72f
            val left = 6.dp.toPx()
            val width = size.width - 2 * left
            // A quiet arc evokes a day's passage, without another photo or clock face.
            drawArc(edge, 180f, 180f, false, Offset(left, y - size.height * .7f), Size(width, size.height * 1.4f), style = Stroke(1.dp.toPx()))
            drawLine(edge, Offset(left, y), Offset(left + width, y), 2.dp.toPx(), StrokeCap.Round)
            drawLine(ink, Offset(left + width * begin, y), Offset(left + width * end, y), 3.dp.toPx(), StrokeCap.Round)
            repeat(profile.dailyCount) { i ->
                val fraction = begin + (end - begin) * (2 * i + 1) / (2 * profile.dailyCount)
                val x = left + width * fraction
                drawLine(edge, Offset(x, y - 28.dp.toPx()), Offset(x, y), 1.dp.toPx())
                drawCircle(ink, 4.dp.toPx(), Offset(x, y - 28.dp.toPx()))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("00:00", "12:00", "24:00").forEach { Text(it, color = Renk.metinIkincil, fontSize = 11.sp) }
        }
    }
    BoxWithConstraints {
        val stacked = maxWidth < 300.dp || LocalDensity.current.fontScale > 1.35f
        if (stacked) Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HourControl(dil, true, profile.startHour, Modifier.fillMaxWidth()) { select(1) }
            HourControl(dil, false, profile.endHour, Modifier.fillMaxWidth()) { select(2) }
        } else Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HourControl(dil, true, profile.startHour, Modifier.weight(1f)) { select(1) }
            HourControl(dil, false, profile.endHour, Modifier.weight(1f)) { select(2) }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(cevir(dil, "Yaklaşık bildirim saatlerin", "Your approximate reminder times"), color = Renk.metinIkincil, fontSize = 12.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("reminder-preview-times")) {
            times.forEach { time ->
                Text(time.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)), color = Renk.metin, fontSize = 13.sp,
                    modifier = Modifier.background(Renk.yuzey, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun HourControl(dil: String, start: Boolean, hour: Int, modifier: Modifier, click: () -> Unit) {
    Surface(onClick = click, color = Renk.zemin, border = BorderStroke(1.dp, Renk.kenarlik), shape = RoundedCornerShape(18.dp),
        modifier = modifier.testTag(if (start) "reminder-start" else "reminder-end")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(cevir(dil, if (start) "Başlangıç" else "Bitiş", if (start) "From" else "Until"), color = Renk.metinIkincil, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(planHour(hour), Modifier.weight(1f), color = Renk.metin, fontSize = 23.sp, fontWeight = FontWeight.Medium)
                Icon(AzimIkon.Ileri, null, Modifier.size(16.dp), tint = Renk.metinIkincil)
            }
        }
    }
}

@Composable
private fun PlanIntroduction(dil: String, stageHeight: Dp) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    var liked by rememberSaveable { mutableStateOf(false) }
    val quotes = listOf(
        cevir(dil, "Her şeyi bugün bitirmek zorunda değilsin.", "You do not have to finish everything today."),
        cevir(dil, "Küçük bir adım da ilerlemektir.", "A small step is still a step forward."),
        cevir(dil, "Kendine, sevdiğin birine konuşur gibi konuş.", "Speak to yourself as you would to someone you love."))
    fun change(direction: Int) { page = (page + direction + quotes.size) % quotes.size; liked = false }
    PlanTitle(cevir(dil, "Bir sözde dur.\nSonrakini keşfet.", "Pause on a quote.\nDiscover another."),
        cevir(dil, "Kartı kaydır; sevdiğin söze kalpten dokun.", "Swipe the card. Tap the heart on a quote you like."))
    val dragThreshold = with(LocalDensity.current) { 48.dp.toPx() }
    Box(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 10.dp).testTag("practice-deck")) {
        Box(Modifier.matchParentSize().padding(horizontal = 14.dp).offset(y = 10.dp)
            .background(Renk.kenarlik.copy(alpha = .5f), RoundedCornerShape(24.dp)))
        Box(Modifier.matchParentSize().padding(horizontal = 6.dp).offset(y = 5.dp)
            .background(Renk.yuzey, RoundedCornerShape(24.dp)))
        Column(Modifier.fillMaxWidth().heightIn(min = stageHeight).clip(RoundedCornerShape(24.dp))
            .background(silverBrush()).border(1.dp, Renk.kenarlik, RoundedCornerShape(24.dp))
            .pointerInput(page) {
                var travel = 0f
                detectHorizontalDragGestures(onDragStart = { travel = 0f }, onHorizontalDrag = { change, amount ->
                    change.consume(); travel += amount
                }, onDragEnd = { if (travel < -dragThreshold) change(1) else if (travel > dragThreshold) change(-1) })
            }.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(cevir(dil, "KENDİNE BİR NOT", "A NOTE TO YOURSELF"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.4.sp)
            AnimatedContent(page, transitionSpec = {
                (fadeIn(tween(250)) + slideInHorizontally(tween(300)) { it / 8 }) togetherWith fadeOut(tween(150))
            }, modifier = Modifier.heightIn(min = (stageHeight - 150.dp).coerceAtLeast(90.dp)), label = "practice-quote") { index ->
                Text(quotes[index], color = Renk.metin, fontFamily = LoraSerif, fontSize = 27.sp, lineHeight = 36.sp,
                    modifier = Modifier.testTag("practice-quote-text"))
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(if (liked) cevir(dil, "Örnek beğenildi", "Sample liked") else cevir(dil, "Bir kez dene", "Give it a try"),
                    Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite }, color = Renk.metinIkincil, fontSize = 12.sp)
                IconToggleButton(checked = liked, onCheckedChange = { liked = it }, modifier = Modifier.size(48.dp).testTag("practice-like")) {
                    val scale by animateFloatAsState(if (liked) 1.15f else 1f, tween(180), label = "practice-heart")
                    Icon(if (liked) AzimIkon.KalpDolu else AzimIkon.Kalp, cevir(dil, "Örnek sözü beğen", "Like sample quote"),
                        Modifier.size(25.dp).graphicsLayer { scaleX = scale; scaleY = scale }, tint = Renk.metin)
                }
            }
        }
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { i -> Box(Modifier.size(if (i == page) 7.dp else 5.dp).background(if (i == page) Renk.metin else Renk.kenarlik, CircleShape)) }
        }
        TextButton(onClick = { change(1) }, modifier = Modifier.heightIn(min = 48.dp).testTag("practice-next")) {
            Text(cevir(dil, "Başka bir söz", "Another quote"), fontSize = 13.sp)
            Spacer(Modifier.width(8.dp)); Icon(AzimIkon.Ileri, null, Modifier.size(18.dp))
        }
    }
}

/** Only the permission page previews a notification. Expansion is a user-controlled demo. */
@Composable
private fun PlanPermission(profile: PersonalProfile, dil: String, allowed: Boolean, access: Set<String>, stageHeight: Dp, help: () -> Unit) {
    PlanTitle(cevir(dil, "İyi bir söz seni bulsun.", "Let good words find you."),
        cevir(dil, "Günde ${profile.dailyCount} kez, ${planHour(profile.startHour)}–${planHour(profile.endHour)} arasında.",
            "${profile.dailyCount} times a day, between ${planHour(profile.startHour)} and ${planHour(profile.endHour)}."))
    val firstTime = remember(profile.dailyCount, profile.startHour, profile.endHour) { previewTimes(profile).firstOrNull()?.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)).orEmpty() }
    val sample = remember(profile, access, dil) {
        PersonalPlan.feed(profile, PersonalPlan.initialCategories(profile, access), access).firstOrNull()?.metin(dil)
            ?: cevir(dil, "Kendine küçük bir an ayır.", "Take a small moment for yourself.")
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().heightIn(min = stageHeight).clip(RoundedCornerShape(32.dp))
        .background(silverBrush()).border(1.dp, Renk.kenarlik, RoundedCornerShape(32.dp)).padding(18.dp).testTag("notification-device"),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically)) {
        Box(Modifier.width(38.dp).height(4.dp).background(Renk.metinIkincil.copy(alpha = .45f), CircleShape))
        Text(cevir(dil, "BİLDİRİM ÖRNEĞİ", "NOTIFICATION PREVIEW"), color = Renk.metinIkincil, fontSize = 10.sp, letterSpacing = 1.2.sp)
        Surface(onClick = { expanded = !expanded }, color = Renk.zemin, shape = RoundedCornerShape(18.dp), shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth().animateContentSize(tween(280)).testTag("live-reminder-preview")
                .semantics { stateDescription = cevir(dil, if (expanded) "Genişletilmiş" else "Daraltılmış", if (expanded) "Expanded" else "Collapsed") }) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(AzimIkon.Bildirim, null, Modifier.size(17.dp), tint = Renk.metin)
                    Text("Ascend", Modifier.weight(1f), color = Renk.metin, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(firstTime, color = Renk.metinIkincil, fontSize = 11.sp, modifier = Modifier.testTag("live-reminder-time"))
                }
                Text(sample, color = Renk.metin, fontSize = 15.sp, lineHeight = 22.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cevir(dil, if (expanded) "Küçült" else "Tamamını gör", if (expanded) "Collapse" else "Read full quote"),
                        Modifier.weight(1f), color = Renk.metinIkincil, fontSize = 12.sp)
                    Icon(AzimIkon.Ileri, null, Modifier.size(15.dp).graphicsLayer { rotationZ = if (expanded) -90f else 90f }, tint = Renk.metinIkincil)
                }
            }
        }
        Box(Modifier.width(72.dp).height(3.dp).background(Renk.metinIkincil.copy(alpha = .3f), CircleShape))
    }
    Surface(onClick = help, color = Color.Transparent, modifier = Modifier.fillMaxWidth().testTag("notification-appearance-help")) {
        Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(if (allowed) AzimIkon.Tik else AzimIkon.Bildirim, null, Modifier.size(22.dp), tint = Renk.metinIkincil)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(cevir(dil, "Ayrıntılı bildirim görünümü", "Detailed notification appearance"), color = Renk.metin, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(cevir(dil, if (allowed) "İznin açık. Cihaz ayarlarını incele." else "Nasıl görünür? Cihaz ayarlarını incele.",
                    if (allowed) "Permission granted. View device settings." else "How it appears. View device settings."), color = Renk.metinIkincil, fontSize = 12.sp, lineHeight = 18.sp)
            }
            Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
        }
    }
}

@Composable
private fun PlanHourDialog(profile: PersonalProfile, start: Boolean, dil: String, close: () -> Unit, select: (Int) -> Unit) {
    AlertDialog(onDismissRequest = close, title = { Text(if (start) cevir(dil, "Başlangıç saatini seç", "Choose start hour") else cevir(dil, "Bitiş saatini seç", "Choose end hour"), fontFamily = ArayuzFont) },
        text = { Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()).selectableGroup(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val hours = if (start) (0 until profile.endHour).toList() else (profile.startHour + 1..24).toList()
            hours.forEach { hour -> PlanChoice(planHour(hour) + if (hour == 24) cevir(dil, " · Gece yarısı", " · Midnight") else "",
                hour == if (start) profile.startHour else profile.endHour, "hour-choice-$hour") { select(hour) } }
        } }, confirmButton = { TextButton(onClick = close) { Text(cevir(dil, "Vazgeç", "Cancel")) } })
}

@Composable
private fun PlanPermissionHelp(dil: String, close: () -> Unit) {
    val ctx = LocalContext.current
    AlertDialog(onDismissRequest = close, title = { Text(cevir(dil, "Bildirim yardımcısı", "Notification help"), fontFamily = ArayuzFont) },
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
