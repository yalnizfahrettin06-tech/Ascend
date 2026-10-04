# Ascend 9.5 — Birbirini tekrar etmeyen onboarding

## Sorunun kökü

Kullanıcının 12:07 tarihli beş ekran görüntüsü esas alındı. Önceki sürümde iki bağımsız problem aynı anda vardı:

- İçerik ekranın üstüne, CTA ise ayrı bir alt çubuğa yerleşiyordu. Metin kısa olduğunda kalan yükseklik tamamen ikisinin arasına yığılıyordu. Daha büyük fotoğraf eklemek bu yapısal sorunu çözmüyordu.
- Aynı Roma heykeli ve aynı bildirim örneği farklı görevlerde tekrar ediyordu. Sayı, saat listesi, açıklama ve bildirim metni üç sayfada yeniden okunuyordu. Sayfalar değişiyor fakat deneyim değişmiyordu.

Yeni hedef daha fazla metin, kart veya dekorasyon eklemek değil; her sayfanın tek bir işi, kendine ait bir görsel anlatımı ve yakındaki tek ilerleme eylemi olması.

## İnternet araştırması ve uygulanan kararlar

Nielsen Norman Group'un 70 katılımcılı, dört iOS uygulamasını kapsayan araştırması, başlangıçtaki açıklama slaytlarının görev başarısı veya süresi üzerinde anlamlı bir iyileşme sağlamadığını; görevi daha zor hissettirebildiğini bildiriyor. Çalışma bütün uygulamalara yönelik başarı garantisi değildir. Ascend için çıkarımımız: “kaydırmayı anlatan” ikinci sayfa yerine gerçekten kaydırılabilen küçük bir deneme sunmak; tanıtımı iki sayfayla sınırlayıp diğer üç sayfayı gerçek ayarlara ayırmak. [NN/G — Mobile Tutorials](https://www.nngroup.com/articles/mobile-tutorials/).

Android'in uyarlanabilir arayüz rehberi, arayüzü kullanılabilir pencere alanına göre ele almayı öneriyor. Bu nedenle belirli bir telefonun ekran yüksekliğine sabitlenen içerik boşlukları kullanılmadı. [Android — Adaptive layouts](https://developer.android.com/develop/ui/compose/layouts/adaptive/get-started-with-adaptive-apps).

## Beş sayfanın ayrı görevi

| Adım | Kullanıcı ne görür/yapar? | Görsel dil | Özellikle çıkarılan tekrar |
|---|---|---|---|
| 1. Karşılama | Kısa değer önerisi ve hesap gerektirmeyen başlangıç | Yeni, tek kullanımlık gümüş Roma kemeri fotoğrafı; yavaş giriş yakınlaşması | Eski heykel kartı, uzun tanıtım, boş CTA bölgesi |
| 2. Söz denemesi | Kartı kaydırır veya “Başka bir söz”e dokunur; kalbi deneyebilir | Katmanlı gümüş kâğıt kartı, üç kısa söz, geçişli metin ve kalp tepkisi | Aynı heykel, uzun kullanım kılavuzu, özellik listesi |
| 3. Sıklık | Günde 1–7 bildirim seçer | Ortasında sayı olan gümüş kadran, sayıya göre değişen yedi nokta | Bildirim örneği, saatler ve tekrar eden özet |
| 4. Saatler | Başlangıç/bitiş aralığını seçer | Gün yayı ve etkin aralık çizgisi; gerçek dağılım noktaları | Aynı bildirim kartı, iki büyük tam genişlikli saat kartı |
| 5. Bildirim | Örneği genişletir, cihaz görünüm yardımına ulaşır, izin verir | Tek telefon çerçevesi içinde genişletilebilir metin bildirimi | Heykel küçük resmi, ikinci saat listesi, ikinci plan özeti |

## Yerleşim sistemi

- Üst marka ve beş adımlı ilerleme göstergesi korunur; adımlar arasındaki hareket yönü geri/ileri yönüyle eşleşir.
- Gövde ve CTA artık tek ölçülen, tek kaydırılan içerik grubudur. Gövdenin sonu ile CTA arasında **24 dp** vardır. Araya esneyen boşluk konmaz.
- Kullanılabilir yükseklikten türetilen görsel alan 210–310 dp aralığındadır. Uzun cihazlarda anlamlı görsel büyür; metin blokları birbirinden kopmaz.
- Grup kullanılabilir alanın içinde ortalanır. Sığmadığında kendi yüksekliğinde büyür ve kaydırılır; son eylem kaydırarak erişilebilir kalır.
- İçerik genişliği en çok 480 dp; yan boşluklar 24 dp. Tablet üzerinde metin satırları uçtan uca yayılmaz.
- Büyük yazıda saat seçimleri iki sütundan tek sütuna geçer. Metin taşmasını gizleyen zorunlu tek satır veya küçültme uygulanmaz.
- Söz kartının yazı alanı yalnızca asgari yüksekliğe sahiptir; büyük yazıda genişleyebilir.

## Tipografi, renk ve etkileşim

Başlıklar tutarlı Lora 29/36 sp, açıklamalar 14/21 sp; ikincil bilgi 12–13 sp. Kadrandaki büyük sayı yalnızca o sayfanın kararını temsil eder. Siyah metin, gümüş yüzeyler ve ince nötr kenarlıklar kullanılır. Fotoğraf da ekranda gri tonlamayla çizilir. Bordo, sarı, altın, mavi veya turuncu vurgu eklenmez.

Söz denemesi gerçek içerik koleksiyonunu veya favorileri değiştirmez; “Örnek beğenildi” geri bildirimi bunu açıklar. Kaydırmaya alternatif metinli düğme bulunur. Sayı kontrolleri erişilebilir etiketli ve sınırlarında devre dışıdır. Dekoratif çizimler ekran okuyucuya gereksiz düğümler eklemez. Son bildirimde iki satırlı dar görünüm dokunarak tam metne açılır; açıklama ve aç/kapat oku birlikte değişir.

Hareketler: sayfa geçişi 240–360 ms, kart değişimi 150–300 ms, kadran güncellemesi 350 ms, zaman aralığı 400 ms, bildirim açılması 280 ms; karşılama fotoğrafında tek seferlik 1100 ms yakınlaşma. Sonsuz dönen veya otomatik olarak sayfa atlayan animasyon yoktur. Compose animasyonları sistem animasyon ölçeğini izler.

## Bildirim mantığı

Sayı ve zaman çizelgesi mevcut `BildirimZamanlari` hesabına bağlıdır. Varsayılan 3 bildirim / 09:00–21:00 seçimi 11:00, 15:00 ve 19:00 örneğini verir. Sayı 4 olduğunda 10:30, 13:30, 16:30 ve 19:30 görünür. Bunlar yaklaşık plan saatleridir.

Bildirim izni yalnızca son sayfadaki eylemle istenir. Kullanıcı izin vermediğinde kurulum otomatik tamamlanmaz; izin açıkken aynı eylem uygulamayı başlatır. “Bildirimsiz devam” eklenmedi. Android'in kendi izin kararı değiştirilmez veya gizlenmez. Ayrıntılı görünüm yardımından mevcut cihaz bildirim ayarlarına gidilebilir; uygulama sistem görünümünü değiştirmiş gibi davranmaz.

## Yeni görselin kaydı

Araç: yerleşik imagegen. Dosya: `app/src/main/res/drawable-nodpi/art_onboarding_gateway.png`. Yalnızca karşılama adımında kullanılır. Diğer dört sayfanın görsel anlatımı Compose bileşenleriyle üretilir.

Üretim istemi: “Create one premium photorealistic architectural illustration asset for the welcome page of Ascend, a quiet affirmation Android app. Landscape 1536x1024. Full bleed artwork, not a screenshot, no phone, no UI, NO TEXT, NO LETTERS. A beautifully proportioned ancient Roman white marble archway seen almost frontally, slightly to the right, with three shallow silver stone steps leading into a softly illuminated open doorway. One delicate olive branch in the left foreground, subtle pale soft shadows and tactile honed marble. Ethereal but credible architectural photography, museum quality. Entire palette strictly neutral white, silver gray and graphite shadows, NO warm beige, NO blue tint, NO yellow, NO gold, NO burgundy. Diffuse daylight, nuanced silver material contrast, soft vignette, enough depth to be interesting at small mobile sizes. The arch fills most of the composition vertically, avoid large empty sky or blank wall. No statues, no human figures, no repeating arches, no dramatic sun rays, no diagonal stripe patterns. Refined, calm, expensive, understated editorial composition.”

## Kontrol ve teslim sınırı

İçerik ve mevcut görsel kaynak denetimleri çalıştırıldı. Onboarding testleri yeni akışa uyarlandı: izin sırası, ret durumunda bitirmeme, sıklık/saat ilişkisinin son önizlemeye taşınması, örnek beğeni/kaydırma, geri dönüşte ayarların korunması, büyük yazıda ilerleme düğmesine erişim ve her adımda 24 dp içerik–CTA aralığı. GitHub derlemesi JVM testlerini/lint'i çalıştırır ve Android test kaynaklarını derler; cihaz testlerinin derlenmesi çalıştırıldıkları anlamına gelmez.

Kullanıcının isteği gereği APK üretildikten sonra ek cihaz veya görsel kontrol başlatılmayacak. Bu değişiklik kullanıcının telefonunda henüz görülmüş veya estetik açıdan onaylanmış olarak sunulmaz. Ana ekran, kategoriler, Pro/demo kapsamı ve ödeme mantığı bu onboarding çalışmasında değiştirilmez.
