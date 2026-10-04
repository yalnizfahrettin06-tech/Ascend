package com.yalnizfahrettin.azim.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yalnizfahrettin.azim.data.AscendStore
import com.yalnizfahrettin.azim.data.HomeFeed
import com.yalnizfahrettin.azim.data.QuotePicker
import com.yalnizfahrettin.azim.data.Reminders
import com.yalnizfahrettin.azim.data.Soz
import com.yalnizfahrettin.azim.data.ThemeMode
import com.yalnizfahrettin.azim.data.UserActions
import com.yalnizfahrettin.azim.data.UserState
import com.yalnizfahrettin.azim.notif.Notifier
import com.yalnizfahrettin.azim.notif.ReminderScheduler
import com.yalnizfahrettin.azim.widget.AzimWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime

/** Owns app state and every user action; screens stay stateless renderers. */
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = AscendStore.get(app)

    /** Null until the first read, so nothing renders with wrong defaults. */
    val state: StateFlow<UserState?> = store.state.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _permission = MutableStateFlow(Notifier.canPost(app))
    val permission: StateFlow<Boolean> = _permission.asStateFlow()

    private val _nextReminder = MutableStateFlow<ZonedDateTime?>(null)
    val nextReminder: StateFlow<ZonedDateTime?> = _nextReminder.asStateFlow()

    /** Today's feed is a snapshot: reading must not reorder the pages under the reader. */
    private val _feed = MutableStateFlow<List<Soz>>(emptyList())
    val feed: StateFlow<List<Soz>> = _feed.asStateFlow()
    private var feedKey: Any? = null

    init {
        viewModelScope.launch {
            store.state.collect { s ->
                val key = Triple(s.activeTopics, LocalDate.now(), s.onboarded)
                if (key != feedKey) {
                    feedKey = key
                    _feed.value = HomeFeed.build(s, LocalDate.now())
                } else if (_feed.value.any { it.kimlik in s.hidden }) {
                    _feed.value = _feed.value.filterNot { it.kimlik in s.hidden }
                }
            }
        }
        viewModelScope.launch {
            store.state.collect { s -> Notifier.ensureChannel(getApplication(), s.language) }
        }
    }

    /** Called on every resume: permission can change outside the app. */
    fun refreshSystemState() {
        _permission.value = Notifier.canPost(getApplication())
        viewModelScope.launch { reschedule(store.current()) }
    }

    private fun reschedule(s: UserState) {
        _nextReminder.value = ReminderScheduler.schedule(getApplication(), s)
    }

    private fun edit(reminders: Boolean = false, widget: Boolean = false, transform: (UserState) -> UserState) {
        viewModelScope.launch {
            val s = store.update(transform)
            if (reminders) reschedule(s)
            if (widget) runCatching { AzimWidget.tazele(getApplication()) }
        }
    }

    fun completeOnboarding(language: String, topics: Set<String>, reminders: Reminders) =
        edit(reminders = true, widget = true) { UserActions.completeOnboarding(it, language, topics, reminders) }

    fun setLanguage(code: String) = edit(widget = true) { it.copy(language = code) }
    fun setTheme(mode: ThemeMode) = edit { it.copy(themeMode = mode) }
    fun setBackground(id: String) = edit { it.copy(background = com.yalnizfahrettin.azim.data.AnaTemalar.allowed(id, it.pro).id) }
    fun setHaptics(enabled: Boolean) = edit { it.copy(haptics = enabled) }
    fun setPro(enabled: Boolean) = edit(reminders = true, widget = true) { UserActions.setPro(it, enabled) }

    fun toggleTopic(topic: String) = edit(reminders = true, widget = true) { UserActions.toggleTopic(it, topic) }
    fun toggleFavorite(id: String) = edit { UserActions.toggleFavorite(it, id) }
    fun restoreFavorite(id: String, index: Int) = edit { UserActions.restoreFavorite(it, id, index) }
    fun setHidden(id: String, hidden: Boolean) = edit(widget = true) { UserActions.setHidden(it, id, hidden) }
    fun restoreHidden() = edit(widget = true) { it.copy(hidden = emptySet()) }
    fun markRead(id: String) = edit { UserActions.markRead(it, id, LocalDate.now()) }

    fun updateReminders(transform: (Reminders) -> Reminders) =
        edit(reminders = true) { it.copy(reminders = transform(it.reminders).normalized()) }

    fun pauseToday(paused: Boolean) = updateReminders { r ->
        val midnight = LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        r.copy(pausedUntil = if (paused) midnight else 0L)
    }

    fun startSeries(id: String) = edit { UserActions.startSeries(it, id) }
    fun completeSeriesDay(id: String) = edit { UserActions.completeSeriesDay(it, id, LocalDate.now()) }

    /** Sends one reminder now from the user's own pool. */
    fun sendTest(done: (Boolean) -> Unit) {
        viewModelScope.launch {
            val s = store.current()
            val pick = QuotePicker.pick(s.copy(reminders = s.reminders.copy(surprise = false)))
            val ok = pick != null && Notifier.show(getApplication(), pick, s)
            if (ok) store.update { UserActions.recordDelivery(it, pick!!.quote.kimlik, false, System.currentTimeMillis(), LocalDate.now()) }
            done(ok)
        }
    }
}
