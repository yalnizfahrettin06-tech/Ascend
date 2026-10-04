# Ascend — Ayrıntılı UI/UX incelemesi

Tarih: 7 Eylül 2026 · İncelenen temel: kullanıcının paylaştığı `azim-kaynak.zip` · Hedef: günlük olumlamalara odaklanan Ascend 3.0.

## 1. Kapsam ve kanıtın sınırı

Bu çalışma kaynak kodu incelemesi, ekranların Compose yerleşim analizi ve bu incelemeye dayanarak yapılan uygulamadan oluşur. Gerçek kullanıcı görüşmesi, mağaza değerlendirmesi, kullanım analitiği veya eski APK üzerinde cihaz testi yapılmış gibi değerlendirilmemelidir. “Kullanıcıyı zorlayabilir” türü ifadeler tasarım hipotezidir; ölçülmüş terk oranı değildir. Yeni sürümün derleme/test kanıtları ayrı doğrulama raporundadır.

GitHub deposu başlangıçta boştu. Kaynak ZIP temel alınarak ilk commit oluşturuldu. ZIP içindeki rapor ve yorumlar kullanıcı talimatı olarak alınmadı; eski sürüm iddiaları bağımsız kanıt sayılmadı. Kullanıcının asıl isteği Ascend’in analiz edilmesi, özellikle onboarding ve UI/UX’in geliştirilmesi ve GitHub üzerinden APK üretimiydi.

Kaynak envanteri: 36 ana Kotlin dosyası, 11 konu grubu, 67 alt konu ve 53 alıntı. 67 konunun varlığı her konuda yeterli içerik bulunduğu anlamına gelmez. İlk sürümde 14 ücretsiz alt konu listeleniyordu. Yeni ücretsiz olumlama grubu üç konu ve 18 içerik getiriyor; mevcut üç başlangıç konusuna eklenen altı olumlamayla toplam 24 özgün içerik eklendi. Toplam içerik 77, alt konu 70, grup 12 oldu.

## 2. Ürünün temel sorunu

Kullanıcının ürün tanımı **günlük olumlama**, kaynak kodunun baskın davranışı ise **motivatör alıntı akışı**. Bu ikisi birlikte sunulabilir; ancak ilk deneyimde hangisinin ana vaat olduğu açık olmalıdır. Kaynakta azim, disiplin ve pes etmeme varsayılanları baskınken öz şefkat veya sakinleşme başlangıçta görünmüyordu.

Önerilen ürün cümlesi: **“Gün içinde kendine ayırdığın küçük bir an; sana yakın gelen olumlamalar ve isteğe bağlı nazik hatırlatmalar.”** Bu sürümde yeni başlangıç varsayılanları öz şefkat ve iç huzur içerikleridir. Eski alıntılar koleksiyonlarda korunur. Olumlamalar tarihsel kişilere mal edilmez; özgün Ascend içeriği olarak görünür. Ürün metni kesin iyileşme, tedavi veya sonuç garantisi vermez.

## 3. Öncelikli bulgular

Öncelik tanımı: P0 ilk deneyimi veya temel kullanıcı kontrolünü etkileyen hata; P1 günlük kullanımda önemli sürtünme; P2 kalite, kapsam veya ileriki sürüm işi. Bunlar güvenlik açığı puanları değildir.

| No | Öncelik | Kaynaktaki kanıt | Kullanıcı etkisi | Bu sürümde durum |
|---|---|---|---|---|
| UX-01 | P0 | `Uygulama`: onboarding akışı başlangıçta `true` ile toplanıyor | Veri okunmadan ana ekran görünebilir; ilk açılış tutarsızlaşır | İlk değer bilinmiyor kabul edilip yükleme kapısı eklendi |
| UX-02 | P0 | `Onboarding`: son CTA her zaman `izinIste()` çağırıyor | Bildirimsiz kullanım görünür seçenek değil | Ayrı bildirimsiz devam ve açık izin niyeti eklendi |
| UX-03 | P0 | Kurulum kaydı dört ayrı DataStore yazımıyla tamamlanıyor | Arada süreç kesilirse yarım kurulum kaydı oluşabilir | Seçimler, plan, izin tercihi ve tamamlanma tek işlemde kaydediliyor |
| UX-04 | P1 | Onboarding’de geri düğmesi/BackHandler yok | Kullanıcı önceki kararını düzeltmekte zorlanır | Görünür geri, sistem geri ve kayıt sırasında tekrar giriş engeli |
| UX-05 | P1 | Karşılama yalnız logo/slogan; 14 ücretsiz alt konu arka arkaya | İlk değer geç görünür; ilk seçimde gereğinden çok tarama | İlk ekranda gerçek olumlama; altı başlangıç niyeti |
| UX-06 | P1 | `KapanisSahnesi`: kelime başına 115 ms ve sonunda 1100 ms bekleme | Kullanıcı süresi belirsiz ve atlanamayan bir sahne bekler | Kapanış sahnesi ana onboarding akışından çıkarıldı |
| UX-07 | P0 | `SozWorker` bildirimi gönderip `gosterildi` çağırıyor | Okunmamış bildirimler okuma sayısını artırır | Teslimat geçmişi ve uygulamada okuma ayrıldı |
| UX-08 | P1 | `AnaEkran`: ilk sayfa etkisi sadece sayfa numarasına bağlı | İçerik sonradan gelince ilk okuma kaydı kaçabilir | Yerleşen gerçek içeriğin kimliği izleniyor |
| UX-09 | P1 | `Uygulama`: akış yalnız boşken kuruluyor | Konu değişikliği ekrana yansımayabilir | Seçim değişiminde havuz yenileniyor |
| UX-10 | P1 | Sayfa göstergesi `sayfa % 5` | 53 içerik beş içerikmiş gibi döngüsel konum hissi verir | Gerçek sıra/toplam ve görünür Önceki/Sonraki |
| UX-11 | P1 | `metinSonuk`, 11sp etiketlerde; test yalnız 2.9 kontrast arıyor | Küçük yardımcı metinler gereğinden sönük | Küçük metin için 4.5 hedefi; zemin/yüzey ve CTA testleri |
| UX-12 | P1 | Özel anahtar 44×26, sayı seçimi 38×38, favori 32×32 | Dokunma ve yardımcı teknoloji kullanımı zorlaşır | Material Switch, 48dp sayı/favori kontrolü |
| UX-13 | P1 | `azimTikla` seçim kontrollerinde de kullanılıyor | Ekran okuyucu seçili/açık durumunu öğrenemeyebilir | Onboarding/konu checkbox; ayarlarda radio; nav Material bileşeni |
| UX-14 | P1 | Ayar seçenekleri kaydırılamayan tek Row | Dar ekran ve büyük yazıda taşma riski | Sarılan seçenek satırları ve esnek yükseklik |
| UX-15 | P1 | Konu/favori/istatistik ekranlarında üst sistem boşluğu yok | Edge-to-edge yerleşimde başlık örtüşebilir | Güvenli alan payları eklendi |
| UX-16 | P1 | `VarsayilanKapi` daima false; diyalog reklam izlemeyi öneriyor | Çalışmayan bir teklif ve sessiz başarısızlık | Hazır değil durumunda dürüst açıklama; sahte ödül yok |
| UX-17 | P1 | `HavuzDengesi` seçilen havuz yerine tüm içerik sayısını kullanıyor | “Tekrarsız gün” vaadi yanlış olabilir | Kanıtlanamayan hesap kaldırıldı; yaklaşık teslimat notu |
| UX-18 | P1 | Plan azaltıldığında eski yüksek numaralı işler iptal edilmiyor | Kullanıcının seçtiğinden fazla hatırlatma riski | Tüm eski dilimler iptal edilerek plan yenileniyor |
| UX-19 | P1 | Rastgele ±20 dakika dar zaman aralığının dışına çıkabiliyor | Sessiz saat tercihi ihlal edilebilir | Aralık içindeki orta noktalarla plan; teslimatta yeniden saat kontrolü |
| UX-20 | P1 | Dil ayarı içerik seçimini değiştiriyor, arayüz kaynak bağlamını değiştirmiyor | Türkçe/İngilizce karışabilir | Compose kaynak bağlamı seçilen dile bağlandı |
| UX-21 | P1 | Favori boş durumda yalnız açıklama; dolu öğe tekrar okunamıyor | Kaydedilen içerik bir çıkmaz olur | Okumaya dönüş CTA’sı ve kaydedileni ana akışta açma |
| UX-22 | P1 | `uygulama_adi`, paylaşım imzası ve çıktı adı eski marka | Ascend/Azim tutarsızlığı | Görünür marka ve paylaşım imzası Ascend |
| UX-23 | P2 | Sistem geri paylaşım/ayar görünümünü yönetmiyor | Geri hareketi beklenmedik biçimde uygulamadan çıkarabilir | Kök ekranda katman sıralı geri davranışı |
| UX-24 | P1 | Release yapılandırmasında açık imza bilgileri ve ZIP’te bulunmayan anahtar referansı | Public kaynak ve derleme düzeni için uygun değil | İlk public commit öncesi ortam değişkenlerine taşındı |

## 4. Ekran ekran değerlendirme

### Karşılama

Amaç markayı izletmekten önce ürünü deneyimletmek. Yeni düzen marka/ilerleme, kısa vaat, ilk olumlama kartı ve tek birincil eylemden oluşur. “Hesap gerekmiyor”, ücretsiz başlangıç ve isteğe bağlı bildirim açıklaması kullanıcının kararını destekler. Dekoratif animasyon yüzünden metin görünürlüğü geciktirilmez. İçerik uzun olduğunda ekran kaydırılır; alt CTA ayrılmış bölgede kalır.

### İhtiyaç seçimi

Kategori taksonomisinin tamamı onboarding işi değildir. “Kendime nazik davranmak”, “Biraz yavaşlamak”, “Kendime güvenmek”, yeniden başlamak, odak ve umut üzerinden kısa bir giriş verilir. Varsayılanlar görünür seçili durumdadır ve kullanıcı hepsini kaldırabilir. Sıfır seçimde gerekçe görünür, Devam devre dışıdır. Seçili durum yalnız renkle ifade edilmez; checkbox ve semantik durum da bulunur. Kilitli konular ilk kurulumda gösterilmez.

### Hatırlatıcı planı

Günde bir bildirim, 09.00–21.00 aralığı başlangıç önerisidir. Bu, tüm kullanıcılar için “en iyi” sıklık olduğu iddiası değildir. Kullanıcı 1–7 ve saat aralığını değiştirebilir. İzin yalnız açık biçimde “Hatırlatıcıları aç ve başla” seçildiğinde istenir. “Şimdilik bildirimsiz devam et” eşzamanlı görünürdür. Planı istemek ile işletim sisteminin izin vermesi ayrı durumlar olarak saklanır/gösterilir.

### Bugün

Kaynakta alıntı, seri, okuma hedefi, bildirim hedefi, kilit keşif önerisi ve haftalık geçmiş aynı deneyimde rekabet ediyordu. Yeni ekranın ilk işi okunabilir tek bir olumlamadır. Gerçek sayfa sayısı, açık gezinme, Kaydet ve Paylaş bunu izler. Kısa ritüel metni okunan cümleyle nasıl zaman geçirilebileceğini anlatır. Hatırlatıcı durumu ikincil karttır. Konu keşfi daha aşağıda bulunur. Kilit satışı başlangıç deneyiminin ana hedefi değildir.

Okuma sayacı artık bildirim teslimatıyla artmaz. Aynı içerik aynı gün tekrar görüntülendiğinde yeniden sayılmaz. Bununla birlikte “görüldü” olayı kullanıcının gerçekten dikkatle okuduğunu ispatlamaz; raporlarda “okuma tamamlandı” gibi güçlü bir anlama dönüştürülmemelidir.

### Keşfet

Yeni olumlama grubu ilk açılır. Eski gruplar ve kategori kimlikleri korunur; böylece önceki favoriler bozulmaz. Seçim değişikliği ana akışı günceller. Kategorilerin tümü içerik açısından eşit dolulukta değildir: boş veya çok zayıf eski grupların editoryal genişletilmesi ikinci aşamadadır. Kilitli içerik sunumu gerçek reklam entegrasyonu olmadan “izle ve aç” diye vaat etmez.

### Kaydedilenler

Boş hâl bir hata değildir. Kullanıcıya kalple içerik saklayabileceği anlatılır ve okumaya dönme eylemi sunulur. Dolu durumda içerik yeniden ana ekranda açılabilir; kaldırma kontrolü büyütüldü. Bir sonraki aşamada arama, kişisel koleksiyonlar ve kaldırmayı geri alma düşünülebilir. Bunların uygulanmış olduğu iddia edilmez.

### Yolculuk

“İstatistik” yerine Yolculuk dili kullanılır. Mevcut seri ve toplamlar korunurken “bir gün ara vermek ilerlemeni silmez” metni eklenir. Seri hâlâ ardışık açılış günlerini ölçer; aktif okuma, düşünme veya iyi oluş ölçümü değildir. Daha sonraki ürün kararı, seriyi isteğe bağlı yapmak ve son yedi günün ziyaretlerini daha açıklayıcı sunmaktır.

### Ayarlar ve paylaşım

Ayarlar; görünüm, bildirimler, etkileşim ve hakkında bilgilerini tutar. Bildirim anahtarı gerçek durum bildirir; sistem izni eksikse ayarlara giden eylem vardır. Temalar/dil sarılan kontrol satırlarında görünür. Paylaşım stüdyosunun mevcut görsel/video üretimi korunmuştur; bu sürümde marka ve geri davranışı düzeltilmiştir. Fotoğraf seçimi, video üretim iptali, hata bildirimi ve yüksek çözünürlüklü dışa aktarmanın fiziksel cihaz testi ayrıca gereklidir.

## 5. Görsel sistem kararları

- Lora içerik ve büyük başlıklarda, sistem sans arayüzde kullanılır. Yeni büyük başlık 32/40sp, gövde 16/24sp; yardımcı etiketler 12sp’dir.
- Ana içerik 24dp çevre boşluğu ve 28dp köşe ile okunabilir bir yüzeyde sunulur. Kontrol grupları 8–12dp, içerik blokları 20–24dp aralıkla ayrılır.
- Var olan bordo, lacivert ve yosun temaları korunur; küçük metinler okunurluk için güçlendirilir. Renk değişimi tek başına çözüm sayılmaz.
- Düğme üzerindeki metin için koyu/açık temaya göre ayrı ön plan kullanılır. Accent’in hem dolgu hem yazı olarak kullanılmasının kontrast etkisi test edilir.
- Android’in standart seçim ve navigasyon bileşenleri erişilebilir durum bilgisini taşır. Özel boyutlu ve yalnız renge dayanan kontroller azaltılır.
- Onboarding zorunlu animasyon içermez. Diğer ekranlarda var olan animasyonların kapsamlı azaltılmış hareket denetimi sonraki kontroldür.

## 6. Öncelik sırası ve kabul ölçütleri

**Bu teslimat:** yeni onboarding, olumlama içeriği, ana ekran düzeni, kritik durum/erişilebilirlik düzeltmeleri ve Actions.

**Sonraki ürün turu:** bütün kategorilerin içerik doluluğu ve alıntı atıflarının editoryal denetimi; favori kaldırmada geri alma; paylaşım hata/iptal durumu; kullanıcı istediğinde onboarding’i yeniden gösterme; arka plandan dönünce tarih ve sıradaki hatırlatma bilgisinin periyodik tazelenmesi.

**Yayın öncesi:** kalıcı üretim imzası, mağaza açıklamaları/görselleri, gizlilik metni, veri yedekleme tercihi, gerçek reklam sağlayıcısı kararı, fiziksel cihazlarda bildirim teslimatı ve TalkBack incelemesi. Bu liste hipotetik bir izin talebi değil, test sürümü ile üretim yayını arasındaki tamamlanmamış iştir.

## 7. Kullanıcı araştırması önerisi

İlk tur için beş katılımcıyla nitel kullanılabilirlik testi önerilir; beş kişinin davranışı istatistiksel dönüşüm oranı olarak yorumlanmaz. Görevler: uygulamanın ne yaptığını kendi cümlesiyle anlatma; başlangıç konularını değiştirme; bildirim istemeden devam; bir içerik kaydetme ve tekrar bulma; günlük hatırlatıcıyı kapatma. Her görevde başarı, geri dönüşler, yardım ihtiyacı ve yanlış beklenti not edilir.

Analitik ancak ayrı ürün/veri kararı sonrasında eklenmelidir. Önerilen olaylar: onboarding görüntüleme/adım ilerleme, kurulum tamamlama, bildirim tercihi, ilk kaydetme ve sonraki gün dönüş. Özel içerik/duygu metinleri olay verisine eklenmemelidir. Bu sürüm bir analitik SDK eklemez; dönüşüm kazanımı ölçülmüş değildir.

## 8. Dayanaklar

- [Android Compose erişilebilirlik varsayılanları](https://developer.android.com/develop/ui/compose/accessibility/api-defaults): minimum dokunma hedefleri ve semantik kontroller.
- [Android bildirim izni](https://developer.android.com/develop/ui/compose/notifications/notification-permission): izin bağlamı ve reddedilmiş izin davranışı.
- [GitHub Actions ücretlendirmesi](https://docs.github.com/en/billing/concepts/product-billing/github-actions): standart public runner kullanımı ve saklama sınırları.

Kaynak kodundan çıkarılan bulgular yukarıdaki dosya/simge kanıtlarına dayanır; dış kaynaklar uygulamada kullanıcı kaybı ölçüldüğünü kanıtlamaz.
