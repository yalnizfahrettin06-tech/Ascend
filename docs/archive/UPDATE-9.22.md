# Ascend 9.22 — somut Pro teklifi ve kısa seriler

## Uygulanan ürün kararları

- Tek ortak Pro ekranı, giriş nedenine göre seçilen tema ve söz, konu adı/örneği veya hazırlanan gerçek widget önizlemesini gösterir. Paylaşım ve video teklifleri ilgili sözü korur.
- Demo açılınca tema uygulanır, seçilen paylaşım arka planı/video korunur, widget ekleme isteğine devam edilir. Teklif kapatılırsa bekleyen işlem iptal edilir. Konu açılması bildirim seçimini kendiliğinden değiştirmez.
- Ücretsiz bölüm açıkça anlatılır: altı konu, Beyaz/Siyah/Roma/Atlı Yolcu, bildirimler, kaydetme, geçmiş ve temel görsel paylaşımı. Pro: tüm konular, tüm görsel koleksiyonu, widget ve video. Mevcut ücretsiz haklar korunur.
- Teklif otomatik ana ekran reklamı değildir. Kullanıcı kilitli özelliği istediğinde gösterilir. Onboarding'deki tek, kapatılabilir demo teklifi korunur.
- Keşfet'teki boş Yakında kartları kaldırıldı. Eski tarayıcıyla kategori açma akışı uygulamadan ayrıldı; eski kayıtları okuyabilen veri kodu korunuyor.
- Video ikincil format seçeneği olarak kalır. Yeni navigasyon, puan sistemi, hesap veya karmaşık editör eklenmedi.

## Üç seri, 21 bilinçli adım

Seriler artık kategori listesinin ilk yedi elemanını almaz. Sabit söz kimlikleriyle editoryal sıra kullanır; katalog sırası değişse bile seri aynı kalır. Her günün sorusu o sözle ilişkilidir. Sorular ve seri açıklamaları Türkçe, İngilizce, Portekizce, Almanca, Fransızca, İtalyanca ve Rusça hazırdır.

- **Kendine nezaket:** duyguyu kabul → iç sesi yumuşatma → görünmeyen yük → hata karşısında yaklaşım → geçmişteki kendini anlama → dinlenme → başarıdan bağımsız değer.
- **Küçük adımlar:** kişisel neden → hedefe küçük bakım → ilk düşünce → ilk denemeye zaman → merak → denemeden öğrenme → küçük ilerlemeyi fark etme.
- **Odak:** niyet → tek işe yer açma → yeterli parçayı belirleme → kesintiden dönüş → bitiş sınırı → dinlenme → çalışma zamanını koruma.

Mevcut katalogdan seçilen sözler korunur; 21 yeni düşünme sorusu eklenmiştir. Bunlar 21 yeni tarihî alıntı değildir. Atıf türleri değiştirilmemiştir. Üç mevcut seri ücretsizdir; eski kullanıcıların serileri sonradan kilitlenmez. Devam eden seri doğrudan açılır; listeye geri dönülebilir. İlerleme kayıtları silinmez.

## Ölçümün sınırı

Teklif gösterme/kapatma, demo açma, devam etme, kurulum ve seri adımları için cihazda günlük toplu sayaçlar vardır. En fazla son 30 gün tutulur. Söz metni, konu kimliği, kullanıcı veya cihaz tanımlayıcısı toplanmaz; dışarıya veri gönderilmez. Bunlar gerçek ödeme, satış, bildirim okunması veya kullanıcı memnuniyeti ölçümü değildir.

## Yayına geçmeden gereken kararlar

Gerçek ödeme entegrasyonu bu sürüme eklenmedi. Önce düzenli içerik üretim kapasitesine göre abonelik/tek seferlik erişim seçilmeli; ülke fiyatları belirlenmeli. Sonrasında Play Billing, satın alma doğrulama, geri yükleme ve iptal/süre bitimi birlikte uygulanmalı. Üç günlük demo sayaçsızdır; ekranda açıkça belirtilir. Yeni içerik takvimi veya satış artışı vaat edilmez.

5–8 gerçek kullanıcıyla kısa görev testi önerilir: ücretsiz hakları açıklama, İmparator temasını kullanma, konu örneğini okuyup bildirim seçme, widget hazırlama, teklifi kapatıp işe dönme. Takılma noktası ve yardım gereksinimi kaydedilmeli. Bu çalışmada gerçek kullanıcı görüşmesi veya fiyat deneyi yapılmadı. Çeviriler teknik olarak kontrol edilir; ana dil editör onayı ayrıca gerekir.

## Doğrulama

İçerik/çeviri denetimleri, birim testleri, lint ve sınırlı Android geçiş testleri derleme hattında çalışır. Özellikle tema uygulama, konuya dönüşte bildirim tercihinin değişmemesi, videoya geçiş ve tekliften vazgeçme kontrol edilir. APK üretiminden sonra uzun cihaz taraması yapılmaz. Nihai çalıştırma sonucu teslim mesajında bildirilir.

### Tamamlanan derleme

Kaynak: `2fc3773`. GitHub Actions: https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/35462267869

103 birim testi, 17 hedefli Android testi ve lint başarılı. APK: https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v9.22.0-ui/Ascend-9.22.0.apk

Tek bir Pro ekranı görüntüsü kısa görsel kontrol için incelendi. Fiziksel cihaz, uzun süreli bildirim teslimatı, gerçek ödeme ve ana dil editör incelemesi yapılmadı.
