package com.yalnizfahrettin.azim.ui

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalnizfahrettin.azim.core.*
import com.yalnizfahrettin.azim.data.Sozler
import com.yalnizfahrettin.azim.notif.*

@Composable
fun BildirimOnizlemesi(dil: String) {
    Surface(color = Renk.yuzeyYuksek, shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AzimIkon.Alev, null, Modifier.size(19.dp), tint = Renk.accent)
                Text("  Ascend", color = Renk.metin, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                Text(cevir(dil, "Önizleme", "Preview"), color = Renk.metinIkincil, style = MaterialTheme.typography.labelSmall)
            }
            Text(com.yalnizfahrettin.azim.data.Sozler.kategoriden("motivasyon").first().metin(dil), color = Renk.metin, style = MaterialTheme.typography.bodyLarge)
            Text(cevir(dil, "Motivasyon  ·  Kaydet  ♡", "Motivation  ·  Save  ♡"), color = Renk.accent, style = MaterialTheme.typography.labelMedium)
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
        BildirimOnizlemesi(dil)
        Surface(color = Renk.yuzey, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(cevir(dil, "01  Bildirim izni", "01  Notification permission"), fontWeight = FontWeight.SemiBold, color = Renk.metin)
                Text(if (izin) cevir(dil, "İzin açık. Sıradaki adım görünümünü düzenlemek.", "Permission is on. Next, adjust how quotes appear.") else cevir(dil, "Ascend, seçtiğin saatlerde sana söz gönderebilsin.", "Let Ascend send quotes during your chosen hours."), color = Renk.metinIkincil, style = MaterialTheme.typography.bodySmall)
                if (!izin) Button(onClick = izinIste, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(cevir(dil, "Bildirimlere izin ver", "Allow notifications")) }
                TextButton(onClick = { ac { TeslimatYardimi.bildirimAyarlariniAc(ctx) } }) { Text(cevir(dil, "Uygulama bildirim ayarları ↗", "App notification settings ↗")) }
            }
        }
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
