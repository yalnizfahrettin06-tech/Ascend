package com.yalnizfahrettin.azim.notif

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.yalnizfahrettin.azim.data.AscendStore
import com.yalnizfahrettin.azim.data.QuotePicker
import com.yalnizfahrettin.azim.data.ReminderPlan
import com.yalnizfahrettin.azim.data.UserActions
import com.yalnizfahrettin.azim.data.UserState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * One pending alarm at a time: the next reminder slot. Each delivery schedules the following one.
 * setAndAllowWhileIdle survives Doze without the exact-alarm permission; a small delay is acceptable,
 * and a slot that is hours late is skipped rather than delivered out of context.
 */
object ReminderScheduler {
    private const val ACTION_FIRE = "com.yalnizfahrettin.azim.REMINDER_FIRE"
    private const val EXTRA_AT = "scheduled_for"
    private const val REQUEST = 4101

    private fun pending(ctx: Context, at: Long, create: Boolean): PendingIntent? = PendingIntent.getBroadcast(
        ctx, REQUEST,
        Intent(ctx, ReminderReceiver::class.java).setAction(ACTION_FIRE).putExtra(EXTRA_AT, at),
        PendingIntent.FLAG_IMMUTABLE or if (create) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_NO_CREATE,
    )

    /** Returns the scheduled time, or null when nothing is scheduled. */
    fun schedule(ctx: Context, state: UserState, after: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())): ZonedDateTime? {
        val alarms = ctx.getSystemService(AlarmManager::class.java) ?: return null
        pending(ctx, 0L, create = false)?.let { alarms.cancel(it) }
        if (!state.onboarded || !Notifier.canPost(ctx)) return null
        val next = ReminderPlan.next(after, state.reminders) ?: return null
        val at = next.toInstant().toEpochMilli()
        alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, requireNotNull(pending(ctx, at, create = true)))
        return next
    }

    fun rescheduleAsync(ctx: Context) {
        val app = ctx.applicationContext
        scope.launch { schedule(app, AscendStore.get(app).current()) }
    }

    internal val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    internal suspend fun deliver(ctx: Context, scheduledFor: Long) {
        val store = AscendStore.get(ctx)
        val state = store.current()
        val now = System.currentTimeMillis()
        val duplicate = now - state.lastDeliveryAt < 10 * 60_000L
        if (state.onboarded && !duplicate && ReminderPlan.shouldDeliver(now, scheduledFor, state.reminders)) {
            QuotePicker.pick(state)?.let { pick ->
                if (Notifier.show(ctx, pick, state)) {
                    store.update { UserActions.recordDelivery(it, pick.quote.kimlik, pick.surprise, now, LocalDate.now()) }
                }
            }
        }
        // Never reschedule the slot that just fired, even if the alarm ran a little early.
        val after = Instant.ofEpochMilli(maxOf(now, scheduledFor) + 60_000L).atZone(ZoneId.systemDefault())
        schedule(ctx, store.current(), after)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        val at = intent.getLongExtra("scheduled_for", 0L)
        ReminderScheduler.scope.launch {
            try { ReminderScheduler.deliver(context.applicationContext, at) } finally { result.finish() }
        }
    }
}

/** Reboot, app update, clock or time-zone change: rebuild the single pending alarm. */
class SystemEventsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        ReminderScheduler.scope.launch {
            try {
                val app = context.applicationContext
                val state = AscendStore.get(app).current()
                Notifier.ensureChannel(app, state.language)
                ReminderScheduler.schedule(app, state)
            } finally { result.finish() }
        }
    }
}

/** Notification buttons: save (add-only) and pause until midnight. */
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION, 0)
        val result = goAsync()
        ReminderScheduler.scope.launch {
            try {
                val app = context.applicationContext
                val store = AscendStore.get(app)
                when (intent.action) {
                    ACTION_SAVE -> store.update { UserActions.addFavorite(it, id) }
                    ACTION_PAUSE_TODAY -> {
                        val midnight = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val state = store.update { it.copy(reminders = it.reminders.copy(pausedUntil = midnight)) }
                        ReminderScheduler.schedule(app, state)
                    }
                }
                NotificationManagerCompat.from(app).cancel(notificationId)
            } finally { result.finish() }
        }
    }

    companion object {
        const val ACTION_SAVE = "com.yalnizfahrettin.azim.SAVE_QUOTE"
        const val ACTION_PAUSE_TODAY = "com.yalnizfahrettin.azim.PAUSE_TODAY"
        private const val EXTRA_ID = "quote_id"
        private const val EXTRA_NOTIFICATION = "notification_id"

        fun intent(ctx: Context, action: String, quoteId: String, notificationId: Int): PendingIntent = PendingIntent.getBroadcast(
            ctx, (action + quoteId).hashCode(),
            Intent(ctx, NotificationActionReceiver::class.java).setAction(action)
                .putExtra(EXTRA_ID, quoteId).putExtra(EXTRA_NOTIFICATION, notificationId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
