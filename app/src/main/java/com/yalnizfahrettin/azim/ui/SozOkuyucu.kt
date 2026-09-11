package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

/** A focused reader over the originating list, which remains mounted with its scroll state. */
@Composable
fun SozOkuyucu(soz: Soz, dil: String, saved: Boolean, close: () -> Unit, save: () -> Unit, share: () -> Unit) {
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().padding(24.dp).testTag("quote-reader")) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = close, modifier = Modifier.testTag("reader-back")) {
                    Icon(AzimIkon.Geri, cevir(dil, "Geri", "Back"))
                }
                Text(Kategoriler.bul(soz.kategori)?.ad(dil).orEmpty(), color = Renk.metinIkincil, modifier = Modifier.weight(1f))
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Center) {
                Text(soz.metin(dil), fontFamily = LoraSerif, fontSize = 30.sp, lineHeight = 41.sp, color = Renk.metin)
                Spacer(Modifier.height(24.dp))
                Text(soz.sunumEtiketi(dil), color = Renk.metinIkincil)
            }
            HorizontalDivider(color = Renk.kenarlik)
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = save, modifier = Modifier.heightIn(min = 48.dp).testTag("reader-save")) {
                    Icon(if(saved) AzimIkon.KalpDolu else AzimIkon.Kalp, null, Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp)); Text(cevir(dil, if(saved) "Kaydedildi" else "Kaydet", if(saved) "Saved" else "Save"))
                }
                TextButton(onClick = share, modifier = Modifier.heightIn(min = 48.dp).testTag("reader-share")) {
                    Icon(AzimIkon.Paylas, null, Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp)); Text(cevir(dil, "Paylaş", "Share"))
                }
            }
        }
    }
}
