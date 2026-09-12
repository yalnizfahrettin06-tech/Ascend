package com.yalnizfahrettin.azim

import android.content.ContentUris
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.data.Erisim
import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.PersonalProfile
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.core.Palet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VisualAcceptanceTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private lateinit var demoDepo: Depo
    @Before fun startWithSavedPersonalPlan() {
        demoDepo = Depo(InstrumentationRegistry.getInstrumentation().targetContext)
        runBlocking {
            withTimeout(15000) {
                demoDepo.proDemoAyarla(false)
                demoDepo.completePersonalPlan(PersonalProfile(), false)
                demoDepo.dilAyarla("tr")
                demoDepo.temaAyarla(TemaModu.AYDINLIK)
                demoDepo.paletAyarla(Palet.MERMER)
                demoDepo.favoriler.first().forEach { demoDepo.favoriDegistir(it) }
            }
        }
        compose.activityRule.scenario.recreate()
        waitForHome()
    }

    private fun waitForHome() {
        compose.waitUntil(15000) { compose.onAllNodesWithTag("active-quote").fetchSemanticsNodes().isNotEmpty() }
        // Recreation can compose the page before Android returns input focus.
        // A coordinate click during that window is dropped by the platform.
        compose.waitUntil(15000) { compose.runOnUiThread { compose.activity.hasWindowFocus() } }
        compose.waitForIdle()
    }


    @Test fun homeExploreSearchAndTopicAreVisible() {
        ekranKaydet("v97-home")
        compose.onNodeWithText("Keşfet").performClick()
        compose.onNodeWithTag("category-search").assertIsDisplayed()
        compose.waitForIdle()
        ekranKaydet("v97-explore")
        compose.onNodeWithTag("collection-feature").performClick()
        compose.waitForIdle()
        ekranKaydet("v97-collection")
        compose.onNodeWithTag("category-search").performTextInput("Marcus")
        compose.onNodeWithTag("category-search").performImeAction()
        compose.waitForIdle()
        ekranKaydet("v97-search")
        compose.onNodeWithTag("nav-gorunum").performClick()
        compose.onNodeWithTag("theme-black").performScrollTo().performClick()
        compose.onNodeWithTag("theme-apply").performScrollTo().performClick()
        compose.waitForIdle()
        ekranKaydet("v97-customize-dark")
        compose.onNodeWithText("Bugün").performClick()
        compose.waitForIdle()
        ekranKaydet("v97-home-black")
        runBlocking { demoDepo.proDemoAyarla(true); demoDepo.arkaPlanAyarla("rider") }
        compose.waitForIdle()
        ekranKaydet("v97-home-rider")
        runBlocking { demoDepo.arkaPlanAyarla("roma") }
        compose.waitForIdle()
        ekranKaydet("v97-home-roma")
        runBlocking { demoDepo.proDemoAyarla(false) }
        compose.waitForIdle()
        assertEquals("white", runBlocking { demoDepo.arkaPlan.first() })

    }
}
