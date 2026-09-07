package com.yalnizfahrettin.azim.paylas

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Kartı üretip paylaşım penceresini açar. Çizim KartCizici'den gelir. */
object PaylasimKarti {

    fun paylas(
        ctx: Context,
        metin: String,
        yazar: String,
        baslik: String,
        ayar: PaylasimAyari = PaylasimAyari(),
    ) {
        val bmp = KartCizici.ciz(ctx, metin, yazar, ayar)
        val uri = kaydet(ctx, bmp)
        bmp.recycle()

        val niyet = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "$metin\n— $yazar")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(niyet, baslik))
    }

    private fun kaydet(ctx: Context, bmp: Bitmap): android.net.Uri {
        val klasor = File(ctx.cacheDir, "paylasim").apply { mkdirs() }
        val dosya = File(klasor, "ascend.png")
        FileOutputStream(dosya).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", dosya)
    }
}
