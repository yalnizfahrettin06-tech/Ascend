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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
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
fun KesifMerkezi(dil: String, topics: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        Text(cevir(dil,"Keşfet","Explore"), Modifier.padding(horizontal = 24.dp, vertical = 14.dp), color = Renk.metin, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Box(Modifier.weight(1f)) { topics() }
    }
}

@Composable
fun GorunumEkrani(dil: String, selected: String?, pro: Boolean, proOpen: () -> Unit, select: (String) -> Unit) {
    var widgetTab by rememberSaveable { mutableStateOf(false) }
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        Text(cevir(dil,"Görünüm","Appearance"), Modifier.padding(horizontal = 24.dp, vertical = 14.dp), color = Renk.metin, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Row(Modifier.padding(horizontal = 24.dp).fillMaxWidth().background(Renk.yuzey, RoundedCornerShape(16.dp)).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(false, true).forEach { widget ->
                Surface(onClick = { widgetTab = widget }, color = if(widgetTab == widget) Renk.metin else Renk.yuzey, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f).testTag(if(widget) "appearance-widget" else "appearance-theme")) {
                    Text(if(widget) "Widget" else cevir(dil,"Uygulama teması","App theme"), Modifier.padding(vertical = 13.dp, horizontal = 6.dp), color = if(widgetTab == widget) Renk.zemin else Renk.metinIkincil, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if(!widgetTab) {
                Text(cevir(dil,"Günün, senin renklerinle.","Your day, in your colors."), fontSize = 16.sp, color = Renk.metinIkincil)
                TemaGrid(dil, AnaTemalar.all, AnaTemalar.allowed(selected, pro).id, pro) { preview = it.id }
            } else {
                ProRozeti(metin = "WIDGET · PRO")
                Text(cevir(dil,"İyi bir söz, telefonunda.","A good thought, on your phone."), fontSize = 27.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold, color = Renk.metin)
                val theme = AnaTemalar.allowed(selected, pro)
                val sample = cevir(dil,"Küçük bir adım da ilerlemektir.","A small step is still a step forward.")
                val bmp = remember(theme, dil) { com.yalnizfahrettin.azim.widget.WidgetTasarimi.render(ctx, com.yalnizfahrettin.azim.widget.WidgetSecimi(theme.id), sample, "Ascend", 720, 360) }
                androidx.compose.foundation.Image(bmp.asImageBitmap(), sample, Modifier.fillMaxWidth().aspectRatio(2f).clip(RoundedCornerShape(22.dp)))
                Text(cevir(dil,"Arka planını seç, telefonuna ekle. Söz her gün yenilenir; yazıyı biz yerleştiririz.","Choose a background and add it to your phone. The quote changes daily; we handle the layout."), color = Renk.metinIkincil, fontSize = 15.sp, lineHeight = 23.sp)
                Button(onClick = { ctx.startActivity(Intent(ctx, WidgetAyarActivity::class.java)) }, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("widget-editor-open"), shape = RoundedCornerShape(16.dp)) { Text(cevir(dil,"Widget oluştur","Create widget")) }
            }
        }
    }
    preview?.let { id -> TemaOnizleme(AnaTemalar.find(id), dil, pro, close = { preview = null }, apply = { select(id); preview = null }, proOpen = proOpen) }
}
