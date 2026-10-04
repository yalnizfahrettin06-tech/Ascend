# Ascend 8.0 — doğrulama ve teslim kaydı

## Yeni görünüm

Beyaz Mermer zemin, siyah mürekkep tipografi, ince konturlar ve soluk Roma büstü/sütun/kemer ayrıntıları uygulamaya entegre edildi. Ana ekran, 20 adımlı kişisel başlangıç, Keşfet, Senin, Kaydedilenler, kişisel plan, bildirim ayarları, Pro açıklaması ve paylaşım stüdyosu aynı tasarım dilini kullanır.

- Yeni kurulum aydınlık Mermer ile açılır. Mürekkep ve Bordo seçenekleri ile nötr gece görünümü korunur.
- Sarı/altın/mavi/turuncu arayüz paletleri kaldırılır; duvar kâğıdı renkleri uygulama arayüzüne aktarılmaz.
- Paylaşım eserleri kendi renklerini korur. 107 hazır arka plan, üç ücretsiz seçim, PNG ve 5/10/30/45 saniyelik MP4 araçları değişmez.
- 700 İngilizce kaynak kayıt, Türkçe çeviri bağlantıları, 70 kategori, kişiselleştirme/erişim kuralları ve bildirim planı korunur.
- Eski Kum/Lacivert/Yosun tercihleri bir kere Mermer'e taşınır. Açıkça seçilmiş Mürekkep/Bordo ve tema modu, kayıtlar ve kazanılmış erişim korunur.

[Tasarım araştırması ve ekran kararları](17-ASCEND-8-TASARIM-ARASTIRMASI.md) · [Üç yeni görselin üretim kaydı](../content/classical-art-provenance.json)

## Son kaynak ve bulut çalışması

- Kaynak: `dd18a5a678c19a4df560038ee612a657cf4b2ca2`.
- [GitHub Actions çalışması](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34373832269).
- Durum: **başarılı**. 68 JVM ve 42 Android testi geçti; başarısız veya atlanan test yok.
- Lint: hata yok, 152 uyarı. Uyarısız derleme iddiası değildir; mevcut kullanılmayan kaynaklar, bağımlılık/SDK ve biçimlendirme önerileri ayrıca kalır.
- APK: sürüm `8.0.0`, kod `17`, paket `com.yalnizfahrettin.azim.debug`, Android 8.0+ (min SDK 26), hedef SDK 35.
- APK boyutu: 30,580,678 bayt; SHA-256: `685b1d9eebbd778bfbb786f73d88e10094e2fc632c3429516f094f737aaedfa8`.
- İndirilen APK'nın geliştirme imzası doğrulandı. [Doğrudan APK](https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v8.0.0-preview.1/Ascend-8.0.0-test.apk) · [SHA-256 dosyası](https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v8.0.0-preview.1/Ascend-8.0.0-test.apk.sha256).
- Aynı kaynakta Android 35 emülatöründen 32 ekran kaydı alındı. [Ana ekran](screenshots-v8/home.png) · [Tek soru](screenshots-v8/onboarding.png) · [Keşfet](screenshots-v8/discover.png).
- İngilizce/Türkçe katalog, kategori ve sahne dosyası kontrolleri geçti; eksik kaynak veya güncelliğini yitirmiş çeviri yok.

## Kontrol kapsamı

Birim testleri katalog, içerik planı, erişim, bildirim zamanları ve tema kontrastını kapsar. Android testleri gerçek ekran gezinmesi, tek/çoklu seçim, isim/klavye, taslak ve yeniden oluşturma, kalıcı plan, tema geçişi, 320 dp ekran ve yüzde 200 yazı, kategori/Pro demoları ile gerçek PNG/MP4 çıktılarını kontrol eder.

Mermer üzerinde iki dekorun örtüşmesi ve gece görünümü için muhafazakâr birleşik zemin kontrastı hesaplanır. Kontrol konturları en az 3:1, normal metin örnekleri en az 4,5:1 hedefiyle sınanır. Bu, uygulamanın bütün erişilebilirlik koşullarını karşıladığına ilişkin sertifikasyon değildir.

İlk v8 çalışmasında değişen söz akışının eski sayfa anahtarlarına erişmesi üç Android akışını çökertti. Sayfa sayısı, anahtarları ve içerik aynı listeyle güncellenecek şekilde düzeltildi. Sonraki kontrollerde klavye kapanışını bekleyen test senkronizasyonu tamamlandı. Anlık ihtiyaç düğmesi ve paneli kendi küçük durum sahibi bileşenine ayrıldı; sayfa akışıyla yeniden oluşturulması birbirinden ayrıldı. Dokunma kayıtları panel düğmesine erişildiğini gösterdi; kesin derleyici/çalışma zamanı nedeni bu kayıtla kanıtlanmış sayılmaz. Test beklentileri veya kapsamı azaltılmadı.

## Görsel inceleme

Son tasarımdan karşılama, tek soru, saatler, izin, ana ekran, Keşfet, kişisel plan, Senin, Kaydedilenler, Pro ve paylaşım ekranları incelendi. Büyük yazı ve gece örneklerinde temel işlemlerin görünürlüğü ayrıca kontrol edildi. Ana sayfa dikey kaydırma gerektirmez; uzun söz büyük yazıda kendi alanında kaydırılır. Paylaşım ve ayrıntı ekranlarında içerik kayabilir, temel alt eylemler görünür kalır.

## Test sürümünün sınırları

- Pro ve kategori reklamı açıkça belirtilen demolardır; ödeme veya abonelik başlatılmaz. Tarayıcı dönüşü üretim ödüllü reklamının yerine geçmez.
- Android 35 emülatöründe doğrulama yapılır. Fiziksel Samsung görünümü, güç tasarrufunda bildirim teslimatı ve TalkBack ile elle kullanım bu kayıtta tamamlanmış sayılmaz.
- Tercihler uygulama depolamasında tutulur. Uygulama bunları bir sunucuya göndermez; Android yedekleme ayarları uygulama verilerini kapsayabilir.
- Test paketi geliştirme imzalıdır. Actions çalışmalarındaki imzalar farklıysa önceki test sürümü üzerine kurulum reddedilebilir. Eski kurulumun kaldırılması o kurulumun verilerini siler; uygulama içi geçiş testleri bu Android imza kısıtını kaldırmaz.
- Yerel Gradle/SDK derlemesi veya emülatör çalıştırılmadı. Derleme GitHub üzerinde; indirilmiş APK'nın paket bilgisi, bütünlüğü ve imzası hafif araçlarla kontrol edilir.

