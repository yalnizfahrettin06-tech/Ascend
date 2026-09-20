package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.BiasAlignment
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
    private val cache = object : android.util.LruCache<String, android.graphics.Bitmap>(32 * 1024 * 1024) {
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
fun TemaZemini(theme: AnaTema, modifier: Modifier = Modifier, veil: Float = .25f, thumbnail: Boolean = false, previewSize: Int = 640, dil: String = "tr") {
    val base = if (theme.dark) Color(0xFF171719) else Color(0xFFF5F5F4)
    Box(modifier.background(base)) {
        theme.art?.let { art ->
            val context = LocalContext.current
            val size = if(thumbnail) previewSize else 1920
            val imageState = remember(art,size) { mutableStateOf<android.graphics.Bitmap?>(null) }
            val bitmap by imageState
            var failed by remember(art,size) { mutableStateOf(false) }
            var retry by remember(art,size) { mutableIntStateOf(0) }
            LaunchedEffect(art,size,retry) {
                failed = false
                try { imageState.value = ThemeImages.load(context,art,size); failed = imageState.value == null }
                catch(e: kotlinx.coroutines.CancellationException) { throw e }
                catch(_: Exception) { failed = true }
            }
            if(bitmap == null) Box(Modifier.matchParentSize().testTag(if(failed) "art-error" else "art-loading"), contentAlignment = Alignment.Center) {
                if(failed) IconButton(onClick = { retry++ }) {
                    Icon(AzimIkon.Sonraki, cevir(dil,"Yeniden dene","Try again"), tint = if(theme.dark) Color.White else Color.Black)
                } else CircularProgressIndicator(Modifier.size(20.dp),color = if(theme.dark) Color.White else Color.Black,strokeWidth = 2.dp)
            }
            val focus = ArtworkFocus.forResource(art)
            bitmap?.let { Image(it.asImageBitmap(), null, Modifier.matchParentSize().testTag("theme-art-${theme.id}"), contentScale = ContentScale.Crop, alignment = BiasAlignment(focus.x * 2 - 1, focus.y * 2 - 1),
                colorFilter = null) }
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(
                base.copy(alpha = if(thumbnail) 0f else if (theme.dark) .32f else veil), base.copy(alpha = if(thumbnail) 0f else if (theme.dark) .08f else .05f)))))
        }
    }
}

@Composable
fun TemaGrid(dil: String, themes: List<AnaTema>, selectedId: String, pro: Boolean, compact: Boolean = false, select: (AnaTema) -> Unit) {
    val columns = if (LocalDensity.current.fontScale > 1.4f || LocalConfiguration.current.screenWidthDp < 360) 1 else 2
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.testTag("theme-gallery")) {
        themes.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { theme ->
                    val selected = selectedId == theme.id
                    val ink = if (theme.dark) Color.White else Color(0xFF171719)
                    Column(Modifier.weight(1f)) {
                        Surface(onClick = { select(theme) }, shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(if (selected) 2.dp else .5.dp, if (selected) Renk.metin else Renk.kenarlik),
                            modifier = Modifier.fillMaxWidth().then(if(compact) Modifier.aspectRatio(if(columns == 1) 1.5f else 1f) else if(theme.art == null) Modifier.heightIn(min = 112.dp) else Modifier.aspectRatio(.72f))
                                .testTag("theme-${theme.id}").semantics {
                                    role = Role.RadioButton
                                    this.selected = selected
                                    contentDescription = theme.label(dil) + if (theme.pro) ", Pro" else ""
                                }) {
                            Box {
                                // Gallery shows the artwork clearly; quote readability is demonstrated in the full preview.
                                TemaZemini(theme, Modifier.matchParentSize(), thumbnail = true, veil = .06f, previewSize = if(columns == 1) 1536 else 640, dil = dil)
                                if(theme.art == null) Text(cevir(dil,"Kendi hızında.\nBir adım daha.","At your pace.\nOne step more."),
                                    Modifier.align(Alignment.CenterStart).padding(16.dp), color = ink, fontFamily = LoraSerif,
                                    fontSize = if(compact) 16.sp else 18.sp, lineHeight = if(compact) 21.sp else 24.sp)
                                if(theme.pro) Text("PRO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                                        .background(Color.Black.copy(alpha = .75f), RoundedCornerShape(6.dp)).padding(horizontal = 7.dp, vertical = 4.dp))
                                if(selected) Surface(color = ink, shape = androidx.compose.foundation.shape.CircleShape,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
                                    Icon(AzimIkon.Tik,cevir(dil,"Seçili","Selected"),Modifier.padding(5.dp).size(15.dp),tint = if(theme.dark) Color.Black else Color.White)
                                }
                            }
                        }
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(theme.label(dil), Modifier.weight(1f), color = Renk.metin, fontSize = 12.sp, lineHeight = 17.sp,
                                fontWeight = if(selected) FontWeight.SemiBold else FontWeight.Medium)
                        }
                    }
                }
                if (row.size < columns) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TemaKoleksiyonKapagi(dil: String, selected: Boolean = false, open: () -> Unit) {
    Surface(onClick = open, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().height((186 * LocalDensity.current.fontScale.coerceAtLeast(1f)).dp).testTag("theme-featured")) {
        Box {
            TemaZemini(AnaTemalar.emperor,Modifier.matchParentSize(),thumbnail = true,previewSize = 1024)
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = .62f),Color.Transparent))))
            Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement = Arrangement.Bottom) {
                Text(CollectionCopy.text("badge",dil),color = Color.White.copy(alpha = .8f),fontSize = 9.sp,letterSpacing = 1.sp)
                Spacer(Modifier.height(7.dp))
                Text(AnaTemalar.emperor.label(dil),Modifier.fillMaxWidth(.7f),color = Color.White,fontSize = 24.sp,lineHeight = 29.sp,fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if(selected) cevir(dil,"Seçili","Selected") else cevir(dil,"Önizle","Preview"),color = Color.White,fontSize = 12.sp)
                    Icon(AzimIkon.Ileri,null,Modifier.size(16.dp),tint = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemaOnizleme(theme: AnaTema, dil: String, pro: Boolean, close: () -> Unit, apply: () -> Unit, proOpen: () -> Unit, quote: Soz? = null) {
    ModalBottomSheet(onDismissRequest = close, containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(theme.label(dil), Modifier.weight(1f), color = Renk.metin, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = close) { Icon(AzimIkon.Kapat, cevir(dil, "Kapat", "Close")) }
            }
            Box(Modifier.fillMaxWidth().aspectRatio(.58f).clip(RoundedCornerShape(22.dp)).testTag("theme-preview")) {
                TemaZemini(theme, Modifier.matchParentSize(), dil = dil)
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.Center) {
                    val previewText = quote?.metin(dil) ?: cevir(dil,"Kendine ayırdığın\nbu an yeter.","This moment\nfor yourself is enough.")
                    val quoteSize = ReadingLayout.quoteSize(previewText.length, LocalDensity.current.fontScale > 1.35f, LocalConfiguration.current.screenWidthDp < 380)
                    Text(quote?.metin(dil) ?: cevir(dil, "Kendine ayırdığın\nbu an yeter.", "This moment\nfor yourself is enough."), Modifier.fillMaxWidth(if(theme.art == null || LocalDensity.current.fontScale > 1.35f) 1f else .76f), fontFamily = LoraSerif,
                        fontSize = quoteSize.sp, lineHeight = (quoteSize + 5).sp, color = if(theme.dark) Color.White else Color.Black)
                }
            }
            Text(cevir(dil, "Görsel ana ekranında; diğer ekranlarda uyumlu, sade bir görünüm.",
                "Artwork on your home screen; a matching, simple look everywhere else."), color = Renk.metinIkincil, fontSize = 13.sp)
        }
        Box(Modifier.fillMaxWidth().padding(16.dp)) {
            Button(onClick = if(theme.pro && !pro) proOpen else apply, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("theme-apply")) {
                Text(cevir(dil, if(theme.pro && !pro) "Pro ile kullan" else "Bu temayı kullan", if(theme.pro && !pro) "Use with Pro" else "Use this theme"))
            }
        }
    }
    }
}

@Composable
fun ArkaPlanGrid(dil: String, themes: List<AnaTema>, selectedId: String, select: (AnaTema) -> Unit) {
    val columns = if(LocalDensity.current.fontScale > 1.4f || LocalConfiguration.current.screenWidthDp < 360) 2 else 3
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        themes.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { theme ->
                    Column(Modifier.weight(1f)) {
                        Surface(onClick = { select(theme) }, shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(if(selectedId == theme.id) 2.dp else .5.dp, if(selectedId == theme.id) Renk.metin else Renk.kenarlik),
                            modifier = Modifier.fillMaxWidth().aspectRatio(.8f).testTag("widget-background-${theme.id}").semantics { selected = selectedId == theme.id; role = Role.RadioButton; contentDescription = theme.label(dil) }) {
                            Box {
                                TemaZemini(theme, Modifier.matchParentSize(), .05f, thumbnail = true)
                                if(selectedId == theme.id) Icon(AzimIkon.Tik,null,Modifier.align(Alignment.BottomEnd).padding(8.dp).size(18.dp),tint = if(theme.dark) Color.White else Color.Black)
                            }
                        }
                        Text(theme.label(dil), color = Renk.metinIkincil,fontSize = 11.sp,lineHeight = 16.sp,modifier = Modifier.padding(top = 5.dp))
                    }
                }
                repeat(columns-row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
