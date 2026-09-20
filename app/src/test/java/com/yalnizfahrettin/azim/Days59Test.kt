package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class Days59Test {
    @Test fun newSeriesHasSevenDistinctCompleteChaptersInEveryLanguage() {
        val series = ShortSeries.all.first { it.id == "discipline" }
        assertTrue(series.pro)
        assertEquals(7,series.quotes.size)
        JourneyCopy.languages.forEach { lang ->
            val days = series.days(lang)
            assertEquals(7,days.size)
            assertEquals(7,days.map { it.title }.toSet().size)
            assertEquals(7,days.map { it.story }.toSet().size)
            assertEquals(7,days.map { it.step }.toSet().size)
            days.forEach { assertTrue(it.story.length > 100); assertTrue(it.step.length > 40) }
            assertTrue(days.none { it.story in RestartSeries.days(lang).map { d -> d.story } })
            if(lang != "en") assertNotEquals(series.days("en"),days)
        }
    }
    @Test fun missedDaysAndDuplicateCompletionDoNotPenalizeOrAdvanceTwice() {
        val today = LocalDate.of(2026,9,20)
        val first = SeriesProgress("discipline").complete(today)
        assertEquals(first,first.complete(today))
        assertEquals(first,first.complete(today.minusDays(1)))
        assertEquals(2,first.complete(today.plusDays(12)).completed)
        assertEquals(first,SeriesProgress.decode(first.encode()))
    }
    @Test fun everyNewUiKeyCoversEveryShippedLanguage() {
        assertEquals(Diller.kodlar.toSet(),JourneyCopy.languages.toSet())
        JourneyCopy.entries.forEach { (key, values) ->
            assertEquals(key,7,values.size)
            values.forEach { assertTrue(key,it.isNotBlank()); assertFalse(key,it.contains("TODO")) }
        }
        Kategoriler.dusunurler.forEach { thinker -> JourneyCopy.languages.forEach { assertTrue(JourneyCopy.thinker(thinker.anahtar,it).isNotBlank()) } }
    }
}
