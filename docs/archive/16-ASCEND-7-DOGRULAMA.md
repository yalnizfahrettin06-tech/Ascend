# Ascend 7.0 — uygulama ve doğrulama kaydı

## Teslim kapsamı

- 20 ekranlık başlangıç: isteğe bağlı isim, 13 tercih sorusu, sıklık, saatler, gerçek plan önizlemesi, erişim açıklaması ve bildirim izni. Geri gitme, atlama, hızlı başlangıç, taslak devamı ve sonradan düzenleme.
- Yanıtların ağırlıklandırdığı tek içerik motoru: önizleme, ana akış, bildirim ve widget aynı erişim ve dışlama kurallarını kullanır. Bu bir psikolojik değerlendirme veya yapay zekâ modeli değildir.
- Üç ana durak: Bugün, Keşfet, Senin. Kaydedilenler Senin içinde. Ana ekranın temel işlemleri dikey sayfa kaydırmasına ihtiyaç duymaz; çok büyük yazıda yalnız sözün alanı kayabilir.
- Siyah/beyaz arayüz ve yükselen çizgi kimliği. Keşfet görselsizdir; mevcut 70 kategori kapağı paylaşım kütüphanesine katılmıştır. Toplam 107 hazır arka plan, arama ve koleksiyon filtresi bulunur.
- Mevcut 700 İngilizce özgün söz ve bağlı Türkçe çeviriler, kayıtlar, kategori hakları ve paylaşım dışa aktarma araçları korunur.

## Yerel hafif kontroller

- İngilizce kaynak/çeviri derleyicisi: 700 kayıt, 70 kategori, güncel parmak izleri; geçti.
- Kategori ve sahne kaynak kontrolleri: eksik veya tekrarlanan dosya yok; geçti.
- Değişikliklerin boşluk/patch kontrolü: geçti.
- Yerel Android SDK/Gradle derlemesi veya emülatör çalıştırılmadı. Bilgisayar yerine GitHub Actions kullanılır.

## Bulut doğrulaması

- [Son bulut çalışması](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34272724247): başarılı.
- Derlenen ve test edilen kaynak: `72427f22de45ab7f743d643b81b06cff4438b2dc`.
- 64 JVM birim testi ve 37 Android testi geçti; başarısız veya atlanan test yok.
- Lint hatası yok; 144 uyarı bulunmaktadır. Bu sonuç uyarısız derleme olarak sunulmaz.
- Android 35 emülatöründe 28 ekran kaydı alındı. Karşılama, tek soru, plan önizlemesi ve ana arayüz görüntüleri görsel olarak incelendi.
- İlk çalışmanın büyük yazı etiketi ve paylaşım aramasının klavye eylemi sorunları düzeltildi. Onboarding yerleşim testi, ilerleme göstergesinin genişletilmiş erişilebilirlik alanı yerine gerçek 2 dp satırını ölçer; erişilebilirlik korunur.
- [Ascend 7.0 test APK](https://github.com/yalnizfahrettin06-tech/Ascend/releases/tag/v7.0.0-preview.1): sürüm `7.0.0`, kod `16`, geliştirme imzası doğrulandı.
- APK SHA-256: `df75b0f08edc8b3fd3ce03fa11aa2f328b2731c7352cfc7b32dcc16f33abf887`.

Kontroller; katalog ve erişim kuralları, kişiselleştirme/dışlama/tekrar davranışı, taslak ve profil saklama, onboarding gezinmesi, küçük ekran ve büyük yazı, plan düzenleme, Pro kapıları, kategori demosu, gerçek PNG ve 5/10/30/45 saniyelik video dışa aktarmayı kapsar.

## Test sürümünün sınırları

- Pro açıkça belirtilen demodur; ödeme veya abonelik yoktur. Kategori açma Google.com tarayıcı demosudur; gerçek ödüllü reklam değildir.
- Android güç tasarrufu hatırlatmaları geciktirebilir. Gerçek Samsung telefonunda teslimat ve görsel uygunluk kullanıcı geri bildirimiyle değerlendirilmelidir.
- Tercihler uygulama depolamasına yazılır; uygulama bunları bir sunucuya göndermez. Android yedekleme ayarları uygulama verilerini kapsayabilir.
- Actions geliştirme imzaları farklı olabilir. Eski test APK üzerine kurulum imza nedeniyle reddedilirse kaldırmak gerekir ve o kurulumun verileri silinir. Uygulama içi veri geçişinin korunması, farklı imzalar arasında Android kurulum kısıtını kaldırmaz.
- Bu doğrulama fiziksel cihaz kapsamının veya mağaza/ödeme/reklam üretim hazırlığının tamamlandığı anlamına gelmez.
