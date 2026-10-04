package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.util.Locale

@Composable
fun ExploreScreen(
    state: UserState,
    toggleTopic: (String) -> Unit,
    openOffer: (ProOffer) -> Unit,
    openSeries: () -> Unit,
    openQuote: (String) -> Unit,
) {
    val language = state.language
    val locale = remember(language) { Locale.forLanguageTag(language) }
    var query by rememberSaveable { mutableStateOf("") }
    var detail by rememberSaveable { mutableStateOf<String?>(null) }
    val focus = LocalFocusManager.current
    val needle = query.trim().lowercase(locale)
    val results = remember(needle, language) {
        if (needle.isEmpty()) emptyList() else Kategoriler.tumAltlar.filter { topic ->
            topic.ad(language).lowercase(locale).contains(needle) ||
                Sozler.kategoriden(topic.anahtar).any { it.metin(language).lowercase(locale).contains(needle) }
        }
    }
    fun toggle(topic: Kategori) {
        if (topic.anahtar in state.access) toggleTopic(topic.anahtar)
        else openOffer(ProOffer(ProSource.TOPIC, topic.anahtar))
    }

    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("explore")) {
        ScreenHeader(stringResource(R.string.tab_explore),
            pluralStringResource(R.plurals.explore_subtitle, state.activeTopics.size, Kategoriler.tumAltlar.size, state.activeTopics.size))
        TextField(query, { query = it }, singleLine = true,
            placeholder = { Text(stringResource(R.string.explore_search)) },
            leadingIcon = { Icon(AzimIkon.Ara, null, tint = Renk.metinIkincil) },
            trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(AzimIkon.Kapat, stringResource(R.string.action_clear)) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Renk.yuzey, unfocusedContainerColor = Renk.yuzey,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).testTag("explore-search"))
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)) {
            if (needle.isNotEmpty()) {
                if (results.isEmpty()) item { Text(stringResource(R.string.explore_no_results), Modifier.padding(16.dp), color = Renk.metinIkincil) }
                items(results, key = { "r-" + it.anahtar }) { TopicRow(it, state, { toggle(it) }) { detail = it.anahtar } }
            } else {
                item(key = "series") {
                    Surface(onClick = openSeries, color = Renk.accentZemin, shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("explore-series")) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Icon(AzimIkon.Basamak, null, Modifier.size(26.dp), tint = Renk.accent)
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.explore_series_title), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
                                Text(ShortSeries.active(state.series)?.let { stringResource(R.string.explore_series_continue, it.completed) }
                                    ?: stringResource(R.string.explore_series_body), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                            }
                            Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
                        }
                    }
                }
                Kategoriler.kesfetGruplari.forEach { group ->
                    item(key = "g-" + group.anahtar) {
                        val chosen = group.altlar.count { it.anahtar in state.activeTopics }
                        Row(Modifier.fillMaxWidth().padding(top = 18.dp, start = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            SectionTitle(group.ad(language), Modifier.weight(1f))
                            if (chosen > 0) Text(stringResource(R.string.explore_group_selected, chosen), style = MaterialTheme.typography.labelSmall, color = Renk.accent)
                        }
                    }
                    items(group.altlar.sortedBy { it.anahtar !in state.access }, key = { group.anahtar + "-" + it.anahtar }) { TopicRow(it, state, { toggle(it) }) { detail = it.anahtar } }
                }
            }
        }
    }
    detail?.let(Kategoriler::bul)?.let { topic ->
        TopicSheet(topic, state, close = { detail = null }, toggle = { toggle(topic) }, openQuote = openQuote,
            openOffer = { openOffer(ProOffer(ProSource.TOPIC, topic.anahtar, Sozler.kategoriden(topic.anahtar).firstOrNull()?.kimlik.orEmpty())) })
    }
}

@Composable
private fun TopicRow(topic: Kategori, state: UserState, toggle: () -> Unit, open: () -> Unit) {
    val language = state.language
    val locked = topic.anahtar !in state.access
    val on = topic.anahtar in state.activeTopics
    val last = on && state.activeTopics.size == 1
    Row(Modifier.fillMaxWidth().heightIn(min = 60.dp).clickable(role = Role.Button, onClick = open)
        .padding(horizontal = 8.dp).testTag("topic-${topic.anahtar}"), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(topic.ad(language), style = MaterialTheme.typography.bodyLarge, color = Renk.metin)
            Text(if (locked) stringResource(R.string.explore_pro_topic) else if (on) stringResource(R.string.explore_in_reminders)
                else Sozler.kategoriden(topic.anahtar).count { it.mevcut(language) }.let { n -> pluralStringResource(R.plurals.quotes_count, n, n) },
                style = MaterialTheme.typography.bodySmall, color = if (on) Renk.accent else Renk.metinIkincil)
        }
        val description = stringResource(if (on) R.string.explore_remove_from_reminders else R.string.explore_add_to_reminders, topic.ad(language))
        IconButton(onClick = toggle, enabled = !last, modifier = Modifier.testTag("toggle-${topic.anahtar}")
            .semantics { contentDescription = description; stateDescription = if (on) "✓" else "" }) {
            Box(Modifier.size(30.dp).background(if (on) Renk.accent else Color.Transparent, CircleShape)
                .then(if (on) Modifier else Modifier.background(Renk.yuzeyYuksek, CircleShape)), contentAlignment = Alignment.Center) {
                Icon(if (locked) AzimIkon.Kilit else if (on) AzimIkon.Tik else AzimIkon.Arti, null, Modifier.size(16.dp),
                    tint = if (on) Renk.zemin else Renk.metin)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicSheet(topic: Kategori, state: UserState, close: () -> Unit, toggle: () -> Unit, openQuote: (String) -> Unit, openOffer: () -> Unit) {
    val language = state.language
    val locked = topic.anahtar !in state.access
    val on = topic.anahtar in state.activeTopics
    // Verbatim quotes first, then reflections; both only where this language has them.
    val quotes = Sozler.kategoriden(topic.anahtar).filter { it.kimlik !in state.hidden && it.mevcut(language) }.sortedBy { !it.gercekAlinti }
    ModalBottomSheet(onDismissRequest = close, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = Renk.zemin) {
        LazyColumn(Modifier.fillMaxWidth().testTag("topic-sheet"), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
            item {
                Text(topic.ad(language), style = MaterialTheme.typography.headlineSmall, color = Renk.metin)
                Text(Kategoriler.grupBul(topic.grup)?.ad(language).orEmpty(), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                Spacer(Modifier.height(12.dp))
                if (locked) PrimaryButton(stringResource(R.string.explore_unlock_with_pro), openOffer, tag = "topic-sheet-pro")
                else SwitchRow(stringResource(R.string.explore_add_switch), on, { toggle() }, enabled = !(on && state.activeTopics.size == 1),
                    subtitle = if (on && state.activeTopics.size == 1) stringResource(R.string.explore_last_topic) else null, tag = "topic-sheet-toggle")
                if (topic.grup in setOf("filozoflar", "tasavvuf", "inanc")) Text(stringResource(R.string.explore_inspired_note),
                    style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil, modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(8.dp))
            }
            items(if (locked) quotes.take(2) else quotes, key = { it.kimlik }) { quote ->
                HorizontalDivider(color = Renk.kenarlik)
                Column(Modifier.fillMaxWidth().clickable(enabled = !locked) { openQuote(quote.kimlik) }.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (quote.gercekAlinti) Text(stringResource(R.string.explore_quote_badge).uppercase(Locale.forLanguageTag(language)) + " · " + quote.imza(language),
                        style = MaterialTheme.typography.labelSmall, color = Renk.accent)
                    Text(quote.metin(language), fontFamily = LoraSerif, style = MaterialTheme.typography.bodyLarge, color = Renk.metin)
                }
            }
            if (locked) item {
                Text(pluralStringResource(R.plurals.explore_locked_more, quotes.size - 2, quotes.size - 2), style = MaterialTheme.typography.bodySmall,
                    color = Renk.metinIkincil, modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}
