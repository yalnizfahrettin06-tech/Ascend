package com.yalnizfahrettin.azim

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.core.HaptikSaglayici
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.notif.Bildirimler
import com.yalnizfahrettin.azim.notif.Planlayici
import com.yalnizfahrettin.azim.ui.*
import com.yalnizfahrettin.azim.widget.AzimWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var acilistakiKimlik by mutableStateOf<String?>(null)
    private var acilisSekmesi by mutableStateOf<Sekme?>(null)
    private lateinit var depo: Depo
    private var bildirimIzni by mutableStateOf(false)

    private val izinIstegi = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        bildirimIzni = Bildirimler.izinVarMi(this)
        Planlayici.yenidenKur(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        depo = Depo(applicationContext)
        Bildirimler.kanalKur(this)
        niyetiOku(intent)

        // Sürüm derlemesinde gerçek SDK bağlanana kadar ödül verilmez.
        val reklam: ReklamKapisi = VarsayilanKapi()

        Planlayici.yenidenKur(this)
        lifecycleScope.launch { AzimWidget.tazele(applicationContext) }

        setContent {
            val tema by depo.tema.collectAsStateWithLifecycle(TemaModu.SISTEM)
            val dinamik by depo.dinamikRenk.collectAsStateWithLifecycle(false)
            val haptik by depo.haptikAcik.collectAsStateWithLifecycle(true)
            val palet by depo.palet.collectAsStateWithLifecycle(com.yalnizfahrettin.azim.core.Palet.KUM)
            val dil by depo.dil.collectAsStateWithLifecycle("tr")
            val base = LocalContext.current
            val configuration = LocalConfiguration.current
            val localizedConfiguration = remember(dil, configuration) { Configuration(configuration).apply { setLocale(Locale.forLanguageTag(dil)) } }
            val localizedContext = remember(base, localizedConfiguration) {
                val resources = base.createConfigurationContext(localizedConfiguration).resources
                object : ContextWrapper(base) { override fun getResources() = resources }
            }
            CompositionLocalProvider(LocalContext provides localizedContext, LocalConfiguration provides localizedConfiguration) {
            AzimTema(modu = tema, palet = palet, dinamik = dinamik) {
                HaptikSaglayici(haptik) {
                    Uygulama(
                        depo = depo,
                        reklam = reklam,
                        acilistakiKimlik = acilistakiKimlik,
                        acilisSekmesi = acilisSekmesi,
                        izinIste = ::bildirimIzniniIste,
                        bildirimIzni = bildirimIzni,
                    )
                }
            }
        }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::depo.isInitialized) {
            bildirimIzni = Bildirimler.izinVarMi(this)
            Planlayici.yenidenKur(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        niyetiOku(intent)
    }

    private fun niyetiOku(i: Intent?) {
        acilistakiKimlik = i?.getStringExtra(Bildirimler.EXTRA_KIMLIK)
        acilisSekmesi = when (i?.action) {
            "com.yalnizfahrettin.azim.FAVORILER" -> Sekme.FAVORI
            else -> null
        }
    }

    /**
     * Bildirim izni artık onboarding'in son adımında, kullanıcı sıklığı
     * seçtikten HEMEN SONRA isteniyor (rapor 1.4). Önceki sürümde uygulama
     * açılır açılmaz bağlamsız soruluyordu.
     */
    private fun bildirimIzniniIste() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            bildirimIzni = Bildirimler.izinVarMi(this)
            if (!bildirimIzni) com.yalnizfahrettin.azim.notif.TeslimatYardimi.bildirimAyarlariniAc(this)
            return
        }
        val durum = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (durum != PackageManager.PERMISSION_GRANTED) {
            izinIstegi.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
