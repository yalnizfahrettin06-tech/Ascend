package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.ui.BildirimPlani
import com.yalnizfahrettin.azim.ui.Onboarding
import com.yalnizfahrettin.azim.data.PersonalProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BildirimPlaniV6Test {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun countAndHourChoicesStayWithinSchedulerBounds() {
        var count by mutableIntStateOf(3)
        var start by mutableIntStateOf(9)
        var end by mutableIntStateOf(21)
        compose.setContent {
            AzimTema {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    BildirimPlani(count, start, end, 3, { count = it }, { b, s -> start = b; end = s }, dil = "en")
                }
            }
        }
        listOf("11:00", "15:00", "19:00").forEach { compose.onNodeWithText(it).assertExists() }
        repeat(4) { compose.onNodeWithContentDescription("More reminders").performScrollTo().performClick() }
        compose.onNodeWithContentDescription("More reminders").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(7, count) }
        repeat(6) { compose.onNodeWithContentDescription("Fewer reminders").performScrollTo().performClick() }
        compose.onNodeWithContentDescription("Fewer reminders").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(1, count) }
        compose.onNodeWithTag("reminder-end").performScrollTo().performClick()
        compose.onNodeWithTag("hour-choice-24").performScrollTo().performClick()
        compose.onNodeWithTag("reminder-start").performScrollTo().performClick()
        compose.onNodeWithTag("hour-choice-23").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(23, start); assertEquals(24, end) }
        compose.onNodeWithText("23:30").assertExists()
        compose.onNodeWithTag("reminder-end").performScrollTo().performClick()
        compose.onNodeWithTag("hour-choice-23").assertDoesNotExist()
        compose.onNodeWithTag("hour-choice-24").assertIsSelected()
    }

    @Test fun largeTextAndRecreationKeepTimeChoiceUsable() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            AzimTema(modu = TemaModu.KARANLIK) {
                val density = LocalDensity.current
                var count by rememberSaveable { mutableIntStateOf(3) }
                var start by rememberSaveable { mutableIntStateOf(9) }
                var end by rememberSaveable { mutableIntStateOf(21) }
                CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        BildirimPlani(count, start, end, 3, { count = it }, { b, s -> start = b; end = s }, dil = "en")
                    }
                }
            }
        }
        compose.onNodeWithTag("reminder-end").performScrollTo().performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("hour-choice-24").performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText("Midnight").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("11:30").assertExists()
        compose.onNodeWithText("16:30").assertExists()
        compose.onNodeWithText("21:30").assertExists()
        ekranKaydet("20-large-text-reminder-plan")
    }

    @Test fun stepViewportStaysBelowProgressWhenScrolling() {
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 16)) { _, _, _, _, _ -> } } }
        compose.onNodeWithText("Which hours work for you?").assertIsDisplayed()
        val progress = compose.onNodeWithTag("onboarding-progress").fetchSemanticsNode().boundsInRoot
        val viewport = compose.onNodeWithTag("onboarding-scroll").fetchSemanticsNode().boundsInRoot
        assertTrue("The scrolling content must not overlap the progress row", viewport.top >= progress.bottom)
        compose.onNodeWithTag("reminder-preview-times").performScrollTo()
        val scrolledViewport = compose.onNodeWithTag("onboarding-scroll").fetchSemanticsNode().boundsInRoot
        assertTrue(scrolledViewport.top >= progress.bottom)
    }
}
