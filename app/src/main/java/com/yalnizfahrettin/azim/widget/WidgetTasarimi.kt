package com.yalnizfahrettin.azim.widget

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.yalnizfahrettin.azim.data.AnaTemalar

data class WidgetSecimi(val theme: String = "black", val centered: Boolean = false, val large: Boolean = false)
object WidgetTasarimi {
    fun load(ctx: Context, id: Int): WidgetSecimi {
        val p = ctx.getSharedPreferences("widget_design", Context.MODE_PRIVATE)
        return WidgetSecimi(p.getString("$id.theme", "black") ?: "black", p.getBoolean("$id.center", false), p.getBoolean("$id.large", false))
    }
    fun save(ctx: Context, id: Int, config: WidgetSecimi) {
        ctx.getSharedPreferences("widget_design", Context.MODE_PRIVATE).edit()
            .putString("$id.theme", config.theme).putBoolean("$id.center", config.centered).putBoolean("$id.large", config.large).commit()
    }
    fun remove(ctx: Context, id: Int) { ctx.getSharedPreferences("widget_design", Context.MODE_PRIVATE).edit()
        .remove("$id.theme").remove("$id.center").remove("$id.large").apply() }

    /** The editor and Glance use this same renderer, including crop and typography. */
    fun render(ctx: Context, config: WidgetSecimi, text: String, source: String, width: Int, height: Int): Bitmap {
        val w = width.coerceIn(180, 1000); val h = height.coerceIn(100, 1000)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap); val theme = AnaTemalar.find(config.theme)
        val dark = theme.dark; val bg = if(dark) Color.rgb(23,23,25) else Color.rgb(245,245,244)
        canvas.drawColor(bg)
        theme.art?.let { res ->
            val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
            val art = BitmapFactory.decodeResource(ctx.resources, res, opts)
            if (art != null) {
                val scale = maxOf(w.toFloat()/art.width, h.toFloat()/art.height)
                val dw = art.width*scale; val dh = art.height*scale
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                }
                canvas.drawBitmap(art, null, RectF((w-dw)/2, (h-dh)/2, (w+dw)/2, (h+dh)/2), paint)
                paint.colorFilter = null; paint.color = bg; paint.alpha = if(dark) 195 else 220
                canvas.drawRect(0f,0f,w.toFloat(),h.toFloat(),paint); art.recycle()
            }
        }
        val unit = w / 360f
        val pad = 20 * unit; val ink = if(dark) Color.WHITE else Color.rgb(24,24,26)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = ink; typeface = Typeface.create("sans-serif", Typeface.NORMAL); textSize = (if(config.large) 24 else 20)*unit*ctx.resources.configuration.fontScale.coerceIn(.85f, 2f) }
        val contentWidth = (w-pad*2).toInt()
        val maxLines = ((h-pad*2-32*unit)/(paint.textSize*1.3f)).toInt().coerceIn(1, 10)
        val layout = StaticLayout.Builder.obtain(text,0,text.length,paint,contentWidth)
            .setAlignment(if(config.centered) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f,1.2f).setIncludePad(false).setMaxLines(maxLines).setEllipsize(TextUtils.TruncateAt.END).build()
        val y = ((h-layout.height-24*unit)/2f).coerceAtLeast(pad)
        canvas.save(); canvas.translate(pad,y); layout.draw(canvas); canvas.restore()
        paint.textSize = 11*unit; paint.alpha = 180
        val label = TextUtils.ellipsize(source,paint,contentWidth.toFloat(),TextUtils.TruncateAt.END).toString()
        val x = if(config.centered) (w-paint.measureText(label))/2f else pad
        canvas.drawText(label,x,(y+layout.height+24*unit).coerceAtMost(h-pad/2),paint)
        return bitmap
    }
}
