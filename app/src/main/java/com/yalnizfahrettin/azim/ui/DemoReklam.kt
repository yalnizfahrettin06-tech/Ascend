package com.yalnizfahrettin.azim.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.Collections
import java.util.IdentityHashMap

/**
 * Temporary demo only: an accepted browser launch followed by leaving and returning
 * to Ascend unlocks one collection. This does not verify an ad view or a web page.
 */
@Stable
internal class DemoReklamDurumu(
    grup: String? = null,
    acildi: Boolean = false,
    durakladi: Boolean = false,
) {
    var bekleyenGrup by mutableStateOf(grup)
        private set
    private var acilisBasarili by mutableStateOf(acildi)
    private var uygulamaDurakladi by mutableStateOf(durakladi)

    fun hazirla(grup: String): Boolean {
        if (bekleyenGrup != null || grup.isBlank()) return false
        bekleyenGrup = grup
        acilisBasarili = false
        uygulamaDurakladi = false
        return true
    }

    fun acilisSonucu(basarili: Boolean) {
        if (bekleyenGrup == null) return
        if (basarili) acilisBasarili = true else temizle()
    }

    fun olay(olay: Lifecycle.Event): String? {
        if (olay == Lifecycle.Event.ON_PAUSE && bekleyenGrup != null) {
            uygulamaDurakladi = true
        }
        if (olay == Lifecycle.Event.ON_RESUME && acilisBasarili && uygulamaDurakladi) {
            val grup = bekleyenGrup
            // Consume before invoking persistence: repeated resumes cannot grant twice.
            temizle()
            return grup
        }
        return null
    }

    private fun temizle() {
        bekleyenGrup = null
        acilisBasarili = false
        uygulamaDurakladi = false
    }

    companion object {
        val Saver = listSaver<DemoReklamDurumu, Any>(
            save = { listOf(it.bekleyenGrup.orEmpty(), it.acilisBasarili, it.uygulamaDurakladi) },
            restore = {
                DemoReklamDurumu(
                    grup = (it[0] as String).takeIf(String::isNotEmpty),
                    acildi = it[1] as Boolean,
                    durakladi = it[2] as Boolean,
                )
            },
        )
    }
}

/** ContextThemeWrapper and Compose wrappers must not hide the hosting Activity. */
internal fun Context.demoActivity(): Activity? {
    var simdiki: Context = this
    val gorulen = Collections.newSetFromMap(IdentityHashMap<Context, Boolean>())
    while (gorulen.add(simdiki)) {
        if (simdiki is Activity) return simdiki.takeUnless { it.isFinishing || it.isDestroyed }
        simdiki = (simdiki as? ContextWrapper)?.baseContext ?: return null
    }
    return null
}

internal fun demoSayfasiniAc(
    context: Context,
    baslat: (Activity, Intent) -> Unit = { activity, intent -> activity.startActivity(intent) },
): Boolean {
    val activity = context.demoActivity() ?: return false
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
        .addCategory(Intent.CATEGORY_BROWSABLE)
    return try {
        baslat(activity, intent)
        true
    } catch (_: RuntimeException) {
        // No browser, blocked intent, or another launch failure must leave the lock intact.
        false
    }
}

@Composable
internal fun rememberDemoReklam(
    acildi: (String) -> Unit,
    hata: () -> Unit,
    baslat: (Context) -> Boolean = { demoSayfasiniAc(it) },
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
): (String) -> Boolean {
    val context = LocalContext.current
    val durum = rememberSaveable(saver = DemoReklamDurumu.Saver) { DemoReklamDurumu() }
    val guncelAcildi by rememberUpdatedState(acildi)
    val guncelHata by rememberUpdatedState(hata)
    val guncelBaslat by rememberUpdatedState(baslat)

    DisposableEffect(lifecycle, durum) {
        val gozlemci = LifecycleEventObserver { _, olay ->
            durum.olay(olay)?.let { guncelAcildi(it) }
        }
        lifecycle.addObserver(gozlemci)
        onDispose { lifecycle.removeObserver(gozlemci) }
    }

    return remember(context, durum) {
        { grup ->
            if (!durum.hazirla(grup)) {
                false
            } else {
                val basarili = try { guncelBaslat(context) } catch (_: RuntimeException) { false }
                durum.acilisSonucu(basarili)
                if (!basarili) guncelHata()
                basarili
            }
        }
    }
}
