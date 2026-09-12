package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yalnizfahrettin.azim.core.*

@Composable
fun DenemeTeklifi(dil: String, busy: Boolean, error: Boolean, close: () -> Unit, start: () -> Unit, free: () -> Unit) {
    Dialog(onDismissRequest = { if(!busy) close() }, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        val view = LocalView.current
        val colors = Renk
        SideEffect {
            (view.parent as? DialogWindowProvider)?.window?.let { window ->
                window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(colors.zemin.toArgb()))
                @Suppress("DEPRECATION")
                window.navigationBarColor = colors.zemin.toArgb()
                if(android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
                androidx.core.view.WindowCompat.getInsetsController(window,view).apply {
                    isAppearanceLightNavigationBars = !colors.karanlikMi
                    isAppearanceLightStatusBars = !colors.karanlikMi
                }
            }
        }
        Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).testTag("trial-offer")) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("ascend", Modifier.weight(1f), fontFamily = ArayuzFont, fontWeight = FontWeight.SemiBold, fontSize = 25.sp, color = Renk.metin)
                IconButton(onClick = free, enabled = !busy) { Icon(AzimIkon.Kapat, cevir(dil,"Ücretsiz devam et","Continue free"), tint = Renk.metin) }
            }
            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProRozeti(metin = "ASCEND PRO")
                Text(cevir(dil,"Kendine daha\nfazla alan aç.","Make more\nroom for yourself."), fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold, color = Renk.metin)
                Text(cevir(dil,"3 günlük denemeyle keşfet.","Explore with a 3-day trial."), fontSize = 18.sp, color = Renk.metinIkincil)
                Surface(color = Renk.yuzey, shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        listOf(cevir(dil,"Tüm konular ve sözler","Every topic and quote"), cevir(dil,"Roma, Atlı Yolcu ve tüm temalar","Rome, Rider and every theme"),
                            cevir(dil,"Telefonuna özel widget’lar","Widgets for your phone"), cevir(dil,"Görsel ve video paylaşımları","Image and video sharing")).forEach {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { Icon(AzimIkon.Tik,null,Modifier.size(20.dp),tint = Renk.metin); Text(it,color = Renk.metin,fontSize = 15.sp,lineHeight = 21.sp) }
                        }
                    }
                }
                Text(cevir(dil,"Demo ekranı: ödeme alınmaz, abonelik başlamaz. Bu sürümde üç günlük süre işletilmez; Pro denemesi açılır.",
                    "Demo screen: no payment or subscription. The three-day timer is not active in this build; demo Pro is enabled."), color = Renk.metinIkincil,fontSize = 12.sp,lineHeight = 18.sp)
            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if(error) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Try again."),color = MaterialTheme.colorScheme.error)
                Button(onClick = start,enabled = !busy,modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("trial-start"),shape = RoundedCornerShape(16.dp)) {
                    Text(cevir(dil,if(busy) "Hazırlanıyor…" else "3 günlük denemeyi başlat",if(busy) "Preparing…" else "Start 3-day trial"))
                }
                TextButton(onClick = free,enabled = !busy,modifier = Modifier.testTag("trial-free")) { Text(cevir(dil,"Ücretsiz devam et","Continue free")) }
            }
        }
    }
}
