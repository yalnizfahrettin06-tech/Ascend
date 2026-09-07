package com.yalnizfahrettin.azim.paylas

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.CancellationException
import java.io.File


object VideoUretici {


    private const val TABAN = 720
    private const val KARE_HIZI = 30
    private const val ANAHTAR_ARALIK = 1

    data class Sonuc(val uri: android.net.Uri?, val hata: String? = null)

    suspend fun uret(
        ctx: Context,
        metin: String,
        yazar: String,
        ayar: PaylasimAyari,
        saniye: Int,
        ilerleme: (Float) -> Unit = {},
    ): Sonuc = withContext(Dispatchers.Default) {
        require(saniye in listOf(5, 10, 30, 45))
        val islem = kotlin.coroutines.coroutineContext
        val (g, y) = boyut(ayar.format)
        val toplamKare = saniye * KARE_HIZI

        var kodlayici: MediaCodec? = null
        var kapsayici: MediaMuxer? = null
        var egl: EglOrtam? = null
        val dosya = File(File(ctx.cacheDir, "paylasim").apply { mkdirs() }, "ascend-${java.util.UUID.randomUUID()}.mp4")

        var zemin: android.graphics.Bitmap? = null
        try {
            zemin = KartCizici.zeminYukle(ctx, ayar.zemin)
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

            val muxer = MediaMuxer(dosya.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            kapsayici = muxer
            var iz = -1
            var basladi = false
            val bilgi = MediaCodec.BufferInfo()

            fun bosalt(sonMu: Boolean) {
                val sonTarih = android.os.SystemClock.elapsedRealtime() + 15_000
                while (true) {
                    islem.ensureActive()
                    check(android.os.SystemClock.elapsedRealtime() < sonTarih) { "Encoder timed out" }
                    val indeks = kodlayici.dequeueOutputBuffer(bilgi, if (sonMu) 10_000 else 0)
                    when {
                        indeks == MediaCodec.INFO_TRY_AGAIN_LATER -> if (!sonMu) return else continue
                        indeks == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                            iz = muxer.addTrack(kodlayici.outputFormat)
                            muxer.start()
                            basladi = true
                        }
                        indeks >= 0 -> {
                            val tampon = kodlayici.getOutputBuffer(indeks)
                            if (tampon != null && bilgi.size > 0 && basladi &&
                                bilgi.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0
                            ) {
                                tampon.position(bilgi.offset)
                                tampon.limit(bilgi.offset + bilgi.size)
                                muxer.writeSampleData(iz, tampon, bilgi)
                            }
                            kodlayici.releaseOutputBuffer(indeks, false)
                            if (bilgi.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) return
                        }
                    }
                }
            }

            for (kare in 0 until toplamKare) {
                islem.ensureActive()
                val t = kare.toFloat() / toplamKare
                // Kelime kelime beliriş ilk %45'te tamamlanır; sonrası okuma süresi.
                val acilim = (t / 0.45f).coerceIn(0f, 1f)
                // Ken Burns: fotoğraf zeminde çok yavaş yakınlaşma.
                val yakinlik = 1f + 0.06f * t

                val bmp = KartCizici.ciz(ctx, metin, yazar, ayar, g, y, acilim, yakinlik, zemin)
                egl.ciz(bmp, g, y)
                egl.zamanDamgasi(kare * 1_000_000_000L / KARE_HIZI)
                check(egl.gonder()) { "Encoder surface failed" }
                bmp.recycle()

                bosalt(false)
                if (kare % KARE_HIZI == 0) ilerleme(t)
            }

            kodlayici.signalEndOfInputStream()
            bosalt(true)

            muxer.stop()
            muxer.release()
            kapsayici = null
            ilerleme(1f)
            Sonuc(
                FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", dosya)
            )
        } catch (e: CancellationException) {
            dosya.delete()
            throw e
        } catch (e: Exception) {
            dosya.delete()
            Sonuc(null, e.message)
        } finally {
            zemin?.recycle()
            runCatching { egl?.kapat() }
            runCatching { kodlayici?.stop() }
            runCatching { kodlayici?.release() }
            runCatching { kapsayici?.stop() }
            runCatching { kapsayici?.release() }
        }
    }


    private fun boyut(format: KartFormat): Pair<Int, Int> = when (format) {
        KartFormat.KARE -> TABAN to TABAN
        KartFormat.STORY -> TABAN to (TABAN * 16 / 9 / 2 * 2)
        KartFormat.YATAY -> (TABAN * 16 / 9 / 2 * 2) to TABAN
    }
}
