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
    ): Bitmap {
        val bmp = Bitmap.createBitmap(genislik, yukseklik, Bitmap.Config.ARGB_8888)
        val tuval = Canvas(bmp)

        zeminiCiz(ctx, tuval, genislik, yukseklik, ayar, yakinlik)

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

            is KartZemin.Foto -> {
                val foto = runCatching {
                    ctx.contentResolver.openInputStream(z.uri)?.use {
                        android.graphics.BitmapFactory.decodeStream(it)
                    }
                }.getOrNull()
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
                    foto.recycle()
                    // Okunurluk katmanı
                    tuval.drawColor(Color.argb((ayar.karartma * 255).toInt(), 0, 0, 0))
                }
            }
        }
    }

    private fun yukle(ctx: Context, ad: String, yedek: Typeface): Typeface =
        runCatching { Typeface.createFromAsset(ctx.assets, ad) }.getOrDefault(yedek)

    private fun androidx.compose.ui.graphics.Color.toArgb(): Int =
        android.graphics.Color.argb(
            (alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(),
        )
}
