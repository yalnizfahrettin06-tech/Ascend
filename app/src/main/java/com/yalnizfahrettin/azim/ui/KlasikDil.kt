package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*





@Composable
fun EditoryalBaslik(etiket: String, baslik: String, aciklama: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.padding(top = 7.dp).width(22.dp).height(1.dp).background(Renk.metinIkincil))
            Text(etiket, color = Renk.metinIkincil, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.3.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(baslik, color = Renk.metin, fontFamily = LoraSerif, fontSize = 32.sp, lineHeight = 40.sp,
            modifier = Modifier.semantics { heading() })
        if (!aciklama.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(aciklama, color = Renk.metinIkincil, fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}
