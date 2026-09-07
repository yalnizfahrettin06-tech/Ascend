package com.yalnizfahrettin.azim.data

object Baslangic {
    val konular = listOf("ozsefkat", "ic_huzur", "kendine_guven", "yeniden", "derin_odak", "umut")
    val varsayilan = setOf("ozsefkat", "ic_huzur")
    fun secimiDegistir(secili: Set<String>, anahtar: String): Set<String> =
        if (anahtar !in konular) secili
        else if (anahtar in secili) secili - anahtar else secili + anahtar
    fun dogrula(secili: Set<String>): Set<String> =
        secili.intersect(Kategoriler.acikAltlar(Kategoriler.ucretsizGruplar)).ifEmpty { varsayilan }
}
