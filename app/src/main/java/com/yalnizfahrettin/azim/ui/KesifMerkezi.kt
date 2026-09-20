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
import androidx.compose.ui.semantics.*
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
        if(showHeading) Text(cevir(dil,"Keşfet","Explore"), Modifier.padding(horizontal = 24.dp, vertical = 9.dp), color = Renk.metin, fontSize = UiRoles.sectionTitle, fontWeight = FontWeight.SemiBold)
        Box(Modifier.weight(1f)) { topics() }
    }
}

@Composable
fun GorunumEkrani(dil: String, selected: String?, pro: Boolean, proOpen: () -> Unit, select: (String) -> Unit, offerOpen: ((ProOffer) -> Unit)? = null, offerDismissals: Int = 0, quote: Soz? = null) {
    val appearanceState = androidx.compose.runtime.saveable.rememberSaveableStateHolder()
    var living by rememberSaveable { mutableStateOf(false) }
    var collectionOffering by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(pro,offerDismissals) { collectionOffering = false }
    var appearanceTab by rememberSaveable { mutableIntStateOf(0) }
    var collectionWallpaper by rememberSaveable { mutableStateOf<String?>(null) }
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingApply by rememberSaveable { mutableStateOf(false) }
    var lastDismissal by rememberSaveable { mutableIntStateOf(offerDismissals) }
    LaunchedEffect(offerDismissals) { if(lastDismissal != offerDismissals) { pendingApply = false; lastDismissal = offerDismissals } }
    LaunchedEffect(pro) { if(pro && pendingApply && preview != null) { select(preview!!); preview = null; pendingApply = false } }
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding()) {
        Text(cevir(dil,"Görünüm","Appearance"), Modifier.padding(horizontal = 24.dp, vertical = 9.dp), color = Renk.metin, fontSize = UiRoles.sectionTitle, fontWeight = FontWeight.SemiBold)
        ChoiceTabs(listOf(cevir(dil,"Uygulama teması","App theme"), "Widget", WallpaperCopy.text("title",dil)),
            appearanceTab, { appearanceTab = it },listOf("appearance-theme","appearance-widget","appearance-wallpaper"))
        Spacer(Modifier.height(10.dp))
        appearanceState.SaveableStateProvider(appearanceTab) {
        if(appearanceTab == 0) {
            val columns = if(androidx.compose.ui.platform.LocalDensity.current.fontScale > 1.4f || androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp < 360) 1 else 2
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
        } else if(appearanceTab == 2) {
            Box(Modifier.weight(1f)) { WallpaperGallery(dil,pro) { if(offerOpen != null) offerOpen(it) else proOpen() } }
        } else {
            Box(Modifier.weight(1f)) { com.yalnizfahrettin.azim.widget.WidgetSetup(initialTheme = selected,embedded = true,language = dil) }

        }
        }
    }
    if(living) LivingCollection(dil,pro,close = { living = false },apply = select, suspendedMotion = collectionOffering, offerDismissals = offerDismissals, proOpen = {
        collectionOffering = true
        if(offerOpen != null) offerOpen(ProOffer(ProSource.COLLECTION,AnaTemalar.living.id)) else proOpen()
    })
    preview?.let { id -> TemaOnizleme(AnaTemalar.find(id), dil, pro, close = { preview = null; pendingApply = false }, apply = { select(id); preview = null }, proOpen = { pendingApply = true; if(offerOpen != null) offerOpen(ProOffer(ProSource.THEME,id)) else proOpen() }, quote = quote, widget = { ctx.startActivity(Intent(ctx,WidgetAyarActivity::class.java).putExtra("collection_theme",id)) }, wallpaper = if(AnaTemalar.find(id).art != null) {{ collectionWallpaper = id }} else null) }
    collectionWallpaper?.let { id -> WallpaperPreview(AnaTemalar.find(id),dil,pro,{ collectionWallpaper = null },{ if(offerOpen != null) offerOpen(ProOffer(ProSource.WALLPAPER,id)) else proOpen() }) }

}
