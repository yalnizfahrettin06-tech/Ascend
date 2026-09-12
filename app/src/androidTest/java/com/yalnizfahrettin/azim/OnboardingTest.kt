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
    private fun next() = compose.onNodeWithTag("onboarding-next").performScrollTo().performClick()

    @Test fun setupHasOneFooterActionWithoutSkippingPermission() {
        var done = false
        compose.setContent { AzimTema { Onboarding("en") { _, _, _, _, _ -> done = true } } }
        compose.onNodeWithTag("onboarding-quick-start").assertDoesNotExist()
        compose.onNodeWithTag("onboarding-next").performScrollTo().assertIsDisplayed()
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

    @Test fun scheduleAndFinalPreviewTrackCountAndHourChanges() {
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 2)) { _, _, _, _, _ -> } } }
        compose.onNodeWithTag("live-reminder-preview").assertDoesNotExist()
        compose.onNodeWithContentDescription("More reminders").performScrollTo().performClick()
        compose.onNodeWithTag("reminder-count").assertTextEquals("4")
        next()
        compose.onNodeWithText("10:30").assertExists()
        compose.onNodeWithTag("reminder-start").performScrollTo().performClick()
        compose.onNodeWithTag("hour-choice-13").performScrollTo().performClick()
        compose.onNodeWithText("14:00").assertExists()
        compose.onNodeWithTag("live-reminder-preview").assertDoesNotExist()
        next()
        compose.onNodeWithTag("live-reminder-time", useUnmergedTree = true).assertTextEquals("14:00")
    }

    @Test fun notificationSampleExpandsAndCollapses() {
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 4)) { _, _, _, _, _ -> } } }
        compose.onNodeWithTag("live-reminder-preview").performScrollTo().performClick()
        compose.onNodeWithText("Collapse").assertExists()
        compose.onNodeWithTag("live-reminder-preview").performClick()
        compose.onNodeWithText("Read full quote").assertExists()
    }

    @Test fun practiceSupportsSwipingAndAccessibleAlternativeWithoutChangingSetup() {
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 1)) { _, _, _, _, _ -> } } }
        val first = "You do not have to finish everything today."
        compose.onNodeWithTag("practice-quote-text").assertTextEquals(first)
        compose.onNodeWithTag("practice-like").performScrollTo().performClick()
        compose.onNodeWithText("Sample liked").assertExists()
        compose.onNodeWithTag("practice-next").performScrollTo().performClick()
        compose.onNodeWithTag("practice-quote-text").assertTextEquals("A small step is still a step forward.")
        compose.onNodeWithTag("practice-deck").performScrollTo().performTouchInput { swipeLeft() }
        compose.onNodeWithTag("practice-quote-text").assertTextEquals("Speak to yourself as you would to someone you love.")
        compose.onNodeWithText("2 / 5").assertIsDisplayed()
    }

    @Test fun actionStaysCloseToContentOnEveryPage() {
        compose.setContent { AzimTema { Onboarding("en") { _, _, _, _, _ -> } } }
        repeat(5) { page ->
            compose.onNodeWithTag("onboarding-next").performScrollTo()
            val body = compose.onNodeWithTag("onboarding-body").fetchSemanticsNode().boundsInRoot
            val footer = compose.onNodeWithTag("onboarding-footer").fetchSemanticsNode().boundsInRoot
            val gap = (footer.top - body.bottom) / compose.activity.resources.displayMetrics.density
            assertTrue("Page $page has a detached action: $gap dp", gap in 23f..25f)
            if (page < 4) next()
        }
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
        compose.onNodeWithTag("onboarding-next").performScrollTo().assertIsDisplayed()
    }

    @Test fun captureFiveTurkishPagesAndExpandedNotification() {
        compose.setContent { AzimTema { Onboarding("tr") { _, _, _, _, _ -> } } }
        repeat(5) { page ->
            compose.onNodeWithTag("onboarding-next").performScrollTo().assertIsDisplayed()
            compose.waitForIdle()
            ekranKaydet("v96-onboarding-${page + 1}")
            if (page < 4) next()
        }
        compose.onNodeWithTag("live-reminder-preview").performScrollTo().performClick()
        compose.waitForIdle()
        ekranKaydet("v96-notification-expanded")
    }

    @Test fun allLargeTextPagesRemainScrollableAndDarkModeReadable() {
        var page by mutableIntStateOf(0)
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                key(page) { AzimTema(modu = TemaModu.KARANLIK) {
                    Onboarding("tr", initialDraft = PersonalProfile(step = page)) { _, _, _, _, _ -> }
                } }
            }
        }
        repeat(5) { current ->
            compose.runOnIdle { page = current }
            compose.onNodeWithTag("onboarding-next").performScrollTo().assertIsDisplayed()
            compose.waitForIdle()
            ekranKaydet("v96-large-dark-${current + 1}")
        }
    }
}
