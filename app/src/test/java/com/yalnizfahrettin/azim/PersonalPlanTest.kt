package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class PersonalPlanTest {
    @Test fun quickStartKeepsAStableMixedStarterSetAndExplicitGoalsWinTies() {
        assertEquals(listOf("motivasyon", "ozsefkat", "marcus"), PersonalPlan.recommendedCategories(PersonalProfile()).take(3))
        assertEquals(Kategoriler.varsayilanSecili, PersonalPlan.initialCategories(PersonalProfile(), Erisim.ucretsizKategoriler))
        val calm = PersonalPlan.recommendedCategories(PersonalProfile().choose("goal", "calm"))
        assertTrue(calm.indexOf("ic_huzur") < calm.indexOf("motivasyon"))
    }
    @Test fun profileRoundTripPreservesUnicodeSpecialCharactersAndAllAnswers() {
        val profile = PersonalProfile(name = "İrem & Kai = + 雪", step = 12, dailyCount = 7, startHour = 0, endHour = 24)
            .choose("context", "work", true).choose("context", "study", true).choose("format", "reflection")
        assertEquals(profile, PersonalProfile.decode(profile.encode()))
        assertEquals(PersonalProfile(), PersonalProfile.decode("%illegal%"))
    }
    @Test fun incompleteOrOutOfRangeDataIsSafeWithoutChangingPermissions() {
        val profile = PersonalProfile.decode("step=800&count=99&start=30&end=0")
        assertEquals(19, profile.step); assertEquals(7, profile.dailyCount)
        assertEquals(23, profile.startHour); assertEquals(24, profile.endHour)
        assertEquals(Erisim.ucretsizKategoriler, Erisim.acikKategoriler(emptySet(), false))
    }
    @Test fun eachQuestionAndCategoryReferenceIsValidAndUnique() {
        assertEquals(13, PersonalPlan.questions.size)
        assertEquals(13, PersonalPlan.questions.map { it.id }.toSet().size)
        PersonalPlan.questions.forEach { question ->
            assertEquals(question.options.size, question.options.map { it.id }.toSet().size)
            question.options.flatMap { it.categories }.forEach { assertNotNull("${question.id}: $it", Kategoriler.bul(it)) }
        }
    }
    @Test fun goalsChangeRealRecommendationWeights() {
        val action = PersonalPlan.weights(PersonalProfile().choose("goal", "action"))
        val calm = PersonalPlan.weights(PersonalProfile().choose("goal", "calm"))
        assertTrue(action.getValue("motivasyon") > calm.getValue("motivasyon"))
        assertTrue(calm.getValue("ic_huzur") > action.getValue("ic_huzur"))
        assertNotEquals(PersonalPlan.recommendedCategories(PersonalProfile().choose("goal", "action")).take(3),
            PersonalPlan.recommendedCategories(PersonalProfile().choose("goal", "calm")).take(3))
    }
    @Test fun everyDirectionalAnswerChangesCategoryWeights() {
        val default = PersonalPlan.weights(PersonalProfile())
        PersonalPlan.questions.filter { it.id in setOf("goal", "energy", "tone", "challenge", "momentum", "context", "values", "inspiration") }
            .forEach { question -> question.options.forEach { option ->
                assertNotEquals("${question.id}/${option.id} must be used", default, PersonalPlan.weights(PersonalProfile().choose(question.id, option.id)))
            } }
    }
    @Test fun contentStyleAndExcludedTopicsAreHardFiltersEvenWhenManuallySelected() {
        val profile = PersonalProfile().choose("format", "affirmation").choose("discovery", "wide")
        val feed = PersonalPlan.feed(profile, Erisim.tumKategoriler, Erisim.tumKategoriler)
        assertEquals(30, feed.size)
        assertTrue(feed.all { Kategoriler.bul(it.kategori)?.grup == "olumlamalar" })
        val filtered = PersonalProfile().choose("avoid", "relationships").choose("avoid", "body", true)
        assertFalse(PersonalPlan.effectiveCategories(filtered, setOf("ask", "ayrilik", "beslenme"), Erisim.tumKategoriler)
            .any { it in setOf("ask", "ayrilik", "beslenme") })
    }
    @Test fun spiritualContentRequiresAnExplicitPreference() {
        fun groups(profile: PersonalProfile) = PersonalPlan.recommendedCategories(profile).map { Kategoriler.bul(it)?.grup }.toSet()
        assertFalse("inanc" in groups(PersonalProfile()))
        assertFalse("tasavvuf" in groups(PersonalProfile()))
        assertTrue("tasavvuf" in groups(PersonalProfile().choose("spirituality", "spiritual")))
        assertFalse("inanc" in groups(PersonalProfile().choose("spirituality", "spiritual")))
        assertTrue("inanc" in groups(PersonalProfile().choose("spirituality", "faith")))
    }
    @Test fun discoveryCannotCrossEntitlementsAndCanBeDisabled() {
        val selected = setOf("motivasyon")
        assertEquals(selected, PersonalPlan.effectiveCategories(PersonalProfile().choose("discovery", "none"), selected, Erisim.ucretsizKategoriler))
        val broad = PersonalPlan.effectiveCategories(PersonalProfile().choose("discovery", "wide"), selected, Erisim.ucretsizKategoriler)
        assertTrue(broad.size > 1); assertTrue(Erisim.ucretsizKategoriler.containsAll(broad))
        assertEquals(selected, PersonalPlan.effectiveCategories(null, selected, Erisim.tumKategoriler))
    }
    @Test fun noMatchingContentNeverFallsBackToExcludedOrPaidContent() {
        val profile = PersonalProfile().choose("format", "affirmation")
        assertTrue(PersonalPlan.feed(profile, setOf("motivasyon"), setOf("motivasyon")).isEmpty())
        assertNull(PersonalPlan.notification(profile, setOf("motivasyon"), setOf("motivasyon"), "en", emptySet(), null))
    }
    @Test fun notificationsExhaustEligiblePoolWithoutImmediateBoundaryRepeat() {
        val profile = PersonalProfile().choose("format", "affirmation").choose("discovery", "none").choose("length", "short")
        val selected = setOf("ozsefkat")
        val seen = mutableSetOf<String>()
        var last: String? = null
        repeat(10) {
            val next = PersonalPlan.notification(profile, selected, Erisim.ucretsizKategoriler, "en", seen, last)!!
            assertFalse(next.yeniTur); assertEquals("ozsefkat", next.soz.kategori)
            assertTrue(seen.add(next.soz.kimlik)); last = next.soz.kimlik
        }
        val next = PersonalPlan.notification(profile, selected, Erisim.ucretsizKategoriler, "en", seen, last)!!
        assertTrue(next.yeniTur); assertNotEquals(last, next.soz.kimlik)
    }
    @Test fun seenCardsFollowUnseenAndOriginalStringsNeverContainTheProfileName() {
        val profile = PersonalProfile(name = "THIS_NAME_IS_NOT_QUOTE_CONTENT")
        val seen = Sozler.kategoriden("motivasyon").map { it.kimlik }.toSet()
        val feed = PersonalPlan.feed(profile, setOf("motivasyon", "ozsefkat"), Erisim.ucretsizKategoriler, seen)
        assertEquals(feed.size, feed.map { it.kimlik }.toSet().size)
        val firstSeen = feed.indexOfFirst { it.kimlik in seen }
        assertTrue(firstSeen > 0)
        assertTrue(feed.drop(firstSeen).all { it.kimlik in seen })
        assertTrue(feed.none { profile.name in it.en })
    }
    @Test fun skipReallyRemovesAnEarlierChoice() {
        val profile = PersonalProfile().choose("format", "affirmation").choose("tone", "gentle").skip("format")
        assertTrue(profile.answer("format").isEmpty())
        assertEquals(setOf("gentle"), profile.answer("tone"))
        assertTrue(PersonalPlan.recommendedCategories(profile).contains("motivasyon"))
    }
    @Test fun previewAndFinishedPlanStartWithTheSameWordsRegardlessOfGreetingName() {
        val preview = PersonalProfile(name = "Ada", step = 17).choose("goal", "calm")
        val starters = PersonalPlan.initialCategories(preview, Erisim.ucretsizKategoriler)
        assertEquals(PersonalPlan.feed(preview, starters, Erisim.ucretsizKategoriler).map { it.kimlik },
            PersonalPlan.feed(preview.copy(step = 0, name = "Irem"), starters, Erisim.ucretsizKategoriler).map { it.kimlik })
    }
}
