package com.yalnizfahrettin.azim

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

fun ekranKaydet(name: String) {
    Thread.sleep(500)
    val ui = InstrumentationRegistry.getInstrumentation().uiAutomation
    fun shell(command: String) { android.os.ParcelFileDescriptor.AutoCloseInputStream(ui.executeShellCommand(command)).use { it.readBytes() } }
    shell("mkdir -p /sdcard/Download/ascend-screenshots")
    shell("screencap -p /sdcard/Download/ascend-screenshots/$name.png")
}

class UygulamaTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun shot(name: String) { compose.waitForIdle(); ekranKaydet(name) }
    @Test fun realNavigationShareAndLanguage() {
        compose.waitUntil(15000) { compose.onAllNodesWithText("Kendi yolunu oluştur →").fetchSemanticsNodes().isNotEmpty() }
        shot("01-welcome")
        compose.onNodeWithText("Kendi yolunu oluştur →").performClick(); shot("02-topics")
        compose.onNodeWithText("Devam").performClick(); shot("03-schedule")
        compose.onNodeWithText("Devam").performClick(); shot("04-permission")
        compose.onNodeWithText("Samsung: Ayrıntılı görünüm").performScrollTo().performClick()
        compose.onNodeWithText("Görünüm ayarlarını aç ↗").performScrollTo(); shot("05-detailed-notification-guide")
        compose.onNodeWithText("Şimdilik bildirimsiz devam et").performClick()
        compose.waitUntil(15000) { compose.onAllNodesWithText("Senin için").fetchSemanticsNodes().isNotEmpty() }
        shot("06-home")
        compose.onNode(hasText("Kaydet") and hasAnyAncestor(hasTestTag("active-quote"))).performScrollTo().performClick()
        compose.onNode(hasText("Paylaş") and hasAnyAncestor(hasTestTag("active-quote"))).performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        shot("07-share-image")
        compose.onNodeWithText("Video").performScrollTo().performClick()
        compose.onNodeWithText("45s").performScrollTo().performClick()
        compose.runOnUiThread { compose.activity.recreate() }
        compose.waitUntil(10000) { compose.onAllNodesWithText("45s").fetchSemanticsNodes().isNotEmpty() }
        compose.waitUntil(15000) { compose.onAllNodesWithTag("share-preview").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("45s").performScrollTo().assertIsSelected(); shot("08-share-video")
        compose.onNodeWithContentDescription("Kâğıt").performScrollTo().performClick()
        compose.onNodeWithText("Görsel").performClick()
        compose.onNodeWithText("Galeriye kaydet").performClick()
        compose.waitUntil(20000) { compose.onAllNodesWithText("Galeriye kaydedildi ✓").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Galeriye kaydedildi ✓").assertIsDisplayed()
        shot("09-gallery-saved")
        compose.onNodeWithContentDescription("Kapat").performClick()
        compose.onNodeWithText("Kaydedilen").performClick()
        compose.onNodeWithText("Sende kalan sözler.").assertIsDisplayed(); shot("10-saved")
        compose.onNodeWithText("Keşfet").performClick()
        compose.onNodeWithText("İlhamını keşfet.").assertIsDisplayed(); shot("11-discover")
        compose.onNodeWithText("Yolculuk").performClick()
        compose.onNodeWithText("Kendi yolunda.").assertIsDisplayed(); shot("12-journey")
        compose.onNodeWithText("Bugün").performClick()
        compose.onNodeWithContentDescription("Ayarlar").performScrollTo().performClick()
        compose.onNodeWithText("Karanlık").performScrollTo().performClick()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Senin için").assertExists(); shot("13-home-dark")
        compose.onNodeWithContentDescription("Ayarlar").performClick()
        compose.onNodeWithText("English").performScrollTo().performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty() }
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("For you").assertExists(); shot("15-home-english")
    }
}
