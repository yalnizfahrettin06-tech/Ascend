package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun RestartSeriesScreen(dil: String, pro: Boolean, progress: SeriesProgress?, favorites: Set<String>, back: () -> Unit,
    start: suspend () -> Unit, complete: suspend () -> Unit, save: (Soz) -> Unit, share: (Soz) -> Unit, proOpen: () -> Unit) {
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(Unit) { while(true) { today = LocalDate.now(); delay(60_000) } }
    val available = if(!pro || progress == null) 0 else if(progress.canComplete(today)) progress.completed.coerceAtMost(6) else (progress.completed - 1).coerceAtLeast(0)
    var reading by rememberSaveable { mutableStateOf<Int?>(null) }
    val day = (reading ?: available).coerceIn(0,available)
    val chapter = remember(dil,day) { RestartSeries.days(dil)[day] }
    val quote = ShortSeries.all.first { it.id == "restart" }.quotes[day]
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    fun write(action: suspend () -> Unit) { scope.launch {
        busy = true; failed = false
        try { action(); reading = null } catch(_: java.io.IOException) { failed = true } finally { busy = false }
    } }
    Column(Modifier.fillMaxSize().background(Renk.zemin).testTag("restart-series")) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp),verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back) { Icon(AzimIkon.Geri,cevir(dil,"Geri","Back"),tint = Renk.metin) }
            Text(RestartSeries.title(dil),Modifier.weight(1f),color = Renk.metin,fontSize = 18.sp,fontWeight = FontWeight.Medium)
            Text("${day + 1} / 7",Modifier.padding(end = 16.dp),color = Renk.metinIkincil,fontSize = 12.sp)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(Modifier.fillMaxWidth().height(188.dp).clip(RoundedCornerShape(22.dp))) {
                EditorialPhoto(EditorialArt.series("restart"),Modifier.matchParentSize())
                Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(alpha = .84f)))))
                Column(Modifier.align(Alignment.BottomStart).padding(20.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(CollectionCopy.text("exclusive",dil),color = Color.White.copy(alpha = .8f),fontSize = 10.sp)
                    Text(chapter.title,color = Color.White,fontSize = 25.sp,lineHeight = 31.sp,fontWeight = FontWeight.SemiBold)
                }
            }
            if(progress == null || !pro) {
                Text(CollectionCopy.text("read",dil),color = Renk.metinIkincil,fontSize = 12.sp)
                Text(CollectionCopy.text("promise",dil),color = Renk.metinIkincil,fontSize = 14.sp,lineHeight = 21.sp)
            }
            Text(chapter.story,color = Renk.metin,fontSize = 17.sp,lineHeight = 27.sp,modifier = Modifier.testTag("restart-story"))
            Surface(color = Renk.yuzey,shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(quote.metin(dil),color = Renk.metin,fontFamily = LoraSerif,fontSize = 23.sp,lineHeight = 32.sp)
                    QuoteActions(quote,dil,favorites,save,share)
                }
            }
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp),verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(CollectionCopy.text("step",dil),color = Renk.metinIkincil,fontSize = 12.sp,fontWeight = FontWeight.Medium)
                Text(chapter.step,color = Renk.metin,fontSize = 16.sp,lineHeight = 25.sp,modifier = Modifier.testTag("restart-step"))
            }
            if(pro && progress != null) Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for(index in 0..available) FilterChip(selected = index == day,onClick = { reading = index },label = { Text("${index + 1}") },modifier = Modifier.testTag("restart-day-$index"))
            }
            if(pro && progress?.completed == 7) Text(cevir(dil,"Seriyi tamamladın. Sözlere istediğin zaman dönebilirsin.","You completed the series. Revisit the quotes anytime."),color = Renk.metinIkincil)
            else if(pro && progress != null && !progress.canComplete(today)) Text(cevir(dil,"Bugünlük bu kadar. Sonraki söz yarın açılacak.","That is enough for today. The next quote opens tomorrow."),color = Renk.metinIkincil)
            Spacer(Modifier.height(8.dp))
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp,vertical = 12.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if(failed) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Please retry."),color = MaterialTheme.colorScheme.error)
            if(!pro || progress == null || (progress.canComplete(today) && day == progress.completed)) Button(
                onClick = { if(!pro) proOpen() else if(progress == null) write(start) else write(complete) },enabled = !busy,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("restart-action")) {
                Text(if(!pro) CollectionCopy.text("start",dil) else if(progress == null) cevir(dil,"Seriye başla","Start series") else cevir(dil,"Bugünü tamamla","Complete today"))
            }
        }
    }
}
