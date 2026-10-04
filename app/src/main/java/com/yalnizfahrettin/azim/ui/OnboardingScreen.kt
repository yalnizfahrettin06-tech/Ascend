package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val STEPS = 5

/**
 * Five short steps: language, topics, rhythm, permission (skippable), ready.
 * Answers live in saveable state and are committed atomically at the end.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    language: String,
    permission: Boolean,
    requestPermission: () -> Unit,
    setLanguage: (String) -> Unit,
    finish: (Set<String>, Reminders) -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var topicsRaw by rememberSaveable { mutableStateOf("") }
    val topics = topicsRaw.split(',').filter(String::isNotBlank).toSet()
    var perDay by rememberSaveable { mutableIntStateOf(Defaults.PER_DAY) }
    var start by rememberSaveable { mutableIntStateOf(Defaults.START_HOUR) }
    var end by rememberSaveable { mutableIntStateOf(Defaults.END_HOUR) }
    var surprise by rememberSaveable { mutableStateOf(true) }
    var askedPermission by rememberSaveable { mutableStateOf(false) }
    val reminders = Reminders(perDay = perDay, startHour = start, endHour = end, surprise = surprise).normalized()

    // Granting permission from the system dialog moves on by itself.
    LaunchedEffect(permission, askedPermission) { if (step == 3 && permission && askedPermission) step = 4 }
    BackHandler(step > 0) { step-- }

    Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().testTag("onboarding")) {
        Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (step > 0) IconButton(onClick = { step-- }, modifier = Modifier.testTag("onboarding-back")) {
                Icon(AzimIkon.Geri, stringResource(R.string.action_back), tint = Renk.metin)
            } else Spacer(Modifier.width(48.dp))
            val progress by animateFloatAsState((step + 1f) / STEPS, tween(220), label = "progress")
            Box(Modifier.weight(1f).height(4.dp).background(Renk.kenarlik, CircleShape)
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo((step + 1f) / STEPS, 0f..1f) }) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress).background(Renk.accent, CircleShape))
            }
            Text(stringResource(R.string.onboarding_step, step + 1, STEPS), style = MaterialTheme.typography.labelSmall,
                color = Renk.metinIkincil, modifier = Modifier.padding(horizontal = 16.dp))
        }
        AnimatedContent(step, modifier = Modifier.weight(1f), label = "onboarding-step", transitionSpec = {
            val dir = if (targetState > initialState) 1 else -1
            (slideInHorizontally(tween(260)) { dir * it / 6 } + fadeIn(tween(220))) togetherWith
                (slideOutHorizontally(tween(220)) { -dir * it / 8 } + fadeOut(tween(160)))
        }) { shown ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                when (shown) {
                    0 -> WelcomeStep(language, setLanguage)
                    1 -> TopicsStep(topics) { topicsRaw = it.joinToString(",") }
                    2 -> RhythmStep(reminders, { perDay = it }, { start = it; if (end <= it) end = it + 1 }, { end = it }, { surprise = it })
                    3 -> PermissionStep(permission, topics, language)
                    else -> ReadyStep(topics, reminders, permission, language)
                }
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            when (step) {
                3 -> if (permission) PrimaryButton(stringResource(R.string.action_continue), { step = 4 }, tag = "onboarding-next")
                else {
                    PrimaryButton(stringResource(R.string.onboarding_permission_allow), { askedPermission = true; requestPermission() }, tag = "onboarding-allow")
                    TextButton(onClick = { step = 4 }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("onboarding-skip")) {
                        Text(stringResource(R.string.onboarding_permission_skip))
                    }
                }
                4 -> PrimaryButton(stringResource(R.string.onboarding_start), { finish(topics, reminders) }, tag = "onboarding-finish")
                else -> PrimaryButton(stringResource(R.string.action_continue), { step++ },
                    enabled = step != 1 || topics.isNotEmpty(), tag = "onboarding-next")
            }
        }
    }
}

@Composable
private fun StepTitle(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Renk.metin, modifier = Modifier.semantics { heading() })
        Text(body, style = MaterialTheme.typography.bodyLarge, color = Renk.metinIkincil)
    }
}

@Composable
private fun WelcomeStep(language: String, setLanguage: (String) -> Unit) {
    Spacer(Modifier.height(24.dp))
    Box(Modifier.size(72.dp).background(Renk.accentZemin, RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) {
        Icon(AzimIkon.YukselenMarka, null, Modifier.size(40.dp), tint = Renk.accent)
    }
    StepTitle(stringResource(R.string.onboarding_welcome_title), stringResource(R.string.onboarding_welcome_body))
    Text(Sozler.kimlikten("v5_motivasyon_07")?.metin(language).orEmpty(), fontFamily = LoraSerif, fontSize = 22.sp, lineHeight = 31.sp,
        color = Renk.metin, modifier = Modifier.padding(vertical = 8.dp))
    SectionTitle(stringResource(R.string.settings_language))
    Segmented(Languages.options, language, setLanguage, tag = "language")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TopicsStep(selected: Set<String>, change: (Set<String>) -> Unit) {
    val language = currentLanguage()
    var limitHit by remember { mutableStateOf(false) }
    StepTitle(stringResource(R.string.onboarding_topics_title), stringResource(R.string.onboarding_topics_body, Defaults.MAX_TOPICS_SETUP))
    Text(stringResource(R.string.onboarding_topics_count, selected.size, Defaults.MAX_TOPICS_SETUP),
        style = MaterialTheme.typography.labelLarge, color = if (limitHit) Renk.accent else Renk.metinIkincil,
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }.testTag("topic-count"))
    Kategoriler.kesfetGruplari.forEach { group ->
        val free = group.altlar.filter { it.anahtar in Access.free }
        if (free.isEmpty()) return@forEach
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(group.ad(language), style = MaterialTheme.typography.titleSmall, color = Renk.metin)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                free.forEach { topic ->
                    val on = topic.anahtar in selected
                    TopicChip(topic.ad(language), on, tag = "setup-topic-${topic.anahtar}") {
                        limitHit = !on && selected.size >= Defaults.MAX_TOPICS_SETUP
                        when {
                            on -> change(selected - topic.anahtar)
                            !limitHit -> change(selected + topic.anahtar)
                        }
                    }
                }
            }
        }
    }
    Text(stringResource(R.string.onboarding_topics_more, Kategoriler.tumAltlar.size - Access.free.size),
        style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
}

@Composable
private fun RhythmStep(r: Reminders, perDay: (Int) -> Unit, start: (Int) -> Unit, end: (Int) -> Unit, surprise: (Boolean) -> Unit) {
    StepTitle(stringResource(R.string.onboarding_rhythm_title), stringResource(R.string.onboarding_rhythm_body))
    RhythmEditor(r, perDay, start, end)
    AscendCard {
        SwitchRow(stringResource(R.string.reminders_surprise), r.surprise, surprise,
            subtitle = stringResource(R.string.reminders_surprise_body), tag = "setup-surprise")
    }
}

/** Shared by onboarding and the reminders screen. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RhythmEditor(r: Reminders, perDay: (Int) -> Unit, start: (Int) -> Unit, end: (Int) -> Unit) {
    AscendCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.reminders_per_day), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
                Text(pluralStringResource(R.plurals.reminders_times_day, r.perDay, r.perDay), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
            }
            Stepper(r.perDay, 1..Defaults.MAX_PER_DAY, stringResource(R.string.reminders_per_day), perDay, tag = "per-day")
        }
        HorizontalDivider(color = Renk.kenarlik)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.reminders_from), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
            Stepper(r.startHour, 0..22, stringResource(R.string.reminders_from), start, ::hourLabel, tag = "start-hour")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.reminders_until), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
            Stepper(r.endHour, r.startHour + 1..24, stringResource(R.string.reminders_until), end, ::hourLabel, tag = "end-hour")
        }
        HorizontalDivider(color = Renk.kenarlik)
        Text(stringResource(R.string.reminders_times_preview), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
        val times = remember(r) { ReminderPlan.slots(LocalDate.now(), r, ZoneId.systemDefault()) }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("reminder-times")) {
            times.forEach {
                Surface(color = Renk.zemin, shape = RoundedCornerShape(50)) {
                    Text(it.format(DateTimeFormatter.ofPattern("HH:mm")), Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge, color = Renk.metin)
                }
            }
        }
    }
}

@Composable
private fun PermissionStep(permission: Boolean, topics: Set<String>, language: String) {
    StepTitle(stringResource(R.string.onboarding_permission_title), stringResource(R.string.onboarding_permission_body))
    val sample = remember(topics, language) {
        Sozler.tumu().firstOrNull { it.kategori in topics }
    }
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().testTag("notification-preview")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(AzimIkon.YukselenMarka, null, Modifier.size(18.dp), tint = Renk.accent)
                Text("Ascend", style = MaterialTheme.typography.labelMedium, color = Renk.metin)
                Dot()
                Text(sample?.let { Kategoriler.bul(it.kategori)?.ad(language) }.orEmpty(), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
            }
            Text(sample?.metin(language).orEmpty(), style = MaterialTheme.typography.bodyLarge, color = Renk.metin)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.notification_save), style = MaterialTheme.typography.labelLarge, color = Renk.accent)
                Text(stringResource(R.string.notification_pause_today), style = MaterialTheme.typography.labelLarge, color = Renk.accent)
            }
        }
    }
    if (permission) StatusBanner(AzimIkon.Tik, stringResource(R.string.onboarding_permission_granted), null, tag = "permission-granted")
    else Text(stringResource(R.string.onboarding_permission_later), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
}

@Composable
private fun ReadyStep(topics: Set<String>, r: Reminders, permission: Boolean, language: String) {
    Spacer(Modifier.height(16.dp))
    StepTitle(stringResource(R.string.onboarding_ready_title), stringResource(R.string.onboarding_ready_body))
    AscendCard {
        Text(stringResource(R.string.reminders_topics), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
        Text(topics.mapNotNull { Kategoriler.bul(it)?.ad(language) }.joinToString(" · "), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
        HorizontalDivider(color = Renk.kenarlik)
        Text(stringResource(R.string.reminders_rhythm), style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
        Text(pluralStringResource(R.plurals.reminders_summary, r.perDay, r.perDay, hourLabel(r.startHour), hourLabel(r.endHour)),
            style = MaterialTheme.typography.titleMedium, color = Renk.metin)
        if (!permission) {
            HorizontalDivider(color = Renk.kenarlik)
            Text(stringResource(R.string.onboarding_ready_no_permission), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil,
                fontWeight = FontWeight.Medium)
        }
    }
}
