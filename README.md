# Ascend

Motivasyon, olumlama, azim, odak ve felsefeden özgün düşünceleri gününe taşıyan Android uygulaması. Kotlin, Jetpack Compose ve Material 3 ile geliştirilmiştir.

## 6.0 yenilikleri

- A harfi ve dağı birleştiren uygulama simgesi, yumuşak alt gezinme ve yenilenen Yolculuk.
- Her birinin kendine ait görseli olan **70 ayrı kategori**, kategori başına 10 özgün söz. Koleksiyonlar yalnız keşfetme filtresidir.
- Ana ekranda ve Keşfet'te açıkça listelenen seçili bildirim konuları.
- Yeni bildirim planlayıcısı: günlük 1–7 hatırlatma, başlangıç/bitiş saati kartları ve gerçek yaklaşık gönderim saatleri.
- Ayrıntılı Android bildirim izni, Samsung görünüm rehberi, kanal ayarı ve deneme bildirimi.
- Dağ, orman ve deniz yanında arena, kale, dağ hisarı, şövalye, cadı, yedi dokulu renk ve sekiz şövalye sahnesi dahil **23 ana ekran sahnesi**.
- Paylaşımda Zirve, Gece ve Kâğıt ücretsiz. Diğer arka planlar, fotoğraf, biçim/yazı düzenleme ve 5/10/30/45 saniyelik video Pro.
- **Pro demosu** tüm kategorileri ve paylaşım araçlarını açar. Ödeme alınmaz ve abonelik başlatılmaz. Demo kapatılabilir; bildirim konuları kullanıcı seçimine bağlıdır.
- Yeni kurulumda 6 ücretsiz kategori. Geçici reklam demosu Google.com'u açar; dönüşte yalnız seçilen kategori açılır. Gerçek reklam bağlı değildir.
- Önceki kullanıcıların açık kategorileri, kaydedilmiş sözleri ve geçmişi uygulama içi geçişte korunur.

## APK ve doğrulama

**[Ascend 6.0 test APK’sını indir](https://github.com/yalnizfahrettin06-tech/Ascend/releases/tag/v6.0.0-preview.1)** — sürüm sayfasından `Ascend-6.0.0-test.apk` dosyasını seç. 48 JVM ve 26 Android testi geçti; lint hatası yok. [Doğrulama ve sınırlar](docs/13-ASCEND-6-TEST-PLANI.md) · [Gerçek ekranlar](docs/14-ASCEND-6-EKRANLAR.md).

[Actions → Ascend APK](https://github.com/yalnizfahrettin06-tech/Ascend/actions/workflows/android.yml) başarılı çalışmasında **Artifacts → Ascend-6.0.0-test-APK** paketini indir. Artifact indirmek için GitHub oturumu gerekir.

APK geliştirme imzalı test sürümüdür; mağazaya yükleme paketi değildir. Paket adı `.debug` ile bittiği için eski Azim ile yan yana kurulabilir. Farklı Actions çalışmaları farklı geliştirme imzaları oluşturabilir. Güncelleme imza hatası verirse eski test sürümünün kaldırılması gerekir ve o test sürümünün verileri silinir. Kalıcı güncelleme için sabit imzalama anahtarı gerekir.

## İngilizce ana kaynak

- [Tek İngilizce kaynak](content/source.en.json): 700 özgün metin ve sabit kimlikler.
- [Türkçe çeviri](content/translations/tr.json) ve [English translation guide](content/README.md).
- İngilizce değiştiğinde gözden geçirilmemiş çeviriyi durduran kaynak parmak izi kontrolü.
- Uygulama dilleri Türkçe ve İngilizce; diğer diller henüz entegre edilmedi.
- Felsefe ve inanç metinleri özgün düşünceler olarak etiketlenir; kişilere veya kutsal metinlere uydurma atıf yapılmaz.
- Eski kaydedilmiş sözler ayrı arşivden çözülür; yeni akış ve bildirimler yeni katalogdan beslenir.

## Raporlar

- [6.0 UI/UX, kategori ve erişim araştırması](docs/12-ASCEND-6-UI-UX-VE-ERISIM.md)
- [6.0 doğrulama ve kabul planı](docs/13-ASCEND-6-TEST-PLANI.md)
- [Kategori görselleri ve üretim kaydı](docs/category-art.json)
- [Yeni sahnelerin üretim kaydı](docs/scene-art.json)
- [5.0 içerik araştırması](docs/09-ASCEND-5-ICERIK-RAPORU.md)

01–11 numaralı belgeler önceki sürümlerin tarihsel kayıtlarıdır.

## Geliştirme

JDK 17 ve Android SDK 35:

```sh
python tools/icerik_derle.py --check
python tools/check_category_art.py
bash gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --max-workers=2
bash gradlew :app:connectedDebugAndroidTest --max-workers=2
```

Sürüm imzası için `ASCEND_KEYSTORE_PATH`, `ASCEND_STORE_PASSWORD`, `ASCEND_KEY_ALIAS`, `ASCEND_KEY_PASSWORD` kullanılır. İmza yokken release çıktısı imzasızdır. Tokenlar ve imzalama anahtarları depoya eklenmez. Uygulama kimliği ve DataStore anahtarları uyumluluk için korunur.

## Actions kullanımı

Public depolarda standart GitHub sunucularında çalışma ücretsizdir. Büyük sunucular ücretlidir; artifact ve önbellek için ayrı saklama sınırları bulunur. Akış standart `ubuntu-latest`, 25 dakikalık iş zaman aşımı, 7 günlük çıktı saklama ve aynı dal için eski çalışmayı iptal etme kullanır. [GitHub ücretlendirme belgesi](https://docs.github.com/en/billing/concepts/product-billing/github-actions).
