package com.yalnizfahrettin.azim.data

import java.time.LocalDate

/** Pure state transitions. Persistence wraps these in one atomic store update. */
object UserActions {

    fun completeOnboarding(s: UserState, language: String, topics: Set<String>, reminders: Reminders): UserState {
        val chosen = topics.intersect(s.access).ifEmpty { Defaults.STARTER_TOPICS }
        return s.copy(onboarded = true, language = language, topics = chosen, reminders = reminders.normalized())
    }

    /** Adds an accessible topic, or removes it while at least one topic stays selected. */
    fun toggleTopic(s: UserState, topic: String): UserState {
        if (Kategoriler.bul(topic) == null) return s
        val current = s.activeTopics
        return when {
            topic in current && current.size > Defaults.MIN_TOPICS -> s.copy(topics = current - topic)
            topic in current -> s
            topic in s.access -> s.copy(topics = current + topic)
            else -> s
        }
    }

    fun toggleFavorite(s: UserState, id: String): UserState =
        if (id in s.favorites) s.copy(favorites = s.favorites - id) else addFavorite(s, id)

    /** Notification action: saving twice never removes the quote. */
    fun addFavorite(s: UserState, id: String): UserState =
        if (id in s.favorites || !Sozler.aktifKimlikMi(id)) s else s.copy(favorites = listOf(id) + s.favorites)

    fun restoreFavorite(s: UserState, id: String, index: Int): UserState {
        val rest = s.favorites - id
        return s.copy(favorites = rest.toMutableList().apply { add(index.coerceIn(0, rest.size), id) })
    }

    fun setHidden(s: UserState, id: String, hidden: Boolean) =
        s.copy(hidden = if (hidden) s.hidden + id else s.hidden - id)

    /** Counts a reading once per quote and day, and keeps the daily streak. */
    fun markRead(s: UserState, id: String, today: LocalDate): UserState {
        val active = markActive(s, today)
        // Today's reads are exactly the first [readToday] entries of the recency list.
        val alreadyToday = active.readDay == today && id in active.recent.take(active.readToday)
        val recent = (listOf(id) + active.recent.filterNot { it == id }).take(UserStateCodec.RECENT_LIMIT)
        if (alreadyToday) return active.copy(recent = recent)
        val todayCount = if (active.readDay == today) active.readToday + 1 else 1
        return active.copy(recent = recent, readToday = todayCount, readDay = today, totalRead = active.totalRead + 1)
    }

    fun markActive(s: UserState, today: LocalDate): UserState {
        val streak = when (s.lastActiveDay) {
            today -> s.streak.coerceAtLeast(1)
            today.minusDays(1) -> s.streak + 1
            else -> 1
        }
        return s.copy(streak = streak, bestStreak = maxOf(s.bestStreak, streak), lastActiveDay = today)
    }

    fun recordDelivery(s: UserState, id: String, surprise: Boolean, now: Long, today: LocalDate): UserState = s.copy(
        notified = (listOf(id) + s.notified.filterNot { it == id }).take(UserStateCodec.NOTIFIED_LIMIT),
        deliveries = listOf(Delivery(today, id, surprise)) +
            s.deliveries.filter { !it.day.isBefore(today.minusDays(UserStateCodec.DELIVERY_DAYS - 1)) },
        lastDeliveryAt = now,
    )

    fun setPro(s: UserState, enabled: Boolean): UserState {
        val next = s.copy(pro = enabled)
        val background = AnaTemalar.allowed(next.background, enabled).id
        return next.copy(background = background)
    }

    fun startSeries(s: UserState, id: String): UserState {
        val series = ShortSeries.all.firstOrNull { it.id == id } ?: return s
        if (series.pro && !s.pro || id in s.series) return s
        return s.copy(series = s.series + (id to SeriesProgress(id)))
    }

    fun completeSeriesDay(s: UserState, id: String, today: LocalDate): UserState {
        val series = ShortSeries.all.firstOrNull { it.id == id } ?: return s
        if (series.pro && !s.pro) return s
        val progress = (s.series[id] ?: SeriesProgress(id)).complete(today)
        return s.copy(series = s.series + (id to progress))
    }
}
