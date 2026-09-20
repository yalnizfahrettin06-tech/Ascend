package com.yalnizfahrettin.azim.data

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

/** Static wallpapers use the original resource, never a thumbnail, quote or dark overlay. */
object WallpaperService {
    val gallery: List<AnaTema> get() = AnaTemalar.all.filter { it.art != null }.distinctBy { it.art }
    fun crop(width: Int, height: Int, aspect: Float, focus: ArtworkFocus): Rect {
        require(width > 0 && height > 0 && aspect.isFinite() && aspect > 0)
        val w = minOf(width, (height * aspect).roundToInt()).coerceAtLeast(1)
        val h = minOf(height, (width / aspect).roundToInt()).coerceAtLeast(1)
        val x = ((width - w) * focus.x.coerceIn(0f,1f)).roundToInt()
        val y = ((height - h) * focus.y.coerceIn(0f,1f)).roundToInt()
        return Rect(x,y,x+w,y+h)
    }
    suspend fun apply(context: Context, theme: AnaTema, target: Int, aspect: Float): Boolean = withContext(Dispatchers.IO) {
        require(target in 1..3)
        require(gallery.any { it.id == theme.id })
        check(!theme.pro || Depo(context).proDemo.first())
        val manager = WallpaperManager.getInstance(context)
        check(manager.isWallpaperSupported && manager.isSetWallpaperAllowed)
        val resource = requireNotNull(theme.art)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
        BitmapFactory.decodeResource(context.resources,resource,options)
        val crop = crop(options.outWidth,options.outHeight,aspect,ArtworkFocus.forResource(resource,aspect))
        // Android decodes the original stream; no large duplicate bitmap on the UI thread.
        context.resources.openRawResource(resource).use { stream ->
            manager.setStream(stream,crop,false,target) > 0
        }
    }
}
