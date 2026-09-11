package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class PhaseTwoUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun exploreReaderReturnsToSameSearchAndDoesNotSelectReminder() {
        var reader by mutableStateOf<Soz?>(null)
        var saved by mutableStateOf(false)
        var selected = false
        compose.setContent { AzimTema {
            KategorilerEkrani(emptySet(), Erisim.ucretsizKategoriler, "en", { selected = true }, {}, false, {}, oku = { reader = it })
            reader?.let { q -> SozOkuyucu(q, "en", saved, { reader = null }, { saved = !saved }, {}) }
        } }
        compose.onNodeWithTag("category-search").performTextInput("Motivation")
        compose.onNodeWithTag("category-motivasyon").performClick()
        val q = Sozler.kategoriden("motivasyon").first()
        compose.onNodeWithTag("category-quote-${q.kimlik}").performScrollTo().performClick()
        compose.onNodeWithTag("quote-reader").assertIsDisplayed()
        compose.onNodeWithTag("reader-save").performClick()
        compose.onNodeWithTag("reader-back").performClick()
        compose.onNodeWithTag("category-quote-${q.kimlik}").assertIsDisplayed()
        compose.onNodeWithTag("category-detail-close").performScrollTo().performClick()
        compose.onNodeWithTag("category-search").assertTextContains("Motivation")
        compose.runOnIdle { assertTrue(saved); assertFalse(selected) }
    }
    @Test fun cancelContentEditDoesNotSaveOrOpenOnboarding() {
        var saves = 0
        compose.setContent { AzimTema {
            KisiselPlanPaneli(PersonalProfile(), Kategoriler.varsayilanSecili, Erisim.ucretsizKategoriler,
                "en", 3, 9, 21, false, {}, {}, { saves++ }, { _, _, _, _ -> saves++ })
        } }
        compose.onNodeWithTag("plan-edit").performClick()
        compose.onNodeWithTag("content-avoid-work").performScrollTo().performClick()
        compose.onNodeWithTag("plan-close").performScrollTo().performClick()
        compose.onNodeWithTag("plan-settings").assertIsDisplayed()
        compose.onNodeWithTag("onboarding-root").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, saves) }
    }
}
