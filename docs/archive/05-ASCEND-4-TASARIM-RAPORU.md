# Ascend 4.0 — Ürün ve tasarım yenilemesi

Tarih: 7 Eylül 2026. Dayanak: Kullanıcının paylaştığı altı uygulama/derleme ekranı, paylaşım stüdyosu referansı ve mevcut Android kaynak kodu. Bu rapor, 3.0 sürümü için yazılan önceki tasarım kararlarını günceller. Kabul edilen ürün kapsamı: günlük olumlamalar, motivasyon, azim, odak ve düşünürlerden sözler; seçilen konulardan gün içine yayılan bildirimler.

## 1. Öncelik ve problem tanımı

Ana ekran, bu güncellemenin birinci önceliğidir. Önceki sürüm doğru çalışan bazı akışlara sahip olsa da bir içerik deneyimi yerine büyütülmüş bir metin formu hissi veriyordu. Kullanıcı ilk bakışta konunun duygusunu, ürünün çeşitliliğini veya bir sözü kendine göre paylaşma imkânını göremiyordu. Büyük serif başlıklar, büyük serif gövde ve uzun açıklamalar birbirleriyle yarışıyordu. Görsel ağırlık, yalnızca metin boyutunu artırarak kurulmuştu.

Yeni yön: fotoğraflı söz kartı, kısa arayüz metinleri, güçlü fakat sınırlı vurgu rengi, görsel koleksiyonlar, erişilebilir eylemler ve gerçek dosya üreten bir paylaşım stüdyosu. Söz ana içerik olmaya devam eder; ortam, tipografi ve hareket onun sunumunu destekler.

## 2. Bulgular ve uygulanan karşılıklar

| Öncelik | Kanıtlanan sorun | Kullanıcı üzerindeki etkisi | 4.0 çözümü |
|---|---|---|---|
| P0 | Ana ekranda görsel yok; büyük düz metin kartı var | İlk izlenim zayıf, içerik tekdüze | Dört özgün manzara, konuya göre otomatik eşleşme, manzara değiştirme |
| P0 | Başlangıçta yalnız özşefkat ve sakinlik varsayılan | Motivasyon ve düşünürler ürünün arkasında kalıyor | Motivasyon + olumlama + felsefe varsayılan karışımı |
| P0 | Ayrıntılı bildirim yönlendirmesi onboarding dışında kalmış | Özellikle Samsung kullanıcıları sözün kısa bölümünü görüyor | Dördüncü adımda izin, görünüm, kanal ve deneme bildirimi |
| P0 | Genişletilmiş bildirim de kırpılmış gövdeyi kullanıyor | Ayrıntıya geçilse bile söz eksik | BigTextStyle içine tam söz ve yazar |
| P0 | Video hatasında sessizce görsel gönderiliyor | Seçilen biçimle çıktı uyuşmuyor | Açık hata, yeniden deneme; sessiz biçim değiştirme kaldırıldı |
| P1 | Galeriye kaydetme yok | Paylaşılacak içerik saklanamıyor | Android 10+ MediaStore kaydı; Android 8/9 dosya seçicisi |
| P1 | Sabit önizleme görsel olarak zayıf | Sonuç anlaşılmadan ayarlarla uğraşılıyor | Büyük Story önizlemesi ve altta doğrudan arka plan seçimi |
| P1 | Ekranda farklı M3 varsayılan renkleri karışıyor | Mor kaydet düğmesi ile bordo marka çelişiyor | Kum vurgusu, gece mavisi yüzeyler, uyumlu ikincil renkler |
| P1 | Bildirim sıklığında son sayı tek başına yeni satıra düşüyor | Dengesiz düzen, gereksiz dikey alan | 1–7 arası kademeli kaydırıcı ve belirgin adet |
| P1 | Keşfet uzun, tek renk akordeon liste | Koleksiyonların farkı algılanmıyor | Fotoğraflı uyarlanabilir ızgara ve konu/düşünür araması |
| P1 | Bazı alt kategorilerde içerik yok | Kullanıcı boş akış seçebiliyor | Gerçek söz sayısı, içeriksiz alt başlıkların açık etiketlenmesi |
| P1 | Felsefe reklam kilidinin arkasında ve reklam hazır değil | Ürünün temel içerik kolu erişilemiyor | Felsefe başlangıç için ücretsiz gruplara eklendi |
| P1 | Kaydedilenler yalnız metin listesi | Saklanan sözden paylaşım zor bulunuyor | Görsel favori kartları, doğrudan paylaşım ve çıkarma |
| P1 | Video her karede arka planı yeniden açıyor | Yavaşlık ve bellek baskısı | Fotoğrafı örnekleyerek açma ve video boyunca bir kez yükleme |
| P1 | Encoder beklemesi sınırsız | Destek sorunu ilerlemede takılabiliyor | Sonlandırma zaman aşımı, iptal ve kaynak temizliği |
| P1 | Aynı çıktı dosyası her paylaşımda üzerine yazılıyor | Önceki paylaşımın dosyası değişebiliyor | Her çıktı için benzersiz dosya adı |
| P2 | Sesli okuma tamamlanınca durum yenilenmiyor | Durdur düğmesi açık kalıyor | TTS tamamlanma/hata geri bildirimi |
| P2 | Eski Actions sürümleri Node.js 20 uyarısı üretiyor | Derleme bakımı erteleniyor | Resmî sürümlerden güncel Actions sürümleri |
| P2 | Gradle wrapper çalıştırılabilir değil | Her koşuda uyarı | Git dosya modu 100755 |

P0: Temel deneyimi bozan sorun. P1: Bu sürümün ana akışını etkileyen sorun. P2: Bakım veya ikincil deneyim iyileştirmesi. Bunlar bir kullanılabilirlik çalışmasının ölçülmüş puanları değil; ekran ve kod incelemesine dayalı önceliklendirmedir.

## 3. Ana ekran tasarımı

### Görsel hiyerarşi

Üstte kısa ASCEND imzası ve tarih yer alır. Ayarlar tek bir yuvarlak simgeden erişilir. Hemen altındaki yatay filtre, kullanıcının seçili içerik gruplarını gösterir; “Senin için” tüm seçili grupların karışımıdır. Filtreler bildirim tercihlerini değiştirmez; yalnız ekranda okunan akışı süzer.

Ana kart, ekranın baskın görselidir. Konuya uygun bir ortam fotoğrafı üzerinde söz, yazar ve dört eylem bulunur: kaydet, paylaş, dinle, kopyala. Arka planın üzerindeki karartma ve beyaz yazı okunurluğu destekler. Uzun içerik için daha küçük başlangıç boyutu kullanılır. Büyük yazı ayarında kart boyu büyüyebilir ve sayfa kaydırılabilir; metni sırf ekrana sığdırmak için kesmek temel davranış değildir.

Kartlar yatay kaydırılır. “Önceki” ve “Sonraki” kontrolleri, kaydırma hareketini yapamayan kullanıcılar için de bırakılmıştır. Sayfa sayacı ve kaydırma ipucu hareketi keşfedilebilir kılar. Manzara seçiminden dört ortam elle seçilebilir veya konuya göre otomatik eşleşmeye dönülebilir.

Kartın ardından bildirim planı özeti gelir: kapalı, izin gerekli, plan açık veya sıradaki saat durumları birbirinden ayrılır. Ekranın devamında görsel koleksiyon kısayolları ve varsa bugün gelen sözler bulunur. Bu düzen, ilk bakışta bir sözle temas kurdurur; daha fazla içerik isteyen kişiye devam alanı bırakır.

### Görsel dil

- Zirve: motivasyon, azim, dayanıklılık.
- Gün batımı: olumlama, iç huzur, ilişkiler.
- Bilgelik: düşünürler, felsefe ve ilgili düşünce koleksiyonları.
- Orman: odak, öğrenme ve çalışma.

Koyu tema için gece mavisi zemin ve sıcak kum vurgusu; açık tema için kâğıt tonları kullanılır. Daha önce kaydedilmiş kullanıcının renk seçimi korunur. Bordo, lacivert ve yosun seçenekleri kaldırılmaz. Tipografide arayüz başlıkları sans serif, sözler Lora olarak ayrılır.

## 4. Onboarding

| Adım | Amaç | Kullanıcının yaptığı iş | Sonuç |
|---|---|---|---|
| 1 — Karşılama | Ürünün duygusunu ve kapsamını anlatmak | “Kendi yolunu oluştur” | Hesapsız başlangıç |
| 2 — İlgi alanları | Ürünü üç ana içerik koluyla tanıtmak | Motivasyon, olumlama, felsefe, odak, azim, iç huzur seçimi | Akış ve bildirimler için başlangıç konuları |
| 3 — Günlük ritim | Sıklık ve zaman aralığını anlaşılır yapmak | 1–7 söz, başlangıç/bitiş saati | Görsel zaman önizlemesi |
| 4 — Bildirim kurulumu | Gerçek teslimat ve okunurluk | Sistem izni, ayrıntılı görünüm rehberi, kanal ayarı, deneme | Bilinçli bildirim tercihi |

Geri dönüş seçimleri kaybetmez. Ekran yeniden oluşturulduğunda adım ve seçimler saklanır. Tüm varsayılanları kaldırıp kendi konularını seçmek mümkündür; boş seçimle sonraki adıma geçilmez. Son adımda bildirim izni açık değilse bildirimli başlangıç düğmesi etkinleşmez. “Şimdilik bildirimsiz devam et” görünür ve işlevseldir.

Bildirim izni istenirken uygulama kurulumu otomatik tamamlamaz. Kullanıcı izin verdikten sonra ayrıntılı görünüm rehberini okuyabilir ve deneme gönderebilir; en son başlangıcı tamamlar. Tercihler tek işlemde kaydedilir. Önceki kullanıcının tamamlanmış onboarding verisi sıfırlanmaz; aynı rehber Ayarlar’daki “Ayrıntılı bildirim kurulumu” bölümünden açılır.

## 5. Bildirimlerin iki ayrı ayarı

Android’in uygulama bildirim izni ile Samsung’un kısa/ayrıntılı açılır pencere tercihi farklıdır. Android 13+ çalışma zamanı izni, uygulamanın bildirim gönderebilmesini belirler. Ayrıntılı görünüm, ekrana gelen bildirimin sunuluş biçimidir. Kaynak: [Android bildirim izni](https://developer.android.com/develop/ui/views/notifications/notification-permission).

Samsung’un güncel One UI 8 yönergesi uygulama bazlı yolu gösterir: Bildirimler → Uygulama bildirimleri → uygulama → açılır pencere stili. Önceki sürümlerde genel Bildirimler bölümündeki açılır pencere stili yolu bulunur. İki yol rehberde ayrı gösterilir; cihaz/model farkı belirtilir. Ascend, ayarın gerçekten “Ayrıntılı” olup olmadığını okuyormuş gibi bir onay göstermez. Kaynaklar: [Samsung One UI 8](https://www.samsung.com/hk_en/support/mobile-devices/how-to-use-updated-notification-settings-in-samsung-one-ui-8/), [Samsung genel bildirim ayarları](https://www.samsung.com/us/support/answer/ANS10002549/).

Yeni kurulumda kanal açılır bildirime uygun önem düzeyiyle oluşturulur. Mevcut kanalın kullanıcının değiştirdiği önem düzeyi korunur. Kanal ayarına kısayol, eski kurulumlarda ekranda açılır görünümü seçmek için sunulur. Rahatsız Etmeyin ve cihazın sessiz ayarları sonucu etkileyebilir. Deneme bildirimi normal okuma istatistiğine eklenmez.

Gün içindeki zamanlar, tercih edilen aralığa dağıtılır. WorkManager işletim sistemi tarafından geciktirilebilir; kesin dakikada alarm vaadi verilmez. Tam sözü okumak için genişletilmiş görünüm ve bildirime dokunarak uygulamada açma yolları birlikte bulunur.

## 6. Paylaşım stüdyosu

Stüdyo ana ekranın üzerinde açılan büyük bir alt paneldir. Ana ekrandaki konum korunur. Varsayılan oran 9:16 Story’dir. Önizlemenin altında dört manzara, düz gece/kâğıt yüzeyleri ve kullanıcının fotoğrafını seçme seçeneği bulunur. Görsel ve video modu görünür bir bölümle ayrılır. Video seçildiğinde 5, 10, 30 ve 45 saniye sunulur.

“Biçim ve yazı” bölümünde kare, Story ve yatay oranlar; Lora, klasik, modern ve daktilo karakterleri; yazı büyüklüğü ve fotoğraf karartması yer alır. Ayrıntılar başlangıçta kapalı tutularak temel paylaşım yolunun önüne geçmeleri önlenir. Önizleme, görsel ve video kareleri aynı çiziciyi kullanır. Video sessizdir; sözün kelimeleri giderek belirir, fotoğrafta hafif yakınlaşma olur. Stüdyodaki statik kart önizlemesi videonun son yerleşimini gösterir.

“Paylaş” Android’in paylaşım seçicisini açar; uygulama başka bir kişiye otomatik göndermez. “Galeriye kaydet” Android 10+ üzerinde Pictures/Ascend veya Movies/Ascend konumunda dosya oluşturur. Android 8/9’da dosya seçiciyle konum seçilir. Bütün galeriye erişim izni istenmez; yalnız kullanıcının seçtiği fotoğrafa erişilir. Kaynak: [Android paylaşılan medya depolama](https://developer.android.com/training/data-storage/shared/media).

PNG çözünürlükleri: kare 1080×1080, Story 1080×1920, yatay 1920×1080. Video H.264/MP4, 30 kare/saniye; kare 720×720, Story 720×1280, yatay 1280×720. Video çözünürlüğünün daha düşük olması, cihazdaki kodlama yükünü azaltmak içindir.

Üretim sırasında ilerleme ve iptal bulunur. İş başarısız olduğunda hata görünür; video yerine izinsizce görsel gönderilmez. Her çıktı benzersiz dosyadır. Galeriye yarım yazılan kayıt hata halinde silinir. Eski paylaşım önbelleği 24 saatten sonra temizlenir. Büyük fotoğraflar sınırlı boyutta açılır ve video kareleri için tekrar tekrar okunmaz.

## 7. Benzer uygulamalardan alınan desenler

[Motivation](https://motivation.app/) kişiye uygun konuları, arka planları ve gün içindeki hatırlatıcıları aynı deneyim içinde sunuyor. Ascend’de bu yaklaşımın karşılığı, seçili konu karışımı + görsel ana kart + görünür bildirim özetidir. [I am](https://theiam.app/) olumlamalar için tema ve hatırlatıcı kişiselleştirmesini öne çıkarıyor. Ascend, olumlamayı bu ailelerden biri olarak sunar; motivasyon ve felsefeyi dışarıda bırakmaz.

Kullanıcının verdiği paylaşım referansından büyük kart önizlemesi, arka plan şeridi, görsel/video ayrımı, süre ve iki ayrı çıktı eylemi alındı. Rakip marka, metin, ekran veya varlıkları kopyalanmadı. Araştırma masa başı ürün incelemesidir; rakip kullanıcıları üzerinde yapılmış bir test veya dönüşüm ölçümü değildir.

## 8. Derleme uyarıları ve dağıtım

Node.js 20 kullanan eski adımlar yerine resmî güncel sürümler doğrulandı: [checkout 7.0.1](https://github.com/actions/checkout/releases/tag/v7.0.1), [setup-java 6.0.0](https://github.com/actions/setup-java/releases/tag/v6.0.0), [upload-artifact 7.0.1](https://github.com/actions/upload-artifact/releases/tag/v7.0.1), [setup-gradle 6.3.0](https://github.com/gradle/actions/releases/tag/v6.3.0). Android derleyici ve uygulama bağımlılıkları sırf yeni sürüm var diye topluca değiştirilmedi. Wrapper çalıştırma izni Git içinde düzeltildi.

Public depolarda standart GitHub barındırmalı çalıştırıcıların işlem süresi ücretsizdir. Bu, her kaynağın sınırsız olduğu anlamına gelmez: büyük çalıştırıcılar ücretlidir, saklama ve eşzamanlılık sınırları vardır. Mevcut düzen standart Ubuntu kullanır; çıktıları 7 gün saklar ve aynı dalda önceki koşuyu iptal eder. Kaynak: [GitHub Actions faturalandırma](https://docs.github.com/en/billing/concepts/product-billing/github-actions).

## 9. Görsel varlıkların kaynağı

Dört manzara yerleşik imagegen aracıyla bu proje için üretildi; rakiplerden veya bir stok fotoğraf sitesinden indirilmedi. Üretilen PNG dosyaları uygulamanın `app/src/main/res/drawable-nodpi/` klasörüne kopyalandı: `scene_summit.png`, `scene_sea.png`, `scene_wisdom.png`, `scene_forest.png`. İnternet bağlantısı olmadan kullanılabilirler. Özgün dosyalar üretim klasöründe de korunur. Son üretim istemlerinin tam metni [görsel istemleri belgesindedir](06-GORSEL-ISTEMLERI.md).

## 10. Kabul ölçütleri ve sınırlar

Bu rapordaki uygulama değişiklikleri kaynak kodda yer alır. APK derlemesi, cihaz testleri, ekran görüntüleri ve gerçek çıktı dosyalarının sonuçları ayrı teslim/doğrulama belgesine yazılır. Test tamamlanmadan bir cihaz davranışı doğrulanmış olarak kabul edilmez.

Üretime çıkış öncesi gerçek Samsung cihazında One UI menülerinin ve üretici video kodlayıcısının kontrolü gerekir. Otomatik Android emülatör testi bu üreticiye özgü kontrolün yerini tutmaz. Bildirim zamanlamasının uzun süreli pil tasarrufu altındaki davranışı ayrıca gerçek kullanımda izlenmelidir. Reklam entegrasyonu hâlâ hazır olmadığı için diğer kilitli gruplar açıkça kilitli kalır; gelir sistemi bu görsel güncellemede yeniden kurulmadı. İçerik atıflarının kaynak eser düzeyinde editoryal doğrulaması ayrı bir içerik işidir; bu güncelleme alıntıların tarihsel doğruluğunu sertifikalandırmaz.


### Felsefe başlangıç içeriği

Yeni ücretsiz felsefe grubunda minimum içerik kontrolü bir eksik yakaladı. Grup toplamını 10 söze tamamlayan beş kısa, iki dilli uyarlama eklendi. Bunlar Marcus Aurelius’un *Düşünceler*, IV. kitap 2, 3, 17, 20 ve 24 bölümlerinden türetildi; uygulamada ve paylaşımda “uyarlama/adapted” etiketi taşır. Doğrudan tarihsel alıntı olarak sunulmaz. İncelenen kaynak: [George Long çevirisi, Internet Classics Archive](https://classics.mit.edu/Antoninus/meditations.4.four.html). Mevcut sözlerin kimlikleri değiştirilmedi.
