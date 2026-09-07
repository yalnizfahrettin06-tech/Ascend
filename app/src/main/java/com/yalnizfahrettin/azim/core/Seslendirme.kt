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

    private var motor: TextToSpeech? = null
    var hazir by mutableStateOf(false)
        private set
    var konusuyor by mutableStateOf(false)
        private set

    init {
        motor = TextToSpeech(ctx.applicationContext) { durum ->
            if (durum != TextToSpeech.SUCCESS) return@TextToSpeech
            val yerel = if (dil == "en") Locale.ENGLISH else Locale("tr", "TR")
            val sonuc = motor?.setLanguage(yerel)
            hazir = sonuc != TextToSpeech.LANG_MISSING_DATA &&
                sonuc != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /** Okuyorsa durdurur, okumuyorsa okur. */
    fun degistir(metin: String) {
        val m = motor ?: return
        if (konusuyor) {
            m.stop()
            konusuyor = false
            return
        }
        if (!hazir) return
        m.speak(metin, TextToSpeech.QUEUE_FLUSH, null, "azim")
        konusuyor = true
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
