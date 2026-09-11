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

    @Test fun quickStartDoesNotRequestPermission() {
        var done = false
        var requested = false
        compose.setContent { AzimTema { Onboarding("en", izinIste = { requested = true }) { selected, count, start, end, enabled ->
            assertEquals(Kategoriler.varsayilanSecili, selected)
            assertEquals(3, count); assertEquals(9, start); assertEquals(21, end)
            assertFalse(enabled); done = true
        } } }
        compose.onNodeWithTag("onboarding-quick-start").performClick()
        compose.runOnIdle { assertTrue(done); assertFalse(requested) }
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

    @Test fun deniedPermissionStillAllowsStarting() {
        var completed = false
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 4)) { _, _, _, _, reminders ->
            assertFalse(reminders); completed = true
        } } }
        next()
        compose.onNodeWithTag("onboarding-finish-without-reminders").performClick()
        compose.runOnIdle { assertTrue(completed) }
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
        compose.onNodeWithTag("onboarding-finish-without-reminders").assertIsDisplayed()
    }
}
