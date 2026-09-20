# Ascend — görsel kimlik, görünür Pro değeri ve 10 günlük gelişim planı

Hazırlanma: 20 Eylül 2026. Başlangıç sürümü: 9.27 çalışması. Süreler iş günü önerisidir; otomatik çalışma takvimi veya tamamlanma garantisi değildir.

## 1. Ana karar

Ascend’in en büyük açığı özellik sayısı değil, **birbiriyle ilişkili özelliklerin tek bir değer olarak deneyimlenmemesi**. Kullanıcı güzel bir tema görüyor; bunun telefonunda, bildirimlerinde ve günlük okumasında nasıl bir bütün oluşturduğunu kendisi keşfetmek zorunda kalıyor. Pro’nun görünürlüğünü artırmak, her ekrana daha büyük bir satış düğmesi koymakla aynı şey değil.

Önerilen ürün cümlesi: **“Kendi disiplinine dön. Her gün bir söz, bir küçük adım; sana ait bir atmosfer.”** Bu bir konumlandırma önerisidir. Kullanıcı araştırmasıyla doğrulanmış bir satış sonucu değildir.

Üç katman aynı dili konuşmalı:

1. **Duygu:** antik arena, taş, metal, sessiz güç; tutarlı ve ayırt edilebilir sanat.
2. **İş:** doğru zamanda gelen iyi içerik, kolay kaydetme, devam edilebilir kısa seri.
3. **Değer:** ücretsiz çekirdek işlev; Pro’da daha derin içerik ve aynı dünyayı farklı yüzeylerde kullanma.

Kullanıcı “daha çok kilit açıyorum” yerine “seçtiğim dünyayı kullanıyorum ve her gün anlamlı bir devam buluyorum” diyebilmeli. Bu hedef, tek başına duvar kâğıdıyla çözülmez. Duvar kâğıdı mevcut güzel sanatın eksik kullanım alanını tamamlar; ana farklılaştırıcı için özel serinin niteliği ve devam deneyimi üzerinde durmak gerekir.

## 2. Bu rapor neye dayanıyor?

- Kullanıcının son geri bildirimi ve önceki görsel tercihleri.
- Güncel `EditorialVisuals`, `KategoriGorseli`, `Atmosfer`, `KesifMerkezi`, `LivingCollection`, `ProEkrani`, `Onboarding`, `WallpaperService` ve ilgili testlerin okunması.
- 20 Eylül kapsamlı denetimi: `Ascend-Denetim-2026-09-20/02-Kapsamli-Inceleme.md`.
- Faz 1–2 ve Faz 3–4 teslim notları. Eski rapordaki kapanmış sorunlar otomatik olarak yeniden “mevcut hata” sayılmadı.
- Android’in resmi WallpaperManager belgesi; son sürümün test sonucu ayrı teslim notunda tutulur.

Bu rapor gerçek kullanıcı görüşmesi, gelir analizi veya bütün cihazlarda yapılan saha deneyi değildir. Kullanıcı kaybı ve Pro dönüşümü hakkındaki ifadeler test edilecek hipotezlerdir. Yerel Pro demosu açılışları gerçek satın alma değildir.

## 3. Bu tur uygulananlar ve sonraya bırakılanlar

### 3.1 Uygulanan kapsam

- Keşfet grup kapakları savaşçı/antik atmosferle eşleştirildi. Eski çay, ofis ve gündelik yaşam kapakları bu girişlerde artık kullanılmıyor.
- Kaydedilen görseli ve kategori görseli seçim yolları aynı editoryal eşlemeye bağlandı. Ana tema seçiminin bütün kütüphaneye aynı resmi basması önlendi.
- Dört kısa serinin kapakları birbirinden farklı tutuldu.
- Görünüm’e üçüncü **Duvar kâğıdı** sekmesi eklendi.
- Görsel seçimi, yazısız önizleme, ana ekran/kilit ekranı/ikisi seçimi ve uygulama eylemi eklendi.
- Uygulama işlemi özgün resim akışını kullanır; küçük galeri görseli, yazı ve ekstra karartma çıktıya eklenmez. Cihaz oranına göre odaklı kırpma yapılır.
- Android desteği, cihaz kısıtlaması, Pro erişimi ve işlem sonucu kontrol edilir; başarısızlık “uygulandı” diye gösterilmez.
- Roma ve Atlı Yolcu duvar kâğıtları ücretsiz; diğer sanat görselleri Pro. Mevcut ücretsiz ana temalar korunur.
- Pro teklifi duvar kâğıdına özgü seçimi gösterir. Demo açılması tek başına telefonun duvar kâğıdını değiştirmez; kullanıcı Uygula’ya basar.
- Hareketli koleksiyondaki eski duvar kâğıdı önizlemesi çalışan uygulama ekranına bağlandı.
- Yeni metinler mevcut yedi dilde tanımlandı.
- Sistem duvar kâğıdı uygulamanın ana temasından bağımsızdır. Pro demosunu kapatmak daha önce uygulanmış telefon duvar kâğıdını geri almaz; uygulama kullanıcının sonradan yaptığı sistem seçimine müdahale etmez.

### 3.2 Henüz uygulanmış sayılmayanlar

Aşağıdaki 10 günlük plan, bu APK’nın içinde tamamlanmış özellik listesi değildir. Onboarding’in baştan görsel tasarımı, bütün Pro metinlerinin yeni satış düzeni, yeni özel seri üretimi, gerçek ödeme, fiziksel cihaz performans ölçümü ve mağaza yayını bu tur bitti diye sunulmaz.

### 3.3 Önceki denetimle durum ilişkisi

| Önceki başlık | Güncel yaklaşım |
|---|---|
| O-01: devam düğmesi erişimi | Önceki fazda sabit alt eylem düzeni kuruldu. Yeniden tasarım bunu korumalı. |
| O-11: gerçek olmayan üç gün vaadi | Önceki fazda Pro demo açıklamasıyla düzeltildi; geri getirilmemeli. |
| G-07: duvar kâğıdı yalnız prototip | Bu tur gerçek uygulama akışı eklendi; cihaz farklılıkları ayrıca denenmeli. |
| G-08: teklif sonrası seçim kaybı | Önceki devam davranışları korunur; duvar kâğıdında seçim ve hedef kaybolmaz. |
| W-01: widget içerik tercihleri | Önceki fazın filtre güvenliği korunur. Yeni görseller içerik tercihini değiştirmemeli. |
| S-02/S-03: ilk fayda ve yedi günlük yön | Önceki seri düzeni üzerine kalite/marka çalışması yapılmalı; aynı işi tekrar icat etmemeli. |
| A-01: kaldırmada geri alma | Mevcut geri alma korunmalı. |
| A-08/A-09: yardım ve veri açıklaması | Mevcut yardım/yedekleme açıklaması korunmalı. |
| Görsel kimlikte çay/savaş karışımı | Bu tur ortak eşleme düzeltildi; sonraki sanat üretiminin kabul ölçütleri aşağıda. |

## 4. Görsel sanat yönetimi

### 4.1 Tek dünya, farklı anlam

“Gladyatör teması” her kategorinin dövüş göstermesi demek olmamalı. Kullanıcının aradığı düşünce görselin davranışında okunmalı. Aynı dünyayı kostüm, malzeme, ışık ve mekân kurar; her kartta kılıç göstermek gerekmez.

| Konu | Bu tur seçilen görsel yön | Gelecekte özel kapak üretiminde aranacak sahne | Kaçınılacak kullanım |
|---|---|---|---|
| Olumlamalar | Taht / sakin güç | Arenaya çıkmadan önce dingin duran savaşçı | Her metne zafer/üstünlük dayatmak |
| Azim | Spartalı | Yoluna devam eden, yorgun ama ayakta figür | Yaralanmayı yüceltmek |
| Disiplin | Lejyon | Düzen, hazırlık, tekrarlanan eğitim | Dağınık büyük savaş kalabalığı |
| Cesaret | Arena şampiyonu | Eşikte duran figür, karar anı | Kanlı düello |
| Spor | Gladyatör | Antrenman, kavrama, duruş, hareket | Sadece tahtta oturan imparator |
| İnanç | Aydınlık antik taş mimari | Sessiz avlu, yukarıdan ışık, düşünme anı | Belirli dini bir savaşçıya mal etmek |
| Doğu geleneği | Sakin taş salon | Dönemi ve kültürü belli, saygılı düşünme sahnesi | İlgisiz Romalıyı tarihî kişinin portresi gibi sunmak |
| İş / liderlik | Atlı komutan | Harita, sorumluluk, karar hazırlığı | Gösterişli zenginlik = başarı mesajı |
| İlişkiler | Kalkan hattı | Dayanışma, koruma, yan yana duruş | Çatışma ve tahakküm |
| Zihin | Kıyı nöbeti | Savaş sonrası sessizlik, açık ufuk | Korkutucu karanlık / tehdit |
| Öğrenme | Bilgelik mekânı | Tablet, kütüphane, öğreticiyle çalışma | Modern kahve masası |
| Düşünürler | Antik bilgelik | Eser/okuma atmosferi | Tek heykeli bütün düşünürlerin gerçek yüzü gibi göstermek |

Mevcut sanatların kullanılması bu tur için yeterli bir bütünlük adımıdır; her kategoriye özgü yeni resim üretildiği iddia edilmez. Özellikle Doğu geleneği ile antik Akdeniz arasındaki kültürel farkı gelecekte ayrı kapakla daha iyi anlatmak gerekir. Fotoğraflar dekoratif sanat; kaynak eser veya tarihsel portre kanıtı değildir.

### 4.2 Görsel kabul ölçütleri

- Ana özne kartın yatay kırpmasında anlaşılır kalmalı; miğfer/yüz gelişigüzel kesilmemeli.
- Küçük kartta tek ana odak; arka planda en fazla bir destekleyici anlam.
- Koyu kömür, taş grisi, kirli gümüş; kontrollü sıcak bronz ışık. Bütün görsele beyaz/gri perde basılmamalı.
- Etiket resmin üstünde okunmuyorsa resim tümden karartılmak yerine etiket ayrı yüzeye alınmalı.
- Birbirine komşu kartların en az siluetleri farklı olmalı; aynı resim yalnız renk değiştirerek çoğaltılmamalı.
- Telefon oranı, kare widget ve yatay kapak için aynı koordinatın doğru olduğu varsayılmamalı.
- Liste görselleri boyuta uygun yüklenmeli; tam çözünürlük uygulama/dışa aktarma için saklanmalı.
- Soğuk ve sıcak ışık aynı koleksiyonda olabilir; modern yaşam fotoğrafı ile tarihî fantezi karışmamalı.

## 5. Onboarding için somut yeni yön

### 5.1 Hedef

Yeni kullanıcı ilk bir dakika içinde şu üç soruya yanıt bulmalı: “Bu uygulama bana ne sunuyor?”, “Bana ne zaman ulaşacak?”, “Benim telefonumda nasıl görünecek?” Bir dakikayı ürün kısıtı değil, kullanıcı deneyi hedefi olarak kullanmak gerekir. Hız uğruna izin ve ücretsiz/Pro farkı gizlenmez.

Mevcut sabit Devam düğmesi, taslak koruması, geri hareketi ve erişilebilirlik iyileştirmeleri tutulur. Yeniden düzenlenecek şey marka sunumu, içerik yoğunluğu ve sayfalar arasındaki görsel ritimdir.

### 5.2 Beş adım, beş ayrı iş

**1 — Dil ve ilk karşılaşma.** Küçük marka işareti; başlık “Kendi disiplinine dön.”; iki satırı geçmeyen açıklama. Üstte kısa, koyu bir arena eşiği detayı, ekranın tamamını kaplayan fotoğraf değil. Dil seçenekleri kendi adlarıyla görünür; seçilen dil belirgin. Yedi büyük kart yerine sade seçim listesi. Devam daima güvenli alt alanda. Dekoratif Aa/Merhaba kartı geri gelmez.

**2 — Ürünü bir kez kullan.** Gerçek örnek söz kartı; kullanıcı bir kez kaydırabilir ve kaydedebilir. Başlık “Bir söz. Yeniden başlamak için.” Metin örneği: “Bugün zorlanman, dün öğrendiklerini silmiyor.” Tek bir anlamlı etkileşim; favori yapmak zorunlu değil. Arka planda yalnız bu adıma ait sakin savaşçı sahnesi. Kartta etkileşim olunca küçük ve kısa geri bildirim; sürekli konfeti yok.

**3 — Gününün ritmi.** Bildirim sayısı ve saat aralığı aynı sayfada. Büyük saat illüstrasyonu, dev sayaç, tekrar eden saat chip’leri birlikte bulunmamalı. Sayı için kısa kontrol, zaman aralığı için iki açık satır; altında tek örnek bildirim. Örnek saatlerin yaklaşık olduğu açık ve küçük metinle belirtilir.

**4 — Bildirim izni.** Gerçek Android bildiriminin sade maketi. “Seçtiğin saatte sana ulaşalım.” Ana eylem Bildirimleri aç. Açılır bildirim/kanal ayar yardımı görünür ama ana iznin eşdeğeri gibi sunulmaz. Android kararını kullanıcı verir; reddedince neden ilerlenmediği ve ayarlara nasıl gidileceği anlaşılır kalır. Uygulama otomatik olarak izin veremez ve her cihazda açılır pencere garantisi vermez.

**5 — Görünüm seçimi.** Yeni antik/koyu dünyayı gösteren fotoğraflı seçenekler önce anlaşılır biçimde sunulur; ücretsiz Roma/Atlı Yolcu ile Pro sanatlar ayrılır. Beyaz/Siyah silinmez, ancak büyük kahraman görselleri kadar yer kaplamak zorunda değildir. Altı önizleme ve “Daha sonra Görünüm’den değiştirebilirsin.” Seçim ve uygulama farklı eylemler gibi hissettirilmez. Ücretsiz tercih, kullanıcının iradesi dışında Pro temaya dönüşmez.

**Pro sunumu:** İlk açılış sonunda uzun zorunlu satış sunumu yerine seçilen Pro sanat için bağlamsal teklif önerilir. Ücretsiz tema seçen kişi önce uygulamanın değerini deneyimleyebilir. Bu davranış değişikliği gerçek kullanıcı göreviyle değerlendirilmeli; henüz bu tur uygulanmış değildir.

### 5.3 Boyut ve hareket kuralları

- Ana eylem minimum 52–56 dp; metin büyüdüğünde yüksekliği artabilir, ekran dışına çıkamaz.
- Küçük ikon dokunma hedefi en az 48 dp; görünen ikon 20–24 dp olabilir.
- Başlık 26–30 sp aralığında; arayüz Inter, yalnız gerçek söz kartında Lora. Her başlık serif olmasın.
- Gövde 14–16 sp; yardım metni okunamayacak 9–10 sp’ye düşürülmesin.
- 16/24 dp dış boşluk, 8/12/16 dp iç ritim. Kalan alan büyük boş Spacer ile itilmesin.
- Fotoğraf yüksekliği ekran alanına göre uyarlansın; kısa telefon ve 2× yazıda küçülebilsin.
- Geçiş yaklaşık 180–280 ms; tek yönde, küçük mesafe. Hareket azaltma tercihi korunur.
- Soğuk başlangıçta fotoğraf yüklenmediğinde tasarım çökmemeli; uygun tonlu yer tutucu ve hata tekrar eylemi bulunmalı.

## 6. On günlük çalışma planı

Her günün sonunda küçük, incelenebilir bir çıktı olmalı. Kabul kapısı geçmeden sırf takvim ilerledi diye sonraki özellik eklenmemeli. Teknik risk çıkarsa son günlerdeki isteğe bağlı işler ötelenir; güvenilirlikten süre kazanılmaz.

### Gün 1 — Tasarım sözleşmesi ve gerçek başlangıç ölçüsü

**Amaç:** Bir sonraki turda tekrar farklı stillere savrulmayı önlemek.

- Güncel APK’dan onboarding, Keşfet, konu ayrıntısı, Görünüm, Pro, seri ve Kaydedilenler için referans kareler al.
- Yeni görsel eşlemenin yatay kartlardaki gerçek kırpmalarını incele. Sadece dosya adına göre karar verme.
- Koyu ana palet, açık alternatif, metin/ayırıcı/rozet/sekme/CTA ölçülerini tek tasarım notunda sabitle.
- Birincil marka yönünü yaz: “sessiz güç, kişisel disiplin, geri dönebilme”. İçerik dilini aşağılayıcı sertlikten ayır.
- Onboarding için beş ekranın küçük ve uzun telefon tel çerçevesini çiz.
- Değiştirilmemesi gerekenleri kayda geçir: ana ekranın güçlü kompozisyonu, ücretsiz sanatlar, kaydedilenler, erişilebilir CTA.

**Çıktı:** Onaylanabilir beş ekran taslağı + tasarım ölçü tablosu + önce/sonra kontrol listesi.

**Kabul:** Aynı buton her ekranda farklı yarıçap veya kontrastta değil; taslakta tek ana eylem var; yeni izin/hesap/ödeme altyapısı eklenmiyor.

### Gün 2 — Onboarding dil ve ilk değer ekranları

**Amaç:** İlk açılış artık eski açık katalog sayfası gibi görünmesin.

- 1. ve 2. adımı yeni koyu/metal kimliğe geçir.
- Dil seçimini sadeleştir; ekranı kaydırınca Devam sabit kalsın.
- İkinci adımda gerçek kaydırma/kaydetme davranışını koru; açıklama paragraflarını kısalt.
- Aynı savaşçı fotoğrafını iki adımda tekrarlama.
- Küçük ekran, klavye, geri dönüş ve yarım kurulumdan devamı kontrol et.

**Çıktı:** İlk iki adım çalışan kod ve TR/EN ekran kayıtları.

**Kabul:** Kullanıcı testinde “Bu ne işe yarıyor?” sorusu uzun açıklama okumadan yanıtlanabiliyor. 320/360/412 dp genişlikte CTA erişilir. 2× yazıda başlık ve seçim çakışmaz.

### Gün 3 — Ritim, izin ve tema adımlarını tamamla

**Amaç:** Görselliği korurken kurulum yükünü azaltmak.

- Ritim ekranında tek sayaç ve tek zaman aralığı göster.
- Bildirim maketini önceki tanıtım kartından farklı fakat aynı tasarım dilinde kur.
- İzin reddi, ayardan geri gelme ve zaten izinli durumunu ayrı metinlerle ele al.
- Tema adımında görsel önceliği düzenle; ücretsiz ve Pro ayrımını önden göster.
- İlk ücretsiz tema → ilk söz; Pro tema → ilgili önizleme → teklif → aynı seçime dönme yollarını doğrula.
- Yedi dilde kritik metinleri sabit anahtarlarla güncelle.

**Çıktı:** Tam onboarding, izin durum matrisi, yedi dil kısa düzen kontrolü.

**Kabul:** Her adımda tek ana eylem; hiçbir düğme sistem çubuğunun altında değil. Reddedilen izin başarılı kabul edilmiyor. Ücretsiz seçim sessizce ücretli görsele çevrilmiyor.

### Gün 4 — Pro teklifini somut sonuca indir

**Amaç:** Pro’yu daha fazla göstermeden daha iyi anlatmak.

- Teklifin ilk görünen alanında kullanıcının seçtiği sonuç: tema, duvar kâğıdı, widget veya seri günü.
- Başlık bağlama uygun olsun: “Bu atmosferi telefonuna taşı.” / “Yedi günlük serine devam et.”
- İlk alanda en fazla üç fayda; tüm özellik listesi ikincil açılır alan.
- “Demoyu aç” sonrasında ilgili işe devam edilsin; kapatınca seçim kaybolmasın.
- Teklif doğrudan kullanıcının Pro eyleminden açılsın. Ana sayfa ziyaretinde tekrar tekrar otomatik açılmasın.
- Duvar kâğıdında Pro açmak ile sistemde resmi uygulamak ayrı niyetler olarak korunsun.
- Ücretsiz kullanıcı haklarını kısa ve doğru söyle; sahte geri sayım/fiyat kullanılmasın.

**Çıktı:** Dört bağlamsal teklif örneği, erişim ve geri dönüş matrisi.

**Kabul:** Deneyen kişi “Bu düğmeye basarsam ne elde edeceğim?” sorusuna yanıt verebiliyor. Demo gerçek abonelik sanılmıyor. Kapatma ve ücretsiz devam açık.

### Gün 5 — Keşfet’in bilgi ve sanat dengesini düzelt

**Amaç:** Güzel resimli ama anlaşılmaz bir katalog oluşmasını önlemek.

- Grup kartında tek başlık ve tek kısa amaç; gereksiz slogan/count/rozet yığını yok.
- Pro işaretini görselin yüksek kontrastlı sabit köşesinde kullan; başlığı okunmaz biçimde silikleştirme.
- Düşünürleri ortak bölümde tut; alt listede isim + kısa dönem/alan bilgisi yeterli.
- Alt kategori ekranında başlık/bildirim anahtarı kompakt; sözler ilk ekranın çoğunu kullanabilsin.
- Arama kapsamını açık tut: arama yazınca genel katalog mu grup içi mi, etiket aynı şeyi söylemeli.
- Kaydedilen kapağının konuya uygunluğu ve ana temadan bağımsızlığını kontrol et.

**Çıktı:** Keşfet kökü, bir grup, bir düşünür listesi, bir konu ayrıntısı ve boş arama tasarımı.

**Kabul:** Kullanıcı bir konuyu en fazla kök → grup → konu yolu ile bulabiliyor. Geri dönünce liste konumu korunuyor. Görsel ile konu adı çatışmıyor.

### Gün 6 — Görünüm’ü tamamlanmış bir koleksiyon deneyimine dönüştür

**Amaç:** Tema/widget/duvar kâğıdı ilişkisinin kullanıcıya görünmesi.

- Üç sekmenin isimleri uzun dillerde ve büyük yazıda okunur kalsın; seçili durum yalnız renk farkı olmasın.
- Aynı sanatın üç kullanımını bir koleksiyon ayrıntısında göster, fakat üçünü birden uygulamayı zorunlu tutma.
- Widget girişindeki gereksiz çift önizlemeyi azalt: doğrudan arka plan + kare/geniş + ekle.
- Duvar kâğıdında orijinal görsel, hedef ve başarı/hata geri bildirimi korunur.
- Ana ekran, kilit ekranı ve her ikisi Android/başlatıcı farklarıyla denenir.
- Animasyon yalnız görünen sahnede çalışsın; wallpaper canlı diye pazarlanmasın, şimdilik statiktir.

**Çıktı:** Tek sanatla üç kullanım görevi, düşük bellekli cihaz için görsel yükleme ölçüsü.

**Kabul:** Kullanıcı tema değiştirme ile sistem duvar kâğıdı değiştirmeyi karıştırmıyor. Yüklenirken donma yok. Pinleme isteği widget eklendi diye raporlanmıyor. Kaynak görselden daha fazla çözünürlük vaat edilmiyor.

### Gün 7 — Pro’nun günlük geri dönüş sebebini güçlendir

**Amaç:** Bir kere tema seçip unutulan ürün olmamak.

- Mevcut özel seriyi bölüm bölüm değerlendir; her gün farklı bir düşünce ve küçük, isteğe bağlı eylem taşımalı.
- Önerilen sonraki seri: “Disipline dönüş”. Yeni seri yapımı yalnız mevcut serinin kalite kapısından sonra.
- Gün taslağı: başlangıcı küçült; çevreni hazırla; dikkat sınırı koy; isteksiz günde minimum adım; aksayınca yeniden başla; dinlenmeyi planla; sürdürülebilir ritim seç.
- Bunlar final içerik değildir: doğal dil, tekrar, hassas durum ve çeviri denetimi gerekir.
- Kapak savaşçı dünyasından; metin bağıran komutlar yerine somut durumlar anlatsın.
- Ücretsiz ilk gün tek başına değer taşısın. Pro devamında gerçek yeni içerik bulunsun.
- Ana ekranda varsa aktif seri için tek sakin devam noktası; ikinci dashboard ekleme.

**Çıktı:** Bir serinin yedi günlük editoryal akışı, ilk gün önizlemesi, ertesi gün devam görevi.

**Kabul:** Hiçbir gün diğerinin yeniden yazımı değil. Tamamlama iki kere artmıyor. Bir gün kaçırınca kullanıcı cezalandırılmıyor. Pro bitince ilerleme silinmiyor.

### Gün 8 — Kaydedilenler, Geçmiş ve paylaşımı incelt

**Amaç:** Günlük kullanımda sürtünmeyi azaltmak.

- Az kayıt varken arama alanını küçült veya isteğe bağlı aç; mevcut kayıt sırasını netleştir.
- Kaydı silmede geri alma ve paylaşımdan geri dönüş konumunu koru.
- Geçmişte boş durum açıklaması ve işe yarar bir bağlantı bulunmalı; teslim edildi ile okundu ayrılmalı.
- Paylaşımda Görsel/Video seçimi açık, eylemler güvenli alt alanda, dışa aktarım durumu görünür.
- Kullanıcıdan font, hizalama, karartma gibi gereksiz editör kararları isteme.
- İptal edilen dışa aktarma ve paylaşım uygulamasından dönüşü dene.

**Çıktı:** Kaydet → bul → paylaş → dön görevi; boş ve hata durumlarının kısa metinleri.

**Kabul:** Paylaş düğmesi navigasyon altında kalmıyor. İşlem sürerken ikinci işlem başlamıyor. Galeriye kaydetme başarısı dosya gerçekten oluşunca gösteriliyor.

### Gün 9 — Dil, erişilebilirlik ve performans kapısı

**Amaç:** Tasarımın yalnız bir ekran görüntüsünde iyi görünmesini engellemek.

- TR/EN/PT/DE/FR/IT/RU için temel akışların metin taşması ve eksik anahtar kontrolü.
- 1×, 1.3×, 2× yazı; kısa/uzun telefon; açık/koyu; üç düğmeli ve hareketli sistem navigasyonu.
- Ekran okuyucuda seçili sekme, düğme amacı ve görselin dekoratif/işlevsel ayrımı.
- Düşük bellekli cihazda uzun galeri kaydırma, modal aç/kapat, arka plana git/dön, video üretimi.
- Uzun bildirim teslimatı için daha önce tanımlanan fiziksel cihaz senaryolarını ayrı çalıştır.
- Ana dili konuşan editör olmadan bütün çevirilere doğal dil onayı verme.

**Çıktı:** Öncelikli hata listesi; ölçülen performans tablosu; kalan cihaz/dil kapıları.

**Kabul:** Kritik eylem erişilemezliği sıfır. Yeni derlemede çökme/ANR varsa yayın yok. Performans iyileşmesi ölçümle yazılır; “akıcı oldu” varsayımıyla değil.

### Gün 10 — Kısa kullanıcı görevleri, son karar ve teslim

**Amaç:** Çalışan paketi teslim etmek ve bir sonraki kapsamı veriye göre seçmek.

- Mümkünse 3–5 kişiye yardım etmeden görev ver: dil seç, ilk sözü kaydet, bildirim konusu değiştir, ücretsiz wallpaper uygula, bir Pro sanatın ne sunduğunu anlat, serine dön.
- Görev tamamlama, duraksama, yanlış dokunma ve kullanıcının kendi açıklamasını kaydet; küçük örneklemden pazar genellemesi yapma.
- En yüksek etkili sorunları düzelt; yeni özellik ekleme.
- Kaynak commit, test sonucu, APK ve kısa sürüm notunu eşleştir.
- Gerçek Play Billing’e geçiş için gereken ayrı iş listesini çıkar; bu 10 günün örtülü kapsamı yapma.

**Çıktı:** APK, kısa değişiklik listesi, kanıtlı açık kapılar ve sonraki sprint için en fazla üç öncelik.

**Kabul:** Özellik vaadi ile gerçek davranış tutarlı. Açık kritik sorun yok. Kullanıcı Pro’nun en az bir somut sonucunu kendi cümlesiyle söyleyebiliyor. Bu başarı ölçütü satış garantisi değil, anlaşılabilirlik kapısıdır.

## 7. Pro değerinin görünür olduğu yerler

| Kullanıcının niyeti | Gösterilecek kanıt | Teklif anı | Gösterilmeyecek şey |
|---|---|---|---|
| Bir sanat beğenmek | Tam boy, gerçek kırpmalı önizleme | Pro görseli kullanma isteği | Uygulama açılır açılmaz aynı satış penceresi |
| Telefonu aynı atmosfere taşımak | Widget ve wallpaper’ın ayrı gerçek sonuçları | Pro arka planı uygulama/ekleme | Çalışmayan prototipi hazır özellik gibi anlatmak |
| Bir konuya derinleşmek | O konudan anlamlı önizleme | Pro konuya bildirim/okuma erişimi | Sadece kilit ikonu ve belirsiz “daha fazla” |
| Seriyi sürdürmek | İlk günün gerçek faydası + sonraki gün başlığı | Devam etmek istediği an | Bütün gelecek günleri boş vaatle satmak |
| Paylaşmak | Gerçek video/görsel önizlemesi | Ücretli biçimi üretme isteği | Ücretsiz kaydetme hakkını sonradan kapatmak |

Pro’nun görsel paketi için kısa öneri: **“Aynı dünyayı her ekranda yaşa.”** İçerik paketi için: **“Bir sözü oku; yedi gün boyunca kendi adımını bul.”** İkisinin birlikte anlatılması ürünün iki gerçek değerini birleştirir. Her yerde ikisini birden uzun uzun açıklamak gerekmez.

## 8. Şimdi eklemeyeceklerim ve kaldıracaklarım

**Eklemeyeceklerim:** sosyal akış, takipçi, AI sohbet koçu, puan/lig, günlük zorunlu duygu formu, yeni alt navigasyon, onlarca yeni dil, kapsamlı paylaşım editörü, aynı anda çok sayıda hareketli koleksiyon. Bunlar şu anki temel sorunu çözmeden bakım ve karar yükü ekler.

**Kaldırılacak/azaltılacaklar:** aynı anlamı iki defa anlatan açıklamalar; yinelenen bildirim ayarı girişleri; ilgisiz stok fotoğraflar; bütün karta basılan gri perde; sonuç göstermeyen Pro listeleri; henüz çalışmayan özelliklerin CTA’ları; her seride aynı uzun tanıtım blokları.

**Korunacaklar:** dört ana bölüm, ana ekranın okuma alanı, güçlü fotoğraf galerisi, ücretsiz bildirim çekirdeği, kullanıcı kayıtları, geri alma, eski veri uyumu, iki ücretsiz sanat, yedi dil, sade widget kurulumu.

## 9. Ölçüm: neyin işe yaradığını nasıl anlayacağız?

Şimdilik yerel kayıt ve gönüllü görev gözlemi yeterlidir. Bu plan yeni bir analiz sunucusuna kişisel veri göndermeyi içermez.

- İlk değer: ilk anlamlı söz kaydetme veya tanıtım kartıyla etkileşim.
- Kurulum: başlayanların bitirme durumu, hangi adımda durduğu; bunu ölçmeden terk oranı uydurulmaz.
- Pro: teklif görüntüleme → demo açma → ilk amaçlanan işlem. Üç ayrı olay.
- Wallpaper: uygulama denemesi → Android başarı sonucu; preview açma başarı sayılmaz.
- Widget: ekleme isteği → başlatıcının onayı; farkı korunur.
- Seri: ilk gün → ikinci gün geri dönüş → yedinci gün; kullanıcının baskıyla günlük tik vermesi hedef değil.
- Bildirim: planlandı/gönderildi/uygulamada açıldı ayrımı; işletim sistemi teslimatı ve insanın okuması aynı değil.
- Teknik: soğuk galeri yüklenmesi, kaydırma sırasında kare gecikmesi, bellek tepe değeri, dışa aktarım başarısızlığı.

Önce mevcut sürümün ölçüsü alınmalı; hedef yüzdeler bunun üzerine kurulmalı. Yerel demo verisinden abonelik geliri veya satın alma isteği sonucu çıkarılamaz.

## 10. Riskler ve karar sınırları

| Risk | Erken işaret | Önlem |
|---|---|---|
| Sert estetik içerik dilini de sertleştirir | “Zayıflık yok, bahane yok” türü klişeler | Görsel savaşçı; metin somut ve yargılamayan olmalı. |
| Galeri kopyala-yapıştır hissi | Birden fazla konu aynı kapağı taşıyor | Grup düzeyinde anlam; ileride gerekli konulara ayrı çekim üretimi. |
| İnanç/düşünür yanlış temsili | Sanat gerçek kişi/geleneğin resmi sanılıyor | Nötr mekân, açık kaynak etiketi; tarihsel portre iddiası yok. |
| Pro gereğinden sık görünür | Görev akışı satış ekranıyla kesiliyor | Yalnız kullanıcının ilgili niyetinden açılan teklif. |
| Demo mağaza ürünü sanılır | Kullanıcı ücret/kart soruyor | Ücret/abonelik yok cümlesi görünür; gerçek satın alma ayrı proje. |
| Duvar kâğıdı cihazda farklı kırpılır | Başlatıcı yeniden yakınlaştırır | Orijinal kaynak, oranlı crop, fiziksel cihaz kontrolü ve dürüst not. |
| Yeni tasarım eski kayıtları bozar | Seçili tema/seri kimliği kaybolur | Kalıcı kimlikleri koru; göç ve eski veri testleri. |
| On günlük liste kapsam taşmasına dönüşür | Her gün yeni özellik ekleniyor | Gün sonu kabul kapısı; kritik düzeltme isteğe bağlı işten önce. |

## 11. Yeniden iş emri olarak kullanılabilecek kısa kapsam

> Bu yol haritasını Gün 1’den başlayarak uygula. Gün 1–3’te onboarding’i koyu antik/gladyatör kimliğine taşı; sabit eylemi, yedi dili, izin açıklığını ve ücretsiz temaları koru. Gün 4’te Pro’yu seçilen sonuca bağla, demo ile gerçek ödemeyi karıştırma. Gün 5–8’de Keşfet, görünümün üç kullanımı, özel seri ve kayıt/paylaşım akışını sadeleştir. Gün 9’da erişilebilirlik, dil ve performans kapılarını çalıştır. Gün 10’da doğrulanmış APK, kısa teslim notu ve açık kalanları ver. Gerekmedikçe yeni özellik veya altyapı ekleme; kaynak commit ile APK eşleşsin. Gerçek cihazda çalıştırılmayan şeyi doğrulanmış diye yazma.

## 12. Kaynaklar

- [Android WallpaperManager](https://developer.android.com/reference/android/app/WallpaperManager) — statik görsel uygulama, hedef bayrakları ve cihaz kısıtlamaları.
- Proje içi Faz 3–4 teslimi: `PHASE_3_4_9_26.md` — kapanan sorunlar ve açık gerçek cihaz kapıları.
- Önceki kapsamlı denetim: `02-Kapsamli-Inceleme.md` — rapor maddeleriyle bağ kurulmuştur; eski sürüm bulguları doğrudan yeni hata ilan edilmez.
