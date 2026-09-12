package com.yalnizfahrettin.azim
import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
class SeriesAndRepeatTest {
    @Test fun progressOnlyAdvancesOncePerDayAndSurvivesBreaks() {
        val day = LocalDate.of(2026,9,12)
        val first = SeriesProgress("kindness").complete(day)
        assertEquals(1,first.completed)
        assertEquals(first,first.complete(day))
        assertEquals(first,first.complete(day.minusDays(1)))
        assertEquals(2,first.complete(day.plusDays(8)).completed)
        assertEquals(first,SeriesProgress.decode(first.encode()))
        var finished = SeriesProgress("kindness")
        repeat(12) { finished = finished.complete(day.plusDays(it.toLong())) }
        assertEquals(7,finished.completed)
        assertFalse(finished.canComplete(day.plusDays(30)))
    }
    @Test fun malformedProgressIsIgnored() {
        assertNull(SeriesProgress.decode("kindness|9|2026-09-12"))
        assertNull(SeriesProgress.decode("unknown|0|"))
        assertNull(SeriesProgress.decode("kindness|2|"))
    }
    @Test fun seriesHaveSevenDistinctAccessibleQuotesAndBilingualPrompts() {
        ShortSeries.all.forEach { series ->
            assertTrue(series.category in Erisim.ucretsizKategoriler)
            assertEquals(7,series.quotes.size)
            assertEquals(7,series.quotes.map { it.kimlik }.toSet().size)
            assertEquals(7,series.prompts.size)
            series.prompts.forEach { assertTrue(it.first.isNotBlank()); assertTrue(it.second.isNotBlank()) }
        }
    }
    @Test fun exhaustedNotificationPoolAvoidsRecentCycleBoundary() {
        val access = setOf("motivasyon")
        val ids = Sozler.kategoriden("motivasyon").map { it.kimlik }
        repeat(40) {
            val choice = PersonalPlan.notification(null,access,access,"tr",ids.toSet(),ids.first(),recent = ids)!!
            assertTrue(choice.yeniTur)
            assertFalse(choice.soz.kimlik in ids.take(8))
        }
    }
    @Test fun smallPoolsReuseOldestWithoutReturningHiddenOrLastQuote() {
        val access = setOf("motivasyon")
        val ids = Sozler.kategoriden("motivasyon").map { it.kimlik }
        val allowed = ids.take(3)
        repeat(20) {
            val choice = PersonalPlan.notification(null,access,access,"tr",ids.toSet(),ids[0],ids.drop(3).toSet(),allowed)!!
            assertEquals(ids[2],choice.soz.kimlik)
        }
    }
}
