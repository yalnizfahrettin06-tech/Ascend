package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

/** Editorial artwork is independent of the user's home theme and access level. */
object EditorialArt {
    val groups = mapOf(
        "olumlamalar" to R.drawable.category_ozsefkat,
        "azim" to R.drawable.warrior_shieldwall,
        "disiplin" to R.drawable.category_derin_odak,
        "cesaret" to R.drawable.warrior_champion,
        Kategoriler.DUSUNURLER to R.drawable.scene_wisdom,
        "dogu_gelenegi" to R.drawable.category_zen,
        "inanc" to R.drawable.category_dua,
        "spor" to R.drawable.category_antrenman,
        "is" to R.drawable.category_girisimcilik,
        "iliskiler" to R.drawable.category_arkadaslik,
        "zihin" to R.drawable.category_huzur,
        "ogrenme" to R.drawable.category_okumak,
    )
    fun group(key: String) = groups[key] ?: R.drawable.category_azim
    fun saved(category: String): Int = KategoriResimleri.all[category] ?: when(Kategoriler.bul(category)?.grup) {
        "filozoflar" -> group(Kategoriler.DUSUNURLER)
        "tasavvuf" -> group("dogu_gelenegi")
        else -> group(Kategoriler.bul(category)?.grup.orEmpty())
    }
    fun series(id: String) = when(id) {
        "restart" -> R.drawable.warrior_emperor
        "kindness" -> R.drawable.category_ozsefkat
        "steps" -> R.drawable.category_azim
        else -> R.drawable.category_derin_odak
    }
}

@Composable
fun EditorialPhoto(resource: Int, modifier: Modifier = Modifier) {
    TemaZemini(AnaTema("editorial-$resource","","",true,false,resource),modifier,veil = .02f,thumbnail = true,previewSize = 640)
}

@Composable
fun EditorialCover(title: String, detail: String, resource: Int, modifier: Modifier = Modifier, open: () -> Unit) {
    Surface(onClick = open,modifier = modifier,shape = RoundedCornerShape(20.dp),color = Renk.yuzey) {
        Column {
            EditorialPhoto(resource,Modifier.fillMaxWidth().aspectRatio(1.65f))
            Column(Modifier.padding(14.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title,color = Renk.metin,fontSize = 16.sp,lineHeight = 21.sp,fontWeight = FontWeight.Medium)
                Text(detail,color = Renk.metinIkincil,fontSize = 12.sp,lineHeight = 17.sp)
            }
        }
    }
}
