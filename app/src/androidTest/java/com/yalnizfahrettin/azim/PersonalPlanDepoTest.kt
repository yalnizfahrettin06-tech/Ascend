package com.yalnizfahrettin.azim

import androidx.datastore.preferences.core.*
import androidx.datastore.core.DataStore
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
            assertEquals(TemaModu.AYDINLIK, depo.tema.first()); assertEquals(Palet.MERMER, depo.palet.first())
            depo.arkaPlanAyarla("KAR_MUHAFIZI")
            depo.acik.first()
            assertEquals("KAR_MUHAFIZI", depo.arkaPlan.first())
        } finally { job.cancelAndJoin(); file.delete() }
    }

    @Test fun newInstallationStartsWithMarbleAndLightWithoutGrantingAccess() = runBlocking {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(ctx.cacheDir, "plan-marble-${UUID.randomUUID()}.preferences_pb")
        try {
            withPlanStore(file) { depo, _ ->
                assertEquals(TemaModu.AYDINLIK, depo.tema.first())
                assertEquals(Palet.MERMER, depo.palet.first())
                assertFalse(depo.dinamikRenk.first())
                assertEquals(Erisim.ucretsizKategoriler, depo.acik.first())
                assertFalse(depo.proDemo.first())
                assertFalse(depo.onboardingBitti.first())
                assertNull(depo.personalProfile.first())
            }
            withPlanStore(file) { reopened, _ ->
                assertEquals(Palet.MERMER, reopened.palet.first())
                assertEquals(TemaModu.AYDINLIK, reopened.tema.first())
                assertEquals(Erisim.ucretsizKategoriler, reopened.acik.first())
            }
        } finally { file.delete() }
    }

    @Test fun v7VisualMigrationPreservesNightChoicesAndPersonalDataAcrossRestart() = runBlocking {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val cases = listOf(
            Triple(Palet.KUM, TemaModu.KARANLIK, Palet.MERMER),
            Triple(Palet.LACIVERT, TemaModu.OLED, Palet.MERMER),
            Triple(Palet.YOSUN, TemaModu.SISTEM, Palet.MERMER),
            Triple(Palet.MONO, TemaModu.KARANLIK, Palet.MONO),
            Triple(Palet.BORDO, TemaModu.SISTEM, Palet.BORDO),
        )
        val profile = PersonalProfile(name = "İpek", dailyCount = 4, startHour = 8, endHour = 22)
            .choose("goal", "confidence").choose("avoid", "relationships", multiple = true)
        val favorite = Sozler.kategoriden("motivasyon").first().kimlik
        val seen = Sozler.kategoriden("azim").first().kimlik
        val earned = setOf("ozguven", "korku")
        val selection = setOf("motivasyon", "ozguven")
        cases.forEach { (oldPalette, theme, expectedPalette) ->
            val file = File(ctx.cacheDir, "plan-v7-${oldPalette.name}-${UUID.randomUUID()}.preferences_pb")
            var migrated: Map<Preferences.Key<*>, Any> = emptyMap()
            suspend fun verify(depo: Depo) {
                assertEquals("Explicit ${theme.name} must survive ${oldPalette.name}", theme, depo.tema.first())
                assertEquals(expectedPalette, depo.palet.first())
                assertFalse(depo.dinamikRenk.first())
                assertNull(depo.arkaPlan.first())
                assertEquals(profile, depo.personalProfile.first())
                assertEquals(setOf(favorite), depo.favoriler.first())
                assertEquals(setOf(seen), depo.gecmis.first())
                assertEquals(Erisim.ucretsizKategoriler + earned, depo.acik.first())
                assertEquals(selection, depo.secili.first())
                assertTrue(depo.onboardingBitti.first())
                assertFalse(depo.proDemo.first())
                assertEquals(4, depo.gunlukAdet.first())
                assertEquals(8, depo.baslangicSaati.first())
                assertEquals(22, depo.bitisSaati.first())
            }
            try {
                withPlanStore(file) { depo, store ->
                    // A real v7 preferences file: access was already migrated in v6,
                    // and v7's visual marker must not suppress the new migration.
                    store.edit {
                        it[booleanPreferencesKey("visual_v7_applied")] = true
                        it[intPreferencesKey("erisim_surumu")] = Erisim.SURUM
                        it[booleanPreferencesKey("onboarding_bitti")] = true
                        it[booleanPreferencesKey("pro_demo_acik")] = false
                        it[stringPreferencesKey("palet")] = oldPalette.name
                        it[stringPreferencesKey("tema")] = theme.name
                        it[booleanPreferencesKey("dinamik_renk")] = true
                        it[stringPreferencesKey("ana_arka_plan")] = "KAR_MUHAFIZI"
                        it[stringPreferencesKey("personal_profile_v1")] = profile.encode()
                        it[stringSetPreferencesKey("favoriler")] = setOf(favorite)
                        it[stringSetPreferencesKey("gosterilen_gecmis")] = setOf(seen)
                        it[stringSetPreferencesKey("kazanilan_kategoriler")] = earned
                        it[stringSetPreferencesKey("secili_kategoriler")] = selection
                        it[intPreferencesKey("gunluk_adet")] = 4
                        it[intPreferencesKey("baslangic_saati")] = 8
                        it[intPreferencesKey("bitis_saati")] = 22
                    }
                    verify(depo)
                    migrated = store.data.first().asMap()
                }
                withPlanStore(file) { reopened, store ->
                    verify(reopened)
                    assertEquals("Reopening and collecting flows must not rewrite migrated data", migrated, store.data.first().asMap())
                    reopened.paletAyarla(Palet.BORDO)
                    reopened.temaAyarla(TemaModu.OLED)
                }
                withPlanStore(file) { reopened, _ ->
                    assertEquals("A later palette choice must survive future starts", Palet.BORDO, reopened.palet.first())
                    assertEquals(TemaModu.OLED, reopened.tema.first())
                    assertEquals(profile, reopened.personalProfile.first())
                    assertEquals(Erisim.ucretsizKategoriler + earned, reopened.acik.first())
                }
            } finally { file.delete() }
        }
    }

    private suspend fun withPlanStore(file: File, block: suspend (Depo, DataStore<Preferences>) -> Unit) {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try { withTimeout(15000) { block(Depo(ctx, store), store) } }
        finally { job.cancelAndJoin() }
    }
}
