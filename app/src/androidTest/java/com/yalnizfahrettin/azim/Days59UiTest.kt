package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import java.util.Collections
import android.view.FrameMetrics
import android.os.Handler
import android.os.Looper

class Days59UiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun appearanceTabsFitSevenLanguagesThreeScalesBothPalettes() {
        var lang by mutableStateOf("tr")
        var scale by mutableFloatStateOf(1f)
        var dark by mutableStateOf(true)
        var selected by mutableIntStateOf(0)
        compose.setContent {
            val d = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(d.density,scale)) {
                AzimTema(modu = if(dark) TemaModu.KARANLIK else TemaModu.AYDINLIK) {
                    Box(Modifier.width(320.dp).height(640.dp).testTag("matrix-root")) {
                        ChoiceTabs(listOf(Diller.metin(lang,"Uygulama teması","App theme"),"Widget",WallpaperCopy.text("title",lang)),selected,{ selected = it },listOf("tab-0","tab-1","tab-2"))
                    }
                }
            }
        }
        JourneyCopy.languages.forEach { l -> listOf(1f,1.3f,2f).forEach { size -> listOf(false,true).forEach { night ->
            compose.runOnIdle { lang = l; scale = size; dark = night }
            repeat(3) { i ->
                val tab = compose.onNodeWithTag("tab-$i").assertIsDisplayed().performClick().assertIsSelected()
                val root = compose.onNodeWithTag("matrix-root").getUnclippedBoundsInRoot()
                val rect = tab.getUnclippedBoundsInRoot()
                assertTrue("$l/$size/$night",rect.right <= root.right && rect.bottom <= root.bottom && rect.left >= root.left)
            }
        } } }
    }

    @Test fun searchScopeEmptyRecoveryAndThinkerInformation() {
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) { KategorilerEkrani(setOf("motivasyon"),setOf("motivasyon"),"tr",{},{},false,{},acilacakGrup = Kategoriler.DUSUNURLER) } }
        compose.onNodeWithTag("category-search").performTextInput("qqqzzzz")
        compose.onNodeWithText(JourneyCopy.text("allResults","tr")).assertExists()
        compose.onNodeWithTag("search-reset").performClick()
        compose.onNodeWithTag("collection-${Kategoriler.DUSUNURLER}").performScrollTo().performClick()
        compose.onNodeWithTag("category-sokrates").assertExists()
        ekranKaydet("v929-thinkers")
    }

    @Test fun fewSavedQuotesUseOptionalSearchAndRecoverFromEmptyQuery() {
        val quote = SetupPractice.quotes.first()
        compose.setContent { AzimTema { FavorilerEkrani(listOf(quote),"tr",{},{},{},embedded = true) } }
        compose.onNodeWithTag("saved-search").assertDoesNotExist()
        compose.onNodeWithTag("saved-search-toggle").performClick()
        compose.onNodeWithTag("saved-search").performTextInput("qqqzzzz")
        compose.onNodeWithTag("saved-no-results").assertExists()
        compose.onNodeWithTag("saved-search-toggle").performClick()
        compose.onNodeWithTag("saved-quote-${quote.kimlik}").assertExists()
    }

    @Test fun disciplinePreviewAndOfferUseTheSelectedSeries() {
        var offers = 0
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) { RestartSeriesScreen("tr",false,null,emptySet(),{},{},{},{},{},{ offers++ },seriesId = "discipline") } }
        compose.onNodeWithTag("restart-story").assertTextEquals(DisciplineSeries.days("tr")[0].story)
        compose.onNodeWithTag("restart-action").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(1,offers); assertEquals(DisciplineSeries.title("tr"),proOfferTitle(ProOffer(ProSource.SERIES,"discipline"),"tr")) }
        ekranKaydet("v929-discipline")
    }

    @Test fun galleryRecordsMeasuredLoadingFramesAndMemory() {
        val frames = Collections.synchronizedList(mutableListOf<Long>())
        val listener = android.view.Window.OnFrameMetricsAvailableListener { _, metrics, _ -> frames.add(metrics.getMetric(FrameMetrics.TOTAL_DURATION)) }
        compose.runOnUiThread { ThemeImages.trim(); compose.activity.window.addOnFrameMetricsAvailableListener(listener,Handler(Looper.getMainLooper())) }
        val start = android.os.SystemClock.elapsedRealtime()
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) { GorunumEkrani("tr","black",true,{}, {}) } }
        compose.waitUntil(30000) { compose.onAllNodesWithTag("art-loading").fetchSemanticsNodes().isEmpty() && compose.onAllNodesWithTag("theme-featured").fetchSemanticsNodes().isNotEmpty() }
        val loadedMs = android.os.SystemClock.elapsedRealtime() - start
        repeat(5) { compose.onNodeWithTag("appearance-gallery").performTouchInput { swipeUp() }; compose.waitForIdle() }
        val values = synchronized(frames) { frames.sorted() }
        val info = android.os.Debug.MemoryInfo(); android.os.Debug.getMemoryInfo(info)
        val result = org.json.JSONObject().put("environment","API35 emulator; no physical-device or baseline improvement claim")
            .put("coldGalleryMs",loadedMs).put("frames",values.size).put("framesOver32ms",values.count { it > 32000000 })
            .put("p95FrameMs",if(values.isEmpty()) 0 else values[((values.size-1)*.95).toInt()]/1000000.0)
            .put("pssKb",info.totalPss).put("artCacheBytes",ThemeImages.cachedBytes())
        val output = java.io.File(compose.activity.getExternalFilesDir(null),"days59-performance.json")
        output.writeText(result.toString(2))
        println("ASCEND_PERFORMANCE=$result")
        compose.runOnUiThread { compose.activity.window.removeOnFrameMetricsAvailableListener(listener) }
        assertTrue(ThemeImages.cachedBytes() <= 24 * 1024 * 1024)
        ekranKaydet("v929-gallery-scroll")
    }
}
