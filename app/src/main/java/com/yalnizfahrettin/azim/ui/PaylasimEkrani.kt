package com.yalnizfahrettin.azim.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Soz
import com.yalnizfahrettin.azim.paylas.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class Sekmesi { ZEMIN, YAZI, FORMAT }

/*
 * PAYLAŞIM STÜDYOSU
 *
 * Önceden paylaşım tek sabit tasarım üretiyordu. Burada kullanıcı zemini,
 * yazı tipini ve formatı seçiyor; önizleme canlı.
 *
 * Önizleme ve çıktı AYNI çiziciden geliyor (KartCizici) — "önizleme başka,
 * paylaşılan başka" hatası yapısal olarak imkânsız.
 *
 * Seçenekler küratörlü: serbest renk seçici, serbest font yükleme yok.
 * Eski sürümün kart stili × tema × font × renk × foto × boyut kombinasyon
 * patlamasını tekrarlamamak için her eksen sınırlı ve tasarlanmış.
 */
@Composable
fun PaylasimEkrani(
    soz: Soz,
    dil: String,
    geri: () -> Unit,
    paylas: (PaylasimAyari) -> Unit,
    videoPaylas: (PaylasimAyari, Int, (Float) -> Unit, (Boolean) -> Unit) -> Unit,
) {
    val ctx = LocalContext.current
    var ayar by remember { mutableStateOf(PaylasimAyari()) }
    var sekme by remember { mutableStateOf(Sekmesi.ZEMIN) }
    var onizleme by remember { mutableStateOf<Bitmap?>(null) }
    // 0 = görsel, >0 = o saniyede video
    var videoSaniye by remember { mutableIntStateOf(0) }
    var uretiliyor by remember { mutableStateOf(false) }
    var ilerleme by remember { mutableFloatStateOf(0f) }

    val fotoSecici = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) ayar = ayar.copy(zemin = KartZemin.Foto(uri)) }

    // Önizleme düşük çözünürlükte üretilir; oran ve yerleşim çıktıyla birebir.
    LaunchedEffect(ayar, soz, dil) {
        onizleme = withContext(Dispatchers.Default) {
            val en = 620
            val boy = (en / ayar.format.oran).toInt()
            KartCizici.ciz(ctx, soz.metin(dil), soz.yazar, ayar, en, boy)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(zeminFircasi())
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // --- başlık ---
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Olcu.sm, vertical = Olcu.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).azimTikla(tikla = geri),
                contentAlignment = Alignment.Center,
            ) {
                Icon(AzimIkon.Geri, "geri", tint = Renk.metinIkincil, modifier = Modifier.size(21.dp))
            }
            Text(
                stringResource(R.string.paylas_baslik),
                style = MaterialTheme.typography.titleMedium,
                color = Renk.metin,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier
                    .clip(RoundedCornerShape(Yaricap.hap))
                    .background(if (uretiliyor) Renk.accentDerin else Renk.accent)
                    .then(
                        if (uretiliyor) Modifier
                        else Modifier.azimTikla(guclu = true) {
                            if (videoSaniye == 0) {
                                paylas(ayar)
                            } else {
                                uretiliyor = true
                                ilerleme = 0f
                                videoPaylas(
                                    ayar, videoSaniye,
                                    { ilerleme = it },
                                    { uretiliyor = false },
                                )
                            }
                        }
                    )
                    .padding(horizontal = Olcu.xl, vertical = Olcu.md),
            ) {
                Text(
                    text = if (uretiliyor) "%${(ilerleme * 100).toInt()}"
                    else stringResource(R.string.paylas),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (uretiliyor) Renk.accent else Color.White,
                )
            }
            Spacer(Modifier.width(Olcu.sm))
        }

        // --- canlı önizleme ---
        Box(
            Modifier.fillMaxWidth().weight(1f).padding(Olcu.xl),
            contentAlignment = Alignment.Center,
        ) {
            onizleme?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(Yaricap.md)),
                )
            }
        }

        // --- çıktı türü: görsel mi video mu ---
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Olcu.xl, vertical = Olcu.sm),
            horizontalArrangement = Arrangement.spacedBy(Olcu.sm),
        ) {
            SekmeHapi(stringResource(R.string.cikti_gorsel), videoSaniye == 0) { videoSaniye = 0 }
            listOf(5, 10, 15).forEach { sn ->
                SekmeHapi(
                    stringResource(R.string.cikti_video, sn),
                    videoSaniye == sn,
                ) { videoSaniye = sn }
            }
        }

        // --- sekmeler ---
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Olcu.xl),
            horizontalArrangement = Arrangement.spacedBy(Olcu.sm),
        ) {
            SekmeHapi(stringResource(R.string.sekme_zemin), sekme == Sekmesi.ZEMIN) { sekme = Sekmesi.ZEMIN }
            SekmeHapi(stringResource(R.string.sekme_yazi), sekme == Sekmesi.YAZI) { sekme = Sekmesi.YAZI }
            SekmeHapi(stringResource(R.string.sekme_format), sekme == Sekmesi.FORMAT) { sekme = Sekmesi.FORMAT }
        }

        Spacer(Modifier.height(Olcu.lg))

        Box(Modifier.fillMaxWidth().height(150.dp)) {
            when (sekme) {
                Sekmesi.ZEMIN -> ZeminSekmesi(
                    ayar = ayar,
                    degistir = { ayar = it },
                    fotoSec = {
                        fotoSecici.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
                Sekmesi.YAZI -> YaziSekmesi(ayar, dil) { ayar = it }
                Sekmesi.FORMAT -> FormatSekmesi(ayar, dil) { ayar = it }
            }
        }
        Spacer(Modifier.height(Olcu.lg))
    }
}

@Composable
private fun SekmeHapi(metin: String, aktif: Boolean, tikla: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(Yaricap.hap))
            .background(if (aktif) Renk.accentDerin else Renk.yuzey)
            .azimTikla(tikla = tikla)
            .padding(horizontal = Olcu.lg, vertical = Olcu.sm),
    ) {
        Text(
            metin,
            style = MaterialTheme.typography.labelLarge,
            color = if (aktif) Renk.accent else Renk.metinIkincil,
        )
    }
}

@Composable
private fun ZeminSekmesi(
    ayar: PaylasimAyari,
    degistir: (PaylasimAyari) -> Unit,
    fotoSec: () -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = Olcu.xl),
            horizontalArrangement = Arrangement.spacedBy(Olcu.md),
        ) {
            item {
                // Fotoğraf — Photo Picker, izin gerekmiyor
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(Yaricap.md))
                        .background(Renk.yuzeyYuksek)
                        .azimTikla(tikla = fotoSec),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(AzimIkon.Kesfet, null, tint = Renk.metinIkincil, modifier = Modifier.size(20.dp))
                }
            }
            items(HazirZeminler.gradyanlar) { g ->
                GradyanKutusu(g, ayar.zemin == g) { degistir(ayar.copy(zemin = g)) }
            }
            items(HazirZeminler.duzler) { d ->
                DuzKutusu(d, ayar.zemin == d) { degistir(ayar.copy(zemin = d)) }
            }
        }
        if (ayar.zemin is KartZemin.Foto) {
            Spacer(Modifier.height(Olcu.lg))
            Column(Modifier.padding(horizontal = Olcu.xl)) {
                Text(
                    stringResource(R.string.karartma),
                    style = MaterialTheme.typography.labelSmall,
                    color = Renk.metinSonuk,
                )
                androidx.compose.material3.Slider(
                    value = ayar.karartma,
                    onValueChange = { degistir(ayar.copy(karartma = it)) },
                    valueRange = 0f..0.85f,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = Renk.accent,
                        activeTrackColor = Renk.accent,
                        inactiveTrackColor = Renk.kenarlik,
                    ),
                )
            }
        }
    }
}

@Composable
private fun DuzKutusu(z: KartZemin.Duz, seciliMi: Boolean, tikla: () -> Unit) {
    Box(
        Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(Yaricap.md))
            .background(Color(z.renk))
            .then(if (seciliMi) Modifier.border(2.dp, Renk.accent, RoundedCornerShape(Yaricap.md)) else Modifier)
            .azimTikla(tikla = tikla),
    )
}

@Composable
private fun GradyanKutusu(z: KartZemin.Gradyan, seciliMi: Boolean, tikla: () -> Unit) {
    Box(
        Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(Yaricap.md))
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(z.ust), Color(z.alt))
                )
            )
            .then(if (seciliMi) Modifier.border(2.dp, Renk.accent, RoundedCornerShape(Yaricap.md)) else Modifier)
            .azimTikla(tikla = tikla),
    )
}

@Composable
private fun YaziSekmesi(ayar: PaylasimAyari, dil: String, degistir: (PaylasimAyari) -> Unit) {
    Column {
        LazyRow(
            contentPadding = PaddingValues(horizontal = Olcu.xl),
            horizontalArrangement = Arrangement.spacedBy(Olcu.sm),
        ) {
            items(KartYazi.entries.toList()) { y ->
                SekmeHapi(y.etiket(dil), ayar.yazi == y) { degistir(ayar.copy(yazi = y)) }
            }
        }
        Spacer(Modifier.height(Olcu.lg))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Olcu.xl),
            horizontalArrangement = Arrangement.spacedBy(Olcu.sm),
        ) {
            SekmeHapi("A−", false) {
                degistir(ayar.copy(yaziOlcegi = (ayar.yaziOlcegi - 0.1f).coerceAtLeast(0.7f)))
            }
            SekmeHapi("A+", false) {
                degistir(ayar.copy(yaziOlcegi = (ayar.yaziOlcegi + 0.1f).coerceAtMost(1.4f)))
            }
            SekmeHapi(
                stringResource(R.string.hizala),
                ayar.hizalama == KartHizalama.SOL,
            ) {
                degistir(
                    ayar.copy(
                        hizalama = if (ayar.hizalama == KartHizalama.ORTA) KartHizalama.SOL
                        else KartHizalama.ORTA
                    )
                )
            }
        }
    }
}

@Composable
private fun FormatSekmesi(ayar: PaylasimAyari, dil: String, degistir: (PaylasimAyari) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = Olcu.xl),
        horizontalArrangement = Arrangement.spacedBy(Olcu.sm),
    ) {
        KartFormat.entries.forEach { f ->
            SekmeHapi(f.etiket(dil), ayar.format == f) { degistir(ayar.copy(format = f)) }
        }
    }
}
