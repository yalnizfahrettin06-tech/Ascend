package com.yalnizfahrettin.azim

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.Depo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
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
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val demoDepo = Depo(instrumentation.targetContext)
        val beforeDemo = runBlocking { withTimeout(5000) { demoDepo.acikGruplar.first() } }
        assertFalse("The chosen collection must initially be locked", "cesaret" in beforeDemo)
        assertFalse("The unrelated collection must initially be locked", "inanc" in beforeDemo)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Özgüven & Cesaret"))
        compose.onNodeWithText("Özgüven & Cesaret").performClick()
        compose.onNodeWithText("GEÇİCİ DEMO").assertIsDisplayed()
        compose.onNodeWithText("Bu sürümde gerçek reklam yok.", substring = true).assertIsDisplayed()
        shot("16-demo")
        val activityBeforeDemo = compose.activity
        compose.onNodeWithText("Demo bağlantısını aç").performClick()
        // Check the actual Activity lifecycle on its main thread. The external browser
        // is not controlled; no page interaction or browser shutdown is needed.
        compose.waitUntil(15000) {
            var leftApp = false
            instrumentation.runOnMainSync {
                leftApp = activityBeforeDemo.lifecycle.currentState != Lifecycle.State.RESUMED
            }
            leftApp
        }
        assertFalse("Leaving the app must not unlock before returning",
            runBlocking { withTimeout(5000) { demoDepo.acikGruplar.first() } }.contains("cesaret"))
        instrumentation.runOnMainSync {
            instrumentation.targetContext.startActivity(
                Intent(instrumentation.targetContext, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )
        }
        val afterDemo = runBlocking {
            withTimeout(15000) { demoDepo.acikGruplar.first { "cesaret" in it } }
        }
        assertEquals("Only the selected collection may unlock", beforeDemo + "cesaret", afterDemo)
        assertFalse("The unrelated collection must stay locked", "inanc" in afterDemo)
        compose.waitUntil(10000) {
            compose.onAllNodesWithText("50 söz · 5 seçili").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("50 söz · 5 seçili").assertIsDisplayed()
        shot("17-demo-unlocked")
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
