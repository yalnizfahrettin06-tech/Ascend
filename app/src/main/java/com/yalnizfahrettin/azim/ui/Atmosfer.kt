package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*

enum class AtmosferGrubu(val tr: String, val en: String) {
    MANZARA("Manzaralar", "Scenery"),
    EFSANE("Efsaneler", "Legends"),
    DOKU("Dokular", "Textures");
    fun ad(dil: String) = com.yalnizfahrettin.azim.data.Diller.metin(dil, tr, en)
}

enum class Atmosfer(val res: Int, val tr: String, val en: String, val grup: AtmosferGrubu = AtmosferGrubu.MANZARA) {
    IMPARATOR(R.drawable.warrior_emperor, "İmparator", "Emperor", AtmosferGrubu.EFSANE),
    DUELLO(R.drawable.warrior_duel, "Düello", "Duel", AtmosferGrubu.EFSANE),
    LEJYON(R.drawable.warrior_legion, "Lejyon", "Legion", AtmosferGrubu.EFSANE),
    GLADYATOR(R.drawable.warrior_gladiator, "Gladyatör", "Gladiator", AtmosferGrubu.EFSANE),
    TAHT(R.drawable.warrior_throne, "Taht", "Throne", AtmosferGrubu.EFSANE),
    SPARTA(R.drawable.warrior_spartan, "Spartalı", "Spartan", AtmosferGrubu.EFSANE),
    KOMUTAN(R.drawable.warrior_commander, "Atlı Komutan", "Mounted Commander", AtmosferGrubu.EFSANE),
    KALKAN(R.drawable.warrior_shieldwall, "Kalkan Duvarı", "Shield Wall", AtmosferGrubu.EFSANE),
    SAMPIYON(R.drawable.warrior_champion, "Arena Şampiyonu", "Arena Champion", AtmosferGrubu.EFSANE),
    KUSATMA(R.drawable.warrior_siege, "Kuşatma", "Siege", AtmosferGrubu.EFSANE),
    ZIRVE(R.drawable.scene_summit, "Zirve", "Summit"),
    DENIZ(R.drawable.scene_sea, "Gün batımı", "Sunset"),
    BILGELIK(R.drawable.scene_wisdom, "Bilgelik", "Wisdom"),
    ORMAN(R.drawable.scene_forest, "Orman", "Forest"),
    ARENA(R.drawable.scene_arena, "Arena", "Arena", AtmosferGrubu.EFSANE),
    KALE(R.drawable.scene_kale, "Kale", "Castle"),
    HISAR(R.drawable.scene_hisar, "Dağ hisarı", "Mountain fortress"),
    SOVALYE(R.drawable.scene_sovalye, "Şövalye", "Knight", AtmosferGrubu.EFSANE),
    CADI(R.drawable.scene_cadi, "Cadı", "Witch", AtmosferGrubu.EFSANE),
    BORDO_DOKU(R.drawable.scene_bordo_doku, "Bordo dokusu", "Burgundy texture", AtmosferGrubu.DOKU),
    TURKUAZ_DOKU(R.drawable.scene_turkuaz_doku, "Turkuaz dokusu", "Turquoise texture", AtmosferGrubu.DOKU),
    GRAFIT_DOKU(R.drawable.scene_grafit_doku, "Grafit", "Graphite", AtmosferGrubu.DOKU),
    LACIVERT_DOKU(R.drawable.scene_lacivert_doku, "Gece mavisi", "Midnight blue", AtmosferGrubu.DOKU),
    AMETIST_DOKU(R.drawable.scene_ametist_doku, "Ametist", "Amethyst", AtmosferGrubu.DOKU),
    ZEYTIN_DOKU(R.drawable.scene_zeytin_doku, "Zeytin", "Olive", AtmosferGrubu.DOKU),
    BAKIR_DOKU(R.drawable.scene_bakir_doku, "Bakır", "Copper", AtmosferGrubu.DOKU),
    KAR_MUHAFIZI(R.drawable.scene_kar_muhafizi, "Kar muhafızı", "Snow guardian", AtmosferGrubu.EFSANE),
    ATLI_YOLCU(R.drawable.scene_atli_yolcu, "Atlı yolcu", "Wandering rider", AtmosferGrubu.EFSANE),
    ORMAN_MUHAFIZI(R.drawable.scene_orman_muhafizi, "Orman muhafızı", "Forest guardian", AtmosferGrubu.EFSANE),
    TAS_SALON(R.drawable.scene_tas_salon, "Taş salon", "Stone hall", AtmosferGrubu.EFSANE),
    COL_YOLCUSU(R.drawable.scene_col_yolcusu, "Çöl yolcusu", "Desert traveller", AtmosferGrubu.EFSANE),
    KIYI_NOBETI(R.drawable.scene_kiyi_nobeti, "Kıyı nöbeti", "Coastal watch", AtmosferGrubu.EFSANE),
    KALE_NOBETI(R.drawable.scene_kale_nobeti, "Kale nöbeti", "Castle watch", AtmosferGrubu.EFSANE);
    fun ad(dil: String) = com.yalnizfahrettin.azim.data.Diller.metin(dil, tr, en)
    companion object {
        // Only this collection is offered by pickers. Old enum/resource IDs remain readable for saved designs.
        val gallery = listOf(IMPARATOR, DUELLO, LEJYON, GLADYATOR, TAHT, SPARTA, KOMUTAN, KALKAN, SAMPIYON, KUSATMA, ARENA, SOVALYE,
            ATLI_YOLCU, KAR_MUHAFIZI, KALE_NOBETI, KIYI_NOBETI, KALE, HISAR, TAS_SALON, BILGELIK)

        fun grup(anahtar: String?) = when (anahtar) {
            "olumlamalar" -> TAHT
            "iliskiler" -> KALKAN
            "zihin" -> KIYI_NOBETI
            "filozoflar", "tasavvuf", "inanc" -> BILGELIK
            "disiplin" -> LEJYON
            "ogrenme" -> BILGELIK
            "is" -> KOMUTAN
            else -> GLADYATOR
        }
    }
}

@Composable
fun AtmosferResmi(atmosfer: Atmosfer, modifier: Modifier = Modifier, karartma: Float = .20f) {
    Box(modifier) {
        Image(painterResource(atmosfer.res), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = karartma), Color.Black.copy(alpha = karartma + .08f), Color.Black.copy(alpha = .58f)))))
    }
}

@Composable
fun KucukBaslik(metin: String, modifier: Modifier = Modifier) {
    Text(metin, modifier, style = MaterialTheme.typography.titleMedium, color = Renk.metin)
}

@Composable
fun YuvarlakIkon(ikon: ImageVector, aciklama: String, tikla: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = Renk.yuzeyYuksek, shape = CircleShape, modifier = modifier) {
        IconButton(onClick = tikla) { Icon(ikon, aciklama, tint = Renk.metin, modifier = Modifier.size(22.dp)) }
    }
}

fun cevir(dil: String, tr: String, en: String) = com.yalnizfahrettin.azim.data.Diller.metin(dil, tr, en)
