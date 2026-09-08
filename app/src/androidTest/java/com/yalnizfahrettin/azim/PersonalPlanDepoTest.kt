package com.yalnizfahrettin.azim

import androidx.datastore.preferences.core.*
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.core.Palet
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.UUID

class PersonalPlanDepoTest {
    @Test fun draftSurvivesStoreRestartAndCompletionIsAtomicWithoutProGrant() = runBlocking {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(ctx.cacheDir, "plan-${UUID.randomUUID()}.preferences_pb")
        val draft = PersonalProfile(name = "Ada", step = 11, dailyCount = 5, startHour = 7, endHour = 20)
            .choose("format", "affirmation").choose("tone", "gentle").choose("discovery", "none")
        suspend fun open(block: suspend (Depo) -> Unit) {
            val job = SupervisorJob()
            val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
            try { withTimeout(15000) { block(Depo(ctx, store)) } } finally { job.cancelAndJoin() }
        }
        try {
            open { depo ->
                depo.saveOnboardingDraft(draft)
                assertFalse(depo.onboardingBitti.first()); assertNull(depo.personalProfile.first())
                assertEquals(Erisim.ucretsizKategoriler, depo.acik.first())
            }
            open { depo ->
                assertEquals(draft, depo.onboardingDraft.first())
                depo.completePersonalPlan(draft, false)
                assertTrue(depo.onboardingBitti.first()); assertFalse(depo.hatirlaticiAcik.first())
                assertFalse(depo.proDemo.first()); assertEquals(Erisim.ucretsizKategoriler, depo.acik.first())
                assertEquals(setOf("ozsefkat", "ic_huzur"), depo.secili.first())
                assertEquals(5, depo.gunlukAdet.first()); assertEquals(7, depo.baslangicSaati.first()); assertEquals(20, depo.bitisSaati.first())
            }
            open { depo ->
                assertEquals(draft.copy(step = 0), depo.personalProfile.first())
                assertEquals(PersonalProfile(), depo.onboardingDraft.first())
            }
        } finally { file.delete() }
    }
    @Test fun profileEditingPreservesEarnedAccessBookmarksAndExistingThemeChoice() = runBlocking {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(ctx.cacheDir, "plan-edit-${UUID.randomUUID()}.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try {
            val depo = Depo(ctx, store)
            depo.temaAyarla(TemaModu.AYDINLIK)
            depo.kategoriAc("ozguven")
            val saved = Sozler.kategoriden("motivasyon").first().kimlik
            depo.favoriDegistir(saved); depo.gosterildi(saved)
            depo.completePersonalPlan(PersonalProfile().choose("goal", "confidence"), false)
            assertEquals(Erisim.ucretsizKategoriler + "ozguven", depo.acik.first())
            assertTrue(saved in depo.favoriler.first()); assertTrue(saved in depo.gecmis.first())
            assertEquals(TemaModu.AYDINLIK, depo.tema.first()); assertEquals(Palet.MONO, depo.palet.first())
            depo.arkaPlanAyarla("KAR_MUHAFIZI")
            depo.acik.first()
            assertEquals("KAR_MUHAFIZI", depo.arkaPlan.first())
        } finally { job.cancelAndJoin(); file.delete() }
    }
}
