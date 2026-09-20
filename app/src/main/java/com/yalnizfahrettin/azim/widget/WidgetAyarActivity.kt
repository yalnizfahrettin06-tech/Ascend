package com.yalnizfahrettin.azim.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class WidgetAyarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (id != AppWidgetManager.INVALID_APPWIDGET_ID && !ownsWidget(this, id)) { finish(); return }
        setContent {
            WidgetSetup(id = id, initialTheme = intent.getStringExtra("collection_theme"), close = { finish() }, configured = { widgetId ->
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId)); finish()
            })
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun WidgetSetup(id: Int = AppWidgetManager.INVALID_APPWIDGET_ID, initialTheme: String? = null,
    embedded: Boolean = false, language: String? = null, close: () -> Unit = {}, configured: (Int) -> Unit = {}) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val depot = remember(ctx) { Depo(ctx) }
    val initial = remember(ctx,id) { WidgetTasarimi.load(ctx,id) }

            val pro by depot.proDemo.collectAsStateWithLifecycle(false)
            val storedLanguage by depot.dil.collectAsStateWithLifecycle("tr")
            val dil = language ?: storedLanguage
            val mode by depot.tema.collectAsStateWithLifecycle(TemaModu.AYDINLIK)
            var square by rememberSaveable { mutableStateOf(
                AppWidgetManager.getInstance(ctx).getAppWidgetInfo(id)?.provider == ComponentName(ctx,AzimSquareWidgetSaglayici::class.java)) }
            var theme by rememberSaveable { mutableStateOf(initialTheme?.takeIf { choice -> AnaTemalar.all.any { it.id == choice } } ?: initial.theme) }
            var showPro by rememberSaveable { mutableStateOf(false) }
            var busy by remember { mutableStateOf(false) }
            var message by rememberSaveable { mutableStateOf<String?>(null) }
            val config = WidgetSecimi(theme, true, false)
            val quote = cevir(dil, "Küçük bir adım da ilerlemektir.", "A small step is still a step forward.")
            val widgetPreview = rememberWidgetPreview(config, quote, if(square) 1080 else 540)
            val bitmap = widgetPreview.bitmap
            fun addWidget() {
                if(busy) return
                if (!pro) showPro = true else {
                                busy = true
                                scope.launch {
                                    try {
                                        if (!depot.proDemo.first()) { showPro = true; return@launch }
                                        if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                                            WidgetTasarimi.save(ctx, id, config)
                                            AzimWidget.tazele(ctx)
                                            configured(id)
                                        } else {
                                            val manager = AppWidgetManager.getInstance(ctx)
                                            if (!manager.isRequestPinAppWidgetSupported) {
                                                message = cevir(dil,"Ana ekranına uzun bas → Widget’lar → Ascend.","Long press your home screen → Widgets → Ascend.")
                                            } else {
                                                val intent = Intent(ctx, WidgetEkleAlicisi::class.java)
                                                    .setAction("ascend.widget.pin." + java.util.UUID.randomUUID())
                                                    .putExtra("theme", theme).putExtra("center", true).putExtra("large", false)
                                                val callback = PendingIntent.getBroadcast(ctx, 0, intent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                                val requested = manager.requestPinAppWidget(ComponentName(ctx, if(square) AzimSquareWidgetSaglayici::class.java else AzimWidgetSaglayici::class.java), null, callback)
                                                message = if(requested) cevir(dil,"Telefonunun ekleme penceresini onayla.","Confirm in your phone's add-widget dialog.")
                                                    else cevir(dil,"Ana ekranından Widget’lar → Ascend yolunu kullan.","Use Widgets → Ascend on your home screen.")
                                            }
                                        }
                                    } catch (_: Exception) { message = cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Try again.") }
                                    finally { busy = false }
                                }
                            }
            }
            var resumeAdd by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(pro,resumeAdd) { if(pro && resumeAdd) { resumeAdd = false; addWidget() } }
            WidgetTheme(mode,embedded) {
                Column(Modifier.fillMaxSize().background(Renk.zemin).then(if(embedded) Modifier else Modifier.safeDrawingPadding())) {
                    val gridColumns = if(androidx.compose.ui.platform.LocalDensity.current.fontScale > 1.4f || androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp < 360) 2 else 3
                    LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                    Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if(!embedded) IconButton(onClick = { close() }) { Icon(AzimIkon.Geri, cevir(dil,"Geri","Back"), tint = Renk.metin) }
                        Text(cevir(dil,"Arka planını seç","Choose a background"), Modifier.weight(1f), color = Renk.metin, fontSize = 18.sp)
                        ProRozeti()
                    }
                    }
                    item { if(id == AppWidgetManager.INVALID_APPWIDGET_ID) FlowRow(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(false,true).forEach { option ->
                            FilterChip(selected = square == option,onClick = { square = option },leadingIcon = if(square == option) {{ Icon(AzimIkon.Tik,null,Modifier.size(16.dp)) }} else null,label = {
                                Text(if(option) cevir(dil,"Kare · 2 × 2","Square · 2 × 2") else cevir(dil,"Geniş · 4 × 2","Wide · 4 × 2"))
                            },modifier = Modifier.widthIn(min = 120.dp).testTag(if(option) "widget-square" else "widget-wide"))
                        }
                    }
                    }
                        item {
                            Box(Modifier.fillMaxWidth().aspectRatio(if(square) 1f else 2f).clip(RoundedCornerShape(20.dp)).background(Renk.yuzey).testTag("widget-live-preview")) {
                                bitmap?.let { Image(it.asImageBitmap(), quote, Modifier.fillMaxSize()) }
                                if(widgetPreview.failed) TextButton(onClick = widgetPreview::retry, modifier = Modifier.align(Alignment.Center)) { Text(cevir(dil,"Yeniden dene","Try again")) }
                                else if(bitmap == null) CircularProgressIndicator(Modifier.align(Alignment.Center).size(24.dp),color = Renk.metin)
                            }
                        }
                        item { Text(cevir(dil,"Her gün yeni bir söz · Boyutu ana ekranında da ayarlayabilirsin.", "A new quote each day · Resize on your home screen too."), color = Renk.metinIkincil, fontSize = 12.sp) }
                        items(AnaTemalar.all.chunked(gridColumns), key = { it.first().id }) { row -> ArkaPlanGrid(dil, row, theme) { theme = it.id } }
                    }
                    Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        message?.let { Text(it, color = Renk.metinIkincil, fontSize = 12.sp) }
                        Button(onClick = {
                            addWidget()
                        }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("widget-add")) {
                            Text(cevir(dil, if(!pro) "Pro ile kullan" else if(id > 0) "Widget’ı kaydet" else "Telefon ekranına ekle",
                                if(!pro) "Use with Pro" else if(id > 0) "Save widget" else "Add to home screen"))
                        }
                    }
                }
                if(showPro) ProEkrani(dil, pro, kaydediliyor = busy, hata = message,
                    offer = ProOffer(ProSource.WIDGET,theme,square = square), widgetPreview = bitmap,
                    kapat = { showPro = false }, degistir = { enabled ->
                        if(!busy) { busy = true; scope.launch {
                            try { depot.proDemoAyarla(enabled); showPro = false; resumeAdd = enabled
                                if(enabled) ProductSignals.record(ctx,ProductSignals.Event.DEMO_ENABLED,ProSource.WIDGET)
                            } catch(_: java.io.IOException) { message = cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Try again.") }
                            finally { busy = false }
                        } }
                    })
            }
}

internal fun ownsWidget(ctx: Context, id: Int): Boolean = AppWidgetManager.getInstance(ctx).getAppWidgetInfo(id)?.provider in setOf(
    ComponentName(ctx,AzimWidgetSaglayici::class.java),ComponentName(ctx,AzimSquareWidgetSaglayici::class.java))

class WidgetEkleAlicisi : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (id <= 0 || !ownsWidget(context,id)) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (Depo(context).proDemo.first()) WidgetTasarimi.save(context,id,WidgetSecimi(
                    AnaTemalar.find(intent.getStringExtra("theme")).id, intent.getBooleanExtra("center",false), intent.getBooleanExtra("large",false)))
                AzimWidget.tazele(context)
            } finally { pending.finish() }
        }
    }
}

@Composable
private fun WidgetTheme(mode: TemaModu, embedded: Boolean, content: @Composable () -> Unit) {
    if(embedded) content() else AzimTema(modu = mode, icerik = content)
}
