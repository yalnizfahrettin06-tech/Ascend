package com.yalnizfahrettin.azim

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.UUID

/** Exercises the actual preference transaction, rather than only a duplicate policy model. */
class ErisimDepoTest {
    private fun isolated(block: suspend (Depo, DataStore<Preferences>, () -> Depo) -> Unit) = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir, "access-${UUID.randomUUID()}.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try {
            withTimeout(15000) { block(Depo(context, store), store) { Depo(context, store) } }
        } finally {
            job.cancelAndJoin()
            file.delete()
        }
    }

    @Test fun newInstallationHasOnlyStarterAccessAndDoesNotTrustUnfinishedLegacySetup() = isolated { depo, store, _ ->
        store.edit {
            it[booleanPreferencesKey("onboarding_bitti")] = false
            it[stringSetPreferencesKey("acik_gruplar")] = setOf("cesaret", "inanc")
            it[stringSetPreferencesKey("secili_kategoriler")] = setOf("kuran", "motivasyon", "unknown")
        }
        assertEquals(Erisim.ucretsizKategoriler, depo.acik.first())
        assertEquals(setOf("motivasyon"), depo.secili.first())
        assertFalse(depo.proDemo.first())
        assertEquals(emptySet<String>(), depo.acikGruplar.first())
    }

    @Test fun completingOnboardingBeforeAnyFlowReadDoesNotGrantLegacyFreeGroups() = isolated { depo, _, reload ->
        depo.onboardingKaydet(setOf("azim", "pes", "kuran"), 3, 9, 21, true)
        val reopened = reload()
        assertEquals(Erisim.ucretsizKategoriler, reopened.acik.first())
        assertEquals(setOf("azim"), reopened.secili.first())
        assertTrue(reopened.onboardingBitti.first())
        assertTrue(reopened.hatirlaticiAcik.first())
    }

    @Test fun oldUserKeepsEveryPriorEntitlementSelectionBookmarkAndHistoryRecord() = isolated { depo, store, reload ->
        val favorites = EskiSozler.kimlikler.keys + Sozler.tumu().take(3).map { it.kimlik }
        val history = Sozler.tumu().map { it.kimlik }.toSet()
        store.edit {
            it[booleanPreferencesKey("onboarding_bitti")] = true
            it[stringSetPreferencesKey("acik_gruplar")] = setOf("cesaret")
            it[stringSetPreferencesKey("secili_kategoriler")] = setOf("odak", "stoacilik", "ozguven", "kuran")
            it[stringSetPreferencesKey("favoriler")] = favorites
            it[stringSetPreferencesKey("gosterilen_gecmis")] = history
            it[intPreferencesKey("gorulen_toplam")] = 728
            it[intPreferencesKey("seri_gun")] = 9
        }
        val access = depo.acik.first()
        assertEquals("25 old free + 5 earned + a preserved legacy selection", 31, access.size)
        assertTrue(access.containsAll(Kategoriler.acikAltlar(Kategoriler.ucretsizGruplar)))
        assertTrue(access.containsAll(setOf("ozguven", "korku", "kuran")))
        assertFalse("Unopened family members must stay locked", "incil" in access)
        assertEquals(setOf("derin_odak", "epiktetos", "ozguven", "kuran"), depo.secili.first())
        assertEquals(favorites, depo.favoriler.first())
        assertEquals(history, depo.gecmis.first())
        assertEquals(728, depo.gorulenToplam.first())
        assertEquals(9, depo.seri.first())
        assertEquals(access, reload().acik.first())
    }

    @Test fun completedRewardGrantsAndSelectsOneCategoryOnlyAndIsIdempotent() = isolated { depo, _, reload ->
        val before = depo.secili.first()
        depo.kategoriAc("ozguven")
        depo.kategoriAc("ozguven")
        depo.kategoriAc("cesaret")
        depo.kategoriAc("unknown")
        assertEquals(Erisim.ucretsizKategoriler + "ozguven", depo.acik.first())
        assertEquals(before + "ozguven", depo.secili.first())
        assertFalse("korku" in depo.acik.first())
        assertEquals(Erisim.ucretsizKategoriler + "ozguven", reload().acik.first())
    }

    @Test fun ProDemoPersistsWithoutAutoSelectingTopicsAndRevocationRetainsEarnedAccess() = isolated { depo, _, reload ->
        depo.kategoriAc("ozguven")
        val selected = depo.secili.first()
        depo.proDemoAyarla(true)
        assertTrue(reload().proDemo.first())
        assertEquals(Erisim.tumKategoriler, depo.acik.first())
        assertEquals("Pro is access, not permission to subscribe to all topics", selected, depo.secili.first())
        depo.kategorileriAyarla(setOf("ozguven", "kuran", "ask"))
        assertEquals(setOf("ozguven", "kuran", "ask"), depo.secili.first())
        depo.proDemoAyarla(false)
        assertFalse(reload().proDemo.first())
        assertEquals(Erisim.ucretsizKategoriler + "ozguven", depo.acik.first())
        assertEquals(setOf("ozguven"), depo.secili.first())
    }

    @Test fun revokingProWithOnlyPremiumSelectionFallsBackAndSelectionCannotBypassAccess() = isolated { depo, _, _ ->
        depo.proDemoAyarla(true)
        depo.kategorileriAyarla(setOf("kuran"))
        depo.proDemoAyarla(false)
        assertEquals(Kategoriler.varsayilanSecili, depo.secili.first())
        depo.kategoriSec("kuran")
        depo.kategorileriAyarla(setOf("kuran", "unknown"))
        assertEquals(Kategoriler.varsayilanSecili, depo.secili.first())
        depo.kategorileriAyarla(setOf("azim", "kuran"))
        assertEquals(setOf("azim"), depo.secili.first())
        depo.kategoriSec("azim")
        assertEquals("At least one eligible notification topic must remain", setOf("azim"), depo.secili.first())
    }

    @Test fun concurrentReadersAndRewardWritesCannotRepeatMigrationOrDropTheNewReward() = isolated { depo, store, reload ->
        store.edit {
            it[booleanPreferencesKey("onboarding_bitti")] = true
            it[stringSetPreferencesKey("secili_kategoriler")] = setOf("motivasyon")
        }
        coroutineScope {
            listOf(
                async { depo.acik.first() },
                async { depo.secili.first() },
                async { depo.proDemo.first() },
                async { depo.kategoriAc("kuran") },
            ).awaitAll()
        }
        val expected = Kategoriler.acikAltlar(Kategoriler.ucretsizGruplar) + "kuran"
        assertEquals(expected, reload().acik.first())
        assertEquals(setOf("motivasyon", "kuran"), reload().secili.first())
        assertEquals(Erisim.SURUM, store.data.first()[intPreferencesKey("erisim_surumu")])
        depo.proDemoAyarla(true)
        depo.proDemoAyarla(false)
        assertEquals("Revocation must preserve grandfathered access too", expected, reload().acik.first())
    }
}
