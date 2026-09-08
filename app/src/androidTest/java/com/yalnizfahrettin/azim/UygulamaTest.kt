package com.yalnizfahrettin.azim

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.data.Erisim
import com.yalnizfahrettin.azim.data.Kategoriler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

fun ekranKaydet(name: String) {
    Thread.sleep(500)
    val ui = InstrumentationRegistry.getInstrumentation().uiAutomation
    fun shell(command: String) { android.os.ParcelFileDescriptor.AutoCloseInputStream(ui.executeShellCommand(command)).use { it.readBytes() } }
    shell("mkdir -p /sdcard/Download/ascend-screenshots")
    shell("screencap -p /sdcard/Download/ascend-screenshots/$name.png")
}

class UygulamaTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun shot(name: String) { compose.waitForIdle(); ekranKaydet(name) }
    private fun closeProIfOpen() {
        compose.waitForIdle()
        if (compose.onAllNodesWithTag("pro-close").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("pro-close").performClick()
        }
        compose.waitUntil(10000) { compose.onAllNodesWithTag("pro-sheet").fetchSemanticsNodes().isEmpty() }
    }

    private fun chooseShareFilter(key: String) {
        compose.onNodeWithTag("share-background-filters").performScrollTo()
            .performScrollToNode(hasTestTag("share-filter-$key"))
        compose.onNodeWithTag("share-filter-$key").performClick()
    }

    private fun openHomeScenePicker() {
        compose.onNode(hasContentDescription("Arka planı değiştir") and hasAnyAncestor(hasTestTag("active-quote")))
            .performScrollTo().performClick()
    }

    private fun chooseShareBackground(key: String) {
        compose.onNodeWithTag("share-background-options").performScrollTo()
            .performScrollToNode(hasTestTag("share-background-$key"))
        compose.onNodeWithTag("share-background-$key").performClick()
    }

    private fun galleryImageIds(): Set<Long> {
        val resolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
        return resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.MediaColumns.MIME_TYPE} = ?",
            arrayOf("Pictures/Ascend%", "image/png"), null)!!.use { cursor ->
            buildSet { while (cursor.moveToNext()) add(cursor.getLong(0)) }
        }
    }

    private fun assertNewStoryPng(before: Set<Long>) {
        val created = galleryImageIds() - before
        assertEquals("The share action must create one new gallery image", 1, created.size)
        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, created.single())
        val resolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
        val bitmap = resolver.openInputStream(uri)!!.use { BitmapFactory.decodeStream(it) }
        assertNotNull("The exported PNG must decode completely", bitmap)
        val decoded = checkNotNull(bitmap)
        try {
            assertEquals(1080, decoded.width)
            assertEquals(1920, decoded.height)
        } finally { decoded.recycle() }
        resolver.query(uri, arrayOf(MediaStore.MediaColumns.IS_PENDING), null, null, null)!!.use {
            assertTrue(it.moveToFirst())
            assertEquals("The gallery file must be published, not pending", 0, it.getInt(0))
        }
    }

    @Test fun realNavigationShareAndLanguage() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val demoDepo = Depo(instrumentation.targetContext)
        compose.waitUntil(15000) { compose.onAllNodesWithText("Kendi yolunu oluştur →").fetchSemanticsNodes().isNotEmpty() }
        shot("01-welcome")
        compose.onNodeWithText("Kendi yolunu oluştur →").performClick(); shot("02-topics")
        compose.onNodeWithText("Devam").performClick(); shot("03-schedule")
        compose.onNodeWithText("Devam").performClick(); shot("04-permission")
        compose.onNodeWithText("Samsung: Ayrıntılı görünüm").performScrollTo().performClick()
        compose.onNodeWithText("Görünüm ayarlarını aç ↗").performScrollTo(); shot("05-detailed-notification-guide")
        compose.onNodeWithText("Şimdilik bildirimsiz devam et").performClick()
        compose.waitUntil(15000) { compose.onAllNodesWithText("Senin için").fetchSemanticsNodes().isNotEmpty() }
        val originalSelection = runBlocking { withTimeout(5000) { demoDepo.secili.first() } }
        assertEquals(Erisim.ucretsizKategoriler, runBlocking { withTimeout(5000) { demoDepo.acik.first() } })
        assertFalse(runBlocking { withTimeout(5000) { demoDepo.proDemo.first() } })
        shot("06-home")

        // Home scenery is a separate choice from paid export backgrounds.
        openHomeScenePicker()
        compose.onNodeWithTag("scene-filter-doku").performClick()
        compose.onNodeWithTag("scene-grid").performScrollToNode(hasTestTag("scene-choice-bakir_doku"))
        shot("24-scene-picker")
        compose.onNodeWithTag("scene-choice-bakir_doku").performClick()
        compose.runOnUiThread { compose.activity.recreate() }
        compose.waitUntil(15000) { compose.onAllNodesWithTag("active-quote").fetchSemanticsNodes().isNotEmpty() }
        openHomeScenePicker()
        compose.onNodeWithTag("scene-filter-doku").assertIsSelected()
        compose.onNodeWithTag("scene-grid").performScrollToNode(hasTestTag("scene-choice-bakir_doku"))
        compose.onNodeWithTag("scene-choice-bakir_doku").assertIsSelected()
        compose.onNodeWithTag("scene-close").performClick()
        shot("25-home-copper")

        compose.onNode(hasText("Kaydet") and hasAnyAncestor(hasTestTag("active-quote"))).performScrollTo().performClick()
        compose.onNode(hasText("Paylaş") and hasAnyAncestor(hasTestTag("active-quote"))).performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        shot("07-share-image")

        // A free user can save a real PNG using one of the three basic backgrounds.
        chooseShareBackground("paper")
        compose.onNodeWithTag("share-image").performScrollTo().assertIsSelected()
        compose.waitUntil(15000) { compose.onAllNodes(hasText("Galeriye kaydet") and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Galeriye kaydet").performClick()
        compose.waitUntil(20000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Galeriye kaydedildi ✓").assertIsDisplayed()
        shot("09-gallery-saved")

        // Advanced sharing opens an explicit demo, without silently granting access.
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        compose.onNodeWithTag("pro-sheet").assertIsDisplayed()
        assertFalse(runBlocking { withTimeout(5000) { demoDepo.proDemo.first() } })
        shot("20-pro")
        compose.onNodeWithTag("pro-close").performClick()
        chooseShareFilter("doku")
        chooseShareBackground("bakir_doku")
        compose.onNodeWithTag("pro-sheet").assertIsDisplayed()
        assertFalse("Selecting a paid texture must not silently enable Pro",
            runBlocking { withTimeout(5000) { demoDepo.proDemo.first() } })
        compose.onNodeWithText("Ödeme alınmaz. Abonelik başlatılmaz.").assertIsDisplayed()
        compose.onNodeWithTag("pro-demo-enable").performClick()
        runBlocking { withTimeout(10000) { demoDepo.proDemo.first { it } } }
        closeProIfOpen()
        val proAccess = runBlocking { withTimeout(10000) { demoDepo.acik.first { it.size == 70 } } }
        assertEquals(Kategoriler.tumAltlar.map { it.anahtar }.toSet(), proAccess)
        assertEquals("Pro must not subscribe the user to every topic", originalSelection,
            runBlocking { withTimeout(5000) { demoDepo.secili.first() } })

        compose.waitUntil(10000) { compose.onAllNodesWithText("PRO DEMO").fetchSemanticsNodes().isNotEmpty() }
        chooseShareFilter("efsane")
        chooseShareBackground("kale_nobeti")
        compose.onNodeWithTag("share-background-kale_nobeti").assertIsSelected()
        compose.onNodeWithTag("share-selected-background").assertTextEquals("Seçili: Kale nöbeti")
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("share-preview").performScrollTo()
        shot("26-knight-share")
        val beforeKnightExport = galleryImageIds()
        compose.waitUntil(15000) { compose.onAllNodes(hasText("Galeriye kaydet") and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Galeriye kaydet").performClick()
        compose.waitUntil(20000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        assertNewStoryPng(beforeKnightExport)
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        compose.onNodeWithText("45s").performScrollTo().performClick()
        compose.runOnUiThread { compose.activity.recreate() }
        compose.waitUntil(10000) { compose.onAllNodesWithText("45s").fetchSemanticsNodes().isNotEmpty() }
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("share-selected-background").assertTextEquals("Seçili: Kale nöbeti")
        compose.onNodeWithTag("share-video").assertIsSelected()
        compose.onNodeWithText("45s").performScrollTo().assertIsSelected(); shot("08-share-video")
        compose.onNodeWithContentDescription("Kapat").performClick()

        compose.onNodeWithTag("nav-favori").performClick()
        compose.onNodeWithText("Sende kalan sözler.").assertIsDisplayed(); shot("10-saved")
        compose.onNodeWithTag("nav-kategori").performClick()
        compose.onNodeWithText("İlhamını keşfet.").assertIsDisplayed(); shot("11-discover")
        compose.onNodeWithText("PRO DEMO").performClick()
        compose.onNodeWithTag("pro-demo-disable").performClick()
        runBlocking { withTimeout(10000) { demoDepo.proDemo.first { !it } } }
        closeProIfOpen()
        val beforeDemo = runBlocking { withTimeout(10000) { demoDepo.acik.first { it == Erisim.ucretsizKategoriler } } }
        val selectionBeforeDemo = runBlocking { withTimeout(5000) { demoDepo.secili.first() } }
        assertEquals("Turning off Pro must keep the chosen starter topics", originalSelection, selectionBeforeDemo)
        assertFalse("The chosen topic must initially be locked", "ozguven" in beforeDemo)
        assertFalse("A neighbouring topic must initially be locked", "korku" in beforeDemo)
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-ozguven"))
        compose.onNodeWithTag("category-ozguven").performClick()
        compose.onNodeWithText("10 özgün söz · Kategori önizlemesi").assertIsDisplayed()
        shot("21-category-detail")
        compose.onNodeWithText("Bu kategoriyi aç · Demo").performScrollTo().performClick()
        compose.onNodeWithText("GEÇİCİ DEMO").assertIsDisplayed()
        compose.onNodeWithText("Bu sürümde gerçek reklam yok.", substring = true).assertIsDisplayed()
        shot("16-demo")

        val activityBeforeDemo = compose.activity
        compose.onNodeWithText("Demo bağlantısını aç").performClick()
        // Observe the real Activity leaving for the external browser. No simulated reward callback.
        compose.waitUntil(15000) {
            var leftApp = false
            instrumentation.runOnMainSync {
                leftApp = activityBeforeDemo.lifecycle.currentState == Lifecycle.State.CREATED
            }
            leftApp
        }
        assertFalse("Leaving the app must not unlock before returning",
            runBlocking { withTimeout(5000) { demoDepo.acik.first() } }.contains("ozguven"))
        // Return as the system launcher would; the app's own background start can be ignored on Android 15.
        val returnCommand = "am start -W --activity-reorder-to-front -n " +
            instrumentation.targetContext.packageName + "/" + MainActivity::class.java.name
        val returnResult = android.os.ParcelFileDescriptor.AutoCloseInputStream(
            instrumentation.uiAutomation.executeShellCommand(returnCommand)
        ).use { String(it.readBytes()) }
        assertFalse("System return failed: $returnResult", returnResult.contains("Error:"))
        compose.waitUntil(15000) {
            var resumed = false
            instrumentation.runOnMainSync {
                resumed = activityBeforeDemo.lifecycle.currentState == Lifecycle.State.RESUMED
            }
            resumed
        }
        val afterDemo = runBlocking { withTimeout(15000) { demoDepo.acik.first { "ozguven" in it } } }
        assertEquals("Only the chosen individual topic may unlock", beforeDemo + "ozguven", afterDemo)
        assertFalse("A neighbouring topic must stay locked", "korku" in afterDemo)
        assertEquals("Only the rewarded topic may join reminders", selectionBeforeDemo + "ozguven",
            runBlocking { withTimeout(10000) { demoDepo.secili.first { "ozguven" in it } } })
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-ozguven"))
        compose.onNodeWithTag("category-ozguven")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Bildirimlerinde seçili"))
        shot("17-demo-unlocked")

        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithText("Kendi yolunda.").assertIsDisplayed(); shot("12-journey")
        compose.onNodeWithTag("nav-ana").performClick()
        compose.onNodeWithContentDescription("Ayarlar").performScrollTo().performClick()
        compose.onNodeWithText("Karanlık").performScrollTo().performClick()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Senin için").assertExists(); shot("13-home-dark")
        compose.onNodeWithContentDescription("Ayarlar").performClick()
        compose.onNodeWithText("English").performScrollTo().performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty() }
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("For you").assertExists()
        compose.onNodeWithText("Demo tamamlandı.", substring = true).assertDoesNotExist()
        shot("15-home-english")
    }
}
