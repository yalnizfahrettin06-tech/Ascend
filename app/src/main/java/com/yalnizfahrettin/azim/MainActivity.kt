package com.yalnizfahrettin.azim

import android.Manifest
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.core.HaptikSaglayici
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.core.asTemaModu
import com.yalnizfahrettin.azim.notif.Notifier
import com.yalnizfahrettin.azim.notif.TeslimatYardimi
import com.yalnizfahrettin.azim.ui.AppViewModel
import com.yalnizfahrettin.azim.ui.AscendApp
import com.yalnizfahrettin.azim.ui.LocalLanguage
import com.yalnizfahrettin.azim.ui.OpenRequest
import com.yalnizfahrettin.azim.ui.Tab
import com.yalnizfahrettin.azim.ui.YouSection
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()
    private var request by mutableStateOf<OpenRequest?>(null)

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        vm.refreshSystemState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Keep the splash until the stored theme and language are known: no light/Turkish flash.
        installSplashScreen().setKeepOnScreenCondition { vm.state.value == null }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) readIntent(intent)

        setContent {
            val state by vm.state.collectAsStateWithLifecycle()
            val language = state?.language ?: "en"
            val base = LocalContext.current
            val configuration = LocalConfiguration.current
            val localizedConfiguration = remember(language, configuration) {
                Configuration(configuration).apply { setLocale(Locale.forLanguageTag(language)) }
            }
            val localizedContext = remember(base, localizedConfiguration) {
                val resources = base.createConfigurationContext(localizedConfiguration).resources
                object : ContextWrapper(base) { override fun getResources() = resources }
            }
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfiguration,
                LocalLanguage provides language,
            ) {
                AzimTema(modu = state?.themeMode?.asTemaModu() ?: TemaModu.SISTEM) {
                    HaptikSaglayici(state?.haptics ?: true) {
                        AscendApp(vm, request, ::requestNotificationPermission)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshSystemState()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        readIntent(intent)
    }

    private fun readIntent(intent: Intent?) {
        val nonce = System.nanoTime()
        request = when (intent?.action) {
            Notifier.ACTION_OPEN_QUOTE -> OpenRequest(quoteId = intent.getStringExtra(Notifier.EXTRA_QUOTE_ID), tab = Tab.TODAY, nonce = nonce)
            ACTION_SHORTCUT_TODAY -> OpenRequest(tab = Tab.TODAY, nonce = nonce)
            ACTION_SHORTCUT_SAVED -> OpenRequest(tab = Tab.YOU, section = YouSection.SAVED, nonce = nonce)
            else -> request
        }
    }

    /** Asks once in context; after a permanent denial the system settings page is the only way. */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (!Notifier.canPost(this)) TeslimatYardimi.bildirimAyarlariniAc(this)
            return
        }
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        when {
            granted && !Notifier.canPost(this) -> TeslimatYardimi.kanalAyarlariniAc(this)
            granted -> vm.refreshSystemState()
            else -> {
                val history = getSharedPreferences("notification_permission", MODE_PRIVATE)
                val askedBefore = history.getBoolean("asked", false)
                if (askedBefore && !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    TeslimatYardimi.bildirimAyarlariniAc(this)
                } else {
                    history.edit().putBoolean("asked", true).apply()
                    permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    companion object {
        const val ACTION_SHORTCUT_TODAY = "com.yalnizfahrettin.azim.TODAY"
        const val ACTION_SHORTCUT_SAVED = "com.yalnizfahrettin.azim.SAVED"
    }
}
