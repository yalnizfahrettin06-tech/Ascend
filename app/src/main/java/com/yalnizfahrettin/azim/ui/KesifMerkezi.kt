package com.yalnizfahrettin.azim.ui

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(false, true).forEach { value ->
                FilterChip(selected = custom == value, onClick = { custom = value },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag(if(value) "explore-customize" else "explore-topics"),
                    label = { Text(cevir(dil, if(value) "Özelleştir" else "Konular", if(value) "Customize" else "Topics"), fontWeight = FontWeight.Medium) })
            }
        }
        Box(Modifier.weight(1f)) {
            if (!custom) topics() else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text(cevir(dil, "Ana ekran teması", "Home screen theme"), color = Renk.metin, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(cevir(dil, "Beyaz ve Siyah ücretsiz. Diğer görünümler Pro.", "White and Black are free. More looks with Pro."), color = Renk.metinIkincil, fontSize = 13.sp)
                Surface(onClick = { ctx.startActivity(Intent(ctx, WidgetAyarActivity::class.java)) }, color = Renk.yuzey, shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Renk.kenarlik), modifier = Modifier.fillMaxWidth().testTag("widget-editor-open")) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("WIDGET · PRO", color = Renk.metinIkincil, fontSize = 10.sp)
                        Text(cevir(dil, "Telefonunda küçük bir ilham alanı.", "A little inspiration on your phone."), color = Renk.metin, fontSize = 17.sp, lineHeight = 23.sp)
                        Text(cevir(dil, "Önizle ve özelleştir →", "Preview and customize →"), color = Renk.metinIkincil, fontSize = 13.sp)
                    }
                }
                TemaGrid(dil, AnaTemalar.all, AnaTemalar.allowed(selected, pro).id, pro) { preview = it.id }
            }
        }
    }
    preview?.let { id -> TemaOnizleme(AnaTemalar.find(id), dil, pro,
        close = { preview = null }, apply = { select(id); preview = null }, proOpen = proOpen) }
}
