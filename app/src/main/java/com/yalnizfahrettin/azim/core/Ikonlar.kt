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
 * Buradaki ikonlar elle çizildi: 24x24 viewport, 2dp çizgi, yuvarlatılmış
 * uçlar — tek görsel dil. "Dolu" varyantlar aktif durum için.
 */
private fun ikon(ad: String, ciz: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = ad, defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f,
    ).apply(ciz).build()

private fun ImageVector.Builder.cizgi(blok: PathBuilder.() -> Unit) = path(
    stroke = SolidColor(Color.Black),
    strokeLineWidth = 2f,
    strokeLineCap = StrokeCap.Round,
    strokeLineJoin = StrokeJoin.Round,
    pathBuilder = blok,
)

private fun ImageVector.Builder.dolgu(blok: PathBuilder.() -> Unit) =
    path(fill = SolidColor(Color.Black), pathBuilder = blok)

object AzimIkon {

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
            moveTo(12f, 15.2f)
            arcTo(3.2f, 3.2f, 0f, true, false, 12f, 8.8f)
            arcTo(3.2f, 3.2f, 0f, false, false, 12f, 15.2f)
            close()
            moveTo(12f, 2.6f); lineTo(13.4f, 5.2f); lineTo(16.3f, 4.6f); lineTo(16.6f, 7.6f)
            lineTo(19.3f, 8.8f); lineTo(17.8f, 11.3f); lineTo(19.3f, 13.8f); lineTo(16.6f, 15f)
            lineTo(16.3f, 18f); lineTo(13.4f, 17.4f); lineTo(12f, 20f); lineTo(10.6f, 17.4f)
            lineTo(7.7f, 18f); lineTo(7.4f, 15f); lineTo(4.7f, 13.8f); lineTo(6.2f, 11.3f)
            lineTo(4.7f, 8.8f); lineTo(7.4f, 7.6f); lineTo(7.7f, 4.6f); lineTo(10.6f, 5.2f)
            close()
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
