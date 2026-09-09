# Ascend 8 — beyaz mermer ve mürekkep tasarım araştırması

Tarih: 9 Eylül 2026. Bu belge, kullanıcının aynı gün paylaştığı arayüz referansı ile mevcut Ascend 7 kaynak kodunun birlikte değerlendirilmesidir. İlk tasarım sözleşmesi uygulamadan önce yazılmıştır; ekran incelemeleri ve doğrulama ölçütleri aynı belgede genişletilir. Belge bir tasarım gerekçesidir; APK veya cihaz testlerinin sonucu değildir.

## 1. Tasarım yönü ve değişmeyecek ürün davranışı

Yeni yön: beyaz sayfa, siyah hatlar, sola hizalı güçlü serif söz, kenarlarda çok hafif klasik mermer figürleri. Arka plandaki büst, sütun ve yaprak; okunacak cümlenin çevresine kimlik katar. Ana içerik görselin içinde eritilmez. Sarı, altın, mavi ve turuncu yeni arayüzün vurgu veya yüzey rengi değildir. Çok az derin yeşil; isteğe bağlı bordo ve siyah mürekkep varyantları kullanılabilir.

Ascend'in 20 ekranlık, atlanabilir ve kaldığı yerden devam eden gerçek kişisel plan akışı korunur. Yanıtlar hâlâ akış, bildirim ve widget seçimini etkiler. Kategori dışlamaları, mevcut kayıtlar, İngilizce ham içerik kaynağı, Türkçe çeviriler, üç ücretsiz paylaşım arka planı, Pro demosu ve açıkça belirtilen tarayıcı reklam demosu korunur. Bu çalışma yeni hesap, ödeme, psikolojik değerlendirme, yapay zekâ veya reklam SDK'sı eklemez.

## 2. Uygulama öncesi tasarım sözleşmesi

| Alan | Karar | Gerekçe |
|---|---|---|
| Ana zemin | `#FFFFFF` | Referanstaki açık beyaz sayfa; sarıya veya krem rengine kaymayan temel |
| Ana mürekkep | `#161916` | Keskin okuma ve çizgisel kimlik |
| Varsayılan vurgu | Mermer: `#3E4B40` | Çok sınırlı derin yeşil; ana buton veya seçilmiş küçük işaret |
| İsteğe bağlı varyant | Bordo: `#643B48`; Mürekkep: siyah | Aynı nötr omurga üzerinde vurgu değişimi |
| Eski paletler | Kum/Lacivert/Yosun seçimlerini Mermer'e taşı; seçim listesinden çıkar | Eski tercihin yeni yasaklanan veya önceki omurgaya ait renkleri geri getirmemesi |
| Duvar kâğıdı rengi | Dinamik renk devre dışı | Sistem duvar kâğıdından mavi/turuncu/altın türemesini önler |
| İlk kurulum | Aydınlık | Yeni tasarım ilk açılışta görünür |
| Önceden seçilmiş görünüm | Açıkça seçilmiş Mürekkep/Bordo ve tema modu korunur; karanlıkta nötr kömür omurga | Kullanıcının okuma tercihi kaybolmaz; lacivert yüzeye dönülmez |
| Ana söz | Sola hizalı serif, yaklaşık 32 sp / 40 sp satır yüksekliği | Referanstaki editoryal hiyerarşi; gerçek metin korunur |
| Dekorasyon | Üst sağ büst, alt sol sütun/yaprak; erişilebilirlik ağacından çıkarılmış | Bilgi taşımayan görsel ayrıntılar ekran okuyucuyu bölmez |
| Alt gezinme | Bugün / Keşfet / Senin; düz beyaz yüzey, çizgi + ikon + etiket | Mevcut üç durak korunur; büyük kapsül ve renkli kart yok |
| Hareket | Kısa durum geçişleri; sürekli hareket yok | Söz okunurken dikkat sabit kalır |

İlk opaklık önerisi, uygulamada tonal koruma ile tamamlandı: `KlasikGorsel` aydınlıkta her RGB kanalını `0,3 × kanal + 0,7` ile açık aralığa taşır; gerçek alfa kanalı korunur. Görsel opaklığının üst sınırı aydınlıkta 0,55, karanlıkta 0,07'dir; bileşenin varsayılan değeri 0,16 ve yerleşimleri küçüktür. Böylece en karanlık kaynak piksel bile aydınlıkta 0,7 kanal tabanından başlar. Beyaz üzerinde tek katman en az 0,835 kanal değeri bırakır. İki katman için çarpımla alınan ihtiyatlı alt sınır 0,697225'tir; bu bir sRGB kanal değeridir, nispi parlaklık değildir. Küçük ikincil ana ekran etiketleri ayrıca opak okuma zeminiyle korunur. Ayrıntılı hesap 7.2'de verilmiştir; bu sınırlar tek başına bütün ekranların görsel olarak doğrulandığı anlamına gelmez.

## 3. Kaynaklarla desteklenen temel ölçütler

- W3C normal metin için en az 4,5:1, büyük metin için 3:1 kontrast eşiği açıklar. İnce yazı çizgilerinin pratikte daha soluk görünebileceğini de belirtir. Ascend ana sözde minimuma yakın bir renk kullanmaz; mürekkep ile geniş bir güvenlik payı hedefler. [W3C — Contrast Minimum](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html)
- Durum veya kontrolü tanımak için gerekli görsel işaretlerin komşu renkle 3:1 kontrastı olmalıdır. Çok açık ayraç yalnız dekor olarak kullanılabilir; seçili durumun tek kanıtı olamaz. [W3C — Non-text Contrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html)
- Seçimler yalnız renkle anlatılmaz: tik, seçilmiş rolü, belirgin çizgi ve gerektiğinde etiket birlikte çalışır. [W3C — Use of Color](https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html)
- Android Compose dokümanı etkileşim alanlarının en az 48 dp olmasını önerir. Dekoratif görseller için açıklamanın boş bırakılması, işlevli ikonlar için anlamlı yerelleştirilmiş açıklama kullanılması gerekir. Görsel ikon küçük olabilir; dokunma hedefi küçülmez. [Android — Accessibility API defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults)
- Android 14, yüzde 200'e kadar yazı ölçeklemeyi destekler. Tasarım bu tercihi sabit boyutla bastırmaz; uzun Türkçe seçenekler, İngilizce metin ve dar ekran birlikte kontrol edilir. [Android 14 — Font scaling](https://developer.android.com/about/versions/14/features#non-linear-font-scaling)

Bu eşikler ve platform özellikleri kaynak desteklidir. Renkler, opaklık sınırları, 32/40 tipografi oranı ve klasik figürlerin yerleşimi; kullanıcının referansına göre alınmış Ascend tasarım kararlarıdır. Başka bir ürünün dönüşüm oranını artırdığı iddiası veya kullanıcı araştırması sonucu olarak sunulmaz.

## 4. Referansın ayrıntılı okunması

Referans tek bir ana ekran tasarım örneğidir. Yalnızca renk paleti alınırsa aynı duyguyu vermeyecektir. Ekranın karakteri beş ilişkinin birlikte çalışmasından doğar:

1. **Sol kenar çizgisi:** marka, anlık ihtiyaç filtresi, konu çizgisi, söz ve kaynak ortak bir başlangıç çizgisi oluşturur. Ortadaki bütün metin bloklarını ayrı ayrı merkezlemek yerine sayfa okunacak bir metin gibi davranır.
2. **Ölçek farkı:** büyük serif söz ile küçük, yalın arayüz etiketleri birbirinden açıkça ayrılır. Söz bir ekran başlığına dönüşmez; ekranın kendisi olur. Yardımcı açıklamalar sözle aynı görsel ağırlığa çıkarılmaz.
3. **Asimetrik denge:** sağ üstteki yüz ve sol alttaki sütun birbirini dengeler. Figürlerin merkezi göz veya ağız ayrıntıları metnin harfleri kadar koyu değildir. Gövde kırpılması bilinçlidir; bir kartın içine konulmuş fotoğraf hissi oluşmaz.
4. **Boşluğun işlevi:** konu ile söz, söz ile kaynak ve okuma ile eylem arasında farklı mesafeler vardır. Her şeyin çevresine eşit miktarda boşluk koymak yerine yakınlık, birlikte okunacak parçaları eşleştirir.
5. **Az sayıdaki güçlü işaret:** yükselen çizgi, kısa konu çizgisi, kaydet kalbi ve seçili gezinme işareti; küçük bir çizgi ailesi gibi görünür. Her bölüme yeni dekoratif ikon eklemek bu bütünlüğü zayıflatır.

Referansta italik bir ifade bulunuyor. V8'de cümlede rastgele sözcük seçerek otomatik italik uygulanmaz: dil değişince yanlış vurgu ve anlam kayması oluşabilir. Mevcut katalogda onaylı vurgu aralıkları bulunmadığı için söz tek tipte ve değişmeden gösterilir. Serif aile, çizgi, hizalama ve boşluklar zaten gerekli ayrımı sağlar.

Referanstaki küçük sloganı birebir yerleştirmek de zorunlu değildir. Sağ kenarda çok dar sütun hâlinde harfleri açmak, küçük telefonda okunamayan yeni bir bilgi katmanı yaratabilir. Kimlik; daha önce kullanılan “Bir söz. Bir adım.” ve “Kendi hızında.” ifadesiyle sürdürülebilir. Bu ifadeler ana sözden sonra gelir; bir başarı veya üstünlük vaadi oluşturmaz.

## 5. Dış ürünlerden alınan örüntüler ve sınırları

Bu turda karşılaştırma, bütün pazarı yeniden saymak yerine kullanıcının yeni görsel yönüne temas eden örneklerle sınırlandı. Kaynaklar 9 Eylül 2026'da açıldı; ürünlerin bütün ücretli akışları fiziksel cihazda denenmedi. Önceki geniş rakip değerlendirmesi [Ascend 7 araştırmasında](15-ASCEND-7-ARASTIRMA-VE-TASARIM.md) bulunur.

| İncelenen kaynak | Kaynakta gözlenen bilgi | Ascend'e taşınan tasarım çıkarımı |
|---|---|---|
| [I am resmî sitesi](https://theiam.app/) | Kategori seçimi; tema, hatırlatma ve widget kişiselleştirmesi ayrı ürün katmanları olarak anlatılır | Kişisel planın içeriği ile görsel tema seçimini birbirine karıştırma. Mermer estetiğini seçmek, felsefe içeriklerine otomatik onay vermek değildir |
| [Motivation resmî sitesi](https://motivation.app/) | İlgili yaşam alanlarını seçme ve gün içinde söz alma; arka plan ve hatırlatıcı özelleştirme sunulur | Ana okuma yüzeyi kolay kalırken düzenleme araçları kullanıcı çağırdığında açılır. Daha fazla görsel tema, ana ekranda daha fazla kontrol gerektirmez |
| [The Met Collection](https://www.metmuseum.org/art/collection) | Arama, koleksiyon bölümleri, eserleri inceleme ve araştırma kaynakları ayrı girişlere sahiptir | Keşfet içerik bulma işidir; fotoğraf koleksiyonu paylaşım stüdyosunda kalır. Arayüz, sanat nesnesini çevrelerken başlık ve açıklamayı açık tutabilir |
| [Rijksmuseum Collection Online](https://data.rijksmuseum.nl/about/) ve [Stories](https://www.rijksmuseum.nl/en/stories) | Koleksiyonu sanat ve tarih anlatılarıyla ilişkilendirme; farklı kataloglar arasında tutarlı bilgi düzeni | Aynı metin kaynağını farklı sunumlarda koru. Okuma, kategori ve paylaşım ayrı görsel yoğunluklar kullanabilir, fakat isim ve durumların anlamı değişmez |

Bu örneklerden “serif daha çok kazandırır”, “mermer kullanıcıyı daha uzun tutar” veya “beyaz zemin her kullanıcı için daha iyidir” sonucu çıkarılamaz. Bu sürüm için beyaz mermer yönünün dayanağı kullanıcının açık tercihidir; kaynaklar bu tercihin okunabilir ve tutarlı uygulanmasını destekler. Rakiplerin sağlık veya dönüşüm iddiaları Ascend'e taşınmaz. Museum kaynakları görüntü temin edilmiş gibi gösterilmez; yeni dekoratif eserler bu arayüz için hazırlanır ve tarihî kişi/eser olarak etiketlenmez.

## 6. Mevcut v7 akışının denetimi

Kod incelemesine ek olarak son v7 bulut çıktılarının ana ekran, onboarding sorusu, Keşfet ve Senin görüntüleri açılarak değerlendirildi. Aşağıdaki bulgular v7'ye aittir; v8'in bitmiş ekran sonucu değildir.

| Öncelik | Alan | Mevcut durum | V8 kararı |
|---|---|---|---|
| P0 | Ana söz | Ortalı söz, ortalı konu ve kaynak; birbirine yakın görsel ağırlıklar | Sol hizalı editoryal hiyerarşi; kenarda kontrollü mermer figür, ayrı konu çizgisi |
| P0 | Renk sistemi | Mürekkep varsayılan olsa da Kum/Lacivert ve dinamik renk hâlâ seçilebilir | Tek nötr omurga; yasak renkli eski tercihler güvenli palete çözülür |
| P0 | Pro | Fotoğraflı koyu tanıtım alanı ve sabit altın ikon var | Ana uygulamayla aynı mermer/serif dil; altın “premium” kodu kaldırılır |
| P0 | Başlangıç | Gerçek 20 ekranlık plan var; başlık ve seçenekler standart form görünümünde | Süreç ve yanıtların etkisi korunur; başlık, adım, seçenek ve alt eylem aynı editoryal sistemle yeniden düzenlenir |
| P1 | Alt gezinme | Doğru üç durak korunmuş; küçük gösterge var | Aynı çizgi kalınlığı, optik ikon hizası ve dengeli etiketler; gereksiz çevre kapsülü yok |
| P1 | Keşfet | Görselsiz içerik listesi iyi; başlık, arama ve çoklu filtreler yüksek alan kullanıyor | İçerik daha erken başlar; arama belirgin, filtreler ikincil; konu durumları yazıyla desteklenir |
| P1 | Senin | Gerçek veri gösteriliyor; profil planı geniş gri kart | Daha sakin plan özeti, gerçek istatistiklerin editoryal bölümlerle ayrılması; büyük metinde alt alta düzen |
| P1 | Planım | Tam özet mevcut fakat çok sayıda yüzey ve farklı köşe dili var | Aynı başlık ve bölücü sistemi; öncelikli/uygun/kilitli konu farkı kaybolmaz |
| P1 | Paylaşım | Çok zengin kütüphane; önizleme ve kontroller arasında görsel rekabet potansiyeli | Sabit başlık, anlaşılır seçim durumu, nötr araç çerçevesi; renkli eserler ürün arayüzünden ayrılır |
| P1 | Küçük yazılar | Bazı rozetler 9 sp, yardımcı metinler 10–11 sp | Temel erişim ve ücret bilgisi küçük süs yazısı olamaz; okunabilir boyut ve kontrast korunur |
| P2 | Boş/hata durumları | İkon ve metin ağırlıklı standart görünümler | Aynı sayfa dili, tek anlaşılır sonraki eylem; dekor ve satış baskısı eklenmez |

### 6.1 Ana ekran

Üst satırda yükseliş çizgisi ve serif “ascend” sözcüğü birlikte yer alır. “Planım” sağda kısa bir metin eylemidir; büstün yüzüne denk gelmez. Bir sonraki satırda anlık ihtiyaç seçicisi sola hizalanır. Bu kontrolün genel bildirim planından ayrı olduğu, açılan panelin kısa açıklamasında açık kalır.

Okuma alanı kalan yüksekliği kullanır. Konu satırında yaklaşık 24 dp çizgi ve kısa etiket, ardından 20–28 dp nefes ve büyük söz gelir. Sözün genişliği dar bir metin sütununa zorlanmaz; telefonun iki yanında yaklaşık 24 dp güvenli boşluk kullanılır. Kaynak/ifade türü sözün altında aynı sol çizgide kalır. Düşünür temalı bir dekor, o sözün tarihî kişiye ait olduğunu ima eden yazar etiketi üretmez.

Önceki/sonraki işlemleri alt okuma sınırındadır; ortadaki küçük ifade bir sayaç veya yeni sekme gibi çizilmez. Kaydet, Paylaş ve Diğer işlemleri tek bir eylem grubudur. Kaydet ince fakat işlevi anlaşılır kontur, Paylaş küçük bir derin yeşil dolgu kullanabilir. Diğer aracı daire içindedir; üç nokta gözden kaybolacak ölçüde küçülmez. Kaydedildiğinde dolu kalp ve durum açıklaması birlikte değişir. Başarı mesajı sözün üstüne büyük bir kutlama katmanı bindirmez.

Normal yazı ölçeğinde ana eylemler ve alt gezinme, sayfa aşağı kaydırılmadan görünür. Çok büyük yazıda söz için kontrollü iç kaydırma kabul edilir; metni kırpmak veya kullanıcının yazı boyutunu küçültmek kabul edilmez. Dekor bu durumda önce küçülür veya sakinleşir. Kullanıcı adı çok uzunsa butonlara yer açmak için başlık veya kaynak alanıyla çakışmaz.

### 6.2 Onboarding — bütün 20 adım

Bu sürüm yeni soru ekleyerek süreyi uzatmaz. Sayfa başına bir görev ve her soru için atlama davranışı korunur. Başlık serif olabilir; yanıt seçenekleri yalın sans serif kalır. Dekor yalnız karşılama ve geçiş niteliğindeki plan ekranlarında daha görünür olur; 13 tercih ekranında başlıktan ve seçeneklerden uzağa çekilir.

| Adımlar | Tasarım görevi | İnce ayrıntı |
|---|---|---|
| 1 Karşılama | Yeni kimliği ilk saniyede göster | Yükseliş çizgisi, serif başlık, tek sakin mermer parça; hızlı başlangıç okunabilir |
| 2 Ad | İsteğe bağlı hitap | Alan etiketi yazarken kaybolmaz; klavye açıldığında Devam erişilir; Atla boş isimle ilerler |
| 3 İçerik türü | Olumlama / motivasyon / düşünce / karışım | Görsel tema bu cevabın yerine geçmez; seçenekler birbirinden renk olmadan ayrılır |
| 4 Amaç | Planın ana yönü | Tek seçim; seçili durum tik ve konturla anlaşılır |
| 5 Enerji | Dönemsel ihtiyaç | Klinik test görünümü ve puan yok; yalın dil korunur |
| 6 Ton | Sözün yaklaşımı | Sert veya yumuşak dil, italik süslemeyle değil gerçek yanıtla belirlenir |
| 7 Zorluk | İçerik ağırlığı | Uzun seçenekler iki satırda; radyo simgesi ilk satıra sıkışmaz |
| 8 Harekete geçiren | Destek biçimi | Seçim, açıklama ve Devam arasında sabit ritim |
| 9 Bağlam | Birden fazla alan | “Birden fazla seçebilirsin” açık; checkbox semantiği kullanılır |
| 10 Değerler | Birden fazla tercih | Aynı seçim örüntüsü; seçilmemiş seçenekler kapalıymış gibi soluklaştırılmaz |
| 11 İlham | Düşünsel eğilim | Klasik büst seçilmiş bir yazar anlamına gelmez |
| 12 Uzunluk | Söz boyu | Kısa/uzun seçimi önizlemede gerçek içeriğe yansımaya devam eder |
| 13 Keşif | Sürpriz karışım | “Yalnız seçimlerim” ile yeni bakışlara izin ayrı ve açık |
| 14 Kaçınılanlar | İstenmeyen içerik | Hariç tutma bir uyarı rengiyle korkutulmaz; işaret ve ifade yeterlidir |
| 15 Maneviyat | Açık onay | Atlama onay değildir; dekoratif Roma figürü bu kararı etkilemez |
| 16 Sıklık | Günde kaç bildirim | Büyük sayı, iki 48+ dp kontrol, kısa açıklama; slider karmaşası geri gelmez |
| 17 Saatler | Başlangıç / bitiş | Büyük okunur saatler; seçildiği belli olan saat listesi; yaklaşık saat notu korunur |
| 18 Önizleme | Yanıtların gerçek sonucunu göster | Gerçek erişilebilir söz; kısa plan özeti; yanlış bir yükleme animasyonu ile “analiz” taklidi yapılmaz |
| 19 Erişim modeli | Ücretsiz / tek konu demosu / Pro demosu | Aynı beyaz sayfada okunabilir hak açıklaması; altın rozet, sahte fiyat ve zaman baskısı yok |
| 20 Bildirim izni | Açık sistem izni | İzin ver ve bildirimsiz devam görünür; cihaz rehberi gerektiğinde açılır |

Adım numarası, geri ve atlama konumları kararlıdır. İlerleme çizgisi metnin üstüne binmez. Geçişte bir önceki soru kaybolurken yeni seçeneklerin yanlış soruya yazılmaması için veri kimliği korunur. Seçim tek tıklamayla kaydedilir fakat kullanıcı Devam ile ilerler; ekran kendi kendine sıçramaz. Sonuç sayfasından önce geriye gidildiğinde önceki işaret görünür olmalıdır.

### 6.3 Keşfet ve kategori ayrıntısı

Keşfet bir resim galerisi değildir. Başlık serif, arama alanı sade, liste düzeni hafif çizgilerle ayrılan sans serif satırlardan oluşur. Her satırda konu adı, alan veya erişim durumu ve tek sağ işaret yeterlidir. “Seçili”, “Açık”, “Demo ile aç” birbirinin yerine kullanılamaz: seçili bildirim önceliğidir, açık erişim hakkıdır.

Arama sonucu ve filtre seçimi değişince sayfa başlığı zıplamaz. Sonuç yoksa mevcut aramanın boş olduğu söylenir; uygulamada hiç içerik yokmuş gibi bir boş durum gösterilmez. Birden fazla filtre varsa “Tümü”ne dönebilme açık kalır. Kilitli satır tıklanınca açılacak kategori adı ve demo davranışı görülür; kilit küçük ama kontrastlıdır. Kategori detayında uzun açıklamalar ve örnek sözler tekrar fotoğraflı kart yığınına dönüşmez.

### 6.4 Senin, Kaydedilenler ve Planım

Senin ekranındaki başlık ve kullanıcının adı sayfanın girişidir. Hafif bir sütun veya yükseliş izi yalnız bu girişte kullanılabilir. Gerçek plan özeti, Kaydedilenler ve haftanın izi birbirinden boşluk/çizgi ile ayrılır. Her bölüme yeni bir büyük kart ve farklı ikon zemini verilmez.

Haftanın günleri yalnız renk değişimiyle anlatılmaz; geldiği günlerde tik ve gün bilgisi bulunur. Günlere dokunma eylemi yoksa düğme gibi sunulmaz. Seri ve okunan söz sayıları başarı baskısı yaratacak kırmızı/sarı durumlara dönüşmez. Dar ekranda veya büyük yazıda üç istatistiği bir satıra sıkıştırmak yerine alt alta yerleşim kullanılır.

Kaydedilenler'de içerik gerçek metindir; kart önizleme resmi değildir. Boş olduğunda kısa bir açıklama ve geri dönüş yolu yeterlidir. Kaldırma, paylaşma ve okuma eylemleri karışmaz. Planım panelinde öncelikli konular, plana uygun açık havuz ve henüz kilitli öneriler ayrımı görünür kalır. Genel profil düzenlemesi, anlık ihtiyaç filtresiyle aynı isim veya ikonla sunulmaz.

### 6.5 Paylaşım stüdyosu

Araç çerçevesi yeni beyaz/mürekkep sistemine geçer. Önizleme; görsel seçimi, biçim, süre ve dışa aktarma işlemlerinden daha baskındır. Kütüphanedeki 107 hazır arka plan ve kendi fotoğrafı özelliği korunur. Mevcut gün doğumu, mavi gece ve şövalye eserlerinin kendi renkleri paylaşılabilir içeriğe aittir; bu renkler butona, Pro rozetine veya sayfa zeminine taşınmaz. Yasaklanan yeni arayüz renkleri ile önceden onaylanmış eser koleksiyonu bu yüzden ayrı ele alınır.

“Seçili” arka plan adı görünür kalır. Thumbnail üzerinde fotoğrafın koyuluğuna bağlı olarak kaybolmayan tik/çerçeve gerekir; gerektiğinde tik opak bir küçük zemin üstündedir. Pro erişimi renkli taçla değil okunabilir metin ve kilitle açıklanır. Üç ücretsiz seçeneğin aynı erişim hakları korunur.

Video seçildiğinde süre ve gelişmiş araçların kapsamı anlaşılırdır. Hazırlama durumunda işlem adı, ilerleme ve durdurma yolu görünür; yeniden dokunmak iki dosya üretmez. “Paylaş” sistem paylaşım ekranını açar; galeriye kaydet ayrı davranış olarak kalır. Çıktı üretimi başarısızsa eski bir önizleme yeni dosya üretilmiş gibi sunulmaz. Bu sürüm, tasarım değişikliği bahanesiyle PNG/MP4 motorunu değiştirmez.

### 6.6 Ayarlar, Pro ve bildirim yardım ekranları

Ayarlar başlık, grup etiketi ve satırdan oluşur. Tema örnekleri aynı nötr zeminde Mermer, Mürekkep ve Bordo gösterir; Kum, Lacivert ve eski Yosun seçimleri Mermer'e çözülür. Sistem dinamik rengi kapatılır. Aydınlık/karanlık tercihleri görünür ve ayrı anlamdadır: vurgu seçimi başka, açık/koyu okuma zemini başkadır.

Pro paneli bir başka uygulamanın satış sayfasına dönüşmez. Mermer başlık alanı ve sade hak listesi kullanır. “Ödeme alınmaz. Abonelik başlatılmaz.” bilgisi düşük kontrastlı dipnot olamaz. Açma ve kapama aynı görünür açıklığı korur. Açıkça belirtilen tarayıcı demo akışında Google'a gidildiği ve bunun gerçek reklam olmadığı anlaşılır kalır; kullanıcı geri geldiğinde yalnız yetkilendirilmiş konu açılır.

Bildirim izni, sistem ayarları ve Samsung görünüm rehberi aynı çizgi/başlık ailesini kullanır. Önizleme “gerçek cihaz ekranının birebir görünümü” diye sunulmaz. Özellikle uzun cihaz adımlarının alt butonların arkasında kalmaması gerekir. Android'in sistem izin penceresinin veya haricî tarayıcının kendi renkleri uygulama temasıyla zorla değiştirilmez.

## 7. Ölçüler, yazı ve görsel katmanların birlikte çalışması

### 7.1 Tipografi ve boşluk ölçeği

Aşağıdaki değerler tasarımın başlangıç aralıklarıdır. Nihai ekran genişliği ve font metriği kontrol edilerek uygulanır; her başlık bu sayılara kör biçimde kilitlenmez.

| Kullanım | Önerilen rol | Başlangıç ölçüsü |
|---|---|---|
| Ana söz | Lora serif, normal ağırlık, sola hizalı | 32 sp / 40 sp; uzun ve dar düzen için kontrollü varyant |
| Onboarding/sayfa başlığı | Aynı serif ailesi | 28–32 sp / 36–40 sp |
| Marka sözcüğü | Serif, normal ağırlık | 24–28 sp; yapay harf aralığı yok |
| Gövde/yanıt seçeneği | Sans serif | 15–16 sp / 22–24 sp |
| Konu ve kaynak | Sans serif | 12–13 sp / 17–19 sp |
| İşlevli küçük etiket/rozet | Sans serif, orta ağırlık | Tercihen 11–12 sp; bilgi 9 sp süs yazısına bırakılmaz |
| Ana buton | Sans serif, orta ağırlık | 14–16 sp; minimum yükseklik 52–56 dp |
| Alt gezinme etiketi | Sans serif | 11–12 sp / 14–16 sp; erişilebilir ölçek korunur |

Kenar boşluğu kompakt telefonda 24 dp, dar veya çok büyük yazı düzeninde gerekirse 20 dp; soru içeriklerinde 24–28 dp olabilir. Bileşen içi aralıklar 4/8/12/16 dp ailesinden seçilir. Bölümler arasında 24–32 dp kullanılır. Çizgi, metin ve ikon yatay eksenleri rastgele birkaç piksel farklı başlamaz. İçerik taşıyan kontrol köşelerinde 16–20 dp; ana eylem kapsüllerinde daha yuvarlak uçlar kullanılabilir. Her köşe yarıçapının farklı olduğu bir ekran yapılmaz.

Çok uzun bir alıntının yedi satıra çıkması hata değildir. Son satırın gereksiz yere tek sözcük kalması mümkün olduğunca gerçek cihaz genişliğinde gözle incelenir; katalog içine zorunlu satır sonları eklenmez. Türkçe `İ/ı/ğ/ş` karakterleri ve İngilizce apostroflar fontta gerçek olarak görüntülenir. Fontlar uygulamaya gömülür; internet bağlantısı yokken metin başka bir aileye düşmemelidir. İleride yeni dil eklenmesi, mevcut Türkçe ve İngilizce görünümle doğrulanmış gibi sunulmaz.

### 7.2 Renk rolleri ve hesaplanan kontrast

Bir rengin “koyu” görünmesi yeterli karar ölçütü değildir. Aşağıdaki oranlar W3C'nin sRGB nispi parlaklık formülüyle hesaplanmıştır; gerçek cihaz görüntü ölçümü değildir. Gösterim için iki ondalığa yuvarlanmıştır; geçiş eşiği karşılaştırmaları yuvarlanmamış değer üzerinden yapılmalıdır.

| Ön plan | Zemin | Yaklaşık oran | Kullanım yorumu |
|---|---|---:|---|
| Mürekkep `#161916` | Beyaz `#FFFFFF` | 17,72:1 | Ana söz, başlık ve temel ikonlar için geniş pay |
| Mermer yeşili `#3E4B40` | Beyaz | 9,19:1 | Sınırlı vurgu metni; tersi beyaz buton etiketi için de aynı oran |
| Bordo `#643B48` | Beyaz | 9,29:1 | İsteğe bağlı vurgu; tersi beyaz etiket için güçlü |
| İkincil metin `#484E49` | Beyaz | 8,53:1 | Uygulanan açıklama/kaynak rolü; ilk öneriden daha koyu |
| İşlevli sınır `#777E77` | Beyaz | 4,17:1 | Kontrol tanımlama çizgisi; küçük gövde metni olarak kullanılmaz |
| Dekoratif ayraç `#D4D8D4` | Beyaz | 1,44:1 | Yalnız süs/ayraç; seçim işaretinin tek göstergesi olamaz |
| Mürekkep `#161916` | İki katmanın ihtiyatlı 0,697225 kanal tabanı | 8,34:1 | Ana sözde figür örtüşmesi için geniş pay |
| Mermer yeşili `#3E4B40` | Aynı ihtiyatlı alt sınır | 4,32:1 | Küçük vurgu metni için opak zemin/ayrışma korunur |
| İkincil metin `#484E49` | Aynı ihtiyatlı alt sınır | 4,01:1 | Yalnız iki katman sınırına güvenilmez; opak okuma zemini kullanılır |
| İkincil metin `#484E49` | İki en koyu dönüştürülmüş pikselin tam alfa bileşimi, kanal 0,76075 | 4,79:1 | Hesaplanan daha sıkı sınır; opak koruma yine korunur |
| Karanlık ana metin `#F5F6F5` | Kömür `#111312` | 17,22:1 | Karanlık ana metin |
| Karanlık ikincil `#C1C7C1` | Kömür | 10,84:1 | Karanlık açıklama/kaynak |
| Aynı karanlık ikincil | Kömür üstünde iki beyaz 0,07 alfa katmanı | 7,40:1 | En parlak dekor varsayımında hesaplanan değer |

Buradan çıkan uygulama kararı: mürekkep ana söz, kontrollü figürün üzerinde kalabilir; gri kategori/kaynak, alt etiket ve ikonlar dekorun yoğun kesişimlerine bırakılmaz. Ana ekrandaki ikincil etiketlerin arkasında opak okuma zemini kullanılır. Aydınlık görselde her kanalın `0,3x + 0,7` dönüşümü ve alfa sınırı birlikte çalışır. İki en koyu dönüştürülmüş pikselin 0,55 alfa ile beyaz üstünde tam bileşimi `0,7×0,55 + (0,7×0,55 + 1×0,45)×0,45 = 0,76075` olur. Sözleşmede kullanılan `0,835² = 0,697225` bundan daha ihtiyatlı bir alt sınırdır; iki sayı aynı hesapmış gibi sunulmaz. Gerçek dosyanın alfa ve kenar davranışı ile gerçek yerleşim, ekran kontrolünde ayrıca değerlendirilir.

Renk kısıtı şu seviyelerde denetlenir: uygulamanın kendi zeminleri, butonları, seçim göstergeleri, bağlantı etiketleri, rozetleri, açılış ekranı, widget çerçevesi ve bildirim simgesi. Eski palet sabitleri veya Material varsayılan mor/mavi renkleri yeni bileşenlerden sızmamalıdır. Hata anlamında sınırlı bordo/kırmızı kullanılabilir, ancak hata yalnız renkle anlatılmaz. “Pro” için sarı/altın, bilgi için mavi, başarı için turuncu kodu kullanılmaz.

### 7.3 Mermer figürün yerleşim ve yoğunluk kuralları

Figürler sayfa içeriğinin parçası gibi tıklanmaz; büst yalnız bir dekor katmanıdır. Görüntü dosyasına metin, işlev ikonu veya logo gömülmez. Böylece dil değişimi, büyük yazı ve TalkBack metni ayrı yönetir. Görsel sayfa kaydığında ekran üstünde iz bırakan bir parallax katmanı değildir.

Ana ekranda büst sağ üstten girer; genişliğin yaklaşık son yarısı ile kenar taşmasını kullanır. Göz ve saçın en kontrastlı bölgeleri mümkünse sözün yoğun bölümünden uzak tutulur. Sütun ve yaprak sol alttan girer, fakat alt eylemlerin erişilebilir alanını veya sistem gezinmesini kapsamaz. Her görselin en beyaz bölgesi siyah kenarlıkla kesilmiş dikdörtgen iz bırakmadan zemine karışır.

Karşılama ve plan önizlemesinde daha geniş tek bir klasik öğe kullanılabilir. Tercih sorularında, ayarlarda ve çok metinli yardım ekranlarında dekor daha az görünür ya da tamamen kaldırılır. Aynı büstün her ekranda aynı boyda tekrarı, kimlik yerine duvar kâğıdı etkisi yaratır. Sistemi birleştiren asıl parçalar beyaz zemin, serif başlık, ince çizgi ve ölçülü boşluktur.

Karanlık mod bir görsel negatif değildir. Nötr kömür zeminde figür görünürlüğü daha düşük tutulur; beyaz büst büyük, parlak bir lekeye dönüşmez. Açık renkte hazırlanmış kenarlar karanlıkta dikdörtgen veya hale bırakıyorsa dosya/kompozisyon düzeltilir. Sözün tamamı bir ekranda geçiyorsa bile kaynak satırının figür üzerinde silinmediği ayrıca kontrol edilir.

Uygulamaya alınan üç dekoratif dosya; anonim Roma büstü, defne/sütun parçası ve yükselen basamaklı mermer kemerdir. Yerleşik görüntü üretim aracıyla bu ürün için oluşturulmuş özgün nötr kompozisyonlardır; müze koleksiyonundan alınmış eser veya belirli bir tarihî kişinin portresi olarak sunulmazlar. Üretim istemleri ve dağıtım dosyalarının kaydı [görsel köken dosyasında](../content/classical-art-provenance.json) bulunur. Özgün alfa korunmuş, dağıtım için WebP kalite 87 dönüşümü yapılmıştır. Bu kayıt görsel kökenini açıklar; uygulama içi kontrast ve yerleşim doğrulamasının yerine geçmez.

## 8. Mikro etkileşim ve erişilebilir durum sözleşmesi

Android'in gezinme rehberi üç ile beş eş düzey ana durak ve seyrek kullanılan işlemler için ek menü önerir. Ascend'in üç durağı korunur; daha fazla fotoğraf veya metin türü eklemek yeni ana sekme gerektirmez. Dinleme, kopyalama ve bildirim ayarları ana eylem grubunun Diğer menüsünde kalır. [Android — Layouts and navigation patterns](https://developer.android.com/design/ui/mobile/guides/layout-and-content/layout-and-nav-patterns)

| Etkileşim | Görsel cevap | Erişilebilir karşılık |
|---|---|---|
| Tek yanıt seçme | Belirgin kontur + tik + hafif nötr dolgu | RadioButton rolü, seçili durum |
| Çoklu yanıt | Aynı ailede tik/dolgu | Checkbox rolü, her yanıtın açık seçili bilgisi |
| Alt sekme | Kısa üst çizgi, koyulaşan ikon, orta ağırlık etiket | Tab rolü; Kaydedilenler açıkken Senin seçili |
| Kaydet | Kalbin dolması; gerekirse kısa geri bildirim | Kaydet/kayıtlardan çıkar açıklaması duruma göre değişir |
| İhtiyaç seçme | Panelde tek işaret; ana ekranda açık etiket | Geçici akış etkisi açıklaması; genel planı değiştirmez |
| Kilitli araç | Kilit/Pro etiketi; seçim öncesi erişim bilgisi | Erişim durumu söylenir; gizli ve çalışmayan buton görünümü yok |
| Metin kopyalama | Kısa, çakışmayan bilgi mesajı | Ekran okuyucunun algılayacağı geri bildirim |
| Kaydetme/hazırlama | Aynı alanda işlem durumu | Tek işlem, yeniden tıklama engeli; hata ve iptal yolu |
| Klavye açılması | Alt eylem klavyenin üstüne yerleşir | Metin alanı etiketi, açık klavye eylemi, kaybolmayan gezinme |

Buton basılı durumu sayfanın tamamını parlatmaz. Seçili çizginin animasyonu yaklaşık 120–180 ms, soru geçişi yaklaşık 160–200 ms aralığında olabilir; süreler bir kaynak zorunluluğu değil bu ürünün hareket bütçesidir. Sonsuz soluk alıp verme, mermerde zoom, parallax, konfeti, sürekli akan parıltı ve kullanıcıyı bekleten süslü “plan oluşturuluyor” sahnesi eklenmez.

W3C'nin AAA düzeyindeki etkileşim animasyonu açıklaması, gereksiz hareketin devre dışı bırakılabilmesini ve hareket tercihlerinin gözetilmesini destekler. Ascend bunu bütün WCAG AAA uyumluluğu iddiası olarak kullanmaz; dekoru sabit tutmak ve sistem tercihini izlemek için gerekçe olarak alır. [W3C — Animation from Interactions](https://www.w3.org/WAI/WCAG22/Understanding/animation-from-interactions.html)

Compose `MotionDurationScale`, animasyon süresini bir çarpanla yönetir; sıfır değerinde hareket bir sonraki karede sonlanır. Varsayılan Compose geçişleri bu platform davranışını bozacak bağımsız zamanlayıcılarla yeniden yazılmaz. Özel çizim hareketi eklenirse aynı tercih dikkate alınır. Dokunsal geri bildirim mevcut kapatma tercihine uyar; her sayfa açılışında titreşim kullanılmaz. [Android — MotionDurationScale](https://developer.android.com/reference/kotlin/androidx/compose/ui/MotionDurationScale)

## 9. Veri geçişi ve uygulama mimarisi

V8 bu aşamada sunum katmanına odaklanır. `PersonalPlan` yanıt modeli, soru sayısı, erişim ve dışlama kuralları sabit kalır. Metinlere görsel görünüm için yeni satır sonu, zorunlu boşluk veya rastgele vurgu bilgisi eklenmez. İngilizce kaynak ile Türkçe çevirinin sabit kimlik bağı korunur; serif uygulamak için cümle tekrar yazılmaz.

Tema değişimi tek merkezde çözülür. Beyaz/karanlık omurga ve vurgu ailesi ayrılır; sayfalar kendi özel mavi/gold sabitlerini tanımlamaz. Eski Kum/Lacivert/Yosun tercihi güvenli Mermer seçimine taşınır; açıkça seçilmiş Mürekkep ve Bordo korunur. Mevcut tema modu korunurken yalnız tema kaydı bulunmayan kurulum aydınlık başlar. Sistem paletinin eski açık kaydı yeni yasak renkleri döndüremez; dinamik renk tercihi veri uyumluluğu için tanınsa bile görünümü belirlemez. Geçiş bir kere uygulanır; sonraki açılışlarda kullanıcının son tercihini sıfırlamaz.

Paylaşım eserlerinin rengi tema modeliyle değiştirilmez; önizleme/dışa aktarma ayarları ve üç ücretsiz arka plan sabit kalır. Ana ekran dekoru paylaşım dosyasına otomatik eklenmez. Ana arayüzde mermer görmek ile dışa aktarılacak görsel seçmek iki ayrı işlemdir. İkon, font ve dekor dosyaları uygulamanın içine paketlenir; çevrimdışı açılışta tasarım eksik kalmaz.

Ortak bileşenler, sayfa başlığı, ince bölüm çizgisi, seçilebilir satır, küçük erişim rozeti ve dekor katmanını yönetebilir. Ancak bir ekranın kişiselleştirme veya erişim kararını ortak görsel bileşene taşımak gerekmez. Görsel yeniden kullanım, veri kararlarını değiştirmez.

## 10. Tasarımın kabul ve doğrulama matrisi

Bu bölüm yapılacak kontrolün tanımıdır; “geçti” sonucu değildir. Son kaynak ile APK ve ekran kanıtının ilişkisi sürümün doğrulama kaydında ayrıca yazılmalıdır.

| Kontrol | Kabul ölçütü |
|---|---|
| Ana ekran, normal yazı | Planım, ihtiyaç, söz, kaydet/paylaş/diğer ve üç sekme; ana sayfa dikey kaydırılmadan erişilir |
| Ana ekran, dar cihaz ve yüzde 200 yazı | Hiçbir temel işlem kırpılmaz; söz okunabilir iç kaydırma kullanabilir; font ölçeği bastırılmaz |
| Dekor + uzun söz | Saç/göz/sütun çizgisi küçük metni kapatmaz; gerçek birleşik zemin kontrastı güvenli |
| Dekor + karanlık | Beyaz kenarlı kutu veya parlak büyük leke yok; metin ve ikincil kontroller okunur |
| Onboarding tek/çoklu seçim | Doğru rol, tik/çizgi, geri dönünce cevap, atlayınca doğru davranış |
| Onboarding isim/klavye | Devam ve Atla erişilir; uzun ad veya klavye alanı taşırmaz |
| Saatler ve izin | Büyük sayılar, seçili saat, görünür devam; sistem izni ve bildirimsiz yol çalışır |
| Plan düzenleme | V7 profil/erişim korunur; iptal mevcut planı değiştirmez |
| Tema geçişi | Eski Kum/Lacivert/Yosun ve dinamik kayıtlar yeni yasak renkleri üretmez; mevcut Mürekkep/Bordo ve tema modu korunur |
| Keşfet | Arama/filtre, açık/seçili/kilitli durum, doğru konu demosu; görsel kalabalık geri gelmez |
| Senin ve Kaydedilenler | Gerçek veriler korunur; büyük yazıda istatistikler ve geri yolu görünür |
| Pro/demolar | Altın veya satış baskısı yok; ödeme alınmadığı ve demo sınırı okunur; haklar değişmez |
| Paylaşım | Ücretsiz üç arka plan ve Pro kapıları; seçili eser adı; gerçek PNG ve mevcut MP4 süreleri çalışır |
| Sistem çevresi | Açılış, durum/gezinti ikonları ve widget çerçevesi yeni tema ile uyumlu |
| Hareket kapalı | Soru/sekme/kaydet işlemleri hareket beklemeden tamamlanır; sürekli dekor hareketi yok |

Kod ve kontrast kontrolleri, ardından GitHub Actions üzerinde APK üretimi ve ilgili Android akışları çalıştırılır. Emülatör görüntülerinde en az karşılama, bir tek seçim, plan önizleme, ana ekran, Keşfet, Senin, Pro ve paylaşım incelenir; büyük yazı ve karanlık varyantlar ayrıca örneklenir. Başarılı APK üretimi fiziksel Samsung ekranının, TalkBack ile elle kullanımın veya mağaza hazırlığının tamamlandığı iddiasına dönüştürülmez.

Kabul için her küçük gölgeyi tekrar tekrar değiştiren sınırsız bir tasarım turu gerekmez. Ürün davranışını bozan taşma, kontrast, renk kaçağı veya belirsiz seçim varsa düzeltilir. Gerekli kontrolleri geçen yeni APK somut olarak teslim edilir; sonraki cihaz geri bildirimleri bir sonraki iyileştirme turunun verisi olur.
