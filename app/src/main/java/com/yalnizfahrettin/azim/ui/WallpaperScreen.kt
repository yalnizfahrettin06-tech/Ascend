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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@Composable
fun WallpaperGallery(dil: String, pro: Boolean, openOffer: (ProOffer) -> Unit) {
    var preview by rememberSaveable { mutableStateOf<String?>(null) }
    val columns = if(LocalDensity.current.fontScale > 1.4f || LocalConfiguration.current.screenWidthDp < 360) 1 else 2
    val rows = remember(columns) { WallpaperService.gallery.chunked(columns) }
    LazyColumn(Modifier.fillMaxSize().testTag("wallpaper-gallery"),contentPadding = PaddingValues(20.dp),verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(WallpaperCopy.text("intro",dil),color = Renk.metin,fontSize = 21.sp,lineHeight = 27.sp,fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(WallpaperCopy.text("detail",dil),color = Renk.metinIkincil,fontSize = 13.sp,lineHeight = 19.sp)
        }
        items(rows,key = { it.first().id }) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { theme ->
                    Column(Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(onClick = { preview = theme.id },shape = RoundedCornerShape(20.dp),modifier = Modifier.fillMaxWidth().aspectRatio(.72f).testTag("wallpaper-${theme.id}")) {
                            Box {
                                TemaZemini(theme,Modifier.matchParentSize(),thumbnail = true,dil = dil)
                                if(theme.pro) ProRozeti(Modifier.align(Alignment.TopEnd).padding(12.dp))
                            }
                        }
                        Text(theme.label(dil),color = Renk.metin,fontSize = 14.sp)
                    }
                }
                if(row.size < columns) Spacer(Modifier.weight(1f))
            }
        }
    }
    preview?.let { id -> WallpaperPreview(AnaTemalar.find(id),dil,pro,{ preview = null },{ openOffer(ProOffer(ProSource.WALLPAPER,id)) }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperPreview(theme: AnaTema, dil: String, pro: Boolean, close: () -> Unit, proOpen: () -> Unit,
    applyWallpaper: (suspend (AnaTema, Int, Float) -> Boolean)? = null) {
    val context = LocalContext.current
    val metrics = context.resources.displayMetrics
    val aspect = metrics.widthPixels.toFloat() / metrics.heightPixels.coerceAtLeast(1)
    var target by rememberSaveable(theme.id) { mutableIntStateOf(1) }
    var busy by remember { mutableStateOf(false) }
    var result by rememberSaveable(theme.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val copy = { key: String -> WallpaperCopy.text(key,dil) }
    BackHandler(busy) { }
    ModalBottomSheet(onDismissRequest = { if(!busy) close() },containerColor = Renk.zemin,dragHandle = null,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,confirmValueChange = { !busy })) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f).testTag("wallpaper-preview")) {
            Row(Modifier.fillMaxWidth().padding(start = 20.dp,end = 8.dp),verticalAlignment = Alignment.CenterVertically) {
                Text(theme.label(dil),Modifier.weight(1f),color = Renk.metin,fontSize = 20.sp,fontWeight = FontWeight.SemiBold)
                IconButton(onClick = close,enabled = !busy,modifier = Modifier.testTag("wallpaper-close")) { Icon(AzimIkon.Kapat,cevir(dil,"Kapat","Close"),tint = Renk.metin) }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.fillMaxWidth(.56f).align(Alignment.CenterHorizontally).aspectRatio(aspect).clip(RoundedCornerShape(22.dp))) {
                    TemaZemini(theme,Modifier.matchParentSize(),thumbnail = true,previewSize = 2048,dil = dil)
                }
                Text(copy("target"),color = Renk.metin,fontSize = 15.sp,fontWeight = FontWeight.Medium)
                // Vertical choices remain readable with long translations and large fonts.
                listOf("home","lock","both").forEachIndexed { index, key ->
                    Surface(onClick = { target = index + 1; result = null },enabled = !busy,
                        color = if(target == index + 1) Renk.metin else Renk.yuzey,shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("wallpaper-target-${index + 1}").semantics { role = Role.RadioButton; selected = target == index + 1 }) {
                        Text(copy(key),Modifier.padding(14.dp),color = if(target == index + 1) Renk.zemin else Renk.metin,fontSize = 14.sp)
                    }
                }
                Text(copy("note"),color = Renk.metinIkincil,fontSize = 12.sp,lineHeight = 18.sp)
                Spacer(Modifier.height(8.dp))
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp,vertical = 12.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                result?.let { Text(copy(it),color = if(it == "error") MaterialTheme.colorScheme.error else Renk.metin,
                    modifier = Modifier.testTag("wallpaper-result").semantics { liveRegion = LiveRegionMode.Polite },fontSize = 13.sp) }
                Button(enabled = !busy,onClick = {
                    if(theme.pro && !pro) proOpen()
                    else {
                        busy = true; result = null
                        scope.launch {
                            try {
                                val success = applyWallpaper?.invoke(theme,target,aspect) ?: WallpaperService.apply(context,theme,target,aspect)
                                result = if(success) "done" else "error"
                            } catch(e: CancellationException) { throw e }
                            catch(_: Exception) { result = "error" }
                            finally { busy = false }
                        }
                    }
                },modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("wallpaper-apply")) {
                    if(busy) CircularProgressIndicator(Modifier.size(20.dp),strokeWidth = 2.dp)
                    else Text(if(theme.pro && !pro) copy("pro") else copy("apply"))
                }
            }
        }
    }
}
