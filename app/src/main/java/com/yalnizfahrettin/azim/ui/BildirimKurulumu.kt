package com.yalnizfahrettin.azim.ui

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.notif.*

@Composable
fun BildirimOnizlemesi(dil: String) {
    Surface(color = Renk.yuzeyYuksek, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Dag, null, Modifier.size(19.dp), tint = Renk.accent)
                Text("  Ascend", color = Renk.metin, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                Text(cevir(dil, "Önizleme", "Preview"), color = Renk.metinIkincil, style = MaterialTheme.typography.labelSmall)
            }
            Text(Sozler.kategoriden("motivasyon").first().metin(dil), color = Renk.metin,
                style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Text(cevir(dil, "Tam metin için bildirimi genişlet ↓", "Expand the notification for the full quote ↓"), color = Renk.accent, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun BildirimKurulumu(dil: String, izin: Boolean, izinIste: () -> Unit) {
    val ctx = LocalContext.current
    val samsung = remember { TeslimatYardimi.acilirPencereAyariVarMi() }
    var detay by rememberSaveable { mutableStateOf(samsung) }
    fun ac(eylem: () -> Boolean) { if (!eylem()) Toast.makeText(ctx, cevir(dil, "Cihaz ayarları açılamadı. Ayarlar > Bildirimler bölümünü açabilirsin.", "Open Settings > Notifications on your device."), Toast.LENGTH_LONG).show() }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(color = Renk.yuzey, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(36.dp).background(Renk.accentZemin, CircleShape), contentAlignment = Alignment.Center) {
                        if (izin) Icon(AzimIkon.Tik, null, Modifier.size(20.dp), tint = Renk.accent)
                        else Text("01", style = MaterialTheme.typography.labelLarge, color = Renk.accent)
                    }
                    Text(cevir(dil, "Bildirim izni", "Notification permission"), fontWeight = FontWeight.SemiBold, color = Renk.metin,
                        modifier = Modifier.weight(1f).semantics { heading() })
                }
                Text(if (izin) cevir(dil, "Hazır. İstersen aşağıdan bir deneme bildirimi gönder.", "You're ready. Send a test notification below if you like.") else cevir(dil, "Seçtiğin konular, belirlediğin saatlerde. İstediğin zaman kapatabilirsin.", "Your topics, during your chosen hours. Turn them off anytime."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                if (!izin) Button(onClick = izinIste, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(cevir(dil, "Bildirimlere izin ver", "Allow notifications")) }
                TextButton(onClick = { ac { TeslimatYardimi.bildirimAyarlariniAc(ctx) } }) { Text(cevir(dil, "Uygulama bildirim ayarları ↗", "App notification settings ↗")) }
            }
        }
        BildirimOnizlemesi(dil)
        Surface(color = Renk.accentZemin, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(cevir(dil, "02  Sözü ayrıntılı gör", "02  See the whole quote"), color = Renk.metin, fontWeight = FontWeight.SemiBold)
                Text(cevir(dil, "Bildirimi aşağı doğru genişlettiğinde tam metni okuyabilirsin. Açılır pencereyi de cihazından ayarlayabilirsin.", "Expand a notification to read the full text. You can also adjust pop-ups in device settings."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { detay = !detay }) { Text(cevir(dil, "Samsung: Ayrıntılı görünüm", "Samsung: Detailed pop-up")) }
                if (detay) {
                    Text(cevir(dil, "One UI 8 ve sonrası\nBildirimler → Uygulama bildirimleri → Ascend → Bildirim açılır pencere stili → Ayrıntılı.\n\nÖnceki One UI sürümleri\nBildirimler → Bildirim açılır pencere stili → Ayrıntılı.", "One UI 8 and later\nNotifications → App notifications → Ascend → Notification pop-up style → Detailed.\n\nEarlier One UI versions\nNotifications → Notification pop-up style → Detailed."), color = Renk.metin, style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = { ac { if (samsung) TeslimatYardimi.bildirimAyarlariniAc(ctx) else TeslimatYardimi.genelBildirimAyarlariniAc(ctx) } }) { Text(cevir(dil, "Görünüm ayarlarını aç ↗", "Open appearance settings ↗")) }
                    Text(cevir(dil, "Menü adları modele göre değişebilir. Bu ayarı Ascend otomatik değiştiremez veya kontrol edemez.", "Menus vary by device. Ascend cannot change or verify this setting automatically."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = { ac { TeslimatYardimi.kanalAyarlariniAc(ctx) } }) { Text(cevir(dil, "Ekranda açılır bildirim ayarı ↗", "Pop-up alert settings ↗")) }
                Text(cevir(dil, "Açılır pencere istiyorsan bu kanalda ekranda göster seçeneğini aç. Sessiz mod ve Rahatsız Etmeyin görünümü etkileyebilir.", "For pop-up alerts, enable pop on screen for this channel. Silent mode and Do Not Disturb may affect alerts."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedButton(enabled = izin, onClick = {
            val ok = Bildirimler.goster(ctx, Sozler.kategoriden("motivasyon").first(), dil)
            Toast.makeText(ctx, if (ok) cevir(dil, "Deneme bildirimi gönderildi. Bildirim panelini açabilirsin.", "Test sent. Open your notification panel.") else cevir(dil, "Bildirim iznini kontrol et.", "Check notification permission."), Toast.LENGTH_LONG).show()
        }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(cevir(dil, "Deneme bildirimi gönder", "Send a test notification")) }
    }
}
