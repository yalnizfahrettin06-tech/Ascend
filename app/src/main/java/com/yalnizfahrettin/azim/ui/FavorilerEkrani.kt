package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.AzimIkon
import com.yalnizfahrettin.azim.core.azimTikla
import com.yalnizfahrettin.azim.data.Soz
import com.yalnizfahrettin.azim.core.LoraSerif
import com.yalnizfahrettin.azim.core.Olcu
import com.yalnizfahrettin.azim.core.Renk
import com.yalnizfahrettin.azim.core.Yaricap

@Composable
fun FavorilerEkrani(favoriler: List<Soz>, dil: String, cikar: (String) -> Unit, oku: (Soz) -> Unit, kesfet: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding(),
        contentPadding = PaddingValues(bottom = Olcu.x5),
    ) {
        item {
            Column(Modifier.padding(horizontal = Olcu.xl).padding(top = Olcu.xxl, bottom = Olcu.lg)) {
                Text(
                    stringResource(R.string.favoriler_baslik),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Renk.metin,
                )
            }
        }
        if (favoriler.isEmpty()) {
            item {
                Spacer(Modifier.height(Olcu.x5))
                BosDurum(
                    ikon = AzimIkon.Kalp,
                    baslik = stringResource(R.string.favori_bos_baslik),
                    aciklama = stringResource(R.string.favori_bos_alt),
                )
            }
            item { androidx.compose.material3.TextButton(onClick = kesfet, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.asc_okumaya_don)) } }
        } else {
            items(favoriler, key = { it.kimlik }) { s ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Olcu.xl, vertical = Olcu.xs)
                        .clip(RoundedCornerShape(Yaricap.md))
                        .background(Renk.yuzey)
                        .border(1.dp, Renk.kenarlik, RoundedCornerShape(Yaricap.md))
                        .padding(Olcu.lg),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = s.metin(dil),
                        fontFamily = LoraSerif,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Renk.metin,
                        modifier = Modifier.weight(1f).clickable { oku(s) }.padding(vertical = 12.dp),
                    )
                    Spacer(Modifier.width(Olcu.md))
                    Box(
                        Modifier.size(48.dp).azimTikla(guclu = true) { cikar(s.kimlik) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            AzimIkon.KalpDolu,
                            contentDescription = stringResource(R.string.favoriden_cikar),
                            tint = Renk.accent,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}
