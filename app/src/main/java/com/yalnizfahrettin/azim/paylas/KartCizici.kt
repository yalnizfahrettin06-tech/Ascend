package com.yalnizfahrettin.azim.paylas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import kotlin.math.max

/*
 * KART ÇİZİCİ
 *
 * Tek kaynak: önizleme, PNG çıktısı ve (ileride) video kareleri hep buradan
 * çiziliyor. Önizleme ile paylaşılan görselin ayrışması böylece imkânsız —
 * eski sürümde widget'ta yaşadığımız "önizleme başka, gerçek başka" hatası
 * tekrarlanmasın.
 *
 * Ölçekten bağımsız: tüm boyutlar tuval genişliğine oranla hesaplanıyor,
 * bu yüzden 540px önizleme ile 1080px çıktı birebir aynı görünüyor.
 */
object KartCizici {

    fun ciz(
        ctx: Context,
        metin: String,
        yazar: String,
        ayar: PaylasimAyari,
        genislik: Int = ayar.format.genislik,
        yukseklik: Int = ayar.format.yukseklik,
        /** 0f..1f — video için kelime kelime beliriş. 1f = tamamı. */
        acilim: Float = 1f,
        /** Ken Burns yakınlaşması; 1f = yakınlaşma yok. */
        yakinlik: Float = 1f,
        zeminBitmap: Bitmap? = null,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(genislik, yukseklik, Bitmap.Config.ARGB_8888)
        val tuval = Canvas(bmp)

        zeminiCiz(ctx, tuval, genislik, yukseklik, ayar, yakinlik, zeminBitmap)

        if (ayar.zemin == KartZemin.Sahne(com.yalnizfahrettin.azim.R.drawable.art_roman_home_v9)) {
            mermerMetni(ctx, tuval, metin, yazar, ayar, genislik, yukseklik, acilim)
            return bmp
        }
        val lora = yukle(ctx, "lora.ttf", Typeface.SERIF)
        val loraItalik = Typeface.create(lora, Typeface.ITALIC)
        val metinRengi = HazirZeminler.metinRengi(ayar.zemin).toArgb()

        val kenar = genislik * 0.10f
        val icGenislik = (genislik - kenar * 2).toInt()

        // Görünecek metin: video için kelime kelime açılır.
        val gorunen = if (acilim >= 1f) metin else kelimeAcilimi(metin, acilim)

        val sozBoya = TextPaint().apply {
            color = metinRengi
            typeface = ayar.yazi.tipi(lora, loraItalik, italik = false)
            isAntiAlias = true
            textSize = sozBoyutu(metin, genislik, ayar)
        }
        val hiza = if (ayar.hizalama == KartHizalama.ORTA) {
            Layout.Alignment.ALIGN_CENTER
        } else {
            Layout.Alignment.ALIGN_NORMAL
        }
        fun tamYukseklik() = StaticLayout.Builder.obtain(metin, 0, metin.length, sozBoya, icGenislik)
            .setLineSpacing(genislik * 0.016f, 1f).build().height
        while (tamYukseklik() > yukseklik * .62f && sozBoya.textSize > genislik * .023f) sozBoya.textSize *= .95f
        val duzen = StaticLayout.Builder
            .obtain(gorunen, 0, gorunen.length, sozBoya, icGenislik)
            .setAlignment(hiza)
            .setLineSpacing(genislik * 0.016f, 1f)
            .build()

        // Tam metnin yüksekliğine göre ortala — video boyunca zıplamasın.
        val tamDuzen = StaticLayout.Builder
            .obtain(metin, 0, metin.length, sozBoya, icGenislik)
            .setAlignment(hiza)
            .setLineSpacing(genislik * 0.016f, 1f)
            .build()

        val yazarBoya = TextPaint().apply {
            color = metinRengi
            alpha = 190
            typeface = ayar.yazi.tipi(lora, loraItalik, italik = true)
            isAntiAlias = true
            textSize = genislik * 0.036f
        }
        val yazarMetni = if (yazar.isBlank()) "" else "— $yazar"
        val blokYukseklik = tamDuzen.height + genislik * 0.09f
        val ust = max(yukseklik * 0.14f, (yukseklik - blokYukseklik) / 2f)

        tuval.save()
        tuval.translate(kenar, ust)
        duzen.draw(tuval)
        tuval.restore()

        if (yazarMetni.isNotEmpty() && acilim >= 0.98f) {
            val y = ust + tamDuzen.height + genislik * 0.07f
            val x = when (ayar.hizalama) {
                KartHizalama.ORTA -> genislik / 2f - yazarBoya.measureText(yazarMetni) / 2f
                KartHizalama.SOL -> kenar
            }
            tuval.drawText(yazarMetni, x, y, yazarBoya)
        }

        if (ayar.imzaGoster) {
            val imza = TextPaint().apply {
                color = metinRengi
                alpha = 110
                typeface = lora
                isAntiAlias = true
                textSize = genislik * 0.028f
                letterSpacing = 0.28f
            }
            val x = when (ayar.hizalama) {
                KartHizalama.ORTA -> genislik / 2f - imza.measureText("ASCEND") / 2f
                KartHizalama.SOL -> kenar
            }
            tuval.drawText("ASCEND", x, yukseklik - genislik * 0.075f, imza)
        }

        return bmp
    }

    /** Editorial marble layout uses the same geometry for preview, PNG and video. */
    private fun mermerMetni(ctx: Context, canvas: Canvas, text: String, source: String,
        a: PaylasimAyari, w: Int, h: Int, reveal: Float) {
        val left = w * .075f
        val width = (w * if (a.hizalama == KartHizalama.SOL) .82f else .60f).toInt()
        val font = yukle(ctx, "lora.ttf", Typeface.SERIF)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24,24,24); typeface = a.yazi.tipi(font, Typeface.create(font, Typeface.ITALIC), false)
            textSize = w * .078f * a.yaziOlcegi
        }
        fun layout(value: String) = StaticLayout.Builder.obtain(value, 0, value.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(false).setLineSpacing(w * .01f, 1f).build()
        val top = h * .34f
        while (layout(text).height > h * .42f && paint.textSize > w * .023f) paint.textSize *= .95f
        val fullHeight = layout(text).height
        canvas.save(); canvas.translate(left, top)
        layout(if (reveal >= 1f) text else kelimeAcilimi(text, reveal)).draw(canvas); canvas.restore()
        val accent = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(108,41,50); strokeWidth = w * .006f; strokeCap = Paint.Cap.ROUND }
        canvas.drawLine(left, top - w * .045f, left + w * .09f, top - w * .045f, accent)
        if (reveal >= .98f && source.isNotBlank()) {
            val sourcePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(57,56,53); typeface = Typeface.SANS_SERIF; textSize = w * .031f }
            val sourceLayout = StaticLayout.Builder.obtain(source, 0, source.length, sourcePaint, width).setIncludePad(false).build()
            canvas.save(); canvas.translate(left, top + fullHeight + w * .035f); sourceLayout.draw(canvas); canvas.restore()
        }
        if (a.imzaGoster) {
            val markY = h * .09f
            for (n in 0..3) canvas.drawRect(left + n * w * .013f, markY - w * (.012f + n * .01f), left + n * w * .013f + w * .007f, markY, accent)
            canvas.drawLine(left, markY - w * .045f, left + w * .05f, markY - w * .07f, accent)
            val brand = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(20,20,20); typeface = font; textSize = w * .06f }
            canvas.drawText("ascend", left + w * .075f, markY, brand)
        }
    }

    /** Metin uzunluğuna göre punto — uzun sözler taşmasın. */
    private fun sozBoyutu(metin: String, genislik: Int, ayar: PaylasimAyari): Float {
        val taban = when {
            metin.length > 140 -> 0.046f
            metin.length > 90 -> 0.054f
            metin.length > 55 -> 0.062f
            else -> 0.072f
        }
        val formatCarpani = if (ayar.format == KartFormat.YATAY) 0.82f else 1f
        return genislik * taban * ayar.yaziOlcegi * formatCarpani
    }

    private fun kelimeAcilimi(metin: String, oran: Float): String {
        val kelimeler = metin.split(" ")
        val adet = (kelimeler.size * oran).toInt().coerceIn(1, kelimeler.size)
        return kelimeler.take(adet).joinToString(" ")
    }

    private fun zeminiCiz(
        ctx: Context,
        tuval: Canvas,
        g: Int,
        y: Int,
        ayar: PaylasimAyari,
        yakinlik: Float,
        zeminBitmap: Bitmap?,
    ) {
        when (val z = ayar.zemin) {
            is KartZemin.Duz -> tuval.drawColor(z.renk.toInt())

            is KartZemin.Gradyan -> {
                val boya = Paint().apply {
                    shader = LinearGradient(
                        0f, 0f, 0f, y.toFloat(),
                        z.ust.toInt(), z.alt.toInt(), Shader.TileMode.CLAMP,
                    )
                }
                tuval.drawRect(0f, 0f, g.toFloat(), y.toFloat(), boya)
            }

            is KartZemin.Foto, is KartZemin.Sahne -> {
                val foto = zeminBitmap ?: zeminYukle(ctx, z)
                if (foto == null) {
                    tuval.drawColor(0xFF121416.toInt())
                } else {
                    // Merkeze kırp + Ken Burns yakınlaşması
                    val olcek = max(g.toFloat() / foto.width, y.toFloat() / foto.height) * yakinlik
                    val en = foto.width * olcek
                    val boy = foto.height * olcek
                    val matris = Matrix().apply {
                        setScale(olcek, olcek)
                        postTranslate((g - en) / 2f, (y - boy) / 2f)
                    }
                    tuval.drawBitmap(foto, matris, Paint(Paint.FILTER_BITMAP_FLAG))
                    if (zeminBitmap == null) foto.recycle()
                    // Okunurluk katmanı
                    val marble = z == KartZemin.Sahne(com.yalnizfahrettin.azim.R.drawable.art_roman_home_v9)
                    val dim = if (marble) ((ayar.karartma - .45f).coerceAtLeast(0f) * 255).toInt() else (ayar.karartma * 255).toInt()
                    tuval.drawColor(Color.argb(dim, 0, 0, 0))
                }
            }
        }
    }

    /** Bound photo decoding and load once per video export, not once per frame. */
    fun zeminYukle(ctx: Context, z: KartZemin): Bitmap? = when (z) {
        is KartZemin.Sahne -> android.graphics.BitmapFactory.decodeResource(ctx.resources, z.kaynak)
        is KartZemin.Foto -> {
            val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            ctx.contentResolver.openInputStream(z.uri)?.use { android.graphics.BitmapFactory.decodeStream(it, null, bounds) }
            val options = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = 1
                while (bounds.outWidth / inSampleSize > 2048 || bounds.outHeight / inSampleSize > 2048) inSampleSize *= 2
            }
            ctx.contentResolver.openInputStream(z.uri)?.use { android.graphics.BitmapFactory.decodeStream(it, null, options) }
                ?: error("Photo cannot be read")
        }
        else -> null
    }

    private val fonts = java.util.concurrent.ConcurrentHashMap<String, Typeface>()
    private fun yukle(ctx: Context, ad: String, yedek: Typeface): Typeface =
        fonts.getOrPut(ad) { runCatching { Typeface.createFromAsset(ctx.assets, ad) }.getOrDefault(yedek) }

    private fun androidx.compose.ui.graphics.Color.toArgb(): Int =
        android.graphics.Color.argb(
            (alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(),
        )
}
