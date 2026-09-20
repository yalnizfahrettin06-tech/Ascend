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
    start: suspend () -> Unit, complete: suspend () -> Unit, save: (Soz) -> Unit, share: (Soz) -> Unit, proOpen: () -> Unit, firstComplete: suspend () -> Unit = { start(); complete() }) {
    val today = rememberCurrentDay()
    val context = androidx.compose.ui.platform.LocalContext.current
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
            Box(Modifier.fillMaxWidth().height(144.dp).clip(RoundedCornerShape(22.dp))) {
                EditorialPhoto(EditorialArt.series("restart"),Modifier.matchParentSize())
                Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(alpha = .84f)))))
                Column(Modifier.align(Alignment.BottomStart).padding(20.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(CollectionCopy.text("exclusive",dil),color = Color.White.copy(alpha = .8f),fontSize = 10.sp)
                    Text(chapter.title,color = Color.White,fontSize = 22.sp,lineHeight = 28.sp,fontWeight = FontWeight.SemiBold)
                }
            }
            if(!pro && progress != null) Text(PhaseCopy.text("preserved",dil),color = Renk.metinIkincil,fontSize = 13.sp)
            Surface(color = Renk.yuzey,shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(CollectionCopy.text("step",dil),color = Renk.metinIkincil,fontSize = 12.sp)
                    Text(chapter.step,color = Renk.metin,fontSize = 16.sp,lineHeight = 24.sp,modifier = Modifier.testTag("restart-step"))
                }
            }
            Text(chapter.story,color = Renk.metin,fontSize = 17.sp,lineHeight = 27.sp,modifier = Modifier.testTag("restart-story"))
            Surface(color = Renk.yuzey,shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(quote.metin(dil),color = Renk.metin,fontFamily = LoraSerif,fontSize = 23.sp,lineHeight = 32.sp)
                    QuoteActions(quote,dil,favorites,save,share)
                }
            }
            var pathOpen by rememberSaveable { mutableStateOf(false) }
            TextButton(onClick = { pathOpen = !pathOpen },modifier = Modifier.testTag("restart-path")) {
                Text(PhaseCopy.text("roadmap",dil))
            }
            if(pathOpen) RestartSeries.days(dil).forEachIndexed { index, item ->
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp),verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1}",Modifier.width(30.dp),color = Renk.metinIkincil)
                    Text(item.title,Modifier.weight(1f),color = Renk.metin,fontSize = 14.sp)
                    if(index <= available) TextButton(onClick = { reading = index },modifier = Modifier.testTag("restart-day-$index")) {
                        Text(if(index == day) cevir(dil,"Seçili","Selected") else cevir(dil,"Oku","Read"))
                    } else Icon(AzimIkon.Kilit,PhaseCopy.text("future",dil),Modifier.size(18.dp),tint = Renk.metinIkincil)
                }
            }
            if(pro && progress?.completed == 7) Text(cevir(dil,"Seriyi tamamladın. Sözlere istediğin zaman dönebilirsin.","You completed the series. Revisit the quotes anytime."),color = Renk.metinIkincil)
            else if(pro && progress != null && !progress.canComplete(today)) Text(cevir(dil,"Bugünlük bu kadar. Sonraki söz yarın açılacak.","That is enough for today. The next quote opens tomorrow."),color = Renk.metinIkincil)
            Spacer(Modifier.height(8.dp))
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp,vertical = 12.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if(failed) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Please retry."),color = MaterialTheme.colorScheme.error)
            if(!pro || progress == null || (progress.canComplete(today) && day == progress.completed)) Button(
                onClick = { if(!pro) proOpen() else if(progress == null) write {
                    firstComplete()
                    ProductSignals.recordSeries(context,ProductSignals.Event.SERIES_STARTED,"restart",1)
                    ProductSignals.recordSeries(context,ProductSignals.Event.SERIES_DAY_COMPLETED,"restart",1)
                } else write { complete(); ProductSignals.recordSeries(context,ProductSignals.Event.SERIES_DAY_COMPLETED,"restart",progress.completed + 1) } },enabled = !busy,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("restart-action")) {
                Text(if(!pro) CollectionCopy.text("start",dil) else if(progress == null) PhaseCopy.text("first",dil) else cevir(dil,"Bugünü tamamla","Complete today"))
            }
        }
    }
}
