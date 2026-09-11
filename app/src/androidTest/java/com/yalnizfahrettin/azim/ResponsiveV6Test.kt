package com.yalnizfahrettin.azim

import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.ui.AltNav
import com.yalnizfahrettin.azim.ui.AnaEkran
import com.yalnizfahrettin.azim.ui.IstatistikEkrani
import com.yalnizfahrettin.azim.ui.Sekme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class ResponsiveV6Test {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun textFits(text: String, ancestorTag: String? = null) {
        val layouts = mutableListOf<TextLayoutResult>()
        val matcher = if (ancestorTag == null) hasText(text) else hasText(text) and hasAnyAncestor(hasTestTag(ancestorTag))
        compose.onNode(matcher, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertTrue("Expected a real text layout for $text", layouts.isNotEmpty())
        val layout = layouts.single()
        assertFalse(
            "The complete label must fit: label=$text, " +
                "size=${layout.size.width}x${layout.size.height}, " +
                "paragraph=${layout.multiParagraph.width}x${layout.multiParagraph.height}, " +
                "lineCount=${layout.lineCount}, " +
                "overflowW=${layout.didOverflowWidth}, overflowH=${layout.didOverflowHeight}",
            layout.hasVisualOverflow,
        )
    }

    private fun navigationFits() {
        val root = compose.onNodeWithTag("responsive-root").fetchSemanticsNode().boundsInRoot
        val content = compose.onNodeWithTag("responsive-content").fetchSemanticsNode().boundsInRoot
        val bounds = listOf(Sekme.ANA, Sekme.KATEGORI, Sekme.ISTATISTIK).map { tab ->
            val node = compose.onNodeWithTag("nav-${tab.rota}")
            node.assertIsDisplayed().assertHasClickAction()
            node.fetchSemanticsNode().boundsInRoot
        }
        bounds.forEach { bound ->
            assertTrue("Every tab must stay within the 320 dp viewport", bound.left >= root.left && bound.right <= root.right)
            assertTrue("Navigation must remain below the scrolling content", bound.top >= content.bottom)
            assertTrue("Navigation must remain inside the screen", bound.bottom <= root.bottom)
        }
        bounds.zipWithNext().forEach { (left, right) ->
            assertTrue("Adjacent tab touch targets must not overlap", left.right <= right.left)
        }
        compose.onNodeWithTag("nav-favori").assertDoesNotExist()
        listOf("Bugün" to "nav-ana", "Keşfet" to "nav-kategori", "Senin" to "nav-istatistik").forEach { (text, tag) -> textFits(text, tag) }
    }

    @Test fun narrowViewportWithDoubleTextKeepsHomeActionsAndJourneyReachable() {
        val quote = Sozler.kategoriden("motivasyon").first()
        val week = listOf(false, true, false, false, true, true, true)
        var saved by mutableStateOf(false)
        var saveCalls = 0
        var shareCalls = 0
        var tab by mutableStateOf(Sekme.ANA)
        compose.setContent {
            val originalContext = LocalContext.current
            val originalConfiguration = LocalConfiguration.current
            val originalDensity = LocalDensity.current
            val configuration = remember(originalConfiguration) {
                Configuration(originalConfiguration).apply {
                    setLocale(Locale.forLanguageTag("tr"))
                    screenWidthDp = 320
                    fontScale = 2f
                }
            }
            val context = remember(originalContext, configuration) {
                val localizedResources = originalContext.createConfigurationContext(configuration).resources
                object : ContextWrapper(originalContext) { override fun getResources() = localizedResources }
            }
            CompositionLocalProvider(
                LocalContext provides context,
                LocalConfiguration provides configuration,
                LocalDensity provides Density(originalDensity.density, 2f),
            ) {
                AzimTema(modu = TemaModu.KARANLIK) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Column(Modifier.width(320.dp).fillMaxHeight().testTag("responsive-root")) {
                            Box(Modifier.weight(1f).testTag("responsive-content")) {
                                if (tab == Sekme.ISTATISTIK) {
                                    IstatistikEkrani(seri = 3, rekor = 5, gorulen = 12,
                                        favoriSayisi = if (saved) 1 else 0, acikKategori = 6, haftalik = week)
                                } else {
                                    AnaEkran(
                                        sozler = listOf(quote), aktifIndeks = 0,
                                        favoriler = if (saved) setOf(quote.kimlik) else emptySet(),
                                        seri = 3, haftalik = week, bugunGelenler = emptyList(),
                                        gunlukGelenler = emptyMap(), bugunGorulen = 1, gunlukHedef = 3,
                                        sonrakiBildirim = null, oneri = null, bugunPlanlanan = 3, dil = "tr",
                                        hatirlaticiAcik = false, bildirimIzni = false,
                                        indeksDegisti = {},
                                        favoriDegistir = { saved = !saved; saveCalls++ },
                                        paylas = { shareCalls++ }, sozSecildi = {}, kesfeGit = {},
                                        ipucunuKapat = {}, ayarlaraGit = {},
                                        seciliKonular = setOf("motivasyon"), haptikAcik = false,
                                    )
                                }
                            }
                            AltNav(tab) { tab = it }
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
        ekranKaydet("22-v8-nav-before-check")
        navigationFits()
        // Primary actions must be visible before any vertical gesture.
        val save = compose.onNodeWithTag("home-save")
        val share = compose.onNodeWithTag("home-share")
        save.assertIsDisplayed().assertHasClickAction()
        share.assertIsDisplayed().assertHasClickAction()
        textFits("Kaydet")
        textFits("Paylaş")
        textFits(quote.metin("tr"), "active-quote")
        save.performClick()
        compose.runOnIdle { assertTrue(saved); assertEquals(1, saveCalls) }
        share.assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1, shareCalls) }
        textFits("Kayıtlı")
        textFits("Paylaş")
        val saveBounds = save.fetchSemanticsNode().boundsInRoot
        val shareBounds = share.fetchSemanticsNode().boundsInRoot
        assertTrue("Save and Share targets must not overlap", saveBounds.right <= shareBounds.left)
        navigationFits()
        ekranKaydet("22-v8-home-large-text")

        compose.onNodeWithTag("nav-istatistik").performClick()
        compose.onNodeWithTag("profile-saved").assertIsDisplayed().assertHasClickAction()
        compose.onNodeWithTag("profile-plan").assertIsDisplayed().assertHasClickAction()
        compose.onNodeWithTag("journey-week").performScrollTo()
        compose.onNodeWithText("Haftanın izi").assertIsDisplayed()
        compose.onNodeWithText("Görülen söz").performScrollTo().assertIsDisplayed()
        textFits("Görülen söz")
        compose.onNodeWithText("Açık konu").performScrollTo().assertIsDisplayed()
        textFits("Açık konu")
        compose.onNodeWithText("En uzun seri · gün").performScrollTo().assertIsDisplayed()
        textFits("En uzun seri · gün")
        navigationFits()
        ekranKaydet("23-v8-journey-large-text")
    }
}
