package com.yalnizfahrettin.azim

import androidx.datastore.preferences.core.*
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.time.LocalDate
import java.util.UUID

class Phase34StorageTest {
    @Test fun setupFavoritesCommitOnceAndCannotImportOtherRecords() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir,"setup-${UUID.randomUUID()}.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO),produceFile = { file })
        try {
            val depot = Depo(context,store)
            val first = SetupPractice.quotes.first().kimlik
            val original = Sozler.tumu().last().kimlik
            depot.favoriDegistir(original)
            val profile = PersonalProfile().choose("theme","rider").choose(SetupPractice.SAVED,first,multiple = true)
                .choose(SetupPractice.SAVED,"invalid-id",multiple = true)
            depot.saveOnboardingDraft(profile)
            assertEquals(setOf(original),depot.favoriler.first())
            repeat(2) { depot.completePersonalPlan(profile,true) }
            assertEquals(setOf(original,first),depot.favoriler.first())
            assertTrue(depot.personalProfile.first()!!.answer(SetupPractice.SAVED).isEmpty())
        } finally { job.cancelAndJoin(); file.delete() }
    }

    @Test fun legacySavedAndSeriesDataSurviveAndFirstCompletionIsAtomic() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir,"phase34-${UUID.randomUUID()}.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO),produceFile = { file })
        try {
            val quote = Sozler.tumu().first().kimlik
            val yesterday = LocalDate.now().minusDays(1)
            store.edit {
                it[stringSetPreferencesKey("favoriler")] = setOf(quote)
                it[stringSetPreferencesKey("short_series_progress")] = setOf(SeriesProgress("kindness",2,yesterday).encode())
                it[stringPreferencesKey("dil")] = "de"
            }
            val depot = Depo(context,store)
            assertTrue(quote in depot.favoriler.first())
            assertEquals(2,depot.seriesProgress.first()["kindness"]!!.completed)
            assertEquals("de",depot.dil.first())
            depot.beginAndCompleteSeriesDay("restart")
            assertNull(depot.seriesProgress.first()["restart"])
            depot.proDemoAyarla(true)
            coroutineScope { repeat(3) { launch { depot.beginAndCompleteSeriesDay("restart") } } }
            assertEquals(1,depot.seriesProgress.first()["restart"]!!.completed)
            depot.beginAndCompleteSeriesDay("restart", yesterday)
            assertEquals(1,depot.seriesProgress.first()["restart"]!!.completed)
            depot.proDemoAyarla(false)
            assertEquals(1,Depo(context,store).seriesProgress.first()["restart"]!!.completed)
            assertTrue(quote in depot.favoriler.first())
        } finally { job.cancelAndJoin(); file.delete() }
    }
}
