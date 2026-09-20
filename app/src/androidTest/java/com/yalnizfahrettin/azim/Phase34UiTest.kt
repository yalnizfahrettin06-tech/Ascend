package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class Phase34UiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun collectionResumesTheChosenStillSceneAfterDemo() {
        var paid by mutableStateOf(false)
        var chosen = ""
        var offers = 0
        compose.setContent { AzimTema { LivingCollection("tr",paid,{}, { chosen = it },proOpen = { offers++ }) } }
        compose.onNodeWithTag("collection-motion").performScrollTo().performClick()
        compose.onNodeWithTag("collection-use").performClick()
        compose.runOnIdle { assertEquals(1,offers); assertEquals("",chosen); paid = true }
        compose.waitUntil(15000) { chosen == "emperor" }
    }
    @Test fun dismissedCollectionOfferDoesNotApplyLater() {
        var paid by mutableStateOf(false)
        var dismissed by mutableIntStateOf(0)
        var chosen = ""
        compose.setContent { AzimTema { LivingCollection("tr",paid,{}, { chosen = it },offerDismissals = dismissed,proOpen = {}) } }
        compose.onNodeWithTag("collection-use").performClick()
        compose.runOnIdle { dismissed++ }
        compose.waitForIdle()
        compose.runOnIdle { paid = true }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals("",chosen) }
    }
    @Test fun sevenLanguageHelpHasSafeResetCancellation() {
        var language by mutableStateOf("tr")
        compose.setContent { AzimTema { HelpAndData(language) } }
        PhaseCopy.languages.forEach { lang ->
            compose.runOnIdle { language = lang }
            compose.onNodeWithTag("settings-privacy").performClick()
            compose.onNodeWithText(PhaseCopy.text("privacyBody",lang)).assertExists()
            compose.onNodeWithTag("data-confirm").performClick()
            compose.onNodeWithTag("settings-reset").performClick()
            compose.onNodeWithText(PhaseCopy.text("resetBody",lang)).assertExists()
            compose.onNodeWithTag("data-cancel").performClick()
            compose.onNodeWithTag("settings-help").assertIsDisplayed()
        }
    }
    @Test fun sevenDayTitlesAreVisibleWithoutUnlockingTheirContent() {
        compose.setContent { AzimTema { RestartSeriesScreen("en",false,null,emptySet(),{},{},{},{},{},{}) } }
        compose.onNodeWithTag("restart-path").performScrollTo().performClick()
        compose.onNodeWithText(RestartSeries.days("en")[6].title).assertExists()
        compose.onNodeWithTag("restart-day-6").assertDoesNotExist()
        compose.onNodeWithTag("restart-story").assertTextEquals(RestartSeries.days("en")[0].story)
    }
    @Test fun allSevenLanguagesDescribeDemoWithoutFictionalTrial() {
        var language by mutableStateOf("tr")
        compose.setContent { AzimTema { DenemeTeklifi(language,false,false,{},{},{}) } }
        PhaseCopy.languages.forEach { lang ->
            compose.runOnIdle { language = lang }
            compose.onNodeWithText(PhaseCopy.text("demo",lang)).assertExists()
            compose.onNodeWithTag("trial-start").assertIsDisplayed().assertTextEquals(PhaseCopy.text("enable",lang))
            compose.onNodeWithText(PhaseCopy.text("demoBody",lang)).assertExists()
            compose.onNodeWithTag("trial-free").assertExists()
        }
    }
    @Test fun seriesCoverFitsLargeTitlesInAllLanguages() {
        var language by mutableStateOf("tr")
        compose.setContent {
            val density = androidx.compose.ui.platform.LocalDensity.current
            CompositionLocalProvider(androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density,2f)) {
                AzimTema { RestartSeriesScreen(language,false,null,emptySet(),{},{},{},{},{},{}) }
            }
        }
        PhaseCopy.languages.forEach { lang ->
            compose.runOnIdle { language = lang }
            val title = compose.onNodeWithTag("restart-title").getUnclippedBoundsInRoot()
            val cover = compose.onNodeWithTag("restart-cover").getUnclippedBoundsInRoot()
            assertTrue(lang,title.top >= cover.top && title.bottom <= cover.bottom)
            compose.onNodeWithTag("restart-action").assertIsDisplayed()
        }
    }
}
