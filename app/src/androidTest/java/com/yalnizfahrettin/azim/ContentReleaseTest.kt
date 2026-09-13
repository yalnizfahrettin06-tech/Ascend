package com.yalnizfahrettin.azim

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ContentReleaseTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun bilingualTopicsOpenAndNewSelectionPersists() {
        val depo = Depo(InstrumentationRegistry.getInstrumentation().targetContext)
        runBlocking {
            depo.proDemoAyarla(true)
            depo.completePersonalPlan(PersonalProfile(), false)
            depo.dilAyarla("tr")
            depo.temaAyarla(TemaModu.AYDINLIK)
            depo.kategorileriAyarla(setOf("motivasyon"))
        }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("active-quote").fetchSemanticsNodes().isNotEmpty() }
        compose.waitUntil(15000) { compose.runOnUiThread { compose.activity.hasWindowFocus() } }
        compose.onNodeWithText("Keşfet").performClick()
        compose.onNodeWithTag("category-search").assertIsDisplayed()
        compose.onNodeWithTag("category-marcus").assertDoesNotExist()
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("collection-unlu_dusunurler"))
        compose.onNodeWithTag("collection-unlu_dusunurler").performClick()
        compose.waitForIdle()
        ekranKaydet("content-thinkers-group")
        compose.onNodeWithTag("category-search").performTextInput("Düştükten")
        compose.onNodeWithTag("category-search").performImeAction()
        compose.onNodeWithTag("category-dustukten_sonra").assertIsDisplayed().performClick()
        compose.onNodeWithTag("category-detail").assertIsDisplayed()
        compose.onNodeWithTag("category-quote-v5_dustukten_sonra_01").assertIsDisplayed()
        ekranKaydet("content-new-topic-tr")
        compose.onNodeWithTag("category-reminder-dustukten_sonra").performScrollTo().performClick()
        compose.waitUntil(10000) { runBlocking { "dustukten_sonra" in depo.secili.first() } }
        compose.onNodeWithTag("category-detail-close").performScrollTo().performClick()
        runBlocking { depo.dilAyarla("en") }
        compose.onNodeWithTag("category-search").performTextReplacement("Creative courage")
        compose.onNodeWithTag("category-search").performImeAction()
        compose.onNodeWithTag("category-yaraticilik").assertIsDisplayed().performClick()
        compose.onNodeWithTag("category-quote-v5_yaraticilik_01").performScrollTo().assertIsDisplayed()
        ekranKaydet("content-new-topic-en")
        compose.activityRule.scenario.recreate()
        assertTrue(runBlocking { "dustukten_sonra" in depo.secili.first() })
        runBlocking { depo.proDemoAyarla(false) }
        assertFalse(runBlocking { "dustukten_sonra" in depo.secili.first() })
    }
}
