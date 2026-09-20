package com.yalnizfahrettin.azim.ui

import com.yalnizfahrettin.azim.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Large type uses an explicit vertical list rather than cutting off long tab labels. */
@Composable
fun ChoiceTabs(labels: List<String>, selected: Int, select: (Int) -> Unit, tags: List<String>) {
    val large = LocalDensity.current.fontScale > 1.3f
    val tab: @Composable (Int, Modifier) -> Unit = { i, modifier ->
        Surface(onClick = { select(i) },color = if(selected == i) Renk.metin else Renk.yuzey,
            border = BorderStroke(if(selected == i) 2.dp else 1.dp,if(selected == i) Renk.metin else Renk.kenarlik),
            shape = RoundedCornerShape(12.dp),modifier = modifier.heightIn(min = 48.dp).testTag(tags[i]).semantics { role = Role.Tab; this.selected = selected == i }) {
            Row(Modifier.padding(horizontal = 10.dp,vertical = 12.dp),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Center) {
                if(selected == i) { Icon(AzimIkon.Tik,null,Modifier.size(15.dp),tint = Renk.zemin); Spacer(Modifier.width(5.dp)) }
                Text(labels[i],Modifier.weight(1f,fill = false),color = if(selected == i) Renk.zemin else Renk.metin,fontSize = 13.sp,lineHeight = 18.sp)
            }
        }
    }
    if(large) Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp),verticalArrangement = Arrangement.spacedBy(6.dp)) { labels.indices.forEach { tab(it,Modifier.fillMaxWidth()) } }
    else Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(IntrinsicSize.Min),horizontalArrangement = Arrangement.spacedBy(8.dp)) { labels.indices.forEach { tab(it,Modifier.weight(1f).fillMaxHeight()) } }
}
