package com.yalnizfahrettin.azim.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.widget.WidgetAyarActivity
import kotlin.math.sin

/** Only the single visible scene animates. Gallery, wallpaper and widgets stay still. */
@Composable
fun LivingScene(modifier: Modifier = Modifier, enabled: Boolean = true) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var resumed by remember { mutableStateOf(owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
    var systemAllows by remember { mutableStateOf(false) }
    DisposableEffect(owner, context) {
        fun refresh() {
            resumed = owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            systemAllows = ValueAnimator.areAnimatorsEnabled() && !(context.getSystemService(Context.POWER_SERVICE) as PowerManager).isPowerSaveMode
        }
        val observer = LifecycleEventObserver { _, _ -> refresh() }
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) = refresh()
        }
        owner.lifecycle.addObserver(observer)
        androidx.core.content.ContextCompat.registerReceiver(context,receiver,android.content.IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED)
        refresh()
        onDispose { owner.lifecycle.removeObserver(observer); context.unregisterReceiver(receiver) }
    }
    val moving = enabled && resumed && systemAllows
    val phase: State<Float> = if(moving) {
        val transition = rememberInfiniteTransition(label = "emperor-atmosphere")
        transition.animateFloat(0f,1f,infiniteRepeatable(tween(18000,easing = LinearEasing),RepeatMode.Restart),label = "atmosphere-phase")
    } else remember { mutableFloatStateOf(0f) }
    Box(modifier.clip(RoundedCornerShape(0.dp)).testTag(if(moving) "living-moving" else "living-still")) {
        TemaZemini(AnaTemalar.emperor,Modifier.matchParentSize().graphicsLayer {
            val drift = sin(phase.value * 6.283185f)
            scaleX = 1.025f; scaleY = 1.025f; translationX = drift * 4f
        },thumbnail = false)
        Canvas(Modifier.matchParentSize()) {
            val t = phase.value
            // A slow diffuse glow and a handful of low-opacity motes, never a full white veil.
            val glow = Offset(size.width * (.7f + .08f * sin(t * 6.283185f)),size.height * .22f)
            drawRect(Brush.radialGradient(listOf(Color(0xFFFFD6A0).copy(alpha = .065f),Color.Transparent),glow,size.width * .8f))
            for(i in 0 until 14) {
                val seed = (i * .618034f) % 1f
                val y = (1f - ((t + seed) % 1f)) * size.height
                val x = size.width * (.14f + .74f * ((i * .381966f) % 1f)) + sin(t * 6.283185f + i) * 7f
                val fade = sin(((t + seed) % 1f) * 3.14159f).coerceAtLeast(0f)
                drawCircle(Color(0xFFF1D7B1).copy(alpha = .22f * fade),if(i % 3 == 0) 1.8f else 1f,Offset(x,y))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivingCollection(dil: String, pro: Boolean, close: () -> Unit, apply: (String) -> Unit, suspendedMotion: Boolean = false, proOpen: () -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var motion by rememberSaveable { mutableStateOf(true) }
    val ctx = LocalContext.current
    val copy = { key: String -> CollectionCopy.text(key,dil) }
    ModalBottomSheet(onDismissRequest = close,containerColor = Renk.zemin,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),dragHandle = null) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f).testTag("living-collection")) {
            Row(Modifier.fillMaxWidth().padding(start = 20.dp,end = 8.dp,top = 8.dp),verticalAlignment = Alignment.CenterVertically) {
                Text(copy("prototype"),Modifier.weight(1f),color = Renk.metinIkincil,fontSize = 12.sp)
                IconButton(onClick = close) { Icon(AzimIkon.Kapat,cevir(dil,"Kapat","Close")) }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(copy("title"),color = Renk.metin,fontSize = 26.sp,lineHeight = 32.sp,fontWeight = FontWeight.SemiBold)
                Text(copy("tagline"),color = Renk.metinIkincil,fontSize = 14.sp,lineHeight = 21.sp)
                Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(copy("scene"),copy("wallpaper"),"Widget").forEachIndexed { index, label ->
                        FilterChip(selected = tab == index,onClick = { tab = index },label = { Text(label,fontSize = 11.sp) },modifier = Modifier.weight(1f).testTag("collection-tab-$index"))
                    }
                }
                if(tab == 2) {
                    // Same artwork, a dedicated wide crop and centered widget text.
                    Box(Modifier.fillMaxWidth().aspectRatio(2f).clip(RoundedCornerShape(22.dp))) {
                        TemaZemini(AnaTemalar.emperor,Modifier.matchParentSize(),thumbnail = true,previewSize = 1024)
                        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .32f)))
                        Text(cevir(dil,"Küçük bir adım da ilerlemektir.","A small step is still a step forward."),Modifier.align(Alignment.Center).padding(24.dp),color = Color.White,fontSize = 19.sp,lineHeight = 26.sp,textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                } else {
                    Box(Modifier.fillMaxWidth().aspectRatio(if(tab == 0) .84f else .66f).clip(RoundedCornerShape(24.dp))) {
                        if(tab == 0) LivingScene(Modifier.matchParentSize(),motion && !suspendedMotion)
                        else TemaZemini(AnaTemalar.emperor,Modifier.matchParentSize(),thumbnail = true,previewSize = 1536)
                        if(tab == 0) {
                            Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent,Color.Black.copy(alpha = .8f)))))
                            Column(Modifier.align(Alignment.BottomStart).padding(24.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(AzimIkon.YukselenMarka,null,Modifier.size(22.dp),tint = Color.White.copy(alpha = .8f))
                                Text(cevir(dil,"Küçük bir adım da ilerlemektir.","A small step is still a step forward."),color = Color.White,fontFamily = LoraSerif,fontSize = 28.sp,lineHeight = 36.sp)
                            }
                        } else {
                            Text("09:41",Modifier.align(Alignment.TopCenter).padding(top = 28.dp),color = Color.White.copy(alpha = .9f),fontSize = 40.sp,fontWeight = FontWeight.Light)
                        }
                    }
                }
                if(tab == 0) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
                        Text(copy("motion"),Modifier.weight(1f),color = Renk.metin)
                        Switch(checked = motion,onCheckedChange = { motion = it },modifier = Modifier.testTag("collection-motion"))
                    }
                    Text(copy("quiet"),color = Renk.metinIkincil,fontSize = 12.sp,lineHeight = 18.sp)
                }
                if(tab == 1) Text(copy("preview"),color = Renk.metinIkincil,fontSize = 12.sp,lineHeight = 18.sp)
                Spacer(Modifier.height(8.dp))
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp,vertical = 12.dp)) {
                if(tab != 1) Button(onClick = {
                    if(!pro) { proOpen() }
                    else if(tab == 2) ctx.startActivity(Intent(ctx,WidgetAyarActivity::class.java).putExtra("collection_theme","emperor"))
                    else { apply(if(motion) AnaTemalar.living.id else AnaTemalar.emperor.id); close() }
                },modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("collection-use")) {
                    Text(if(!pro) copy("pro") else if(tab == 2) copy("widget") else if(motion) copy("apply") else copy("still"))
                }
            }
        }
    }
}
