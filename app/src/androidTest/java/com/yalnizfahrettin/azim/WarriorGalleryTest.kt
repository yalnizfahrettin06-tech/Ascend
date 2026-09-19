package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.ui.*
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class WarriorGalleryTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun freePreviewKeepsProGateAndGalleryIsLazy() {
        var proOpened = false
        var selected = "white"
        compose.setContent { AzimTema { GorunumEkrani("tr",selected,false,{ proOpened = true },{ selected = it }) } }
        compose.onNodeWithTag("theme-featured").assertIsDisplayed()
        compose.onNodeWithTag("theme-${AnaTemalar.all.last().id}").assertDoesNotExist()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("theme-art-emperor",useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
        ekranKaydet("warrior-gallery-light")
        compose.onNodeWithTag("theme-featured").performClick()
        compose.onNodeWithTag("living-collection").assertExists()
        compose.onNodeWithTag("collection-use").performClick()
        compose.runOnIdle { assertTrue(proOpened); assertEquals("white",selected) }
        ekranKaydet("warrior-emperor-preview")
    }
    @Test fun proThemeCanBeAppliedWithLargeFrenchText() {
        var selected by mutableStateOf("black")
        compose.setContent {
            val d = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(d.density,1.6f)) {
                AzimTema(modu = TemaModu.KARANLIK) { GorunumEkrani("fr",selected,true,{}, { selected = it }) }
            }
        }
        compose.onNodeWithTag("appearance-gallery").performScrollToNode(hasTestTag("theme-duel"))
        compose.onNodeWithTag("theme-duel").assertIsDisplayed().performClick()
        compose.onNodeWithTag("theme-apply").performScrollTo().assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals("duel",selected) }
        compose.onNodeWithTag("theme-duel").assertIsSelected()
        ekranKaydet("warrior-gallery-large-french")
    }
}
