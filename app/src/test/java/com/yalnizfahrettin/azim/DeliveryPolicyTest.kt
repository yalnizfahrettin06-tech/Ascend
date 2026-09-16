package com.yalnizfahrettin.azim

import com.yalnizfahrettin.azim.notif.DeliveryPolicy
import org.junit.Assert.*
import org.junit.Test

class DeliveryPolicyTest {
    private val now = 100_000_000L
    private val minute = 60_000L
    @Test fun delayedBatchOnlyDeliversFreshSpacedWork() {
        assertFalse(DeliveryPolicy.shouldDeliver(now, now - 61 * minute, 0, 3, 9, 21))
        assertTrue(DeliveryPolicy.shouldDeliver(now, now - 30 * minute, 0, 3, 9, 21))
        assertFalse(DeliveryPolicy.shouldDeliver(now + minute, now, now, 3, 9, 21))
        assertTrue(DeliveryPolicy.shouldDeliver(now + 20 * minute, now, now, 3, 9, 21))
    }
    @Test fun legacyFutureAndDuplicateJobsDoNotPost() {
        assertFalse(DeliveryPolicy.shouldDeliver(now, 0, 0, 3, 9, 21))
        assertFalse(DeliveryPolicy.shouldDeliver(now, now + minute, 0, 3, 9, 21))
        assertFalse(DeliveryPolicy.shouldDeliver(now, now, now, 3, 9, 21))
    }
    @Test fun shortWindowCanStillDeliverConfiguredFrequency() {
        assertTrue(DeliveryPolicy.shouldDeliver(now, now, now - 8 * minute, 7, 9, 10))
        assertFalse(DeliveryPolicy.shouldDeliver(now, now, now - minute, 7, 9, 10))
    }
    @Test fun clockRollbackDoesNotSilenceAllFutureReminders() {
        assertTrue(DeliveryPolicy.shouldDeliver(now, now, now + 24 * 60 * minute, 3, 9, 21))
    }
}
