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

Bu kayıt yazılırken v7 bulut derlemesi ve Android testleri henüz tamamlanmamıştır. Sonuç, kaynak kimliği ve APK bağlantısı burada güncellenecektir.

Kontroller; katalog ve erişim kuralları, kişiselleştirme/dışlama/tekrar davranışı, taslak ve profil saklama, onboarding gezinmesi, küçük ekran ve büyük yazı, plan düzenleme, Pro kapıları, kategori demosu, gerçek PNG ve 5/10/30/45 saniyelik video dışa aktarmayı kapsar.

## Test sürümünün sınırları

- Pro açıkça belirtilen demodur; ödeme veya abonelik yoktur. Kategori açma Google.com tarayıcı demosudur; gerçek ödüllü reklam değildir.
- Android güç tasarrufu hatırlatmaları geciktirebilir. Gerçek Samsung telefonunda teslimat ve görsel uygunluk kullanıcı geri bildirimiyle değerlendirilmelidir.
- Tercihler uygulama depolamasına yazılır; uygulama bunları bir sunucuya göndermez. Android yedekleme ayarları uygulama verilerini kapsayabilir.
- Actions geliştirme imzaları farklı olabilir. Eski test APK üzerine kurulum imza nedeniyle reddedilirse kaldırmak gerekir ve o kurulumun verileri silinir. Uygulama içi veri geçişinin korunması, farklı imzalar arasında Android kurulum kısıtını kaldırmaz.
- Bu doğrulama fiziksel cihaz kapsamının veya mağaza/ödeme/reklam üretim hazırlığının tamamlandığı anlamına gelmez.
