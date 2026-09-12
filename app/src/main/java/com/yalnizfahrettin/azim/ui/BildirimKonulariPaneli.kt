package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BildirimKonulariPaneli(dil: String, selected: Set<String>, close: () -> Unit, toggle: (String) -> Unit, discover: () -> Unit, settings: () -> Unit = {}) {
    // Keep switched-off rows until dismissal, so the choice can be reversed immediately.
    val original = rememberSaveable { selected.toList() }
    val rows = Kategoriler.tumAltlar.filter { it.anahtar in original || it.anahtar in selected }
    ModalBottomSheet(onDismissRequest = close,containerColor = Renk.zemin,sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp).padding(bottom = 16.dp).testTag("reminder-topic-panel")) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(cevir(dil,"Bildirimlerin","Your reminders"),Modifier.weight(1f),color = Renk.metin,fontSize = 18.sp)
                TextButton(onClick = settings, modifier = Modifier.testTag("reminder-settings")) {
                    Icon(AzimIkon.Ayarlar,null,Modifier.size(17.dp)); Spacer(Modifier.width(6.dp))
                    Text(cevir(dil,"Ayarlar","Settings"))
                }
            }
            Text(cevir(dil,"${selected.size} konu açık · En az bir konu açık kalmalı.","${selected.size} topics on · Keep at least one enabled."),Modifier.padding(vertical = 12.dp),color = Renk.metinIkincil,fontSize = 12.sp)
            LazyColumn(Modifier.fillMaxWidth().weight(1f,fill = false).heightIn(max = minOf(360, rows.size.coerceAtLeast(1) * 70).dp),contentPadding = PaddingValues(vertical = 4.dp)) {
                items(rows,key = { it.anahtar }) { topic ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(topic.ad(dil),Modifier.weight(1f),color = Renk.metin,fontSize = 15.sp,lineHeight = 21.sp)
                        Switch(checked = topic.anahtar in selected,onCheckedChange = { toggle(topic.anahtar) },enabled = topic.anahtar !in selected || selected.size > 1,
                            modifier = Modifier.testTag("reminder-toggle-${topic.anahtar}").semantics { contentDescription = topic.ad(dil) })
                    }
                }
            }
            TextButton(onClick = discover,modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("reminder-discover")) {
                Text(cevir(dil,"Kategorileri keşfet","Explore categories")); Spacer(Modifier.width(8.dp)); Icon(AzimIkon.Ileri,null,Modifier.size(16.dp))
            }
        }
    }
}
