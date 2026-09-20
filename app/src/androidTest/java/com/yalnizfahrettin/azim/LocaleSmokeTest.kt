package com.yalnizfahrettin.azim

import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.Onboarding
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LocaleSmokeTest {
    @get:Rule val compose=createAndroidComposeRule<ComponentActivity>()

    @Test fun sevenLanguagePickerSwitchesAndRussianIntroductionRenders() {
        var lang by mutableStateOf("tr")
        compose.setContent { AzimTema(modu=TemaModu.AYDINLIK) {
            Onboarding(dil=lang, languageChanged={lang=it}, bitir={_,_,_,_,_->})
        } }
        listOf("pt","de","fr","it","ru").forEach { code ->
            compose.onNodeWithTag("language-$code").performScrollTo().performClick().assertIsSelected()
            compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
            compose.onNodeWithText(Diller.metin(code,"Devam","Continue")).assertIsDisplayed()
        }
        ekranKaydet("locale-picker-russian")
        compose.onNodeWithTag("onboarding-next").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("onboarding-next").assertIsDisplayed()
        ekranKaydet("locale-introduction-russian")
    }

    @Test fun localePreferencesPersistAndAndroidResourcesMatchAllFiveLanguages() = runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val file=File(context.cacheDir,"locale-${System.nanoTime()}.preferences_pb")
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        val store=PreferenceDataStoreFactory.create(scope=scope,produceFile={file})
        val depot=Depo(context,store)
        try {
            listOf("pt","de","fr","it","ru").forEach { lang ->
                depot.dilAyarla(lang)
                assertEquals(lang,Depo(context,store).dil.first())
                val localized=context.createConfigurationContext(android.content.res.Configuration(context.resources.configuration).apply {
                    setLocale(java.util.Locale.forLanguageTag(lang))
                })
                assertEquals(Diller.metin(lang,"Ayarlar","Settings"),localized.getString(R.string.ayarlar))
                val quote=Sozler.tumu().first()
                assertNotEquals(quote.en,quote.metin(lang))
            }
            depot.completePersonalPlan(PersonalProfile().choose("language","ru"),false)
            assertEquals("ru",depot.dil.first())
        } finally { scope.cancel(); file.delete() }
    }
}
