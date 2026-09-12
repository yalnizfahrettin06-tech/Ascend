package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.ui.PaylasimEkrani
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Sozler
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class ShareRegressionTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private fun open(scale: Float) {
        compose.setContent {
            val d = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(d.density,scale)) {
                AzimTema(modu = TemaModu.KARANLIK) { PaylasimEkrani(Sozler.tumu().first(),"tr",{},pro = true) }
            }
        }
        compose.waitUntil(30000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
    }
    private fun buttonsFit() {
        val area = compose.onNodeWithTag("share-safe-content").fetchSemanticsNode().boundsInRoot
        listOf("share-save-device","share-export").forEach { tag ->
            val node = compose.onNodeWithTag(tag).assertIsDisplayed().assertIsEnabled()
            val bounds = node.fetchSemanticsNode().boundsInRoot
            assertTrue("$tag below safe area",bounds.bottom <= area.bottom)
            assertTrue("$tag above safe area",bounds.top >= area.top)
        }
    }
    @Test fun imageAndVideoCanActuallyBeSaved() {
        open(1f)
        buttonsFit()
        ekranKaydet("share-safe-normal")
        compose.onNodeWithTag("share-save-device").performClick()
        compose.waitUntil(120000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        buttonsFit()
        compose.onNodeWithTag("share-save-device").performClick()
        compose.waitUntil(180000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        ekranKaydet("share-video-saved")
    }
    @Test fun largeTextKeepsFooterReachable() {
        open(1.8f)
        buttonsFit()
        ekranKaydet("share-safe-large-text")
    }
}
