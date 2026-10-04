# Ascend 9.28 — İlk dört günlük planın uygulanması

## Kapsam

- Onboarding ve Pro teklifi, kullanıcının ana ekran temasından bağımsız siyah marka kabuğuna alındı. Kapatınca önceki sistem çubuğu görünümü geri yüklenir.
- Beş adımın görsel görevleri ayrıldı: arena/dil; taht/gerçek söz; lejyon/ritim; etkileşimli bildirim maketi; gerçek tema galerisi.
- Dil listesi daha kompakt; sabit alt eylem ve yedi dil korunuyor. Büyük yazıda dekor küçülür, içerik kayar; ana düğme içerikle birlikte kaybolmaz.
- Tanıtımdaki sözler gerçek, ücretsiz katalog kayıtlarıdır. Seçilenler taslakta tutulur; kurulum tamamlanırken mevcut favorilere tek işlemde eklenir. Kopya kayıt ve geçersiz kimlik engellenir. İptal edilmiş kurulum favorilere yazılmaz.
- Ritimde gereksiz ikinci nokta göstergesi kaldırıldı. Tek çizgi ve yaklaşık saatler var. Büyük yazıda başlangıç/bitiş kontrolleri alt alta gelir.
- Bildirim maketi yalnız izin adımında; açılır görünüm yardımı belirgin. Bildirimsiz devam eklenmedi, mevcut sistem izin/ayar dönüşü korunuyor.
- Fotoğraflı tema sırası Atlı Yolcu, Roma, İmparator, Düello. Siyah/Beyaz kompakt seçimler. Yeni taslakta Atlı Yolcu başlangıç; eski seçimi ezmez.
- Ücretsiz temayla kurulum doğrudan biter. Pro tema seçilirse o görünümün teklifi açılır; vazgeçince seçim ekranına dönülür, kullanıcı adına başka tema seçilmez.
- Pro'da gerçek seçimin önizlemesi, somut kullanım açıklaması ve bağlama göre üç fayda gösterilir. Diğer faydalar açılır alana taşındı.
- Kurulum ve uygulama içi teklif aynı fayda bileşenlerini kullanır. Alt eylemler sabit; demo açıklaması görünür. Gösterim işareti ekran yeniden oluşturulmasında korunur.
- Demo sonrası wallpaper ve paylaşım işlemleri kullanıcının son Uygula/Paylaş eylemini beklemeye devam eder. Ödeme veya gerçek abonelik eklenmedi.

## Planla ilgili kararlar

Onboarding ilk sözünde yeni bir cümle üretmek yerine mevcut ücretsiz ve yedi dile çevrilmiş katalog kullanıldı. Ritimde bildirim maketi tekrarlanmadı. Sayı alanına farklı bir lejyon sahnesi eklendi; büyük yazıda dekoratif boşluk azaltılır. Yeni bitmap üretimi yapılmadı; mevcut kaliteli sanatlar kullanıldı.

Ana ekran, Keşfet yapısı, dört alt bölüm ve ücretsiz erişim hakları korunuyor. Bu çalışma 10 günlük yol haritasının ilk dört günlük kapsamıdır; sonraki altı günün işleri tamamlandı sayılmaz.

## Doğrulama

- Sürüm: 9.28.0 / kod 59.
- APK kaynak commit: c7002e9ab3ce878ad3eba1731b5437f06fbe1b72.
- GitHub Actions: https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/35521474404 — build, ui-tests, share-regression ve publish başarılı.
- Android testleri: 35 ana UI + 26 paylaşım/regresyon + 1 görsel tanılama + 1 sistem duvar kâğıdı testi, toplam 63; sıfır hata.
- Derleme işindeki birim testleri ve lint başarılı. Dil paketleri kontrolü başarılı.
- Dar 320×640 alan ve iki kat yazı boyutunda Almanca onboarding ana eylemleri doğrulandı. Taslak favorilerin korunması, tamamlanırken tek kez yazılması ve ücretsiz/Pro çıkış davranışları test edildi.
- Son APK ekranlarından ritim/lejyon görünümü açılıp kısa görsel kontrol yapıldı. Uzun fiziksel cihaz taraması yapılmadı.
- APK: https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v9.28.0-ui/Ascend-9.28.0.apk
- Boyut: 54.456.535 bayt. ZIP/CRC, manifest ve DEX kontrolü başarılı.
- SHA256: B2E62C321539AB1E0ABC51DE6EF382AB341AB307AC3D43D03983E98D463F6C3B.
- İlk test geçişindeki eski ücretsiz çıkış metni beklentisi sabit eylem kimliğine güncellendi; ücretsiz çıkış davranışı testi korunup son derlemede geçti.

Gerçek cihaz testi ve ana dili konuşan editör değerlendirmesi yapılmadı. Yeni satın alma dönüşümü veya gelir iddiası yok.

