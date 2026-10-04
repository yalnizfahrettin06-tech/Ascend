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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
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
import com.yalnizfahrettin.azim.data.AscendStore
import com.yalnizfahrettin.azim.data.QuotePicker
import com.yalnizfahrettin.azim.data.UserState
import com.yalnizfahrettin.azim.data.Sozler
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
open class AzimWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = AscendStore.get(context).current()
        val initialPro = state.pro
        val dil = state.language
        val manager = androidx.glance.appwidget.GlanceAppWidgetManager(context)
        val widgetId = manager.getAppWidgetId(id)
        val initialConfig = WidgetTasarimi.load(context, widgetId)
        val daily = gununSozu(state)
        updateAppWidgetState(context, id) { state ->
            state[ACCESS] = initialPro
            state[SOZ] = daily?.metin(dil) ?: com.yalnizfahrettin.azim.data.Diller.metin(dil, "Kendine küçük bir an ayır.", "Take a moment for yourself.")
            state[YAZAR] = daily?.sunumEtiketi(dil) ?: "Ascend"
            state[KIMLIK] = daily?.kimlik.orEmpty()
        }
        provideContent {
            val state = currentState<androidx.datastore.preferences.core.Preferences>()
            val pro = state[ACCESS] ?: initialPro
            val config = WidgetSecimi(state[THEME] ?: initialConfig.theme, state[CENTER] ?: initialConfig.centered, state[LARGE] ?: initialConfig.large)
            val size = androidx.glance.LocalSize.current
            val quote = if(pro) state[SOZ].orEmpty().ifBlank { com.yalnizfahrettin.azim.data.Diller.metin(dil, "Kendine küçük bir an ayır.", "Take a moment for yourself.") }
                else com.yalnizfahrettin.azim.data.Diller.metin(dil, "Widget’lar Ascend Pro ile.", "Widgets are part of Ascend Pro.")
            val source = if(pro) state[YAZAR] ?: "Ascend" else com.yalnizfahrettin.azim.data.Diller.metin(dil, "Önizlemek için dokun", "Tap to preview")
            val bitmap = androidx.compose.runtime.remember(config, quote, source, size, pro) {
                WidgetTasarimi.render(context, if(pro) config else WidgetSecimi(), quote, source,
                    (size.width.value * context.resources.displayMetrics.density).toInt(), (size.height.value * context.resources.displayMetrics.density).toInt())
            }
            val intent = if(pro) android.content.Intent(context, com.yalnizfahrettin.azim.MainActivity::class.java)
                .setAction(com.yalnizfahrettin.azim.notif.Notifier.ACTION_OPEN_QUOTE)
                .putExtra(com.yalnizfahrettin.azim.notif.Notifier.EXTRA_QUOTE_ID, state[KIMLIK])
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

        /** One stable quote per day from the chosen topics. */
        private fun gununSozu(state: UserState): com.yalnizfahrettin.azim.data.Soz? {
            val choices = QuotePicker.topicPool(state).sortedBy { it.kimlik }
            return choices.takeIf { it.isNotEmpty() }?.random(kotlin.random.Random(java.time.LocalDate.now().toEpochDay().toInt()))
        }

        /** Tüm widget örneklerine yeni bir söz yazar. */
        suspend fun tazele(ctx: Context) {
            val state = AscendStore.get(ctx).current()
            val dil = state.language
            val access = state.pro
            val soz = gununSozu(state)
            val manager = androidx.glance.appwidget.GlanceAppWidgetManager(ctx)
            val ids = manager.getGlanceIds(AzimWidget::class.java) + manager.getGlanceIds(AzimSquareWidget::class.java)
            ids
                .forEach { id ->
                    val widgetId = androidx.glance.appwidget.GlanceAppWidgetManager(ctx).getAppWidgetId(id)
                    val config = WidgetTasarimi.load(ctx, widgetId)
                    updateAppWidgetState(ctx, id) { p ->
                        p[THEME] = config.theme
                        p[CENTER] = config.centered
                        p[LARGE] = config.large
                        p[ACCESS] = access
                        p[SOZ] = soz?.metin(dil) ?: com.yalnizfahrettin.azim.data.Diller.metin(dil, "Uygulamadan içerik tercihlerini düzenleyebilirsin.", "Adjust content preferences in the app.")
                        p[KIMLIK] = soz?.kimlik.orEmpty()
                        p[YAZAR] = soz?.sunumEtiketi(dil) ?: "Ascend"
                    }
                }
            AzimWidget().updateAll(ctx)
            AzimSquareWidget().updateAll(ctx)
        }
    }
}

class AzimWidgetSaglayici : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AzimWidget()
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { WidgetTasarimi.remove(context, it) }
        super.onDeleted(context, appWidgetIds)
    }
}

/** Separate provider metadata gives square widgets a real 2 x 2 starting footprint. */
class AzimSquareWidget : AzimWidget()
class AzimSquareWidgetSaglayici : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AzimSquareWidget()
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { WidgetTasarimi.remove(context,it) }
        super.onDeleted(context,appWidgetIds)
    }
}
