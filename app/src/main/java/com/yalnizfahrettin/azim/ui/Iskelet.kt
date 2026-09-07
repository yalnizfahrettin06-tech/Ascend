package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*

enum class Sekme(val rota: String) { ANA("ana"), KATEGORI("kategori"), FAVORI("favori"), ISTATISTIK("istatistik") }

@Composable
fun AltNav(secili: Sekme, secildi: (Sekme) -> Unit) {
    val etiketler = listOf(R.string.nav_ana, R.string.nav_kategori, R.string.nav_favori, R.string.nav_istatistik)
    val ikonlar = listOf(AzimIkon.Ev, AzimIkon.Izgara, AzimIkon.Kalp, AzimIkon.Grafik)
    NavigationBar(containerColor = Renk.yuzey) {
        Sekme.entries.forEachIndexed { i, sekme ->
            NavigationBarItem(selected = secili == sekme, onClick = { secildi(sekme) },
                icon = { Icon(ikonlar[i], null, Modifier.size(22.dp)) }, label = { Text(stringResource(etiketler[i])) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Renk.accent, selectedTextColor = Renk.accent, indicatorColor = Renk.accentZemin, unselectedIconColor = Renk.metinIkincil, unselectedTextColor = Renk.metinIkincil))
        }
    }
}
