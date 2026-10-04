package com.yalnizfahrettin.azim.core

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/*
 * SESLİ OKUMA
 *
 * Ana ekran raporu, Katman 1: eylem sırasına dördüncü düğme.
 *
 * İki işi birden yapıyor:
 *  - Erişilebilirlik raporundaki (6.1/6.2) boşluğun bir kısmını kapatıyor:
 *    görme zorluğu yaşayan kullanıcı sözü dinleyebiliyor.
 *  - Sabah gözü kapalı dinlemek isteyen kullanıcı için gerçek bir kullanım.
 *
 * Android'in yerleşik TextToSpeech'i kullanılıyor — yeni bağımlılık yok.
 * Cihazda ilgili dil paketi yoksa düğme sessizce devre dışı kalır;
 * kullanıcıya hata gösterilmez, düğme sadece soluklaşır.
 */
class Seslendirici(ctx: Context, private val dil: String) {

    private val main = android.os.Handler(android.os.Looper.getMainLooper())
    private var motor: TextToSpeech? = null
    var hazir by mutableStateOf(false)
        private set
    var konusuyor by mutableStateOf(false)
        private set
    /** Engine start is asynchronous; the first request waits for it instead of failing. */
    private var bekleyen: String? = null
    private var basarisiz = false

    init {
        motor = TextToSpeech(ctx.applicationContext) { durum ->
            if (durum != TextToSpeech.SUCCESS) { main.post { basarisiz = true; konusuyor = false; bekleyen = null }; return@TextToSpeech }
            motor?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(id: String?) {}
                override fun onDone(id: String?) { main.post { konusuyor = false } }
                @Deprecated("Platform callback") override fun onError(id: String?) { main.post { konusuyor = false } }
            })
            val yerel = Locale.forLanguageTag(com.yalnizfahrettin.azim.data.Diller.normalize(dil))
            val sonuc = motor?.setLanguage(yerel)
            val uygun = sonuc != TextToSpeech.LANG_MISSING_DATA && sonuc != TextToSpeech.LANG_NOT_SUPPORTED
            main.post {
                hazir = uygun
                basarisiz = !uygun
                val metin = bekleyen
                bekleyen = null
                if (uygun && metin != null) motor?.speak(metin, TextToSpeech.QUEUE_FLUSH, null, "azim") else konusuyor = false
            }
        }
    }

    /** Okuyorsa durdurur, okumuyorsa okur. Returns false when the device cannot speak this language. */
    fun degistir(metin: String): Boolean {
        val m = motor ?: return false
        if (konusuyor) {
            m.stop()
            bekleyen = null
            konusuyor = false
            return true
        }
        if (basarisiz) return false
        if (!hazir) { bekleyen = metin; konusuyor = true; return true }
        m.speak(metin, TextToSpeech.QUEUE_FLUSH, null, "azim")
        konusuyor = true
        return true
    }

    fun durdur() {
        motor?.stop()
        konusuyor = false
    }

    fun kapat() {
        motor?.stop()
        motor?.shutdown()
        motor = null
    }
}

/** Ekran ömrüne bağlı seslendirici. Dil değişince yeniden kurulur. */
@Composable
fun rememberSeslendirici(dil: String): Seslendirici {
    val ctx = LocalContext.current
    val seslendirici = remember(dil) { Seslendirici(ctx, dil) }
    DisposableEffect(seslendirici) {
        onDispose { seslendirici.kapat() }
    }
    return seslendirici
}
