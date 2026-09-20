package com.yalnizfahrettin.azim

import org.junit.Test
import org.junit.Assert.*
import com.yalnizfahrettin.azim.data.*

/** System theme mutation runs alone after UI assertions, never in a user device test. */
class WallpaperSystemTest {
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
}
