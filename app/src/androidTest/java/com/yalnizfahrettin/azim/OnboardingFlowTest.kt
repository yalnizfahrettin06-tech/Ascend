package com.yalnizfahrettin.azim

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.data.Reminders
import com.yalnizfahrettin.azim.ui.OnboardingScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {
    @get:Rule val rule = createComposeRule()

    @Test fun chooseTopicsSkipPermissionAndFinish() {
        var result: Pair<Set<String>, Reminders>? = null
        rule.setContent {
            AzimTema { OnboardingScreen("en", permission = false, requestPermission = {}, setLanguage = {}) { t, r -> result = t to r } }
        }
        rule.onNodeWithTag("onboarding-next").performClick()
        rule.onNodeWithTag("onboarding-next").assertIsNotEnabled()
        rule.onNodeWithTag("setup-topic-azim").performScrollTo().performClick()
        rule.onNodeWithTag("onboarding-next").assertIsEnabled().performClick()
        rule.onNodeWithTag("per-day").performScrollTo()
        rule.onNodeWithTag("onboarding-next").performClick()
        rule.onNodeWithTag("onboarding-skip").performClick()
        rule.onNodeWithTag("onboarding-finish").performClick()
        assertEquals(setOf("azim"), result!!.first)
        assertEquals(3, result!!.second.perDay)
    }
}
