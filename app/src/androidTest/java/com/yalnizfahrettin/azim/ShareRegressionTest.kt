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
    private var fontScale by mutableStateOf(1f)
    private fun open(scale: Float) {
        fontScale = scale
        compose.setContent {
            val d = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(d.density,fontScale)) {
                AzimTema(modu = TemaModu.KARANLIK) { PaylasimEkrani(Sozler.tumu().first(),"tr",{},pro = true) }
            }
        }
        try {
            compose.waitUntil(60000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        } finally { ekranKaydet("share-open") }
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
        compose.onNodeWithTag("share-image").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("share-image").assertIsSelected()
        compose.onNodeWithTag("share-video").assertIsNotSelected()
        compose.onNodeWithText("Tümünü gör").performScrollTo().performClick()
        compose.onNodeWithTag("share-library-search").performTextInput("Marcus")
        compose.onNodeWithText("Sonuç bulunamadı.").assertIsDisplayed()
        compose.onNodeWithTag("share-library-search").performTextReplacement("İmparator")
        compose.onNodeWithTag("share-library-search").performImeAction()
        compose.onAllNodes(hasContentDescription("İmparator") and hasAnyAncestor(hasTestTag("share-artwork-grid"))).assertCountEquals(1)
        compose.onNode(hasContentDescription("İmparator") and hasAnyAncestor(hasTestTag("share-artwork-grid"))).performClick()
        compose.waitForIdle()
        ekranKaydet("share-safe-normal")
        val resolver = compose.activity.contentResolver
        fun count(uri: android.net.Uri): Int = resolver.query(uri,arrayOf("_id"),"${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} LIKE ? AND ${android.provider.MediaStore.MediaColumns.IS_PENDING} = 0",arrayOf("Ascend%"),null)?.use { it.count } ?: 0
        val images = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val videos = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val beforeImage = count(images)
        compose.onNodeWithTag("share-save-device").performClick()
        compose.waitUntil(120000) { count(images) > beforeImage }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        compose.onNodeWithTag("share-video").assertIsSelected()
        compose.onNodeWithTag("share-image").assertIsNotSelected()
        buttonsFit()
        val beforeVideo = count(videos)
        compose.onNodeWithTag("share-save-device").performClick()
        compose.waitUntil(180000) { count(videos) > beforeVideo }
        compose.waitUntil(10000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
        ekranKaydet("share-video-saved")
        compose.runOnIdle { fontScale = 1.8f }
        compose.waitForIdle()
        buttonsFit()
        ekranKaydet("share-safe-large-text")
    }
    @Test fun cancelledVideoReturnsToUsableActions() {
        open(1f)
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        compose.onNodeWithTag("share-save-device").performClick()
        compose.onNodeWithText("Hazırlanıyor · İptal").performClick()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-save-device").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("share-save-device").assertIsEnabled().assertIsDisplayed()
        compose.onNodeWithText(com.yalnizfahrettin.azim.data.JourneyCopy.text("cancelled","tr")).assertExists()
    }

}
