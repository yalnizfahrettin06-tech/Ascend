package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class LivingExperienceUnitTest {
    @Test fun onlyNewSeriesIsPaidAndEachLanguageHasSevenDifferentEditorialDays() {
        assertEquals(setOf("kindness","steps","focus"),ShortSeries.all.filterNot { it.pro }.map { it.id }.toSet())
        assertEquals(listOf("restart"),ShortSeries.all.filter { it.pro }.map { it.id })
        Diller.kodlar.forEach { language ->
            val chapters = RestartSeries.days(language)
            assertEquals(7,chapters.size)
            assertEquals(7,chapters.map { it.story }.distinct().size)
            assertEquals(7,chapters.map { it.step }.distinct().size)
            chapters.forEach { assertTrue(it.title.isNotBlank()); assertTrue(it.story.length > 100); assertTrue(it.step.length > 40) }
            if(language != "en") assertNotEquals(RestartSeries.days("en"),chapters)
        }
    }
    @Test fun livingThemeRequiresAccessAndInterruptedSeriesKeepsProgress() {
        assertEquals("black",AnaTemalar.allowed(AnaTemalar.living.id,false).id)
        assertEquals(AnaTemalar.living,AnaTemalar.allowed(AnaTemalar.living.id,true))
        val first = SeriesProgress("restart").complete(LocalDate.of(2026,9,19))
        assertEquals(first,SeriesProgress.decode(first.encode()))
        assertEquals(2,first.complete(LocalDate.of(2026,10,1)).completed)
    }
}
