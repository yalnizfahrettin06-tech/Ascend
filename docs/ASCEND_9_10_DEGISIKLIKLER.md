# Ascend 9.10.0

## Senin: bildirim geçmişi
Son 30 takvim gününde gönderilmiş sözler tarihe göre listelenir. Her sözde Kaydet ve Paylaş vardır. Aynı gün aynı söz bir kart olarak gösterilir. Eski sürümden mevcut yedi günlük kayıtlar korunur; geçmişte silinmiş kayıtlar geri getirilemez. Yeni bildirimler geldikçe 30 günlük aralık oluşur. Kayıt yalnız Android bildirim gönderimini kabul ettikten sonra yazılır; kullanıcının bildirimi gördüğü iddia edilmez. Bildirim gelmemişse açıklayıcı boş durum gösterilir.

## Senin: kısa seriler
Üç ücretsiz, isteğe bağlı seri: Kendine daha nazik; Küçük adımlar; Dikkatine alan aç. Her biri mevcut ücretsiz içerikten seçilmiş yedi ayrı söz ve Türkçe/İngilizce yedi kısa düşünme sorusudur. Kişisel not veya yanıt girişi yoktur. Seriler ek bildirim göndermez, mevcut bildirim planını değiştirmez.

Başlama ve ilerleme DataStore ile cihazda tutulur. Her takvim gününde en fazla bir adım tamamlanır; sonraki adım ertesi gün açılır. Ara vermek ilerlemeyi silmez. Önceki günler yeniden okunabilir; yedi adım bitince tüm seri okunabilir. Kaydet ve Paylaş burada da kullanılabilir. Sistem saati geriye alınırsa aynı gün tekrar ilerleme verilmez. Üretim sunucusu bulunmadığı için ileri tarih manipülasyonuna karşı sunucu garantisi sunulmaz.

## Tekrar seçimi
Uygulama çevrimdışı olduğundan değişiklik mevcut Kotlin veri/seçim katmanındadır. Yeni sunucu veya hesap eklenmedi. Yakın geçmiş 30 yerine 700 farklı söz saklar; bildirilen sözler de ana ekranın yakın geçmişine eklenir. Bildirim havuzu tükendiğinde son sekiz içerik mümkünse bekletilir; küçük havuzlarda en eski uygun içerik seçilir. Son bildirim art arda tekrar edilmez; tek uygun söz kalması istisnadır. Erişim, içerik tercihleri ve gizlenen sözler korunur.

## Kontroller
Seri içeriği, ilerleme kaydı, aynı gün/geri tarih koruması, ara verme ve bitirme; tam/küçük havuzlarda tekrar seçimleri için birim testleri eklendi. Kaynak, birim testleri, lint ve APK derlemesi CI üzerinde çalışır. Uzun APK sonrası emülatör incelemesi kapsam dışıdır.
