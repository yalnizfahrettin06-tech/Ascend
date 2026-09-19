package com.yalnizfahrettin.azim.ui

import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clipToBounds
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
fun KesifMerkezi(dil: String, showHeading: Boolean = true, topics: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        if(showHeading) Text(cevir(dil,"Keşfet","Explore"), Modifier.padding(horizontal = 24.dp, vertical = 9.dp), color = Renk.metin, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Box(Modifier.weight(1f)) { topics() }
    }
}

@Composable
fun GorunumEkrani(dil: String, selected: String?, pro: Boolean, proOpen: () -> Unit, select: (String) -> Unit, offerOpen: ((ProOffer) -> Unit)? = null, offerDismissals: Int = 0) {
    var living by rememberSaveable { mutableStateOf(false) }
    var collectionOffering by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(pro,offerDismissals) { collectionOffering = false }
    var widgetTab by rememberSaveable { mutableStateOf(false) }
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingApply by rememberSaveable { mutableStateOf(false) }
    var lastDismissal by rememberSaveable { mutableIntStateOf(offerDismissals) }
    LaunchedEffect(offerDismissals) { if(lastDismissal != offerDismissals) { pendingApply = false; lastDismissal = offerDismissals } }
    LaunchedEffect(pro) { if(pro && pendingApply && preview != null) { select(preview!!); preview = null; pendingApply = false } }
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        Text(cevir(dil,"Görünüm","Appearance"), Modifier.padding(horizontal = 24.dp, vertical = 9.dp), color = Renk.metin, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Row(Modifier.padding(horizontal = 24.dp).fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(false, true).forEach { widget ->
                Surface(onClick = { widgetTab = widget }, color = if(widgetTab == widget) Renk.metin else Renk.yuzey, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f).fillMaxHeight().testTag(if(widget) "appearance-widget" else "appearance-theme")) {
                    Box(Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
                        Text(if(widget) "Widget" else cevir(dil,"Uygulama teması","App theme"), Modifier.padding(vertical = 13.dp, horizontal = 6.dp), color = if(widgetTab == widget) Renk.zemin else Renk.metinIkincil, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        if(!widgetTab) {
            val columns = if(androidx.compose.ui.platform.LocalDensity.current.fontScale > 1.4f) 1 else 2
            val rows = remember(columns) { AnaTemalar.all.chunked(columns) }
            LazyColumn(Modifier.weight(1f).clipToBounds().testTag("appearance-gallery"),contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 24.dp),verticalArrangement = Arrangement.spacedBy(18.dp)) {
                item(key = "featured") { TemaKoleksiyonKapagi(dil,selected == AnaTemalar.living.id) { living = true } }
                item(key = "gallery-heading") {
                    Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
                        Text(cevir(dil,"Tüm temalar","All themes"),Modifier.weight(1f),fontSize = 16.sp,fontWeight = FontWeight.SemiBold,color = Renk.metin)
                        Text(cevir(dil,"Dokun ve önizle","Tap to preview"),fontSize = 11.sp,color = Renk.metinIkincil)
                    }
                }
                items(rows,key = { it.first().id }) { row -> TemaGrid(dil,row,AnaTemalar.allowed(selected,pro).id,pro) { preview = it.id } }
            }
        } else {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement = Arrangement.spacedBy(18.dp)) {
                ProRozeti(metin = "WIDGET · PRO")
                Text(cevir(dil,"İyi bir söz, telefonunda.","A good thought, on your phone."), fontSize = 27.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold, color = Renk.metin)
                val theme = AnaTemalar.allowed(selected, pro)
                val sample = cevir(dil,"Küçük bir adım da ilerlemektir.","A small step is still a step forward.")
                val previewState = remember(theme,dil) { mutableStateOf<android.graphics.Bitmap?>(null) }
                val bmp by previewState
                LaunchedEffect(theme,dil) {
                    previewState.value = withContext(Dispatchers.Default) { com.yalnizfahrettin.azim.widget.WidgetTasarimi.render(ctx, com.yalnizfahrettin.azim.widget.WidgetSecimi(theme.id), sample, "Ascend", 1080, 540) }
                }
                Box(Modifier.fillMaxWidth().aspectRatio(2f).clip(RoundedCornerShape(22.dp)).background(Renk.yuzey)) {
                    bmp?.let { androidx.compose.foundation.Image(it.asImageBitmap(), sample, Modifier.fillMaxSize()) }
                }
                Text(cevir(dil,"Arka planını seç, telefonuna ekle. Söz her gün yenilenir; yazıyı biz yerleştiririz.","Choose a background and add it to your phone. The quote changes daily; we handle the layout."), color = Renk.metinIkincil, fontSize = 15.sp, lineHeight = 23.sp)
                Button(onClick = { ctx.startActivity(Intent(ctx, WidgetAyarActivity::class.java)) }, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("widget-editor-open"), shape = RoundedCornerShape(16.dp)) { Text(cevir(dil,"Widget oluştur","Create widget")) }
            }
        }
    }
    if(living) LivingCollection(dil,pro,close = { living = false },apply = select, suspendedMotion = collectionOffering, proOpen = {
        collectionOffering = true
        if(offerOpen != null) offerOpen(ProOffer(ProSource.COLLECTION,AnaTemalar.living.id)) else proOpen()
    })
    preview?.let { id -> TemaOnizleme(AnaTemalar.find(id), dil, pro, close = { preview = null; pendingApply = false }, apply = { select(id); preview = null }, proOpen = { pendingApply = true; if(offerOpen != null) offerOpen(ProOffer(ProSource.THEME,id)) else proOpen() }) }
}
