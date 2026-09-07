package com.yalnizfahrettin.azim.paylas

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object MedyaDeposu {
    suspend fun gorsel(ctx: Context, metin: String, yazar: String, ayar: PaylasimAyari): Uri = withContext(Dispatchers.IO) {
        val klasor = File(ctx.cacheDir, "paylasim").apply { mkdirs() }
        val dosya = File(klasor, "ascend-${UUID.randomUUID()}.png")
        val bmp = KartCizici.ciz(ctx, metin, yazar, ayar)
        try {
            dosya.outputStream().use { check(bmp.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", dosya)
        } catch (e: Exception) { dosya.delete(); throw e }
        finally { bmp.recycle() }
    }

    fun paylas(ctx: Context, uri: Uri, video: Boolean, baslik: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = if (video) "video/mp4" else "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri("Ascend", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(send, baslik).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun galeriyeKaydet(ctx: Context, kaynak: Uri, video: Boolean): Uri = withContext(Dispatchers.IO) {
        val koleksiyon = if (video) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val degerler = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Ascend-${UUID.randomUUID()}.${if (video) "mp4" else "png"}")
            put(MediaStore.MediaColumns.MIME_TYPE, if (video) "video/mp4" else "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, if (video) "Movies/Ascend" else "Pictures/Ascend")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val hedef = checkNotNull(ctx.contentResolver.insert(koleksiyon, degerler))
        try {
            kopyala(ctx, kaynak, hedef)
            ctx.contentResolver.update(hedef, ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
            hedef
        } catch (e: Exception) {
            ctx.contentResolver.delete(hedef, null, null)
            throw e
        }
    }

    suspend fun kopyala(ctx: Context, kaynak: Uri, hedef: Uri) = withContext(Dispatchers.IO) {
        checkNotNull(ctx.contentResolver.openInputStream(kaynak)).use { input ->
            checkNotNull(ctx.contentResolver.openOutputStream(hedef)).use { output -> input.copyTo(output) }
        }
    }

    fun eskiDosyalariTemizle(ctx: Context) {
        val once = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        File(ctx.cacheDir, "paylasim").listFiles()?.filter { it.lastModified() < once }?.forEach { it.delete() }
    }
}
