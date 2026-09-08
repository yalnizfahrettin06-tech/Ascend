package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class OnboardingTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private fun next() = compose.onNodeWithText("Continue").performClick()
    private fun start() = compose.onNodeWithText("Make it yours →").performClick()
    @Test fun skipKeepsBalancedSelectionAndDoesNotOptIn() {
        var done = false
        compose.setContent { AzimTema { Onboarding("en") { selected, count, start, end, enabled ->
            assertEquals(Baslangic.varsayilan, selected); assertEquals(3, count)
            assertEquals(9, start); assertEquals(21, end); assertFalse(enabled); done = true
        } } }
        start(); next(); next()
        compose.onNodeWithText("Start Ascend").assertIsNotEnabled()
        compose.onNodeWithText("Continue without reminders").performClick()
        compose.runOnIdle { assertTrue(done) }
    }
    @Test fun usersCanReplaceAllDefaultsAndBackPreservesSelection() {
        compose.setContent { AzimTema { Onboarding("en") { _,_,_,_,_ -> } } }
        start()
        Baslangic.varsayilan.forEach { compose.onNodeWithText(baslangicAdi(it,"en")).performClick() }
        compose.onNodeWithText("Continue").assertIsNotEnabled()
        compose.onNodeWithText("Deep Focus").performScrollTo().performClick(); next()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Deep Focus").assertIsOn()
        compose.onNodeWithText("Continue").assertIsEnabled()
    }
    @Test fun permissionRequestDoesNotCompleteOnboardingUntilExplicitStart() {
        var permission by mutableStateOf(false)
        var requests = 0
        var done = false
        compose.setContent { AzimTema { Onboarding("en", bildirimIzni = permission, izinIste = { requests++; permission = true }) { _,_,_,_,enabled -> assertTrue(enabled); done = true } } }
        start(); next(); next()
        compose.onNodeWithText("Allow notifications").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1,requests); assertFalse(done) }
        compose.onNodeWithText("Start Ascend").performClick()
        compose.runOnIdle { assertTrue(done) }
    }
    @Test fun recreationRestoresStepAndTopicChoice() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { AzimTema { Onboarding("en") { _,_,_,_,_ -> } } }
        start(); compose.onNodeWithText("Deep Focus").performScrollTo().performClick(); next()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Set your daily rhythm.").assertExists()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Deep Focus").assertIsOn()
    }
    @Test fun largeTextCanReachNotificationSetupAndSkip() {
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) { Onboarding("en") { _,_,_,_,_ -> } }
        } }
        start(); next(); next()
        compose.onNodeWithText("Continue without reminders").assertIsDisplayed()
        compose.onNodeWithText("Samsung: Detailed pop-up").performScrollTo().performClick()
        compose.onNodeWithText("Open appearance settings ↗").performScrollTo().assertIsDisplayed()
        ekranKaydet("14-large-text-notifications")
    }
}
