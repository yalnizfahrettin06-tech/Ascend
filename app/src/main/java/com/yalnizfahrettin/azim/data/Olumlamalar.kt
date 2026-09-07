package com.yalnizfahrettin.azim.data

/** View of the English-master catalogue, not a second editable content source. */
object Olumlamalar {
    val tumu: List<Soz> get() = Sozler.tumu().filter { Kategoriler.bul(it.kategori)?.grup == "olumlamalar" }
}
