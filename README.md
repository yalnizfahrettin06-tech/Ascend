# Ascend

Günlük olumlamalar için Kotlin, Jetpack Compose ve Material 3 ile geliştirilmiş Android uygulaması. Azim kaynaklarından Ascend 3.0 deneyimine geçiş.

## Telefonuna APK indir

1. [Actions → Ascend APK](https://github.com/yalnizfahrettin06-tech/Ascend/actions/workflows/android.yml) sayfasını aç.
2. Başarılı çalışmayı seç. İstersen **Run workflow** ile yeni derleme başlat.
3. **Artifacts → Ascend-3.0.0-test-APK** dosyasını indir ve ZIP içindeki APK’yı telefonunda aç.

GitHub’dan artifact indirmek için oturum açman gerekir. APK geliştirme imzasıyla üretilen test sürümüdür; mağazaya yükleme paketi değildir. Paket adı `.debug` ile bittiği için mevcut Azim sürümüyle yan yana kurulabilir. Farklı Actions çalışmaları farklı geliştirme imzaları oluşturabilir; güncelleme imza hatası verirse eski test sürümünün kaldırılması gerekir ve o test sürümünün verileri silinir. Kalıcı güncelleme için sabit imzalama anahtarı sonraki yayın adımıdır.

## Neler değişti?

- İlk olumlamayla başlayan, geri dönülebilen üç adımlı onboarding.
- Altı başlangıç niyeti, 24 özgün Türkçe/İngilizce olumlama.
- Bildirimsiz devam, atomik kurulum kaydı, ayarlarda hatırlatıcı anahtarı.
- Olumlama odaklı Bugün ekranı, görünür önceki/sonraki ve kaydet/paylaş eylemleri.
- Keşfet, Kaydedilenler, Yolculuk ve ayarlarda okunabilirlik/erişilebilirlik iyileştirmeleri.
- Bildirim teslimatını okumadan ayıran sayaçlar ve yenilenen zamanlama.
- GitHub üzerinde APK derleme, birim testleri, Android lint ve emülatör UI testleri.

## Raporlar

Ayrıntılı analiz, onboarding tasarımı ve entegrasyon/doğrulama raporları `docs/` klasörüne eklenir. Kod içindeki eski sürümlere atıf yapan yorumlar tarihsel kaynak notlarıdır; doğrulanmış ürün araştırması sayılmaz.

## Geliştirme

JDK 17 ve Android SDK 35:

```sh
bash gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --max-workers=2
bash gradlew :app:connectedDebugAndroidTest --max-workers=2
```

Sürüm imzası gerektiğinde `ASCEND_KEYSTORE_PATH`, `ASCEND_STORE_PASSWORD`, `ASCEND_KEY_ALIAS`, `ASCEND_KEY_PASSWORD` ortam değişkenleri kullanılır. İmza yokken release çıktısı imzasızdır. Erişim tokenları ve imzalama anahtarları depoya eklenmez. Uygulama kimliği ve DataStore anahtarları yükseltme uyumluluğu için korunur.

## Actions kullanımı

Public depolarda standart GitHub sunucularında çalışma ücretsizdir. Büyük sunucular ücretlidir; artifact ve önbellek için ayrı saklama sınırları bulunur. Bu akış standart `ubuntu-latest`, 25 dakikalık iş zaman aşımı, 7 günlük çıktı saklama ve aynı dal için eski çalışmayı iptal etme kullanır. [GitHub ücretlendirme belgesi](https://docs.github.com/en/billing/concepts/product-billing/github-actions).
