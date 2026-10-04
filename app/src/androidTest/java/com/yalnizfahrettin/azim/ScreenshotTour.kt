package com.yalnizfahrettin.azim

import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.core.HaptikSaglayici
import com.yalnizfahrettin.azim.core.TemaModu
import com.yalnizfahrettin.azim.data.*
import com.yalnizfahrettin.azim.ui.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Locale

/** Renders every main screen with realistic state and stores PNGs for visual review. */
@RunWith(AndroidJUnit4::class)
class ScreenshotTour {
    @get:Rule val rule = createComposeRule()

    private val today = LocalDate.now()
    private val sample = UserState(
        onboarded = true, language = "tr", topics = setOf("motivasyon", "ozsefkat", "derin_odak"),
        favorites = Sozler.kategoriden("motivasyon").take(4).map { it.kimlik },
        deliveries = Sozler.kategoriden("ozsefkat").take(3).map { Delivery(today, it.kimlik, false) },
        streak = 5, bestStreak = 9, lastActiveDay = today, readToday = 3, readDay = today, totalRead = 42,
    )

    private fun shoot(name: String, dark: Boolean = false, language: String = "tr", fontScale: Float = 1f, node: String? = null, content: @Composable () -> Unit) {
        rule.setContent { Localized(language) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            CompositionLocalProvider(androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(density.density, fontScale)) {
                AzimTema(if (dark) TemaModu.KARANLIK else TemaModu.AYDINLIK) { HaptikSaglayici(true) { content() } }
            }
        } }
        rule.waitForIdle()
        save(name, node)
    }

    private fun save(name: String, node: String? = null) {
        val target = if (node != null) rule.onNodeWithTag(node, useUnmergedTree = true) else rule.onRoot()
        val bitmap = target.captureToImage().asAndroidBitmap()
        val dir = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        val file = File(dir, "$name.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        // The app is uninstalled after the run; keep a copy where adb can pull it.
        val shell = InstrumentationRegistry.getInstrumentation().uiAutomation
        fun run(command: String) = android.os.ParcelFileDescriptor.AutoCloseInputStream(shell.executeShellCommand(command)).use { it.readBytes() }
        run("mkdir -p /data/local/tmp/ascend-screens")
        run("cp ${file.absolutePath} /data/local/tmp/ascend-screens/")
    }

    @Composable
    private fun Localized(language: String, content: @Composable () -> Unit) {
        val base = LocalContext.current
        val config = Configuration(LocalConfiguration.current).apply { setLocale(Locale.forLanguageTag(language)) }
        val context = remember(language) {
            val res = base.createConfigurationContext(config).resources
            object : ContextWrapper(base) { override fun getResources() = res }
        }
        CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides config, LocalLanguage provides language, content = content)
    }

    private val next = ZonedDateTime.now().withHour(15).withMinute(0)

    @Test fun today() = shoot("01-today") {
        TodayScreen(sample, HomeFeed.build(sample, today), true, next, remember { SnackbarHostState() }, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})
    }

    @Test fun todayDarkPermissionOff() = shoot("02-today-dark-permission", dark = true) {
        TodayScreen(sample, HomeFeed.build(sample, today), false, null, remember { SnackbarHostState() }, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})
    }

    @Test fun todayArtBackground() = shoot("03-today-art") {
        val s = sample.copy(pro = true, background = AnaTemalar.emperor.id)
        TodayScreen(s, HomeFeed.build(s, today), true, next, remember { SnackbarHostState() }, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})
    }

    @Test fun explore() = shoot("04-explore") { ExploreScreen(sample, {}, {}, {}, {}) }

    @Test fun reminders() = shoot("05-reminders") {
        RemindersScreen(sample, true, next, {}, {}, {}, {}, {}, {}, {})
    }

    @Test fun remindersDark() = shoot("06-reminders-dark", dark = true) {
        RemindersScreen(sample, false, null, {}, {}, {}, {}, {}, {}, {})
    }

    @Test fun you() = shoot("07-you") {
        YouScreen(sample, YouSection.SAVED, {}, {}, {}, {}, { _, _ -> }, {}, {}, {})
    }

    @Test fun settings() = shoot("08-settings") { SettingsScreen(sample, {}, {}, {}, {}, {}, {}, {}) }

    @Test fun onboardingTour() {
        rule.setContent { Localized("tr") { AzimTema(TemaModu.AYDINLIK) { OnboardingScreen("tr", false, {}, {}) { _, _ -> } } } }
        save("10-onboarding-welcome")
        rule.onNodeWithTag("onboarding-next").performClick(); rule.waitForIdle()
        rule.onNodeWithTag("setup-topic-motivasyon").performScrollTo().performClick()
        rule.onNodeWithTag("setup-topic-ozsefkat").performScrollTo().performClick(); rule.waitForIdle()
        save("11-onboarding-topics")
        rule.onNodeWithTag("onboarding-next").performClick(); rule.waitForIdle()
        save("12-onboarding-rhythm")
        rule.onNodeWithTag("onboarding-next").performClick(); rule.waitForIdle()
        save("13-onboarding-permission")
        rule.onNodeWithTag("onboarding-skip").performClick(); rule.waitForIdle()
        save("14-onboarding-ready")
    }

    @Test fun englishToday() = shoot("20-today-en", language = "en") {
        val s = sample.copy(language = "en")
        TodayScreen(s, HomeFeed.build(s, today), true, next, remember { SnackbarHostState() }, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})
    }

    @Test fun todayLargeText() = shoot("30-today-large-text", fontScale = 1.6f) {
        TodayScreen(sample, HomeFeed.build(sample, today), false, next, remember { SnackbarHostState() }, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})
    }

    @Test fun remindersLargeText() = shoot("31-reminders-large-text", fontScale = 1.6f) {
        RemindersScreen(sample, true, next, {}, {}, {}, {}, {}, {}, {})
    }

    @Test fun germanSettings() = shoot("32-settings-de", language = "de") { SettingsScreen(sample.copy(language = "de"), {}, {}, {}, {}, {}, {}, {}) }

    @Test fun germanToday() = shoot("33-today-de", language = "de") {
        val s = sample.copy(language = "de")
        TodayScreen(s, HomeFeed.build(s, today), true, next, remember { SnackbarHostState() }, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})
    }

    @Test fun classicQuoteReader() = shoot("40-reader-classic", node = "quote-reader") {
        SozOkuyucu(Sozler.kimlikten("pd_marcus_04")!!, "tr", false, {}, {}, {})
    }

    @Test fun thinkerTopicExplore() = shoot("41-explore-search", language = "tr") {
        ExploreScreen(sample.copy(pro = true), {}, {}, {}, {})
    }

    @Test fun proSheet() = shoot("42-pro", node = "pro-sheet") { ProEkrani("tr", false, false, null, {}, {}) }

    @Test fun series() = shoot("43-series") {
        KisaSerilerEkrani("tr", emptyMap(), emptySet(), {}, {}, {}, {}, {})
    }

    @Test fun appearance() = shoot("44-appearance") { GorunumEkrani("tr", AnaTemalar.white.id, false, {}, {}) }
}
