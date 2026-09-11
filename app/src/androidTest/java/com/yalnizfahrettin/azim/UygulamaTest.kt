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
import com.yalnizfahrettin.azim.data.PersonalProfile
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.core.Palet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Before
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
    private fun shot(name: String) { compose.waitForIdle(); ekranKaydet(name.replaceFirst("-", "-v8-")) }
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


    private lateinit var demoDepo: Depo

    @Before fun startWithSavedPersonalPlan() {
        demoDepo = Depo(InstrumentationRegistry.getInstrumentation().targetContext)
        runBlocking {
            withTimeout(15000) {
                demoDepo.proDemoAyarla(false)
                demoDepo.completePersonalPlan(PersonalProfile(), false)
                demoDepo.dilAyarla("tr")
                demoDepo.temaAyarla(TemaModu.AYDINLIK)
                demoDepo.paletAyarla(Palet.MERMER)
                demoDepo.favoriler.first().forEach { demoDepo.favoriDegistir(it) }
            }
        }
        compose.activityRule.scenario.recreate()
        waitForHome()
    }

    private fun waitForHome() {
        compose.waitUntil(15000) { compose.onAllNodesWithTag("active-quote").fetchSemanticsNodes().isNotEmpty() }
        // Recreation can compose the page before Android returns input focus.
        // A coordinate click during that window is dropped by the platform.
        compose.waitUntil(15000) { compose.runOnUiThread { compose.activity.hasWindowFocus() } }
        compose.waitForIdle()
    }

    private fun recordTouchState(tag: String, label: String) {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        android.util.Log.i("AscendTouch", "$label: active package=${automation.rootInActiveWindow?.packageName}")
        compose.onNodeWithTag(tag).printToLog("AscendTouch")
    }

    private inline fun <T> withTouchEvidence(label: String, block: () -> T): T = try {
        block()
    } catch (failure: Throwable) {
        recordTouchState("home-content", label)
        ekranKaydet("failure-$label")
        throw failure
    }

    private fun openShare() {
        compose.onNodeWithTag("home-share").assertIsDisplayed().performClick()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
    }

    private fun enableProFromGate() {
        compose.onNodeWithTag("pro-sheet").assertIsDisplayed()
        compose.onNodeWithText("Ödeme alınmaz. Abonelik başlatılmaz.").assertIsDisplayed()
        compose.onNodeWithTag("pro-demo-enable").performClick()
        runBlocking { withTimeout(10000) { demoDepo.proDemo.first { it } } }
        closeProIfOpen()
    }

    private fun librarySearch(query: String) {
        compose.onNodeWithTag("share-library-search").performScrollTo().performTextReplacement(query)
        compose.onNodeWithTag("share-library-search").performImeAction()
        compose.waitForIdle()
    }

    private fun chooseLibraryArtwork(key: String) {
        compose.onNodeWithTag("share-artwork-grid").performScrollTo()
            .performScrollToNode(hasTestTag("share-background-topic-$key"))
        compose.onNodeWithTag("share-background-topic-$key").performClick()
    }

    private fun saveGalleryAndVerify() {
        val before = galleryImageIds()
        compose.waitUntil(15000) { compose.onAllNodes(hasText("Galeriye kaydet") and isEnabled()).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Galeriye kaydet").performClick()
        compose.waitUntil(20000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        assertNewStoryPng(before)
    }

    @Test fun primaryActionsAndThreeDestinationsStayVisibleWithoutScrolling() {
        listOf("home-plan", "home-moment", "home-save", "home-share", "home-more", "quote-next")
            .forEach { compose.onNodeWithTag(it).assertIsDisplayed().assertHasClickAction() }
        listOf("nav-ana", "nav-kategori", "nav-istatistik")
            .forEach { compose.onNodeWithTag(it).assertIsDisplayed().assertHasClickAction() }
        compose.onNodeWithTag("nav-favori").assertDoesNotExist()
        shot("06-home")
        recordTouchState("home-save", "before-save")
        compose.onNodeWithTag("home-save").performClick()
        val saved = withTouchEvidence("save") {
            // Yield to Compose's clock while the click launches the DataStore write.
            // Blocking on a future emission can prevent that frame from advancing.
            compose.waitUntil(10000) { runBlocking { demoDepo.favoriler.first().size == 1 } }
            runBlocking { demoDepo.favoriler.first().single() }
        }
        compose.onNodeWithTag("home-save").assertIsSelected()
        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithTag("profile-saved").assertIsDisplayed().performClick()
        compose.onNodeWithText(Sozler.kimlikten(saved)!!.metin("tr")).assertExists()
        shot("10-saved")
        compose.onNodeWithTag("nav-kategori").performClick()
        compose.onNodeWithTag("category-search").assertIsDisplayed()
        shot("11-discover")
        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithTag("profile-plan").assertIsDisplayed()
        shot("12-profile")
        compose.onNodeWithTag("journey-week").performScrollTo().assertIsDisplayed()
        shot("12-journey")
    }

    @Test fun planPanelOpensEditorCategoriesAndSettingsWithoutChangingSavedAnswers() {
        val original = runBlocking { demoDepo.personalProfile.first() }
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("personal-plan-panel").assertIsDisplayed()
        shot("18-personal-plan")
        compose.onNodeWithTag("plan-edit").performScrollTo().performClick()
        compose.onNodeWithTag("onboarding-root").assertDoesNotExist()
        compose.onNodeWithTag("content-avoid-work").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("plan-close").performScrollTo().performClick()
        compose.onNodeWithTag("plan-close").performScrollTo().performClick()
        waitForHome()
        assertEquals("Opening and cancelling the editor must not replace the saved plan", original,
            runBlocking { demoDepo.personalProfile.first() })
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("plan-categories").performScrollTo().performClick()
        compose.onNodeWithTag("category-search").assertIsDisplayed()
        compose.onNodeWithTag("nav-ana").performClick()
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("plan-settings").performScrollTo().performClick()
        compose.onNodeWithTag("plan-reminders-toggle").assertIsDisplayed()
    }

    @Test fun momentChoiceChangesOnlyTheCurrentFeed() {
        val original = runBlocking { demoDepo.personalProfile.first() }
        val selected = runBlocking { demoDepo.secili.first() }
        recordTouchState("home-moment", "before-moment")
        compose.onNodeWithTag("home-moment").performClick()
        withTouchEvidence("moment") {
            compose.waitUntil(10000) { compose.onAllNodesWithTag("moment-calm").fetchSemanticsNodes().isNotEmpty() }
        }
        compose.onNodeWithTag("moment-calm").performScrollTo().performClick()
        compose.onNodeWithTag("home-moment").assertTextContains("Biraz sakinlik", substring = true)
        compose.onNodeWithTag("active-quote").assertIsDisplayed()
        assertEquals(original, runBlocking { demoDepo.personalProfile.first() })
        assertEquals("Momentary calm must not replace reminder topics", selected, runBlocking { demoDepo.secili.first() })
        shot("19-moment-calm")
        compose.onNodeWithTag("home-moment").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithTag("moment-plan").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("moment-plan").performClick()
        compose.onNodeWithTag("home-moment").assertTextContains("Sana göre", substring = true)
    }

    @Test fun freeStoryPngExportsWhilePaidVideoRemainsExplicitlyGated() {
        openShare()
        shot("07-share-image")
        chooseShareBackground("paper")
        compose.onNodeWithTag("share-image").performScrollTo().assertIsSelected()
        saveGalleryAndVerify()
        shot("09-gallery-saved")
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        compose.onNodeWithTag("pro-sheet").assertIsDisplayed()
        assertFalse(runBlocking { demoDepo.proDemo.first() })
        shot("20-pro")
        closeProIfOpen()
        compose.onNodeWithTag("share-image").performScrollTo().assertIsSelected()
    }

    @Test fun collectionArtworkIsSearchableProGatedAndRestoresAfterRecreation() {
        val originalSelection = runBlocking { demoDepo.secili.first() }
        openShare()
        chooseShareFilter("koleksiyon")
        compose.onNodeWithTag("share-library-count").performScrollTo().assertTextEquals("70 görsel")
        librarySearch("motivasyon")
        compose.onNodeWithTag("share-library-count").assertTextEquals("1 görsel")
        chooseLibraryArtwork("motivasyon")
        assertFalse("Selecting collection artwork must not silently grant Pro", runBlocking { demoDepo.proDemo.first() })
        enableProFromGate()
        val access = runBlocking { withTimeout(10000) { demoDepo.acik.first { it.size == 70 } } }
        assertEquals(Kategoriler.tumAltlar.map { it.anahtar }.toSet(), access)
        assertEquals("Pro changes access, not the user's plan selection", originalSelection, runBlocking { demoDepo.secili.first() })
        chooseLibraryArtwork("motivasyon")
        compose.onNodeWithTag("share-background-topic-motivasyon").assertIsSelected()
        librarySearch("")
        compose.onNodeWithTag("share-library-count").assertTextEquals("70 görsel")
        compose.onNodeWithTag("share-artwork-grid").performScrollTo()
            .performScrollToNode(hasTestTag("share-background-topic-kuran"))
        compose.onNodeWithTag("share-background-topic-kuran").assertExists()
        librarySearch("motivasyon")
        chooseLibraryArtwork("motivasyon")
        shot("24-artwork-library")
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("share-library-search").performScrollTo().assertTextEquals("motivasyon")
        compose.onNodeWithTag("share-artwork-grid").performScrollTo()
        compose.onNodeWithTag("share-background-topic-motivasyon").assertIsSelected()
        compose.onNodeWithTag("share-preview").performScrollTo()
        shot("25-collection-share")
    }

    @Test fun knightPngAndVideoDurationSurviveRecreation() {
        openShare()
        chooseShareFilter("efsane")
        chooseShareBackground("kale_nobeti")
        enableProFromGate()
        chooseShareBackground("kale_nobeti")
        compose.onNodeWithTag("share-background-kale_nobeti").assertIsSelected()
        compose.onNodeWithTag("share-selected-background").assertTextEquals("Seçili: Kale nöbeti")
        compose.onNodeWithTag("share-preview").performScrollTo()
        shot("26-knight-share")
        saveGalleryAndVerify()
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        compose.onNodeWithTag("share-duration-45").performScrollTo().performClick()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("share-selected-background").performScrollTo().assertTextEquals("Seçili: Kale nöbeti")
        compose.onNodeWithTag("share-video").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("share-duration-45").performScrollTo().assertIsSelected()
        shot("08-share-video")
        compose.onNodeWithText("PRO DEMO").performClick()
        compose.onNodeWithTag("pro-demo-disable").performClick()
        runBlocking { withTimeout(10000) { demoDepo.proDemo.first { !it } } }
        closeProIfOpen()
        compose.onNodeWithTag("share-image").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("share-duration-45").assertDoesNotExist()
        compose.onNodeWithTag("share-filter-ucretsiz").performScrollTo().assertIsSelected()
    }

    @Test fun browsingTopicsKeepsRemindersUntilTheDetailSwitchChanges() {
        val original = runBlocking { demoDepo.secili.first() }
        val originalProfile = runBlocking { demoDepo.personalProfile.first() }
        val unlocked = runBlocking { demoDepo.acik.first() }
        val chosen = Kategoriler.tumAltlar.first { it.anahtar in original }.anahtar
        val other = Kategoriler.tumAltlar.first { it.anahtar in unlocked && it.anahtar !in original }.anahtar
        compose.onNodeWithTag("nav-kategori").performClick()
        compose.onNodeWithTag("category-filter-all").performClick()
        compose.onNodeWithTag("category-count").assertTextEquals("70 konu")
        compose.onNodeWithTag("category-filter-selected").performClick().assertIsSelected()
        compose.onNodeWithTag("category-count").assertTextEquals("${original.size} konu")
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-$chosen"))
        compose.onNodeWithTag("category-$chosen").performClick()
        compose.onNodeWithTag("category-reminder-$chosen").assertIsOn()
        assertEquals("Opening a selected topic must not deselect it", original, runBlocking { demoDepo.secili.first() })
        compose.onNodeWithTag("category-detail-close").performClick()

        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-filter-all"))
        compose.onNodeWithTag("category-filter-all").performClick()
        compose.onNodeWithTag("category-group-filter").performClick()
        compose.onNodeWithTag("category-filter-open-access").performClick().assertIsSelected()
        compose.onNodeWithTag("category-filters").performScrollToNode(hasTestTag("category-apply-filters"))
        compose.onNodeWithTag("category-apply-filters").performClick()
        compose.onNodeWithTag("category-count").assertTextEquals("${unlocked.size} konu")
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-$other"))
        compose.onNodeWithTag("category-$other").performClick()
        compose.onNodeWithTag("category-reminder-$other").assertIsOff()
        val lastQuote = Sozler.kategoriden(other).last()
        compose.onNodeWithTag("category-detail").performScrollToNode(hasTestTag("category-quote-${lastQuote.kimlik}"))
        compose.onNodeWithTag("category-quote-${lastQuote.kimlik}").assertTextEquals(lastQuote.metin("tr"))
        assertEquals("Reading every unlocked quote must not subscribe to reminders", original, runBlocking { demoDepo.secili.first() })
        compose.onNodeWithTag("category-detail").performScrollToNode(hasTestTag("category-reminder-$other"))
        compose.onNodeWithTag("category-reminder-$other").performClick()
        compose.waitUntil(10000) { runBlocking { other in demoDepo.secili.first() } }
        compose.onNodeWithTag("category-reminder-$other").assertIsOn()
        assertEquals("The explicit switch adds exactly one topic", original + other, runBlocking { demoDepo.secili.first() })
        compose.onNodeWithTag("category-reminder-$other").performClick()
        compose.waitUntil(10000) { runBlocking { other !in demoDepo.secili.first() } }
        assertEquals(original, runBlocking { demoDepo.secili.first() })
        assertEquals("Browsing and manual topics must preserve personal answers", originalProfile,
            runBlocking { demoDepo.personalProfile.first() })
    }

    @Test fun externalDemoUnlocksOnlyTheChosenIndividualCategoryOnReturn() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val beforeDemo = runBlocking { demoDepo.acik.first() }
        val selectionBeforeDemo = runBlocking { demoDepo.secili.first() }
        assertFalse("The chosen topic must initially be locked", "ozguven" in beforeDemo)
        assertFalse("A neighbouring topic must initially be locked", "korku" in beforeDemo)
        compose.onNodeWithTag("nav-kategori").performClick()
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-filter-all"))
        compose.onNodeWithTag("category-filter-all").performClick()
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-ozguven"))
        compose.onNodeWithTag("category-ozguven").performClick()
        compose.onNodeWithTag("category-detail-access").assertTextEquals("10 özgün söz · 2 sözlük önizleme")
        assertEquals("Browsing a locked preview must not change reminder topics", selectionBeforeDemo,
            runBlocking { demoDepo.secili.first() })
        shot("21-category-detail")
        compose.onNodeWithText("Bu kategoriyi aç · Demo").performScrollTo().performClick()
        compose.onNodeWithText("GEÇİCİ DEMO").assertIsDisplayed()
        compose.onNodeWithText("Bu sürümde gerçek reklam yok.", substring = true).assertIsDisplayed()
        shot("16-demo")
        val activityBeforeDemo = compose.activity
        compose.onNodeWithText("Demo bağlantısını aç").performClick()
        compose.waitUntil(15000) {
            var leftApp = false
            instrumentation.runOnMainSync { leftApp = activityBeforeDemo.lifecycle.currentState == Lifecycle.State.CREATED }
            leftApp
        }
        assertFalse("Leaving the app must not unlock before returning", runBlocking { demoDepo.acik.first() }.contains("ozguven"))
        val command = "am start -W --activity-reorder-to-front -n " + instrumentation.targetContext.packageName + "/" + MainActivity::class.java.name
        val result = android.os.ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command))
            .use { String(it.readBytes()) }
        assertFalse("System return failed: $result", result.contains("Error:"))
        compose.waitUntil(15000) {
            var resumed = false
            instrumentation.runOnMainSync { resumed = activityBeforeDemo.lifecycle.currentState == Lifecycle.State.RESUMED }
            resumed
        }
        val afterDemo = runBlocking { withTimeout(15000) { demoDepo.acik.first { "ozguven" in it } } }
        assertEquals("Only the chosen individual topic may unlock", beforeDemo + "ozguven", afterDemo)
        assertFalse("A neighbouring topic must stay locked", "korku" in afterDemo)
        assertEquals("Unlocking access must leave reminder topics unchanged", selectionBeforeDemo,
            runBlocking { demoDepo.secili.first() })
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-filter-all"))
        compose.onNodeWithTag("category-filter-all").performClick()
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-ozguven"))
        compose.onNodeWithTag("category-ozguven")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Erişime açık"))
        shot("17-demo-unlocked")
    }

    @Test fun darkThemeAndLanguageCanBeChangedFromPersonalCenter() {
        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithTag("profile-settings").performClick()
        compose.onNodeWithText("Karanlık").performScrollTo().performClick()
        runBlocking { withTimeout(5000) { demoDepo.tema.first { it == TemaModu.KARANLIK } } }
        shot("28-settings-dark")
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("nav-ana").performClick()
        waitForHome()
        shot("13-home-dark")
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("plan-settings").performScrollTo().performClick()
        compose.onNodeWithText("English").performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty() }
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("nav-ana").performClick()
        waitForHome()
        compose.onNodeWithTag("home-moment").assertTextContains("For you", substring = true)
        compose.onNodeWithText("Demo tamamlandı.", substring = true).assertDoesNotExist()
        shot("15-home-english")
    }

    @Test fun appearanceChoicesStayInTheNewPaletteAndPersistAcrossRecreation() {
        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithTag("profile-settings").performClick()
        compose.onNodeWithText("Mermer").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Mürekkep").assertExists()
        compose.onNodeWithText("Bordo").assertExists()
        listOf("Kum", "Lacivert", "Yosun", "Duvar kağıdı renkleri").forEach {
            compose.onNodeWithText(it).assertDoesNotExist()
        }
        shot("27-settings-light")
        compose.onNodeWithText("Mürekkep").performScrollTo().performClick()
        runBlocking { withTimeout(5000) { demoDepo.palet.first { it == Palet.MONO } } }
        compose.onNodeWithText("Mürekkep").assertIsSelected()
        compose.onNodeWithText("Bordo").performClick()
        runBlocking { withTimeout(5000) { demoDepo.palet.first { it == Palet.BORDO } } }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Ayarlar").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Bordo").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Mermer").performClick()
        runBlocking { withTimeout(5000) { demoDepo.palet.first { it == Palet.MERMER } } }
        compose.onNodeWithText("Mermer").assertIsSelected()
    }
}
