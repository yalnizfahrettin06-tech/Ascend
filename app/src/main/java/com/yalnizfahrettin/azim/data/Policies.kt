package com.yalnizfahrettin.azim.data

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

/** When reminders fire: evenly spaced midpoints within the chosen window, computed per zone. */
object ReminderPlan {
    /** A delivery this much later than its slot is skipped instead of arriving out of context. */
    const val MAX_LATENESS_MS = 3 * 60 * 60_000L

    fun slots(day: LocalDate, r: Reminders, zone: ZoneId): List<ZonedDateTime> {
        val n = r.normalized()
        // Wall-clock arithmetic first, then the zone: a daylight-saving night must not shift every slot.
        val start = day.atStartOfDay().plusHours(n.startHour.toLong())
        val minutes = (n.endHour - n.startHour) * 60L
        return (0 until n.perDay).map { start.plusMinutes(minutes * (2 * it + 1) / (2 * n.perDay)).atZone(zone) }
    }

    /** The next slot strictly after [now] that is not inside a pause; looks ahead two days. */
    fun next(now: ZonedDateTime, r: Reminders): ZonedDateTime? {
        if (!r.enabled) return null
        return (0L..2L).asSequence()
            .flatMap { slots(now.toLocalDate().plusDays(it), r, now.zone).asSequence() }
            .firstOrNull { it.isAfter(now) && !r.isPaused(it.toInstant().toEpochMilli()) }
    }

    fun upcomingToday(now: ZonedDateTime, r: Reminders): List<ZonedDateTime> =
        if (!r.enabled) emptyList() else slots(now.toLocalDate(), r, now.zone)
            .filter { it.isAfter(now) && !r.isPaused(it.toInstant().toEpochMilli()) }

    fun shouldDeliver(now: Long, scheduledFor: Long, r: Reminders): Boolean =
        r.enabled && !r.isPaused(now) && now >= scheduledFor - 60_000L && now - scheduledFor <= MAX_LATENESS_MS
}

data class Pick(val quote: Soz, val surprise: Boolean)

/** What reminders say: the chosen topics, with an occasional surprise from other unlocked topics. */
object QuotePicker {
    /** Groups that only enter a mix when the user picked something from them. */
    private val personalGroups = setOf("inanc", "tasavvuf")
    /** Topics that can hurt when they arrive unasked. */
    private val sensitiveTopics = setOf("ask", "ayrilik", "vedalar", "sakatlik", "pismanlik")
    private const val COOLDOWN = 8

    fun topicPool(s: UserState): List<Soz> = s.activeTopics.flatMap(Sozler::kategoriden)
        .filter { it.kimlik !in s.hidden && it.mevcut(s.language) }

    fun surprisePool(s: UserState): List<Soz> {
        val chosenGroups = s.activeTopics.mapNotNull { Kategoriler.bul(it)?.grup }.toSet()
        val topics = s.access - s.activeTopics - sensitiveTopics
        return Sozler.tumu().filter { q ->
            q.kategori in topics && q.kimlik !in s.hidden && q.mevcut(s.language) &&
                Kategoriler.bul(q.kategori)?.grup.let { it !in personalGroups || it in chosenGroups }
        }
    }

    fun pick(s: UserState, random: Random = Random.Default): Pick? {
        val base = topicPool(s)
        val surprise = if (s.reminders.surprise && random.nextDouble() < Defaults.SURPRISE_RATE) surprisePool(s) else emptyList()
        val (pool, isSurprise) = if (surprise.isNotEmpty()) surprise to true else base to false
        if (pool.isEmpty()) return null
        return Pick(leastRecent(pool, s.notified, random), isSurprise)
    }

    /** Never-sent quotes first; otherwise the older half, excluding the last few sends. */
    fun leastRecent(pool: List<Soz>, notified: List<String>, random: Random): Soz {
        val rank = notified.withIndex().associate { it.value to it.index }
        val fresh = pool.filter { it.kimlik !in rank }
        if (fresh.isNotEmpty()) return fresh.random(random)
        val cooled = pool.filter { rank.getValue(it.kimlik) >= COOLDOWN }.ifEmpty { pool }
        val oldestFirst = cooled.sortedByDescending { rank.getValue(it.kimlik) }
        return oldestFirst.take((oldestFirst.size + 1) / 2).random(random)
    }
}

/** What Today shows: the chosen topics, unread quotes first, in a stable daily order. */
object HomeFeed {
    fun build(s: UserState, today: LocalDate): List<Soz> {
        val pool = QuotePicker.topicPool(s)
        val recentRank = s.recent.withIndex().associate { it.value to it.index }
        val random = Random(today.toEpochDay() * 31 + s.activeTopics.sorted().hashCode())
        val (read, unread) = pool.partition { it.kimlik in recentRank }
        return unread.shuffled(random) + read.sortedByDescending { recentRank.getValue(it.kimlik) }
    }
}
