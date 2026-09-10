# ASCEND
# Bordo kimliğini güçlendirme ve arayüzü rafine etme raporu

**Tarih:** 10 Eylül 2026  
**Belge türü:** Tasarım analizi, bileşen şartnamesi ve sonraki uygulama için karar raporu  
**Ana dayanak:** Ascend_Bordo_UI_UX_Refinement_Promptu.md  
**İncelenen kaynak:** Ascend 8.3, 9911223 sürüm noktası  
**Bu çalışmanın çıktısı:** Rapor. Uygulama kodu, içerik, APK ve erişim davranışları değiştirilmedi.

---

## 1. Ana tasarım kararı

Ascend'in mevcut açık zeminli, serif ağırlıklı yapısı korunmalı. İyileştirme, arayüzün baştan kurulmasıyla değil, mevcut bileşenlerin renk rolleri ve birbirleriyle ilişkileri üzerinden yapılmalı.

Önerilen yön: **sıcak açık zemin + güçlü mürekkep metin + tekrarlanan fakat küçük bordo imzalar + kontrollü klasik ayrıntı.**

Bu yönün üç görünür sonucu olmalı:

1. Kullanıcı ana ekrana baktığında önce sözü okumalı; buna rağmen uygulamanın kendine ait bir renk kimliği olduğunu hissetmeli.
2. Keşfet'te kartların aynı uygulamaya ait olduğu hemen anlaşılmalı; kullanıcı koleksiyonları yalnız başlıklarını tek tek okuyarak ayırmak zorunda kalmamalı.
3. Seçilmiş konu, aktif sekme, basılan düğme ve yalnızca marka süsü birbirine karışmamalı.

Bu raporun önerdiği değişiklikler, yeni bir ekran mimarisi oluşturmaz. Bugün, Keşfet ve Senin korunur. Sözün font ailesi ve mevcut okuma biçimi korunur. Paylaşım, kaydetme, konu açma ve bildirim planı aynı işlevleri sürdürür.

**En önemli karar:** Bordoyu her yerde biraz artırmak yerine, nerede ne anlattığını belirlemek gerekir. Paylaş düğmesindeki bordo markayı; seçili sekmedeki bordo konumu; koleksiyondaki tikli bordo durum ise kullanıcının tercihini anlatır. Bu üç kullanımın yüzey ve işaret biçimleri farklı olmalıdır.

## 2. İnceleme yöntemi ve kanıt sınırları

Bu raporda üç bilgi türü ayrı tutuldu:

| Bilgi türü | Kaynağı | Nasıl kullanıldı? |
|---|---|---|
| Güncel tasarım beklentisi | Kullanıcının yeni refinement dosyası | Öncelik ve sınırları belirler. |
| Doğrulanmış mevcut yapı | 8.3 kaynak dosyaları | Renk, boyut, ikon eşlemesi ve yerleşim kararlarını gösterir. |
| Görsel değerlendirme | Konuşmadaki önceki ekranlar ve kullanıcının son sürüm değerlendirmesi | Sorunun estetik yönünü açıklar; son APK'nın birebir piksel ölçümü sayılmaz. |

Yeni logo, dosyada sütun + yeşil yaprak + bordo zemin olarak tarif ediliyor. Logonun gerçek görseli bu yeni dosyaya eklenmemiş. Bu nedenle rapor, logonun kesin yeşil tonunu, çizgi oranını veya kırpımını uydurmaz. Logoya ilişkin ölçüler uygulama için öneridir; gerçek logo dosyasıyla optik eşleştirme gerekir.

Bu turda son APK yeniden açılıp ekran görüntüsü alınmadı. Dolayısıyla “şu boşluk cihazda tam 42 dp” veya “bordo ekranın yüzde 7'sini kaplıyor” gibi ölçülmemiş iddialar kullanılmadı. Kodda belirli bir değer varsa mevcut değer olarak; öneri varsa öneri olarak belirtildi.

Renk, boyut ve hareket önerileri Ascend'e özgü tasarım kararlarıdır. Erişilebilirlik ilkeleri ise ilgili Android ve W3C kaynaklarıyla desteklenmiştir. Bir estetik önerinin yanında sayı olması onu evrensel bir tasarım standardına dönüştürmez.

## 3. Korunacaklar, değiştirilecekler ve kapsam dışında kalanlar

### 3.1 Korunacaklar

- Açık, sıcak zemin ve koyu serif söz tipografisi.
- Ana söz ekranının tek okuma odağı.
- Heykelin arka planda dekor olarak bulunması.
- Üç ana navigasyon hedefi.
- Keşfet'in başlık → açıklama → arama → sekmeler → bildirim özeti → koleksiyonlar sırası.
- Mevcut koleksiyon ve konu isimlerinin içerik anlamı.
- Açık/kilitli konu ayrımı ve açık kontrolle yapılan bildirim seçimi.
- Arama, filtre, geri dönüş ve kaydırma konumunun korunması.
- Ana ekran eylemlerinin kaydırma gerektirmeden erişilebilir olması.
- Koyu tema, büyük yazı ve ekran okuyucu desteği.

### 3.2 Bu raporun önerdiği değişiklikler

- Paylaş düğmesine belirgin fakat açık bordo yüzey ve koyu bordo metin.
- Aktif Keşfet sekmesine koyu bordo metin + mevcut kısa gösterge.
- Koleksiyon kartlarına daha anlaşılır yüzey ayrımı.
- Her koleksiyon için aynı aileden farklı bir çizgisel ikon.
- Gerçekten seçili alt konusu olan koleksiyonların küçük ve açıklayıcı durum işareti.
- Bildirim özetinin durum metninin daha doğru, yüzeyinin daha zarif olması.
- Üst bölüm boşluklarının sınırlı ölçüde düzenlenmesi.
- Başlık veya heykel çevresinde isteğe bağlı tek bir düşük yoğunluklu bordo atmosfer.

### 3.3 Bu çalışmada önerilmeyenler

Onboarding'i yeniden yazmak; yeni soru, yeni sekme veya yeni kategori eklemek; fiyatlama, Pro, reklam veya erişim kurallarını değiştirmek; heykeli yenilemek; her kategoriye resim koymak; söz kartını yeniden tasarlamak; yeni font ailesine geçmek; marka için yeni bir logo icat etmek.

Yeşil, yeni logonun bir parçasıdır. Bu bilgi, uygulamanın düğmelerine, kartlarına ve sekmelerine ikinci bir yeşil vurgu sistemi eklemek için gerekçe değildir.

## 4. Mevcut 8.3 yapısının ayrıntılı teşhisi

### 4.1 Renk sistemi

Mevcut kaynakta ana zemin #FCFAF8, kart yüzeyi #F7F4F3, ana metin #242022, ikincil metin #6E676A ve bordo #6C2932. Bu temel palet doğru yönde.

Sorun, bordonun bulunmaması değil; çoğunlukla küçük çizgi ve ikonlara sıkışması. Ana ekrandaki Paylaş düğmesi nötr yüzeye, aktif navigasyon metni mürekkebe, koleksiyonlar aynı nötr zemine bağlı. Böylece bütün sistem tutarlı fakat marka açısından çok temkinli görünebilir.

**Teşhis:** Yeni bir palet gerekmiyor. Var olan bordonun bir veya iki orta ölçekli, düşük doygunluklu yüzeyde görünmesine ihtiyaç var.

### 4.2 Ana ekran

Kaynakta söz alanı ayrı, gezinme ve eylemler ayrı tutuluyor. Bu davranış korunmalı. Heykelin genişliği ve yüksekliği sınırlandırılmış; opaklığı yüzde 9 ile sınırlı. Sözü büyütmek veya heykeli yeniden yerleştirmek ilk çözüm olmamalı.

Üst kaynak seçicisi ile okuma grubu arasındaki mesafe, kullanılabilir yüksekliğe ve söz uzunluğuna bağlı. Bu nedenle tek bir ekran yüksekliğine göre negatif boşluk eklemek diğer telefonları bozabilir.

**Teşhis:** Dikey dağılım sınırlanmalı; yazının font boyutu üzerinden boşluk doldurulmamalı.

### 4.3 Ana ekran eylemleri

Kaydet ve Paylaş asgari 52 dp. Kaydet konturlu, Paylaş nötr dolgu kullanıyor. İşlevsel ayrım var; ancak Paylaş'ın tonu renk kimliğini taşımıyor.

**Teşhis:** En güvenli marka kazanımı burada. Yerleşimi değiştirmeden, yalnızca yüzey/metin/ikon renklerini doğru eşlemek yeterli.

### 4.4 Keşfet kartları

Mevcut kartlar 20 dp köşeli, 16 dp iç boşluklu, 20/26 sp başlıklı ve 12/18 sp açıklamalı. Kart yüzeyi ile sayfa arasındaki ayrım düşük. Kenarlığın ayrıca yarı saydam olması bu ayrımı daha da sessizleştiriyor.

Mevcut ikon eşlemelerinde aynı ikonlar birden çok koleksiyon tarafından kullanılıyor:

- Olumlamalar ve İlişkiler: kalp.
- Azim ve Cesaret: yükseliş.
- Disiplin ve İş: pusula.
- Filozoflar ve İnanç: kitap.
- Zihin ve Tasavvuf: yaprak.
- Diğerleri: yol.

**Teşhis:** Kartların birbirine benzemesi yalnız yüzey rengi sorunu değil. İlk tarama ipuçları da birbirini tekrar ediyor.

### 4.5 Seçim bilgisinin yeri

Konu satırlarında “Bildirimlerinde” gibi durumlar var. Ancak koleksiyon kartı kendi içinde kaç alt konunun seçildiğini göstermiyor. Kullanıcı bir üst düzeye döndüğünde tercihinin nerede bulunduğunu yeniden aramak zorunda kalabilir.

**Teşhis:** Koleksiyonun tamamını seçilmiş gibi boyamadan, alt konu seçimini özetlemek gerekir.

### 4.6 Navigasyon

Mevcut üçlü navigasyonun 76 dp asgari yüksekliği, 24 dp ikonları ve kısa bordo üst çizgisi tutarlı. Burada büyük bir değişiklik gerekmiyor.

**Teşhis:** Navigasyonu yeniden çizmek yerine mevcut aktif gösterge korunmalı; marka ağırlığı öncelikle Paylaş ve Keşfet'te güçlendirilmeli.

## 5. Marka kimliği: sütun, yaprak ve bordo

### 5.1 Görsel hiyerarşi

Yeni kimlikte sütun kalıcılık ve düşünsel yapı; yaprak canlılık ve gelişim; bordo ise sıcaklık ve karakter çağrışımı taşıyabilir. Bunlar tasarım yorumudur; bütün kullanıcıların aynı anlamı çıkaracağı varsayılmamalı.

Bu üç unsurun her ekranda birlikte görünmesi gerekmiyor. Kimliğin tutarlılığı, aynı üç görseli tekrar tekrar yerleştirmekten değil, uyumlu oran ve renklerden gelmeli.

### 5.2 Uygulama içindeki logo

Ana ekrandaki marka satırında gerçek yeni logo kullanılacaksa başlangıç önerisi 24–28 dp optik ölçüdür. Yanındaki “ascend” kelime işareti mevcut serif karakterini korumalı. Logo kelime işaretinden daha baskın olmamalı.

Logonun bordo zemini küçük bir simge yüzeyinde kalmalı; başlığı dolduran bir şeride dönüşmemeli. Logoyu büyütüp sayfa üstüne renk taşımak, önceki reddedilen tasarıma geri döndürür.

Gerçek ikon bu boyutta okunmuyorsa ayrıntıları keyfî silmek yerine onaylı küçük boyut varyantı kullanılmalı. Henüz böyle bir varyant yoksa rapor aşamasında tasarlanmış gibi gösterilmemeli.

### 5.3 Yükselen çizginin rolü

Yükselen çizgi, Bugün sekmesinin işlevsel ikonu olarak kalabilir. Ancak yeni logo geldikten sonra aynı çizginin ayrıca “resmî marka logosu” gibi sunulması iki kimlik yaratabilir.

Öneri: sütun/yaprak gerçek marka; yükseliş işareti navigasyon ve ilerleme metaforu. Bu ayrım arayüzü baştan değiştirmeden uygulanabilir.

### 5.4 Yeşilin sınırı

Yeşili ilk aşamada gerçek logo içindeki yaprakla sınırla. Koleksiyon ikonlarını farklı yeşillere boyama; bildirim açık durumunu yeşile çevirme; yaprağın renginden yeni kart paletleri üretme.

İleride tek bir dekoratif yaprak ayrıntısı gerekirse logonun gerçek renginden türetilmeli ve ayrı değerlendirilmelidir. Bu raporda böyle bir ekleme gerekli görülmüyor.

## 6. Bordo için net kullanım sistemi

### 6.1 Dört farklı rol

| Rol | Anlam | Görsel biçim | Örnek |
|---|---|---|---|
| Marka | Ascend'e ait görünüm | Açık bordo yüzey + koyu bordo içerik | Paylaş |
| Konum | Şu anda buradasın | Koyu bordo kısa metin + kısa çizgi | Aktif Keşfet sekmesi |
| Tercih | Bunu sen seçtin | Tik + açıklama + küçük bordo alan | 2 konu seçili |
| Atmosfer | İçeriği bağlayan dekor | Çok düşük opaklıklı, maskeli ton | Başlık kenarı |

Bu roller aynı anda aynı bileşene yığılmamalı. Bir koleksiyon kartında bordo ikon zaten marka rolünü taşıyorsa, seçili durum ayrıca tik ve durum metniyle belirtilmeli. İkonun bordo olması tek başına seçili anlamına gelmemeli.

### 6.2 Önerilen renk tokenları

| Token | Öneri | Kullanım ve sınır |
|---|---|---|
| canvas | #FCFAF8 | Mevcut ana zemin korunur. |
| surfaceQuiet | #F7F4F3 | Arama gibi ikincil yüzeyler. |
| surfaceCard | #F2EEEB | Koleksiyonların açık taş yüzeyi; ilk karşılaştırma adayı. |
| ink | #242022 | Söz, başlık ve temel içerik. |
| inkSecondary | #6E676A | Açıklama, kaynak, pasif metin. |
| brandInk | #6C2932 | Aktif kısa metin ve ikonlar. |
| brandMuted | #8B5C64 | İkincil bordo ayrıntı; uzun paragraf için kullanılmaz. |
| brandWash | #F3E7EA | Paylaş ve gerçek seçili durum yüzeyi. |
| brandWashQuiet | #F7EFF1 | Küçük bildirim özeti gibi daha sessiz alan. |
| brandPressed | #EBDADD | Kısa basılı durum; kalıcı kart rengi değildir. |
| hairline | #E3DADC | Dekoratif sınır, ayırıcı. |
| controlOutline | #82777D | Belirgin olması gereken kontrol sınırı. |

Bunlar mevcut renklerin yerine topluca yazılacak bir liste değildir. Özellikle surfaceCard ve brandWash ayrı roller olarak tanımlanmalı; tek bir genel accentContainer değiştirilerek bütün ekranlar aynı anda pembeye dönüştürülmemeli.

### 6.3 Yüzde 5–10 görünürlük hedefinin doğru yorumu

Kullanıcının yüzde 5–10 hedefi bir görsel denge yönüdür. Bu oran; opaklık, boyanmış piksel alanı ve algısal ağırlık ile aynı şey değildir.

Örneğin geniş ama çok açık bir yüzey, küçük ve koyu bir işaretten daha az dikkat çekebilir. Bu nedenle ekranın tam yüzde 8'ini bordo pikselle doldurmak tasarım hedefi yapılmamalı.

Başlangıç bütçesi:

- Ana ekranda bir orta ölçekli açık bordo alan: Paylaş.
- Buna eşlik eden iki veya üç küçük işaret: kategori çizgisi, Planım oku, aktif navigasyon göstergesi.
- Keşfet'te aktif sekme ve küçük kart ikonları temel tekrar düzeni.
- Aynı görünümde en fazla bir dekoratif sis bölgesi.
- Geniş, opak, doygun bordo başlık veya söz yüzeyi yok.

Bu sayılar tasarım ekibinin taşmayı önlemesi için önerilen çalışma sınırlarıdır; ölçülmüş kullanıcı tercihi değildir.

### 6.4 Yoğunluğu artırma sırası

1. Önce Paylaş'ın yüzeyini ve yazısını düzenle.
2. Sonra aktif Keşfet sekmesini güçlendir.
3. Ardından koleksiyonların gerçek seçim işaretlerini ekle.
4. Hâlâ fazla nötr görünüyorsa kart yüzeyi ve başlık atmosferini karşılaştır.
5. Son adımda dekoratif sis düşün.

Birinci aşama yeterliyse beşinci aşamayı sırf raporda adı geçtiği için ekleme.

### 6.5 Renklerin birlikte kullanılmayacağı durumlar

Paylaş'ta bordo yüzey varsa Kaydet'e aynı yoğunlukta bordo dolgu verme. Aktif sekmede koyu bordo yazı ve çizgi varsa ayrıca büyük pembe kapsül kullanma. Koleksiyon seçiliyken tik, kalın dış çerçeve, büyük rozet ve tüm kart dolgusunu birden ekleme.

Kullanıcı bir işaretin anlamını okumadan bile ayırt edebilmeli. Fazla vurgu bütün bileşenleri aynı önem düzeyine taşır.

## 7. Renk ve kontrast denetimi

Aşağıdaki değerler önerilen opak sRGB çiftleri üzerinden hesaplandı. Ekrandaki gerçek font rasterizasyonu, saydamlık, görsel arka plan ve cihaz parlaklığını kapsayan bir cihaz testi değildir.

| Ön plan / arka plan | Yaklaşık oran | Yorum |
|---|---:|---|
| #242022 / #FCFAF8 | 15,46:1 | Ana söz ve başlık güçlü kalır. |
| #6E676A / #F2EEEB | 4,78:1 | Kart açıklaması için başlangıçta yeterli; saydamlaştırma yapılmamalı. |
| #6C2932 / #F3E7EA | 8,71:1 | Paylaş yazısı ve ikonu için güçlü çift. |
| #6C2932 / #EBDADD | 7,80:1 | Basılı durumdaki yazı korunur. |
| #8B5C64 / #FCFAF8 | 5,29:1 | Kısa ikincil bordo etiket için uygun aday. |
| #82777D / #FCFAF8 | 4,13:1 | Kontrol sınırı olarak belirgin. |
| #DBB6C0 / #302529 | 8,06:1 | Koyu temada açık bordo içerik için aday. |
| #F2EEEB / #FCFAF8 | 1,11:1 | Kart yüzeyi tek başına güçlü bir etkileşim işareti değildir. |

Normal metinde 4,5:1 eşiği kullanılmalı. Büyük metin için farklı eşik bulunsa da Ascend'in küçük açıklamalarını “büyük metin” kabul ederek esnetme. Android sp ile web pt ölçülerini doğrudan birbirine eşitleme. [W3C — Contrast Minimum](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum)

Kartın açık taş yüzeyinin düşük kontrastlı olması tek başına başarısızlık anlamına gelmez. Ancak kartın tıklanabilirliği yalnız bu yüzey farkına bağlanamaz. Ok, başlık, odak göstergesi ve tıklanabilir semantik birlikte çalışmalı. İşlevi veya durumu anlamak için gerekli görsel işaretlerde 3:1 dikkate alınmalı; dekoratif her çizgiyi koyulaştırmak gerekmez. [W3C — Non-text Contrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html)

Seçim yalnız renk değişimiyle anlatılmamalı. Tik, kısa açıklama ve seçili semantiği korunmalı. [W3C — Use of Color](https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html)

## 8. Ana ekran: korunacak kompozisyon

Ana ekranın iskeleti aynı kalmalı:

1. Marka ve Planım.
2. Sana göre / anlık ihtiyaç seçicisi.
3. Kategori, söz ve kaynak.
4. Önceki, gerçek konum sayacı, sonraki.
5. Kaydet, Paylaş, diğer araçlar.
6. Alt navigasyon.

Bu sıra zaten anlaşılır. Sorun yeni bir blok eksikliği değil; üst grubun boşluk dengesi ve alt eylemlerin görsel ağırlığı.

### 8.1 Başlık ve kaynak seçicisi

Mevcut marka satırını büyütme. Normal ölçekte 56–64 dp bandı yeterli başlangıçtır; büyük yazıda yüksekliği serbest bırak.

Marka satırı ile kaynak seçicisinin dokunma alanı arasında 4–8 dp görsel nefes hedeflenebilir. Kaynak seçicisinin en az 48 dp dokunma alanı korunmalı. Metnin kendisi daha küçük görünebilir.

Kaynak seçicisi ile kategori etiketinin arasını sabit tek bir dev boşluğa dönüştürme. Normal ekranda 20–32 dp aralığıyla karşılaştırma yapılmalı. Kısa sözlü, uzun ekranlarda 40 dp'ye kadar nefes kabul edilebilir; bu alan sürekli büyüyen esnek boşluk olmamalı.

Bu aralıklar başlangıç önerisidir. Nihai karar aynı söz, aynı cihaz genişliği ve aynı yazı ölçeğiyle önce/sonra karşılaştırılarak verilmeli.

### 8.2 Sözün görsel büyüklüğü

Mevcut Lora serif ve uzunluğa göre font kademeleri korunmalı. Bu çalışmada sözün normal font ölçülerini topluca küçültme veya büyütme.

Üstteki boşluğu azaltmak için sözü yukarı zorla taşımak da çözüm değil. Kategori, söz ve kaynak birlikte hareket eden tek grup olarak kalmalı; kaynak sayfa altında bağımsız bir dipnot hâline gelmemeli.

Söz kısa olduğunda tek sözcüğü satır sonunda yalnız bırakmayan satır dengesi aranabilir. Fakat metne elle satır sonu eklemek, çeviri ve farklı ekranlarda sorun yaratır. Metnin anlamına müdahale etmeden doğal sarma tercih edilmeli.

### 8.3 Kategori çizgisi

Mevcut 22 × 1 dp çizgi iyi bir başlangıç. Öneri 26–28 dp uzunluk ve aynı 1 dp kalınlık. Renk brandInk; opaklık düşürülmemeli. Çizginin ince fakat belirgin olması, kalınlaştırılmasından daha rafine sonuç verir.

Kategori metni gri kalabilir. Böylece çizgi marka vurgusunu taşır, uzun kategori adları bordo paragraf etkisine dönüşmez.

Çizgi ile etiket arasında 8–10 dp; etiket ile söz arasında mevcut 12–16 dp ilişkisi korunmalı. Kategori iki satıra geçerse çizgi ilk satırın optik merkezine bağlanmalı; iki satırın tam ortasında anlamsız biçimde yüzmemeli.

### 8.4 Kaynak satırı

Kaynağın işlevi sözü açıklamaktır. Metinle arasında yaklaşık 16 dp yeterlidir. Kaynağı sırf sade görünmesi için daha soluk veya daha küçük yapma.

“Ascend · Olumlama” ve düşünür kaynağı aynı düzen içinde okunmalı. Uzun kaynaklar sarabilmeli. Heykel veya aura bu satırın kontrastını düşürmemeli.

### 8.5 Kısa ve uzun sözde alan yönetimi

Kısa sözde: okuma grubunun üstü kontrollü mesafede kalsın; boşluğun bir kısmı kaynak sonrasında bırakılabilir. Bu boşluğu yeni dekor veya sloganla doldurmak gerekmez.

Uzun sözde: mevcut içerik kaydırması devreye girebilir. Eylemler ve navigasyon söz tarafından aşağı itilmemeli.

Büyük yazıda: “her şey tek ekrana sığmalı” hedefi yerine “her şey okunabilir ve ulaşılabilir olmalı” hedefi kullanılmalı. Bu kullanıcı grubunda heykel ve aura daha da sakinleşebilir; içerik ölçüsünü küçültmek son çare değildir.

## 9. Ana ekran: eylem ailesi

### 9.1 Paylaş

Önerilen normal durum:

- Yüzey: brandWash, #F3E7EA.
- Metin ve ikon: brandInk, #6C2932.
- Asgari yükseklik: mevcut 52 dp; gerekiyorsa 56 dp.
- Köşe: mevcut kapsül.
- Sınır: yüzeyle uyumlu çok hafif bordo kenar; siyah çerçeve eklenmez.
- İkon: mevcut paylaş vektörü; görsel ölçüsü diğer eylemle optik olarak eşleşir.

Bu, ekranın tek orta büyüklükte bordo yüzeyi olmalı. Doygun bordo dolgu kullanılmamalı.

Basılı durumda yüzey brandPressed'e kısa süreli yaklaşabilir. Yazı ve ikon sabit koyu bordo kalır. Kontrol parmağın altında belirginleşir; zıplamaz veya büyümez.

### 9.2 Kaydet

Normal durum açık zemin, okunur mürekkep metin ve belirgin 1 dp kontur. Mevcut sınırın fazla saydam görünmesi durumunda kontrol sınırı opaklığa değil doğrudan uygun renk tokenına bağlanmalı.

Kaydedilmiş durumda kalp biçimi değişsin ve mevcut durum metni korunsun. Kalp koyu bordo olabilir; bütün düğmenin Paylaş ile aynı pembe yüzeye dönüşmesi gerekmez.

Kaydet işlemi tek dokunuşla anlaşılmalı. Kalp animasyonu varsa kısa olmalı; metin değişimi satır yüksekliğini veya düğmenin genişliğini aniden değiştirmemeli.

### 9.3 Diğer araçlar

Üç nokta nötr kalmalı. Görünen daire 48–52 dp; dokunma alanı en az 48 dp. İkon mürekkep veya okunur gri, yüzey ana zemin.

Bu kontrolü bordo yapmak, Paylaş'ın hafifçe öne çıkmasıyla yarışır. Menü açıldığında kullanılan yüzey de ana arayüzün sıcak nötr ailesinde kalmalı.

### 9.4 Eylem durum tablosu

| Kontrol | Normal | Basılı | Kalıcı seçili | Odak |
|---|---|---|---|---|
| Kaydet | Açık zemin, mürekkep, net kontur | Hafif nötr katman | Dolu kalp + durum metni; küçük bordo ikon | Açıkça görülen çevre çizgisi |
| Paylaş | Açık bordo yüzey, koyu bordo içerik | Bir kademe koyu açık bordo | Seçili durumu yok | Kontrastlı bordo çevre çizgisi |
| Menü | Nötr daire, mürekkep noktalar | Nötr basılı katman | Menü açıkken semantik durum | Nötr/koyu görünür çevre çizgisi |

Paylaş'a “seçili” semantiği verilmemeli; bir eylemdir. Kaydet ise kayıt durumunu açıkça anlatır. Renk grameri bu işlev farkını yansıtmalı.

### 9.5 Sayaç ve oklar

Gerçek sayaç korunmalı; 4 / 600 gibi değerler tasarım örneği olarak sabitlenmemeli. Oklar aynı görünür ölçü ve çizgi ağırlığında kalmalı.

İlk veya son sözde devre dışı ok görünümü anlaşılır olmalı. Etkin okların kontrastını dekor gibi düşürme. Sayacın tamamını bordo yapmaya gerek yok; en fazla geçerli konum sayısı hafif vurgu alabilir.

İlk uygulama önerisi sayaç rengini değiştirmemek. Paylaş, kategori çizgisi ve navigasyon zaten yeterli marka tekrarını sağlayabilir. Bu yaklaşım bir gereksiz ayrıntıyı bilinçli biçimde dışarıda bırakır.

## 10. Heykel ve bordo atmosfer

### 10.1 Temel karar

Heykel dosyası, rengi ve mevcut konumu korunmalı. Aura bağımsız, isteğe bağlı bir arka plan katmanı olarak değerlendirilmelidir. Heykelin kendisine bordo renk filtresi verilmemeli.

### 10.2 Önerilen aura

Başlangıç adayı: heykelin gerisinde, ekranın sağ tarafında, kenarlara doğru tamamen saydamlaşan tek geniş ton alanı. Kaynak renk bordo; en yüksek başlangıç opaklığı yüzde 1,5–2,5. Yüzde 3,5'in üzerine ancak aynı cihazdaki karşılaştırma gerekçesiyle çıkılmalı.

Bu opaklıklar deneysel tasarım sınırlarıdır. Heykelin yüzde 9 opaklığıyla aynı parametre değildir; iki katmanın toplam etkisi ayrıca görülmelidir.

Aura merkezini yüzün tam arkasına koymak, halo veya dramatik kutsallık etkisi yaratabilir. Daha güvenli konum heykelin dış kenarına doğru, metinden uzak bölgedir.

### 10.3 Maske ve okunurluk

Ton alanının dört yanında sert sınır olmamalı. Dikdörtgen görsel kutusu, pembe leke veya belirgin daire okunmamalı. Kaynak ve kategori metninin arkasında gerekirse nötr okuma yüzeyi korunmalı.

Aura sözün orta satırlarının arkasına taşmamalı. Uzun sözle çakıştığında dekorun alanı veya yoğunluğu azaltılmalı; sözün yeri değiştirilmemeli.

### 10.4 Kabul ve ret

Kabul: göz ilk bakışta söze gider, ikinci bakışta mermer çevresinde sıcak bir ton hisseder.

Ret: kullanıcı “arka plana pembe daire konmuş” der; heykelin yüzü boyalı görünür; ton, kaynak yazısını zayıflatır; fotoğrafın kenarı belli olur.

Bu dört ret işaretinden biri varsa aura tamamen çıkarılabilir. Aura kullanmamak bu raporun eksik uygulanması sayılmaz; ölçülü olma talebinin karşılığıdır.

## 11. Keşfet: başlık, arama ve üst kontroller

### 11.1 Genel düzen

Mevcut sıra korunmalı. Kullanıcı önce nerede olduğunu, sonra ne arayabileceğini, ardından hangi görünümü kullandığını anlamalı.

Arama listeden bağımsız kalmalı. Koleksiyonlarda aşağı kaydırırken arama tamamen kaybolmamalı. Mevcut çözüm bu açıdan doğru; rapor bunu yeniden ele alınacak bir hata gibi görmüyor.

Başlık ve arama alanının toplam yüksekliği, kartların ilk ekranda hiç görünmemesine neden olmamalı. Normal yazı ölçeğinde ilk kart sırası görünür olmalı. Büyük yazıda bu hedef zorla sağlanmamalı; kontrollerin okunurluğu önce gelir.

### 11.2 Başlık

“Keşfet” mürekkep serif olarak korunmalı. Başlığın tamamını bordoya çevirmek gereksiz. Bordo, sağdaki çok düşük yoğunluklu mimari izde veya küçük bir vurgu çizgisinde kullanılabilir.

İki olası çözüm:

- **Tercih edilen ilk çözüm:** mevcut hafif sütun ayrıntısını koru; ikon ve sekmelerden gelen marka tekrarını yeterli kabul et.
- **İsteğe bağlı ikinci çözüm:** başlığın sağ kenarında yüzde 1,5–2 bordo ton geçişi. Ayrıntılı sütun ile belirgin sis aynı anda kullanılmaz.

Başlığın altındaki kısa açıklama bir satırda doğal akmalı; sığmıyorsa iki satıra izin verilmeli. Font boyutu yalnız bir satıra sığdırmak için küçültülmemeli.

### 11.3 Arama alanı

Mevcut sıcak nötr yüzey korunabilir. Kart yüzeyi koyulaşırsa aramayı aynı ölçüde koyulaştırmak gerekmiyor. Arama, bütün koleksiyonların üzerinde tek bir kontrol olarak algılanmalı.

Başlangıç şartnamesi:

| Özellik | Öneri |
|---|---|
| Görünen yükseklik | Normal yazıda 52–56 dp |
| Köşe | Mevcut 14–16 dp ailesi |
| İç yatay boşluk | 12–16 dp |
| İkon | 20–22 dp; okunur gri |
| Metin | 14–16 sp sans |
| Normal sınır | Sessiz nötr veya görünmez |
| Odak sınırı | 1,5 dp bordo |
| Temizleme | En az 48 dp dokunma alanı |

Yer tutucu ile yazılmış sorgunun rengi ayrışmalı. Kullanıcının yazdığı metin mürekkep olmalı. Arama ikonunu bordo yapma gereği yok; odak sınırı markayı yeterince gösterir.

Sorgu varken sonuç sayısı okunur kalmalı. Boş sonuç durumunda bir açıklama ve temizleme eylemi yeterli; büyük dekoratif boş ekran resmi gerekmez.

### 11.4 Sekmeler

Aktif sekme için öneri: brandInk metin, orta/yarı kalın ağırlık ve 22–28 dp uzunlukta 2 dp bordo alt çizgi. Pasif sekmeler ikincil gri.

Sekmeler içerik genişliğinde olmalı. Üç etiketi ekranı doldurmak için eşit genişliklere zorlamak uzun Türkçe kelimelerde dengesiz boşluk yaratır. Yatay kaydırılabilir düzen korunmalı.

Alt çizgi metnin tamamının genişliğine uzatılmamalı. Kısa çizgi Ascend'in kategori çizgisi ve navigasyon işaretiyle akraba kalır.

Seçili sekmeye ayrıca pembe kapsül eklenmemeli. Hem yazı hem çizgi seçimi zaten belirginleştirir.

### 11.5 Bildirim özeti

Bu satır, kullanıcıya seçiminin hâlâ kayıtlı olduğunu hatırlatır. Bir kampanya kartı gibi görünmemeli.

Önerilen biçim:

- Asgari 48 dp dokunma alanı.
- İç yatay boşluk 12 dp, iç dikey boşluk 8–10 dp.
- Solda 18–20 dp bordo bildirim ikonu.
- Ortada 12–13 sp okunur metin.
- Sağda 16 dp nötr yön oku.
- Yüzey brandWashQuiet; doygun pembe görünmemeli.
- Sert gölge ve kalın çerçeve yok.

“Daha kompakt” olması, dokunma alanını 36 dp'ye düşürmek anlamına gelmemeli. Görünen içerik hafifletilebilir; dokunma alanı korunur.

### 11.6 Bildirim özeti için doğru metinler

| Gerçek durum | Önerilen ifade |
|---|---|
| Konu seçili ve bildirim kullanımı etkin | “3 konu seçili” veya bağlama göre “3 konu bildirim planında” |
| Konular kayıtlı, bildirimler kapalı | “3 konu seçili · Bildirimler kapalı” |
| Hiç konu seçilmemiş | “Bildirim konularını seç” |
| Sistem izniyle ilgili bilgi bu ekranda bilinmiyor | İzin varmış gibi konuşmayan “3 konu planında” |

Bu metinler mevcut veriyi doğru yansıtmalı. Sadece konu seçili diye gerçekten bildirim gönderildiği ima edilmemeli. Yeni bildirim mantığı kurulması istenmiyor; mevcut durumun arayüzde yanlış anlatılmaması isteniyor.

## 12. Koleksiyon kartlarının yeniden inceltilmesi

### 12.1 Kart karakterinin üç kaynağı

Kartlar şu üç ipucuyla ayrışmalı:

1. Koleksiyona özel küçük ikon.
2. Kısa, anlamlı açıklama.
3. Gerçek seçim varsa onu açıklayan küçük durum bilgisi.

Yüzey rengi her kartı başka bir dünyaya taşıyan unsur olmamalı. On iki farklı pastel renk, her kartta başka bir doku veya farklı köşe biçimleri kullanılmamalı.

### 12.2 Kart yüzeyi

Mevcut #F7F4F3 yerine koleksiyonlara özgü #F2EEEB ilk karşılaştırma adayıdır. Ana zemin #FCFAF8 kalır. Böylece kart sınırlarını fark etmek biraz kolaylaşır.

Bu ayrım hâlâ oldukça hafiftir. Başlığın, yön okunun ve düzgün hizalanmış içeriğin rolü devam eder. Bütün etkileşim görünürlüğünü yüzey farkından beklemek yanlış olur.

Kenarlık tek, ince ve tutarlı olmalı. Kartı fark edilir kılmak için ağır gölge yerine öncelikle yüzey tonu, kenar ve çevresindeki boşluk birlikte ayarlanmalı.

### 12.3 Ölçüler

Normal iki sütun görünümü için başlangıç:

- Ekran yatay kenarı 20–24 dp.
- Sütun aralığı 12 dp.
- Satır aralığı 12 dp.
- Kart iç boşluğu 14–16 dp.
- İkon 20–22 dp.
- İkon ile başlık 8–10 dp.
- Başlık 20/26 sp; ilk denemede mevcut ölçü korunur.
- Açıklama 13/19 sp; mevcut 12/18'den küçük bir okunurluk artışı.
- Açıklama ile alt bilgi en az 12 dp.
- Alt bilgi 11–12 sp; yazı sığmıyorsa satırın büyümesine izin verilir.
- Köşe 20 dp.

Kart yüksekliği sabitlenmemeli. Aynı sıradaki kartlar eşitlenebilir; farklı sıralar aynı yüksekliğe zorlanmamalı.

### 12.4 Dar ekranın gerçek etkisi

360 dp genişlikte 24 dp iki kenar ve 12 dp aralık çıkarıldığında kart başına yaklaşık 150 dp kalır. İç boşluklar çıkarılınca başlık için yaklaşık 118 dp vardır.

Bu nedenle “Azim & Dayanıklılık” gibi bir başlık, yalnız estetik istekle tek satıra indirilemez. Fontu aşırı küçültmek yerine iki veya üç satır kabul edilmelidir.

360 dp altı veya yüzde 130 üzeri yazıda mevcut tek sütun davranışı korunmalı. Çok uzun yerelleştirmelerde gerekirse daha erken tek sütun değerlendirilir; metin üç noktayla kaybedilmez.

### 12.5 Açıklamaların görevi

Açıklama başlığın sözlük tanımı olmamalı. Kullanıcıya bu koleksiyondaki sözlerin hangi ihtiyaca eşlik ettiğini anlatmalı.

Örnekler, içerik değişikliği değil UI kopyası önerileridir:

| Koleksiyon | Önerilen kısa açıklama |
|---|---|
| Günlük olumlamalar | “Kendine daha nazik bir dil” |
| Azim & Dayanıklılık | “Devam et, yeniden başla” |
| Disiplin & Odak | “Dikkatine alan aç” |
| Özgüven & Cesaret | “Korkuya rağmen bir adım” |
| Filozoflar | “Düşünceye yeni bir açı” |
| Tasavvuf & Doğu | “İç dünyana bir bakış” |
| İnanç | “İnanç üzerine düşünceler” |
| Spor & Beden | “Harekete eşlik eden sözler” |
| İş & Başarı | “Emek, amaç ve gelişim” |
| İlişkiler | “Bağ kurmak ve anlamak” |
| Zihin & Huzur | “Günün içinde sakin bir durak” |
| Öğrenme & Gelişim | “Merakına yer aç” |

Her açıklama gerçek içerikle karşılaştırılmalı. Koleksiyonda olmayan bir fayda veya deneyim vaat edilmemeli. “Kaygını yok et”, “Başarıyı garantile” gibi ifadeler bu tasarımın tonu değildir.

### 12.6 Alt bilgi

“9 konu” gibi gerçek sayı korunmalı. Ok sağ alt köşede aynı optik çizgide olmalı. Kartın tamamı tıklanabilirken küçük oka ayrı bir ikinci hedef eklenmemeli.

Seçili alt konu varsa iki satırlı alt bilgi kullanılabilir:

- Birinci satır: “9 konu”.
- İkinci satır: küçük tik ve “2 konu seçili”.

Dar alanda iki bilgiyi “9 konu · 2 seçili” biçiminde tek satıra zorlamak yerine doğal sarma tercih edilmeli.

### 12.7 Tümü seçili görünümü

Bir koleksiyondaki bütün konular seçilmiş olsa bile kartı koyu bordoya doldurmak gerekmiyor. “9 konu seçili” bilgisi yeterli.

Bu sayının seçili alt konulardan geldiği anlaşılmalı. Koleksiyona dokunmanın toplu seçim yaptığı izlenimi verilmemeli. Mevcut detay açma davranışı korunur.

### 12.8 Kartlarda önerilmeyen ayrıntılar

Roma rakamı, dekoratif çizgi, farklı ikon, desen, bordo köşe ve rozetin aynı kartta birlikte kullanılması önerilmiyor. Her kartta tek kimlik ipucu olarak ikon; gerektiğinde tek durum ipucu olarak tikli bilgi yeterlidir.

Bu sınırlama kartları sıradanlaştırmaz. Aynı sistemdeki dikkatli ikon ve metin eşlemesi, rastgele süsten daha tutarlı bir karakter üretir.

## 13. Koleksiyonlara özgü ikon sistemi

### 13.1 Ortak çizim kuralları

Mevcut 24 × 24 koordinat alanı ve 1,75 dp yuvarlak çizgi sistemi korunmalı. Yeni ikonlar bu aileye eklenmeli; farklı bir kütüphaneden kalın, köşeli veya dolgulu simgeler karıştırılmamalı.

Koleksiyon ikonu 20–22 dp görünür ölçüde test edilmeli. Büyütülmüş tasarım önizlemesinde güzel görünen fakat telefonda kapanan detaylar çıkarılmalı.

Küçük çizgiler arasında yaklaşık 2 dp optik nefes hedeflenebilir. Bu mekanik bir kural değildir; simgenin gerçek boyutta okunması karar ölçütüdür.

### 13.2 Önerilen on iki simge

| Koleksiyon | Simge önerisi | Ayırt edici özellik | Kaçınılacak biçim |
|---|---|---|---|
| Günlük olumlamalar | Açık kalp | Yumuşak, tek kontur | Parıltı ve çoklu kalp |
| Azim & Dayanıklılık | Yükselen üç basamak | Düzenli ilerleme | Finans grafiği oku |
| Disiplin & Odak | Dört köşe odağı + merkez nokta | Toplanma ve dikkat | Hedef tahtasında çok sayıda halka |
| Özgüven & Cesaret | Açık kemer/eşik | Bir adım atma çağrışımı | Savaş kalkanı veya kılıç |
| Filozoflar | Sade sütun | Yeni marka ile düşünsel bağ | Ayrıntılı Korint başlığı |
| Tasavvuf & Doğu | Merkeze yönelen tek açık kıvrım | İçeri yönelme | Belirli geleneği bütün koleksiyona mal eden işaret |
| İnanç | Açık iki el | Kapsayıcı dua/düşünme çağrışımı | Tüm inançları tek dinî sembolle temsil |
| Spor & Beden | Dengeli hareket çizgisi | Beden hareketi | Aşırı fitness veya yarış amblemi |
| İş & Başarı | Sade iş çantası | İş yaşamını hızlı tanıma | Para işareti, taç |
| İlişkiler | Birbirine bağlanan iki halka | Bağ kurma | Olumlamalardaki kalbin aynısı |
| Zihin & Huzur | Tek yatay dalga | Sakinlik ve süreklilik | Çoklu su çizgileri ve gün batımı resmi |
| Öğrenme & Gelişim | Açık kitap | Öğrenme ve merak | Mezuniyet şapkasıyla daraltılmış anlam |

Bu tablo ikon çizimi için brief'tir; ikonlar üretilmiş veya kullanıcılar tarafından doğrulanmış değildir. Özellikle açık el, iç kıvrım ve hareket çizgisi gerçek boyutta anlaşılabilirlik karşılaştırması gerektirir.

### 13.3 Bordo uygulaması

İlk tercih, koleksiyon ikonunun tamamını aynı koyu bordo çizgiyle çizmek. İkon çok küçük olduğu için her birine iki ayrı renk yerleştirmek çoğu durumda gereksiz ayrıntı üretir.

İki renk yalnız marka logosunda veya açık bir anlam sağlayan tek simgede düşünülebilir. Her ikonun yüzde 20'sini bordo, kalanını siyah yapmaya çalışmak çizim sistemini yapaylaştırır.

### 13.4 Navigasyon ile fark

Navigasyon ikonları işlevsel ve çok sık kullanılan işaretlerdir. Koleksiyon ikonları ise taramayı destekler. Navigasyona sütun, yaprak ve kitap ekleyerek anlamları değiştirme.

Bugün yükseliş, Keşfet pusula, Senin profil olarak kalabilir. Aktif navigasyon ikonu mürekkep, üst çizgi bordo. Marka karakterini güçlendirmek için zaten çalışan bir navigasyonu tekrar kurmak gerekmiyor.

### 13.5 Anlaşılabilirlik kontrolü

İkon tek başına koleksiyonun tek tanımlayıcısı olmamalı. Başlık her zaman görünür kalır. Ekran okuyucu dekoratif ikonu ayrıca okumaz; kartın başlığı ve durumu bir bütün olarak açıklanır.

Küçük bir kullanıcı kontrolünde ikonun hangi koleksiyona ait olduğu sık karıştırılıyorsa, daha karmaşık çizim yerine daha tanıdık bir metafor seçilmeli. Bu rapor “premium” görünümü tanınırlığın önüne koymaz.

## 14. Durum sistemi: marka, seçim, erişim ve odak

### 14.1 Birbirinden ayrılması gereken durumlar

- **Aktif sekme:** Kullanıcının baktığı görünüm.
- **Seçili konu:** Kullanıcının tercih listesinde olan konu.
- **Erişime açık konu:** İçeriği kullanılabilen konu.
- **Bildirim teslimatı:** Hatırlatmanın açık/kapalı olması ve gerekli izinler.
- **Basılı durum:** Parmağın kontrol üzerinde olduğu kısa an.
- **Odak:** Klavye veya erişilebilirlik aracıyla gelinen kontrol.

Bu durumlar aynı değildir. “Açık” konu otomatik olarak “seçili” olmamalı; “seçili” konu da otomatik olarak “bildirim gönderiliyor” anlamına gelmemeli.

### 14.2 Koleksiyon durum matrisi

| Durum | Yüzey | İkon | Alt bilgi | Etkileşim |
|---|---|---|---|---|
| Hiç seçili alt konu yok | Açık taş | Koyu bordo | Gerçek konu sayısı | Ayrıntıya gider |
| Bazı alt konular seçili | Aynı açık taş | Koyu bordo | Sayı + tikli seçili konu özeti | Ayrıntıya gider |
| Bütün alt konular seçili | Aynı açık taş | Koyu bordo | Tüm seçili konu sayısı | Ayrıntıya gider |
| Basılı | Kısa, hafif bordo etkileşim katmanı | Aynı | Aynı | Bırakıldığında geçiş |
| Odaklı | Kontrastlı dış odak çizgisi | Aynı | Aynı | Standart odak davranışı |

Seçili kartın tamamını pembe yapmamak bilinçli tercihtir. Böylece çok sayıda konu seçildiğinde Keşfet yeniden pembe bir ızgaraya dönüşmez.

### 14.3 Konu satırları

Mevcut düz konu listesi korunmalı. Koleksiyonların kart olması, bütün alt konuların da kart olması gerektiği anlamına gelmez.

Açık konu: başlık ve içerik sayısı. Seçili konu: küçük tik ve doğru kısa durum metni. Kilitli konu: küçük kilit veya açık “Kilitli · Önizleme” ifadesi. Başlığın tamamını soluklaştırarak okunamaz yapma.

Satıra dokunmak yine ayrıntı açmalı. Bildirim seçiminin satır tıklamasıyla gizlice değişmesi bu refinement çalışmasının parçası olamaz.

### 14.4 “Önerilen” işareti

Mevcut veri gerçekten bir koleksiyonu önerilen olarak tanımlamıyorsa, tasarım çeşitliliği için “Sana özel” veya “Önerilen” rozeti eklenmemeli.

Gerçek bir öneri varsa bile seçili tikinden farklı bir işaret ve metin kullanılmalı. Ancak yeni öneri mantığı kurmak bu raporun kapsamı değildir.

## 15. Yaratıcı fakat kontrollü yüzey ayrıntıları

### 15.1 Bordo sis

Öneri: yalnız Keşfet başlığının sağ/üst kenarında veya ana ekrandaki heykel gerisinde. Aynı ekranda iki ayrı sis bölgesi yok.

Uygulama fikri, yüksek kontrastlı bir resim yerine yavaş ton kaybı olan geniş bir geçiştir. Rastgele nokta, damla veya fırça izi görünmemeli. Sonuç temiz bir sayfanın ışığı gibi algılanmalı.

**Karar:** İsteğe bağlı, son aşama. Ana renk düzeni kurulmadan eklenmez.

### 15.2 Pigment dokusu

Tozlu pigment etkisi küçük ekranlarda kir veya sıkıştırma hatası gibi görünebilir. Bu nedenle ilk uygulamada parçacıklı doku önerilmiyor.

Yalnız çok hafif, sabit ve bütünlüklü bir ton geçişi yeterliyse gerçek doku dosyası kullanılmamalı. Böylece ekran sade kalır ve farklı yoğunluklardaki raster ayrıntılarla uğraşılmaz.

**Karar:** İlk uygulamada uygulanmayacak.

### 15.3 Mermer hissi

Mermer hissi damar resmi koymadan da üretilebilir: sıcak açık zemin, ölçülü yüzey farkı, düşük yoğunluklu heykel, serif tipografi ve ince çizgiler.

Her kartın üzerine damar dokusu koymak kartları okunması gereken küçük afişlere dönüştürür. Kullanıcının istediği taş hissi, kartların açıklamalarını bastırmamalı.

**Karar:** Nötr taş yüzeyi uygulanacak; görünür mermer damarları uygulanmayacak.

### 15.4 Kenar ışığı

Seçili küçük bilgi alanının üst kenarında çok ince bordo ton farkı denenebilir. Ancak aynı bileşende zaten tik, metin ve renk varsa ek kenar ışığına gerek yok.

**Karar:** İlk uygulamada gereksiz. Renk düzeni yeterli kalmazsa yalnız tek bileşen üzerinde karşılaştırılabilir.

### 15.5 Mikro desen

Her koleksiyona ayrı desen önerilmiyor. Yeni ikon sistemi zaten karakter farkı sağlayacak. Desenler eklenirse hem görsel dil hem bakım yükü artar.

**Karar:** Kart bazlı mikro desen uygulanmayacak.

### 15.6 Bilinçli dekor eksiltme

Bir dekor yalnız yakınlaştırınca fark ediliyorsa, telefonda işe yaradığı varsayılmamalı. İki sürüm yan yana normal ölçekte karşılaştırıldığında katkısı anlaşılamıyorsa çıkarılabilir.

Amaç her boş alana bir şey eklemek değil; az sayıda ayrıntının güvenilir biçimde çalışmasıdır.

## 16. Alt navigasyonun rafine edilmesi

Mevcut boyutlar büyük ölçüde korunmalı: 76 dp asgari içerik alanı, sistem inset'i ayrıca, 24 dp ikon, 12/16 sp etiket ve 22 × 2 dp aktif çizgi.

Önerilen aktif görünüm:

- İkon mürekkep.
- Etiket yarı kalın mürekkep.
- Üst çizgi koyu bordo.
- Arkada dolu kapsül yok.

Pasif görünüm:

- Aynı çizgi kalınlığında okunur gri ikon.
- Normal ağırlıkta gri etiket.
- Görünmez aktif çizgi alanı yerini korur; ikon yukarı/aşağı sıçramaz.

Aktif ikonun içine ayrıca bordo bir parça eklemek ancak küçük boyutta temiz görünüyorsa denenmeli. İlk tercih mevcut kısa çizgiyi korumaktır. Paylaş ve Keşfet'e eklenen renkler navigasyonun da yeniden boyanmasını gerektirmez.

Çizgi ile ikon arası 4–6 dp; ikon ile etiket arası 4–6 dp. Üç hedefin yatay merkezi ve etiket tabanları eşleşmeli.

## 17. Diğer ekranlara sınırlı yayılım

### 17.1 Planım

Konu etiketleri mevcut nötr yüzey ve küçük bordo çizgiyi koruyabilir. Seçilmiş konu sayısı veya başlıklar topluca bordoya çevrilmemeli.

Ana ekrandaki Planım bağlantısı brandInk veya yalnız bordo okla güçlendirilebilir. İkisini aynı anda koyulaştırmak zorunlu değil; ana ekranda ilk karşılaştırma yalnız okun vurgulanmasıyla yapılmalı.

### 17.2 Senin

Profil başlığı açık zeminde kalmalı. Haftalık durum noktası gibi küçük vurgular aynı bordo ailesine bağlanabilir. İstatistik sayıları mürekkep kalmalı.

Bu sürümde yeni istatistik, yeni alışkanlık kartı veya yeni yolculuk modülü önerilmiyor.

### 17.3 Kaydedilenler

Kaydedilmiş kalp veya ayraç bordo olabilir. Sözler, kaynaklar ve liste yüzeyleri nötr kalmalı. Listeye çok sayıda pembe kart eklenmemeli.

### 17.4 Paylaşım stüdyosu

Bu çalışma stüdyodaki arka plan koleksiyonlarını yeniden renklendirmez. UI kontrol yüzeyiyle kullanıcının seçtiği paylaşım görseli farklı katmanlardır.

Ana ekrandaki Paylaş düğmesinin bordo olması, dışa aktarılan tüm söz görsellerinin bordo olması anlamına gelmez.

### 17.5 Onboarding

Yeni dosya onboarding'in baştan yazılmasını istemiyor. Mevcut akış korunmalı. Yalnız merkezi token veya ikon değişikliği burada görünüyorsa görsel uyum kontrolü yapılır.

Birincil onboarding düğmelerinin hepsini yeniden bordoya çevirmek bu rapora aykırıdır.

## 18. Koyu tema ve mevcut palet tercihi

Koyu temada açık temanın bordo rengi aynen kullanılmamalı. Mevcut açık bordo #DBB6C0 gibi içerik tonları, koyu seçili yüzeylerle eşlenebilir.

Paylaş için başlangıç: koyu, düşük doygunluklu seçili yüzey #302529; açık bordo ikon/metin #DBB6C0. Ana zemin ve söz mürekkep/açık metin düzeni aynı kalır.

Aura koyu temada daha kolay “ışıklı leke” gibi görünebilir. İlk uygulamada kapalı tutulması önerilir. Açık tema için başarılı olan efektin koyu temada zorunlu karşılığı yoktur.

Mevcut MONO tercihi ayrı bir kullanıcı seçimidir. Kullanıcı özellikle nötr palet seçmişse bu tercihi kaldırıp her yere bordo uygulamak doğru olmaz. Varsayılan ve Bordo paletleri yeni renk gramerini taşımalı; MONO aynı durumları şekil, çizgi ve nötr yüzeyle anlatabilmelidir.

Bu, marka tutarsızlığı değil; mevcut görünüm tercihine saygıdır. Yeni palette eski kullanıcı seçiminin anlamı sessizce değiştirilmemeli.

## 19. Mikro etkileşimler

| Etkileşim | Önerilen davranış | Kaçınılacak davranış |
|---|---|---|
| Sekme değiştirme | Kısa renk/çizgi geçişi; mevcut içerik geçişi korunur | Kartların zıplayarak yeniden dizilmesi |
| Kart basma | Hafif, kısa etkileşim katmanı | Kartın büyük ölçek değişimi |
| Kaydet | Biçim ve durum metni değişimi; mevcut tek haptik | Çoklu titreşim, uzun kalp patlaması |
| Paylaş | Hızlı basılı durum, mevcut açılış akışı | Sahte yükleme veya gecikme |
| Arama odağı | Sakin bordo sınır | Sürekli parlayan çerçeve |
| Seçim işareti | Tik ve kısa metin güncellemesi | Geçici başarı rozetinin sayfayı itmesi |

Mevcut 140–180 ms renk geçişleri yeterli başlangıçtır. Animasyon sürelerini sırf daha “premium” görünmesi için uzatma.

Sistem hareket azaltma veya animasyon kapatma tercihinde sonuç anında veya çok kısa görünmeli. Dekor katmanları zaten sabit olmalı.

Bir karta basma, koleksiyonun seçilmesi değildir. Etkileşim katmanı kısa sürede kaybolmalı; sonrasında kalıcı seçili durum yalnız gerçek veriden gelmeli.

## 20. Erişilebilirlik ve duyarlı yerleşim şartnamesi

### 20.1 Dokunma alanları

İkonun görünen boyutuyla dokunma alanı ayrı düşünülmeli. 20 dp ikon, 48 dp hedefin içinde kalabilir. Kompakt bildirim özeti ve üç nokta bu nedenle daha küçük dokunma hedeflerine dönüştürülmemeli. [Android — Compose accessibility defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults)

### 20.2 Ekran okuyucu

Koleksiyon tek anlamlı hedef olarak okunmalı: adı, konu sayısı, varsa seçili alt konu sayısı ve ayrıntı açma işlevi.

Örnek: “Azim ve Dayanıklılık. 9 konu. 2 konu seçili. Koleksiyonu aç.”

İkon adı ayrıca “basamaklar” diye okunmamalı. Dekoratif sis ve heykel erişilebilirlik ağacına girmemeli.

### 20.3 Büyük yazı

Yüzde 100, 130, 150 ve 200 ölçeklerde kontrol planlanmalı. En kritik alanlar: Paylaş/Kaydet metinleri, “Seçtiklerim” sekmesi, uzun koleksiyon başlıkları ve bildirim özeti.

Kart açıklamasına koşulsuz maxLines = 2 eklemek önerilmez. Tasarım hedefi normal ölçekte iki satır olabilir; içerik daha uzunsa yükseklik artmalı veya düzen tek sütuna geçmeli.

### 20.4 Sınır ve durum kontrastı

Hairline dekor olarak hafif kalabilir. Buna karşılık odak, seçili tik ve kontrolü tanımak için gerekli sınır yeterince görünür olmalı.

Çizgi inceldikçe gerçek cihazda görünürlük zayıflayabilir. Özellikle 1 dp'nin altına inen işlevsel çizgilerden kaçınılmalı. Bu raporun görsel inceliği “neredeyse görünmeyen kontrol” anlamına gelmez.

### 20.5 Ekran boyutları

320, 360 ve 411 dp genişlikler temel karşılaştırma noktalarıdır. Geniş telefonda yazı ve kartlar sınırsız yayılmamalı; mevcut içeriğin ölçüsü korunmalı.

Üç düğmeli Android navigasyonu ile hareketle navigasyon ayrı kontrol edilmeli. Alt boşluk bir sistem inset'i ve bir uygulama boşluğu olarak iki kez büyütülmemeli.

### 20.6 Düşük parlaklık ve gri tonlama

Düşük parlaklıkta kart yüzeyi kaybolabilir; bu durumda başlık, yön oku ve durum işareti hâlâ anlaşılır olmalı.

Gri tonlamada aktif sekme çizgisi, seçili tik ve kaydedilmiş kalp biçimi anlaşılır kalıyorsa renk sistemi daha güvenilir çalışır. Gri tonlama estetik kararın yerine geçmez, durum anlatımını kontrol eder.

## 21. Karşılaştırılacak üç tasarım seviyesi

### A — İşlevsel bordo, önerilen ilk uygulama

Paylaş açık bordo; aktif Keşfet sekmesi koyu bordo; koleksiyon ikonları ayrışmış; gerçek seçim bilgisi eklenmiş; kart yüzeyi hafif belirgin. Aura yok.

Avantajı: en az görsel riskle marka gücünü artırır. Mevcut güçlü yapıyı korur. Kullanıcının “büyük redesign istemiyorum” talebine en yakın seçenek.

### B — A düzeni + tek atmosfer

A'nın üzerine yalnız bir başlık veya heykel atmosferi. Diğer her şey aynı.

Avantajı: klasik kimliği biraz daha hissedilebilir yapabilir. Riski: düşük kontrastlı pembe leke veya gereksiz dekor algısı.

### C — Çoklu yüzey ve doku

Her kartta pembe ton, farklı mikro desen, hem başlıkta hem heykelde aura ve çok sayıda bordo kontrol.

**Önerilmiyor.** Bu seçenek yeni brief'teki kontrollü ve rafine kullanım hedefini aşar; önceki renk salınımını yeniden başlatır.

Karar: A temel kabul edilmeli. B ancak aynı koşullardaki karşılaştırmada katkısı açıkça görülürse seçilmeli. C uygulamaya alınmamalı.

## 22. Uygulama sırası ve değişiklik haritası

Bu bölüm sonraki kod çalışmasına yol gösterir; bu rapor hazırlanırken aşağıdaki değişiklikler yapılmadı.

### Aşama 1 — Renk rolleri ve ana eylem

Merkezi palete gerektiği kadar ayrı rol ekle. Paylaş'a brandWash/brandInk eşlemesini uygula. Kaydet'in okunur sınırını koru. Aktif Keşfet sekmesini düzenle.

Başarı ölçütü: başka ekranların ana düğmeleri istemeden bordo olmamış; ana sözün görünümü değişmemiş.

### Aşama 2 — Koleksiyon karakteri

İkon eşlemelerini ayrıştır. Kart yüzeyini ve açıklama ölçüsünü karşılaştır. Gerçek seçili alt konu sayısını, mevcut veriden türetilen görsel özet olarak göster.

Başarı ölçütü: kartların tamamı aynı aileden; seçim ve erişim karışmıyor; bütün metin okunuyor.

### Aşama 3 — Boşluk düzeltmesi

Ana ekranın üst boşluğunu aynı sözlerle karşılaştır. Kaynak seçicisi ile kategori arasını kontrollü aralıkta tut. Eylemleri ve navigasyonu taşıma.

Başarı ölçütü: kısa sözde kopukluk azalıyor; uzun sözde veya büyük yazıda içerik kesilmiyor.

### Aşama 4 — İsteğe bağlı atmosfer

Yalnız A düzeni değerlendirildikten sonra tek aura adayını karşılaştır. Kullanıcıya seçilecek iki görsel varsa tek fark aura olmalı; renk ve tipografi aynı anda değiştirilmemeli.

Başarı ölçütü: söze odak korunuyor. Sonuç fazla dekoratifse aura kaldırılıyor.

### Aşama 5 — Sınırlı yayılım ve doğrulama

Planım, Senin, Kaydedilenler ve onboarding'de merkezi token değişikliğinin istenmeyen etkisini kontrol et. Yeni özellik geliştirme.

### Kaynak alanları

| Alan | Beklenen UI sorumluluğu |
|---|---|
| Paletler / Tasarım | Renk rolleri ve karşılıkları |
| AnaEkran | Paylaş/Kaydet, üst mesafeler, isteğe bağlı aura |
| KategorilerEkrani | Aktif sekme, özet, kart yüzeyi, durum |
| İkonlar | Yeni koleksiyon simgelerinin ortak aileye eklenmesi |
| KlasikDil | Gerekirse tek dekor katmanı ve ortak yüzey dili |
| İskelet | Mevcut navigasyon işaretinin korunması |
| UI kontrolleri | Metin, durum, odak ve dar ekran denetimi |

İçerik derleyicisi, bildirim zamanlayıcısı, erişim motoru, ödeme veya paylaşım üretim algoritması bu değişiklik haritasının dışındadır.

## 23. Kabul testi matrisi

Bu tablo yapılmış testlerin sonucu değil, uygulama tamamlandığında kullanılacak denetim planıdır.

| Senaryo | Kontrol | Kabul koşulu |
|---|---|---|
| Kısa Türkçe söz | Üst boşluk | Kaynak seçicisi ile kategori kopuk görünmez. |
| Uzun Türkçe söz | Okuma alanı | Tam metin erişilir, eylemler sabit kalır. |
| Kaydet sonrası | Durum | Biçim + metin değişir, Paylaş'la karışmaz. |
| İlk/son söz | Oklar | Devre dışı durum doğru ve anlaşılır. |
| Keşfet aşağı kaydırma | Arama | Arama erişilebilir kalır. |
| Uzun koleksiyon başlığı | Sarma | Başlık kesilmez; komşu karta taşmaz. |
| 0 seçili konu | Özet | Yanlış bildirim vaadi yoktur. |
| 1 veya çok seçili konu | Kart durumu | Gerçek sayı gösterilir; tüm kart seçimi ima edilmez. |
| Bildirimler kapalı | Metin | Seçimler kayıtlı olsa da teslimat açıkmış gibi konuşulmaz. |
| Kilitli konu | Ayrım | Kilitli ile seçilmemiş birbirine karışmaz. |
| Arama ve geri dönüş | Konum | Sorgu/filtre ve beklenen konum korunur. |
| 320 dp / yüzde 200 | Hedefler | Kaydet, Paylaş ve navigasyon erişilebilir. |
| Koyu tema | Renk | Koyu bordo üzerinde koyu yazı oluşmaz. |
| MONO | Tercih | Kullanıcının nötr görünüm seçimi korunur. |
| Ekran okuyucu | Anlam | Kart adı ve durumu tek, anlaşılır açıklama verir. |
| Hareket azaltma | Geçiş | Etkileşim bekletilmez, dekor hareket etmez. |
| Aura açık/kapalı | Odak | Aura sözü geri plana itmez. |
| Üç düğmeli sistem nav | Alt alan | Çakışma veya çift boşluk yoktur. |

### Görsel karşılaştırmada sabit tutulacaklar

Aynı söz, aynı seçili konu sayısı, aynı telefon genişliği, aynı yazı ölçeği, aynı tema ve aynı kaydırma konumu kullanılmalı. Bunlar değişiyorsa iki ekran arasındaki farkın tasarım kararından mı içerikten mi geldiği anlaşılamaz.

Normal boyut değerlendirmesi önce yapılmalı. Yakınlaştırılmış görüntü yalnız çizgi ve maske kusurlarını görmek için kullanılmalı; estetik karar yalnız yakınlaştırılmış görüntüden verilmemeli.

## 24. Öncelikler ve tamamlanma tanımı

### Öncelik 1 — Zorunlu rafinasyon

- Paylaş'ın açık bordo yüzeyi ve koyu bordo içeriği.
- Aktif Keşfet sekmesinin renk grameri.
- Koleksiyon ikonlarının ayrışması.
- Kart yüzeyi ve açıklama okunurluğu.
- Gerçek seçim durumunun küçük fakat açık ifadesi.
- Ana ekran üst boşluğunun sınırlı düzenlenmesi.

### Öncelik 2 — Tutarlılık

- Bildirim özeti durum metinleri.
- Kaydet'in kayıtlı görünümü ve konturu.
- Odak ve basılı durumların aynı ailede olması.
- Koyu tema ve MONO karşılıkları.
- Yeni logo dosyası mevcutsa küçük boyutta gerçek marka eşleştirmesi.

### Öncelik 3 — Koşullu dekor

- Tek başlık atmosferi veya heykel aurası.
- Yalnız karşılaştırmada değer katan çok hafif ton geçişi.

Tamamlanma, bütün dekor önerilerinin eklenmesi değildir. Öncelik 1 ve 2'nin ölçülü, okunur ve tutarlı çalışması; Öncelik 3'ün ise kullanıldıysa gerekçeli olmasıdır.

## 25. Uygulanacak / uygulanmayacak karar listesi

| Karar | Sonuç |
|---|---|
| Mevcut ana ekran düzenini koru | Uygulanacak |
| Paylaş'a açık bordo yüzey ver | Uygulanacak |
| Aktif Keşfet yazısını koyu bordo yap | Uygulanacak |
| Her koleksiyona ayrı, tutarlı küçük ikon | Uygulanacak |
| Kartları açık taş yüzeyle biraz ayır | Karşılaştırılarak uygulanacak |
| Açıklamayı 13/19 sp dene | Karşılaştırılarak uygulanacak |
| Seçili alt konu sayısını göster | Mevcut veriden doğru türetilerek uygulanacak |
| Bildirim kapalı durumunu doğru anlat | Uygulanacak |
| Sis veya aura | Koşullu |
| Yeşili bütün UI'ya yay | Uygulanmayacak |
| Heykeli bordo filtreyle boya | Uygulanmayacak |
| Her karta farklı desen veya resim | Uygulanmayacak |
| Bordo başlık bandına geri dön | Uygulanmayacak |
| Bütün ana düğmeleri bordo yap | Uygulanmayacak |
| Onboarding'i yeniden kur | Uygulanmayacak |
| Fiyat, Pro, reklam veya bildirim iş kurallarını değiştir | Uygulanmayacak |
| Renk oranını tutturmak için boş alanları boya | Uygulanmayacak |

## 26. Tasarımcı veya yapay zekâ için uygulama brief'i

Aşağıdaki bölüm ileride uygulama görevi verilirken bu raporla birlikte kullanılabilir:

> Ascend'in mevcut 8.3 açık zeminli, serif söz odaklı arayüzünü koruyarak bu rapordaki Öncelik 1 ve 2 maddelerini uygula. Büyük redesign yapma. Ana sözün font ailesini, ekran akışını, heykeli ve üçlü navigasyonu koru.
>
> Bordoyu marka, aktif konum ve gerçek seçim için ayrı rollerle kullan. Paylaş açık bordo yüzey ve koyu bordo içerik taşısın. Kaydet açık zemin ve okunur konturla ayrı kalsın. Aktif Keşfet sekmesi koyu bordo yazı ve kısa çizgiyle belirtilsin.
>
> Koleksiyon kartlarına rastgele renkler verme. Aynı nötr taş yüzeyi ve aynı ikon çizgi sistemi içinde her koleksiyonu farklılaştır. Seçili alt konu varsa gerçek sayıyı tikli kısa bilgiyle göster; koleksiyonun tümü seçilmiş gibi davranma. Satır tıklaması detay açmaya devam etsin.
>
> Seçili konu ile bildirim teslimatını birbirine eşitleme. Mevcut durum verisi neyi doğruluyorsa yalnız onu yaz. Gerçek öneri verisi yoksa “Sana özel” rozeti üretme.
>
> Yeni logo, sütun ve yeşil yapraklı resmî varlık olarak ele alınmalı. Gerçek dosya yoksa logoyu uydurma. Yükselen çizgi işlevsel navigasyon ikonu olarak kalabilir.
>
> Sis ve aura zorunlu değildir. Önce işlevsel renk düzenini tamamla. Sonra tek bir düşük yoğunluklu dekor adayını, aynı ekranın dekorsuz hâliyle karşılaştır. Fazla dramatikse çıkar.
>
> Sözleri, kategorileri, erişim kurallarını, ödeme veya reklam akışını ve bildirim motorunu değiştirme. Büyük yazı, dar ekran, koyu tema, MONO, ekran okuyucu ve mevcut durum koruma davranışlarını denetle.
>
> Sonuçta değişen alanları, bilinçli olarak uygulanmayan dekor önerilerini ve gerçekten yapılan kontrolleri açıkça raporla. İncelenmeyen bir cihazı veya ölçülmeyen renk oranını doğrulanmış gibi sunma.

## 27. Son kontrol listesi

- [ ] İlk bakışta ana söz en güçlü öğe mi?
- [ ] Bordo fark ediliyor ama sayfayı kaplamıyor mu?
- [ ] Paylaş yeterince görünür ve Kaydet'ten ayrı mı?
- [ ] Menü daha sessiz bir üçüncü eylem mi?
- [ ] Aktif sekme renk dışında çizgiyle de anlaşılıyor mu?
- [ ] Kart ikonları gerçekten birbirinden farklı mı?
- [ ] İkonların çizgi ağırlığı ve optik ölçüsü aynı aileden mi?
- [ ] Seçili kart bilgisi gerçek alt konu sayısından mı geliyor?
- [ ] Kartın tamamı yanlışlıkla toplu seçim kontrolü gibi görünmüyor mu?
- [ ] Bildirim kapalıyken özet doğru mu?
- [ ] Açıklamalar normal boyutta rahat okunuyor mu?
- [ ] Büyük yazıda başlık ve açıklamalar kaybolmuyor mu?
- [ ] Kaynak seçicisi ile kategori arasındaki boşluk kontrollü mü?
- [ ] Heykelin dosyası ve görünümü korunmuş mu?
- [ ] Aura varsa bağımsız katman ve gerçekten gerekli mi?
- [ ] Aynı ekranda birden fazla dekoratif renk bölgesi yok mu?
- [ ] Yeşil yalnız gerçek marka varlığında mı?
- [ ] Koyu tema ve MONO tercihi anlamını koruyor mu?
- [ ] Renk tokenları başka ekranları istemeden boyamıyor mu?
- [ ] Değişiklikler UI/UX sınırında kalmış mı?

## 28. Kaynaklar ve dayanaklar

### Birincil proje dayanakları

1. Kullanıcının 10 Eylül 2026 tarihli Ascend_Bordo_UI_UX_Refinement_Promptu.md dosyası.
2. Konuşmada sağlanan tasarım referansları ve önceki ekran görüntüleri.
3. [Ascend 9911223 kaynak noktası](https://github.com/yalnizfahrettin06-tech/Ascend/tree/9911223f110aa9d52abfe896fc055e182ad9299a): Paletler, AnaEkran, KategorilerEkrani, Kategoriler ve İskelet alanları bu turda okunarak karşılaştırıldı.

### Erişilebilirlik dayanakları

4. [W3C — Contrast Minimum](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum): metin kontrastı.
5. [W3C — Non-text Contrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html): anlamlı görsel işaretler ve kontrol durumları.
6. [W3C — Use of Color](https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html): rengin tek bilgi taşıyıcısı olmaması.
7. [Android — Accessibility API defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults): dokunma hedefleri ve Compose davranışları.

Bu kaynaklar Ascend'in estetik seçimini kanıtlayan kullanıcı araştırmaları değildir. Estetik kararlar brief, mevcut yapı ve tasarım değerlendirmesinden; erişilebilirlik sınırları ilgili birincil kaynaklardan gelir. Rapordaki ikon metaforları, aura değerleri ve yüzey önerileri uygulama öncesi/sonrası görsel karşılaştırmayla doğrulanacak adaylardır.

---

**Raporun önerdiği nihai yön:** Önce açık bordo Paylaş, net aktif sekme, anlamlı koleksiyon ikonları ve gerçek seçim bilgisi. Ardından ölçülü boşluk düzeltmesi. Dekor ancak bunlar tamamlandıktan sonra ve gerçekten katkı sağlıyorsa.
