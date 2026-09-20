package com.yalnizfahrettin.azim
import com.yalnizfahrettin.azim.data.*
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
class Phase34Test {
    @Test fun everyReleaseMessageHasSevenNonemptyTranslations() {
        PhaseCopy.entries.forEach { (key, values) ->
            assertEquals(key,7,values.size)
            assertTrue(key,values.all { it.isNotBlank() })
        }
    }
    @Test fun seriesWaitsAcrossClockRollbackAndReopensOnNextLocalDay() {
        val day = LocalDate.of(2026,9,20)
        val first = SeriesProgress("restart").complete(day)
        assertFalse(first.canComplete(day.minusDays(1)))
        assertFalse(first.canComplete(day))
        assertEquals(2,first.complete(day.plusDays(1)).completed)
    }
}
