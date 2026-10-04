package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import java.util.Locale

/** Large screen title with an optional leading back button and trailing action. */
@Composable
fun ScreenHeader(title: String, subtitle: String? = null, back: (() -> Unit)? = null, action: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(start = if (back != null) 8.dp else 24.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (back != null) IconButton(onClick = back, modifier = Modifier.testTag("header-back")) {
            Icon(AzimIkon.Geri, stringResource(R.string.action_back), tint = Renk.metin)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = Renk.metin, modifier = Modifier.semantics { heading() })
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil) }
        }
        action?.invoke()
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(Locale.getDefault()), modifier = modifier.padding(top = 8.dp, bottom = 8.dp).semantics { heading() },
        style = MaterialTheme.typography.labelMedium, color = Renk.metinIkincil, letterSpacing = 1.sp)
}

@Composable
fun AscendCard(modifier: Modifier = Modifier, padding: PaddingValues = PaddingValues(18.dp), content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), color = Renk.yuzey, shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun SwitchRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit, subtitle: String? = null, enabled: Boolean = true, tag: String = "") {
    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp)
        .toggleable(checked, enabled = enabled, role = Role.Switch, onValueChange = onChange).testTag(tag),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Renk.metin)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil) }
        }
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
fun NavRow(title: String, value: String? = null, icon: ImageVector? = null, tag: String = "", onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).clickable(role = Role.Button, onClick = onClick).testTag(tag)
        .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        icon?.let { Icon(it, null, Modifier.size(22.dp), tint = Renk.metinIkincil) }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Renk.metin)
            value?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil) }
        }
        Icon(AzimIkon.Ileri, null, Modifier.size(18.dp), tint = Renk.metinIkincil)
    }
}

private fun Modifier.clip(RoundedCornerShape(16.dp)) = this.then(Modifier.background(androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(16.dp)))

/** Accessible − value + control. */
@Composable
fun Stepper(value: Int, range: IntRange, label: String, onChange: (Int) -> Unit, format: (Int) -> String = { it.toString() }, tag: String = "") {
    val description = stringResource(R.string.a11y_value, label, format(value))
    Row(Modifier.semantics(mergeDescendants = false) { stateDescription = description }.testTag(tag),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilledTonalIconButton(onClick = { onChange(value - 1) }, enabled = value > range.first,
            modifier = Modifier.size(44.dp).semantics { contentDescription = "$label −" }) { Icon(AzimIkon.Eksi, null) }
        Text(format(value), style = MaterialTheme.typography.titleLarge, color = Renk.metin, textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 56.dp).testTag("$tag-value"))
        FilledTonalIconButton(onClick = { onChange(value + 1) }, enabled = value < range.last,
            modifier = Modifier.size(44.dp).semantics { contentDescription = "$label +" }) { Icon(AzimIkon.Arti, null) }
    }
}

fun hourLabel(hour: Int): String = "%02d:00".format(Locale.ROOT, hour % 24)

/** Segmented single choice. */
@Composable
fun <T> Segmented(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit, tag: String = "") {
    Row(Modifier.fillMaxWidth().background(Renk.yuzeyYuksek, RoundedCornerShape(16.dp)).padding(4.dp).selectableGroup().testTag(tag),
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { (value, label) ->
            val on = value == selected
            Box(Modifier.weight(1f).heightIn(min = 44.dp)
                .background(if (on) Renk.zemin else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(12.dp))
                .selectable(on, role = Role.RadioButton) { onSelect(value) }.testTag("$tag-$value"),
                contentAlignment = Alignment.Center) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = if (on) Renk.metin else Renk.metinIkincil,
                    fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp))
            }
        }
    }
}

/** Selectable topic pill; locked topics show a lock and stay tappable for the offer. */
@Composable
fun TopicChip(label: String, selected: Boolean, locked: Boolean = false, tag: String = "", onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(50),
        color = if (selected) Renk.accentZemin else Renk.zemin,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) Renk.accent else Renk.kenarlik),
        modifier = Modifier.heightIn(min = 44.dp).testTag(tag).semantics { this.selected = selected }) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (selected) Icon(AzimIkon.Tik, null, Modifier.size(16.dp), tint = Renk.accent)
            if (locked) Icon(AzimIkon.Kilit, stringResource(R.string.pro_locked), Modifier.size(14.dp), tint = Renk.metinIkincil)
            Text(label, style = MaterialTheme.typography.labelLarge, color = Renk.metin)
        }
    }
}

/** A calm but visible state message with one action. */
@Composable
fun StatusBanner(icon: ImageVector, text: String, action: String?, tag: String = "", onAction: () -> Unit = {}) {
    Surface(color = Renk.accentZemin, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().testTag(tag)) {
        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = Renk.accent)
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Renk.metin)
            action?.let { TextButton(onClick = onAction) { Text(it, fontWeight = FontWeight.SemiBold) } }
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, tag: String = "") {
    Button(onClick = onClick, enabled = enabled, shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp).testTag(tag)) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun StatTile(value: String, label: String, icon: ImageVector? = null, modifier: Modifier = Modifier) {
    Surface(color = Renk.yuzey, shape = RoundedCornerShape(18.dp), modifier = modifier) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                icon?.let { Icon(it, null, Modifier.size(18.dp), tint = Renk.accent) }
                Text(value, style = MaterialTheme.typography.titleLarge, color = Renk.metin)
            }
            Text(label, style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
        }
    }
}

@Composable
fun Dot(modifier: Modifier = Modifier) = Box(modifier.size(4.dp).background(Renk.metinIkincil, CircleShape))
