# Ascend

Motivasyon, olumlama, azim, odak ve felsefeden sözleri gününe taşıyan Android uygulaması. Kotlin, Jetpack Compose ve Material 3 ile geliştirilmiştir. Ascend 4.0, görsel ana ekranı ve medya paylaşımını merkeze alır.

## Telefonuna APK indir

1. [Actions → Ascend APK](https://github.com/yalnizfahrettin06-tech/Ascend/actions/workflows/android.yml) sayfasını aç.
2. Başarılı çalışmayı seç. İstersen **Run workflow** ile yeni derleme başlat.
3. **Artifacts → Ascend-4.0.0-test-APK** dosyasını indir ve ZIP içindeki APK’yı telefonunda aç.

GitHub’dan artifact indirmek için oturum açman gerekir. APK geliştirme imzasıyla üretilen test sürümüdür; mağazaya yükleme paketi değildir. Paket adı `.debug` ile bittiği için mevcut Azim sürümüyle yan yana kurulabilir. Farklı Actions çalışmaları farklı geliştirme imzaları oluşturabilir; güncelleme imza hatası verirse eski test sürümünün kaldırılması gerekir ve o test sürümünün verileri silinir. Kalıcı güncelleme için sabit imzalama anahtarı sonraki yayın adımıdır.

## 4.0 deneyimi

- Dört özgün manzara üzerinde kaydırılan söz kartları; konu filtreleri ve arka plan seçimi.
- Motivasyon, olumlama ve felsefeyi birlikte sunan başlangıç içeriği.
- Dört adımlı onboarding: karşılama, konular, sıklık/saat aralığı, bildirim kurulumu.
- Android izni, Samsung kısa/ayrıntılı görünüm rehberi, kanal ayarı ve deneme bildirimi.
- Görsel koleksiyonlar, arama, kaydedilen sözlerden doğrudan paylaşım ve yenilenen yolculuk ekranı.
- Story/kare/yatay PNG; 5/10/30/45 saniyelik H.264 video; fotoğraf, yazı ve arka plan seçimi.
- Paylaşım seçicisi, galeriye kaydetme, üretim ilerlemesi, iptal ve açık hata durumları.
- GitHub üzerinde APK, JVM testleri, lint ve Android emülatöründe gerçek UI/medya testleri.

## Güncel raporlar

- [Ayrıntılı ürün ve tasarım raporu](docs/05-ASCEND-4-TASARIM-RAPORU.md)
- [Özgün arka planların üretim kaydı](docs/06-GORSEL-ISTEMLERI.md)

`01`–`04` numaralı belgeler önceki 3.0 sürümünün tarihsel inceleme ve ekranlarıdır. 4.0 görünümünü temsil etmezler. Güncel doğrulama sonuçları teslim belgesine eklenir; Actions durumunu yukarıdaki bağlantıdan izleyebilirsin.

## Geliştirme

JDK 17 ve Android SDK 35:

```sh
bash gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --max-workers=2
bash gradlew :app:connectedDebugAndroidTest --max-workers=2
```

Sürüm imzası gerektiğinde `ASCEND_KEYSTORE_PATH`, `ASCEND_STORE_PASSWORD`, `ASCEND_KEY_ALIAS`, `ASCEND_KEY_PASSWORD` ortam değişkenleri kullanılır. İmza yokken release çıktısı imzasızdır. Erişim tokenları ve imzalama anahtarları depoya eklenmez. Uygulama kimliği ve DataStore anahtarları yükseltme uyumluluğu için korunur.

## Actions kullanımı

Public depolarda standart GitHub sunucularında çalışma ücretsizdir. Büyük sunucular ücretlidir; artifact ve önbellek için ayrı saklama sınırları bulunur. Bu akış standart `ubuntu-latest`, 25 dakikalık iş zaman aşımı, 7 günlük çıktı saklama ve aynı dal için eski çalışmayı iptal etme kullanır. [GitHub ücretlendirme belgesi](https://docs.github.com/en/billing/concepts/product-billing/github-actions).
