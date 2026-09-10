package com.yalnizfahrettin.azim.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/*
 * KENDİ İKON SETİMİZ
 *
 * material-icons-extended 10.000'den fazla ikon sınıfı taşır; bu uygulama
 * 14 tanesini kullanıyor. Kütüphane hem APK'yı hem derlemeyi gereksiz
 * şişiriyordu (zayıf makinede dex adımı bellek yetmediği için düşüyordu).
 *
 * Buradaki ikonlar elle çizildi: 24x24 viewport, 1.75dp çizgi, yuvarlatılmış
 * uçlar — tek görsel dil. "Dolu" varyantlar aktif durum için.
 */
private fun ikon(ad: String, ciz: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = ad, defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f,
    ).apply(ciz).build()

private fun ImageVector.Builder.cizgi(blok: PathBuilder.() -> Unit) = path(
    stroke = SolidColor(Color.Black),
    strokeLineWidth = 1.75f,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round,
    pathBuilder = blok,
)

private fun ImageVector.Builder.dolgu(blok: PathBuilder.() -> Unit) =
    path(fill = SolidColor(Color.Black), pathBuilder = blok)

object AzimIkon {

    // Collection family: same 24-unit viewport and 1.75-unit rounded stroke.
    val AcikKalp = ikon("acik_kalp") {
        cizgi {
            moveTo(10f, 18.8f); curveTo(6f, 16f, 3.5f, 12.8f, 3.5f, 9.2f)
            curveTo(3.5f, 4f, 9.2f, 3.3f, 12f, 7f)
            curveTo(14.8f, 3.3f, 20.5f, 4f, 20.5f, 9.2f)
            curveTo(20.5f, 12.8f, 18f, 16f, 14f, 18.8f)
        }
    }
    val Basamak = ikon("basamak") {
        cizgi {
            moveTo(3f, 19f); horizontalLineTo(9f); verticalLineTo(13f)
            horizontalLineTo(15f); verticalLineTo(7f); horizontalLineTo(21f)
        }
    }
    val Odak = ikon("odak") {
        cizgi {
            moveTo(8f, 4f); horizontalLineTo(4f); verticalLineTo(8f)
            moveTo(16f, 4f); horizontalLineTo(20f); verticalLineTo(8f)
            moveTo(20f, 16f); verticalLineTo(20f); horizontalLineTo(16f)
            moveTo(8f, 20f); horizontalLineTo(4f); verticalLineTo(16f)
        }
        dolgu {
            moveTo(12f, 13.3f); arcTo(1.3f, 1.3f, 0f, true, true, 12f, 10.7f)
            arcTo(1.3f, 1.3f, 0f, true, true, 12f, 13.3f); close()
        }
    }
    val Esik = ikon("esik") {
        cizgi {
            moveTo(5f, 20f); verticalLineTo(10f)
            curveTo(5f, 1.5f, 19f, 1.5f, 19f, 10f); verticalLineTo(20f)
            moveTo(9f, 20f); horizontalLineTo(15f)
        }
    }
    val Sutun = ikon("sutun") {
        cizgi {
            moveTo(5f, 4f); horizontalLineTo(19f); verticalLineTo(7f); horizontalLineTo(5f); close()
            moveTo(8f, 7f); verticalLineTo(18f)
            moveTo(12f, 10f); verticalLineTo(16f)
            moveTo(16f, 7f); verticalLineTo(18f)
            moveTo(5f, 21f); verticalLineTo(18f); horizontalLineTo(19f); verticalLineTo(21f); close()
        }
    }
    val IcYol = ikon("ic_yol") {
        cizgi {
            moveTo(19f, 18f); curveTo(11f, 24f, 1f, 17f, 5f, 9f)
            curveTo(8f, 2f, 19f, 3f, 19f, 10f)
            curveTo(19f, 16f, 10f, 17f, 10f, 11f)
            curveTo(10f, 9f, 13f, 8f, 14f, 10f)
        }
    }
    val Eller = ikon("eller") {
        cizgi {
            moveTo(3f, 6f); verticalLineTo(13f); lineTo(8f, 20f); horizontalLineTo(16f)
            lineTo(21f, 13f); verticalLineTo(6f)
            moveTo(5f, 11f); lineTo(9f, 15f); lineTo(12f, 13f); lineTo(15f, 15f); lineTo(19f, 11f)
        }
    }
    val Hareket = ikon("hareket") {
        cizgi {
            moveTo(3f, 12f); horizontalLineTo(6f); lineTo(9f, 6f)
            lineTo(15f, 18f); lineTo(18f, 12f); horizontalLineTo(21f)
        }
    }
    val Canta = ikon("canta") {
        cizgi {
            moveTo(4f, 8f); horizontalLineTo(20f); verticalLineTo(20f); horizontalLineTo(4f); close()
            moveTo(8f, 8f); verticalLineTo(4f); horizontalLineTo(16f); verticalLineTo(8f)
            moveTo(4f, 12f); curveTo(8f, 15f, 16f, 15f, 20f, 12f)
            moveTo(12f, 13f); verticalLineTo(16f)
        }
    }
    val Bag = ikon("bag") {
        cizgi {
            moveTo(10f, 16f); lineTo(8f, 18f)
            curveTo(3f, 22f, 0f, 15f, 5f, 12f); lineTo(8f, 9f)
            curveTo(10f, 7f, 13f, 8f, 14f, 10f)
            moveTo(14f, 8f); lineTo(16f, 6f)
            curveTo(21f, 2f, 24f, 9f, 19f, 12f); lineTo(16f, 15f)
            curveTo(14f, 17f, 11f, 16f, 10f, 14f)
        }
    }
    val Dalga = ikon("dalga") {
        cizgi {
            moveTo(3f, 13f); curveTo(6f, 13f, 6f, 8f, 9f, 8f)
            curveTo(12f, 8f, 12f, 16f, 15f, 16f)
            curveTo(18f, 16f, 18f, 11f, 21f, 11f)
        }
    }

    // Ascend's single rising line: a small pause before the next step upward.
    val Yukselis = ikon("ascend") {
        cizgi { moveTo(3f, 19f); lineTo(10f, 12f); lineTo(14f, 15f); lineTo(21f, 4f) }
    }
    val Kisi = ikon("senin") {
        cizgi {
            moveTo(12f, 12f); arcTo(4f, 4f, 0f, true, true, 12f, 4f)
            arcTo(4f, 4f, 0f, true, true, 12f, 12f)
            moveTo(4f, 21f); curveTo(4f, 13f, 20f, 13f, 20f, 21f)
        }
    }
    val Ara = ikon("ara") {
        cizgi {
            moveTo(10.5f, 17f); arcTo(6.5f, 6.5f, 0f, true, true, 10.5f, 4f)
            arcTo(6.5f, 6.5f, 0f, true, true, 10.5f, 17f)
            moveTo(15f, 15f); lineTo(21f, 21f)
        }
    }
    val Ileri = ikon("ileri") {
        cizgi { moveTo(9f, 5f); lineTo(16f, 12f); lineTo(9f, 19f) }
    }

    // Compatibility names keep older feature screens on the current identity.
    val Dag = Yukselis
    val DagDolu = Yukselis
    val Ayrac = ikon("ayrac") {
        cizgi {
            moveTo(7f, 3.5f); horizontalLineTo(17f)
            curveTo(18.1f, 3.5f, 19f, 4.4f, 19f, 5.5f)
            verticalLineTo(21f); lineTo(12f, 17f); lineTo(5f, 21f); verticalLineTo(5.5f)
            curveTo(5f, 4.4f, 5.9f, 3.5f, 7f, 3.5f); close()
        }
    }
    val AyracDolu = ikon("ayrac_dolu") {
        dolgu {
            moveTo(7f, 3.5f); horizontalLineTo(17f)
            curveTo(18.1f, 3.5f, 19f, 4.4f, 19f, 5.5f)
            verticalLineTo(21f); lineTo(12f, 17f); lineTo(5f, 21f); verticalLineTo(5.5f)
            curveTo(5f, 4.4f, 5.9f, 3.5f, 7f, 3.5f); close()
        }
    }
    val Patika = ikon("patika") {
        cizgi {
            moveTo(5f, 20f); horizontalLineTo(13.5f)
            curveTo(20f, 20f, 20f, 13.5f, 13.5f, 13.5f)
            horizontalLineTo(10.5f)
            curveTo(4f, 13.5f, 4f, 7f, 10.5f, 7f)
            horizontalLineTo(16f)
            moveTo(14f, 4f); lineTo(17f, 7f); lineTo(14f, 10f)
        }
    }
    val Kitap = ikon("kitap") {
        cizgi {
            moveTo(12f, 6f); verticalLineTo(21f)
            moveTo(12f, 6f); curveTo(9f, 3.5f, 5f, 3.5f, 3f, 4f)
            verticalLineTo(18f); curveTo(6.5f, 17.5f, 9f, 18f, 12f, 21f)
            curveTo(15f, 18f, 17.5f, 17.5f, 21f, 18f); verticalLineTo(4f)
            curveTo(19f, 3.5f, 15f, 3.5f, 12f, 6f)
        }
    }

    val Ev = ikon("ev") {
        cizgi {
            moveTo(3f, 10.5f); lineTo(12f, 3.5f); lineTo(21f, 10.5f)
            moveTo(5.5f, 9f); verticalLineTo(20f); horizontalLineTo(18.5f); verticalLineTo(9f)
        }
    }
    val EvDolu = ikon("ev_dolu") {
        dolgu {
            moveTo(12f, 3.2f); lineTo(21.5f, 10.6f); verticalLineTo(20.4f)
            horizontalLineTo(14.2f); verticalLineTo(15.2f); horizontalLineTo(9.8f)
            verticalLineTo(20.4f); horizontalLineTo(2.5f); verticalLineTo(10.6f); close()
        }
    }

    val Izgara = ikon("izgara") {
        cizgi {
            moveTo(4f, 5f); horizontalLineTo(10f); verticalLineTo(11f); horizontalLineTo(4f); close()
            moveTo(14f, 5f); horizontalLineTo(20f); verticalLineTo(11f); horizontalLineTo(14f); close()
            moveTo(4f, 14f); horizontalLineTo(10f); verticalLineTo(20f); horizontalLineTo(4f); close()
            moveTo(14f, 14f); horizontalLineTo(20f); verticalLineTo(20f); horizontalLineTo(14f); close()
        }
    }
    val IzgaraDolu = ikon("izgara_dolu") {
        dolgu {
            moveTo(4f, 5f); horizontalLineTo(10f); verticalLineTo(11f); horizontalLineTo(4f); close()
            moveTo(14f, 5f); horizontalLineTo(20f); verticalLineTo(11f); horizontalLineTo(14f); close()
            moveTo(4f, 14f); horizontalLineTo(10f); verticalLineTo(20f); horizontalLineTo(4f); close()
            moveTo(14f, 14f); horizontalLineTo(20f); verticalLineTo(20f); horizontalLineTo(14f); close()
        }
    }

    val Kalp = ikon("kalp") {
        cizgi {
            moveTo(12f, 20f)
            curveTo(12f, 20f, 3.5f, 14.8f, 3.5f, 9.2f)
            curveTo(3.5f, 6.6f, 5.6f, 4.8f, 8.1f, 4.8f)
            curveTo(9.8f, 4.8f, 11.3f, 5.7f, 12f, 7f)
            curveTo(12.7f, 5.7f, 14.2f, 4.8f, 15.9f, 4.8f)
            curveTo(18.4f, 4.8f, 20.5f, 6.6f, 20.5f, 9.2f)
            curveTo(20.5f, 14.8f, 12f, 20f, 12f, 20f)
            close()
        }
    }
    val KalpDolu = ikon("kalp_dolu") {
        dolgu {
            moveTo(12f, 20.4f)
            curveTo(12f, 20.4f, 3.2f, 15f, 3.2f, 9.2f)
            curveTo(3.2f, 6.4f, 5.4f, 4.5f, 8.1f, 4.5f)
            curveTo(9.9f, 4.5f, 11.3f, 5.5f, 12f, 6.8f)
            curveTo(12.7f, 5.5f, 14.1f, 4.5f, 15.9f, 4.5f)
            curveTo(18.6f, 4.5f, 20.8f, 6.4f, 20.8f, 9.2f)
            curveTo(20.8f, 15f, 12f, 20.4f, 12f, 20.4f)
            close()
        }
    }

    val Grafik = ikon("grafik") {
        cizgi {
            moveTo(3.5f, 16.5f); lineTo(9f, 11f); lineTo(13f, 15f); lineTo(20.5f, 7.5f)
        }
    }
    val GrafikDolu = ikon("grafik_dolu") {
        dolgu {
            moveTo(3.5f, 15.4f); lineTo(9f, 9.9f); lineTo(13f, 13.9f); lineTo(19.4f, 7.5f)
            lineTo(21.5f, 9.6f); lineTo(13f, 18.1f); lineTo(9f, 14.1f); lineTo(4.6f, 18.5f)
            close()
        }
    }

    val Ayarlar = ikon("ayarlar") {
        cizgi {
            moveTo(4f, 7f); horizontalLineTo(7f)
            moveTo(11f, 7f); horizontalLineTo(20f)
            moveTo(4f, 17f); horizontalLineTo(13f)
            moveTo(17f, 17f); horizontalLineTo(20f)
            moveTo(9f, 9f); arcTo(2f, 2f, 0f, true, true, 9f, 5f)
            arcTo(2f, 2f, 0f, true, true, 9f, 9f); close()
            moveTo(15f, 19f); arcTo(2f, 2f, 0f, true, true, 15f, 15f)
            arcTo(2f, 2f, 0f, true, true, 15f, 19f); close()
        }
    }

    val Sonraki = ikon("sonraki") {
        cizgi { moveTo(5f, 12f); horizontalLineTo(19f); moveTo(13f, 6f); lineTo(19f, 12f); lineTo(13f, 18f) }
    }
    val Disari = ikon("disari") {
        cizgi { moveTo(6f, 18f); lineTo(18f, 6f); moveTo(7f, 6f); horizontalLineTo(18f); verticalLineTo(17f) }
    }
    val Asagi = ikon("asagi") {
        cizgi { moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f) }
    }
    val Arti = ikon("arti") {
        cizgi { moveTo(5f, 12f); horizontalLineTo(19f); moveTo(12f, 5f); verticalLineTo(19f) }
    }
    val Eksi = ikon("eksi") {
        cizgi { moveTo(5f, 12f); horizontalLineTo(19f) }
    }
    val Daha = ikon("daha") {
        dolgu {
            listOf(5f, 12f, 19f).forEach { x ->
                moveTo(x, 13.5f); arcTo(1.5f, 1.5f, 0f, true, true, x, 10.5f)
                arcTo(1.5f, 1.5f, 0f, true, true, x, 13.5f); close()
            }
        }
    }
    val Bildirim = ikon("bildirim") {
        cizgi {
            moveTo(5f, 17f); curveTo(7f, 14f, 6f, 12f, 7f, 8f)
            curveTo(8f, 3f, 16f, 3f, 17f, 8f)
            curveTo(18f, 12f, 17f, 14f, 19f, 17f); close()
            moveTo(10f, 20f); curveTo(11f, 21f, 13f, 21f, 14f, 20f)
            moveTo(12f, 3f); verticalLineTo(4f)
        }
    }
    val Saat = ikon("saat") {
        cizgi {
            moveTo(12f, 21f); arcTo(9f, 9f, 0f, true, true, 12f, 3f)
            arcTo(9f, 9f, 0f, true, true, 12f, 21f); close()
            moveTo(12f, 7f); verticalLineTo(12f); lineTo(16f, 14f)
        }
    }
    val Yaprak = ikon("yaprak") {
        cizgi {
            moveTo(6f, 18f); curveTo(0f, 9f, 10f, 3f, 20f, 4f)
            curveTo(21f, 14f, 15f, 22f, 6f, 18f); close()
            moveTo(4f, 21f); lineTo(15f, 10f)
        }
    }

    val Yenile = ikon("yenile") {
        cizgi {
            moveTo(20f, 12f)
            arcTo(8f, 8f, 0f, true, true, 17.3f, 6.1f)
            moveTo(20.5f, 3f); verticalLineTo(7.5f); horizontalLineTo(16f)
        }
    }

    val Kopyala = ikon("kopyala") {
        cizgi {
            moveTo(9f, 9f); horizontalLineTo(19f)
            arcTo(1f, 1f, 0f, false, true, 20f, 10f)
            verticalLineTo(20f)
            arcTo(1f, 1f, 0f, false, true, 19f, 21f)
            horizontalLineTo(9f)
            arcTo(1f, 1f, 0f, false, true, 8f, 20f)
            verticalLineTo(10f)
            arcTo(1f, 1f, 0f, false, true, 9f, 9f)
            close()
            moveTo(5f, 15f)
            arcTo(1f, 1f, 0f, false, true, 4f, 14f)
            verticalLineTo(4f)
            arcTo(1f, 1f, 0f, false, true, 5f, 3f)
            horizontalLineTo(15f)
            arcTo(1f, 1f, 0f, false, true, 16f, 4f)
        }
    }

    val Paylas = ikon("paylas") {
        cizgi {
            moveTo(18f, 8f)
            arcTo(2.5f, 2.5f, 0f, true, false, 18f, 3f)
            arcTo(2.5f, 2.5f, 0f, false, false, 18f, 8f)
            close()
            moveTo(6f, 14.5f)
            arcTo(2.5f, 2.5f, 0f, true, false, 6f, 9.5f)
            arcTo(2.5f, 2.5f, 0f, false, false, 6f, 14.5f)
            close()
            moveTo(18f, 21f)
            arcTo(2.5f, 2.5f, 0f, true, false, 18f, 16f)
            arcTo(2.5f, 2.5f, 0f, false, false, 18f, 21f)
            close()
            moveTo(8.2f, 10.9f); lineTo(15.8f, 6.6f)
            moveTo(8.2f, 13.1f); lineTo(15.8f, 17.4f)
        }
    }

    val Kilit = ikon("kilit") {
        cizgi {
            moveTo(6.5f, 10.5f); horizontalLineTo(17.5f)
            arcTo(1f, 1f, 0f, false, true, 18.5f, 11.5f)
            verticalLineTo(19.5f)
            arcTo(1f, 1f, 0f, false, true, 17.5f, 20.5f)
            horizontalLineTo(6.5f)
            arcTo(1f, 1f, 0f, false, true, 5.5f, 19.5f)
            verticalLineTo(11.5f)
            arcTo(1f, 1f, 0f, false, true, 6.5f, 10.5f)
            close()
            moveTo(8.5f, 10.5f); verticalLineTo(7.5f)
            arcTo(3.5f, 3.5f, 0f, false, true, 15.5f, 7.5f)
            verticalLineTo(10.5f)
        }
    }

    val Tik = ikon("tik") {
        cizgi { moveTo(5f, 12.5f); lineTo(10f, 17.5f); lineTo(19f, 6.5f) }
    }

    val Alev = ikon("alev") {
        dolgu {
            moveTo(12f, 2.5f)
            curveTo(12f, 2.5f, 6.5f, 7.4f, 6.5f, 13.2f)
            curveTo(6.5f, 16.6f, 9f, 19.2f, 12f, 19.2f)
            curveTo(15f, 19.2f, 17.5f, 16.6f, 17.5f, 13.2f)
            curveTo(17.5f, 9.6f, 14.6f, 7.9f, 14.6f, 7.9f)
            curveTo(14.6f, 7.9f, 15.1f, 10.8f, 13.5f, 11.6f)
            curveTo(13.5f, 11.6f, 14.4f, 6.3f, 12f, 2.5f)
            close()
        }
    }

    val Ses = ikon("ses") {
        cizgi {
            moveTo(4f, 9.5f); horizontalLineTo(7.5f); lineTo(12f, 5.5f); verticalLineTo(18.5f)
            lineTo(7.5f, 14.5f); horizontalLineTo(4f); close()
            moveTo(15.5f, 9f)
            arcTo(4f, 4f, 0f, false, true, 15.5f, 15f)
            moveTo(18f, 6.5f)
            arcTo(7.5f, 7.5f, 0f, false, true, 18f, 17.5f)
        }
    }

    val SesDur = ikon("ses_dur") {
        cizgi {
            moveTo(4f, 9.5f); horizontalLineTo(7.5f); lineTo(12f, 5.5f); verticalLineTo(18.5f)
            lineTo(7.5f, 14.5f); horizontalLineTo(4f); close()
            moveTo(16f, 9.5f); lineTo(21f, 14.5f)
            moveTo(21f, 9.5f); lineTo(16f, 14.5f)
        }
    }

    val Kapat = ikon("kapat") {
        cizgi { moveTo(6.5f, 6.5f); lineTo(17.5f, 17.5f); moveTo(17.5f, 6.5f); lineTo(6.5f, 17.5f) }
    }

    val Kesfet = ikon("kesfet") {
        cizgi {
            moveTo(12f, 21f)
            arcTo(9f, 9f, 0f, true, true, 12f, 3f)
            arcTo(9f, 9f, 0f, false, true, 12f, 21f)
            close()
            moveTo(15.5f, 8.5f); lineTo(13.5f, 13.5f); lineTo(8.5f, 15.5f); lineTo(10.5f, 10.5f)
            close()
        }
    }

    val Geri = ikon("geri") {
        cizgi {
            moveTo(19f, 12f); horizontalLineTo(5f)
            moveTo(11f, 6f); lineTo(5f, 12f); lineTo(11f, 18f)
        }
    }
}
