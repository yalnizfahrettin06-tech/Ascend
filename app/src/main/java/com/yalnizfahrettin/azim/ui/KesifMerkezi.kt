package com.yalnizfahrettin.azim.ui

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.widget.WidgetAyarActivity

@Composable
fun KesifMerkezi(dil: String, selected: String?, pro: Boolean, proOpen: () -> Unit, select: (String) -> Unit,
    selectedRequest: Int, topics: @Composable () -> Unit) {
    var custom by rememberSaveable { mutableStateOf(false) }
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    val ctx = LocalContext.current
    LaunchedEffect(selectedRequest) { if (selectedRequest > 0) custom = false }
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        BackHandler(custom) { custom = false }
        Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            if(custom) IconButton(onClick = { custom = false }) { Icon(AzimIkon.Geri, cevir(dil,"Geri","Back"), tint = Renk.metin) }
            Text(cevir(dil, if(custom) "Görünüm" else "Keşfet", if(custom) "Appearance" else "Explore"), Modifier.weight(1f), color = Renk.metin, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            if(!custom) IconButton(onClick = { custom = true }, modifier = Modifier.testTag("explore-customize")) { Icon(AzimIkon.Izgara, cevir(dil,"Tema ve widget","Themes and widgets"), tint = Renk.metin) }
        }
        Box(Modifier.weight(1f)) {
            if (!custom) topics() else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Surface(onClick = { ctx.startActivity(Intent(ctx, WidgetAyarActivity::class.java)) }, color = Renk.yuzey, shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("widget-editor-open")) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(cevir(dil,"Telefonuna widget ekle","Add a phone widget"), Modifier.weight(1f), color = Renk.metin, fontSize = 14.sp)
                        ProRozeti(); Spacer(Modifier.width(10.dp)); Icon(AzimIkon.Ileri, null, Modifier.size(16.dp), tint = Renk.metinIkincil)
                    }
                }
                Text(cevir(dil,"Temanı seç","Choose your theme"), color = Renk.metinIkincil, fontSize = 13.sp)
                TemaGrid(dil, AnaTemalar.all, AnaTemalar.allowed(selected, pro).id, pro) { preview = it.id }
            }
        }
    }
    preview?.let { id -> TemaOnizleme(AnaTemalar.find(id), dil, pro,
        close = { preview = null }, apply = { select(id); preview = null }, proOpen = proOpen) }
}
