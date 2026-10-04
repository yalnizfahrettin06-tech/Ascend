package com.yalnizfahrettin.azim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yalnizfahrettin.azim.BuildConfig
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.*

@Composable
fun SettingsScreen(
    state: UserState,
    back: () -> Unit,
    setLanguage: (String) -> Unit,
    setTheme: (ThemeMode) -> Unit,
    setHaptics: (Boolean) -> Unit,
    openAppearance: () -> Unit,
    openPro: () -> Unit,
    restoreHidden: () -> Unit,
) {
    val language = state.language
    Column(Modifier.fillMaxSize().background(Renk.zemin).statusBarsPadding().navigationBarsPadding().testTag("settings")) {
        ScreenHeader(stringResource(R.string.settings_title), back = back)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle(stringResource(R.string.settings_language))
            Segmented(Languages.options, language, setLanguage, tag = "settings-language")
            Text(stringResource(R.string.settings_language_note), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)

            SectionTitle(stringResource(R.string.settings_theme))
            Segmented(listOf(ThemeMode.SYSTEM to stringResource(R.string.settings_theme_system),
                ThemeMode.LIGHT to stringResource(R.string.settings_theme_light),
                ThemeMode.DARK to stringResource(R.string.settings_theme_dark)), state.themeMode, setTheme, tag = "settings-theme")

            AscendCard {
                NavRow(stringResource(R.string.settings_appearance), AnaTemalar.find(state.background).label(language), AzimIkon.Izgara, "settings-appearance", openAppearance)
                HorizontalDivider(color = Renk.kenarlik)
                SwitchRow(stringResource(R.string.settings_haptics), state.haptics, setHaptics, tag = "settings-haptics")
            }

            SectionTitle("Ascend Pro")
            AscendCard {
                NavRow(stringResource(if (state.pro) R.string.settings_pro_on else R.string.settings_pro_off),
                    stringResource(R.string.settings_pro_body), AzimIkon.Kilit, "settings-pro", openPro)
            }

            SectionTitle(stringResource(R.string.settings_content))
            AscendCard {
                if (state.hidden.isEmpty()) Text(stringResource(R.string.settings_hidden_none), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                else NavRow(pluralStringResource(R.plurals.settings_hidden_restore, state.hidden.size, state.hidden.size), null, AzimIkon.Yenile, "settings-restore-hidden", restoreHidden)
            }

            SectionTitle(stringResource(R.string.settings_about))
            AscendCard {
                Text(stringResource(R.string.settings_about_body), style = MaterialTheme.typography.bodySmall, color = Renk.metinIkincil)
                Text(stringResource(R.string.settings_version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.labelMedium, color = Renk.metin)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
