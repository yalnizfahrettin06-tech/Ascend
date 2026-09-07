package com.yalnizfahrettin.azim

import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.paylas.*
import com.yalnizfahrettin.azim.data.Sozler
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MedyaTest {
    private val ctx get() = InstrumentationRegistry.getInstrumentation().targetContext
    @Test fun videosArePlayableAtEveryOfferedDuration() = runBlocking {
        listOf(5,10,30,45).forEach { duration ->
            val result = VideoUretici.uret(ctx, "Küçük bir adım. Yeni bir başlangıç.", "Ascend", PaylasimAyari(), duration)
            assertNotNull("Video $duration: ${result.hata}", result.uri)
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(ctx, result.uri)
                val actual = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
                assertTrue("Expected ${duration * 1000}, got $actual", kotlin.math.abs(actual - duration * 1000) < 150)
                assertEquals("720",retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                val frame = retriever.getFrameAtTime((duration * 900000L))
                assertNotNull(frame); frame?.recycle()
                if (duration == 5) {
                    val saved = MedyaDeposu.galeriyeKaydet(ctx, result.uri!!, true)
                    try { assertNotNull(ctx.contentResolver.openInputStream(saved)?.use { it.read() }) }
                    finally { ctx.contentResolver.delete(saved,null,null) }
                }
            } finally { retriever.release() }
        }
    }
    @Test fun galleryImageHasCorrectDimensionsAndCompletedRecord() = runBlocking {
        val uri = MedyaDeposu.gorsel(ctx, "Kendine yer aç.", "Ascend", PaylasimAyari(format = KartFormat.STORY))
        val saved = MedyaDeposu.galeriyeKaydet(ctx, uri, false)
        try {
            val bitmap = ctx.contentResolver.openInputStream(saved)?.use { android.graphics.BitmapFactory.decodeStream(it) }!!
            assertEquals(1080,bitmap.width); assertEquals(1920,bitmap.height); bitmap.recycle()
            ctx.contentResolver.query(saved, arrayOf(MediaStore.MediaColumns.IS_PENDING), null,null,null)!!.use { assertTrue(it.moveToFirst()); assertEquals(0,it.getInt(0)) }
        } finally { ctx.contentResolver.delete(saved,null,null) }
    }
    @Test fun expandedNotificationKeepsLongQuoteAndAttribution() {
        val ui = InstrumentationRegistry.getInstrumentation().uiAutomation
        ui.grantRuntimePermission(ctx.packageName, android.Manifest.permission.POST_NOTIFICATIONS)
        com.yalnizfahrettin.azim.notif.Bildirimler.kanalKur(ctx)
        val quote = Sozler.tumu().maxBy { it.tr.length }
        assertTrue(com.yalnizfahrettin.azim.notif.Bildirimler.goster(ctx,quote,"tr"))
        val manager = ctx.getSystemService(android.app.NotificationManager::class.java)
        val notification = manager.activeNotifications.first { it.id == quote.kimlik.hashCode() }.notification
        val full = notification.extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT).toString()
        assertTrue(full.contains(quote.tr)); assertTrue(full.contains(quote.yazar))
        manager.cancel(quote.kimlik.hashCode())
    }
}
