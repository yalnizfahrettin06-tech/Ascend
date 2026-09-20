package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.ui.*
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class VisualUnityTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun discoveryCoversNavigateToTopics() {
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            KesifMerkezi("tr") { KategorilerEkrani(emptySet(),Erisim.ucretsizKategoriler,"tr",{},{},false,{},insets = false,series = {}) }
        } }
        compose.waitUntil(10000) { compose.onAllNodesWithTag("theme-art-editorial-${EditorialArt.group("olumlamalar")}",useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        ekranKaydet("923-discovery-dark")
        compose.onNodeWithTag("collection-feature").performClick()
        compose.onNodeWithTag("category-ozsefkat").assertExists()
    }

    @Test fun savedCardReadsWithoutThemeDependency() {
        var read = false
        val quote = Sozler.kategoriden("zorluk_sabir").first()
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            FavorilerEkrani(listOf(quote),"tr",{}, { read = true },{},embedded = true)
        } }
        compose.waitForIdle()
        compose.waitUntil(10000) { compose.onAllNodesWithTag("theme-art-editorial-${EditorialArt.saved(quote.kategori)}",useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        ekranKaydet("923-saved-dark")
        compose.onNodeWithTag("saved-read-${quote.kimlik}").performClick()
        compose.runOnIdle { assertTrue(read) }
    }

    @Test fun seriesHaveDistinctCoversAndOnlyStartedSeriesShowProgress() {
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            KesifMerkezi("tr",showHeading = false) { KisaSerilerEkrani("tr",emptyMap(),emptySet(),{},{},{},{},{},insets = false) }
        } }
        compose.onAllNodesWithText("Kısa seriler").assertCountEquals(1)
        compose.onNodeWithTag("series-progress-kindness").assertDoesNotExist()
        compose.waitUntil(10000) { compose.onAllNodesWithTag("theme-art-editorial-${EditorialArt.series("steps")}",useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        ekranKaydet("923-series-dark")
        compose.onNodeWithTag("series-kindness").performScrollTo().performClick()
        compose.onNodeWithTag("series-start").performScrollTo().assertIsDisplayed()
        assertEquals(ShortSeries.all.size,ShortSeries.all.map { EditorialArt.series(it.id) }.distinct().size)
    }

    @Test fun largeGermanTextKeepsCollectionsUsableAndBrandIsVisible() {
        var brand by mutableStateOf(false)
        compose.setContent { AzimTema {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density,1.5f)) {
                if(brand) Surface { Row(Modifier.fillMaxWidth().padding(36.dp),horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Icon(AzimIkon.YukselenMarka,"Ascend",Modifier.size(96.dp))
                    Icon(AzimIkon.YukselenMarka,"Ascend small",Modifier.size(24.dp))
                } }
                else KategorilerEkrani(emptySet(),Erisim.ucretsizKategoriler,"de",{},{},false,{})
            }
        } }
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("collection-unlu_dusunurler"))
        compose.onNodeWithTag("collection-unlu_dusunurler").assertIsDisplayed()
        ekranKaydet("923-discovery-large-de")
        compose.onNodeWithTag("collection-unlu_dusunurler").performClick()
        compose.onNodeWithTag("category-marcus").assertExists()
        compose.runOnIdle { brand = true }
        compose.waitForIdle()
        ekranKaydet("923-brand")
    }
}
