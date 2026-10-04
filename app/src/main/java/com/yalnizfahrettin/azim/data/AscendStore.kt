package com.yalnizfahrettin.azim.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.ascendStore by preferencesDataStore("ascend")

/** Single persistence gateway. Every change is one atomic read-modify-write of [UserState]. */
class AscendStore private constructor(
    private val store: DataStore<Preferences>,
    private val defaultLanguage: String,
) {
    val state: Flow<UserState> = store.data.map { UserStateCodec.decode(it, defaultLanguage) }.distinctUntilChanged()

    suspend fun current(): UserState = state.first()

    suspend fun update(transform: (UserState) -> UserState): UserState {
        var result = UserState()
        store.edit { prefs ->
            result = transform(UserStateCodec.decode(prefs, defaultLanguage))
            UserStateCodec.encode(result, prefs)
        }
        return result
    }

    companion object {
        @Volatile private var instance: AscendStore? = null
        fun get(context: Context): AscendStore = instance ?: synchronized(this) {
            instance ?: AscendStore(
                context.applicationContext.ascendStore,
                Defaults.languageFor(context.resources.configuration.locales[0]),
            ).also { instance = it }
        }
    }
}

/** Pure mapping between preferences and the model, so it is testable on the JVM. */
object UserStateCodec {
    private val ONBOARDED = booleanPreferencesKey("onboarded")
    private val LANGUAGE = stringPreferencesKey("language")
    private val THEME = stringPreferencesKey("theme_mode")
    private val BACKGROUND = stringPreferencesKey("background")
    private val HAPTICS = booleanPreferencesKey("haptics")
    private val PRO = booleanPreferencesKey("pro_demo")
    private val TOPICS = stringSetPreferencesKey("topics")
    private val R_ENABLED = booleanPreferencesKey("reminders_enabled")
    private val R_PER_DAY = intPreferencesKey("reminders_per_day")
    private val R_START = intPreferencesKey("reminders_start")
    private val R_END = intPreferencesKey("reminders_end")
    private val R_SURPRISE = booleanPreferencesKey("reminders_surprise")
    private val R_PAUSED = longPreferencesKey("reminders_paused_until")
    private val FAVORITES = stringPreferencesKey("favorites")
    private val HIDDEN = stringSetPreferencesKey("hidden")
    private val NOTIFIED = stringPreferencesKey("notified")
    private val RECENT = stringPreferencesKey("recent")
    private val DELIVERIES = stringPreferencesKey("deliveries")
    private val LAST_DELIVERY = longPreferencesKey("last_delivery_at")
    private val STREAK = intPreferencesKey("streak")
    private val BEST = intPreferencesKey("best_streak")
    private val LAST_ACTIVE = stringPreferencesKey("last_active_day")
    private val READ_TODAY = intPreferencesKey("read_today")
    private val READ_DAY = stringPreferencesKey("read_day")
    private val TOTAL_READ = intPreferencesKey("total_read")
    private val SERIES = stringSetPreferencesKey("series")

    const val NOTIFIED_LIMIT = 400
    const val RECENT_LIMIT = 300
    const val DELIVERY_DAYS = 30L

    private fun list(raw: String?): List<String> = raw.orEmpty().split('|').filter(String::isNotBlank)
    private fun day(raw: String?): LocalDate? = raw?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    fun decode(p: Preferences, defaultLanguage: String = "en") = UserState(
        onboarded = p[ONBOARDED] ?: false,
        language = p[LANGUAGE]?.takeIf { it in Languages.codes } ?: defaultLanguage,
        themeMode = runCatching { ThemeMode.valueOf(p[THEME] ?: "") }.getOrDefault(ThemeMode.SYSTEM),
        background = p[BACKGROUND] ?: AnaTemalar.white.id,
        haptics = p[HAPTICS] ?: true,
        pro = p[PRO] ?: false,
        topics = p[TOPICS].orEmpty().filter { Kategoriler.bul(it) != null }.toSet(),
        reminders = Reminders(
            enabled = p[R_ENABLED] ?: true,
            perDay = p[R_PER_DAY] ?: Defaults.PER_DAY,
            startHour = p[R_START] ?: Defaults.START_HOUR,
            endHour = p[R_END] ?: Defaults.END_HOUR,
            surprise = p[R_SURPRISE] ?: true,
            pausedUntil = p[R_PAUSED] ?: 0L,
        ).normalized(),
        favorites = list(p[FAVORITES]).filter(Sozler::aktifKimlikMi).distinct(),
        hidden = p[HIDDEN].orEmpty(),
        notified = list(p[NOTIFIED]),
        recent = list(p[RECENT]),
        deliveries = p[DELIVERIES].orEmpty().split(';').filter(String::isNotBlank).mapNotNull(Delivery::decode),
        lastDeliveryAt = p[LAST_DELIVERY] ?: 0L,
        streak = p[STREAK] ?: 0,
        bestStreak = p[BEST] ?: 0,
        lastActiveDay = day(p[LAST_ACTIVE]),
        readToday = p[READ_TODAY] ?: 0,
        readDay = day(p[READ_DAY]),
        totalRead = p[TOTAL_READ] ?: 0,
        series = p[SERIES].orEmpty().mapNotNull(SeriesProgress::decode).associateBy { it.id },
    )

    fun encode(s: UserState, p: MutablePreferences) {
        p[ONBOARDED] = s.onboarded
        p[LANGUAGE] = s.language
        p[THEME] = s.themeMode.name
        p[BACKGROUND] = s.background
        p[HAPTICS] = s.haptics
        p[PRO] = s.pro
        p[TOPICS] = s.topics
        val r = s.reminders.normalized()
        p[R_ENABLED] = r.enabled
        p[R_PER_DAY] = r.perDay
        p[R_START] = r.startHour
        p[R_END] = r.endHour
        p[R_SURPRISE] = r.surprise
        p[R_PAUSED] = r.pausedUntil
        p[FAVORITES] = s.favorites.distinct().joinToString("|")
        p[HIDDEN] = s.hidden
        p[NOTIFIED] = s.notified.take(NOTIFIED_LIMIT).joinToString("|")
        p[RECENT] = s.recent.take(RECENT_LIMIT).joinToString("|")
        val oldest = (s.deliveries.firstOrNull()?.day ?: LocalDate.now()).minusDays(DELIVERY_DAYS - 1)
        p[DELIVERIES] = s.deliveries.filter { !it.day.isBefore(oldest) }.joinToString(";") { it.encode() }
        p[LAST_DELIVERY] = s.lastDeliveryAt
        p[STREAK] = s.streak
        p[BEST] = s.bestStreak
        s.lastActiveDay?.let { p[LAST_ACTIVE] = it.toString() } ?: p.remove(LAST_ACTIVE)
        p[READ_TODAY] = s.readToday
        s.readDay?.let { p[READ_DAY] = it.toString() } ?: p.remove(READ_DAY)
        p[TOTAL_READ] = s.totalRead
        p[SERIES] = s.series.values.map { it.encode() }.toSet()
    }
}
