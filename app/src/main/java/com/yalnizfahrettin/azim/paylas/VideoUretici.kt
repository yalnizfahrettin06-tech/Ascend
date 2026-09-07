package com.yalnizfahrettin.azim.paylas

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/*
 * VİDEO ÜRETİCİ
 *
 * FFmpeg KULLANILMIYOR. Android'in kendi donanım kodlayıcısı (MediaCodec)
 * ve kapsayıcısı (MediaMuxer) cihazda zaten var, H.264 için lisanslı ve
 * ek indirme gerektirmiyor. FFmpeg ikilisi taşımak APK'yı onlarca MB
 * şişirir ve lisans riski doğurur.
 *
 * ⚠️ CİHAZ TESTİ GEREKİYOR: kodlayıcı davranışı üreticiye göre değişir ve
 * emülatörde doğrulanamaz. Hata durumunda üretim sessizce başarısız olur
 * ve çağıran taraf görsel paylaşıma düşer — kullanıcı hiçbir zaman kırık
 * bir çıktıyla karşılaşmaz.
 */
object VideoUretici {

    /** Video çözünürlüğü karttan düşük tutuluyor: düşük segment cihazlarda 1080p kodlama düşebiliyor. */
    private const val TABAN = 720
    private const val KARE_HIZI = 30
    private const val ANAHTAR_ARALIK = 1

    data class Sonuc(val uri: android.net.Uri?)

    suspend fun uret(
        ctx: Context,
        metin: String,
        yazar: String,
        ayar: PaylasimAyari,
        saniye: Int,
        ilerleme: (Float) -> Unit = {},
    ): Sonuc = withContext(Dispatchers.Default) {
        val (g, y) = boyut(ayar.format)
        val toplamKare = saniye * KARE_HIZI

        var kodlayici: MediaCodec? = null
        var kapsayici: MediaMuxer? = null
        var egl: EglOrtam? = null
        val dosya = File(File(ctx.cacheDir, "paylasim").apply { mkdirs() }, "ascend.mp4")

        try {
            val bicim = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, g, y).apply {
                setInteger(
                    MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
                )
                setInteger(MediaFormat.KEY_BIT_RATE, g * y * 6)
                setInteger(MediaFormat.KEY_FRAME_RATE, KARE_HIZI)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, ANAHTAR_ARALIK)
            }
            kodlayici = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC).apply {
                configure(bicim, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            }
            egl = EglOrtam(kodlayici.createInputSurface())
            kodlayici.start()

            kapsayici = MediaMuxer(dosya.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var iz = -1
            var basladi = false
            val bilgi = MediaCodec.BufferInfo()

            fun bosalt(sonMu: Boolean) {
                while (true) {
                    val indeks = kodlayici.dequeueOutputBuffer(bilgi, if (sonMu) 10_000 else 0)
                    when {
                        indeks == MediaCodec.INFO_TRY_AGAIN_LATER -> if (!sonMu) return else continue
                        indeks == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                            iz = kapsayici.addTrack(kodlayici.outputFormat)
                            kapsayici.start()
                            basladi = true
                        }
                        indeks >= 0 -> {
                            val tampon = kodlayici.getOutputBuffer(indeks)
                            if (tampon != null && bilgi.size > 0 && basladi &&
                                bilgi.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0
                            ) {
                                tampon.position(bilgi.offset)
                                tampon.limit(bilgi.offset + bilgi.size)
                                kapsayici.writeSampleData(iz, tampon, bilgi)
                            }
                            kodlayici.releaseOutputBuffer(indeks, false)
                            if (bilgi.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) return
                        }
                    }
                }
            }

            for (kare in 0 until toplamKare) {
                val t = kare.toFloat() / toplamKare
                // Kelime kelime beliriş ilk %45'te tamamlanır; sonrası okuma süresi.
                val acilim = (t / 0.45f).coerceIn(0f, 1f)
                // Ken Burns: fotoğraf zeminde çok yavaş yakınlaşma.
                val yakinlik = 1f + 0.06f * t

                val bmp = KartCizici.ciz(ctx, metin, yazar, ayar, g, y, acilim, yakinlik)
                egl.ciz(bmp, g, y)
                egl.zamanDamgasi(kare * 1_000_000_000L / KARE_HIZI)
                egl.gonder()
                bmp.recycle()

                bosalt(false)
                ilerleme(t)
            }

            kodlayici.signalEndOfInputStream()
            bosalt(true)

            Sonuc(
                FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", dosya)
            )
        } catch (e: Exception) {
            // Kodlayıcı yoksa veya cihaz desteklemiyorsa: sessizce başarısız.
            Sonuc(null)
        } finally {
            runCatching { egl?.kapat() }
            runCatching { kodlayici?.stop() }
            runCatching { kodlayici?.release() }
            runCatching { kapsayici?.stop() }
            runCatching { kapsayici?.release() }
        }
    }

    /** Kart formatını video çözünürlüğüne indirger; kenarlar çift sayı olmalı. */
    private fun boyut(format: KartFormat): Pair<Int, Int> = when (format) {
        KartFormat.KARE -> TABAN to TABAN
        KartFormat.STORY -> TABAN to (TABAN * 16 / 9 / 2 * 2)
        KartFormat.YATAY -> (TABAN * 16 / 9 / 2 * 2) to TABAN
    }
}
