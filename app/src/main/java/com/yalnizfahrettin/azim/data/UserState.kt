package com.yalnizfahrettin.azim.data

import java.time.LocalDate

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Reminder rhythm. Hours are local wall-clock hours; [endHour] may be 24 (midnight). */
data class Reminders(
    val enabled: Boolean = true,
    val perDay: Int = Defaults.PER_DAY,
    val startHour: Int = Defaults.START_HOUR,
    val endHour: Int = Defaults.END_HOUR,
    val surprise: Boolean = true,
    val pausedUntil: Long = 0L,
) {
    fun normalized(): Reminders {
        val start = startHour.coerceIn(0, 23)
        return copy(perDay = perDay.coerceIn(1, Defaults.MAX_PER_DAY), startHour = start, endHour = endHour.coerceIn(start + 1, 24))
    }
    fun isPaused(now: Long) = pausedUntil > now
}

/** One reminder that Android accepted. */
data class Delivery(val day: LocalDate, val quoteId: String, val surprise: Boolean) {
    fun encode() = "$day|$quoteId|${if (surprise) 1 else 0}"
    companion object {
        fun decode(raw: String): Delivery? = runCatching {
            val p = raw.split('|')
            Delivery(LocalDate.parse(p[0]), p[1], p.getOrNull(2) == "1")
        }.getOrNull()
    }
}

/** The whole persisted user model. Screens, reminders and widgets all read this one snapshot. */
data class UserState(
    val onboarded: Boolean = false,
    val language: String = "en",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val background: String = AnaTemalar.white.id,
    val haptics: Boolean = true,
    val pro: Boolean = false,
    val topics: Set<String> = emptySet(),
    val reminders: Reminders = Reminders(),
    /** Newest first. */
    val favorites: List<String> = emptyList(),
    val hidden: Set<String> = emptySet(),
    /** Reminder memory, newest first; drives rotation through the pool. */
    val notified: List<String> = emptyList(),
    /** Quotes read in the app, newest first. */
    val recent: List<String> = emptyList(),
    /** Newest first, last 30 days. */
    val deliveries: List<Delivery> = emptyList(),
    val lastDeliveryAt: Long = 0L,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDay: LocalDate? = null,
    val readToday: Int = 0,
    val readDay: LocalDate? = null,
    val totalRead: Int = 0,
    val series: Map<String, SeriesProgress> = emptyMap(),
) {
    val access: Set<String> get() = Access.unlocked(pro)

    /** Topics the user chose and can currently access; never empty after onboarding. */
    val activeTopics: Set<String> get() = topics.intersect(access).ifEmpty { Defaults.STARTER_TOPICS.intersect(access) }

    fun readCountOn(day: LocalDate) = if (readDay == day) readToday else 0

    /** A streak only counts while today or yesterday was active. */
    fun currentStreak(today: LocalDate): Int = when (lastActiveDay) {
        today, today.minusDays(1) -> streak
        else -> 0
    }

    fun deliveriesByDay(): Map<LocalDate, List<Delivery>> = deliveries.groupBy { it.day }
}

/** Storage limits shared by the codec and state transitions. */
object Limits {
    const val NOTIFIED = 400
    const val RECENT = 300
    const val DELIVERY_DAYS = 30L
}

object Defaults {
    const val PER_DAY = 3
    const val MAX_PER_DAY = 7
    const val START_HOUR = 9
    const val END_HOUR = 21
    const val MIN_TOPICS = 1
    const val MAX_TOPICS_SETUP = 5
    const val SURPRISE_RATE = 0.15
    val STARTER_TOPICS = setOf("motivasyon", "ozsefkat", "derin_odak")

    fun languageFor(locale: java.util.Locale): String = locale.language.takeIf { it in Languages.codes } ?: "en"
}
