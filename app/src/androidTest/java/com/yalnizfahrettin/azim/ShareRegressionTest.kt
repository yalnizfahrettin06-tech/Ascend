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
        val resolver = compose.activity.contentResolver
        fun count(uri: android.net.Uri): Int = resolver.query(uri,arrayOf("_id"),"${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?",arrayOf("Ascend%"),null)?.use { it.count } ?: 0
        val images = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val videos = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val beforeImage = count(images)
        compose.onNodeWithTag("share-save-device").performClick()
        compose.waitUntil(120000) { count(images) > beforeImage }
        compose.waitForIdle()
        compose.onNodeWithTag("share-video").performScrollTo().performClick()
        buttonsFit()
        val beforeVideo = count(videos)
        compose.onNodeWithTag("share-save-device").performClick()
        compose.waitUntil(180000) { count(videos) > beforeVideo }
        compose.waitForIdle()
        ekranKaydet("share-video-saved")
    }
    @Test fun largeTextKeepsFooterReachable() {
        open(1.8f)
        buttonsFit()
        ekranKaydet("share-safe-large-text")
    }
}
