package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.ui.*
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class CompactUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun trialActionsRemainReachableWithLargeText() {
        var started = false
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density,2f)) {
                AzimTema { DenemeTeklifi("tr",false,false,{}, { started = true },{}) }
            }
        }
        compose.onNodeWithTag("trial-start").performScrollTo().assertIsDisplayed().performClick()
        compose.runOnIdle { assertTrue(started) }
        compose.onNodeWithTag("trial-free").performScrollTo().assertIsDisplayed()
    }
    @Test fun topicsCanBeTurnedBackOnWithoutShowingQuotes() {
        var selected by mutableStateOf(setOf("motivasyon","ozsefkat"))
        var discover = false
        compose.setContent { AzimTema {
            BildirimKonulariPaneli("tr",selected,{}, { key -> selected = if(key in selected) selected - key else selected + key }, { discover = true })
        } }
        compose.onNodeWithTag("reminder-toggle-motivasyon").performClick().assertIsOff()
        compose.onNodeWithTag("reminder-toggle-ozsefkat").assertIsNotEnabled()
        compose.onNodeWithTag("reminder-toggle-motivasyon").performClick().assertIsOn()
        compose.onNodeWithTag("category-detail").assertDoesNotExist()
        compose.onNodeWithTag("reminder-discover").performClick()
        compose.runOnIdle { assertTrue(discover) }
    }
    @Test fun galleryDoesNotComposeAllArtworkBeforeScrolling() {
        compose.setContent { AzimTema { GorunumEkrani("tr","white",false,{}, {}) } }
        val last = AnaTemalar.all.last().id
        compose.onNodeWithTag("theme-$last").assertDoesNotExist()
        compose.onNodeWithTag("appearance-gallery").performScrollToNode(hasTestTag("theme-$last"))
        compose.onNodeWithTag("theme-$last").assertIsDisplayed()
    }
}
