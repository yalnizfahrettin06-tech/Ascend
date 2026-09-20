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

class AccessPreviewTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun lockedCategoryPreviewsTwoQuotesBeforePro() {
        val topic = Kategoriler.tumAltlar.first { it.anahtar !in Erisim.ucretsizKategoriler && Sozler.kategoriden(it.anahtar).size >= 3 }
        var proOpened = false
        var openedQuote = false
        compose.setContent { AzimTema { KategorilerEkrani(emptySet(), Erisim.ucretsizKategoriler,"tr",{}, {},false,{ proOpened = true },oku = { openedQuote = true }) } }
        compose.onNodeWithTag("category-search").performTextInput(topic.ad("tr"))
        compose.onNodeWithTag("category-${topic.anahtar}").performClick()
        compose.onNodeWithTag("category-detail").assertExists()
        val quotes = Sozler.kategoriden(topic.anahtar)
        compose.onNodeWithTag("category-detail").performScrollToNode(hasTestTag("category-quote-${quotes[1].kimlik}"))
        compose.onNodeWithTag("category-quote-${quotes[1].kimlik}").assertIsDisplayed()
        compose.onNodeWithTag("category-quote-${quotes[2].kimlik}").assertDoesNotExist()
        compose.onNodeWithTag("category-reminder-${topic.anahtar}").assertDoesNotExist()
        compose.runOnIdle { assertFalse(proOpened); assertFalse(openedQuote) }
        ekranKaydet("category-pro-two-quote-preview")
        compose.onNodeWithTag("category-detail").performScrollToNode(hasTestTag("category-preview-pro"))
        compose.onNodeWithTag("category-preview-pro").performClick()
        compose.runOnIdle { assertTrue(proOpened) }
    }
    @Test fun bothArtworkThemesCanBeAppliedWithoutPro() {
        var selected by mutableStateOf("white")
        var proOpened = false
        compose.setContent { AzimTema { GorunumEkrani("tr",selected,false,{ proOpened = true },{ selected = it }) } }
        listOf("roma", "rider").forEach { id ->
            compose.onNodeWithTag("appearance-gallery").performScrollToNode(hasTestTag("theme-$id"))
            compose.onNodeWithTag("theme-$id").performClick()
            compose.onNodeWithTag("theme-apply").assertIsDisplayed().performClick()
            compose.runOnIdle { assertEquals(id,selected); assertFalse(proOpened) }
        }
    }
}
