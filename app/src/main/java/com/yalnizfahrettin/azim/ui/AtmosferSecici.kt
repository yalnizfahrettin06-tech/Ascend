package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yalnizfahrettin.azim.core.AzimIkon
import com.yalnizfahrettin.azim.core.Renk

@Composable
fun AtmosferSecici(
    dil: String,
    secili: Atmosfer?,
    otomatik: Atmosfer,
    sec: (Atmosfer?) -> Unit,
    kapat: () -> Unit,
) {
    var filtre by rememberSaveable { mutableStateOf((secili ?: otomatik).grup.name) }
    val grup = AtmosferGrubu.valueOf(filtre)
    val gorunen = remember(grup) { Atmosfer.entries.filter { it.grup == grup } }
    val liste = rememberLazyGridState()
    LaunchedEffect(grup) { liste.scrollToItem(gorunen.indexOf(secili).coerceAtLeast(0)) }
    val pencereYuksekligi = (LocalConfiguration.current.screenHeightDp * .86f).dp

    Dialog(onDismissRequest = kapat, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(.94f).heightIn(max = pencereYuksekligi).testTag("scene-picker"),
            shape = RoundedCornerShape(28.dp), color = Renk.zemin,
            border = BorderStroke(1.dp, Renk.kenarlik),
        ) {
            Column {
                Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(cevir(dil, "Arka plan seç", "Choose a background"), Modifier.weight(1f).semantics { heading() }, style = MaterialTheme.typography.titleLarge, color = Renk.metin)
                    IconButton(onClick = kapat, modifier = Modifier.size(48.dp).testTag("scene-close")) {
                        Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close"), tint = Renk.metin)
                    }
                }
                Surface(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
                        .selectable(selected = secili == null, role = Role.RadioButton, onClick = { sec(null) })
                        .testTag("scene-auto"),
                    color = if (secili == null) Renk.accentZemin else Renk.yuzey,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(Modifier.heightIn(min = 60.dp).padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(AzimIkon.Dag, null, Modifier.size(24.dp), tint = Renk.accent)
                        Column(Modifier.weight(1f)) {
                            Text(cevir(dil, "Konuya göre otomatik", "Match the topic"), style = MaterialTheme.typography.labelLarge, color = Renk.metin)
                            Text(cevir(dil, "Şimdi: ${otomatik.ad(dil)}", "Now: ${otomatik.ad(dil)}"), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                        }
                        if (secili == null) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.accent)
                    }
                }
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AtmosferGrubu.entries, key = { it.name }) { g ->
                        FilterChip(
                            selected = grup == g, onClick = { filtre = g.name },
                            label = { Text(g.ad(dil)) }, shape = RoundedCornerShape(50),
                            modifier = Modifier.heightIn(min = 48.dp).testTag("scene-filter-${g.name.lowercase()}"),
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), state = liste,
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false).selectableGroup().testTag("scene-grid"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(gorunen, key = { it.name }) { atmosfer ->
                        val secildi = secili == atmosfer
                        Surface(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                                .selectable(selected = secildi, role = Role.RadioButton, onClick = { sec(atmosfer) })
                                .testTag("scene-choice-${atmosfer.name.lowercase()}"),
                            color = if (secildi) Renk.accentZemin else Renk.yuzey,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(if (secildi) 2.dp else 1.dp, if (secildi) Renk.accent else Renk.kenarlik),
                        ) {
                            Column {
                                Box(Modifier.fillMaxWidth().aspectRatio(1.35f)) {
                                    AtmosferResmi(atmosfer, Modifier.matchParentSize(), karartma = .08f)
                                    if (secildi) Surface(shape = CircleShape, color = Renk.accent, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                                        Icon(AzimIkon.Tik, null, Modifier.padding(5.dp).size(16.dp), tint = Renk.zemin)
                                    }
                                }
                                Text(atmosfer.ad(dil), Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), style = MaterialTheme.typography.labelLarge, color = if (secildi) Renk.accent else Renk.metin)
                            }
                        }
                    }
                }
            }
        }
    }
}
