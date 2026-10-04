package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    state: UserState,
    feed: List<Soz>,
    permission: Boolean,
    nextReminder: ZonedDateTime?,
    snackbar: SnackbarHostState,
    read: (String) -> Unit,
    toggleFavorite: (String) -> Unit,
    share: (Soz) -> Unit,
    hide: (String, Boolean) -> Unit,
    requestPermission: () -> Unit,
    enableReminders: () -> Unit,
    resume: () -> Unit,
    openReminders: () -> Unit,
) {
    val language = state.language
    val currentFeed by rememberUpdatedState(feed)
    val pager = rememberPagerState { currentFeed.size }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    var speaker by remember { mutableStateOf<Seslendirici?>(null) }
    DisposableEffect(language) { onDispose { speaker?.kapat(); speaker = null } }
    val active = feed.getOrNull(pager.settledPage)
    LaunchedEffect(active?.kimlik) { active?.let { read(it.kimlik); speaker?.durdur() } }

    val theme = AnaTemalar.find(state.background)
    val art = theme.art != null
    val ink = if (art) Color.White else Renk.metin
    val inkSoft = if (art) Color.White.copy(alpha = .78f) else Renk.metinIkincil

    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("today")) {
        Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting(), style = MaterialTheme.typography.headlineSmall, color = Renk.metin)
                Text(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, EEEE", java.util.Locale.forLanguageTag(language))),
                    style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
            }
            val streak = state.currentStreak(java.time.LocalDate.now())
            if (streak > 0) Surface(color = Renk.accentZemin, shape = RoundedCornerShape(50), modifier = Modifier.testTag("streak")) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(AzimIkon.Alev, null, Modifier.size(16.dp), tint = Renk.accent)
                    Text(pluralStringResource(R.plurals.streak_days, streak, streak), style = MaterialTheme.typography.labelMedium, color = Renk.metin)
                }
            }
        }
        Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            val r = state.reminders
            when {
                !permission -> StatusBanner(AzimIkon.Bildirim, stringResource(R.string.today_permission_off), stringResource(R.string.action_turn_on), "banner-permission", requestPermission)
                !r.enabled -> StatusBanner(AzimIkon.Bildirim, stringResource(R.string.today_reminders_off), stringResource(R.string.action_turn_on), "banner-off", enableReminders)
                r.isPaused(System.currentTimeMillis()) -> StatusBanner(AzimIkon.Saat, stringResource(R.string.today_paused), stringResource(R.string.action_resume), "banner-paused", resume)
                else -> Surface(onClick = openReminders, color = Color.Transparent, shape = RoundedCornerShape(14.dp), modifier = Modifier.testTag("next-reminder")) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(AzimIkon.Bildirim, null, Modifier.size(16.dp), tint = Renk.accent)
                        Text(nextReminder?.let { stringResource(R.string.today_next_reminder, it.format(DateTimeFormatter.ofPattern("HH:mm"))) }
                            ?: stringResource(R.string.today_no_more_today), style = MaterialTheme.typography.bodySmall, color = Renk.metin)
                        Dot()
                        Text(pluralStringResource(R.plurals.topics_count, state.activeTopics.size, state.activeTopics.size),
                            Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                        Icon(AzimIkon.Ileri, null, Modifier.size(14.dp), tint = Renk.metinIkincil)
                    }
                }
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(28.dp)).background(if (art) Color.Black else Renk.yuzey)) {
            if (art) TemaZemini(theme, Modifier.matchParentSize(), veil = .55f, dil = language)
            if (feed.isEmpty()) EmptyFeed(openReminders)
            else HorizontalPager(pager, Modifier.fillMaxSize().testTag("quote-pager"), key = { feed.getOrNull(it)?.kimlik ?: it }) { page ->
                val quote = feed.getOrNull(page) ?: return@HorizontalPager
                QuotePage(quote, language, ink, inkSoft)
            }
        }
        if (active != null) Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${pager.currentPage + 1} / ${feed.size}", Modifier.weight(1f).padding(start = 8.dp),
                style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil)
            val saved = active.kimlik in state.favorites
            IconButton(onClick = {
                if (state.haptics) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                toggleFavorite(active.kimlik)
            }, modifier = Modifier.testTag("today-save").semantics { selected = saved }) {
                Icon(if (saved) AzimIkon.KalpDolu else AzimIkon.Kalp,
                    stringResource(if (saved) R.string.action_unsave else R.string.action_save), tint = if (saved) Renk.accent else Renk.metin)
            }
            IconButton(onClick = { share(active) }, modifier = Modifier.testTag("today-share")) {
                Icon(AzimIkon.Paylas, stringResource(R.string.action_share), tint = Renk.metin)
            }
            IconButton(onClick = {
                val s = speaker ?: Seslendirici(context, language).also { speaker = it }
                if (!s.degistir(active.metin(language))) scope.launch { snackbar.showSnackbar(context.getString(R.string.today_tts_unavailable)) }
            }, modifier = Modifier.testTag("today-listen")) {
                Icon(if (speaker?.konusuyor == true) AzimIkon.SesDur else AzimIkon.Ses, stringResource(R.string.action_listen), tint = Renk.metin)
            }
            var menu by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menu = true }, modifier = Modifier.testTag("today-more")) {
                    Icon(AzimIkon.Daha, stringResource(R.string.action_more), tint = Renk.metin)
                }
                DropdownMenu(menu, { menu = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_copy)) }, leadingIcon = { Icon(AzimIkon.Kopyala, null) }, onClick = {
                        menu = false
                        clipboard.setText(AnnotatedString("${active.metin(language)}\n— ${active.sunumEtiketi(language)}"))
                        scope.launch { snackbar.showSnackbar(context.getString(R.string.today_copied)) }
                    })
                    DropdownMenuItem(text = { Text(stringResource(R.string.action_hide)) }, modifier = Modifier.testTag("today-hide"), onClick = {
                        menu = false
                        val id = active.kimlik
                        hide(id, true)
                        scope.launch {
                            val result = snackbar.showSnackbar(context.getString(R.string.today_hidden), context.getString(R.string.action_undo), duration = SnackbarDuration.Long)
                            if (result == SnackbarResult.ActionPerformed) hide(id, false)
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun QuotePage(quote: Soz, language: String, ink: Color, inkSoft: Color) {
    val text = quote.metin(language)
    val large = LocalDensity.current.fontScale > 1.3f
    val size = when {
        text.length < 70 -> if (large) 26 else 32
        text.length < 120 -> if (large) 23 else 27
        else -> if (large) 20 else 23
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center) {
        Text(Kategoriler.bul(quote.kategori)?.ad(language).orEmpty().uppercase(java.util.Locale.forLanguageTag(language)),
            style = MaterialTheme.typography.labelMedium, color = inkSoft, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(18.dp))
        Text(text, fontFamily = LoraSerif, fontSize = size.sp, lineHeight = (size * 1.35f).sp, color = ink,
            style = TextStyle(lineBreak = LineBreak.Paragraph), modifier = Modifier.testTag("quote-text"))
        Spacer(Modifier.height(20.dp))
        Text("— ${quote.sunumEtiketi(language)}", style = MaterialTheme.typography.bodySmall, color = inkSoft, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptyFeed(openReminders: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.today_empty_title), style = MaterialTheme.typography.titleLarge, color = Renk.metin)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.today_empty_body), style = MaterialTheme.typography.bodyMedium, color = Renk.metinIkincil)
        Spacer(Modifier.height(16.dp))
        Button(onClick = openReminders) { Text(stringResource(R.string.reminders_topics_edit)) }
    }
}

@Composable
private fun greeting(): String = stringResource(when (LocalTime.now().hour) {
    in 5..11 -> R.string.greeting_morning
    in 12..17 -> R.string.greeting_afternoon
    in 18..22 -> R.string.greeting_evening
    else -> R.string.greeting_night
})
