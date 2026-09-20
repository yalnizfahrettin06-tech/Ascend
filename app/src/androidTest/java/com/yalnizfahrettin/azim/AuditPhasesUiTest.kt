package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test

class AuditPhasesUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun everyLocaleHasPersistentActionAtLargeFontOnEveryStep() {
        var locale by mutableStateOf("tr")
        var scale by mutableFloatStateOf(1f)
        var page by mutableIntStateOf(0)
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, scale)) {
                key(locale, scale, page) { AzimTema {
                    Box(Modifier.fillMaxWidth().height(560.dp)) {
                        Onboarding(locale, initialDraft = PersonalProfile(step = page)) { _,_,_,_,_ -> }
                    }
                } }
            }
        }
        listOf("tr","en","pt","de","fr","it","ru").forEach { lang ->
            listOf(1f, 1.3f, 2f).forEach { size ->
                repeat(5) { step ->
                    compose.runOnIdle { locale = lang; scale = size; page = step }
                    val action = compose.onNodeWithTag("onboarding-next").assertIsDisplayed().assertHasClickAction().assertHeightIsAtLeast(56.dp)
                    val root = compose.onNodeWithTag("onboarding-root").getUnclippedBoundsInRoot()
                    val bounds = action.getUnclippedBoundsInRoot()
                    org.junit.Assert.assertTrue("Action outside root: $lang / $size / $step", bounds.bottom <= root.bottom && bounds.top >= root.top)
                }
            }
        }
    }

    @Test fun lastSavedQuoteCanBeRestored() {
        val quote = Sozler.tumu().first()
        var saved by mutableStateOf(listOf(quote))
        compose.setContent { AzimTema {
            FavorilerEkrani(saved, "en", cikar = { saved = emptyList() }, oku = {}, kesfet = {},
                restore = { _, _ -> saved = listOf(quote) })
        } }
        compose.onNodeWithTag("saved-remove-${quote.kimlik}").performScrollTo().performClick()
        compose.onNodeWithText("Undo").performClick()
        compose.onNodeWithTag("saved-quote-${quote.kimlik}").assertExists()
    }

    @Test fun searchBackFirstClearsSearchThenReturnsToCollections() {
        compose.setContent { AzimTema {
            KategorilerEkrani(emptySet(), Erisim.ucretsizKategoriler, "en", {}, {}, false, {})
        } }
        compose.onNodeWithTag("collection-feature").performClick()
        compose.onNodeWithTag("category-search").performTextInput("Marcus")
        compose.onNodeWithTag("category-search").performImeAction()
        compose.onNodeWithText(JourneyCopy.text("allResults","en")).assertIsDisplayed()
        compose.onNodeWithTag("discovery-back").performClick()
        compose.onNodeWithTag("collection-feature").assertDoesNotExist()
        compose.onNodeWithTag("discovery-back").performClick()
        compose.onNodeWithTag("collection-feature").assertExists()
    }
}
