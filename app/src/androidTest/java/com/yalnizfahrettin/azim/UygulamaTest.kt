package com.yalnizfahrettin.azim

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Exercises real routing, persistence and localized resources, beyond isolated screens. */
class UygulamaTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun screenshot(name: String) {
        compose.waitForIdle()
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        fun shell(command: String) {
            android.os.ParcelFileDescriptor.AutoCloseInputStream(automation.executeShellCommand(command)).use { it.readBytes() }
        }
        // Gradle uninstalls the app after testing, so app-scoped files would be deleted.
        shell("mkdir -p /sdcard/Download/ascend-screenshots")
        shell("screencap -p /sdcard/Download/ascend-screenshots/$name.png")
    }
    @Test fun onboardingNavigationSavedContentAndLanguage() {
        compose.waitUntil(10000) { compose.onAllNodesWithText("Bana göre düzenle").fetchSemanticsNodes().isNotEmpty() }
        screenshot("07-gercek-karsilama-tr")
        compose.onNodeWithText("Bana göre düzenle").performClick()
        screenshot("08-gercek-niyetler-tr")
        compose.onNodeWithText("Devam").performClick()
        screenshot("09-gercek-hatirlatici-tr")
        compose.onNodeWithText("Şimdilik bildirimsiz devam et").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Bugün kendin için").fetchSemanticsNodes().isNotEmpty() }
        screenshot("10-gercek-bugun-tr")
        compose.onNodeWithText("Kaydet").performScrollTo().performClick()
        compose.onNodeWithText("Kaydedilen").performClick()
        compose.onNodeWithText("Kaydedilenler").assertIsDisplayed()
        screenshot("11-gercek-kaydedilenler-tr")
        compose.onNodeWithText("Keşfet").performClick()
        compose.onNodeWithText("Sana iyi gelen konular").assertIsDisplayed()
        screenshot("12-gercek-kesfet-tr")
        compose.onNodeWithText("Yolculuk").performClick()
        compose.onNodeWithText("Yolculuğun").assertIsDisplayed()
        screenshot("13-gercek-yolculuk-tr")
        compose.onNodeWithText("Bugün").performClick()
        compose.onNodeWithContentDescription("Ayarlar").performClick()
        screenshot("14-gercek-ayarlar-tr")
        compose.onNodeWithText("English").performScrollTo().performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Settings").assertIsDisplayed()
        screenshot("15-gercek-settings-en")
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("A moment for yourself").assertExists()
    }
}
