package com.yalnizfahrettin.azim.ui

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.PhaseCopy

@Composable
fun HelpAndData(dil: String) {
    val ctx = LocalContext.current
    var panel by rememberSaveable { mutableStateOf<String?>(null) }
    var failed by remember { mutableStateOf(false) }
    fun copy(key: String) = PhaseCopy.text(key,dil)
    Column(Modifier.fillMaxWidth()) {
        listOf("help","privacy","sources","licenses","reset").forEach { key ->
            TextButton(onClick = { panel = key; failed = false },modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("settings-$key")) {
                Text(copy(key),Modifier.fillMaxWidth())
            }
        }
    }
    panel?.let { key ->
        val licenseText = remember(key) {
            if(key == "licenses") listOf("Inter-OFL.txt","Lora-OFL.txt").joinToString("\n\n") { name ->
                ctx.assets.open(name).bufferedReader().use { it.readText() }
            } else ""
        }
        AlertDialog(onDismissRequest = { panel = null },
            title = { Text(copy(key)) },
            text = { Column(Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                Text(when(key) {
                    "licenses" -> licenseText
                    "reset" -> copy("resetBody")
                    "privacy" -> copy("privacyBody")
                    "sources" -> copy("sourcesBody")
                    else -> copy("helpBody")
                },fontSize = 14.sp,lineHeight = 22.sp)
                if(failed) Text(cevir(dil,"Kaydedilemedi. Yeniden dene.","Could not save. Try again."),color = MaterialTheme.colorScheme.error)
            } },
            confirmButton = { TextButton(onClick = {
                if(key == "reset") {
                    // Android clears app storage and terminates the process only after explicit confirmation.
                    failed = !(ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                } else panel = null
            },modifier = Modifier.testTag("data-confirm")) { Text(if(key == "reset") copy("delete") else cevir(dil,"Kapat","Close")) } },
            dismissButton = { if(key == "reset") TextButton(onClick = { panel = null },modifier = Modifier.testTag("data-cancel")) { Text(copy("cancel")) } })
    }
}
