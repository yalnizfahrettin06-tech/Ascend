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
        val depo = Depo(context)
        val initialPro = depo.proDemo.first()
        val dil = depo.dil.first()
        val manager = androidx.glance.appwidget.GlanceAppWidgetManager(context)
        val widgetId = manager.getAppWidgetId(id)
        val initialConfig = WidgetTasarimi.load(context, widgetId)
        val daily = gununSozu(depo)
        updateAppWidgetState(context, id) { state ->
            state[ACCESS] = initialPro
            state[SOZ] = daily?.metin(dil) ?: if(dil == "tr") "Kendine küçük bir an ayır." else "Take a moment for yourself."
            state[YAZAR] = daily?.sunumEtiketi(dil) ?: "Ascend"
            state[KIMLIK] = daily?.kimlik.orEmpty()
        }
        provideContent {
            val state = currentState<androidx.datastore.preferences.core.Preferences>()
            val pro = state[ACCESS] ?: initialPro
            val config = WidgetSecimi(state[THEME] ?: initialConfig.theme, state[CENTER] ?: initialConfig.centered, state[LARGE] ?: initialConfig.large)
            val size = androidx.glance.LocalSize.current
            val quote = if(pro) state[SOZ].orEmpty().ifBlank { if(dil == "tr") "Kendine küçük bir an ayır." else "Take a moment for yourself." }
                else if(dil == "tr") "Widget’lar Ascend Pro ile." else "Widgets are part of Ascend Pro."
            val source = if(pro) state[YAZAR] ?: "Ascend" else if(dil == "tr") "Önizlemek için dokun" else "Tap to preview"
            val bitmap = androidx.compose.runtime.remember(config, quote, source, size, pro) {
                WidgetTasarimi.render(context, if(pro) config else WidgetSecimi(), quote, source,
                    (size.width.value * 2).toInt(), (size.height.value * 2).toInt())
            }
            val intent = if(pro) android.content.Intent(context, com.yalnizfahrettin.azim.MainActivity::class.java)
                .putExtra(com.yalnizfahrettin.azim.notif.Bildirimler.EXTRA_KIMLIK, state[KIMLIK])
                else android.content.Intent(context, WidgetAyarActivity::class.java).putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            androidx.glance.Image(provider = androidx.glance.ImageProvider(bitmap), contentDescription = "$quote $source",
                contentScale = androidx.glance.layout.ContentScale.FillBounds,
                modifier = GlanceModifier.fillMaxSize().cornerRadius(20.dp)
                    .clickable(androidx.glance.appwidget.action.actionStartActivity(intent)))
        }
    }

    companion object {
        val THEME = stringPreferencesKey("widget_theme")
        val CENTER = androidx.datastore.preferences.core.booleanPreferencesKey("widget_center")
        val LARGE = androidx.datastore.preferences.core.booleanPreferencesKey("widget_large")
        val ACCESS = androidx.datastore.preferences.core.booleanPreferencesKey("widget_pro")
        val KIMLIK = stringPreferencesKey("widget_quote_id")
        val SOZ = stringPreferencesKey("widget_soz")
        val YAZAR = stringPreferencesKey("widget_yazar")

        private suspend fun gununSozu(depo: Depo): com.yalnizfahrettin.azim.data.Soz? {
            val secili = depo.secili.first()
            val allowed = depo.acik.first()
            val hidden = depo.hiddenQuotes.first()
            val choices = Sozler.tumu().filter { it.kategori in secili && it.kategori in allowed && it.kimlik !in hidden }.sortedBy { it.kimlik }
            return choices.takeIf { it.isNotEmpty() }?.random(kotlin.random.Random(java.time.LocalDate.now().toEpochDay().toInt()))
        }

        /** Tüm widget örneklerine yeni bir söz yazar. */
        suspend fun tazele(ctx: Context) {
            val depo = Depo(ctx)
            val dil = depo.dil.first()
            val access = depo.proDemo.first()
            val soz = gununSozu(depo)
            val widget = AzimWidget()
            androidx.glance.appwidget.GlanceAppWidgetManager(ctx)
                .getGlanceIds(AzimWidget::class.java)
                .forEach { id ->
                    val widgetId = androidx.glance.appwidget.GlanceAppWidgetManager(ctx).getAppWidgetId(id)
                    val config = WidgetTasarimi.load(ctx, widgetId)
                    updateAppWidgetState(ctx, id) { p ->
                        p[THEME] = config.theme
                        p[CENTER] = config.centered
                        p[LARGE] = config.large
                        p[ACCESS] = access
                        p[SOZ] = soz?.metin(dil) ?: if(dil == "tr") "Uygulamadan içerik tercihlerini düzenleyebilirsin." else "Adjust content preferences in the app."
                        p[KIMLIK] = soz?.kimlik.orEmpty()
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
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { WidgetTasarimi.remove(context, it) }
        super.onDeleted(context, appWidgetIds)
    }
}
