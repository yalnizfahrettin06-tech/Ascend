package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.data.*
import org.junit.Assert.*
import org.junit.Test

class ProJourneyTest {
    @Test fun offerSurvivesRecreationWithoutBecomingAnEntitlement() {
        ProSource.entries.forEach { source ->
            val offer = ProOffer(source,"emperor","v5_motivasyon_01",true)
            assertEquals(offer,ProOffer.decode(offer.encode()))
        }
        assertEquals(ProOffer(),ProOffer.decode("bad|input"))
        assertEquals("black",AnaTemalar.allowed("emperor",false).id)
    }
    @Test fun seriesSequenceIsIndependentFromCategoryOrderAndHasCompleteTranslations() {
        ShortSeries.all.forEach { series ->
            assertEquals(7,series.quoteIds.distinct().size)
            assertNotEquals(Sozler.kategoriden(series.category).take(7).map { it.kimlik },series.quoteIds)
            assertEquals(series.quoteIds,series.quotes.map { it.kimlik })
            Diller.kodlar.forEach { language ->
                assertTrue(series.summary(language).isNotBlank())
                for(day in 0..6) {
                    assertTrue(series.prompt(day,language).isNotBlank())
                    if(language !in setOf("en","tr")) assertNotEquals(series.prompt(day,"en"),series.prompt(day,language))
                }
            }
        }
        assertEquals("v5_ozsefkat_08",ShortSeries.all.first().quoteIds.first())
        assertEquals(setOf("kindness","steps","focus"),ShortSeries.all.map { it.id }.toSet())
    }
}
