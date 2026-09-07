package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@Composable
fun Onboarding(dil: String, kaydediliyor: Boolean = false, hata: String? = null,
    bitir: (Set<String>, Int, Int, Int, Boolean) -> Unit,
) {
    var adim by rememberSaveable { mutableIntStateOf(0) }
    var secili by rememberSaveable { mutableStateOf(Baslangic.varsayilan) }
    var adet by rememberSaveable { mutableIntStateOf(1) }
    var bas by rememberSaveable { mutableIntStateOf(9) }
    var bit by rememberSaveable { mutableIntStateOf(21) }
    BackHandler(adim > 0 || kaydediliyor) { if (!kaydediliyor) adim-- }
    Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (adim > 0) TextButton(onClick = { adim-- }, enabled = !kaydediliyor) { Text(stringResource(R.string.asc_geri)) }
            else Spacer(Modifier.width(16.dp))
            Text("Ascend", style = MaterialTheme.typography.titleLarge, color = Renk.metin, modifier = Modifier.weight(1f))
            Text(stringResource(R.string.asc_adim, adim + 1, 3), color = Renk.metinIkincil, style = MaterialTheme.typography.labelLarge)
        }
        LinearProgressIndicator(progress = { (adim + 1) / 3f }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), color = Renk.accent, trackColor = Renk.kenarlik)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 28.dp).widthIn(max = 600.dp).align(Alignment.CenterHorizontally)) {
            when (adim) {
                0 -> Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text(stringResource(R.string.asc_kucuk_an), color = Renk.accent, style = MaterialTheme.typography.labelLarge)
                    OnboardingBaslik(stringResource(R.string.asc_hosgeldin), stringResource(R.string.asc_hosgeldin_alt))
                    Surface(color = Renk.accentZemin, shape = RoundedCornerShape(28.dp)) {
                        Column(Modifier.fillMaxWidth().padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(stringResource(R.string.asc_ilk_olumlama), color = Renk.accent, style = MaterialTheme.typography.labelLarge)
                            Text(Olumlamalar.tumu.first().metin(dil), style = MaterialTheme.typography.displaySmall, color = Renk.metin)
                            Text(stringResource(R.string.asc_rituel), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Text(stringResource(R.string.asc_baslangic_guven), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                }
                1 -> Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OnboardingBaslik(stringResource(R.string.asc_niyet), stringResource(R.string.asc_niyet_alt))
                    Baslangic.konular.forEach { anahtar ->
                        val kat = Kategoriler.bul(anahtar) ?: return@forEach
                        val aktif = anahtar in secili
                        Surface(color = if (aktif) Renk.accentZemin else Renk.yuzey, shape = RoundedCornerShape(20.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (aktif) Renk.accent else Renk.kenarlik)) {
                            Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).toggleable(value = aktif, role = Role.Checkbox, onValueChange = { secili = Baslangic.secimiDegistir(secili, anahtar) }).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(kat.ad(dil), color = Renk.metin, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                Checkbox(checked = aktif, onCheckedChange = null)
                            }
                        }
                    }
                    Text(stringResource(if (secili.isEmpty()) R.string.asc_en_az_bir else R.string.asc_secili, secili.size), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                }
                else -> {
                    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OnboardingBaslik(stringResource(R.string.asc_ritim), stringResource(R.string.asc_ritim_alt))
                        Text(stringResource(R.string.asc_bildirim_onizleme), color = Renk.metinIkincil, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(24.dp))
                    BildirimPlani(adet, bas, bit, secili.size, { adet = it }, { b, s -> bas = b; bit = s })
                }
            }
        }
        Column(Modifier.fillMaxWidth().background(Renk.zemin).padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            hata?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp)) }
            Button(onClick = { if (adim < 2) adim++ else bitir(secili, adet, bas, bit, true) },
                enabled = !kaydediliyor && (adim != 1 || secili.isNotEmpty()),
                modifier = Modifier.widthIn(max = 552.dp).fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(18.dp),
            ) { Text(stringResource(if (kaydediliyor) R.string.asc_kaydediliyor else when (adim) { 0 -> R.string.asc_kisisellestir; 1 -> R.string.ob_devam; else -> R.string.asc_hatirlatici_ac })) }
            if (adim == 2) TextButton(onClick = { bitir(secili, adet, bas, bit, false) }, enabled = !kaydediliyor, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.asc_simdilik_atla)) }
        }
    }
}

@Composable
private fun OnboardingBaslik(baslik: String, aciklama: String) {
    Text(baslik, style = MaterialTheme.typography.headlineLarge, color = Renk.metin, modifier = Modifier.semantics { heading() })
    Text(aciklama, style = MaterialTheme.typography.bodyLarge, color = Renk.metinIkincil)
}

@Composable
fun AnaDugme(metin: String, tikla: () -> Unit) {
    Button(onClick = tikla, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(18.dp)) { Text(metin) }
}
