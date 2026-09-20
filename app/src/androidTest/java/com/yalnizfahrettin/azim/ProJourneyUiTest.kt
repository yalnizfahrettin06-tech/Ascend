package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.ui.*
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class ProJourneyUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun chosenThemeIsShownAndAppliedAfterDemo() {
        var selected by mutableStateOf("white")
        var pro by mutableStateOf(false)
        var offer by mutableStateOf<ProOffer?>(null)
        compose.setContent { AzimTema {
            GorunumEkrani("tr",selected,pro,{}, { selected = it },offerOpen = { offer = it })
            offer?.let { request -> ProEkrani("tr",pro,kapat = { offer = null },degistir = { pro = it; offer = null },offer = request) }
        } }
        compose.onNodeWithTag("appearance-gallery").performScrollToNode(hasTestTag("theme-emperor"))
        compose.onNodeWithTag("theme-emperor").performClick()
        compose.onNodeWithTag("theme-apply").assertIsDisplayed().performClick()
        compose.onNodeWithTag("pro-context-title").assertTextEquals("İmparator")
        compose.runOnIdle { assertEquals("white",selected); assertFalse(pro) }
        ekranKaydet("pro-context-emperor")
        compose.onNodeWithTag("pro-demo-enable").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals("emperor",selected); assertTrue(pro) }
    }
    @Test fun topicAccessReturnsToSameTopicWithoutChangingReminderChoice() {
        var pro by mutableStateOf(false)
        var offer by mutableStateOf<ProOffer?>(null)
        var toggled = false
        val topic = Kategoriler.tumAltlar.first { it.anahtar !in Erisim.ucretsizKategoriler && Sozler.kategoriden(it.anahtar).size > 2 }
        compose.setContent { AzimTema {
            KategorilerEkrani(emptySet(),if(pro) Erisim.tumKategoriler else Erisim.ucretsizKategoriler,"tr",{ toggled = true },{},pro,{},offerOpen = { offer = it })
            offer?.let { request -> ProEkrani("tr",pro,kapat = { offer = null },degistir = { pro = it; offer = null },offer = request) }
        } }
        compose.onNodeWithTag("category-search").performTextInput(topic.ad("tr"))
        compose.onNodeWithTag("category-${topic.anahtar}").performClick()
        compose.onNodeWithTag("category-detail").performScrollToNode(hasTestTag("category-preview-pro"))
        compose.onNodeWithTag("category-preview-pro").performClick()
        compose.onNodeWithTag("pro-context-title").assertTextEquals(topic.ad("tr"))
        compose.onNodeWithTag("pro-demo-enable").performClick()
        compose.onNodeWithTag("category-detail").performScrollToNode(hasTestTag("category-reminder-${topic.anahtar}"))
        compose.onNodeWithTag("category-reminder-${topic.anahtar}").assertExists()
        compose.runOnIdle { assertFalse(toggled) }
    }
    @Test fun videoSelectionResumesOnlyAfterAccessIsEnabled() {
        var pro by mutableStateOf(false)
        var offer by mutableStateOf<ProOffer?>(null)
        var dismissed by mutableIntStateOf(0)
        val quote = Sozler.kategoriden("motivasyon").first()
        compose.setContent { AzimTema {
            PaylasimEkrani(quote,"tr",{},pro,offerOpen = { offer = it },offerDismissals = dismissed)
            offer?.let { request -> ProEkrani("tr",pro,kapat = { dismissed++; offer = null },degistir = { pro = it; offer = null },offer = request) }
        } }
        compose.onNodeWithTag("share-format-options").performClick()
        compose.onNodeWithTag("share-video").performClick()
        compose.runOnIdle { assertEquals(ProSource.VIDEO,offer?.source) }
        compose.onNodeWithTag("pro-close").performClick()
        compose.onNodeWithTag("share-image").assertIsSelected()
        compose.onNodeWithTag("share-video").performClick()
        compose.onNodeWithTag("pro-demo-enable").performClick()
        compose.onNodeWithTag("share-video").assertIsSelected()
    }
}
