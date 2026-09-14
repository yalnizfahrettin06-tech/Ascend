# Ascend 9.18 — söz kontrolü, doğal dil ve sade Pro

Bu güncelleme kabul edilen 1, 4, 5 ve 6 numaralı başlıklara odaklanır. Kategori/içerik mimarisini yeniden kurmaz; yeni dil eklemez.

## Söz kontrolü
- Ana ekranın üç nokta menüsü: “Bu sözü bir daha gösterme”.
- Mevcut kalıcı gizleme altyapısı korunur. Söz, rastgele akıştan ve gelecek bildirimlerden çıkar; widget da aynı dışlama listesini kullanır.
- Sonuç mesajı neyin değiştiğini açıklar ve Geri al sunar. Bildirim ayarlarından gizlenen sözleri topluca geri getirme korunur.
- Geçmiş kayıtlar silinmez; kullanıcının özellikle açtığı eski kayıtları okumak engellenmez.
- Yedi dilde bildirim seçiminin dışlama listesine uyması, boş havuzdan gizli söz döndürmemesi ve geri alma için testler bulunur.

## Çeviri kalite turu
- Fransızcadaki fiyat teklifi (devis), Portekizcedeki finansal kotasyon (cotação) ve diğer dillerdeki tırnak işareti anlamındaki yanlış “quote” çevirileri ilgili arayüz metinlerinde düzeltildi.
- Onboarding, kaydetme/paylaşma, geçmiş, widget ve gizlenen sözleri geri getirme ifadeleri gözden geçirildi.
- Almanca ilk adımların hitabı daha tutarlı hale getirildi.
- İlk kategori sözlerinden v5_ozsefkat_02, beş yeni dilde daha doğal ifade edildi; İngilizce kaynak ve Türkçe metin korunur.
- Yeni Pro ve gizleme metinleri beş dilde çevrildi. Çeviri tabloları çevrimdışı üretildi, kaynak eşleşmeleri doğrulandı.
- Bu hedefli bir dil turudur; bütün kataloğun ana dil konuşuru tarafından onaylandığı anlamına gelmez.

## Sadelik
- Ayarlardaki artık erişilmeyen ikinci bildirim kurulumu kaldırıldı. Görünüm ve bildirim bağlantıları mevcut tek akışa götürür.
- Ayarlar içinde anlamı kalmayan “03” bölüm numarası kaldırıldı.
- Video ve kısa seriler korunur; yeni editör seçeneği eklenmedi.

## Pro
- Hem Pro hem onboarding teklifinde gerçek Atlı Yolcu arka planıyla tema/widget görsel örneği gösterilir.
- Süre, yazı stilleri ve ayrıntılı editör araçları gibi artık sunulmayan vaatler kaldırıldı.
- Kısa anlatım: tema seç, widget ekle, söz paylaş. Kategorilere erişim ve bildirim konularını kullanıcının seçmesi açık kalır.
- Pro ekranında ücretsiz devam et görünür; demo etkinleştirmeden kapatır.
- Ödeme/abonelik başlamadığı açıkça yazılıdır. Onboarding üç günlük teklifinin süre sayacının bu demo sürümünde çalışmadığı açıklaması korunur.

## Doğrulama
Kaynak/çeviri, birim ve sınırlı Android emülatör testleri CI üzerinden çalıştırılır. Fiziksel cihazların tamamı ve uzun süreli bildirim teslimatı bu kısa testin kapsamı dışındadır.
