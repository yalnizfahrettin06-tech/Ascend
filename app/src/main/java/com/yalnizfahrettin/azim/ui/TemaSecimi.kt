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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

private object ThemeImages {
    private val cache = object : android.util.LruCache<String, android.graphics.Bitmap>(16 * 1024 * 1024) {
        override fun sizeOf(key: String, value: android.graphics.Bitmap) = value.allocationByteCount
    }
    private val permits = Semaphore(2)
    suspend fun load(context: android.content.Context, resource: Int, maxSide: Int): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
        val key = "$resource:$maxSide"
        cache.get(key) ?: permits.withPermit {
            cache.get(key) ?: run {
                val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
                android.graphics.BitmapFactory.decodeResource(context.resources, resource, bounds)
                var sample = 1
                while(maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
                android.graphics.BitmapFactory.decodeResource(context.resources, resource,
                    android.graphics.BitmapFactory.Options().apply { inSampleSize = sample; inScaled = false })?.also { cache.put(key,it) }
            }
        }
    }
}

@Composable
fun TemaZemini(theme: AnaTema, modifier: Modifier = Modifier, veil: Float = .25f, thumbnail: Boolean = false) {
    val base = if (theme.dark) Color(0xFF171719) else Color(0xFFF5F5F4)
    Box(modifier.background(base)) {
        theme.art?.let { art ->
            val context = LocalContext.current
            val size = if(thumbnail) 320 else 1200
            val imageState = remember(art,size) { mutableStateOf<android.graphics.Bitmap?>(null) }
            val bitmap by imageState
            LaunchedEffect(art,size) { imageState.value = ThemeImages.load(context,art,size) }
            bitmap?.let { Image(it.asImageBitmap(), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop,
                colorFilter = if(theme.id == "rider") null else ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })) }
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(
                base.copy(alpha = if (theme.dark) .65f else veil), base.copy(alpha = if (theme.dark) .2f else .05f)))))
        }
    }
}

@Composable
fun TemaGrid(dil: String, themes: List<AnaTema>, selectedId: String, pro: Boolean, compact: Boolean = false, select: (AnaTema) -> Unit) {
    val columns = if (LocalDensity.current.fontScale > 1.4f) 1 else 2
    Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.testTag("theme-gallery")) {
        themes.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { theme ->
                    val selected = selectedId == theme.id
                    Column(Modifier.weight(1f)) {
                        Surface(onClick = { select(theme) }, shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Renk.metin else Renk.kenarlik),
                            modifier = Modifier.fillMaxWidth().height(if(compact) 128.dp else 156.dp).testTag("theme-${theme.id}")
                                .semantics { this.selected = selected; contentDescription = theme.label(dil) + if (theme.pro) ", Pro" else "" }) {
                            Box {
                                TemaZemini(theme, Modifier.matchParentSize(), thumbnail = true)
                                val ink = if (theme.dark) Color.White else Color(0xFF171719)
                                Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("ascend", fontFamily = ArayuzFont, fontWeight = FontWeight.SemiBold, color = ink, fontSize = 13.sp)
                                        if (theme.pro) Text("PRO", color = ink, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                            modifier = Modifier.background(if (theme.dark) Color.Black.copy(.55f) else Color.White.copy(.85f), RoundedCornerShape(5.dp)).padding(4.dp))
                                    }
                                    Text(cevir(dil, "Kendi hızında.\nBir adım daha.", "At your pace.\nOne step more."), color = ink,
                                        fontFamily = LoraSerif, fontSize = if(compact) 15.sp else 18.sp, lineHeight = if(compact) 20.sp else 23.sp)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Icon(if (selected) AzimIkon.Tik else if (theme.pro && !pro) AzimIkon.Kilit else AzimIkon.Kalp, null, Modifier.size(16.dp), tint = ink)
                                    }
                                }
                            }
                        }
                        Text(theme.label(dil), color = Renk.metin, fontSize = 12.sp, lineHeight = 18.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.padding(top = 6.dp))
                    }
                }
                if (row.size < columns) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaOnizleme(theme: AnaTema, dil: String, pro: Boolean, close: () -> Unit, apply: () -> Unit, proOpen: () -> Unit) {
    ModalBottomSheet(onDismissRequest = close, containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(theme.label(dil), Modifier.weight(1f), color = Renk.metin, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = close) { Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close")) }
            }
            Box(Modifier.fillMaxWidth().height(340.dp).clip(RoundedCornerShape(22.dp)).testTag("theme-preview")) {
                TemaZemini(theme, Modifier.matchParentSize())
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
                    Text(cevir(dil, "Kendine ayırdığın\nbu an yeter.", "This moment\nfor yourself is enough."), fontFamily = LoraSerif,
                        fontSize = 30.sp, lineHeight = 38.sp, color = if(theme.dark) Color.White else Color.Black)
                }
            }
            Text(cevir(dil, "Görsel ana ekranında; diğer ekranlarda uyumlu, sade bir görünüm.",
                "Artwork on your home screen; a matching, simple look everywhere else."), color = Renk.metinIkincil, fontSize = 13.sp)
            Button(onClick = if(theme.pro && !pro) proOpen else apply, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("theme-apply")) {
                Text(cevir(dil, if(theme.pro && !pro) "Pro ile kullan" else "Bu temayı kullan", if(theme.pro && !pro) "Use with Pro" else "Use this theme"))
            }
        }
    }
}

@Composable
fun ArkaPlanGrid(dil: String, themes: List<AnaTema>, selectedId: String, select: (AnaTema) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        themes.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { theme ->
                    Column(Modifier.weight(1f)) {
                        Surface(onClick = { select(theme) }, shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(if(selectedId == theme.id) 2.dp else .5.dp, if(selectedId == theme.id) Renk.metin else Renk.kenarlik),
                            modifier = Modifier.fillMaxWidth().aspectRatio(.8f).testTag("widget-background-${theme.id}")) {
                            Box {
                                TemaZemini(theme, Modifier.matchParentSize(), .05f, thumbnail = true)
                                if(selectedId == theme.id) Icon(AzimIkon.Tik,null,Modifier.align(Alignment.BottomEnd).padding(8.dp).size(18.dp),tint = if(theme.dark) Color.White else Color.Black)
                            }
                        }
                        Text(theme.label(dil), color = Renk.metinIkincil,fontSize = 11.sp,lineHeight = 16.sp,modifier = Modifier.padding(top = 5.dp))
                    }
                }
                repeat(3-row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
