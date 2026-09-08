# Ascend 6 — UI/UX ve kategori erişim raporu

**Tarih:** 8 Eylül 2026

**Uygulama hedefi:** 6.0.0 / 15

**Kapsam:** Ana ekranı temel alan görsel düzenleme; bildirim planı; 70 bağımsız kategori; kategori görselleri; A–dağ kimliği; alt gezinme; Yolculuk; ücretsiz paylaşım ve Pro demosu.

**Doğrulama durumu:** 70 kategori görseli ve 19 yeni sahnenin üretimi, paketlenmesi, görsel eşleşme incelemesi ve dosya bütünlüğü kontrolleri tamamlandı. Önceki dört sahneyle birlikte uygulamadaki 23 sahnenin kaynak dosyaları da doğrulandı. GitHub Actions derlemesi ve Android akış doğrulaması **PENDING** durumundadır; bu aşamada yeni APK veya cihaz testleri başarılı kabul edilmez. Sürümün kabul adımları ve sonuç alanları [test planındadır](13-ASCEND-6-TEST-PLANI.md). Önceki sürümün başarılı testleri bu sürüme ait sonuç olarak kullanılmaz.

## 1. Bu güncellemenin temel kararı

Ascend, tek bir genel motivasyon havuzu olarak sunulmamalı. Bir kullanıcının işe başlamak için aradığı motivasyon, uzun süre emek vermek için aradığı azim ve bir aksaklıktan sonra yeniden denemek için aradığı söz aynı ihtiyaç değildir. Uygulamanın kategori yapısı bu farkı ilk bakışta anlatmalıdır.

Önceki kodda zaten 70 alt kategori ve her birinde 10 söz vardı. Kullanıcının gördüğü ana Keşfet kartları ise 12 geniş aileyi temsil ediyordu; erişim de aileye veriliyordu. Böylece bir açılış, 5–9 kategoriyi ve 50–90 sözü birlikte açabiliyordu. Sorun yalnız içerik sayısı değildi: gerçek seçim birimi ekranda görünmüyordu.

V6'da **kategori, erişim ve bildirim seçiminin birimi** haline gelir. **Aileler yalnız gezinme filtresidir.** Her kategori kendi adı, ilgili görseli, 10 sözlük kapsamı, iki örnek metni ve açık/seçili/kilitli durumu ile sunulur. Motivasyon, Azim ve Pes Etmemek ayrı kartlardır. Birinin açılması diğerlerini açmaz.

Yeni kurulumda 6 tam kategori ücretsizdir. Geri kalan 64 kategori tek tek demo ile açılabilir veya açıkça belirtilen Pro demosuyla birlikte erişilebilir. Bu dağılım, gelir açısından kanıtlanmış bir optimum değildir. İçeriği deneyimleme, anlaşılır bir ödül ve tekrar kullanım arasında seçilen başlangıç dengesidir.

## 2. Gönderilen beş ekran görüntüsündeki sorunlar

| Görsel | Gözlenen sorun | V6 kararı | Kullanıcının göreceği fark |
|---|---|---|---|
| 1 — Konu seçimi | Dağ ve deniz fotoğrafları farklı kartlarda tekrarlanıyor. “Felsefe” gibi geniş bir etiket, seçilen gerçek alt konuyu saklıyor. | Başlangıçta gerçek kategori adları ve kategoriye özel görseller kullanılır. | Kullanıcı Marcus Aurelius'u mu, bütün felsefeyi mi seçtiğini tahmin etmez. Seçili çerçeve ve onay işareti aynı anda görünür. |
| 2 — Bildirim planı | Başlık ilerleme çizgisine dayanıyor; iri sürgüler sayıyı ve aralığı okumayı zorlaştırıyor. Aynı saatler birden fazla çizgide tekrar ediliyor. | Başlık ile ilerleme alanı ayrılır. Sayı için azalt/artır, saatler için iki kart ve tek gerçek plan önizlemesi kullanılır. | “Günde kaç kez?” ve “Hangi saatlerde?” iki ayrı, kısa karar olur. |
| 3 — İzin kurulumu | Büyük bildirim örneği ve uzun açıklamalar asıl izin eylemini aşağı itiyor. Ayrıntı rehberi ile zorunlu adımın hiyerarşisi zayıf. | İzin durumu ve izin düğmesi önde; görünüm/pil rehberi gerektiğinde açılır. Bildirimsiz devam yolu korunur. | Kullanıcı izin verdiğini, sistem ayarına gitmesi gerektiğini veya bildirimsiz ilerleyebildiğini anlayabilir. |
| 4 — Ana ekran | Fotoğraflı söz kartı güçlü; alt gezinme ayrı bir eski bileşen gibi duruyor. Üst filtreler ile bildirim tercihleri karışabiliyor. | Dağ kartı korunur; kategori filtreleri somut adlar kullanır. “Bildirim konuların” alanı isimleri gösterir. Alt bar yeniden tasarlanır. | Ana odak sözde kalır; hangi konulardan bildirim geleceği ayrıca okunur. |
| 5 — Keşfet | Büyük grup kartları 30/50/90 söz sunuyor; görüntüler tekrar ediyor. “Açık” ve “bildirimde seçili” durumları yeterince ayrışmıyor. | 70 bağımsız kart; arama, aile filtresi, Seçtiklerim/Açık konular filtreleri ve kategori ayrıntısı. | Bir kategori açmanın kapsamı önceden görülür; erişim ile bildirim üyeliği ayrı anlaşılır. |

Yolculuk, marka simgesi ve paylaşım erişimi de kullanıcının bu ekranlara ek olarak istediği alanlardır. Bunlar aşağıdaki bölümlerde ayrı kararlar halinde ele alınır.

## 3. Rakip araştırması neyi destekliyor?

I am'in resmî ürün sayfası, yaşam alanına göre kategori seçimini ve arka plan, hatırlatıcı, widget kişiselleştirmesini birlikte sunuyor. Ascend için alınan yön, kullanıcının seçtiği konu ile günlük deneyimi arasındaki bağı görünür kılmak oldu. Metinleri veya görselleri kopyalanmadı. [I am — resmî ürün sayfası](https://theiam.app/)

Motivation'ın resmî sayfası, kullanıcının ilgilendiği yaşam alanlarını seçmesini; tema, widget ve hatırlatıcıları kişiselleştirmesini anlatıyor. Ascend'de tek baskın söz kartı ve küçük kişiselleştirme kontrolleri bu ürün ailesine uygun bir düzen oluşturuyor. [Motivation — resmî ürün sayfası](https://motivation.app/)

Bu sayfalar rakiplerin gelirini, gerçek kullanıcı kaybını, ülkeye göre reklam getirisini veya en başarılı ücretli kategori sayısını göstermiyor. Bu nedenle “rakip böyle yapıyor, gelir artışı kesindir” sonucu çıkarılmadı. Mağaza içi satın alma duvarlarının bütün ülke/sürüm varyantları da bu incelemede doğrulanmış sayılmıyor.

AdMob'un resmî playbook'u, ödüllü reklamı kullanıcının seçtiği bir değer değişimi olarak ele alıyor; ödülün anlaşılır ve yeterli olması ile farklı düzenlerin denenmesine dikkat çekiyor. Ascend için çıkarımımız, önce kategori içeriğini göstermek ve yalnız kullanıcının açmak istediği konu için teklif sunmak. Kaynaktaki oyun örneklerinin gelir yüzdeleri Ascend'e taşınmadı. [AdMob — Rewarded Ads Playbook](https://admob.google.com/home/resources/rewarded-ads-playbook/)

Gerçek reklam entegrasyonuna geçildiğinde eylem ve ödül önceden açıkça belirtilmeli, ödüllü reklam için açık kullanıcı seçimi alınmalı, vazgeçmek normal kullanımı bozmamalı ve tamamlanan eylemin ödülü verilmelidir. Google'ın ödülü doğruladığı izlenimi yaratılmamalıdır. Bunlar şu anki Google.com demosunun reklam ölçümü yaptığı anlamına gelmez. [AdMob — ödüllü reklam politikası](https://support.google.com/admob/answer/7313578?hl=en-GB)

## 4. Kategori yapısı ve içerik değeri

### Görünür seçim birimi

Her kart **10 ayrı özgün düşünce** sunar. Kategori ayrıntısında bunlardan ikisi önizlenebilir; önizleme metinleri toplam 10'a ek içerik değildir. Açılan kategorinin 10 sözünün tamamı kullanılabilir. Bu sürüm söz başına reklam, her okumadan sonra kilit veya süre dolunca yeniden açma uygulamaz.

Aşağıdaki `*` işareti yeni kurulumda ücretsiz olan kategorileri gösterir. Aile adı bir satın alma veya ödül paketi değildir.

| Gezinme ailesi | Bağımsız kategoriler | Kategori / söz |
|---|---|---:|
| Günlük olumlamalar | Kendime nazik davranmak*, Biraz yavaşlamak*, Kendime güvenmek | 3 / 30 |
| Azim & Dayanıklılık | Motivasyon*, Azim*, Pes Etmemek, Zorluğa Karşı Sabır, Yorulmaya Karşı Sabır, Tükenmişlik, Yeniden Başlamak, Uzun Soluklu Hedefler, Umut | 9 / 90 |
| Disiplin & Odak | Erteleme, Derin Odak*, Dürtü Kontrolü, Rutin Kurmak, Dikkat Dağınıklığı | 5 / 50 |
| Özgüven & Cesaret | Kendine Güven, Korkuyla Yüzleşme, Reddedilme, Risk Almak, Utangaçlık | 5 / 50 |
| Filozoflar | Marcus Aurelius*, Seneca, Epiktetos, Platon, Aristoteles, Nietzsche, Konfüçyüs, Machiavelli | 8 / 80 |
| Tasavvuf & Doğu | Mevlânâ, Yunus Emre, Şems, Hafız, Zen | 5 / 50 |
| İnanç | Kur'an üzerine düşünceler, İncil üzerine düşünceler, Tevrat üzerine düşünceler, Dua, Şükür | 5 / 50 |
| Spor & Beden | Antrenman, Dayanıklılık, Sakatlıktan Dönüş, Sabah Rutini, Beslenme Disiplini | 5 / 50 |
| İş & Başarı | Girişimcilik, Kariyer, Liderlik, Para, Zaman Yönetimi, Başarısızlık, Başarı | 7 / 70 |
| İlişkiler | Aşk, Ayrılık, Aile, Arkadaşlık, Yalnızlık, Affetmek | 6 / 60 |
| Zihin & Huzur | Kaygı, Stres, Minnettarlık, Şimdiki An, Uyku, Karamsarlık, Huzur | 7 / 70 |
| Öğrenme & Gelişim | Merak, Okumak, Hata Yapmak, Alışkanlık, Sınav & Öğrencilik | 5 / 50 |
| **Toplam** | **70 kategori; yeni kurulumda 6 ücretsiz, 64 açılabilir** | **70 / 700** |

Yakın başlıkların farklı değer üretmesi gerekir. Motivasyon başlama enerjisini, Azim tekrar eden emeği, Pes Etmemek aksama sonrası yeniden denemeyi işler. Uzun Soluklu Hedefler daha uzun bir zaman ufkuna; Spor & Beden içindeki Dayanıklılık fiziksel tempo ve koşullara odaklanır. Bu farkın asıl kanıtı kategori adı değil, içerideki metinlerdir. İki örnek metin, açma kararından önce bu farkı değerlendirmeye yardım eder.

70 başlık kullanıcıyı aynı anda 70 karar vermeye zorlamaz: altı konu onboarding'de tanıtılır; Keşfet'te arama, 12 aile ve açık/seçili filtreleri kullanılır. Arama sonucu boşken çıkış yolu vardır. En son seçili kategori sessizce kaldırılamaz; bildirim havuzunun boşalması önlenir ve neden açıklanır.

### Tek İngilizce kaynak korunuyor

Bu arayüz güncellemesi yeni bir söz kataloğu üretmez. [İngilizce ham kaynak](../content/source.en.json), 700 özgün düşüncenin tek kanonik dosyasıdır; [Türkçe dosya](../content/translations/tr.json) aynı kimliklere bağlı 700 çeviriyi içerir. İkisini toplamak 1.400 bağımsız söz olduğu anlamına gelmez. Kaynak ve çeviri dosyalarında bu UI çalışmasına ait içerik değişikliği yapılmadığı kontrol edilmiştir.

Düşünür/gelenek adları altındaki sözler özgün Ascend düşünceleridir; doğrulanmış tarihsel alıntı veya kutsal metin olarak yeniden etiketlenmez. Kategori ayrıntısında bu ayrım görünürdür. İçerik üretimi ve sonraki dillerin düzeni [v5 içerik raporunda](09-ASCEND-5-ICERIK-RAPORU.md) ayrıntılıdır. Uygulama sürümünün 6 olması, değişmemiş ham içerik paketinin sürümünü zorla değiştirmeyi gerektirmez.

## 5. Erişim, bildirim seçimi ve eski kullanıcı hakları

| Durum | Erişim | Bildirim seçimine etkisi |
|---|---|---|
| Yeni kurulum | Altı ücretsiz kategorinin 60 sözü | Başlangıç seçimi kullanıcı tarafından düzenlenir. |
| Tek kategori demosu tamamlandı | Yalnız istenen kategori kalıcı yerel hak olarak eklenir. | Açılış açıklamasında belirtildiği gibi yalnız o kategori seçime eklenir. |
| Pro demosu açıldı | 70 kategori erişilebilir olur. | Mevcut seçim aynen korunur; 70 konu otomatik seçilmez. |
| Pro demosu kapatıldı | Ücretsiz ve tek tek kazanılmış haklar kalır. | Artık erişilemeyen konular seçimden çıkar; boşalırsa kullanılabilir başlangıç seçimine dönülür. |
| V5'ten verisi korunarak güncellenen kurulum | Önceki 25 ücretsiz kategori, açılmış ailelerin bütün alt kategorileri ve geçerli eski seçim hakları korunur. | Geçerli mevcut seçim korunur; yeni kullanıcıya uygulanan altı kategori sınırına düşürülmez. |

Erişim ile abonelik/konu seçimi iki ayrı durumdur. Keşfet'te **Kilitli**, **Açık, seçilmedi** ve **Bildirimlerinde seçili** ayrımı; ana ekranda ve Keşfet'te **Bildirim konuların** isim listesi bunu açıklar. Dörtten çok seçimde liste genişletilebilir. Ana ekranın üst filtreleri o an okunan sözleri süzer; tek başına bildirim üyeliğini değiştirmez.

Eski verinin taşınması bir kez, aynı kayıt işlemi içinde yapılır. Birden fazla veri okuyucusu veya açma işlemi aynı anda çalıştığında hakların yeniden hesaplanıp silinmemesi gerekir. Yeni kurulum, onboarding sonrasında yanlışlıkla eski kullanıcı kabul edilmez. Bu ayrımlar için ayrı erişim ve gerçek Android veri deposu testleri hazırlanmıştır.

“Kalıcı” burada **uygulama verisi korunduğu sürece** demektir. Bu sürümde hesapla hak kurtarma veya bulut senkronizasyonu yoktur. Uygulamayı silip yeniden kurmak yerel hakları ve kaydedilenleri kaybettirebilir. Sabit test imzası ayrıca yapılandırılmadıysa eski test APK'sının üstüne kurulum imza farkından dolayı mümkün olmayabilir; bu durum, veriyi koruyan geçiş testinden ayrıdır.

## 6. Bildirim planının yeni düzeni

Planlama ekranındaki büyük sürgüler kaldırılır. Günlük sayı, 1–7 sınırları arasında iki 52 dp düğmeyle değiştirilir. Sınırda ilgili düğme kapanır; sayı kısa bir geçişle güncellenir. Başlangıç ve bitiş ayrı kartlardır. Saat seçicisi yalnız geçerli tam saatleri sunar; bitiş başlangıçtan sonra olmak zorundadır.

“24:00” sayısı ekranda **00:00 — Gece yarısı** olarak açıklanır. Böylece kullanıcının gün sonunu mu, gün başlangıcını mı seçtiği belirsiz bırakılmaz. Kodda gün sonu sınırı 24 olarak korunur.

**Günün planı**, gerçek zaman hesaplayıcısıyla aynı saatleri gösterir. Örneğin 09:00–21:00 aralığı ve üç bildirim için örnek saatler 11:00, 15:00 ve 19:00'dır. Tek bildirimde örnek saat 15:00 olur. Saatler aralığın eşit parçalarının ortalarından alınır; çizim için ayrı, gerçeğe uymayan noktalar üretilmez.

Bu saatler planlanan yaklaşık teslim zamanlarıdır. Cihazın güç tasarrufu ve arka plan koşulları teslimatı geciktirebilir; dakika garantisi verilmez. Aralığın dışına bilerek yeni bildirim planlanmaz. Onboarding ve Ayarlar aynı plan bileşenini kullanır.

İlerleme çizgisi ile içerik arasında ayrı bir boşluk ve kırpma sınırı vardır. Her adımın kaydırma durumu ayrılır; uzun bir sayfadan geçiş, yeni başlığı çizginin altına sürüklememelidir. Büyük yazıda saat kartları ve sayı alanı dikey yerleşebilir. İzin sayfası da bu akışın ayrı dördüncü adımı olarak kalır.

## 7. Görsel kimlik ve küçük etkileşimler

### Ana ekran ve görseller

Dağ fotoğraflı söz kartı ana odaktır. Yazı, imza ve kaydet/paylaş/dinle/kopyala eylemleri kartla ilişkilidir. Ana ekranın bir gösterge paneline dönüşmemesi için seçili bildirim konuları ve günlük plan, sözün ardından kısa yardımcı alanlar halinde sunulur. Dar ekranda veya büyük yazıda dört eylem iki satıra geçebilir. Kaydetme gibi durum değiştiren eylemlerde geri bildirim kullanıcının haptik tercihine uyar.

**70 kategoriye 70 ayrı ilgili fotoğraf** tamamlandı. Her kategori anahtarının kendi dosyası ve görsel konusu bulunuyor; kaynak ve paketlenmiş dosyalar arasında tekrar yok. Geniş arka plan koleksiyonu **23 sahneye** çıkarılmıştır. Kullanıcının karanlık fantastik figür ve dokulu renk isteği; birden fazla şövalye ortamı, Cadı ve yedi ayrı renk dokusuyla karşılanır. Bütün sahneler ana ekranın arka plan seçiminde bulunur; Zirve dışındakiler paylaşımda Pro kapsamındadır.

| Görsel ailesi | Adet | Sahne adları |
| --- | ---: | --- |
| Manzaralar | 6 | Zirve, Gün batımı, Bilgelik, Orman, Kale, Dağ hisarı |
| Efsaneler | 10 | Arena, Şövalye, Cadı, Kar muhafızı, Atlı yolcu, Orman muhafızı, Taş salon, Çöl yolcusu, Kıyı nöbeti, Kale nöbeti |
| Dokular | 7 | Bordo dokusu, Turkuaz dokusu, Grafit, Gece mavisi, Ametist, Zeytin, Bakır |

Ana ekrandaki **Arka plan seç** penceresi bu üç aileyi filtre olarak sunar. İki sütunlu görsel önizlemelerde seçili karta vurgu ve onay işareti eklenir; ekran okuyucu seçim durumunu okur. Pencere mevcut seçimin ailesinde açılır ve seçili öğeye kayar. Aile filtresine dokunmak arka planı değiştirmez; bu ancak bir görsel seçildiğinde gerçekleşir. **Konuya göre otomatik** seçeneği ayrı, her zaman görünür bir satırdır. Görsel adları sabit yükseklikte kesilmez; uzun İngilizce adlar ve büyük yazı satıra yayılır. İçerik yüksekliği ekranla sınırlı, görsel ızgara kaydırılabilirdir.

Üretim istemleri, kategori eşlemeleri, kaynak ve paketlenmiş dosya özetleri [kategori görsel envanterinde](category-art.json) ve [yeni sahne envanterinde](scene-art.json) tutuluyor. `tools/check_category_art.py`; 70 eşleşme, dosya bütünlüğü, kaynak/çıktı benzersizliği, WebP sınırları ve uygulama kaynak eşlemesi denetimlerini geçti. `tools/check_scene_art.py` de 19 yeni sahnenin dosya özetlerini, gerçek WebP boyutlarını ve benzersizliğini; 23 sahnenin tamamının uygulama kaynaklarında bulunmasını doğruladı. İki denetim de Actions'ta Android derlemesinden önce çalışır. Yetmiş kategori görselinin tamamı kendi istemiyle karşılaştırılarak gözle incelendi; 19 yeni sahnenin paketlenmiş çıktıları da açılıp kontrol edildi. Şövalye ortamları birbirinden ayrılırken orta bölüm söz için sakin bırakıldı; yedi renk dokusu kendi adına uygun, belirgin ve ölçülü malzeme dokusu taşıyor. Cihazdaki son kırpım ve yazı okunurluğu denetimi Android ekran doğrulamasının parçası olarak **PENDING** durumundadır.

Kategori kapakları **512 × 683 WebP** olarak toplam **3.978.318 bayt**, 19 yeni geniş sahne **1080 × 1440 WebP** olarak toplam **5.197.010 bayt** tutuyor. Tamamlanan **89 yeni görselin toplamı 9.175.328 bayt (yaklaşık 9,18 MB)**. Önceki dört sahneyle birlikte uygulamada toplam 23 hazır sahne vardır. Özgün üretim dosyaları korundu; paketleme yalnız orantılı boyutlandırma ve WebP dönüşümü uyguladı. Kategori kapakları küçük kartlar için optimize edildiğinden, paylaşımda 1080 piksel genişliğine büyütülen “Konu görseli” seçeneği geniş sahnelere göre daha yumuşak görünebilir.

### A–dağ simgesi ve alt gezinme

Marka, A harfinin bacaklarını bir zirveyle birleştiren çizimden oluşur. Sıcak kum yüzey, daha koyu sağ yüz ve açık kar tepesi koyu mavi zemine yerleşir. Launcher'ın dairesel/yuvarlak kare kırpımları için işaret merkezde tutulur; bildirimde tek renk karşılığı kullanılır. Splash ve kısayollar aynı aileye aittir.

Alt gezinme, zeminden hafif ayrılan yuvarlak bir yüzeye dönüşür. Bugün için dağ, Keşfet için pusula, Kaydedilen için ayraç, Yolculuk için patika kullanılır. Etiketler kaldırılmaz. Seçili durum; renk, yüzey ve ikonla gösterilir. 180 ms renk/ölçek geçişi kullanılır; sürekli oynayan veya kullanıcıdan dikkat isteyen dekorasyon eklenmez. Sekmelerde en az 64 dp yüksekliğinde etkileşim alanı ve gerçek sekme semantiği bulunur.

### Yolculuk

Yolculuk ekranı, son yedi gündeki gerçek ziyaret sayısını dağ fotoğrafı üzerinde gösterir. Haftalık izde günler tam tarihli erişilebilir açıklamalara sahiptir. Okunan söz, kaydedilen söz, en uzun seri ve açık kategori sayısı mevcut kayıtlardan gelir. Grafik uğruna uydurma ilerleme, zirve yüzdesi veya gerçekleşmemiş başarı eklenmez. Alt bölümde küçük bir patika çizimi ve kısa bir tempo cümlesi yeterlidir.

## 8. Paylaşımın ücretsiz ve Pro sınırı

Kullanıcının kararı: **üç temel arka plan ücretsiz; diğer arka planlar ve gelişmiş araçlar Pro.** Üç ücretsiz seçenek Zirve, Gece ve Kâğıt'tır. Ücretsiz standart çıktı Story oranında PNG'dir; varsayılan yazı, boyut, ortalama ve karartma korunur.

| Özellik | Ücretsiz | Pro demosu |
|---|---|---|
| Zirve / Gece / Kâğıt | Var | Var |
| Diğer sahneler, renkler, gradyanlar | — | Var |
| Sözün kendi kategori görseli | — | Var; temel sahneyle aynıysa tekrar listelenmez. |
| Kullanıcının fotoğrafı | — | Var |
| Standart Story PNG, paylaşma ve kaydetme | Var | Var |
| Kare / yatay çıktı, yazı ailesi, boyut, karartma, hizalama | — | Var |
| 5 / 10 / 30 / 45 saniyelik sessiz MP4 | — | Var |

Yirmi üç sahne, sekiz düz renk ve altı gradyan toplam **37 hazır arka plan** oluşturur. Bunların üçü ücretsizdir. İlgili kategori görseli ve fotoğraf seçicisi ayrıca sunulur. Paylaşım stüdyosunda **Ücretsiz / Manzaralar / Efsaneler / Dokular / Renkler** filtreleri uzun tek listeyi böler. İlk görünüm Ücretsiz'dir ve Zirve, Gece, Kâğıt doğrudan görünür. Kategori görseli Manzaralar'da, bütün düz renkler ve gradyanlar Renkler'dedir. Kişisel fotoğraf düğmesi filtrelerden bağımsızdır. Kullanıcı başka aileye bakarken mevcut seçim korunur ve **Seçili:** satırında adı görünür; önizleme kendiliğinden değişmez. Ücretsiz kullanıcıya kilitli seçenekler Pro rozetiyle gösterilir ve dokununca açıklama açılır. Filtreler ile kendi içindeki seçenekler yatay kaydırılabilir; ana ekran seçicisi iki sütunlu ve yüksekliği sınırlı kaydırma alanındadır.

Pro ekranı “PRO DEMO” olarak adlandırılır. **Ödeme alınmaz. Abonelik başlatılmaz.** Bu açıklama etkinleştirme düğmesinin hemen üstünde kalır. Demoyu açma ve kapatma ayrı açık eylemlerdir. Fiyat, indirim, deneme sonunda ücret veya gerçek satın alma başarısı taklit edilmez.

Erişim yalnız düğmede denetlenmez. Yeniden oluşturulan önizleme ve dışa aktarılacak ayarlar aynı kurala tabidir. Pro kapanırsa gelişmiş seçimler ücretsiz karşılıklarına döner; devam eden iş iptal edilir. Üretimden önce ve dosya tesliminden önce erişim yeniden kontrol edilir. Ücretsiz arka plan seçimi mümkünse korunur; video, özel tipografi veya premium sahne eski ekran durumundan sızmamalıdır.

## 9. Geliri hangi verilerle değerlendireceğiz?

Kategori sayısını artırmak reklam gelirini doğrusal artırmaz. Konular yeterince farklı değilse yeni kartlar yalnız arama yükü yaratır. Ödül yetersizse kullanıcı bağlantıyı açmaz; çok genişse diğer konuların değeri görünmez. Bu yüzden temel öneri, **anlamlı küçük kategori + dürüst önizleme + tek ve kalıcı ödül** düzenidir.

İlk incelemede yeni kullanıcıların şu işleri yardımsız yapıp yapamadığı ölçülmeli: Motivasyon ile Azim'i ayırmak; bir kategorinin açık mı seçili mi olduğunu söylemek; yarın hangi konulardan bildirim geleceğini bulmak; sayıyı/saatleri değiştirmek; üç ücretsiz arka planla paylaşmak. Bu görevler anlaşılmadan daha fazla açma teklifi eklemek önerilmez.

Gerçek reklam ve mağaza sistemi ayrıca kurulduğunda değerlendirme için aşağıdaki olay adları kullanılabilir. **Bu sürüm bir analitik SDK'sı veya bu olayları gönderen bir sistem eklemez.** Bunlar gelecekteki ölçüm taslağıdır; demo tıklaması gerçek reklam gösterimi, Pro demo açılması da satın alma sayılmaz.

| Ölçüm | Hesap / kullanım | Yanında izlenecek kalite işareti |
|---|---|---|
| Kategori önizlemesinden açma isteğine geçiş | Açma isteği / önizleme oturumu | Önizlemeden hemen çıkış ve aramada sonuçsuz kalma |
| Açılan konunun kullanılması | Açılan konuyu bildirimlere ekleyen veya okuyan kullanıcı | Başka kategori seçimini yanlışlıkla kaybetme |
| Gerçek ödül tamamlama | Gerçek reklam ödül olayı / başlatılan gerçek reklam | Vazgeçiş, hata ve verilmeyen ödül |
| Bildirim tercihinin anlaşılması | Kurulum tamamlandıktan sonra konuları/saatleri doğru açıklayabilme | İlk gün bildirimleri kapatma ve sayıyı azaltma |
| Paylaşım başarısı | Başarıyla üretilen dosya / üretim isteği | Hata, iptal, boş veya okunmayan çıktı |
| Kalıcı kullanım | Yeni kurulum grubunda 1. ve 7. gün geri dönüş | Yalnız oturum süresini uzatmaya çalışma yerine faydalı tekrar kullanım |

Olası olay adları `category_preview_opened`, `category_unlock_requested`, `reward_earned`, `share_export_completed` ve `pro_offer_viewed` olabilir. İçerik metni, fotoğraf, dosya yolu veya hassas konu tercihlerini dışarı göndermeyen toplu ölçüm yeterli olmalıdır. Olay adlarının belgede bulunması, uygulamada veri toplandığı anlamına gelmez.

Gelir, ancak gerçek ve geçerli gösterim sayısı ile gerçek eCPM ölçüldüğünde hesaplanabilir: yaklaşık reklam geliri `geçerli gösterim / 1000 × eCPM` olur. Yeni kullanıcı ve eski hakları korunan kullanıcı grupları ayrı değerlendirilmelidir. Varsa gelecekteki denemeler yeni kullanıcılar üzerinde yürütülmeli; önceden verilmiş haklar geri alınmamalıdır. Bu sürüm için gelir, dönüşüm veya elde tutma artışı yüzdesi ileri sürülmez.

## 10. Entegrasyon ve teslim sınırı

V6'nın kaynakta ele aldığı alanlar: kategori erişimi ve geçişi, bildirim planı, seçili konuların görünürlüğü, marka/nav/Yolculuk, Pro demosu, paylaşım erişimi ve 23 sahnenin filtreli seçimi. Tam kapsamın 89 yeni görseli, dosya envanterleri ve son boyut ölçümleri doğrulandı. Uygulamanın derlenmesi, gerçek Android akışlarının çalıştırılması ve ortaya çıkan ekranların incelenmesi ayrı teslim kapılarıdır; bunlar henüz **PENDING** durumundadır.

Bu sürümde gerçek AdMob reklamı, Google Play Billing, fiyatlandırma, abonelik, hesapla hak kurtarma veya analitik SDK'sı yoktur. Google.com yönlendirmesi ve Pro anahtarı yalnız açıkça adlandırılmış test yollarıdır. Gerçek reklam/billing geçişi; demo ödülünün ilgili SDK'nın doğrulanmış sonucuyla değiştirilmesini ve başarısız/iptal edilmiş işlemlerde hak verilmemesini gerektirir.

Test cihazındaki ekran görüntüsü görsel kanıttır; geçerli APK imzası kurulum kanıtıdır; test raporu davranış kanıtıdır. Bunların hiçbiri tek başına her telefon, her bildirim ayarı ve her paylaşım uygulaması için eksiksiz doğrulama yerine geçmez. Son sürümün kanıtları [kabul planındaki sonuç alanına](13-ASCEND-6-TEST-PLANI.md) eklenmelidir.
