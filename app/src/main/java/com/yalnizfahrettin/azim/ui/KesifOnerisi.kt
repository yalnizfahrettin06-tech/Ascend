package com.yalnizfahrettin.azim.ui

import com.yalnizfahrettin.azim.data.Kategori
import com.yalnizfahrettin.azim.data.KategoriGrubu
import com.yalnizfahrettin.azim.data.Kategoriler
import com.yalnizfahrettin.azim.data.Sozler
import java.time.LocalTime

/**
 * Ana ekrandaki kategori keşif önerisi.
 *
 * Tek bir kalıp ("Aşk kategorisini denemek ister misin?") hep tekrarlanınca
 * kör noktaya döner. Bunun yerine koşula bağlı bir set: kullanıcının gerçek
 * durumuna değen öneri, genel bir davetten daha çok tıklanır.
 *
 * Sıralama önemli — üstteki koşul sağlanırsa alttakilere bakılmaz.
 */
sealed interface Oneri {
    val grup: KategoriGrubu

    /** Havuz küçük: bildirimlerde tekrar yaşanacak. */
    data class HavuzKucuk(override val grup: KategoriGrubu, val kategoriSayisi: Int) : Oneri

    /** Favorilerin çoğu tek gruptan: aynı ailenin kilitli grubunu öner. */
    data class FavoriEgilimi(override val grup: KategoriGrubu, val kaynak: String) : Oneri

    /** Saate uygun grup. */
    data class ZamanaUygun(override val grup: KategoriGrubu) : Oneri

    /** Hiçbiri: sade davet. */
    data class Genel(override val grup: KategoriGrubu) : Oneri
}

object KesifMotoru {

    private val sabahGruplari = listOf("disiplin", "spor")
    private val aksamGruplari = listOf("zihin", "tasavvuf")

    fun oner(
        secili: Set<String>,
        acikGruplar: Set<String>,
        favoriler: Set<String>,
        gunlukAdet: Int,
        saat: Int = LocalTime.now().hour,
    ): Oneri? {
        val kilitliler = Kategoriler.gruplar.filter { it.anahtar !in acikGruplar }
        if (kilitliler.isEmpty()) return null

        // 1) Havuz küçükse bu en dürüst öneri: gerçek bir sorunu çözüyor.
        val havuz = Sozler.tumu().count { it.kategori in secili }
        if (gunlukAdet > 0 && havuz / gunlukAdet < 7) {
            return Oneri.HavuzKucuk(kilitliler.first(), secili.size)
        }

        // 2) Favorilerin çoğu tek gruptansa, komşu kilitli grubu öner.
        val favoriGruplari = favoriler
            .mapNotNull { Sozler.kimlikten(it)?.kategori }
            .mapNotNull { Kategoriler.bul(it)?.grup }
        if (favoriGruplari.size >= 3) {
            val baskin = favoriGruplari.groupingBy { it }.eachCount().maxByOrNull { it.value }
            if (baskin != null && baskin.value * 2 >= favoriGruplari.size) {
                val kaynakAd = Kategoriler.grupBul(baskin.key)?.anahtar
                if (kaynakAd != null) {
                    return Oneri.FavoriEgilimi(kilitliler.random(), kaynakAd)
                }
            }
        }

        // 3) Saate uygun grup kilitliyse öner.
        val hedef = when (saat) {
            in 5..11 -> sabahGruplari
            in 18..23 -> aksamGruplari
            else -> emptyList()
        }
        hedef.firstNotNullOfOrNull { a -> kilitliler.firstOrNull { it.anahtar == a } }
            ?.let { return Oneri.ZamanaUygun(it) }

        // 4) Sade davet.
        return Oneri.Genel(kilitliler.random())
    }
}
