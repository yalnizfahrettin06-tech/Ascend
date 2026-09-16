package com.yalnizfahrettin.azim.notif

/** Drop stale work, never catch up with a burst after Doze or a reboot. */
object DeliveryPolicy {
    const val MAX_LATENESS_MS = 60 * 60_000L
    fun shouldDeliver(now: Long, scheduled: Long, last: Long, count: Int, start: Int, end: Int): Boolean {
        // Work from an older app version has no timestamp and must not replay.
        if (scheduled <= 0 || now < scheduled || now - scheduled > MAX_LATENESS_MS) return false
        val gap = (((end - start).coerceAtLeast(1) * 60_000L * 60) / count.coerceIn(1, 7) / 2)
            .coerceIn(60_000L, 20 * 60_000L)
        // A wall-clock rollback must not silence reminders until the old date is reached.
        if (last > now) return true
        return last == 0L || now - last >= gap
    }
}
