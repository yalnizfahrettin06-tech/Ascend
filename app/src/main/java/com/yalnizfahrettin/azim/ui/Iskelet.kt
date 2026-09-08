package com.yalnizfahrettin.azim.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*

enum class Sekme(val rota: String) { ANA("ana"), KATEGORI("kategori"), FAVORI("favori"), ISTATISTIK("istatistik") }

@Composable
fun AltNav(secili: Sekme, secildi: (Sekme) -> Unit) {
    val etiketler = listOf(R.string.nav_ana, R.string.nav_kategori, R.string.nav_favori, R.string.nav_istatistik)
    val ikonlar = listOf(AzimIkon.Dag, AzimIkon.Kesfet, AzimIkon.Ayrac, AzimIkon.Patika)
    val etkinIkonlar = listOf(AzimIkon.DagDolu, AzimIkon.Kesfet, AzimIkon.AyracDolu, AzimIkon.Patika)
    Box(Modifier.fillMaxWidth().background(Renk.zemin).navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Surface(
            color = Renk.yuzey,
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Renk.kenarlik.copy(alpha = .6f)),
            shadowElevation = 4.dp,
        ) {
            Row(
                Modifier.fillMaxWidth().selectableGroup().padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Sekme.entries.forEachIndexed { index, sekme ->
                    val etkin = secili == sekme
                    val renk by animateColorAsState(if (etkin) Renk.accent else Renk.metinIkincil, tween(180), label = "nav-color")
                    val zemin by animateColorAsState(if (etkin) Renk.accent.copy(alpha = .12f) else Color.Transparent, tween(180), label = "nav-background")
                    val olcek by animateFloatAsState(if (etkin) 1.06f else 1f, tween(180), label = "nav-icon")
                    Column(
                        Modifier.weight(1f).heightIn(min = 64.dp).clip(RoundedCornerShape(22.dp))
                            .background(zemin)
                            .testTag("nav-${sekme.rota}")
                            .selectable(selected = etkin, role = Role.Tab, onClick = { if (!etkin) secildi(sekme) })
                            .padding(horizontal = 2.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    ) {
                        Icon(if (etkin) etkinIkonlar[index] else ikonlar[index], null, Modifier.size(23.dp).scale(olcek), tint = renk)
                        Text(
                            stringResource(etiketler[index]), color = renk, fontSize = 11.sp,
                            lineHeight = 14.sp, letterSpacing = 0.sp,
                            fontWeight = if (etkin) FontWeight.SemiBold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
