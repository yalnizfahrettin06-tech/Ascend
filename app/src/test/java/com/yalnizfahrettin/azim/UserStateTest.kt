package com.yalnizfahrettin.azim

import androidx.datastore.preferences.core.mutablePreferencesOf
import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class UserStateTest {
    private val today = LocalDate.of(2026, 10, 4)
    private val quote = Sozler.kategoriden("motivasyon").first().kimlik

    @Test fun onboardingKeepsOnlyAccessibleChoices() {
        val s = UserActions.completeOnboarding(UserState(), "tr", setOf("motivasyon", "kariyer"), Reminders(perDay = 9, startHour = 22, endHour = 5))
        assertTrue(s.onboarded)
        assertEquals(setOf("motivasyon"), s.topics)
        assertEquals(7, s.reminders.perDay)
        assertEquals(23, s.reminders.endHour)
    }

    @Test fun topicToggleKeepsAtLeastOneAndRespectsAccess() {
        var s = UserState(topics = setOf("motivasyon"))
        s = UserActions.toggleTopic(s, "motivasyon")
        assertEquals(setOf("motivasyon"), s.topics)
        s = UserActions.toggleTopic(s, "kariyer")
        assertEquals(setOf("motivasyon"), s.topics)
        s = UserActions.toggleTopic(s, "azim")
        assertEquals(setOf("motivasyon", "azim"), s.topics)
        s = UserActions.toggleTopic(s.copy(pro = true), "kariyer")
        assertTrue("kariyer" in s.topics)
    }

    @Test fun notificationSaveNeverRemoves() {
        val saved = UserActions.addFavorite(UserState(), quote)
        assertEquals(listOf(quote), UserActions.addFavorite(saved, quote).favorites)
        assertTrue(UserActions.toggleFavorite(saved, quote).favorites.isEmpty())
    }

    @Test fun restoreFavoriteReturnsToItsPlace() {
        val ids = Sozler.kategoriden("azim").take(3).map { it.kimlik }
        val s = UserState(favorites = ids)
        val removed = UserActions.toggleFavorite(s, ids[1])
        assertEquals(ids, UserActions.restoreFavorite(removed, ids[1], 1).favorites)
    }

    @Test fun readingCountsOncePerQuoteAndDay() {
        var s = UserActions.markRead(UserState(), quote, today)
        s = UserActions.markRead(s, quote, today)
        assertEquals(1, s.readToday)
        assertEquals(1, s.totalRead)
        s = UserActions.markRead(s, Sozler.kategoriden("azim").first().kimlik, today)
        s = UserActions.markRead(s, quote, today)
        assertEquals(2, s.readToday)
        s = UserActions.markRead(s, quote, today.plusDays(1))
        assertEquals(1, s.readToday)
        assertEquals(3, s.totalRead)
    }

    @Test fun streakGrowsDailyAndResetsAfterAGap() {
        var s = UserActions.markActive(UserState(), today)
        s = UserActions.markActive(s, today)
        s = UserActions.markActive(s, today.plusDays(1))
        assertEquals(2, s.streak)
        assertEquals(2, s.currentStreak(today.plusDays(2)))
        assertEquals(0, s.currentStreak(today.plusDays(3)))
        s = UserActions.markActive(s, today.plusDays(4))
        assertEquals(1, s.streak)
        assertEquals(2, s.bestStreak)
    }

    @Test fun disablingProDropsPaidBackgroundsButKeepsChoices() {
        val s = UserState(pro = true, topics = setOf("kariyer", "azim"), background = AnaTemalar.emperor.id)
        val off = UserActions.setPro(s, false)
        assertEquals(AnaTemalar.black.id, off.background)
        assertEquals(setOf("azim"), off.activeTopics)
        assertEquals(s.topics, off.topics)
    }

    @Test fun codecRoundTripsTheWholeModel() {
        val original = UserState(
            onboarded = true, language = "tr", themeMode = ThemeMode.DARK, background = AnaTemalar.black.id, haptics = false,
            pro = true, topics = setOf("motivasyon", "kariyer"), reminders = Reminders(true, 5, 7, 23, false, 123L),
            favorites = listOf(quote), hidden = setOf("x"), notified = listOf(quote), recent = listOf(quote),
            deliveries = listOf(Delivery(today, quote, true)), lastDeliveryAt = 42L, streak = 3, bestStreak = 4,
            lastActiveDay = today, readToday = 2, readDay = today, totalRead = 9,
            series = mapOf("steps" to SeriesProgress("steps", 2, today)),
        )
        val prefs = mutablePreferencesOf()
        UserStateCodec.encode(original, prefs)
        assertEquals(original, UserStateCodec.decode(prefs))
    }

    @Test fun emptyStoreUsesDeviceLanguageAndSafeDefaults() {
        val s = UserStateCodec.decode(mutablePreferencesOf(), "tr")
        assertFalse(s.onboarded)
        assertEquals("tr", s.language)
        assertEquals(ThemeMode.SYSTEM, s.themeMode)
        assertEquals(Reminders(), s.reminders)
    }

    @Test fun freeLibraryIsMeaningfulAndValid() {
        assertTrue(Access.free.size >= 20)
        assertTrue(Access.free.all { Kategoriler.bul(it) != null && Sozler.kategoriden(it).isNotEmpty() })
        assertTrue(Defaults.STARTER_TOPICS.all { it in Access.free })
    }

    @Test fun everyFreeTopicHasAtLeastThirtyTexts() {
        Access.free.forEach { topic -> assertTrue(topic, Sozler.kategoriden(topic).size >= 30) }
    }

    @Test fun classicQuotesCarryTheirSource() {
        val classics = Sozler.tumu().filter { it.gercekAlinti }
        assertTrue(classics.size >= 40)
        classics.forEach { q ->
            assertTrue(q.kimlik.startsWith("pd_"))
            assertTrue(q.imza("tr").contains(q.eser!!.yazar))
            assertNotNull(q.ceviriNotu("tr"))
        }
        assertEquals("Marcus Aurelius · Meditations", Sozler.kimlikten("pd_marcus_01")!!.imza("en"))
    }

    @Test fun untranslatedQuotesStayOutOfOtherLanguages() {
        val s = UserState(onboarded = true, language = "de", topics = setOf("motivasyon"))
        val pool = QuotePicker.topicPool(s)
        assertTrue(pool.isNotEmpty())
        assertTrue(pool.all { it.mevcut("de") })
        assertTrue(QuotePicker.topicPool(s.copy(language = "tr")).size > pool.size || pool.size >= 30)
    }
}
