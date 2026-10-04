package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

val LocalLanguage = staticCompositionLocalOf { "en" }

@Composable
fun currentLanguage(): String = LocalLanguage.current

enum class Tab(val label: Int, val icon: ImageVector) {
    TODAY(R.string.tab_today, AzimIkon.YukselenMarka),
    EXPLORE(R.string.tab_explore, AzimIkon.Kesfet),
    REMINDERS(R.string.tab_reminders, AzimIkon.Bildirim),
    YOU(R.string.tab_you, AzimIkon.Kisi),
}

/** An external request to show something: a notification quote, a shortcut tab. */
data class OpenRequest(val quoteId: String? = null, val tab: Tab? = null, val section: YouSection? = null, val nonce: Long = 0L)

private enum class Route { SETTINGS, APPEARANCE, SERIES }

@Composable
fun AscendApp(vm: AppViewModel, request: OpenRequest?, requestPermission: () -> Unit) {
    val maybeState by vm.state.collectAsStateWithLifecycle()
    val state = maybeState ?: return
    val permission by vm.permission.collectAsStateWithLifecycle()
    val nextReminder by vm.nextReminder.collectAsStateWithLifecycle()

    if (!state.onboarded) {
        OnboardingScreen(state.language, permission, requestPermission, vm::setLanguage) { topics, reminders ->
            vm.completeOnboarding(state.language, topics, reminders)
        }
        return
    }

    val feed by vm.feed.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(Tab.TODAY) }
    var route by rememberSaveable { mutableStateOf<Route?>(null) }
    var youSection by rememberSaveable { mutableStateOf(YouSection.SAVED) }
    var readerId by rememberSaveable { mutableStateOf<String?>(null) }
    var shareId by rememberSaveable { mutableStateOf<String?>(null) }
    var offer by rememberSaveable { mutableStateOf<String?>(null) }
    var offerDismissals by rememberSaveable { mutableIntStateOf(0) }
    var proBusy by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val tabs = rememberSaveableStateHolder()

    // Each external request is handled once.
    var handled by rememberSaveable { mutableLongStateOf(-1L) }
    LaunchedEffect(request?.nonce) {
        val r = request ?: return@LaunchedEffect
        if (r.nonce == handled) return@LaunchedEffect
        handled = r.nonce
        r.tab?.let { tab = it; route = null }
        r.section?.let { youSection = it }
        r.quoteId?.takeIf { Sozler.aktifKimlikMi(it) }?.let { readerId = it }
    }

    val openOffer: (ProOffer) -> Unit = { offer = it.encode() }
    val share: (Soz) -> Unit = { shareId = it.kimlik }

    BackHandler(route != null) { route = if (route == Route.APPEARANCE) Route.SETTINGS else null }
    BackHandler(route == null && tab != Tab.TODAY) { tab = Tab.TODAY }

    Box(Modifier.fillMaxSize().background(Renk.zemin)) {
        when (route) {
            Route.SETTINGS -> SettingsScreen(state, back = { route = null }, setLanguage = vm::setLanguage, setTheme = vm::setTheme,
                setHaptics = vm::setHaptics, openAppearance = { route = Route.APPEARANCE }, openPro = { openOffer(ProOffer()) },
                restoreHidden = vm::restoreHidden)
            Route.APPEARANCE -> GorunumEkrani(state.language, state.background, state.pro, { openOffer(ProOffer(ProSource.THEME)) },
                vm::setBackground, openOffer, offerDismissals, feed.firstOrNull(), back = { route = Route.SETTINGS })
            Route.SERIES -> Box(Modifier.fillMaxSize().background(Renk.zemin)) {
                KisaSerilerEkrani(state.language, state.series, state.favorites.toSet(), back = { route = null },
                    start = { vm.startSeries(it) }, complete = { vm.completeSeriesDay(it) },
                    save = { vm.toggleFavorite(it.kimlik) }, share = share, pro = state.pro,
                    proOpen = { openOffer(ProOffer(ProSource.SERIES, "restart")) },
                    firstComplete = { vm.startSeries("restart"); vm.completeSeriesDay("restart") },
                    firstCompleteFor = { vm.startSeries(it); vm.completeSeriesDay(it) },
                    offerFor = { openOffer(ProOffer(ProSource.SERIES, it)) })
            }
            null -> Scaffold(
                containerColor = Renk.zemin,
                contentWindowInsets = WindowInsets(0),
                snackbarHost = { SnackbarHost(snackbar) },
                bottomBar = { BottomBar(tab) { tab = it } },
            ) { padding ->
                Box(Modifier.padding(padding).fillMaxSize()) {
                    AnimatedContent(tab, label = "tab", transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) }) { shown ->
                        tabs.SaveableStateProvider(shown.name) {
                            when (shown) {
                                Tab.TODAY -> TodayScreen(state, feed, permission, nextReminder, snackbar,
                                    read = vm::markRead, toggleFavorite = vm::toggleFavorite, share = share, hide = vm::setHidden,
                                    requestPermission = requestPermission, enableReminders = { vm.updateReminders { it.copy(enabled = true) } },
                                    resume = { vm.pauseToday(false) }, openReminders = { tab = Tab.REMINDERS })
                                Tab.EXPLORE -> ExploreScreen(state, vm::toggleTopic, openOffer, { route = Route.SERIES }) { readerId = it }
                                Tab.REMINDERS -> RemindersScreen(state, permission, nextReminder, requestPermission, vm::updateReminders,
                                    vm::pauseToday, vm::toggleTopic, addTopics = { tab = Tab.EXPLORE }, sendTest = vm::sendTest,
                                    openHistory = { youSection = YouSection.HISTORY; tab = Tab.YOU })
                                Tab.YOU -> YouScreen(state, youSection, { youSection = it }, openSettings = { route = Route.SETTINGS },
                                    openQuote = { readerId = it }, toggleFavorite = vm::toggleFavorite, restoreFavorite = vm::restoreFavorite,
                                    share = share, goToday = { tab = Tab.TODAY }, openReminders = { tab = Tab.REMINDERS })
                            }
                        }
                    }
                }
            }
        }
    }

    readerId?.let(Sozler::kimlikten)?.let { quote ->
        LaunchedEffect(quote.kimlik) { vm.markRead(quote.kimlik) }
        SozOkuyucu(quote, state.language, quote.kimlik in state.favorites, close = { readerId = null },
            save = { vm.toggleFavorite(quote.kimlik) }, share = { shareId = quote.kimlik })
    }
    shareId?.let(Sozler::kimlikten)?.let { quote ->
        PaylasimEkrani(quote, state.language, geri = { shareId = null }, pro = state.pro,
            proAc = { openOffer(ProOffer(ProSource.SHARE)) }, offerOpen = openOffer, offerDismissals = offerDismissals)
    }
    offer?.let { encoded ->
        ProEkrani(state.language, state.pro, proBusy, null, offer = ProOffer.decode(encoded),
            kapat = { offer = null; offerDismissals++ },
            degistir = { enabled ->
                proBusy = true
                vm.setPro(enabled)
                if (enabled) ProductSignals.record(context, ProductSignals.Event.DEMO_ENABLED, ProOffer.decode(encoded).source)
                proBusy = false
                offer = null
            })
    }
}

@Composable
private fun BottomBar(selected: Tab, select: (Tab) -> Unit) {
    NavigationBar(containerColor = Renk.zemin, tonalElevation = androidx.compose.ui.unit.Dp(0f), modifier = Modifier.testTag("bottom-bar")) {
        Tab.entries.forEach { tab ->
            NavigationBarItem(selected = tab == selected, onClick = { select(tab) },
                icon = { Icon(tab.icon, null) }, label = { Text(stringResource(tab.label)) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Renk.accent, selectedTextColor = Renk.metin,
                    indicatorColor = Renk.accentZemin, unselectedIconColor = Renk.metinIkincil, unselectedTextColor = Renk.metinIkincil),
                modifier = Modifier.testTag("tab-${tab.name.lowercase()}"))
        }
    }
}
