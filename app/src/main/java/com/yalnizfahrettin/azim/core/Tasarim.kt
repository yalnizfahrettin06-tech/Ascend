package com.yalnizfahrettin.azim.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R

/*
 * TEK TASARIM KAYNAĞI.
 *
 * Eski projede iki ayrı AzimTheme vardı (ui/Theme.kt ve ui/theme/Theme.kt);
 * biri ölüydü. Burada tek bir dosya var ve tüm renk/ölçü kararları buradan çıkar.
 *
 * ACCENT KURALI — eski sürümün en büyük görsel problemi accent rengin
 * (bordo/gül) aynı anda nav, kart kenarlığı, checkbox, başlık ve istatistikte
 * "ana karakter" olmasıydı. Bu palette accent SADECE üç yerde kullanılır:
 *   1) aktif alt navigasyon öğesi
 *   2) birincil eylem (favori dolu kalp, seri alevi)
 *   3) ilerleme çubuğunun dolu kısmı
 * Geri kalan her vurgu nötr ölçekten (metinIkincil / kenarlikGuclu / yuzeyYuksek)
 * beslenir. "Biraz daha belirgin" ihtiyacı accent istemez, bir ton yukarı ister.
 */

@Immutable
data class AzimRenkleri(
    val zemin: Color,
    val yuzey: Color,
    val yuzeyYuksek: Color,
    val kenarlik: Color,
    val kenarlikGuclu: Color,
    val metin: Color,
    val metinIkincil: Color,
    val metinSonuk: Color,
    val accent: Color,
    val accentSonuk: Color,
    val accentZemin: Color,
    /**
     * Derin şarap — kontrast şartına TABİ DEĞİL.
     *
     * accent tek başınayken hem okunması gereken yerlerde (ikon, metin) hem
     * okunması gerekmeyen yerlerde (dolgu, çizgi, halka zemini) kullanılıyordu;
     * bu yüzden en zayıf halka olan okunabilirlik tüm sistemi rehin alıyor ve
     * accent bir türlü koyulaşamıyordu. Ayrıldı: burası gerçekten koyu olabilir.
     */
    val accentDerin: Color,
    val karanlikMi: Boolean,
)


/*
 * ÖLÇÜ — katı 4/8 ızgarası.
 * Eski DpSpacing'de xs/sm/md yanında s5,s6,s7,s14,s18,s22,s26,s34 gibi
 * 14 adet "isim verilmiş magic number" vardı ve ızgara zaten kırıktı.
 * Burada sadece ızgaraya oturan 9 değer var. Ara değer gerekiyorsa
 * bu bir tasarım hatasıdır, yeni token değil.
 */
object Olcu {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val x3 = 32.dp
    val x4 = 40.dp
    val x5 = 48.dp
}

/*
 * YARIÇAP — yumuşatıldı.
 * Önceki set (8/12/16) köşeleri sert bırakıyordu; küçük yüzeylerde
 * neredeyse dik görünüyordu. Yeni set bir kademe yukarı:
 * dokunulabilir her yüzey artık belirgin şekilde yuvarlak.
 */
object Yaricap {
    val sm = 12.dp
    val md = 18.dp
    val lg = 24.dp
    val xl = 30.dp
    val hap = 999.dp
}

val LoraSerif = FontFamily(
    Font(R.font.lora, FontWeight.Normal),
    Font(R.font.lora, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.lora_italic, FontWeight.Normal, FontStyle.Italic),
)

/* Sözler serif, arayüz sistem sans. Karışım kasıtlı: içerik ile kabuk ayrılır. */
val AzimTipografi = Typography(
    headlineLarge = TextStyle(fontFamily = LoraSerif, fontSize = 32.sp, lineHeight = 40.sp),
    displaySmall = TextStyle(
        fontFamily = LoraSerif, fontWeight = FontWeight.Normal,
        fontSize = 30.sp, lineHeight = 44.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = LoraSerif, fontWeight = FontWeight.Normal,
        fontSize = 22.sp, lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 12.sp,
        lineHeight = 14.sp, letterSpacing = 1.2.sp,
    ),
)

val LocalAzimRenk = staticCompositionLocalOf { paletiCoz(Palet.BORDO, karanlik = true, oled = false) }

enum class TemaModu { SISTEM, AYDINLIK, KARANLIK, OLED }

/**
 * @param dinamik Android 12+ duvar kağıdı renklerini kullan (rapor 2.7).
 *   Yalnız Material3 şemasını etkiler; Azim'in kendi nötr ölçeği korunur ki
 *   marka kimliği duvar kağıdına göre dağılmasın.
 */
@Composable
fun AzimTema(
    modu: TemaModu = TemaModu.SISTEM,
    palet: Palet = Palet.BORDO,
    dinamik: Boolean = false,
    icerik: @Composable () -> Unit,
) {
    val sistemKaranlik = isSystemInDarkTheme()
    val karanlik = when (modu) {
        TemaModu.SISTEM -> sistemKaranlik
        TemaModu.AYDINLIK -> false
        else -> true
    }
    val renk = paletiCoz(palet, karanlik, oled = modu == TemaModu.OLED)
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val dinamikDestekli = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
    val m3 = if (dinamik && dinamikDestekli) {
        if (renk.karanlikMi) androidx.compose.material3.dynamicDarkColorScheme(ctx)
        else androidx.compose.material3.dynamicLightColorScheme(ctx)
    } else if (renk.karanlikMi) {
        darkColorScheme(
            primary = renk.accent, background = renk.zemin, surface = renk.yuzey,
            onPrimary = if (renk.karanlikMi) Color(0xFF121416) else Color.White, onBackground = renk.metin, onSurface = renk.metin,
        )
    } else {
        lightColorScheme(
            primary = renk.accent, background = renk.zemin, surface = renk.yuzey,
            onPrimary = if (renk.karanlikMi) Color(0xFF121416) else Color.White, onBackground = renk.metin, onSurface = renk.metin,
        )
    }
    CompositionLocalProvider(LocalAzimRenk provides renk) {
        MaterialTheme(colorScheme = m3, typography = AzimTipografi, content = icerik)
    }
}

/**
 * Ana ekranın zemini — düz renk DEĞİL.
 *
 * Düz siyah bir tuval "boğuk ve hissiz" duruyordu. Referans uygulamaların
 * canlı hissetmesinin sebebi arka planın hep hafif bir derinlik taşıması.
 * Burada iki katman var: üstten aşağı açılan çok hafif bir dikey gradyan ve
 * en üstte accent'in neredeyse görünmez bir izi. İkisi de o kadar kısık ki
 * fark edilmiyor — ama düz zeminle yan yana konunca fark açık.
 *
 * Eski sürümün "aurora" hatasından farkı: bu HAREKETSİZ. Animasyon yok,
 * dikkat dağıtmıyor, yalnız derinlik veriyor.
 */
@Composable
fun zeminFircasi(): Brush {
    val r = LocalAzimRenk.current
    return Brush.verticalGradient(
        0f to r.yuzey.copy(alpha = if (r.karanlikMi) 0.55f else 0.7f),
        0.32f to r.zemin,
        1f to r.zemin,
    )
}

/** Üst köşedeki çok sönük accent izi — markanın nefesi. */
@Composable
fun accentIzi(): Brush {
    val r = LocalAzimRenk.current
    return Brush.radialGradient(
        listOf(r.accent.copy(alpha = if (r.karanlikMi) 0.055f else 0.05f), Color.Transparent),
    )
}

/** Kısayol: `Renk.metinIkincil` */
val Renk: AzimRenkleri
    @Composable get() = LocalAzimRenk.current
