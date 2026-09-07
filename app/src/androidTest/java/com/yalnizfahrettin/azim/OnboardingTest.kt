package com.yalnizfahrettin.azim

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.io.File

class OnboardingTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private fun label(id: Int) = compose.activity.getString(id)
    private fun click(id: Int) = compose.onNodeWithText(label(id)).performClick()
    private fun screenshot(name: String) {
        compose.waitForIdle()
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(ctx.getExternalFilesDir(null), "screenshots/$name.png")
        file.parentFile?.mkdirs()
        InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot().let { image ->
            file.outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
            image.recycle()
        }
    }
    @Test fun skipRemindersCompletesWithSelectionAndNoOptIn() {
        var completed = false
        compose.setContent { AzimTema(modu = TemaModu.AYDINLIK) { Onboarding("en") { selected, count, start, end, enabled ->
            assertEquals(Baslangic.varsayilan, selected); assertEquals(1, count)
            assertEquals(9, start); assertEquals(21, end); assertFalse(enabled); completed = true
        } } }
        screenshot("01-welcome-light")
        click(R.string.asc_kisisellestir)
        screenshot("02-intentions-light")
        click(R.string.ob_devam)
        screenshot("03-reminders-light")
        click(R.string.asc_simdilik_atla)
        compose.runOnIdle { assertTrue(completed) }
    }
    @Test fun emptySelectionDisablesContinueAndBackPreservesChoice() {
        compose.setContent { AzimTema { Onboarding("en") { _,_,_,_,_ -> } } }
        click(R.string.asc_kisisellestir)
        Baslangic.varsayilan.forEach { key -> compose.onNodeWithText(Kategoriler.bul(key)!!.ad("en")).performClick() }
        compose.onNodeWithText(label(R.string.ob_devam)).assertIsNotEnabled()
        compose.onNodeWithText(Kategoriler.bul("kendine_guven")!!.ad("en")).performClick()
        click(R.string.ob_devam)
        click(R.string.asc_geri)
        compose.onNodeWithText(Kategoriler.bul("kendine_guven")!!.ad("en")).assertIsOn()
    }
    @Test fun explicitOptInIsPreserved() {
        var optedIn = false
        compose.setContent { AzimTema { Onboarding("en") { _,_,_,_,enabled -> optedIn = enabled } } }
        click(R.string.asc_kisisellestir); click(R.string.ob_devam); click(R.string.asc_hatirlatici_ac)
        compose.runOnIdle { assertTrue(optedIn) }
    }
    @Test fun restorationKeepsTheCurrentStep() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { AzimTema { Onboarding("en") { _,_,_,_,_ -> } } }
        click(R.string.asc_kisisellestir)
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText(label(R.string.asc_niyet)).assertExists()
    }
    @Test fun largeTextKeepsFooterAccessible() {
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) { Onboarding("en") { _,_,_,_,_ -> } }
        } }
        screenshot("04-welcome-dark-large-text")
        compose.onNodeWithText(label(R.string.asc_kisisellestir)).assertIsDisplayed().performClick()
        click(R.string.ob_devam)
        compose.onNodeWithText(label(R.string.asc_simdilik_atla)).assertIsDisplayed()
        screenshot("05-reminders-dark-large-text")
    }
    @Test fun homeHasVisibleActionsAndHonestNotificationState() {
        val words = Sozler.akis(Baslangic.varsayilan)
        compose.setContent { AzimTema(modu = TemaModu.AYDINLIK) { AnaEkran(
            words, 0, emptySet(), 1, List(7) { false }, emptyList(), emptyMap(), 0, 1, null, null, 1, "en", false, false,
            {}, {}, {}, {}, {}, {}, {},
        ) } }
        compose.onNodeWithText(label(R.string.favoriye_ekle)).assertExists()
        screenshot("06-today-light")
        compose.onNodeWithText(label(R.string.asc_bildirim_kapali)).performScrollTo().assertIsDisplayed()
    }
}
