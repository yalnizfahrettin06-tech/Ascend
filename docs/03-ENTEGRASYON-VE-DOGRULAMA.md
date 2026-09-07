# Ascend — Entegrasyon, APK ve doğrulama raporu

## 1. Teslimatın tanımı

Kaynak ZIP boş GitHub deposuna alınarak Ascend 3.0.0 test sürümü oluşturuldu. Çalışma, onboarding tasarımının koda entegrasyonunu ve günlük deneyimdeki temel etkileşim düzeltmelerini kapsar. Mağazada yayımlanmış bir üretim sürümü değildir. Derleme/test sonuçları bu raporun sonunda güncellenir.

## 2. Uygulanan değişiklik haritası

| Katman | Dosyalar | Değişiklik |
|---|---|---|
| Başlangıç | `ui/Onboarding.kt`, `data/Baslangic.kt` | Üç adım, ilk olumlama, altı niyet, geri, seçim doğrulaması, bildirimsiz bitirme |
| İçerik | `data/Olumlamalar.kt`, `Kategoriler.kt`, `Sozler.kt` | 24 özgün TR/EN olumlama; üç yeni ücretsiz konu; tüm içerik erişim işlevlerine entegrasyon |
| Kalıcılık | `data/Depo.kt` | Atomik onboarding kaydı, hatırlatıcı tercihi, günlük benzersiz görüntüleme, teslimat geçmişi ayrımı |
| Kök akış | `ui/Uygulama.kt` | Yükleme kapısı, sistem geri, konu değişiminde akış yenileme, favoriden okuma |
| Ana ekran | `ui/AnaEkran.kt` | Yeni içerik hiyerarşisi, gerçek sıra, önceki/sonraki, görünür eylemler, hatırlatıcı durumları |
| Navigasyon | `ui/Iskelet.kt` | Material NavigationBar; Bugün, Keşfet, Kaydedilen, Yolculuk |
| Ekran düzeltmeleri | `AyarlarEkrani.kt`, `FavorilerEkrani.kt`, `KategorilerEkrani.kt`, `IstatistikEkrani.kt` | Güvenli alanlar, erişilebilir seçimler, boş durum eylemi, dürüst kilit durumu |
| Tasarım | `core/Tasarim.kt`, `Paletler.kt` | Başlık/gövde ölçeği, küçük metin kontrastı, koyu tema CTA ön planı |
| Dil ve izin | `MainActivity.kt`, `res/values*` | Arayüz kaynaklarının dile bağlanması, geri dönüşte izin güncellemesi, Ascend metinleri |
| Bildirim | `BildirimZamanlari.kt`, `Planlayici.kt`, `Bildirimler.kt` | Aralık içinde plan, eski işleri temizleme, kapalı tercih/izin kontrolü, teslimat sonucuna göre kayıt |
| Paylaşım | `KartCizici.kt`, `PaylasimKarti.kt`, `VideoUretici.kt` | Ascend imzası ve dosya adları; mevcut üretim motoru korunur |
| Derleme | `app/build.gradle.kts`, `.github/workflows/android.yml` | Kaynaktan imza bilgilerinin çıkarılması, APK/unit/lint/UI işleri |

## 3. Veri ve sürüm uyumluluğu

`applicationId` ve Kotlin paket adı `com.yalnizfahrettin.azim` olarak korunur. Görünür isim değişikliği paket kimliği değişikliği gerektirmez. DataStore adı, kategori ve favori kimlikleri korunur. Eski kullanıcıların seçimi zorla yeni varsayılana çevrilmez; yeni varsayılan yalnız kayıtlı seçim bulunmadığında uygulanır.

Hatırlatıcı tercihi yeni bir anahtardır. Önceden onboarding tamamlanmış fakat bu anahtar yazılmamışsa eski hatırlatıcı davranışı açık kabul edilir; yeni kurulumda açık veya kapalı seçimi açıkça yazılır. İşletim sistemindeki izin ayrıca kontrol edilir.

Test APK’sı `.debug` paket ekiyle kurulur. Böylece mevcut üretim uygulamasını yerinden etmez; verileri de otomatik paylaşmaz. Azim’in üretim kullanıcı verisini Ascend test APK’sında görmemek bu nedenle beklenir. Gerçek yükseltme testi için aynı paket ve önceki üretim imzası gerekir.

## 4. GitHub derleme düzeni

Workflow: **Ascend APK**. `main` kod değişikliğinde, pull request’te veya elle başlatıldığında çalışır. Yalnız rapor/Markdown değişiklikleri gereksiz APK üretmez. Aynı dalda yeni çalışma başlarsa eski çalışma iptal edilir.

`build` işi JDK 17 üzerinde debug APK, JVM testleri ve Android lint çalıştırır. Kontroller geçerse `Ascend-3.0.0-test-APK` çıktısı yüklenir. Test/lint raporları başarısızlıkta da saklanır.

`ui-tests` işi build başarılı olduktan sonra Android 35 x86_64 emülatörde Compose testlerini çalıştırır. Onboarding ekranları ve büyük yazı görünümü için görüntüler alınır. Bunlar gerçek Android görüntüleridir; tasarım maketi diye sunulmaz. Başarısız testte görüntü indirme adımına ulaşılamaması mümkündür; XML/HTML test raporları yine saklanır.

Her iş 25 dakika ile sınırlıdır. Çıktılar 7 gün saklanır. Standart `ubuntu-latest` kullanılır. Public depoda standart runner çalışması ücretsizdir; büyük runner ve depolama ayrı kurallara tabidir. Mevcut hesabın faturalama bütçesi değiştirilmedi. [GitHub Actions ücretlendirmesi](https://docs.github.com/en/billing/concepts/product-billing/github-actions).

## 5. APK indirme adımları

1. [Ascend APK çalışmalarını](https://github.com/yalnizfahrettin06-tech/Ascend/actions/workflows/android.yml) aç.
2. Son başarılı çalışmayı seç; tamamlanan testlerle aynı kod revizyonu olduğundan emin ol.
3. Artifacts bölümündeki **Ascend-3.0.0-test-APK** dosyasını indir. İndirme için GitHub oturumu gerekir.
4. ZIP’i aç ve içindeki APK’yı Android cihazına aktar. Android’in bu dosyayı açan uygulama için istediği yükleme izniyle kurulumu tamamla.

APK indirmek için Android Studio, JDK veya yerel Gradle kurman gerekmez. Artifact süresi dolarsa **Run workflow** ile yeni çıktı üretilebilir.

## 6. İmza ve erişim durumu

Erişim tokenı kaynak, rapor veya workflow dosyasına yazılmadı. ZIP’te sabit yazılmış release imza bilgileri ilk public commit’ten önce ortam değişkenlerine taşındı. Public kaynakta erişim tokenı ve sabit imza parolası taraması yapıldı.

Sabit test imzasını depo sırrına ekleme girişimi GitHub tarafından **403 Resource not accessible by personal access token** yanıtıyla reddedildi. Bu, kod gönderme izninden ayrı bir **Secrets** yetkisidir. Workflow yalnız bu sır sonradan yapılandırılırsa kullanabilir; şimdilik standart debug imzasına düşer. Sonraki bağımsız derleme farklı debug anahtarı üretirse eski test APK’sının üzerine kurulamayabilir. Kaldırıp kurmak o test paketinin verilerini siler. Kalıcı test/üretim anahtarıyla imzalama henüz tamamlanmış değildir.

İsteğe bağlı workflow sırrı: `ASCEND_TEST_KEYSTORE_B64`. Üretim sürümü için ayrı `ASCEND_KEYSTORE_PATH`, `ASCEND_STORE_PASSWORD`, `ASCEND_KEY_ALIAS`, `ASCEND_KEY_PASSWORD` ortam değişkenleri gerekir. Yeni bir test anahtarı önceki üretim anahtarının yerini tutmaz. Mevcut kaynakta imza yokken release çıktısı imzasızdır ve doğrudan telefona kurulacak test çıktısı olarak kullanılmaz.

## 7. Test kapsamı

| Kontrol | Doğruladığı davranış | Doğrulamadığı |
|---|---|---|
| Başlangıç birim testleri | Ücretsiz/geçerli başlangıç konuları, varsayılan olumlama havuzu, seçim süzme | Kullanıcı tercihinin uygunluğu |
| Zamanlama birim testleri | Dar aralık, yedi farklı saat, geçmişin elenmesi, gece yarısı sınırı | Doze/OEM üzerinde gerçek teslimat zamanı |
| İçerik testleri | TR/EN metin varlığı, uzunluk, kimlik ve kategori benzersizliği, göç | Eski alıntıların tarihsel atıf doğruluğu |
| Kontrast testleri | Paletlerde ana/ikincil/küçük metin ve düğme ön planı | Tüm özel paylaşım arka planları veya tam erişilebilirlik uyumu |
| Onboarding UI testleri | Bildirimsiz bitirme, opt-in, boş seçim, geri, kayıtlı durum, büyük yazı | Gerçek Android izin penceresinde tüm OEM davranışları |
| Gerçek uygulama testi | Türkçe kurulum, kalıcı durumdan yönlendirme, kaydetme, sekmeler, İngilizce kaynak değişimi | Üretim paketinden yükseltme ve fiziksel cihaz paylaşımı |
| Android lint | Statik Android API, kaynak ve izin denetimi | Cihazdaki her çalışma zamanı hatası |

## 8. İlk doğrulama ve düzeltme

İlk Actions çalışması APK’yı derledi ve JVM testlerini geçti; lint `MissingPermission` hatasıyla durdu. Bildirim gönderiminin `SecurityException` durumunu açıkça yakalaması sağlandı. Lint devre dışı bırakılmadı, baseline ile hata gizlenmedi. Sonraki çalışma APK/test/lint aşamasını geçti. UI testlerinin ve son revizyonun nihai sonucu aşağıdaki kanıt kaydında tutulur.

Başlangıç lint raporunda 74 uyarı vardı; büyük kısmı kullanılmayan kaynaklar, çoğul metin adayları ve bağımlılık güncelleme önerileriydi. Dil bölünmesi ve TTS görünürlüğüyle ilgili uyarılar ayrıca ele alındı. Bağımlılıkların tamamı bu tasarım görevi sırasında gelişigüzel yükseltilmedi. Hatasız lint, sıfır uyarı anlamına gelmez.

## 9. Kalan doğrulama ve ürün işleri

- Fiziksel Android 13+ cihazda izin verme/reddetme; sistem ayarlarından kapatma/açma; Samsung gibi üreticilerde teslimat.
- Uçak modu, yeniden başlatma, saat dilimi değişikliği, dar zaman aralığı ve frekans azaltma senaryolarının cihazdaki testi.
- TalkBack okuma sırası, switch satırlarının etiket ilişkisi, yatay ekran ve tablet yerleşimi.
- Fotoğraf arka planı, görsel paylaşımı, video üretimi/iptali ve uygulama arka plana geçince medya davranışı.
- Önceki üretim imzasıyla yükseltme ve favori/veri devamlılığı.
- Kategori havuzlarının editoryal tamamlanması; eski alıntı atıflarının kaynak denetimi.
- Üretim mağaza hazırlığı ve gerçek reklam sağlayıcısı kararı.

Bu maddeler tamamlanmış gibi işaretlenmez. Teslimatın hedefi çalışan, incelenebilir bir Ascend test sürümü ve onboarding/UI/UX entegrasyonunun ilk kapsamlı aşamasıdır.

## 10. Nihai kanıt kaydı

Son çalışma ve çıktı bağlantıları, tamamlanan test sayıları ve görüntü inceleme sonucu doğrulama bitiminde bu bölüme eklenir.
