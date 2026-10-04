package com.yalnizfahrettin.azim.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.notif.TeslimatYardimi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/** Everything about reminders in one place. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersScreen(
    state: UserState,
    permission: Boolean,
    nextReminder: ZonedDateTime?,
    requestPermission: () -> Unit,
    update: ((Reminders) -> Reminders) -> Unit,
    pauseToday: (Boolean) -> Unit,
    toggleTopic: (String) -> Unit,
    addTopics: () -> Unit,
    sendTest: ((Boolean) -> Unit) -> Unit,
    openHistory: () -> Unit,
) {
    val language = state.language
    val r = state.reminders
    val context = LocalContext.current
    var help by rememberSaveable { mutableStateOf(false) }
    val paused = r.isPaused(System.currentTimeMillis())

    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("reminders")) {
        ScreenHeader(stringResource(R.string.tab_reminders), stringResource(R.string.reminders_subtitle))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Status: the one thing that must be obvious.
            AscendCard {
                val (title, body) = when {
                    !permission -> R.string.reminders_status_permission to R.string.reminders_status_permission_body
                    !r.enabled -> R.string.reminders_status_off to R.string.reminders_status_off_body
                    paused -> R.string.reminders_status_paused to R.string.reminders_status_paused_body
                    else -> R.string.reminders_status_on to null
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(40.dp).background(Renk.accentZemin, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Icon(if (permission && r.enabled && !paused) AzimIkon.Tik else AzimIkon.Bildirim, null, tint = Renk.accent)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(title), style = MaterialTheme.typography.titleMedium, color = Renk.metin, modifier = Modifier.testTag("reminder-status"))
                        Text(body?.let { stringResource(it) } ?: nextReminder?.let {
                            stringResource(R.string.reminders_next_at, it.format(DateTimeFormatter.ofPattern("HH:mm")))
                        } ?: stringResource(R.string.today_no_more_today), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                    }
                }
                if (!permission) PrimaryButton(stringResource(R.string.onboarding_permission_allow), requestPermission, tag = "reminders-allow")
                SwitchRow(stringResource(R.string.reminders_enabled), r.enabled, { on -> update { it.copy(enabled = on) } }, tag = "reminders-enabled")
                if (r.enabled) TextButton(onClick = { pauseToday(!paused) }, modifier = Modifier.testTag("reminders-pause")) {
                    Icon(AzimIkon.Saat, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                    Text(stringResource(if (paused) R.string.reminders_resume else R.string.reminders_pause_today))
                }
            }

            SectionTitle(stringResource(R.string.reminders_rhythm))
            RhythmEditor(r, { n -> update { it.copy(perDay = n) } },
                { h -> update { it.copy(startHour = h, endHour = maxOf(it.endHour, h + 1)) } },
                { h -> update { it.copy(endHour = h) } })

            SectionTitle(stringResource(R.string.reminders_topics))
            AscendCard {
                Text(stringResource(R.string.reminders_topics_body), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.activeTopics.mapNotNull(Kategoriler::bul).sortedBy { it.ad(language) }.forEach { topic ->
                        InputChip(selected = true, onClick = { toggleTopic(topic.anahtar) }, enabled = state.activeTopics.size > 1,
                            label = { Text(topic.ad(language)) },
                            trailingIcon = { if (state.activeTopics.size > 1) Icon(AzimIkon.Kapat, stringResource(R.string.action_remove), Modifier.size(16.dp)) },
                            modifier = Modifier.heightIn(min = 40.dp).testTag("reminder-topic-${topic.anahtar}"))
                    }
                }
                OutlinedButton(onClick = addTopics, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("reminders-add-topics")) {
                    Icon(AzimIkon.Arti, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.reminders_topics_edit))
                }
                HorizontalDivider(color = Renk.kenarlik)
                SwitchRow(stringResource(R.string.reminders_surprise), r.surprise, { on -> update { it.copy(surprise = on) } },
                    subtitle = stringResource(R.string.reminders_surprise_body), tag = "reminders-surprise")
            }

            SectionTitle(stringResource(R.string.reminders_recent))
            AscendCard {
                val recent = state.deliveries.take(3)
                if (recent.isEmpty()) Text(stringResource(R.string.reminders_recent_empty), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                recent.forEach { d ->
                    Sozler.kimlikten(d.quoteId)?.let { q ->
                        Text(q.metin(language), fontFamily = LoraSerif, style = MaterialTheme.typography.bodyMedium, color = Renk.metin, maxLines = 3)
                    }
                }
                if (state.deliveries.isNotEmpty()) TextButton(onClick = openHistory) { Text(stringResource(R.string.reminders_see_history)) }
            }

            SectionTitle(stringResource(R.string.reminders_help_title))
            AscendCard {
                NavRow(stringResource(R.string.reminders_test), stringResource(R.string.reminders_test_body), AzimIkon.Bildirim, "reminders-test") {
                    if (!permission) requestPermission()
                    else sendTest { ok -> Toast.makeText(context, context.getString(if (ok) R.string.reminders_test_sent else R.string.reminders_test_failed), Toast.LENGTH_SHORT).show() }
                }
                NavRow(stringResource(R.string.reminders_help), stringResource(R.string.reminders_help_body), AzimIkon.Ayarlar, "reminders-help") { help = true }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
    if (help) DeliveryHelp { help = false }
}

@Composable
private fun DeliveryHelp(close: () -> Unit) {
    val ctx = LocalContext.current
    val samsung = remember { TeslimatYardimi.acilirPencereAyariVarMi() }
    fun open(action: () -> Boolean) { if (!action()) Toast.makeText(ctx, ctx.getString(R.string.help_open_failed), Toast.LENGTH_LONG).show() }
    AlertDialog(onDismissRequest = close, title = { Text(stringResource(R.string.reminders_help)) }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.help_body_general))
            OutlinedButton(onClick = { open { TeslimatYardimi.bildirimAyarlariniAc(ctx) } }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.help_app_settings)) }
            OutlinedButton(onClick = { open { TeslimatYardimi.kanalAyarlariniAc(ctx) } }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.help_popup_settings)) }
            if (samsung) Text(stringResource(R.string.help_body_samsung))
            Text(stringResource(R.string.help_body_battery), style = MaterialTheme.typography.bodySmall)
        }
    }, confirmButton = { TextButton(onClick = close) { Text(stringResource(R.string.action_done)) } })
}
