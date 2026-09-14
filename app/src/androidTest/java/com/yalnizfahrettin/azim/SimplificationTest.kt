package com.yalnizfahrettin.azim

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.notif.Bildirimler
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class SimplificationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun personalSpaceDiscoveryAndRepeatedNotificationOpen() {
        val depot=Depo(compose.activity)
        runBlocking { depot.completePersonalPlan(PersonalProfile(),false); depot.dilAyarla("tr"); depot.proDemoAyarla(false) }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15000) { compose.onAllNodesWithTag("active-quote").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Senin").performClick()
        compose.onNodeWithTag("saved-list").assertExists()
        compose.onNodeWithText("Özet").assertDoesNotExist()
        compose.onNodeWithTag("personal-tab-series").assertDoesNotExist()
        compose.onNodeWithTag("personal-tab-history").performClick()
        compose.onNodeWithTag("notification-history").assertExists()
        ekranKaydet("personal-simple-history")
        compose.onNodeWithText("Keşfet").performClick()
        compose.onNodeWithTag("discovery-series").performClick()
        compose.onNodeWithTag("short-series").assertExists()
        ekranKaydet("discovery-short-series")
        val quote=Sozler.kategoriden("motivasyon").first()
        val intent=Bildirimler.acilisNiyeti(compose.activity,quote.kimlik)
        assertNotEquals(intent.data,Bildirimler.acilisNiyeti(compose.activity,Sozler.kategoriden("motivasyon")[1].kimlik).data)
        repeat(2) {
            compose.runOnUiThread { compose.activity.startActivity(intent) }
            compose.waitUntil(15000) { compose.onAllNodesWithTag("quote-reader").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText(quote.tr).assertIsDisplayed()
            compose.onNodeWithTag("reader-back").performClick()
            compose.onNodeWithTag("quote-reader").assertDoesNotExist()
        }
        assertTrue(runBlocking { quote.kimlik in depot.recentQuotes.first() })
    }
}
