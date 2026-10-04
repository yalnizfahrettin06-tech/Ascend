# Ascend 5 — Doğrulama ve teslim

**Tarih:** 8 Eylül 2026

**Sürüm:** 5.0.0 / 14

**Test edilen kod:** `36e89559cf6f8c82b9b041c542aa7e143711d9c4`

**Başarılı GitHub Actions:** [Derleme ve cihaz testleri](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34202753211)

## Sonuç

700 İngilizce özgün metin ve bunlara bağlı 700 Türkçe çeviri uygulamaya entegre edildi. 70 kategorinin her birinde 10 söz var. Kaynak ile üretilen uygulama verisi eşleşiyor. Son çalışmada APK derlemesi, **38 JVM testi ve 15 Android testi** başarılı; atlanan Android testi yok.

| Kontrol | Sonuç |
|---|---|
| Tek İngilizce kaynak / Türkçe kimlik eşleşmesi | 700 / 700, eksik veya yetim kayıt yok |
| Yerel çeviri ve şablon kontrolleri | 8 kontrol başarılı |
| Birim testleri | 38 geçti, 0 hata, 0 atlanan |
| Android 15 cihaz testleri | 15 geçti, 0 hata, 0 atlanan |
| Android lint | 0 hata, 137 uyarı |
| APK imzası | apksigner doğrulandı; v2 etkin |
| Paket | `com.yalnizfahrettin.azim.debug` |
| Android desteği | minSdk 26, targetSdk 35 |
| APK boyutu | 20,890,463 bayt |
| APK SHA-256 | `df6955d74cf6e1074afc8599fbe63c4626af7dd420806ef97b3893731e072bdf` |

## İçerik ve veri kontrolleri

Sabit söz kimlikleri metin değişiminden etkilenmiyor. Her kategori tam 10 kayıt içeriyor. Türkçe veya İngilizce seçimi sözün kimliğini değiştirmiyor; desteklenmeyen içerik dilinde İngilizceye dönülüyor. Bu bir ek dilin arayüzde entegre olduğu anlamına gelmiyor.

Önceki sürümün 82 arşiv kimliği çözülüyor; bunlar yeni kategori, akış ve bildirim havuzuna girmiyor. İki tam bildirim turunda seçilmemiş kategoriye çıkılmaması, kullanılmamış kayıt varken erken tur başlamaması ve tur sınırında hemen aynı sözün seçilmemesi doğrulandı.

Gerçek Android DataStore ile izole testte 700 kaydın geçmişte korunması, eski kimliklerin temizlenmesi, yeni turda yalnız seçili kategorilerin geçmişinin yenilenmesi ve aynı gün yeniden okunmuş sözün günlük sayacı artırmadan tur geçmişine dönmesi doğrulandı. Test deposu gerçek kullanıcı deposundan ayrı tutuldu.

Yerel 8 kontrol; ana kaynak/üretilmiş veri eşleşmesi, İngilizce değişikliğinde eski çevirinin reddi, eksik çeviri, sürüm uyuşmazlığı, fazla uzun çeviri, yinelenen çeviri, 700 boş kayıtlı Almanca şablon ve var olan şablonun üzerine yazmanın reddini kapsar. Almanca dosya bir test şablonuydu; yayımlanan çeviri değildir.

## Gerçek ekran, izin ve medya akışları

Ana uygulama testinde onboarding, kategori seçimi, bildirim sıklığı ve saatleri, Android izin kurulumu/Samsung ayrıntılı görünüm rehberi, bildirimsiz devam, ana ekran, kaydetme, görsel paylaşım ve galeriye kayıt, Keşfet, Yolculuk, karanlık tema ve İngilizce geçişi çalıştırıldı. 45 saniyelik video seçiminin ekran yeniden oluşturulduğunda korunması kontrol edildi.

Ayrı medya testleri PNG'nin 1080×1920 boyutlarını ve galeri kaydının tamamlanmasını doğruladı. 5/10/30/45 saniyelik MP4 dosyalarının süreleri, 720 piksel genişliği ve son bölümlerinden okunabilir kare alınması kontrol edildi. Android'e gerçekten gönderilen bildirimin genişletilmiş metninde en uzun Türkçe sözün ve imzasının korunduğu doğrulandı. Bunlar emülatör sonuçlarıdır; her cihaz markasında veya her sosyal ağda paylaşım arayüzünün aynı görüneceği garantisi değildir. Video iptal yolu bu çalışmada ayrı otomatik testle doğrulanmadı.

## Geçici kategori açma

Üretim kodu Google.com için gerçek Android tarayıcı niyeti açar. Uygulama başarılı açılışı ve ayrılma/dönüş yaşam döngüsünü izler; yalnız seçilen koleksiyonu kaydeder. Ekran bunun gerçek reklam olmadığını açıklar. Web sayfasının yüklenmesini, izlenme süresini veya reklam gelirini doğrulamaz.

Başarısız açılış, ilk açılıştaki yanıltıcı dönüş, çift tıklama, tekrarlanan dönüş, durumun yeniden oluşturulması ve iç içe Android bağlamları ayrı testlerde doğrulandı. Ana uygulama testinde gerçek Chrome açıldı; sistem üzerinden Ascend'e dönüldü; yalnız Özgüven & Cesaret grubunun açıldığı, İnanç grubunun kilitli kaldığı doğrulandı.

İlk denemede 14/15 Android testi geçti. Son test, arka plandaki test uygulamasının Chrome açılırken kendi penceresini öne getirme adımında zaman aşımına uğradı; kayıtlarda MainActivity yeniden RESUMED durumuna gelmemişti. Test artık uygulamanın durmasını bekliyor ve sistemin başlatma komutuyla geri dönüyor. Kontrol kaldırılmadı veya atlanmadı. [İlk çalışma](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34169024738), son başarılı çalışmadan ayrıdır.

## Lint ve sınırlar

Uyarı dağılımı: OldTargetApi: 1, UnusedAttribute: 3, GradleDependency: 13, ModifierParameter: 1, PluralsCandidate: 19, ObsoleteSdkInt: 1, UnusedResources: 98, UseOfNonLambdaOffsetOverload: 1. Derlemeyi durduran hata yok. Kullanılmayan kaynaklar ve eski bağımlılık/bazı arayüz önerileri bu içerik güncellemesinde kapsamlı biçimde değiştirilmedi.

Bu APK geliştirme imzalı test sürümüdür. Actions çalışmalarında sabit test anahtarı henüz yapılandırılmadığı için önceki test APK'sının üzerine kurulum imza hatası verebilir. Eski test uygulamasını kaldırmak o kurulumdaki kaydedilenleri ve ayarları siler. Arşiv uyumluluğu yalnız veriyi koruyan, imzası uyumlu yerinde güncellemede geçerlidir. Mağaza yayını ve kalıcı imzalama bu teslimin parçası değildir.

Bildirimler Android arka plan çalışma kurallarına tabidir; seçilen dakikada kesin teslim sözü verilmez. Gerçek cihazlarda pil/üretici ayarları ayrıca denenmelidir. Uygulama şu an TR/EN destekler; sonraki diller için tek kaynak ve çeviri iş akışı hazırlanmıştır.

## Teslim dosyaları

- [Tek İngilizce kaynak](../content/source.en.json)
- [Türkçe çeviri](../content/translations/tr.json)
- [English translation guide](../content/README.md)
- [Ayrıntılı içerik ve araştırma raporu](09-ASCEND-5-ICERIK-RAPORU.md)
- [Gerçek uygulama ekranları](11-ASCEND-5-EKRANLAR.md)
