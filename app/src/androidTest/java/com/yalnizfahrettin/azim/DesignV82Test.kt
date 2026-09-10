
package com.yalnizfahrettin.azim

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class DesignV82Test {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private fun shot(name: String) { compose.waitForIdle(); ekranKaydet("v84-" + name) }

    @Test fun collectionSearchDetailAndRecreationPreservePositionAndSelection() {
        val restoration = StateRestorationTester(compose)
        var selected by mutableStateOf(setOf("ozsefkat", "motivasyon"))
        restoration.setContent {
            AzimTema { KategorilerEkrani(selected, Erisim.ucretsizKategoriler, "tr",
                { selected = if (it in selected) selected - it else selected + it }, {}, false, {}, bildirimAcik = false) }
        }
        compose.onNodeWithTag("collection-olumlamalar").assertIsDisplayed()
        compose.onNodeWithTag("collection-olumlamalar").assertTextContains("1 konu seçili")
        compose.onNodeWithTag("collection-azim").assertTextContains("1 konu seçili")
        compose.onNodeWithTag("category-selection-summary").assertTextContains("2 konu seçili · Bildirimler kapalı")
        shot("collections")
        compose.onNodeWithTag("category-grid").performScrollToIndex(3)
        compose.onNodeWithTag("category-search").assertIsDisplayed()
        compose.onNodeWithTag("category-grid").performScrollToIndex(0)
        compose.onNodeWithTag("collection-olumlamalar").performClick()
        compose.onNodeWithTag("category-count").assertTextEquals("3 konu")
        compose.onNodeWithTag("category-search").performTextInput("MARCUS")
        compose.onNodeWithTag("category-search").performImeAction()
        compose.onNodeWithTag("category-marcus").performScrollTo().performClick()
        compose.onNodeWithTag("category-detail-access").assertTextEquals("10 özgün söz · Erişime açık")
        compose.runOnIdle { assertEquals(setOf("ozsefkat", "motivasyon"), selected) }
        compose.onNodeWithTag("category-detail-close").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("category-search").assertTextContains("MARCUS")
        compose.onNodeWithTag("category-clear-search").performClick()
        compose.onNodeWithTag("category-count").assertTextEquals("3 konu")
        compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("category-back-collections"))
        compose.onNodeWithTag("category-back-collections").performClick()
        compose.onNodeWithTag("collection-olumlamalar").assertIsDisplayed()
        compose.onNodeWithTag("category-selection-summary").performClick()
        compose.onNodeWithTag("category-delivery-off").assertIsDisplayed()
        shot("selected-delivery-off")
        compose.onNodeWithTag("category-count").assertTextEquals("2 konu")
    }

    @Test fun emptySelectionOffersTopicsAndOpeningACollectionDoesNotSelectIt() {
        var selected by mutableStateOf(emptySet<String>())
        compose.setContent {
            AzimTema { KategorilerEkrani(selected, Erisim.ucretsizKategoriler, "tr",
                { selected = selected + it }, {}, false, {}, bildirimAcik = false) }
        }
        compose.onNodeWithTag("category-selection-summary").assertTextContains("Bildirim konularını seç").performClick()
        compose.onNodeWithTag("category-count").assertTextEquals("70 konu")
        compose.onNodeWithTag("category-filter-collections").performClick()
        compose.onNodeWithTag("collection-olumlamalar").performClick()
        compose.runOnIdle { assertTrue(selected.isEmpty()) }
    }

    @Test fun typographyMatrixKeepsFullQuotesAndPlanSourcesReachable() {
        var scale by mutableFloatStateOf(1f)
        var screen by mutableIntStateOf(0)
        var width by mutableIntStateOf(411)
        var dark by mutableStateOf(false)
        // Exact content shown in the v8.1 baseline on the same Pixel 2 device.
        val quote = Sozler.kategoriden("azim").first { it.metin("tr").startsWith("Çabanın karşılığını") }
        compose.setContent {
            val original = LocalDensity.current
            val config = Configuration(LocalConfiguration.current).apply {
                setLocale(java.util.Locale.forLanguageTag("tr"))
                screenWidthDp = width
            }
            CompositionLocalProvider(LocalDensity provides Density(original.density, scale), LocalConfiguration provides config) {
                AzimTema(modu = if (dark) TemaModu.KARANLIK else TemaModu.AYDINLIK) {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {
                        Column(Modifier.width(width.dp).fillMaxHeight()) {
                            Box(Modifier.weight(1f)) {
                                when (screen) {
                                    0 -> AnaEkran(listOf(quote), 0, emptySet(), 0, List(7) { false },
                                        emptyList(), emptyMap(), 0, 3, null, null, 0, "tr", false, false,
                                        {}, {}, {}, {}, {}, {}, {})
                                    1 -> KategorilerEkrani(setOf("ozsefkat"), Erisim.ucretsizKategoriler, "tr", {}, {}, false, {})
                                    2 -> Onboarding("tr", initialDraft = PersonalProfile(step = 17)) { _,_,_,_,_ -> }
                                    4 -> Onboarding("tr", initialDraft = PersonalProfile(step = 18)) { _,_,_,_,_ -> }
                                    5 -> Onboarding("tr", initialDraft = PersonalProfile(step = 19)) { _,_,_,_,_ -> }
                                    6 -> Onboarding("tr", initialDraft = PersonalProfile(step = 15)) { _,_,_,_,_ -> }
                                    7 -> Onboarding("tr", initialDraft = PersonalProfile(step = 16)) { _,_,_,_,_ -> }
                                    else -> Onboarding("tr") { _,_,_,_,_ -> }
                                }
                            }
                            if (screen <= 1) AltNav(if (screen == 0) Sekme.ANA else Sekme.KATEGORI) {}
                        }
                    }
                }
            }
        }
        for (value in listOf(1f, 1.3f, 1.5f, 2f)) {
            compose.runOnIdle { scale = value; width = if (value >= 1.5f) 320 else 411; screen = 0 }
            compose.onNodeWithTag("home-save").assertIsDisplayed()
            compose.onNodeWithTag("home-share").assertIsDisplayed()
            compose.onNodeWithTag("nav-ana").assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(quote.metin("tr"), useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertFalse("Full Turkish quote must wrap at scale " + value, layouts.single().hasVisualOverflow)
            shot("home-" + value)
            compose.runOnIdle { screen = 1 }
            compose.onNodeWithTag("category-grid").performScrollToNode(hasTestTag("collection-azim"))
            compose.onNodeWithTag("collection-azim").assertIsDisplayed()
            shot("library-" + value)
            compose.runOnIdle { screen = 2 }
            compose.onNodeWithTag("onboarding-preview-source").performScrollTo().assertIsDisplayed()
            val source = compose.onNodeWithTag("onboarding-preview-source").fetchSemanticsNode().boundsInRoot
            val footer = compose.onNodeWithTag("onboarding-next").fetchSemanticsNode().boundsInRoot
            assertTrue("The full source must be above the footer", source.bottom <= footer.top)
            shot("plan-source-" + value)
        }
        compose.runOnIdle { scale = 1f; width = 411; screen = 3 }
        shot("welcome-tr")
        for ((view, name) in listOf(4 to "access-tr", 5 to "permission-tr", 6 to "frequency-tr", 7 to "hours-tr")) {
            compose.runOnIdle { screen = view }
            compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
            shot(name)
        }
        for (narrow in listOf(320, 360)) {
            compose.runOnIdle { width = narrow; screen = 0 }
            compose.onNodeWithTag("home-save").assertIsDisplayed()
            compose.onNodeWithTag("home-share").assertIsDisplayed()
            shot("home-width-" + narrow)
        }
        compose.runOnIdle { dark = true; width = 411; screen = 1 }
        shot("library-dark")
    }
}
