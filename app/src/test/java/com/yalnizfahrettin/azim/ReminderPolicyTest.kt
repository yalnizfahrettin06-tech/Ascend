package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

class ReminderPolicyTest {
    private val zone = ZoneId.of("Europe/Istanbul")
    private val chosen = setOf("motivasyon", "ozsefkat")
    private fun state(surprise: Boolean = true, topics: Set<String> = chosen) =
        UserState(onboarded = true, topics = topics, reminders = Reminders(surprise = surprise))

    @Test fun slotsAreEvenMidpointsInsideTheWindow() {
        val slots = ReminderPlan.slots(LocalDate.of(2026, 10, 4), Reminders(perDay = 3, startHour = 9, endHour = 21), zone)
        assertEquals(listOf("11:00", "15:00", "19:00"), slots.map { "%02d:%02d".format(it.hour, it.minute) })
    }

    @Test fun nextSlotSkipsPastSlotsAndRollsToTomorrow() {
        val r = Reminders(perDay = 2, startHour = 8, endHour = 20)
        val evening = ZonedDateTime.of(2026, 10, 4, 18, 0, 0, 0, zone)
        val next = ReminderPlan.next(evening, r)!!
        assertEquals(LocalDate.of(2026, 10, 5), next.toLocalDate())
        assertEquals(11, next.hour)
    }

    @Test fun pauseMovesTheNextSlotPastTheGivenTime() {
        val morning = ZonedDateTime.of(2026, 10, 4, 7, 0, 0, 0, zone)
        val midnight = LocalDate.of(2026, 10, 5).atStartOfDay(zone).toInstant().toEpochMilli()
        val next = ReminderPlan.next(morning, Reminders(pausedUntil = midnight))!!
        assertEquals(LocalDate.of(2026, 10, 5), next.toLocalDate())
    }

    @Test fun disabledRemindersHaveNoNextSlot() {
        assertNull(ReminderPlan.next(ZonedDateTime.now(zone), Reminders(enabled = false)))
    }

    @Test fun slotsSurviveDaylightSavingChanges() {
        val newYork = ZoneId.of("America/New_York")
        val slots = ReminderPlan.slots(LocalDate.of(2026, 3, 8), Reminders(perDay = 1, startHour = 9, endHour = 21), newYork)
        assertEquals(15, slots.single().hour)
    }

    @Test fun lateDeliveriesAreSkippedAfterThreeHours() {
        val r = Reminders()
        assertTrue(ReminderPlan.shouldDeliver(1_000_000L + 60_000L, 1_000_000L, r))
        assertFalse(ReminderPlan.shouldDeliver(1_000_000L + ReminderPlan.MAX_LATENESS_MS + 1, 1_000_000L, r))
        assertFalse(ReminderPlan.shouldDeliver(2_000_000L, 1_000_000L, r.copy(enabled = false)))
    }

    @Test fun withoutSurpriseEveryReminderComesFromChosenTopics() {
        val s = state(surprise = false)
        repeat(300) { seed -> assertTrue(QuotePicker.pick(s, Random(seed))!!.quote.kategori in chosen) }
    }

    @Test fun surpriseIsRareAndNeverFromChosenOrLockedTopics() {
        val s = state()
        val picks = (0 until 2000).map { QuotePicker.pick(s, Random(it))!! }
        val surprises = picks.filter { it.surprise }
        val rate = surprises.size / 2000.0
        assertTrue("surprise rate $rate", rate in 0.10..0.20)
        assertTrue(picks.filterNot { it.surprise }.all { it.quote.kategori in chosen })
        assertTrue(surprises.all { it.quote.kategori !in chosen && it.quote.kategori in Access.free })
    }

    @Test fun surpriseRespectsPersonalAndSensitiveTopics() {
        val pool = QuotePicker.surprisePool(state().copy(pro = true))
        val groups = pool.mapNotNull { Kategoriler.bul(it.kategori)?.grup }.toSet()
        assertFalse("inanc" in groups)
        assertFalse("tasavvuf" in groups)
        assertTrue(pool.none { it.kategori == "ayrilik" })
    }

    @Test fun hiddenQuotesNeverArrive() {
        val hidden = Sozler.kategoriden("motivasyon").map { it.kimlik }.toSet()
        val s = state(surprise = false, topics = setOf("motivasyon", "ozsefkat")).copy(hidden = hidden)
        repeat(200) { assertTrue(QuotePicker.pick(s, Random(it))!!.quote.kimlik !in hidden) }
    }

    @Test fun rotationVisitsTheWholePoolBeforeRepeating() {
        var s = state(surprise = false, topics = setOf("motivasyon"))
        val pool = Sozler.kategoriden("motivasyon").size
        val sent = mutableListOf<String>()
        repeat(pool) { i ->
            val pick = QuotePicker.pick(s, Random(i))!!
            sent += pick.quote.kimlik
            s = UserActions.recordDelivery(s, pick.quote.kimlik, false, i.toLong(), LocalDate.of(2026, 10, 4))
        }
        assertEquals(pool, sent.toSet().size)
        val next = QuotePicker.pick(s, Random(99))!!.quote.kimlik
        assertFalse("just sent quotes cool down", next in s.notified.take(8))
    }

    @Test fun lockedTopicsFallBackToAccessibleStarters() {
        val s = UserState(onboarded = true, topics = setOf("kariyer"))
        assertTrue(s.activeTopics.isNotEmpty())
        assertTrue(s.activeTopics.all { it in Access.free })
    }

    @Test fun homeFeedShowsOnlyChosenTopicsUnreadFirst() {
        val read = Sozler.kategoriden("motivasyon").first().kimlik
        val s = state().copy(recent = listOf(read))
        val feed = HomeFeed.build(s, LocalDate.of(2026, 10, 4))
        assertTrue(feed.all { it.kategori in chosen })
        assertEquals(read, feed.last().kimlik)
        assertEquals(feed, HomeFeed.build(s, LocalDate.of(2026, 10, 4)))
    }
}
