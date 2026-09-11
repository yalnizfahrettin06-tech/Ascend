package com.yalnizfahrettin.azim.paylas

import android.content.SharedPreferences

/** Remember typography and layout only. Photos and source media remain explicit choices. */
object SonPaylasimDuzeni {
    fun oku(p: SharedPreferences): PaylasimAyari = runCatching {
        PaylasimAyari(
            format = KartFormat.valueOf(p.getString("last-format", "STORY")!!),
            yazi = KartYazi.valueOf(p.getString("last-font", "LORA")!!),
            karartma = p.getFloat("last-dim", .45f).coerceIn(0f, 1f),
            yaziOlcegi = p.getFloat("last-scale", 1f).coerceIn(.8f, 1.3f),
            hizalama = KartHizalama.valueOf(p.getString("last-align", "ORTA")!!),
            imzaGoster = p.getBoolean("last-signature", true),
        )
    }.getOrDefault(PaylasimAyari())

    fun kaydet(p: SharedPreferences, a: PaylasimAyari) {
        p.edit().putString("last-format", a.format.name).putString("last-font", a.yazi.name)
            .putFloat("last-dim", a.karartma).putFloat("last-scale", a.yaziOlcegi)
            .putString("last-align", a.hizalama.name).putBoolean("last-signature", a.imzaGoster).apply()
    }
}
