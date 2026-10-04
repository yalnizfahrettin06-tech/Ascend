package com.yalnizfahrettin.azim.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yalnizfahrettin.azim.MainActivity
import com.yalnizfahrettin.azim.R
import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.Pick
import com.yalnizfahrettin.azim.data.UserState
import java.util.Locale

object Notifier {
    const val CHANNEL = "daily_words"
    private const val LEGACY_CHANNEL = "azim_sozler"
    const val ACTION_OPEN_QUOTE = "com.yalnizfahrettin.azim.OPEN_QUOTE"
    const val EXTRA_QUOTE_ID = "quote_id"

    fun localized(ctx: Context, language: String): Context =
        ctx.createConfigurationContext(Configuration(ctx.resources.configuration).apply { setLocale(Locale.forLanguageTag(language)) })

    /** A calm default channel: sound and a status-bar icon, no forced pop-up. Users can raise it in system settings. */
    fun ensureChannel(ctx: Context, language: String) {
        val mgr = ctx.getSystemService(NotificationManager::class.java) ?: return
        val res = localized(ctx, language)
        mgr.deleteNotificationChannel(LEGACY_CHANNEL)
        val channel = mgr.getNotificationChannel(CHANNEL)
            ?: NotificationChannel(CHANNEL, res.getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        channel.name = res.getString(R.string.channel_name)
        channel.description = res.getString(R.string.channel_description)
        mgr.createNotificationChannel(channel)
    }

    fun canPost(ctx: Context): Boolean {
        val mgr = ctx.getSystemService(NotificationManager::class.java)
        return NotificationManagerCompat.from(ctx).areNotificationsEnabled() &&
            mgr?.getNotificationChannel(CHANNEL)?.importance != NotificationManager.IMPORTANCE_NONE
    }

    fun openIntent(ctx: Context, quoteId: String): PendingIntent = PendingIntent.getActivity(
        ctx, quoteId.hashCode(),
        Intent(ctx, MainActivity::class.java).apply {
            action = ACTION_OPEN_QUOTE
            putExtra(EXTRA_QUOTE_ID, quoteId)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    /** Posts the complete quote. Returns false when Android refuses it. */
    fun show(ctx: Context, pick: Pick, state: UserState): Boolean {
        if (!canPost(ctx)) return false
        ensureChannel(ctx, state.language)
        val res = localized(ctx, state.language)
        val quote = pick.quote
        val topic = Kategoriler.bul(quote.kategori)?.ad(state.language).orEmpty()
        val title = if (pick.surprise) res.getString(R.string.notification_surprise_title, topic) else topic
        val text = quote.metin(state.language)
        val id = quote.kimlik.hashCode()
        val builder = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_bildirim)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$text\n\n— ${quote.sunumEtiketi(state.language)}").setBigContentTitle(title))
            .setContentIntent(openIntent(ctx, quote.kimlik))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        if (quote.kimlik !in state.favorites) builder.addAction(0, res.getString(R.string.notification_save),
            NotificationActionReceiver.intent(ctx, NotificationActionReceiver.ACTION_SAVE, quote.kimlik, id))
        builder.addAction(0, res.getString(R.string.notification_pause_today),
            NotificationActionReceiver.intent(ctx, NotificationActionReceiver.ACTION_PAUSE_TODAY, quote.kimlik, id))
        return try {
            NotificationManagerCompat.from(ctx).notify(id, builder.build())
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
