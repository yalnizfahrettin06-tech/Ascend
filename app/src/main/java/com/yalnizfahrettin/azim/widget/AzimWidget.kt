package com.yalnizfahrettin.azim.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.defaultWeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.yalnizfahrettin.azim.MainActivity
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.data.AnaTemalar
import com.yalnizfahrettin.azim.data.AscendStore
import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.QuotePicker
import com.yalnizfahrettin.azim.data.Soz
import com.yalnizfahrettin.azim.data.UserState
import com.yalnizfahrettin.azim.notif.Notifier
import java.time.LocalDate

/**
 * Home-screen quote as real Glance text: readable by TalkBack, scaled with the system font,
 * and light enough to update without rendering a full-size bitmap.
 * One stable quote per day from the user's topics; "Next" steps through the same daily order.
 */
open class AzimWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val user = AscendStore.get(context).current()
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val config = WidgetTasarimi.load(context, widgetId)
        val theme = AnaTemalar.allowed(config.theme, user.pro)
        val pool = dailyOrder(user)
        val res = Notifier.localized(context, user.language)
        val background = theme.art?.let { backgroundArt(context, it) }
        provideContent {
            val offset = currentState<Preferences>()[OFFSET] ?: 0
            Content(context, pool.getOrNull(Math.floorMod(offset, pool.size.coerceAtLeast(1))), user.language, theme.dark,
                background, config.large, res.getString(R.string.widget_next), res.getString(R.string.widget_empty))
        }
    }

    @Composable
    private fun Content(context: Context, quote: Soz?, language: String, dark: Boolean, background: Bitmap?, large: Boolean, nextLabel: String, emptyLabel: String) {
        val onArt = background != null
        val ink = ColorProvider(if (onArt || dark) Color.White else Color(0xFF1F1B16))
        val soft = ColorProvider(if (onArt || dark) Color(0xCCFFFFFF) else Color(0xFF5E554A))
        val accent = ColorProvider(if (onArt || dark) Color(0xFFF0A066) else Color(0xFFA84B16))
        val size = LocalSize.current
        val compact = size.height < 140.dp
        val text = quote?.metin(language) ?: emptyLabel
        val fontSize = when {
            compact -> 13.sp
            large || text.length < 70 -> 19.sp
            text.length < 120 -> 16.sp
            else -> 14.sp
        }
        val open = Intent(context, MainActivity::class.java).setAction(Notifier.ACTION_OPEN_QUOTE)
            .putExtra(Notifier.EXTRA_QUOTE_ID, quote?.kimlik.orEmpty())
        Box(GlanceModifier.fillMaxSize().cornerRadius(22.dp)
            .background(if (dark || onArt) Color(0xFF12100E) else Color(0xFFFAF7F2))) {
            if (background != null) {
                Image(ImageProvider(background), contentDescription = null, contentScale = ContentScale.Crop, modifier = GlanceModifier.fillMaxSize())
                Box(GlanceModifier.fillMaxSize().background(Color(0x99000000))) {}
            }
            Column(GlanceModifier.fillMaxSize().padding(horizontal = 16.dp, vertical = if (compact) 10.dp else 14.dp)) {
                Text(quote?.let { Kategoriler.bul(it.kategori)?.ad(language) }.orEmpty().uppercase(java.util.Locale.forLanguageTag(language)),
                    style = TextStyle(color = accent, fontSize = 10.sp, fontWeight = FontWeight.Medium), maxLines = 1)
                Spacer(GlanceModifier.height(6.dp))
                Text(text, maxLines = if (compact) 3 else 7,
                    style = TextStyle(color = ink, fontSize = fontSize, fontFamily = FontFamily.Serif),
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth().clickable(actionStartActivity(open)))
                Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(quote?.sunumEtiketi(language).orEmpty(), maxLines = 1, style = TextStyle(color = soft, fontSize = 10.sp),
                        modifier = GlanceModifier.defaultWeight())
                    if (quote != null) Text("$nextLabel  ›", style = TextStyle(color = accent, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End),
                        modifier = GlanceModifier.padding(start = 8.dp, top = 6.dp, bottom = 2.dp).clickable(actionRunCallback<NextQuoteAction>())
                            .semantics { contentDescription = nextLabel })
                }
            }
        }
    }

    companion object {
        val OFFSET = intPreferencesKey("widget_offset")

        /** The day's order is stable, so every widget and every "Next" tap agree. */
        fun dailyOrder(user: UserState): List<Soz> =
            QuotePicker.topicPool(user).sortedBy { it.kimlik }.shuffled(kotlin.random.Random(LocalDate.now().toEpochDay()))

        /** A small, cropped-on-device background; the quote itself is never drawn into it. */
        private fun backgroundArt(context: Context, res: Int): Bitmap? = runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
            BitmapFactory.decodeResource(context.resources, res, bounds)
            var sample = 1
            while (bounds.outWidth / (sample * 2) >= 420) sample *= 2
            BitmapFactory.decodeResource(context.resources, res, BitmapFactory.Options().apply { inSampleSize = sample; inScaled = false })
        }.getOrNull()

        /** Re-renders every placed widget, e.g. after topics, language or Pro change. */
        suspend fun tazele(ctx: Context) {
            AzimWidget().updateAll(ctx)
            AzimSquareWidget().updateAll(ctx)
        }
    }
}

class NextQuoteAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { it[AzimWidget.OFFSET] = (it[AzimWidget.OFFSET] ?: 0) + 1 }
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

/** Separate provider metadata gives square widgets a real 2 x 2 starting footprint. */
class AzimSquareWidget : AzimWidget()
class AzimSquareWidgetSaglayici : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AzimSquareWidget()
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { WidgetTasarimi.remove(context, it) }
        super.onDeleted(context, appWidgetIds)
    }
}
