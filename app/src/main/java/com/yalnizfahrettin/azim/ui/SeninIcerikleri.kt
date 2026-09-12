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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
        Text(title, color = Renk.metin, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuoteActions(quote: Soz, dil: String, favorites: Set<String>, save: (Soz) -> Unit, share: (Soz) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = { save(quote) }, modifier = Modifier.testTag("personal-save-${quote.kimlik}")) {
            Icon(if(quote.kimlik in favorites) AzimIkon.KalpDolu else AzimIkon.Kalp, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp)); Text(cevir(dil,if(quote.kimlik in favorites) "Kaydedildi" else "Kaydet",if(quote.kimlik in favorites) "Saved" else "Save"))
        }
        TextButton(onClick = { share(quote) }, modifier = Modifier.testTag("personal-share-${quote.kimlik}")) {
            Icon(AzimIkon.Paylas,null,Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text(cevir(dil,"Paylaş","Share"))
        }
    }
}

@Composable
fun BildirimGecmisiEkrani(dil: String, history: Map<String,List<String>>, favorites: Set<String>, back: () -> Unit, save: (Soz) -> Unit, share: (Soz) -> Unit) {
    BackHandler(onBack = back)
    val cutoff = LocalDate.now().minusDays(29)
    val groups = history.toSortedMap(reverseOrder()).mapNotNull { (date, ids) ->
        val day = runCatching { LocalDate.parse(date) }.getOrNull()
        val quotes = ids.distinct().mapNotNull(Sozler::kimlikten)
        if(day == null || day < cutoff || quotes.isEmpty()) null else day to quotes
    }
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("notification-history")) {
        PersonalHeader(cevir(dil,"Bildirim geçmişi","Notification history"),dil,back)
        LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text(cevir(dil,"Son 30 günde sana gelen sözler.","Quotes sent to you in the last 30 days."),color = Renk.metinIkincil,fontSize = 14.sp) }
            if(groups.isEmpty()) item {
                Text(cevir(dil,"Henüz bir bildirim yok. İlk sözün geldiğinde burada bulabilirsin.","No notifications yet. Your first quote will appear here after it is sent."),Modifier.padding(vertical = 32.dp),color = Renk.metinIkincil)
            }
            groups.forEach { (day, quotes) ->
                item(key = day.toString()) { Text(day.format(DateTimeFormatter.ofPattern("d MMMM yyyy",Locale.forLanguageTag(dil))),Modifier.padding(top = 14.dp),color = Renk.metinIkincil,fontSize = 12.sp) }
                items(quotes,key = { "$day-${it.kimlik}" }) { quote ->
                    Surface(color = Renk.yuzey,shape = RoundedCornerShape(18.dp),modifier = Modifier.fillMaxWidth()) {
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
    start: suspend (String) -> Unit, complete: suspend (String) -> Unit, save: (Soz) -> Unit, share: (Soz) -> Unit) {
    var chosen by rememberSaveable { mutableStateOf<String?>(null) }
    var readingDay by rememberSaveable(chosen) { mutableStateOf<Int?>(null) }
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(Unit) { while(true) { today = LocalDate.now(); delay(60_000) } }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val series = ShortSeries.all.firstOrNull { it.id == chosen }
    val goBack = { if(chosen != null) chosen = null else back() }
    BackHandler(onBack = goBack)
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().testTag("short-series")) {
        PersonalHeader(cevir(dil,"Kısa seriler","Short series"),dil,goBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if(series == null) {
                Text(cevir(dil,"7 gün, her gün bir söz. Kendi isteğinle başla; ara vermek ilerlemeni silmez.","7 days, one quote each day. Start when you want; taking a break keeps your progress."),color = Renk.metinIkincil,fontSize = 15.sp,lineHeight = 23.sp)
                ShortSeries.all.forEach { item ->
                    Surface(onClick = { chosen = item.id },color = Renk.yuzey,shape = RoundedCornerShape(20.dp),modifier = Modifier.fillMaxWidth().testTag("series-${item.id}")) {
                        Column(Modifier.padding(20.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(item.title(dil),color = Renk.metin,fontSize = 21.sp,fontWeight = FontWeight.Medium)
                            val count = progress[item.id]?.completed ?: 0
                            Text(if(count == 7) cevir(dil,"Tamamlandı · Yeniden oku","Completed · Read again") else if(item.id in progress) cevir(dil,"$count / 7 gün tamamlandı","$count / 7 days completed") else cevir(dil,"7 günlük seri","7-day series"),color = Renk.metinIkincil,fontSize = 13.sp)
                            LinearProgressIndicator(progress = { count / 7f },modifier = Modifier.fillMaxWidth(),color = Renk.metin,trackColor = Renk.kenarlik)
                        }
                    }
                }
            } else {
                Text(series.title(dil),color = Renk.metin,fontSize = 27.sp,lineHeight = 34.sp,fontWeight = FontWeight.SemiBold)
                val state = progress[series.id]
                if(state == null) {
                    Text(cevir(dil,"Her gün bir söz ve istersen üzerinde düşüneceğin kısa bir soru. Yanıt yazman gerekmez; ek bildirim gönderilmez.","A quote each day and a short optional reflection. No answers to write and no extra notifications."),color = Renk.metinIkincil,fontSize = 15.sp,lineHeight = 23.sp)
                    Button(onClick = { scope.launch { busy = true; error = false; try { start(series.id) } catch(_: java.io.IOException) { error = true } finally { busy = false } } },enabled = !busy,modifier = Modifier.fillMaxWidth().testTag("series-start")) { Text(cevir(dil,"Seriye başla","Start series")) }
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
                    else if(index == state.completed) Button(onClick = { scope.launch { busy = true; error = false; try { complete(series.id); readingDay = null } catch(_: java.io.IOException) { error = true } finally { busy = false } } },enabled = !busy,modifier = Modifier.fillMaxWidth().testTag("series-complete-day")) { Text(cevir(dil,"Bugünü tamamla","Complete today")) }
                }
            }
            if(error) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Please retry."),color = MaterialTheme.colorScheme.error)
        }
    }
}
