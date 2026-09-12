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
    private fun next() = compose.onNodeWithTag("onboarding-next").performClick()

    @Test fun setupHasOneFooterActionWithoutSkippingPermission() {
        var done = false
        compose.setContent { AzimTema { Onboarding("en") { _, _, _, _, _ -> done = true } } }
        compose.onNodeWithTag("onboarding-quick-start").assertDoesNotExist()
        compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
        compose.runOnIdle { assertFalse(done) }
    }

    @Test fun fivePagesRequestPermissionOnlyOnFinalTap() {
        var allowed by mutableStateOf(false)
        var requests = 0
        var completed = false
        compose.setContent { AzimTema { Onboarding("en", bildirimIzni = allowed,
            izinIste = { requests++; allowed = true }) { _, _, _, _, reminders ->
            assertTrue(reminders); completed = true
        } } }
        repeat(4) { next() }
        compose.onNodeWithText("5 / 5").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, requests); assertFalse(completed) }
        next()
        compose.runOnIdle { assertEquals(1, requests); assertFalse(completed) }
        next()
        compose.runOnIdle { assertTrue(completed) }
    }

    @Test fun deniedPermissionKeepsSetupOpen() {
        var completed = false
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 4)) { _, _, _, _, _ -> completed = true } } }
        next()
        compose.onNodeWithTag("onboarding-finish-without-reminders").assertDoesNotExist()
        compose.runOnIdle { assertFalse(completed) }
    }

    @Test fun livePreviewTracksCountAndHourChanges() {
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 2)) { _, _, _, _, _ -> } } }
        compose.onNodeWithTag("live-reminder-time").assertTextEquals("11:00")
        compose.onNodeWithContentDescription("More reminders").performClick()
        compose.onNodeWithTag("live-reminder-time").assertTextEquals("10:30")
        compose.onNodeWithTag("live-reminder-summary").assertTextEquals("4 reminders daily · Approximate times")
        next()
        compose.onNodeWithTag("reminder-start").performClick()
        compose.onNodeWithTag("hour-choice-13").performScrollTo().performClick()
        compose.onNodeWithTag("live-reminder-time").assertTextEquals("14:00")
    }

    @Test fun rhythmSurvivesBackAndRestoration() {
        val restore = StateRestorationTester(compose)
        restore.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 2)) { _, _, _, _, _ -> } } }
        compose.onNodeWithContentDescription("More reminders").performClick()
        next()
        restore.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("4 / 5").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-back").performClick()
        compose.onNodeWithTag("reminder-count").assertTextEquals("4")
    }

    @Test fun legacyDraftAndLargeTextHaveReachableExit() {
        val old = PersonalProfile.decode("v=1&step=19&count=2&start=10&end=20")
        compose.setContent { val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                AzimTema { Onboarding("tr", initialDraft = old) { _, _, _, _, _ -> } }
            }
        }
        compose.onNodeWithText("5 / 5").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
    }
}
