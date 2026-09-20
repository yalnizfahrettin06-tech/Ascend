package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.yalnizfahrettin.azim.data.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun DenemeTeklifi(dil: String, busy: Boolean, error: Boolean, close: () -> Unit, start: () -> Unit, free: () -> Unit, offer: ProOffer = ProOffer(ProSource.ONBOARDING)) {
    SetupTheme {
    val context = LocalContext.current
    var impression by rememberSaveable(offer.encode()) { mutableStateOf(false) }
    LaunchedEffect(offer.encode()) { if (!impression) { ProductSignals.record(context,ProductSignals.Event.OFFER_VIEWED,ProSource.ONBOARDING); impression = true } }
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
        Column(Modifier.fillMaxSize().background(Renk.zemin).safeDrawingPadding().imePadding().testTag("trial-offer")) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("ascend", Modifier.weight(1f), fontFamily = ArayuzFont, fontWeight = FontWeight.SemiBold, fontSize = 25.sp, color = Renk.metin)
                IconButton(onClick = free, enabled = !busy) { Icon(AzimIkon.Kapat, SetupCopy.text("freeLooks",dil), tint = Renk.metin) }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProRozeti(metin = "PRO DEMO")
                Text(proOfferTitle(offer,dil), fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold, color = Renk.metin)
                if(offer.selection.isNotBlank()) Text(PhaseCopy.text("demo",dil), fontSize = 16.sp, color = Renk.metinIkincil)
                ProGorselOrnek(dil,offer)
                ProOutcome(dil,offer)
                ProBenefits(dil,offer)

            }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(PhaseCopy.text("demoBody",dil),color = Renk.metinIkincil,fontSize = 12.sp,lineHeight = 18.sp)
                if(error) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Try again."),color = MaterialTheme.colorScheme.error)
                Button(onClick = start,enabled = !busy,modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("trial-start"),shape = RoundedCornerShape(16.dp)) {
                    Text(if(busy) cevir(dil,"Hazırlanıyor…","Preparing…") else PhaseCopy.text("enable",dil))
                }
                TextButton(onClick = free,enabled = !busy,modifier = Modifier.testTag("trial-free")) { Text(SetupCopy.text("freeLooks",dil)) }
            }
        }
    }
}
}
