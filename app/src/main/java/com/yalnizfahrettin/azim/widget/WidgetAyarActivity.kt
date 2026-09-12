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
        val depot = Depo(this)
        val initial = WidgetTasarimi.load(this, id)
        setContent {
            val pro by depot.proDemo.collectAsStateWithLifecycle(false)
            val dil by depot.dil.collectAsStateWithLifecycle("tr")
            val mode by depot.tema.collectAsStateWithLifecycle(TemaModu.AYDINLIK)
            var theme by rememberSaveable { mutableStateOf(initial.theme) }
            var showPro by rememberSaveable { mutableStateOf(false) }
            var busy by remember { mutableStateOf(false) }
            var message by rememberSaveable { mutableStateOf<String?>(null) }
            val config = WidgetSecimi(theme, true, false)
            val quote = cevir(dil, "Küçük bir adım da ilerlemektir.", "A small step is still a step forward.")
            val previewState = remember(config,dil) { mutableStateOf<android.graphics.Bitmap?>(null) }
            val bitmap by previewState
            LaunchedEffect(config,dil) {
                previewState.value = withContext(Dispatchers.Default) { WidgetTasarimi.render(this@WidgetAyarActivity, config, quote, "Ascend", 720, 360) }
            }
            AzimTema(modu = mode) {
                Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding()) {
                    Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { finish() }) { Icon(AzimIkon.Geri, cevir(dil,"Geri","Back"), tint = Renk.metin) }
                        Text(cevir(dil,"Arka planını seç","Choose a background"), Modifier.weight(1f), color = Renk.metin, fontSize = 18.sp)
                        ProRozeti()
                    }
                    LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Box(Modifier.fillMaxWidth().aspectRatio(2f).clip(RoundedCornerShape(20.dp)).background(Renk.yuzey).testTag("widget-live-preview")) {
                                bitmap?.let { Image(it.asImageBitmap(), quote, Modifier.fillMaxSize()) }
                            }
                        }
                        item { Text(cevir(dil,"Her gün yeni bir söz · Önizleme", "A new quote each day · Preview"), color = Renk.metinIkincil, fontSize = 12.sp) }
                        items(AnaTemalar.all.chunked(3), key = { it.first().id }) { row -> ArkaPlanGrid(dil, row, theme) { theme = it.id } }
                    }
                    Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        message?.let { Text(it, color = Renk.metinIkincil, fontSize = 12.sp) }
                        Button(onClick = {
                            if (!pro) showPro = true else {
                                busy = true
                                lifecycleScope.launch {
                                    try {
                                        if (!depot.proDemo.first()) { showPro = true; return@launch }
                                        if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                                            WidgetTasarimi.save(this@WidgetAyarActivity, id, config)
                                            AzimWidget.tazele(this@WidgetAyarActivity)
                                            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)); finish()
                                        } else {
                                            val manager = AppWidgetManager.getInstance(this@WidgetAyarActivity)
                                            if (!manager.isRequestPinAppWidgetSupported) {
                                                message = cevir(dil,"Ana ekranına uzun bas → Widget’lar → Ascend.","Long press your home screen → Widgets → Ascend.")
                                            } else {
                                                val intent = Intent(this@WidgetAyarActivity, WidgetEkleAlicisi::class.java)
                                                    .setAction("ascend.widget.pin." + java.util.UUID.randomUUID())
                                                    .putExtra("theme", theme).putExtra("center", true).putExtra("large", false)
                                                val callback = PendingIntent.getBroadcast(this@WidgetAyarActivity, 0, intent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                                val requested = manager.requestPinAppWidget(ComponentName(this@WidgetAyarActivity, AzimWidgetSaglayici::class.java), null, callback)
                                                message = if(requested) cevir(dil,"Telefonunun ekleme penceresini onayla.","Confirm in your phone's add-widget dialog.")
                                                    else cevir(dil,"Ana ekranından Widget’lar → Ascend yolunu kullan.","Use Widgets → Ascend on your home screen.")
                                            }
                                        }
                                    } catch (_: Exception) { message = cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Try again.") }
                                    finally { busy = false }
                                }
                            }
                        }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp).testTag("widget-add")) {
                            Text(cevir(dil, if(!pro) "Pro ile kullan" else if(id > 0) "Widget’ı kaydet" else "Telefon ekranına ekle",
                                if(!pro) "Use with Pro" else if(id > 0) "Save widget" else "Add to home screen"))
                        }
                    }
                }
                if(showPro) ProEkrani(dil, pro, kapat = { showPro = false }, degistir = { enabled -> lifecycleScope.launch { depot.proDemoAyarla(enabled); AzimWidget.tazele(this@WidgetAyarActivity); showPro = false } })
            }
        }
    }
}

internal fun ownsWidget(ctx: Context, id: Int): Boolean = AppWidgetManager.getInstance(ctx).getAppWidgetInfo(id)?.provider == ComponentName(ctx, AzimWidgetSaglayici::class.java)

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
