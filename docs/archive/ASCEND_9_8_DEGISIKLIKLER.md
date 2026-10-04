# Ascend 9.8.0 — Daha az seçim, daha açık akış

## Uygulanan kararlar
- Dil adımında Aa/Merhaba/Hello paneli kaldırıldı. Ascend yazısı Inter SemiBold oldu; ana ekran söz fontu korundu.
- Tema seçimi yukarı taşındı: Beyaz, Siyah, Roma, Koyu Atlı Yolcu, Şövalye, Orman. Beyaz/Siyah ücretsiz; dört görsel tema Pro. Atlı Yolcu paylaşım kütüphanesindeki orijinal resimdir.
- Tema seçimi sonrası üç günlük deneme teklif ekranı gelir. Pro teması önizlenebilir; demo açılırsa seçilen tema uygulanır. Ücretsiz devam edilirse koyu Pro teması Siyah, açık Pro teması Beyaz olur.
- Deneme açıkça demodur: ödeme alınmaz, abonelik başlamaz, üç gün sayacı çalışmaz. Gerçek Play Billing eklenmedi.
- Keşfet: arama + tek koleksiyon listesi. Koleksiyon açılınca konular görünür. Bildirimlerim tek bağlantıdır; seçili konulara erişim sürer. Kilitli konu satırında Pro rozeti görünür.
- Görünüm sağ üst düğmesinden açılır. Widget girişi tek kısa satırdır.
- Widget: arka plan seç, önizle, ekle. Boyut, hizalama, yazı boyutu kontrolleri kaldırıldı. Yazı ortalı ve sabit düzendedir; sistem büyük yazı ayarı korunur. Günün rastgele sözü gün boyunca sabit kalır, sistemin widget yenilemesinde yeni güne geçer. İçerik tercihleri, erişim ve gizlenen sözler dikkate alınır.
- Paylaş: görsel/video, arka plan veya kişisel fotoğraf ve kaydet/paylaş. Yazı, yerleşim, oran ve hareket editörü kaldırıldı; tutarlı dikey çıktı kullanılır.
- Bildirim görünümü bağlantısı izin ekranında başlık altına alındı. Kanalın açılır bildirim ayarlarına, Samsung cihazlarında ayrıca ayrıntılı görünüm ayarlarına yönlendirme sunulur.

## Android bildirim araştırması
Tam bildirim arka planını uygulamanın temasıyla değiştirmek Android 12 ve üstünde serbest değildir; sistem standart bir şablon uygular. BigPictureStyle genişletilmiş görsel destekler, ancak bu tüm bildirimin arka planı değildir. Sözün tamamını koruyan BigTextStyle kullanımı sürdürüldü. Yüksek önem seviyesi açılır bildirim için uygundur; kullanıcı kanal ayarları ve Rahatsız Etmeyin modu sonucu belirler. Uygulama cihazın ayrıntılı görünüm anahtarını zorla açamaz.

- https://developer.android.com/develop/ui/views/notifications/custom-notification
- https://developer.android.com/about/versions/12/behavior-changes-12
- https://developer.android.com/reference/android/app/Notification.BigPictureStyle

## Teslim ve doğrulama sınırı
Kaynak, birim testleri, lint ve APK derlemesi GitHub Actions ile yapılır. Kullanıcı isteği doğrultusunda APK sonrası uzun emülatör/görsel kontrol turu yapılmaz. Üretim ödeme entegrasyonu ve cihazlara özgü açılır bildirim görünümü bu teslimde doğrulanmış sayılmaz. Eski tasarımların arşiv Android testleri yeni ekran yapısını temsil etmez; güncel isteğe bağlı akış VisualAcceptanceTest ve OnboardingTest üzerindedir.
