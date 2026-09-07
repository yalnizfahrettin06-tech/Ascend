package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnaEkran(
    sozler: List<Soz>, aktifIndeks: Int, favoriler: Set<String>, seri: Int,
    haftalik: List<Boolean>, bugunGelenler: List<Soz>, gunlukGelenler: Map<String, List<Soz>>,
    bugunGorulen: Int, gunlukHedef: Int, sonrakiBildirim: String?, oneri: Oneri?,
    bugunPlanlanan: Int, dil: String, hatirlaticiAcik: Boolean, bildirimIzni: Boolean,
    indeksDegisti: (Int) -> Unit, favoriDegistir: (Soz) -> Unit, paylas: (Soz) -> Unit,
    sozSecildi: (Soz) -> Unit, kesfeGit: (KategoriGrubu) -> Unit,
    ipucunuKapat: () -> Unit, ayarlaraGit: () -> Unit,
) {
    val pano = LocalClipboardManager.current
    val ses = rememberSeslendirici(dil)
    val kapsam = rememberCoroutineScope()
    val mesaj = remember { SnackbarHostState() }
    val kopyalandi = stringResource(R.string.kopyalandi)
    val durum = rememberPagerState(initialPage = aktifIndeks.coerceIn(0, (sozler.size - 1).coerceAtLeast(0))) { sozler.size.coerceAtLeast(1) }
    LaunchedEffect(aktifIndeks, sozler) {
        if (aktifIndeks in sozler.indices && aktifIndeks != durum.currentPage) durum.scrollToPage(aktifIndeks)
    }
    val aktifSoz = sozler.getOrNull(durum.settledPage)
    LaunchedEffect(aktifSoz?.kimlik) {
        if (aktifSoz != null) indeksDegisti(durum.settledPage)
        ses.durdur()
    }
    Box(Modifier.fillMaxSize().background(Renk.zemin)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).widthIn(max = 640.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Ascend", style = MaterialTheme.typography.titleLarge, color = Renk.metin)
                    Text(LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM, EEEE", Locale.forLanguageTag(dil))), style = MaterialTheme.typography.bodyMedium, color = Renk.metinIkincil)
                }
                IconButton(onClick = ayarlaraGit) { Icon(AzimIkon.Ayarlar, stringResource(R.string.ayarlar), tint = Renk.metin) }
            }
            Text(stringResource(R.string.asc_bugun), style = MaterialTheme.typography.headlineLarge, color = Renk.metin, modifier = Modifier.semantics { heading() })
            if (sozler.isEmpty()) {
                Text(stringResource(R.string.soz_yok), color = Renk.metinIkincil)
                TextButton(onClick = { kesfeGit(Kategoriler.gruplar.first()) }) { Text(stringResource(R.string.asc_kesfet)) }
            } else {
                HorizontalPager(state = durum, modifier = Modifier.fillMaxWidth(), pageSpacing = 16.dp) { sayfa ->
                    val soz = sozler[sayfa]
                    Surface(color = Renk.accentZemin, shape = RoundedCornerShape(28.dp)) {
                        Column(Modifier.fillMaxWidth().heightIn(min = 300.dp).padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            Text(stringResource(R.string.asc_gunun_sozu), style = MaterialTheme.typography.labelLarge, color = Renk.accent)
                            Text(soz.metin(dil), style = MaterialTheme.typography.displaySmall, color = Renk.metin)
                            Text(if (soz.yazar == "Ascend") Kategoriler.bul(soz.kategori)?.ad(dil).orEmpty() else "— ${soz.yazar}", style = MaterialTheme.typography.bodyMedium, color = Renk.metinIkincil)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { kapsam.launch { durum.animateScrollToPage(durum.currentPage - 1) } }, enabled = durum.currentPage > 0 && !durum.isScrollInProgress) { Text(stringResource(R.string.asc_onceki)) }
                    Text(stringResource(R.string.asc_sayfa, durum.currentPage + 1, sozler.size), style = MaterialTheme.typography.labelLarge, color = Renk.metinIkincil)
                    TextButton(onClick = { kapsam.launch { durum.animateScrollToPage(durum.currentPage + 1) } }, enabled = durum.currentPage < sozler.lastIndex && !durum.isScrollInProgress) { Text(stringResource(R.string.asc_sonraki)) }
                }
                aktifSoz?.let { soz ->
                    val favori = soz.kimlik in favoriler
                    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalButton(onClick = { favoriDegistir(soz) }) {
                            Icon(if (favori) AzimIkon.KalpDolu else AzimIkon.Kalp, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(if (favori) R.string.asc_kaydedildi else R.string.favoriye_ekle))
                        }
                        OutlinedButton(onClick = { paylas(soz) }) { Text(stringResource(R.string.paylas)) }
                        TextButton(onClick = { pano.setText(AnnotatedString(soz.metin(dil))); kapsam.launch { mesaj.showSnackbar(kopyalandi) } }) { Text(stringResource(R.string.kopyala)) }
                    }
                    if (ses.hazir) TextButton(onClick = { ses.degistir(soz.metin(dil)) }) { Text(stringResource(if (ses.konusuyor) R.string.sesi_durdur else R.string.sesli_oku)) }
                }
            }
            Surface(color = Renk.yuzey, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.asc_rituel), style = MaterialTheme.typography.bodyLarge, color = Renk.metin)
                    Text(stringResource(R.string.asc_yolculuk_alt), style = MaterialTheme.typography.bodyMedium, color = Renk.metinIkincil)
                }
            }
            Surface(color = Renk.yuzey, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.asc_hatirlaticilar), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
                    Text(when { !hatirlaticiAcik -> stringResource(R.string.asc_bildirim_kapali); !bildirimIzni -> stringResource(R.string.asc_bildirim_engelli); sonrakiBildirim != null -> stringResource(R.string.sonraki_soz, sonrakiBildirim); else -> stringResource(R.string.asc_plan_acik) }, color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = ayarlaraGit) { Text(stringResource(R.string.ayarlar)) }
                }
            }
            if (bugunGelenler.isNotEmpty()) {
                Text(stringResource(R.string.bugun_gelenler), style = MaterialTheme.typography.titleMedium, color = Renk.metin)
                bugunGelenler.forEach { soz ->
                    TextButton(onClick = { sozSecildi(soz) }) { Text(soz.metin(dil), style = MaterialTheme.typography.bodyLarge) }
                }
            }
            OutlinedButton(onClick = { kesfeGit(Kategoriler.gruplar.first()) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.asc_kesfet)) }
            Spacer(Modifier.height(20.dp))
        }
        SnackbarHost(mesaj, Modifier.align(Alignment.BottomCenter))
    }
}
