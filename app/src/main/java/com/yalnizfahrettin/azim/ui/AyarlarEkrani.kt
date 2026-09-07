package com.yalnizfahrettin.azim.ui

import androidx.compose.material3.Switch
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.yalnizfahrettin.azim.notif.TeslimatYardimi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.BuildConfig
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.AzimIkon
import com.yalnizfahrettin.azim.core.azimTikla
import com.yalnizfahrettin.azim.core.Olcu
import com.yalnizfahrettin.azim.core.Palet
import com.yalnizfahrettin.azim.core.Renk
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.core.Yaricap

/*
 * AYARLAR
 *
 * Eski sürümde bu ekran 911 satır, 8 bölüm ve içinde bir ARAMA KUTUSU
 * ("Ayarları filtrele...") barındırıyordu. Bir ayar ekranına arama koymak
 * zorunda kalmak, o ekranın artık bir ekran olmadığının kanıtıdır.
 *
 * Burada üç bölüm var ve hepsi tek ekrana sığıyor. Kart stili / kart teması /
 * kart fontu / kart rengi / kart fotoğrafı / kart boyutu gibi paylaşım kartı
 * özelleştirmeleri kaldırıldı: kombinasyon uzayı test edilemiyordu ve
 * kullanıcının ilgilendiği şey söz, kartın fontu değil.
 */
@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AyarlarEkrani(
    tema: TemaModu,
    dil: String,
    gunlukAdet: Int,
    baslangic: Int,
    bitis: Int,
    haptik: Boolean,
    dinamikRenk: Boolean,
    palet: Palet,
    hatirlaticiAcik: Boolean,
    bildirimIzni: Boolean,
    hatirlaticiSec: (Boolean) -> Unit,
    temaSec: (TemaModu) -> Unit,
    dilSec: (String) -> Unit,
    adetSec: (Int) -> Unit,
    saatSec: (Int, Int) -> Unit,
    haptikSec: (Boolean) -> Unit,
    dinamikSec: (Boolean) -> Unit,
    paletSec: (Palet) -> Unit,
    seciliKategoriSayisi: Int,
    geri: () -> Unit,
) {
    var kurulum by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .background(Renk.zemin)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Olcu.sm, vertical = Olcu.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(44.dp).azimTikla(etiket = "geri", tikla = geri),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    AzimIkon.Geri, "geri",
                    tint = Renk.metinIkincil, modifier = Modifier.size(22.dp),
                )
            }
            Text(
                stringResource(R.string.ayarlar),
                style = MaterialTheme.typography.headlineSmall,
                color = Renk.metin,
            )
        }

        BolumBasligi(stringResource(R.string.gorunum))
        SecimSatiri(
            stringResource(R.string.tema),
            listOf(
                TemaModu.SISTEM to stringResource(R.string.tema_sistem),
                TemaModu.AYDINLIK to stringResource(R.string.tema_aydinlik),
                TemaModu.KARANLIK to stringResource(R.string.tema_karanlik),
                TemaModu.OLED to stringResource(R.string.tema_oled),
            ),
            tema,
            temaSec,
        )
        SecimSatiri(
            stringResource(R.string.renk_paleti),
            Palet.entries.map { it to it.etiket(dil) },
            palet,
            paletSec,
        )
        SecimSatiri(
            stringResource(R.string.dil),
            listOf("tr" to "Türkçe", "en" to "English"),
            dil,
            dilSec,
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AyarSatiri(
                stringResource(R.string.dinamik_renk),
                stringResource(R.string.dinamik_renk_alt),
            ) { Anahtar(dinamikRenk, dinamikSec, stringResource(R.string.dinamik_renk)) }
        }

        BolumBasligi(stringResource(R.string.bildirimler))
        AyarSatiri(stringResource(R.string.asc_hatirlaticilar), null) { Anahtar(hatirlaticiAcik, hatirlaticiSec, stringResource(R.string.asc_hatirlaticilar)) }
        if (hatirlaticiAcik && !bildirimIzni) {
            val ctx = LocalContext.current
            Text(stringResource(R.string.asc_bildirim_engelli), color = Renk.metinIkincil, modifier = Modifier.padding(horizontal = Olcu.xl))
            androidx.compose.material3.TextButton(onClick = { TeslimatYardimi.bildirimAyarlariniAc(ctx) }, modifier = Modifier.padding(horizontal = Olcu.xl)) { Text(stringResource(R.string.sistem_bildirim_ayarlari)) }
        }
        if (hatirlaticiAcik) BildirimPlani(
            adet = gunlukAdet,
            baslangic = baslangic,
            bitis = bitis,
            seciliKategoriSayisi = seciliKategoriSayisi,
            adetDegisti = adetSec,
            araligiDegisti = saatSec,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(Olcu.lg))
        androidx.compose.material3.OutlinedButton(onClick = { kurulum = true }, modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().heightIn(min = 52.dp)) { Text(cevir(dil, "Ayrıntılı bildirim kurulumu", "Detailed notification setup")) }

        AyarSatiri(stringResource(R.string.haptik), null) { Anahtar(haptik, haptikSec, stringResource(R.string.haptik)) }


        BolumBasligi(stringResource(R.string.hakkinda))
        AyarSatiri(stringResource(R.string.surum), null) {
            Text(
                BuildConfig.VERSION_NAME,
                style = MaterialTheme.typography.bodyMedium,
                color = Renk.metinSonuk,
            )
        }
        Spacer(Modifier.height(Olcu.x5))
    }
    if (kurulum) androidx.compose.material3.ModalBottomSheet(onDismissRequest = { kurulum = false }, containerColor = Renk.zemin,
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.9f).verticalScroll(rememberScrollState()).padding(24.dp)) {
            KucukBaslik(cevir(dil, "Bildirim kurulumu", "Notification setup"))
            Spacer(Modifier.height(16.dp))
            BildirimKurulumu(dil, bildirimIzni) { hatirlaticiSec(true) }
            androidx.compose.material3.TextButton(onClick = { kurulum = false }, modifier = Modifier.fillMaxWidth()) { Text(cevir(dil, "Tamam", "Done")) }
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> SecimSatiri(
    baslik: String,
    secenekler: List<Pair<T, String>>,
    secili: T,
    sec: (T) -> Unit,
) {
    Column(Modifier.padding(horizontal = Olcu.xl, vertical = Olcu.sm)) {
        Text(baslik, style = MaterialTheme.typography.bodyLarge, color = Renk.metin)
        Spacer(Modifier.height(Olcu.md))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Olcu.sm), verticalArrangement = Arrangement.spacedBy(Olcu.sm)) {
            secenekler.forEachIndexed { i, (deger, etiket) ->
                Hap(etiket, deger == secili) { sec(deger) }

            }
        }
    }
}

@Composable
private fun Hap(metin: String, aktif: Boolean = false, tikla: () -> Unit) {
    Box(
        Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(Yaricap.hap))
            .background(if (aktif) Renk.accentZemin else Renk.yuzey)
            .border(
                1.dp,
                if (aktif) Renk.accentSonuk else Renk.kenarlik,
                RoundedCornerShape(Yaricap.hap),
            )
            .selectable(selected = aktif, role = Role.RadioButton, onClick = tikla)
            .padding(horizontal = Olcu.md, vertical = Olcu.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            metin,
            style = MaterialTheme.typography.labelLarge,
            color = if (aktif) Renk.accent else Renk.metinIkincil,
            maxLines = 1,
        )
    }
}

/**
 * Samsung "Ayrıntılı açılır pencere" yönlendirmesi.
 *
 * One UI'ın varsayılanı "Kısa açılır pencere" ve metni erken kırpıyor.
 * Uzun sözlerde yaşanan kırpılmanın büyük kısmı bundan kaynaklanıyor.
 * Ayarı programatik değiştiremiyoruz; doğru sayfaya götürüp GÖSTERİYORUZ —
 * iki küçük mockup metinle anlatmaktan çok daha anlaşılır.
 */
@Composable
private fun AcilirPencereBlogu() {
    val ctx = LocalContext.current
    if (!TeslimatYardimi.acilirPencereAyariVarMi()) return

    Column(Modifier.padding(horizontal = Olcu.xl)) {
        Text(
            stringResource(R.string.acilir_pencere_baslik),
            style = MaterialTheme.typography.bodyLarge,
            color = Renk.metin,
        )
        Spacer(Modifier.height(Olcu.xs))
        Text(
            stringResource(R.string.acilir_pencere_alt),
            style = MaterialTheme.typography.bodyMedium,
            color = Renk.metinSonuk,
        )
        Spacer(Modifier.height(Olcu.lg))
        Row(Modifier.fillMaxWidth()) {
            PencereOrnegi(
                Modifier.weight(1f),
                stringResource(R.string.pencere_kisa),
                satirSayisi = 1,
                iyi = false,
            )
            Spacer(Modifier.width(Olcu.md))
            PencereOrnegi(
                Modifier.weight(1f),
                stringResource(R.string.pencere_ayrintili),
                satirSayisi = 3,
                iyi = true,
            )
        }
        Spacer(Modifier.height(Olcu.lg))
        Hap(stringResource(R.string.ayari_ac)) { TeslimatYardimi.bildirimAyarlariniAc(ctx) }
        Spacer(Modifier.height(Olcu.sm))
    }
}

/** Kırpılmış ve tam bildirimi yan yana gösteren küçük örnek. */
@Composable
private fun PencereOrnegi(modifier: Modifier, etiket: String, satirSayisi: Int, iyi: Boolean) {
    Column(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Yaricap.sm))
                .background(Renk.yuzeyYuksek)
                .then(
                    if (iyi) Modifier.border(1.dp, Renk.accent, RoundedCornerShape(Yaricap.sm))
                    else Modifier
                )
                .padding(Olcu.md),
        ) {
            Box(
                Modifier.fillMaxWidth(0.45f).height(5.dp)
                    .clip(RoundedCornerShape(Yaricap.hap))
                    .background(if (iyi) Renk.accent else Renk.kenarlikGuclu),
            )
            repeat(satirSayisi) { i ->
                Spacer(Modifier.height(Olcu.sm))
                Box(
                    Modifier
                        .fillMaxWidth(if (i == satirSayisi - 1 && satirSayisi > 1) 0.6f else 1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(Yaricap.hap))
                        .background(Renk.kenarlikGuclu),
                )
            }
            if (!iyi) {
                Spacer(Modifier.height(Olcu.sm))
                Text("…", style = MaterialTheme.typography.labelSmall, color = Renk.metinSonuk)
            }
        }
        Spacer(Modifier.height(Olcu.sm))
        Text(
            etiket,
            style = MaterialTheme.typography.labelSmall,
            color = if (iyi) Renk.accent else Renk.metinSonuk,
        )
    }
}

/** Açma/kapama anahtarı — Material3 Switch yerine marka diliyle. */
@Composable
private fun Anahtar(acik: Boolean, degistir: (Boolean) -> Unit, etiket: String) {
    Switch(checked = acik, onCheckedChange = degistir, modifier = Modifier.semantics { contentDescription = etiket })
}

@Composable
private fun SayiHapi(n: Int, aktif: Boolean, tikla: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(Yaricap.sm))
            .background(if (aktif) Renk.accentZemin else Renk.yuzey)
            .border(
                1.dp,
                if (aktif) Renk.accentSonuk else Renk.kenarlik,
                RoundedCornerShape(Yaricap.sm),
            )
            .selectable(selected = aktif, role = Role.RadioButton, onClick = tikla),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "$n",
            style = MaterialTheme.typography.labelLarge,
            color = if (aktif) Renk.accent else Renk.metinIkincil,
        )
    }
}
