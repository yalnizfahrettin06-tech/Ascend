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
    @Test fun quickStartUsesRealFreePlanAndNeverOptsIn() {
        var done = false
        compose.setContent { AzimTema { Onboarding("en") { selected, count, start, end, enabled ->
            assertEquals(Kategoriler.varsayilanSecili, selected); assertEquals(3, count)
            assertEquals(9, start); assertEquals(21, end); assertFalse(enabled); done = true
        } } }
        compose.onNodeWithText("Build your path").assertIsDisplayed()
        ekranKaydet("01-v8-onboarding-welcome")
        compose.onNodeWithTag("onboarding-quick-start").performClick()
        compose.onNodeWithText("Your starting point.").assertIsDisplayed()
        ekranKaydet("04-v8-onboarding-plan")
        next(); next()
        compose.onNodeWithTag("onboarding-finish-without-reminders").performClick()
        compose.runOnIdle { assertTrue(done) }
    }
    @Test fun everyQuestionCanBeSkippedAndFooterStaysAccessible() {
        var final: PersonalProfile? = null
        compose.setContent { AzimTema { Onboarding("en", finishProfile = { profile, _ -> final = profile }) { _, _, _, _, _ -> } } }
        next()
        compose.onNodeWithTag("onboarding-skip").performClick()
        PersonalPlan.questions.forEach { question ->
            compose.onNodeWithTag("onboarding-question-${question.id}").assertIsDisplayed()
            compose.onNodeWithTag("onboarding-skip").performClick()
        }
        compose.onNodeWithTag("reminder-count").assertExists()
        repeat(4) { next() }
        compose.onNodeWithTag("onboarding-finish-without-reminders").performClick()
        compose.runOnIdle { assertNotNull(final); assertTrue(final!!.answers.isEmpty()) }
    }
    @Test fun backAndRecreationPreserveAnswersAndSavedDraftStep() {
        var draft = PersonalProfile()
        val restoration = StateRestorationTester(compose)
        restoration.setContent { AzimTema { Onboarding("en", draftChanged = { draft = it }) { _,_,_,_,_ -> } } }
        next(); compose.onNodeWithTag("onboarding-name").performTextInput("Ada")
        next(); compose.onNodeWithTag("onboarding-option-format-affirmation").performClick(); next()
        compose.runOnIdle { assertEquals("Ada", draft.name); assertEquals(3, draft.step); assertEquals(setOf("affirmation"), draft.answer("format")) }
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("onboarding-question-goal").assertIsDisplayed()
        ekranKaydet("02-v8-onboarding-question")
        compose.onNodeWithTag("onboarding-back").performClick()
        compose.onNodeWithTag("onboarding-option-format-affirmation").assertIsSelected()
        compose.onNodeWithTag("onboarding-skip").performClick()
        compose.runOnIdle { assertTrue(draft.answer("format").isEmpty()) }
    }
    @Test fun suppliedPersistentDraftResumesWithoutReplayingWelcome() {
        val draft = PersonalProfile(step = 5).choose("format", "reflection").choose("tone", "thoughtful")
        compose.setContent { AzimTema { Onboarding("en", initialDraft = draft) { _,_,_,_,_ -> } } }
        compose.onNodeWithTag("onboarding-question-tone").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-option-tone-thoughtful").assertIsSelected()
        compose.onNodeWithTag("onboarding-back").performClick()
        compose.onNodeWithTag("onboarding-question-energy").assertIsDisplayed()
    }
    @Test fun planPreviewUsesActualEarnedOrProAccess() {
        val profile = PersonalProfile(step = 17).choose("goal", "action")
        compose.setContent { AzimTema { Onboarding("en", initialDraft = profile, previewAccess = Erisim.tumKategoriler) { _,_,_,_,_ -> } } }
        compose.onNodeWithText("Topics in your plan").assertIsDisplayed()
        compose.onNodeWithText("Procrastination", substring = true).assertExists()
        compose.onNodeWithText("These topics unlock separately.", substring = true).assertDoesNotExist()
    }
    @Test fun permissionRequestDoesNotCompleteUntilExplicitStart() {
        var permission by mutableStateOf(false)
        var requests = 0; var done = false
        compose.setContent { AzimTema { Onboarding("en", initialDraft = PersonalProfile(step = 19), bildirimIzni = permission,
            izinIste = { requests++; permission = true }) { _,_,_,_,enabled -> assertTrue(enabled); done = true } } }
        compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
        ekranKaydet("05-v8-onboarding-permission")
        next()
        compose.runOnIdle { assertEquals(1, requests); assertFalse(done) }
        compose.onNodeWithText("Start Ascend").assertIsDisplayed(); next()
        compose.runOnIdle { assertTrue(done) }
    }
    @Test fun largeTextCanReachPermissionHelpAndAlwaysFinishWithoutPermission() {
        var done = false
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                Onboarding("en", initialDraft = PersonalProfile(step = 19)) { _,_,_,_,enabled -> assertFalse(enabled); done = true }
            }
        } }
        compose.onNodeWithTag("onboarding-finish-without-reminders").assertIsDisplayed()
        compose.onNodeWithText("Notification appearance and device settings").performScrollTo().performClick()
        compose.onNodeWithText("Got it").performClick()
        ekranKaydet("14-large-text-notifications")
        compose.onNodeWithTag("onboarding-finish-without-reminders").performClick()
        compose.runOnIdle { assertTrue(done) }
    }
    @Test fun nameKeyboardDoneSavesDraftAndMovesToFirstQuestion() {
        var draft = PersonalProfile(step = 1)
        compose.setContent { AzimTema { Onboarding("en", initialDraft = draft, draftChanged = { draft = it }) { _,_,_,_,_ -> } } }
        compose.onNodeWithTag("onboarding-name").performTextInput("Çağrı")
        compose.onNodeWithTag("onboarding-name").performImeAction()
        compose.onNodeWithTag("onboarding-question-format").assertIsDisplayed()
        compose.runOnIdle { assertEquals("Çağrı", draft.name); assertEquals(2, draft.step) }
    }
    @Test fun multipleChoicesExposeCheckboxStatesAndSurviveRecreationAtLargeText() {
        var draft = PersonalProfile(step = 8)
        val restoration = StateRestorationTester(compose)
        restoration.setContent { AzimTema {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                Onboarding("en", initialDraft = draft, draftChanged = { draft = it }) { _,_,_,_,_ -> }
            }
        } }
        compose.onNodeWithTag("onboarding-option-context-work").performScrollTo().assertIsOff().performClick().assertIsOn()
        compose.onNodeWithTag("onboarding-option-context-study").performScrollTo().performClick().assertIsOn()
        compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("onboarding-option-context-work").performScrollTo().assertIsOn().performClick().assertIsOff()
        compose.onNodeWithTag("onboarding-selection-count").performScrollTo().assertTextEquals("1 selected · Choose more than one")
        compose.runOnIdle { assertEquals(setOf("study"), draft.answer("context")) }
        ekranKaydet("24-v8-large-text-question")
    }
}
