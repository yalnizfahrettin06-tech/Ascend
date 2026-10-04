package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.time.LocalDate

enum class YouSection { SAVED, HISTORY }

/** Personal space: progress at a glance, saved quotes and reminder history. */
@Composable
fun YouScreen(
    state: UserState,
    section: YouSection,
    selectSection: (YouSection) -> Unit,
    openSettings: () -> Unit,
    openQuote: (String) -> Unit,
    toggleFavorite: (String) -> Unit,
    restoreFavorite: (String, Int) -> Unit,
    share: (Soz) -> Unit,
    goToday: () -> Unit,
    openReminders: () -> Unit,
) {
    val language = state.language
    val today = LocalDate.now()
    val holder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("you")) {
        ScreenHeader(stringResource(R.string.tab_you), action = {
            IconButton(onClick = openSettings, modifier = Modifier.testTag("open-settings")) {
                Icon(AzimIkon.Ayarlar, stringResource(R.string.settings_title), tint = Renk.metin)
            }
        })
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(state.currentStreak(today).toString(), stringResource(R.string.you_streak), AzimIkon.Alev, Modifier.weight(1f))
            StatTile(state.readCountOn(today).toString(), stringResource(R.string.you_read_today), AzimIkon.Kitap, Modifier.weight(1f))
            StatTile(state.favorites.size.toString(), stringResource(R.string.you_saved), AzimIkon.Kalp, Modifier.weight(1f))
        }
        Text(stringResource(R.string.you_totals, state.bestStreak, state.totalRead), style = MaterialTheme.typography.bodySmall,
            color = Renk.metinIkincil, modifier = Modifier.padding(horizontal = 24.dp))
        Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Segmented(listOf(YouSection.SAVED to stringResource(R.string.you_saved_tab), YouSection.HISTORY to stringResource(R.string.you_history_tab)),
                section, selectSection, tag = "you-section")
        }
        Box(Modifier.weight(1f)) {
            holder.SaveableStateProvider(section.name) {
                when (section) {
                    YouSection.SAVED -> FavorilerEkrani(state.favorites.mapNotNull(Sozler::kimlikten), language,
                        cikar = toggleFavorite, oku = { openQuote(it.kimlik) }, kesfet = goToday, paylas = share,
                        embedded = true, restore = restoreFavorite)
                    YouSection.HISTORY -> BildirimGecmisiEkrani(language,
                        state.deliveriesByDay().mapKeys { it.key.toString() }.mapValues { (_, v) -> v.map { it.quoteId } },
                        state.favorites.toSet(), back = {}, save = { toggleFavorite(it.kimlik) }, share = share,
                        embedded = true, reminders = openReminders)
                }
            }
        }
    }
}
