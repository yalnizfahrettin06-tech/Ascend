package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class WallpaperJourneyTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun thirdAppearanceTabShowsActualGallery() {
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) { GorunumEkrani("tr","black",false,{}, {}) } }
        compose.onNodeWithTag("appearance-wallpaper").performClick().assertIsSelected()
        compose.onNodeWithTag("wallpaper-gallery").assertExists()
        compose.onNodeWithTag("wallpaper-emperor").performScrollTo().performClick()
        compose.onNodeWithTag("wallpaper-preview").assertExists()
        compose.onNodeWithTag("wallpaper-apply").assertIsDisplayed()
        ekranKaydet("927-wallpaper-preview")
    }
    @Test fun paidArtworkRequiresProAndNeverAppliesMerelyByEnablingDemo() {
        var pro by mutableStateOf(false)
        var offers = 0
        var applies = 0
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            WallpaperPreview(AnaTemalar.emperor,"tr",pro,{}, { offers++ }, { _, target, _ -> assertEquals(2,target); applies++; true })
        } }
        compose.onNodeWithTag("wallpaper-target-2").performScrollTo().performClick()
        compose.onNodeWithTag("wallpaper-apply").performClick()
        compose.runOnIdle { assertEquals(1,offers); assertEquals(0,applies); pro = true }
        compose.runOnIdle { assertEquals(0,applies) }
        compose.onNodeWithTag("wallpaper-apply").performClick()
        compose.waitUntil { applies == 1 }
        compose.onNodeWithTag("wallpaper-result").assertTextEquals(WallpaperCopy.text("done","tr"))
    }
    @Test fun freeWallpaperAppliesAndFailureIsNotReportedAsSuccess() {
        var targetSeen = 0
        compose.setContent { AzimTema(modu = TemaModu.KARANLIK) {
            WallpaperPreview(AnaTemalar.rider,"tr",false,{}, { error("Free artwork opened Pro") }, { _, target, _ -> targetSeen = target; false })
        } }
        compose.onNodeWithTag("wallpaper-target-3").performScrollTo().performClick()
        compose.onNodeWithTag("wallpaper-apply").performClick()
        compose.waitUntil { targetSeen == 3 }
        compose.onNodeWithTag("wallpaper-result").assertTextEquals(WallpaperCopy.text("error","tr"))
    }
    @Test fun emulatorCanInstallOriginalFreeArtwork() {
        org.junit.Assume.assumeTrue(android.os.Build.FINGERPRINT.contains("generic") || android.os.Build.MODEL.contains("sdk"))
        val ctx = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val manager = android.app.WallpaperManager.getInstance(ctx)
        org.junit.Assume.assumeTrue(manager.isWallpaperSupported && manager.isSetWallpaperAllowed)
        kotlinx.coroutines.runBlocking {
            assertTrue(WallpaperService.apply(ctx,AnaTemalar.rider,android.app.WallpaperManager.FLAG_LOCK,.5f))
        }
        assertTrue(manager.getWallpaperId(android.app.WallpaperManager.FLAG_LOCK) > 0)
    }
    @Test fun cropAccessAndEditorialIdentityStayConsistent() {
        for(aspect in listOf(.42f,.5f,.66f,1f,2f)) {
            val r = WallpaperService.crop(2048,3072,aspect,ArtworkFocus(.7f,.3f))
            assertTrue(r.left >= 0 && r.top >= 0 && r.right <= 2048 && r.bottom <= 3072)
            assertEquals(aspect,r.width().toFloat()/r.height(),.002f)
        }
        assertEquals(setOf("roma","rider"),WallpaperService.gallery.filter { !it.pro }.map { it.id }.toSet())
        assertEquals(WallpaperService.gallery.size,WallpaperService.gallery.map { it.art }.distinct().size)
        assertEquals(R.drawable.warrior_legion,EditorialArt.group("disiplin"))
        assertEquals(R.drawable.warrior_gladiator,EditorialArt.group("spor"))
        assertEquals(R.drawable.warrior_throne,EditorialArt.saved("ozsefkat"))
        assertEquals(4,ShortSeries.all.map { EditorialArt.series(it.id) }.distinct().size)
        for(language in WallpaperCopy.languages) for(key in listOf("title","apply","detail","pro","done","error","target")) assertTrue(WallpaperCopy.text(key,language).isNotBlank())
        val offer = ProOffer(ProSource.WALLPAPER,"emperor")
        assertEquals(offer,ProOffer.decode(offer.encode()))
    }
}
