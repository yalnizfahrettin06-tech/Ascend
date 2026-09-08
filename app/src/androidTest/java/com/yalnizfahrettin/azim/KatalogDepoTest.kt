package com.yalnizfahrettin.azim

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.data.Erisim
import com.yalnizfahrettin.azim.data.Sozler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.UUID

class KatalogDepoTest {
    private fun isolated(block: suspend (Depo, suspend (Set<String>) -> Unit) -> Unit) = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir, "catalog-${UUID.randomUUID()}.preferences_pb")
        val job = SupervisorJob()
        val store = PreferenceDataStoreFactory.create(scope = CoroutineScope(job + Dispatchers.IO), produceFile = { file })
        try {
            withTimeout(15000) {
                block(Depo(context, store)) { ids -> store.edit { it[stringSetPreferencesKey("gosterilen_gecmis")] = ids } }
            }
        } finally {
            job.cancelAndJoin()
            file.delete()
        }
    }

    @Test fun historyKeepsTheWholeCatalogAndFiltersLegacyIds() = isolated { depo, seed ->
        depo.proDemoAyarla(true)
        depo.kategorileriAyarla(Erisim.tumKategoriler)
        assertEquals(70, depo.secili.first().size)
        assertEquals(700, Sozler.bildirimHavuzu(depo.secili.first(), "en").size)
        val ids = Sozler.tumu().map { it.kimlik }
        seed(ids.dropLast(1).toSet() + "old-category:123")
        depo.bildirimGecmisineEkle(ids.last())
        assertEquals(ids.toSet(), depo.gecmis.first())
        assertEquals(ids.last(), depo.sonBildirimKimlik.first())
        // Entitlement revocation changes future selections, never erases prior history.
        depo.proDemoAyarla(false)
        assertEquals(Erisim.ucretsizKategoriler, depo.secili.first())
        assertEquals(ids.toSet(), depo.gecmis.first())
    }

    @Test fun newCycleClearsOnlySelectedCategoriesAndRereadingRestoresHistory() = isolated { depo, seed ->
        depo.kategoriAc("merak")
        depo.kategorileriAyarla(setOf("motivasyon"))
        val category = Sozler.kategoriden("motivasyon")
        val first = category[0].kimlik
        val second = category[1].kimlik
        val outside = Sozler.kategoriden("merak").first().kimlik
        depo.gosterildi(first)
        val count = depo.gorulenToplam.first()
        seed(category.map { it.kimlik }.toSet() + outside)
        depo.bildirimGecmisineEkle(second, setOf("motivasyon"))
        assertEquals(setOf(second, outside), depo.gecmis.first())
        depo.gosterildi(first)
        assertEquals(setOf(first, second, outside), depo.gecmis.first())
        assertEquals("A same-day reread must not increment daily statistics", count, depo.gorulenToplam.first())
        assertEquals(1, depo.bugunGorulen.first())
    }
}
