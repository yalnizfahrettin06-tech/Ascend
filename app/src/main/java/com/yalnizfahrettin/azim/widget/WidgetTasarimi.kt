package com.yalnizfahrettin.azim.widget

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.yalnizfahrettin.azim.data.AnaTemalar

data class WidgetSecimi(val theme: String = "black", val centered: Boolean = true, val large: Boolean = false)
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
        val w = width.coerceIn(180, 1440); val h = height.coerceIn(100, 1440)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap); val theme = AnaTemalar.find(config.theme)
        val dark = theme.dark; val bg = if(dark) Color.rgb(23,23,25) else Color.rgb(245,245,244)
        canvas.drawColor(bg)
        theme.art?.let { res ->
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
            BitmapFactory.decodeResource(ctx.resources,res,bounds)
            var sample = 1
            while(bounds.outWidth / (sample * 2) >= w && bounds.outHeight / (sample * 2) >= h) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample; inScaled = false }
            val art = BitmapFactory.decodeResource(ctx.resources, res, opts)
            if (art != null) {
                val scale = maxOf(w.toFloat()/art.width, h.toFloat()/art.height)
                val dw = art.width*scale; val dh = art.height*scale
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    colorFilter = null
                }
                canvas.drawBitmap(art, null, RectF((w-dw)/2, (h-dh)/2, (w+dw)/2, (h+dh)/2), paint)
                art.recycle()
            }
        }
        val unit = w / 360f
        val pad = 20 * unit; val ink = if(dark) Color.WHITE else Color.rgb(24,24,26)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = ink; typeface = Typeface.create("sans-serif", Typeface.NORMAL); textSize = 20*unit*ctx.resources.configuration.fontScale.coerceIn(.85f, 2f) }
        val contentWidth = (w-pad*2).toInt()
        val maxLines = ((h-pad*2-32*unit)/(paint.textSize*1.3f)).toInt().coerceIn(1, 10)
        val layout = StaticLayout.Builder.obtain(text,0,text.length,paint,contentWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f,1.2f).setIncludePad(false).setMaxLines(maxLines).setEllipsize(TextUtils.TruncateAt.END).build()
        val y = ((h-layout.height-24*unit)/2f).coerceAtLeast(pad)
        if(theme.art != null) {
            // Contrast only behind the quote, preserving the photograph elsewhere.
            val shade = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(0f,y-18*unit,0f,y+layout.height+32*unit,
                    intArrayOf(Color.TRANSPARENT, if(dark) 0x85000000.toInt() else 0xBFFFFFFF.toInt(), if(dark) 0x85000000.toInt() else 0xBFFFFFFF.toInt(), Color.TRANSPARENT),
                    floatArrayOf(0f,.2f,.8f,1f), Shader.TileMode.CLAMP)
            }
            canvas.drawRect(0f,y-18*unit,w.toFloat(),y+layout.height+32*unit,shade)
        }
        canvas.save(); canvas.translate(pad,y); layout.draw(canvas); canvas.restore()
        paint.textSize = 11*unit; paint.alpha = 180
        val label = TextUtils.ellipsize(source,paint,contentWidth.toFloat(),TextUtils.TruncateAt.END).toString()
        val x = (w-paint.measureText(label))/2f
        canvas.drawText(label,x,(y+layout.height+24*unit).coerceAtMost(h-pad/2),paint)
        return bitmap
    }
}
