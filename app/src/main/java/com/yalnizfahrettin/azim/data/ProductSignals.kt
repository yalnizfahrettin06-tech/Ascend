package com.yalnizfahrettin.azim.data

import android.content.Context
import java.time.LocalDate

/** Local demo diagnostics only. No network, quote text, category IDs or device identifiers. */
object ProductSignals {
    enum class Event { OFFER_VIEWED, OFFER_CLOSED, DEMO_ENABLED, ACTION_RESUMED, SETUP_COMPLETED, SERIES_STARTED, SERIES_DAY_COMPLETED }
    @Synchronized fun record(context: Context, event: Event, source: ProSource = ProSource.GENERAL) {
        val prefs = context.getSharedPreferences("local-product-signals", Context.MODE_PRIVATE)
        val today = LocalDate.now()
        val key = "$today.${event.name}.${source.name}"
        val editor = prefs.edit()
        prefs.all.keys.filter { runCatching { LocalDate.parse(it.substringBefore('.')) < today.minusDays(29) }.getOrDefault(true) }
            .forEach(editor::remove)
        editor.putInt(key, (prefs.getInt(key, 0) + 1).coerceAtMost(10000)).apply()
    }
}
