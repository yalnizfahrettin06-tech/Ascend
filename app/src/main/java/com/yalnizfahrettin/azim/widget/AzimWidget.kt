package com.yalnizfahrettin.azim.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.glance.appwidget.state.updateAppWidgetState
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.data.PersonalPlan
import kotlinx.coroutines.flow.first

/*
 * EV EKRANI WIDGET'I (rapor 3.1)
 *
 * Eski sürümde ÜÇ paralel widget çizim yolu vardı — RemoteViews tabanlı
 * WidgetBuilder, ui/widget/AzimWidget.kt ve widget/ui/WidgetContent.kt.
 * Üçü birbirini görmüyordu; onboarding önizlemesi ile ana ekrandaki gerçek
 * widget farklı kodlardan çiziliyordu. 1.0.0'da bu karmaşayı temizlerken
 * widget'ı tamamen kaldırmıştım.
 *
 * Burada TEK yol var: Glance. Aynı composable üç boyutta da (SizeMode.Exact)
 * kendini uyarlıyor — ayrı mini/normal/geniş kodu yok, dolayısıyla
 * birbirinden ayrışacak ikinci bir yol da yok.
 *
 * Bir söz uygulaması için ev ekranı varlığı çekirdek elde tutma aracıdır:
 * kullanıcı uygulamayı açmadan sözü görür.
 */
class AzimWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Icerik() }
    }

    @Composable
    private fun Icerik() {
        val durum = currentState<androidx.datastore.preferences.core.Preferences>()
        val soz = durum[SOZ] ?: ""
        val yazar = durum[YAZAR] ?: ""

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF141414)))
                .cornerRadius(16.dp)
                .padding(16.dp)
                .clickable(actionRunCallback<YenileEylemi>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.Start,
        ) {
            if (soz.isBlank()) {
                Text(
                    "ASCEND",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF9FADBC)),
                        fontFamily = FontFamily.Serif,
                    ),
                )
            } else {
                Text(
                    soz,
                    maxLines = 5,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFF2F5F8)),
                        fontFamily = FontFamily.Serif,
                    ),
                )
                Text(
                    "— $yazar",
                    modifier = GlanceModifier.padding(top = 8.dp),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF9FADBC)),
                        fontFamily = FontFamily.Serif,
                    ),
                )
            }
        }
    }

    companion object {
        val SOZ = stringPreferencesKey("widget_soz")
        val YAZAR = stringPreferencesKey("widget_yazar")

        /** Tüm widget örneklerine yeni bir söz yazar. */
        suspend fun tazele(ctx: Context) {
            val depo = Depo(ctx)
            val dil = depo.dil.first()
            val secili = depo.secili.first()
            val soz = PersonalPlan.notification(depo.personalProfile.first(), secili, depo.acik.first(),
                dil, depo.gecmis.first(), depo.sonBildirimKimlik.first(), depo.hiddenQuotes.first())?.soz
            val widget = AzimWidget()
            androidx.glance.appwidget.GlanceAppWidgetManager(ctx)
                .getGlanceIds(AzimWidget::class.java)
                .forEach { id ->
                    updateAppWidgetState(ctx, id) { p ->
                        p[SOZ] = soz?.metin(dil) ?: if(dil == "tr") "Uygulamadan içerik tercihlerini düzenleyebilirsin." else "Adjust content preferences in the app."
                        p[YAZAR] = soz?.sunumEtiketi(dil) ?: "Ascend"
                    }
                }
            widget.updateAll(ctx)
        }
    }
}

/** Widget'a dokununca yeni söz. */
class YenileEylemi : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        AzimWidget.tazele(context)
    }
}

class AzimWidgetSaglayici : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AzimWidget()
}
