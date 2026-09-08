package com.yalnizfahrettin.azim.data

object Baslangic {
    val konular = listOf("motivasyon", "ozsefkat", "marcus", "derin_odak", "azim", "ic_huzur")
    val varsayilan = setOf("motivasyon", "ozsefkat", "marcus")
    fun secimiDegistir(secili: Set<String>, anahtar: String): Set<String> =
        if (anahtar !in konular) secili
        else if (anahtar in secili) secili - anahtar else secili + anahtar
    fun dogrula(secili: Set<String>): Set<String> =
        secili.intersect(Erisim.ucretsizKategoriler).ifEmpty { varsayilan }
}
