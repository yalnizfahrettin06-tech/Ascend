# Ascend 4.0 — Teslim ve doğrulama

8 Eylül 2026 (Türkiye saati). [GitHub doğrulama çalışması](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34163234314).

Test edilen uygulama kodu: `14ef150281612edd4fc1b7dd7899d6af0a71d0f3`. Sonraki teslim commit’i yalnız README, rapor ve ekran görüntülerini ekler. İndirilen APK’nın Android paket bilgisi ve APK Signature Scheme v2 imzası ayrıca doğrulandı. İki Actions işinin de sonuç açıklamalarında uyarı yok.

## Teslim

- APK: **Ascend-4.0.0-test.apk** — 19.83 MiB.
- Paket: `com.yalnizfahrettin.azim.debug`; sürüm 4.0.0, versionCode 13.
- Android 8.0 ve üzeri (minSdk 26); hedef/derleme API 35.
- Ayrıntılı ürün raporu, gerçek ekran galerisi, kaynak görsel istemleri ve örnek paylaşım dosyaları.
- Kaynak kod [Ascend deposunun main dalında](https://github.com/yalnizfahrettin06-tech/Ascend).

SHA-256: `7df422b11e0f42fd5aeeaef6fbf472365db6a37aefda28470cc34229f01fde4d`

## Kontrol sonuçları

30 JVM testi geçti. Android 15 / API 35 emülatöründe **9 cihaz testi geçti**; başarısız veya atlanan test yok. Android lint 0 hata, 135 uyarı ile tamamlandı. Önemli eski Actions uyarıları giderildi: Node.js 20 adımları güncellendi, Gradle wrapper çalıştırma izni düzeltildi. Android 8–11 yedekleme kuralı da açıkça tanımlandı.

| Test grubu | Senaryo | Sonuç |
|---|---|---|
| MedyaTest | galleryImageHasCorrectDimensionsAndCompletedRecord | Geçti |
| MedyaTest | videosArePlayableAtEveryOfferedDuration | Geçti |
| MedyaTest | expandedNotificationKeepsLongQuoteAndAttribution | Geçti |
| OnboardingTest | permissionRequestDoesNotCompleteOnboardingUntilExplicitStart | Geçti |
| OnboardingTest | recreationRestoresStepAndTopicChoice | Geçti |
| OnboardingTest | skipKeepsBalancedSelectionAndDoesNotOptIn | Geçti |
| OnboardingTest | usersCanReplaceAllDefaultsAndBackPreservesSelection | Geçti |
| OnboardingTest | largeTextCanReachNotificationSetupAndSkip | Geçti |
| UygulamaTest | realNavigationShareAndLanguage | Geçti |

Video testleri 5, 10, 30, 45 saniyelik çıktıları üretti, MediaMetadataRetriever ile süreyi ±150 ms toleransla doğruladı ve son bölümden görüntü karesi çözdü. Story video genişliği 720 piksel olarak kontrol edildi. Görsel galeri kaydı 1080×1920 boyut ve tamamlanmış MediaStore kaydıyla doğrulandı. Bildirim testi en uzun mevcut sözün ve yazarın genişletilmiş gövdede eksiksiz olduğunu denetledi.

Onboarding testleri bildirimsiz devam, boş konu seçimini engelleme, geri dönüşte seçimleri koruma, izin istemeyle kurulumu tamamlama adımlarını ayırma ve yeniden oluşturma sonrasında durumu korumayı kapsar. Büyük yazı testinde 2× ölçek kullanılır. Gerçek uygulama testi ana ekran, kaydetme, paylaşım, galeri, keşfet, yolculuk ve İngilizce dil geçişini dolaşır; paylaşım taslağını ekran yeniden oluşturulduktan sonra denetler.

## Görsel inceleme

Galerideki PNG dosyaları tasarım maketi değildir; GitHub emülatöründe çalışan Android uygulamasından alınmıştır. Ana ekran, onboarding, bildirim rehberi, paylaşım modları, kaydedilenler, keşfet, yolculuk ve koyu tema kontrol edilmiştir. Örnek video ve görsel, aynı Android medya koduyla üretilmiştir.

## Bilinçli bırakılan uyarılar

Kalan lint uyarılarının önemli bölümü önceki ekranlardan kalmış kullanılmayan kaynaklar, daha yeni bağımlılık sürümleri ve çoğul metin önerileridir. Uyarıyı susturmak için bütün Android bağımlılıkları veya hedef API topluca yükseltilmedi. Bunlar sonraki bakım sürümünde ayrı değerlendirilmelidir. Hatalar gizlenmedi ve lint denetimi kapatılmadı.

## Test kapsamının sınırı

Bu bir geliştirme imzalı test APK’sıdır. Farklı Actions koşularının imzası farklı olabilir; mevcut test sürümünün üzerine kurulum imza hatası verirse kaldırıp kurmak gerekir ve kaldırma o test uygulamasındaki verileri siler. Üretim yayını için sabit sürüm anahtarı gerekir; erişim anahtarı kaynak koda konmadı.

Samsung’un gerçek One UI arayüzü ve farklı üreticilerin donanım video kodlayıcıları bu emülatörle doğrulanamaz. Ayrıntılı görünüm rehberi resmî Samsung belgelerine dayanır; uygulama cihazın bu tercihini otomatik değiştirdiğini iddia etmez. WorkManager bildirimleri pil/sistem koşullarında gecikebilir. Android 8/9 için dosya seçicisi yolu uygulanmıştır; bu doğrulama koşusunda API 35 kullanılmıştır. Diğer kilitli koleksiyonların reklamla açılması bu sürümde hazır değildir.
