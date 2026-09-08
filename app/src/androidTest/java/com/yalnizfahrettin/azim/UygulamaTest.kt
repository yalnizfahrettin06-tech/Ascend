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
                demoDepo.favoriler.first().forEach { demoDepo.favoriDegistir(it) }
            }
        }
        compose.activityRule.scenario.recreate()
        waitForHome()
    }

    private fun waitForHome() {
        compose.waitUntil(15000) { compose.onAllNodesWithTag("active-quote").fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
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
        compose.onNodeWithTag("home-save").performClick()
        val saved = runBlocking { withTimeout(5000) { demoDepo.favoriler.first { it.size == 1 }.single() } }
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
        compose.onNodeWithTag("journey-week").performScrollTo().assertIsDisplayed()
        shot("12-journey")
    }

    @Test fun planPanelOpensEditorCategoriesAndSettingsWithoutChangingSavedAnswers() {
        val original = runBlocking { demoDepo.personalProfile.first() }
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("personal-plan-panel").assertIsDisplayed()
        shot("18-personal-plan")
        compose.onNodeWithTag("plan-edit").performScrollTo().performClick()
        compose.onNodeWithTag("onboarding-root").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-back").performClick()
        waitForHome()
        assertEquals("Opening and cancelling the editor must not replace the saved plan", original,
            runBlocking { demoDepo.personalProfile.first() })
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("plan-categories").performScrollTo().performClick()
        compose.onNodeWithTag("category-search").assertIsDisplayed()
        compose.onNodeWithTag("nav-ana").performClick()
        compose.onNodeWithTag("home-plan").performClick()
        compose.onNodeWithTag("plan-settings").performScrollTo().performClick()
        compose.onNodeWithText("Ayarlar").assertIsDisplayed()
    }

    @Test fun momentChoiceChangesOnlyTheCurrentFeed() {
        val original = runBlocking { demoDepo.personalProfile.first() }
        val selected = runBlocking { demoDepo.secili.first() }
        compose.onNodeWithTag("home-moment").performClick()
        compose.onNodeWithTag("moment-calm").performScrollTo().performClick()
        compose.onNodeWithTag("home-moment").assertTextContains("Biraz sakinlik", substring = true)
        compose.onNodeWithTag("active-quote").assertIsDisplayed()
        assertEquals(original, runBlocking { demoDepo.personalProfile.first() })
        assertEquals("Momentary calm must not replace reminder topics", selected, runBlocking { demoDepo.secili.first() })
        shot("19-moment-calm")
        compose.onNodeWithTag("home-moment").performClick()
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

    @Test fun externalDemoUnlocksOnlyTheChosenIndividualCategoryOnReturn() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val beforeDemo = runBlocking { demoDepo.acik.first() }
        val selectionBeforeDemo = runBlocking { demoDepo.secili.first() }
        assertFalse("The chosen topic must initially be locked", "ozguven" in beforeDemo)
        assertFalse("A neighbouring topic must initially be locked", "korku" in beforeDemo)
        compose.onNodeWithTag("nav-kategori").performClick()
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-ozguven"))
        compose.onNodeWithTag("category-ozguven").performClick()
        compose.onNodeWithText("10 özgün söz · Bir göz at").assertIsDisplayed()
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
        assertEquals("Only the rewarded topic may join reminders", selectionBeforeDemo + "ozguven",
            runBlocking { withTimeout(10000) { demoDepo.secili.first { "ozguven" in it } } })
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-ozguven"))
        compose.onNodeWithTag("category-ozguven")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Bildirimlerinde seçili"))
        shot("17-demo-unlocked")
    }

    @Test fun darkThemeAndLanguageCanBeChangedFromPersonalCenter() {
        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithTag("profile-settings").performClick()
        compose.onNodeWithText("Karanlık").performScrollTo().performClick()
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
}
