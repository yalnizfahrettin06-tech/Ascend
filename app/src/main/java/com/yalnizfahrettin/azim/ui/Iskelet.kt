package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*

enum class Sekme(val rota: String) { ANA("ana"), KATEGORI("kategori"), FAVORI("favori"), ISTATISTIK("istatistik") }

@Composable
fun AltNav(secili: Sekme, secildi: (Sekme) -> Unit) {
    val dil = LocalConfiguration.current.locales[0].language
    val sekmeler = listOf(Sekme.ANA, Sekme.KATEGORI, Sekme.ISTATISTIK)
    val etiketler = listOf(cevir(dil, "Bugün", "Today"), cevir(dil, "Keşfet", "Explore"), cevir(dil, "Senin", "You"))
    val ikonlar = listOf(AzimIkon.Yukselis, AzimIkon.Kesfet, AzimIkon.Kisi)
    Column(Modifier.fillMaxWidth().background(Renk.zemin).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Renk.kenarlik.copy(alpha = .55f)))
        Row(Modifier.fillMaxWidth().selectableGroup().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            sekmeler.forEachIndexed { index, sekme ->
                val etkin = secili == sekme || (sekme == Sekme.ISTATISTIK && secili == Sekme.FAVORI)
                val renk by animateColorAsState(if (etkin) Renk.metin else Renk.metinIkincil, tween(150), label = "navigation-ink")
                Column(
                    Modifier.weight(1f).heightIn(min = 64.dp).testTag("nav-${sekme.rota}")
                        .selectable(selected = etkin, role = Role.Tab, onClick = { if (secili != sekme) secildi(sekme) })
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(Modifier.width(18.dp).height(2.dp).background(if (etkin) renk else Color.Transparent, RoundedCornerShape(1.dp)))
                    Icon(ikonlar[index], null, Modifier.size(22.dp), tint = renk)
                    Text(etiketler[index], color = renk, fontSize = 11.sp, lineHeight = 14.sp,
                        fontWeight = if (etkin) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
