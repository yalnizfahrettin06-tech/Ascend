# Ascend 9.24 — Yaşayan İmparator ve Yeniden Başlamak

## Bu sürümün deneyimi

Görünüm → üstteki İmparator kartı, tek yaşayan koleksiyon prototipini açar. Üç görünüm vardır: uygulama içi sahne, sabit telefon duvar kâğıdı önizlemesi ve uyumlu widget. Sahne çok yavaş kamera kayması, düşük yoğunluklu sıcak ışık ve az sayıda atmosfer parçacığı kullanır. Metin hareket etmez. Hareket kapatılabilir; statik İmparator teması da seçilebilir. Widget ayrı kullanıcı işlemiyle oluşturulur.

Bu, yeni bir video indirme servisi değildir. Mevcut yerel yüksek çözünürlüklü çizim üzerinde hafif yerel çizim kullanılır. Ağ, ek dosya paketi veya indirme gerektirmez. Duvar kâğıdı sekmesi açıkça sabit önizlemedir; canlı Android duvar kâğıdı servisi, sistem arka planını uygulama veya otomatik telefon değişikliği içermez. Sonraki gerçek cihaz değerlendirmesinden önce böyle bir vaat verilmez.

Hareket yalnız aktif, görünür sahnede çalışır. Galeri ve widget sabittir. Ekran dışına kayınca, uygulama arka plana geçince, pil tasarrufunda ve sistem animasyonları kapalıyken durur. Ana ekran üzerinde Pro, paylaşım veya bildirim ayarları açılınca da hareket durdurulur. Sistem animasyon ayarı değişikliği izlenir. Görsel çözücü mevcut sınırlı önbelleği kullanır.

## Yeni Pro serisi

Keşfet → Kısa seriler → Yeniden Başlamak. Yedi günlük yeni editoryal içerik:

1. Nerede kaldığını gör — önceki emeği fark etme.
2. Kendi nedenini ayır — kişisel isteği beklentiden ayırma.
3. Eşiği küçült — iki dakikalık başlangıç.
4. Bir yeri hazırla — tek bir pratik engeli azaltma.
5. Aksayan günü taşı — telafi borcu yaratmadan geri dönme.
6. Küçük kanıtı fark et — somut öğrenmeyi görme.
7. Bir dönüş yolu bırak — esnek bir sonraki adım.

Her gün yeni bir özgün okuma ve isteğe bağlı eylem içerir. Eşlik eden yedi söz mevcut katalogdan, günün anlamına göre sabit kimlikle seçilir; bu sözler yeni yazılmış tarihî alıntılar gibi sunulmaz. Yeni özel değer, editoryal akış ve özgün anlatım/eylemdir. Türkçe, İngilizce, Portekizce, Almanca, Fransızca, İtalyanca ve Rusça içerikler çevrimdışıdır.

İlk gün herkese açık önizlemedir. Pro olmadan başlama/tamamlama depolama katmanında da engellenir. Pro kapatılınca sonraki bölümler kapanır, kayıtlı ilerleme silinmez. Günde bir adım ilerlenir; ara vermek seriyi sıfırlamaz. Eski üç seri ücretsiz kalır. Bildirim sıklığına ek bildirim eklenmez. Pro hâlâ açıkça ödeme almayan yerel demodur.

## Araştırma ve karar

- [Android Compose animasyon rehberi](https://developer.android.com/develop/ui/compose/animation/quick-guide): tekrar eden hareket, çizim katmanında değer okuma ve grafik dönüşümleri. Uygulamada animasyon değerleri metin düzenine değil çizime uygulanır.
- [Android WallpaperService.Engine](https://developer.android.com/reference/android/service/wallpaper/WallpaperService.Engine): gerçek canlı duvar kâğıdının ayrı yaşam döngüsü vardır. Bu sürüm böyle bir servis olarak sunulmaz; önce uygulama içi tek prototip doğrulanır.
- Önceki Ascend 9.22 Görsel Kimlik ve Pro Stratejisi analizinin 8, 11 ve 15. bölümleri temel alındı.

## Kontrol sınırı

Hedefli testler: ücretsiz önizleme, Pro erişimi, tema iptali/uygulama, seride günlük sınır ve Pro kapatılınca ilerleme, yedi dil içerik bütünlüğü, animasyonun piksel değiştirmesi ve durdurulması. Fiziksel cihazda uzun süreli pil, ısınma ve kare hızı ölçümü bu teslimin kapsamı değildir. Son CI sonuçları teslimle birlikte eklenir.
