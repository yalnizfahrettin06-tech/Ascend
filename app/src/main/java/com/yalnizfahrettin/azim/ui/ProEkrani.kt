package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProEkrani(
    dil: String, acik: Boolean, kaydediliyor: Boolean = false, hata: String? = null,
    kapat: () -> Unit, degistir: (Boolean) -> Unit,
    offer: ProOffer = ProOffer(), widgetPreview: android.graphics.Bitmap? = null,
) {
    SetupTheme {
    val ctx = LocalContext.current
    var impression by rememberSaveable(offer.encode()) { mutableStateOf(false) }
    LaunchedEffect(offer.encode()) { if (!impression) { ProductSignals.record(ctx, ProductSignals.Event.OFFER_VIEWED, offer.source); impression = true } }
    val close = { if (!kaydediliyor) { ProductSignals.record(ctx, ProductSignals.Event.OFFER_CLOSED, offer.source); kapat() } }
    ModalBottomSheet(onDismissRequest = close, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Renk.zemin, dragHandle = null) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f).testTag("pro-sheet")) {
            Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                ProRozeti(metin = "PRO DEMO")
                Spacer(Modifier.weight(1f))
                IconButton(onClick = close, enabled = !kaydediliyor, modifier = Modifier.testTag("pro-close")) { Icon(AzimIkon.Kapat, cevir(dil,"Kapat","Close"), tint = Renk.metin) }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text(proOfferTitle(offer,dil), color = Renk.metin, fontFamily = ArayuzFont, fontSize = 26.sp, lineHeight = 33.sp,
                    modifier = Modifier.testTag("pro-context-title").semantics { heading() })
                ProGorselOrnek(dil,offer,widgetPreview)
                ProOutcome(dil,offer)
                ProBenefits(dil,offer)

            }
            HorizontalDivider(color = Renk.kenarlik)
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) {
                hata?.let { Text(it,color = MaterialTheme.colorScheme.error,modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }) }
                Text(cevir(dil,"Ödeme alınmaz. Abonelik başlatılmaz.","No payment is taken. No subscription starts."),color = Renk.metinIkincil,style = MaterialTheme.typography.bodySmall)
                Button(onClick = { degistir(!acik) }, enabled = !kaydediliyor,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag(if(acik) "pro-demo-disable" else "pro-demo-enable"), shape = RoundedCornerShape(18.dp)) {
                    if(kaydediliyor) CircularProgressIndicator(Modifier.size(20.dp),strokeWidth = 2.dp)
                    else Text(if(acik) cevir(dil,"Pro demosunu kapat","Turn off Pro demo") else cevir(dil,"Demoyu aç ve devam et","Enable demo and continue"))
                }
                if(!acik) TextButton(onClick = close,enabled = !kaydediliyor,modifier = Modifier.align(Alignment.CenterHorizontally).testTag("pro-decline")) { Text(SetupCopy.text("cancel",dil)) }
            }
        }
    }
    }
}

fun proOfferTitle(offer: ProOffer, dil: String): String = when(offer.source) {
    ProSource.WALLPAPER -> WallpaperCopy.text("intro",dil)
    ProSource.COLLECTION -> CollectionCopy.text("title",dil)
    ProSource.SERIES -> RestartSeries.title(dil)
    ProSource.THEME, ProSource.ONBOARDING -> if(offer.selection.isNotBlank()) AnaTemalar.find(offer.selection).label(dil) else PhaseCopy.text("demo",dil)
    ProSource.TOPIC -> Kategoriler.bul(offer.selection)?.ad(dil) ?: cevir(dil,"Tüm konular ve sözler","Every topic and quote")
    ProSource.WIDGET -> cevir(dil,"Hazırladığın widget, telefonunda.","Your widget, on your home screen.")
    ProSource.VIDEO -> cevir(dil,"Bu sözü videoya dönüştür.","Turn this quote into a video.")
    ProSource.SHARE, ProSource.PHOTO -> cevir(dil,"Sözünü seçtiğin görünümle paylaş.","Share your quote with your chosen look.")
    else -> cevir(dil,"Sevdiğin sözleri gününe taşı.","Bring the words you love into your day.")
}

@Composable
fun ProRozeti(modifier: Modifier = Modifier, metin: String = "PRO") {
    Text(
        metin, modifier.clip(RoundedCornerShape(6.dp)).background(Renk.yuzey)
            .border(1.dp, Renk.accent.copy(alpha = .35f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 5.dp),
        color = Renk.accent, fontSize = 10.sp, lineHeight = 12.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun ProOzelligi(ikon: ImageVector, baslik: String, aciklama: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Icon(ikon, null, Modifier.size(23.dp), tint = Renk.metin)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(baslik, fontFamily = ArayuzFont, fontSize = 16.sp, lineHeight = 22.sp, color = Renk.metin)
            Text(aciklama, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
        }
    }
}

/** Concrete, read-only examples; no additional editor or purchase step. */
@Composable
fun ProGorselOrnek(dil: String, offer: ProOffer = ProOffer(), widgetPreview: android.graphics.Bitmap? = null) {
    val quote = Sozler.kimlikten(offer.quoteId) ?: if(offer.source == ProSource.TOPIC) Sozler.kategoriden(offer.selection).firstOrNull() else null
    val sample = quote?.metin(dil) ?: cevir(dil,"Küçük bir adım da ilerlemektir.","A small step is still a step forward.")
    val theme = if(offer.selection.isNotBlank() && offer.source != ProSource.TOPIC) AnaTemalar.find(offer.selection) else AnaTemalar.emperor
    if(offer.source == ProSource.WALLPAPER) {
        Box(Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(20.dp))) { TemaZemini(theme,Modifier.matchParentSize(),thumbnail = true,previewSize = 1536,dil = dil) }
    } else if(offer.source == ProSource.SERIES) {
        Surface(color = Renk.yuzey,shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(20.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(RestartSeries.days(dil)[1].title,color = Renk.metin,fontSize = 18.sp,fontWeight = FontWeight.SemiBold)
                Text(RestartSeries.days(dil)[1].step,color = Renk.metinIkincil,fontSize = 15.sp,lineHeight = 23.sp)
            }
        }
    } else if(offer.source == ProSource.TOPIC) {
        Surface(color = Renk.yuzey,shape = RoundedCornerShape(20.dp),modifier = Modifier.testTag("pro-visual-preview")) {
            Column(Modifier.padding(20.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(sample,color = Renk.metin,fontFamily = LoraSerif,fontSize = 21.sp,lineHeight = 29.sp)
                Text(quote?.sunumEtiketi(dil).orEmpty(),color = Renk.metinIkincil,fontSize = 11.sp)
            }
        }
    } else if(widgetPreview != null) {
        Image(widgetPreview.asImageBitmap(),cevir(dil,"Önizleme","Preview"),Modifier.fillMaxWidth().heightIn(max = 250.dp).clip(RoundedCornerShape(20.dp)).testTag("pro-widget-preview"))
    } else {
        Box(Modifier.fillMaxWidth().heightIn(min = 210.dp).clip(RoundedCornerShape(20.dp)).testTag("pro-visual-preview")) {
            TemaZemini(theme,Modifier.matchParentSize(),thumbnail = false)
            Text(sample,Modifier.align(Alignment.Center).padding(24.dp), color = if(theme.dark) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color(0xFF191919),
                fontFamily = LoraSerif,fontSize = 22.sp,lineHeight = 30.sp,textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ProBenefits(dil: String, offer: ProOffer) {
    var expanded by rememberSaveable(offer.encode()) { mutableStateOf(false) }
    val art = cevir(dil,"Tüm temalar","All themes")
    val widgets = cevir(dil,"Telefonuna özel widget’lar","Widgets for your phone")
    val wallpaper = WallpaperCopy.text("title",dil)
    val topics = cevir(dil,"Tüm konular ve sözler","Every topic and quote")
    val series = CollectionCopy.text("benefit",dil)
    val share = cevir(dil,"Tüm arka planlar ve video","Every background and video")
    val primary = when(offer.source) {
        ProSource.WIDGET -> listOf(widgets, wallpaper, art)
        ProSource.WALLPAPER -> listOf(wallpaper, art, widgets)
        ProSource.SERIES -> listOf(series, topics, art)
        ProSource.TOPIC -> listOf(topics, series, art)
        ProSource.SHARE, ProSource.VIDEO, ProSource.PHOTO -> listOf(share, art, widgets)
        else -> listOf(art, widgets, wallpaper)
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.testTag("pro-benefits")) {
        primary.forEach { label ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Tik,null,Modifier.size(18.dp),tint = Renk.metin)
                Text(label,color = Renk.metin,fontSize = 14.sp,lineHeight = 20.sp)
            }
        }
        TextButton(onClick = { expanded = !expanded },modifier = Modifier.heightIn(min = 48.dp).testTag("pro-more")) {
            Text(SetupCopy.text(if(expanded) "less" else "more",dil))
        }
        if(expanded) {
            (listOf(art,widgets,wallpaper,topics,series,share) - primary.toSet()).forEach {
                Text(it,color = Renk.metinIkincil,fontSize = 14.sp,lineHeight = 20.sp)
            }
            Text(SetupCopy.text("freeRights",dil),color = Renk.metinIkincil,fontSize = 13.sp,lineHeight = 19.sp)
        }
    }
}

@Composable
fun ProOutcome(dil: String, offer: ProOffer) {
    val key = when(offer.source) {
        ProSource.WALLPAPER -> "wallResult"
        ProSource.WIDGET -> "widgetResult"
        ProSource.SERIES -> "seriesResult"
        ProSource.SHARE, ProSource.VIDEO, ProSource.PHOTO -> "shareResult"
        ProSource.THEME, ProSource.ONBOARDING -> "artResult"
        else -> null
    }
    if(key != null) Text(SetupCopy.text(key,dil),color = Renk.metinIkincil,fontSize = 14.sp,lineHeight = 21.sp)
}
