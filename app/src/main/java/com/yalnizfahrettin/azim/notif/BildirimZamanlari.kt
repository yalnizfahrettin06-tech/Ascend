package com.yalnizfahrettin.azim.notif

import java.time.LocalDateTime

/** Equal midpoints within the chosen window; WorkManager delivery is approximate. */
object BildirimZamanlari {
    fun hesapla(simdi: LocalDateTime, adet: Int, bas: Int, bit: Int): List<LocalDateTime> {
        require(adet in 1..7)
        require(bas in 0..23 && bit in bas + 1..24)
        val baslangic = simdi.toLocalDate().atStartOfDay().plusHours(bas.toLong())
        val dakika = (bit - bas) * 60L
        return (0 until adet).map { baslangic.plusMinutes(dakika * (2 * it + 1) / (2 * adet)) }
            .filter { it.isAfter(simdi) }
    }
}
