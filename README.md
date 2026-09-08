# Ascend

Motivasyon, olumlama, azim, odak ve felsefeden özgün düşünceleri gününe taşıyan Android uygulaması. Kotlin, Jetpack Compose ve Material 3 ile geliştirilmiştir. Ascend 5.0, 70 kategorinin tamamına 700 yeni söz ve gelecekteki çeviriler için tek İngilizce kaynak getirir.

## Telefonuna APK indir

**[Ascend 5.0 test APK’sını indir](https://github.com/yalnizfahrettin06-tech/Ascend/releases/tag/v5.0.0-preview.1)** — ön sürüm sayfasındaki `Ascend-5.0.0-test.apk` dosyasını seç.

Yeni bir derleme oluşturmak için:

1. [Actions → Ascend APK](https://github.com/yalnizfahrettin06-tech/Ascend/actions/workflows/android.yml) sayfasını aç.
2. Başarılı çalışmayı seç. İstersen **Run workflow** ile yeni derleme başlat.
3. **Artifacts → Ascend-5.0.0-test-APK** dosyasını indir ve ZIP içindeki APK’yı telefonunda aç.

GitHub’dan artifact indirmek için oturum açman gerekir. APK geliştirme imzasıyla üretilen test sürümüdür; mağazaya yükleme paketi değildir. Paket adı `.debug` ile bittiği için mevcut Azim sürümüyle yan yana kurulabilir. Farklı Actions çalışmaları farklı geliştirme imzaları oluşturabilir; güncelleme imza hatası verirse eski test sürümünün kaldırılması gerekir ve o test sürümünün verileri silinir. Kalıcı güncelleme için sabit imzalama anahtarı sonraki yayın adımıdır.

## 5.0 içeriği ve çeviri kaynağı

- 12 koleksiyon, 70 kategori, her kategoride 10 yeni söz: 700 İngilizce özgün metin ve 700 Türkçe çeviri.
- **[Tek İngilizce ana kaynak](content/source.en.json)**; sabit kimliklere bağlı ayrı **[Türkçe çeviri](content/translations/tr.json)**.
- İngilizce değiştiğinde gözden geçirilmemiş çeviriyi durduran kaynak parmak izi kontrolü.
- **[English translation guide](content/README.md)**: yeni dil şablonu oluşturma, kimlikler ve editoryal kurallar. Bu sürümde uygulama dilleri Türkçe ve İngilizce; başka diller henüz entegre edilmedi.
- Felsefe ve inanç kategorilerinde açık özgün düşünce etiketi; kişilere veya kutsal metinlere uydurma atıf yok.
- Eski kaydedilmiş sözleri çözen ayrı arşiv; yeni akış ve bildirimler yalnız yeni katalogdan beslenir.
- Seçili bildirim havuzunu tamamlayan tekrar düzeni; tur sınırında hemen aynı sözü göndermeme.
- Kilitli koleksiyonlarda açıkça etiketli geçici demo: Google.com açılır, uygulamaya dönünce seçilen koleksiyon açılır. Gerçek ödüllü reklam henüz bağlı değildir.

## Görsel deneyim

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
- [5.0 ayrıntılı içerik ve araştırma raporu](docs/09-ASCEND-5-ICERIK-RAPORU.md)
- [5.0 doğrulama ve bilinen sınırlar](docs/10-ASCEND-5-DOGRULAMA.md)
- [5.0 gerçek uygulama ekranları](docs/11-ASCEND-5-EKRANLAR.md)
- [Özgün arka planların üretim kaydı](docs/06-GORSEL-ISTEMLERI.md)

`01`–`04` numaralı belgeler 3.0, `05`–`08` numaralı belgeler 4.0 sürümünün tarihsel kayıtlarıdır. Güncel 5.0 kapsamı ve doğrulama sonuçları `09`–`11` numaralı belgelerdedir.

## Geliştirme

JDK 17 ve Android SDK 35:

```sh
python tools/icerik_derle.py --check
bash gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --max-workers=2
bash gradlew :app:connectedDebugAndroidTest --max-workers=2
```

Sürüm imzası gerektiğinde `ASCEND_KEYSTORE_PATH`, `ASCEND_STORE_PASSWORD`, `ASCEND_KEY_ALIAS`, `ASCEND_KEY_PASSWORD` ortam değişkenleri kullanılır. İmza yokken release çıktısı imzasızdır. Erişim tokenları ve imzalama anahtarları depoya eklenmez. Uygulama kimliği ve DataStore anahtarları yükseltme uyumluluğu için korunur.

## Actions kullanımı

Public depolarda standart GitHub sunucularında çalışma ücretsizdir. Büyük sunucular ücretlidir; artifact ve önbellek için ayrı saklama sınırları bulunur. Bu akış standart `ubuntu-latest`, 25 dakikalık iş zaman aşımı, 7 günlük çıktı saklama ve aynı dal için eski çalışmayı iptal etme kullanır. [GitHub ücretlendirme belgesi](https://docs.github.com/en/billing/concepts/product-billing/github-actions).
