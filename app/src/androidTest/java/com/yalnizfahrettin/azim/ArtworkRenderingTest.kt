package com.yalnizfahrettin.azim

import android.graphics.*
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.data.ArtworkFocus
import com.yalnizfahrettin.azim.paylas.*
import org.junit.Assert.*
import org.junit.Test

class ArtworkRenderingTest {
    @Test fun portraitFaceAreaRemainsUncoveredWhileTextHasLocalContrast() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resource = R.drawable.warrior_emperor
        val photo = BitmapFactory.decodeResource(context.resources, resource, BitmapFactory.Options().apply { inScaled = false })
        val reference = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val scale = maxOf(1080f/photo.width, 1920f/photo.height)
        val focus = ArtworkFocus.forResource(resource)
        val matrix = Matrix().apply {
            setScale(scale,scale)
            postTranslate(focus.left(1080f,photo.width*scale),focus.top(1920f,photo.height*scale))
        }
        Canvas(reference).drawBitmap(photo,matrix,Paint(Paint.FILTER_BITMAP_FLAG))
        val result = KartCizici.ciz(context,"A small step is still a step forward.","Ascend",
            PaylasimAyari(zemin = KartZemin.Sahne(resource)))
        try {
            // Face and upper half must retain original pixels, not a global grey veil.
            for(y in 100..800 step 100) for(x in 100..900 step 100) {
                assertEquals("Unexpected overlay at $x,$y",reference.getPixel(x,y),result.getPixel(x,y))
            }
            assertFalse(reference.sameAs(result))
        } finally { photo.recycle(); reference.recycle(); result.recycle() }
    }
}
