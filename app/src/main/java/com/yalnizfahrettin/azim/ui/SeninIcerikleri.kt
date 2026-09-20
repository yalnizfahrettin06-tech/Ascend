package com.yalnizfahrettin.azim.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.semantics.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun PersonalHeader(title: String, dil: String, back: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = back) { Icon(AzimIkon.Geri, cevir(dil,"Geri","Back"), tint = Renk.metin) }
        Text(title, color = Renk.metin, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun QuoteActions(quote: Soz, dil: String, favorites: Set<String>, save: (Soz) -> Unit, share: (Soz) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        IconButton(onClick = { save(quote) }, modifier = Modifier.testTag("personal-save-${quote.kimlik}")) {
            Icon(if(quote.kimlik in favorites) AzimIkon.KalpDolu else AzimIkon.Kalp, cevir(dil,if(quote.kimlik in favorites) "Kaydedildi" else "Kaydet",if(quote.kimlik in favorites) "Saved" else "Save"), Modifier.size(20.dp), tint = Renk.metinIkincil)
        }
        IconButton(onClick = { share(quote) }, modifier = Modifier.testTag("personal-share-${quote.kimlik}")) {
            Icon(AzimIkon.Paylas,cevir(dil,"Paylaş","Share"),Modifier.size(20.dp),tint = Renk.metinIkincil)
        }
    }
}

@Composable
fun BildirimGecmisiEkrani(dil: String, history: Map<String,List<String>>, favorites: Set<String>, back: () -> Unit, save: (Soz) -> Unit, share: (Soz) -> Unit, embedded: Boolean = false, reminders: (() -> Unit)? = null) {
    if(!embedded) BackHandler(onBack = back)
    val cutoff = LocalDate.now().minusDays(29)
    val groups = history.toSortedMap(reverseOrder()).mapNotNull { (date, ids) ->
        val day = runCatching { LocalDate.parse(date) }.getOrNull()
        val quotes = ids.distinct().mapNotNull(Sozler::kimlikten)
        if(day == null || day < cutoff || quotes.isEmpty()) null else day to quotes
    }
    Column(Modifier.fillMaxSize().background(Renk.zemin).then(if(embedded) Modifier else Modifier.statusBarsPadding()).testTag("notification-history")) {
        if(!embedded) PersonalHeader(cevir(dil,"Bildirim geçmişi","Notification history"),dil,back)
        LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text(JourneyCopy.text("historyNote",dil),color = Renk.metinIkincil,fontSize = 14.sp) }
            if(groups.isEmpty()) item {
                EditorialPhoto(EditorialArt.group("zihin"),Modifier.fillMaxWidth().height(144.dp).clip(RoundedCornerShape(20.dp)))
                Text(cevir(dil,"Henüz bir bildirim yok. İlk sözün geldiğinde burada bulabilirsin.","No notifications yet. Your first quote will appear here after it is sent."),Modifier.padding(vertical = 32.dp),color = Renk.metinIkincil)
                reminders?.let { TextButton(onClick = it) { Text(cevir(dil,"Bildirimlerim","My reminders")) } }
            }
            groups.forEach { (day, quotes) ->
                item(key = day.toString()) { Text(day.format(DateTimeFormatter.ofPattern("d MMMM yyyy",Locale.forLanguageTag(dil))),Modifier.padding(top = 14.dp),color = Renk.metinIkincil,fontSize = 12.sp) }
                items(quotes,key = { "$day-${it.kimlik}" }) { quote ->
                    Surface(color = Renk.yuzey.copy(alpha = .45f),shape = RoundedCornerShape(24.dp),modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(quote.metin(dil),color = Renk.metin,fontFamily = LoraSerif,fontSize = 21.sp,lineHeight = 29.sp)
                            Text(quote.sunumEtiketi(dil),color = Renk.metinIkincil,fontSize = 11.sp)
                            QuoteActions(quote,dil,favorites,save,share)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KisaSerilerEkrani(dil: String, progress: Map<String,SeriesProgress>, favorites: Set<String>, back: () -> Unit,
    start: suspend (String) -> Unit, complete: suspend (String) -> Unit, save: (Soz) -> Unit, share: (Soz) -> Unit, embedded: Boolean = false, insets: Boolean = true, pro: Boolean = false, proOpen: () -> Unit = {}, firstComplete: suspend () -> Unit = { start("restart"); complete("restart") }, firstCompleteFor: (suspend (String) -> Unit)? = null, offerFor: ((String) -> Unit)? = null) {
    var chosen by rememberSaveable { mutableStateOf(ShortSeries.active(progress)?.id) }
    val context = LocalContext.current
    var readingDay by rememberSaveable(chosen) { mutableStateOf<Int?>(null) }
    val today = rememberCurrentDay()
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val series = ShortSeries.all.firstOrNull { it.id == chosen }
    val goBack = { if(chosen != null) chosen = null else back() }
    if(!embedded || chosen != null) BackHandler(onBack = goBack)
    if(series?.pro == true) {
        key(series.id) { RestartSeriesScreen(dil,pro,progress[series.id],favorites,goBack,{ start(series.id) },{ complete(series.id) },save,share,
            { if(offerFor != null) offerFor(series.id) else proOpen() },
            { if(firstCompleteFor != null) firstCompleteFor(series.id) else if(series.id == "restart") firstComplete() else { start(series.id); complete(series.id) } },seriesId = series.id) }
        return
    }
    Column(Modifier.fillMaxSize().background(Renk.zemin).then(if(embedded || !insets) Modifier else Modifier.statusBarsPadding()).testTag("short-series")) {
        if(!embedded || chosen != null) PersonalHeader(cevir(dil,"Kısa seriler","Short series"),dil,goBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if(series == null) {
                ShortSeries.all.sortedByDescending { it.id in progress && (progress[it.id]?.completed ?: 7) < 7 }.forEach { item ->
                    val state = progress[item.id]
                    val count = state?.completed ?: 0
                    Surface(onClick = { chosen = item.id },color = Renk.yuzey,shape = RoundedCornerShape(20.dp),modifier = Modifier.fillMaxWidth().testTag("series-${item.id}")) {
                        Column {
                            EditorialPhoto(EditorialArt.series(item.id),Modifier.fillMaxWidth().height(132.dp))
                            Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if(item.pro) Text(CollectionCopy.text("exclusive",dil),color = Renk.metinIkincil,fontSize = 10.sp)
                                    Text(item.title(dil),color = Renk.metin,fontSize = 18.sp,lineHeight = 24.sp,fontWeight = FontWeight.Medium)
                                    Text(if(count == 7) cevir(dil,"Tamamlandı · Yeniden oku","Completed · Read again") else if(state != null) "$count / 7" else cevir(dil,"7 günlük seri","7-day series"),color = Renk.metinIkincil,fontSize = 12.sp)
                                }
                                Icon(AzimIkon.Ileri,null,Modifier.size(20.dp),tint = Renk.metinIkincil)
                            }
                            if(state != null) LinearProgressIndicator(progress = { count / 7f },modifier = Modifier.fillMaxWidth().testTag("series-progress-${item.id}"),color = Renk.metin,trackColor = Renk.kenarlik)
                        }
                    }
                }
            } else {
                EditorialPhoto(EditorialArt.series(series.id),Modifier.fillMaxWidth().height(142.dp).clip(RoundedCornerShape(20.dp)))
                Text(series.title(dil),color = Renk.metin,fontSize = 23.sp,lineHeight = 30.sp,fontWeight = FontWeight.Medium)
                val state = progress[series.id]
                if(state == null) {
                    Text(series.summary(dil),color = Renk.metinIkincil,fontSize = 15.sp,lineHeight = 23.sp)
                    Text(cevir(dil,"Her gün bir söz ve istersen üzerinde düşüneceğin kısa bir soru. Yanıt yazman gerekmez; ek bildirim gönderilmez.","A quote each day and a short optional reflection. No answers to write and no extra notifications."),color = Renk.metinIkincil,fontSize = 15.sp,lineHeight = 23.sp)
                    Button(onClick = { scope.launch { busy = true; error = false; try { start(series.id); ProductSignals.recordSeries(context,ProductSignals.Event.SERIES_STARTED,series.id,1) } catch(_: java.io.IOException) { error = true } finally { busy = false } } },enabled = !busy,modifier = Modifier.fillMaxWidth().testTag("series-start")) { Text(cevir(dil,"Seriye başla","Start series")) }
                } else {
                    val available = if(state.canComplete(today)) state.completed.coerceAtMost(6) else (state.completed - 1).coerceAtLeast(0)
                    val index = (readingDay ?: available).coerceIn(0,available)
                    val quote = series.quotes[index]
                    Text(cevir(dil,"${index + 1}. gün","Day ${index + 1}"),color = Renk.metinIkincil,fontSize = 13.sp)
                    Surface(color = Renk.yuzey,shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(20.dp),verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(quote.metin(dil),fontFamily = LoraSerif,fontSize = 26.sp,lineHeight = 35.sp,color = Renk.metin)
                            QuoteActions(quote,dil,favorites,save,share)
                        }
                    }
                    Text(cevir(dil,"Düşünmek istersen","If you want to reflect"),fontSize = 12.sp,color = Renk.metinIkincil)
                    Text(series.prompt(index,dil),fontSize = 17.sp,lineHeight = 25.sp,color = Renk.metin)
                    Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for(day in 0..available) FilterChip(selected = day == index,onClick = { readingDay = day },label = { Text("${day + 1}") })
                    }
                    if(state.completed == 7) Text(cevir(dil,"Seriyi tamamladın. Sözlere istediğin zaman dönebilirsin.","You completed the series. Revisit the quotes anytime."),color = Renk.metinIkincil)
                    else if(!state.canComplete(today)) Text(cevir(dil,"Bugünlük bu kadar. Sonraki söz yarın açılacak.","That is enough for today. The next quote opens tomorrow."),color = Renk.metinIkincil)
                    else if(index == state.completed) Button(onClick = { scope.launch { busy = true; error = false; try { complete(series.id); ProductSignals.recordSeries(context,ProductSignals.Event.SERIES_DAY_COMPLETED,series.id,state.completed + 1); readingDay = null } catch(_: java.io.IOException) { error = true } finally { busy = false } } },enabled = !busy,modifier = Modifier.fillMaxWidth().testTag("series-complete-day")) { Text(cevir(dil,"Bugünü tamamla","Complete today")) }
                }
            }
            if(error) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Please retry."),color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SeninBolumleri(dil: String, selected: String, select: (String) -> Unit, settings: () -> Unit, content: @Composable () -> Unit) {
    val holder = androidx.compose.runtime.saveable.rememberSaveableStateHolder()
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp),verticalAlignment = Alignment.CenterVertically) {
            Text(cevir(dil,"Senin","You"),Modifier.weight(1f),fontSize = 18.sp,fontWeight = FontWeight.SemiBold,color = Renk.metin)
            IconButton(onClick = settings,modifier = Modifier.testTag("profile-settings")) { Icon(AzimIkon.Ayarlar,cevir(dil,"Ayarlar","Settings"),Modifier.size(20.dp),tint = Renk.metinIkincil) }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp),horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("" to cevir(dil,"Kaydedilenler","Saved quotes"), "history" to cevir(dil,"Geçmiş","History")).forEach { (key,label) ->
                Surface(onClick = { select(key) },color = if(selected == key) Renk.metin else Renk.yuzey,shape = RoundedCornerShape(12.dp),modifier = Modifier.weight(1f).testTag("personal-tab-${key.ifEmpty { "overview" }}").semantics { role = Role.Tab; this.selected = selected == key }) {
                    Text(label,Modifier.padding(vertical = 12.dp),color = if(selected == key) Renk.zemin else Renk.metinIkincil,fontSize = 12.sp,textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
        Box(Modifier.weight(1f)) { holder.SaveableStateProvider(selected) { content() } }
    }
}
