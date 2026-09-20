package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.ui.*
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class LivingExperienceTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun collectionPreviewsAreFreeAndApplyingRequiresPro() {
        var paid by mutableStateOf(false)
        var chosen = "white"
        var offers = 0
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            LivingCollection("tr",paid,{}, { chosen = it },proOpen = { offers++ })
        } }
        compose.onNodeWithTag("collection-use").performClick()
        compose.runOnIdle { assertEquals(1,offers); assertEquals("white",chosen) }
        compose.onNodeWithTag("collection-tab-1").performClick()
        compose.onNodeWithTag("collection-use").assertDoesNotExist()
        compose.onNodeWithText(CollectionCopy.text("preview","tr")).performScrollTo().assertExists()
        ekranKaydet("924-wallpaper-preview")
        compose.onNodeWithTag("collection-tab-2").performScrollTo().performClick()
        compose.onNodeWithTag("collection-tab-2").assertIsSelected()
        compose.waitForIdle()
        ekranKaydet("924-collection-widget")
        compose.onNodeWithTag("collection-tab-0").performScrollTo().performClick()
        compose.runOnIdle { paid = true }
        compose.onNodeWithTag("collection-use").performClick()
        compose.runOnIdle { assertEquals(AnaTemalar.living.id,chosen) }
    }
    @Test fun firstEditorialDayIsFreeAndProgressDoesNotLeakWhenProEnds() {
        var pro by mutableStateOf(false)
        var state by mutableStateOf<SeriesProgress?>(null)
        var offers = 0
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            Column(Modifier.fillMaxSize().safeDrawingPadding()) { RestartSeriesScreen("tr",pro,state,emptySet(),{}, { state = SeriesProgress("restart") },
                { state = state!!.complete(LocalDate.now()) },{},{},{ offers++ }) }
        } }
        compose.onNodeWithTag("restart-story").assertTextEquals(RestartSeries.days("tr")[0].story)
        ekranKaydet("924-series-first-day")
        compose.onNodeWithTag("restart-action").performClick()
        compose.runOnIdle { assertEquals(1,offers); assertNull(state); pro = true }
        compose.onNodeWithTag("restart-action").performClick()
        compose.runOnIdle { assertEquals(1,state!!.completed); state = state!!.copy(completed = 3,lastDay = LocalDate.now().minusDays(1)) }
        compose.onNodeWithTag("restart-story").assertTextEquals(RestartSeries.days("tr")[3].story)
        compose.runOnIdle { pro = false }
        compose.onNodeWithTag("restart-story").assertTextEquals(RestartSeries.days("tr")[0].story)
        compose.onNodeWithTag("restart-day-3").assertDoesNotExist()
        compose.runOnIdle { assertEquals(3,state!!.completed) }
    }
    @Test fun motionChangesPixelsAndPauseReturnsToStillArtwork() {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        fun animation(value: String) { android.os.ParcelFileDescriptor.AutoCloseInputStream(automation.executeShellCommand("settings put global animator_duration_scale $value")).use { it.readBytes() } }
        animation("1")
        var active by mutableStateOf(true)
        try {
            compose.mainClock.autoAdvance = false
            compose.setContent { AzimTema(modu = TemaModu.KARANLIK) { LivingScene(Modifier.size(240.dp,320.dp),active) } }
            compose.mainClock.advanceTimeBy(64)
            compose.waitUntil(15000) { compose.mainClock.advanceTimeBy(32); compose.onAllNodesWithTag("theme-art-emperor",useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("living-moving").assertExists()
            compose.mainClock.autoAdvance = false
            val first = compose.onNodeWithTag("living-moving").captureToImage().asAndroidBitmap()
            compose.mainClock.advanceTimeBy(6000)
            val second = compose.onNodeWithTag("living-moving").captureToImage().asAndroidBitmap()
            assertFalse("Visible scene should change pixels",first.sameAs(second))
            ekranKaydet("924-living-scene")
            compose.runOnUiThread { active = false }
            compose.mainClock.advanceTimeBy(64)
            compose.onNodeWithTag("living-still").assertExists()
        } finally { compose.mainClock.autoAdvance = true; animation("0") }
    }
}
