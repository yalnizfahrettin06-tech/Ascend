# Ascend — Soft Minimal UI/UX Yeniden Tasarım Promptu

## Rolün ve görevin

Sen kıdemli bir mobil ürün tasarımcısı ve Jetpack Compose UI mühendisisin. Ascend adlı motivasyon, olumlama ve alıntı uygulamasının mevcut arayüzünü yeniden tasarlayacaksın. Bu görev yalnızca **UI/UX tasarım mimarisi, görsel hiyerarşi, yerleşim, bileşen davranışı, etkileşim geri bildirimi ve erişilebilirlik** kapsamındadır.

Mevcut ekranları yüzeysel biçimde renklendirme. Arayüzü, aşağıdaki teşhis ve hedeflere göre sistematik biçimde yeniden kur. Uygulamanın iş kurallarına, içerik modeline, kategori kimliklerine, bildirim motoruna, ödeme/Pro mantığına, reklam akışına, kayıtlı verilere veya paylaşım üretim altyapısına bu görev kapsamında müdahale etme.

## Ana tasarım hedefi

Ascend; tam genişlik bordo şeritler ve büyük bordo düğmeler kullanan sert bir uygulama gibi görünmemeli. Ana kimliği şu dört unsur oluşturmalı:

1. Sakin ve sıcak bir açık zemin.
2. Güçlü fakat ölçülü siyah/mürekkep tipografisi.
3. Bordoyu yalnızca seçili durum, ince çizgi, küçük işaret, odak halkası ve mikro vurgu olarak kullanan zarif bir sistem.
4. Roma sütunu, büst, yükselen çizgi ve mermer dokusunu çok düşük yoğunlukta kullanan özgün Ascend imgesi.

Arayüzün hissi **soft, minimal, yumuşak, editoryal, ferah ve kişisel** olmalı. “Soft” sözcüğünü her şeyi soluklaştırmak şeklinde yorumlama. Metin okunurluğu güçlü, dokunma alanları belirgin ve etkileşim durumları anlaşılır kalmalı.

## Değişmez kapsam sınırları

Aşağıdaki ürün kararlarını koru:

- Bugün, Keşfet ve Senin olmak üzere üç ana gezinme hedefi.
- Mevcut 20 adımlı onboarding akışı ve soruların işlevleri.
- Hızlı başlama yolu, geri gitme ve soruları atlama davranışı.
- Mevcut kategori ve koleksiyon sayıları, kategori kimlikleri ve içerikler.
- Kullanıcının seçtiği konular, açık/kilitli konu ayrımı ve bildirim izni ayrımı.
- Mevcut Pro demo ve kategori açma davranışı.
- Kaydet, paylaş, kopyala, sesli okuma ve paylaşım stüdyosu işlevleri.
- Mevcut açık/koyu tema desteği ve kullanıcı verilerinin korunması.
- Mevcut uygulama ikonu. İkonu bu görevde yeniden tasarlama.

Bu görevde yeni sosyal özellik, hesap sistemi, istatistik modeli, abonelik teklifi, reklam biçimi, yeni onboarding sorusu, yeni kategori veya yeni paylaşım teması icat etme. Metinleri yalnızca görsel hiyerarşiyi düzeltmek için kısaltman gerekiyorsa anlamı ve işlevsel gerçeği koru.

## Mevcut ekranlardan çıkarılan temel problemler

### 1. Bordo renk yanlış ölçekte kullanılıyor

Mevcut tasarımda bordo, üst bölümün tamamını kaplayan kalın bir blok ve alt bölümde tekrar eden dev bir birincil buton olarak kullanılıyor. Aynı kuvvetli renk ekranın hem başında hem sonunda tekrarlandığı için içerik iki ağır plaka arasında sıkışıyor. Bordo bir ayrıntı veya marka imzası gibi değil, sayfanın büyük kısmını yöneten bir dolgu rengi gibi davranıyor.

Yeni sistemde:

- Tam genişlik, yüksek bordo başlık bantlarını kaldır.
- Bir ekranın yaklaşık yüzde 15–20'sini kaplayan koyu renk bloklar kullanma.
- Büyük birincil eylemlerde varsayılan dolgu olarak kömür/mürekkep rengini düşün.
- Bordoyu 1–2 dp çizgilerde, aktif sekme işaretinde, küçük ikon ayrıntısında, seçim halkasında, progress göstergesinde ve yüzde 4–10 opaklıklı yüzey tonlarında kullan.
- Aynı viewport içinde birden fazla büyük doygun bordo yüzey oluşturma.
- Bordo metni uzun paragraflarda kullanma; kısa etiket veya durum vurgusuyla sınırla.

### 2. Sert katman geçişleri var

Beyaz durum çubuğu, aniden başlayan bordo başlık ve ardından tekrar keskin beyaz içerik yüzeyi üç ayrı katman gibi görünüyor. Bu geçişlerde yumuşaklık, görsel bağ veya kontrollü boşluk bulunmuyor. Üst alandaki dekor çizgilerinin kesilmiş uçları da üretim hatası izlenimi yaratıyor.

Yeni sistemde:

- Sistem çubuğu ile uygulama başlığı aynı açık zemin ailesinde devam etsin.
- Başlık alanını beyaz/açık yüzey üzerinde oluştur; marka işaretini bordo mikro vurgu olarak kullan.
- Gerekirse başlığın altında çok ince bir ayırıcı veya yumuşak gölge kullan; sert renk kesintisi oluşturma.
- Dekor görsellerinin kırpılmış düz kenarları görünmesin. Maske ve opaklık geçişiyle yüzeye karışsın.
- Safe area ve status bar boşlukları tek kez tüketilsin; fazladan üst boşluk veya çakışma oluşmasın.

### 3. Boşluklar ferahlık yerine kopukluk yaratıyor

Karşılama ekranında başlık ve açıklama üst yarıda toplanırken alt yarıda büyük, işlevsiz bir boşluk kalıyor. Sabit alt eylem paneli de sayfadan kopuk bir ikinci katman gibi duruyor. Ana ekranda “Sana göre” kontrolü ile kategori arasında çok büyük boşluk var; sözün altındaki boşluk ise kontrol grubuna hizmet etmiyor.

Yeni sistemde:

- Boşluğu amaçlarına göre ayır: bölüm arası boşluk, öğe içi boşluk ve güvenli alt alan aynı değerle çözülmesin.
- Dikey yerleşimi `SpaceBetween` benzeri eşit dağıtımla kurma.
- İçerik kümesini optik merkeze göre konumlandır; ekranın matematiksel merkezine körü körüne bağlama.
- Kısa içerikte boşluğu tek bir dev alanda toplama; 24–48 dp aralığında kontrollü bölümlere dağıt.
- Sabit alt CTA üstünde en az 24 dp içerik güvenliği bırak; CTA alanını bağımsız beyaz bir duvar gibi göstermemek için yalnız gerekli yükseklikte tut.
- Uzun içerikte kaydırma ile erişim sağla; metni küçülterek veya CTA altında kırparak çözme.

### 4. Tipografi ölçekleri birbiriyle yarışıyor

Serif başlıklar ve sözler çoğu ekranda gereğinden büyük. Onboarding'de büyük başlık, numaralı editoryal blok, kategori etiketleri ve önizleme kartı aynı anda dikkat istiyor. Ana ekranda uzun söz altı satıra yayılarak büstle çarpışıyor ve eylemleri aşağı itiyor. Keşfet kartlarında serif başlıkların boyutu kartları gereksiz yere büyütüyor.

Yeni sistemde:

- Serif yalnızca duygusal/editorial metinlerde kullanılsın: ana söz, kısa bölüm başlığı ve seçilmiş koleksiyon adı.
- Navigasyon, açıklama, sayaç, buton ve durum metinlerinde sans kullan.
- Ana söz normal telefonda yaklaşık 34–42 sp aralığında, satır yüksekliği yüzde 108–116 olacak biçimde içerik uzunluğuna uyarlansın.
- Onboarding ana başlıkları yaklaşık 34–40 sp; Keşfet koleksiyon başlıkları yaklaşık 24–30 sp sınırında başlasın.
- Her ekranda en fazla üç belirgin yazı seviyesi bulunsun.
- Tamamı büyük harf ve yüksek harf aralığı yalnızca çok kısa eyebrow etiketlerinde kullanılsın.
- Metinler iki veya üç satırı aştığında kırpılmasın; bileşen yüksekliği veya yerleşim biçimi değişsin.
- Türkçe karakterlerin tüm font ağırlıklarında doğru görüntülendiğini doğrula.

## Tasarım sistemi

### Renk rolleri

Aşağıdaki renkler kesin zorunlu hex değerleri değil, sistemin hedef aralığıdır. Uygulamadaki mevcut merkezi tema yapısını kullan ve erişilebilir kontrastı ölçerek nihai değerleri belirle.

| Rol | Önerilen yön | Kullanım |
|---|---|---|
| Canvas | sıcak kırık beyaz, yaklaşık `#FCFAF8` | Ana ekran zemini |
| Surface | beyaz veya çok hafif sıcak gri, yaklaşık `#FFFFFF` / `#F7F4F3` | Kart, alan ve alt yüzey |
| Ink | siyaha yakın sıcak kömür, yaklaşık `#242022` | Ana metin ve birincil eylem |
| Ink secondary | sıcak nötr gri, yaklaşık `#6E676A` | Açıklama ve ikincil bilgi |
| Burgundy | mevcut markaya yakın `#6C2932` | Mikro vurgu ve seçili durum |
| Burgundy muted | yaklaşık `#8B5C64` | Küçük ikon/etiket, gerektiğinde |
| Burgundy wash | yaklaşık `#F5ECEE` | Seçili kart veya chip arka planı |
| Hairline | yaklaşık `#E8E1E3` | 1 dp ayırıcı ve sakin sınır |

Kurallar:

- Ekran yüzeyinin büyük çoğunluğu açık ve nötr kalsın.
- Burgundy wash yüzeyi, düz pembe kutu gibi görünmemeli; nötr yüzeye çok yakın olmalı.
- Mavi, turuncu, sarı veya altın arayüz vurgusu ekleme.
- Başarı, hata veya sistem durumları için semantik renk gerekirse marka rengine dönüştürme; erişilebilir ve ölçülü semantik renk kullan.
- Koyu temada bordoyu geniş zemin yapmak yerine küçük aktif durum vurgusu olarak koru.

### Köşe, çizgi ve gölge sistemi

- Büyük kartlarda 20–24 dp, küçük seçimlerde 14–18 dp, kapsüllerde tam yuvarlak biçim kullan.
- Her bileşeni kapsüle çevirme. Düz metin bağlantıları ve açık yüzey bölümleri de kullan.
- Kart sınırları 1 dp hairline olsun; seçili durumda 1.5–2 dp bordo sınır veya kısa bordo işaret kullanılabilir.
- Ağır drop shadow kullanma. Yalnız yükselti gerçekten gerekiyorsa düşük opaklık ve geniş blur kullan.
- İç içe üç ayrı yuvarlatılmış kutu oluşturma.
- Alt navigasyonun üstünde sert gri çizgi yerine yüzey ayrımı veya çok hafif gölge tercih et.

### Boşluk sistemi

4 dp tabanlı tutarlı bir sistem kur:

- 4 dp: mikro ikon/metin ayarı.
- 8 dp: yakın ilişkili öğeler.
- 12–16 dp: bileşen içi boşluk.
- 20–24 dp: kart ve ekran yatay kenarı.
- 28–32 dp: bölüm ayrımı.
- 40–48 dp: ana içerik kümeleri arasındaki büyük boşluk.

Ekranın genişliği uygunsa yatay kenar 24 dp, dar ekranda en az 16 dp olsun. Aynı ekranda rastgele 59, 73 veya 91 dp gibi bağlamsız boşluklar üretme.

### İkon sistemi

- Tüm navigasyon ve eylem ikonlarında aynı çizgi karakterini kullan.
- Görünen ikon 22–26 dp olabilir; dokunma hedefi en az 48 × 48 dp olmalı.
- Yükselen çizgi, keşif pusulası ve profil ikonlarının stroke kalınlığı optik olarak eşleşsin.
- Aktif durumu yalnız bordo renk ile anlatma; küçük üst çizgi, dolu/çizgisel varyant veya arka plan biçimi farkı da ekle.
- Dekoratif büst, sütun ve yaprak erişilebilirlik ağacında okunmasın.

## Ekran bazında yeniden tasarım

### A. Onboarding ortak iskeleti

Mevcut onboarding'in üstünde her adımda büyük bordo blok bulunuyor. Bu uygulamanın ana sertlik kaynağıdır.

Yeni ortak iskelet:

1. Sistem çubuğuyla birleşen açık zemin.
2. Üstte 56–64 dp yüksekliğinde sade bir app bar.
3. Solda küçük yükselen çizgi + `ascend` sözcük işareti veya geri düğmesi + sözcük işareti.
4. Sağda `1 / 20` gibi gerçek adım bilgisi, ikincil nötr renkte.
5. App bar altında 2 dp yüksekliğinde, segmentli veya kesintisiz ince progress. Tamamlanan bölüm bordo; kalan bölüm hairline.
6. İçerik alanı en fazla 24 dp yatay kenarla, açık zeminde.
7. Alt eylem alanı ekranın geri kalanını kaplamayan, güvenli alanı tüketen sade bir bölüm.

Progress çizgisinin uçlarında kesilmiş dekor parçaları görünmesin. İlerleme çizgisi içerik ölçümünden bağımsız, temiz ve tam hizalı olsun.

#### Onboarding başlangıç ekranı — mevcut Görsel 1

Sorunlar:

- Üst bordo blok gereğinden baskın.
- Ana metin kümesi yukarıda; ekranın orta-alt kısmında anlamsız büyük boşluk var.
- “20 küçük adım” bilgisi ana değer önerisi kadar görünür.
- Büyük bordo CTA ve bordo metin bağlantısı aynı ağırlık ailesini tekrar ediyor.
- Sütun dekoru neredeyse görünmez fakat kesilmiş bir leke gibi algılanıyor.

Yeni düzen:

- Başlık alanını açık tut; küçük bordo yükseliş çizgisi ve progress yeterli marka izi oluştursun.
- “Yeni bir başlangıç” eyebrow'u küçük ve nötr, yanındaki 24–32 dp çizgi bordo olabilir.
- “Kendi hızında. Bir adım yukarı.” başlığını iki veya üç kontrollü satıra yerleştir.
- Açıklamayı başlığa 16–20 dp yakınlaştır.
- 20 adım ve yerel saklama bilgisini büyük ayrı bölüm yerine küçük bir bilgi satırı veya iki kısa satır olarak sun.
- Sütunu sağ alt/orta bölgede yüzde 3–6 opaklıkla, yumuşak maskeli tek dekor olarak kullan. Başlığın okunurluğunu etkilemesin.
- Birincil `Yolunu oluştur` eylemi mürekkep dolgulu veya açık zemin üzerinde net konturlu olabilir; küçük bordo ok/işaret marka vurgusu olsun.
- `Hızlı başla, sonra kişiselleştir` ikincil metin eylemi, bordo yerine nötr mürekkep ve küçük bordo alt çizgi/ok kullanabilir.
- İçerik ile alt eylem arasında kalan boşluğu kompozisyona dağıt; dev boş alan bırakma.

#### Onboarding soru ekranları

- Tek seçimli ve çok seçimli sorularda aynı bileşen dili kullan.
- Seçenekler tam genişlik dev kapsüller yerine, içerik uzunluğuna göre 56–68 dp minimum yüksekliğinde yumuşak satırlar olsun.
- Varsayılan seçenek: açık yüzey + hairline sınır.
- Seçili seçenek: çok hafif bordo wash + 1.5 dp bordo sınır + sağda dolu olmayan, anlaşılır seçim işareti.
- Seçimi yalnız renk ile anlatma.
- Çoklu seçimde sayaç veya kısa yardımcı açıklama soruya yakın yer alsın.
- `Atla` üstte sakin bir metin eylemi olarak kalabilir; ana başlıkla yarışmasın.
- Geri dönüldüğünde önceki seçimler görünür kalsın.
- Klavye açılan isim ekranında CTA klavyenin arkasında kalmasın; içerik IME inset'iyle doğru yer değiştirsin.

#### Plan özeti — mevcut Görsel 2

Sorunlar:

- Eyebrow, büyük başlık, uzun açıklama, numaralı blok, büyük “Planındaki konular” başlığı, chip'ler ve önizleme aynı anda yüksek ağırlık taşıyor.
- `01` numarası gerçek bir sıralama sunmuyor ve görsel gürültü yaratıyor.
- Önizleme kartı sabit CTA altında kesiliyor; sözün tamamı ilk bakışta ulaşılamaz görünüyor.
- Chip'ler gereğinden iri ve pembe kutular gibi görünüyor.

Yeni düzen:

- `04 · Senin yolun` ve `01` gibi dekoratif numaralardan yalnız biri kalsın; tercihen ikisini de kaldır ve `Planın hazır` gibi işlevsel küçük etiket kullan.
- Ana başlığı 2 satırla sınırla, açıklamayı kısalt fakat gerçek sonraki iki adımı söylemeye devam et.
- Plan davranışı açıklamasını ikonlu küçük bilgi satırına dönüştür; ayrı numaralı editoryal blok yapma.
- Konuları kompakt, sarmalanan chip'lerde göster. Chip yüksekliği 36–40 dp; bordo wash çok hafif.
- İlk söz önizlemesini kart içinde tam okunabilir yap veya kartın kendisini kaydırılabilir kıl. Kaynak metni mutlaka sözle birlikte görünür olsun.
- Sabit CTA, önizleme kartının altını örtmesin. Scroll içeriğine alt padding ekle.
- Plan özeti bir “başarı/sonuç” hissi versin; uzun form metni gibi görünmesin.

#### Erişim açıklaması — mevcut Görsel 3

Sorunlar:

- Üç uzun numaralı paragraf onboarding akışını sözleşme metni gibi gösteriyor.
- Serif alt başlıklar ve büyük numaralar ekranı ağırlaştırıyor.
- Demo/reklam açıklamaları görsel olarak birincil onboarding içeriğiyle aynı seviyede.
- Ekrandaki sağ kenar paneli uygulama arayüzü değilse tasarım kararı olarak ele alınmamalı; test ekranında sistem overlay'lerini kapat.

Yeni düzen:

- Üç erişim biçimini kompakt, taranabilir üç satır veya üç küçük kart olarak sun.
- Her öğede küçük ikon, kısa başlık ve en fazla iki satır açıklama kullan.
- Büyük `01/02/03` tipografisini kaldır; sıra gerekliyse 24 dp küçük rozet kullan.
- “Ücretsiz”, “Tek tek aç” ve “Pro demo” ifadelerinin davranışını değiştirme; yalnız hiyerarşisini sadeleştir.
- Pro demo metnini küçük bir bilgi notu olarak tut. Onboarding'in ana satış ekranına dönüşmesine izin verme.
- Alt CTA'nın yüksekliğini 52–56 dp civarına indir; dev bordo dolgu yerine mürekkep dolgu ve küçük bordo vurgu kullan.

#### Bildirim izni — mevcut Görsel 4

Sorunlar:

- Son adım; büyük başlık, saat cümlesi, büyük önizleme kartı, iki açıklama ve iki eylem nedeniyle yoğun.
- Ana CTA yine geniş bordo blok.
- Bildirim kartı gerçek sistem bildirimiyle karışabilecek kadar büyük ama görsel hiyerarşisi yeterince net değil.

Yeni düzen:

- Üstte kısa başlık ve tek cümlelik kişisel plan özeti.
- Bildirim önizlemesini daha küçük, gerçek bildirime yakın yatay oranlı bir bileşen olarak sun.
- `Bildirimlere izin ver` eyleminden önce izin verilince ne olacağını tek kısa cümlede belirt.
- Cihaz ayarları bağlantısını ikincil satır eylemi yap; onboarding ana akışında gereğinden baskınlaştırma.
- Birincil CTA ile `Şimdilik bildirimsiz devam et` arasındaki ağırlık farkı açık olsun; ikisi de bordo metin/dolgu olmasın.
- İzin verilmediğinde uygulamanın kullanılabileceği bilgisi sakin ve kısa kalsın.
- CTA tıklandığında ancak Android izin diyaloğu gerçekten açıldığında bekleme durumu göster.

### B. Bugün ana ekranı — mevcut Görsel 5

Sorunlar:

- Tam genişlik bordo başlık ekran yüksekliğinin önemli bölümünü tüketiyor.
- `Sana göre` ile kategori arasında çok büyük boşluk var.
- Büst görseli sağ üstte çok büyük, yüz ve omuz kesilerek metinle rekabet ediyor.
- Uzun söz gereğinden büyük; satır kırılımları kompozisyonu aşağı doğru itiyor.
- Kaynak ile gezinme satırı arasında uzun, işlevsiz bir boşluk bulunuyor.
- Kaydet/Paylaş kapsülleri ve üç nokta alt navigasyona fazla yakın.
- Alt navigasyon, içerikten sert çizgiyle ayrılan ikinci bir beyaz panel gibi.

Yeni ekran yapısı:

1. Açık zemin üzerinde 56–64 dp sade marka satırı.
2. Solda küçük yükseliş işareti + `ascend`; sağda `Planım` metin eylemi.
3. Bordo yalnız yükseliş işaretinin bir parçasında, Planım yanındaki küçük okta veya 2 dp alt çizgide.
4. Hemen altında `Sana göre` kaynak seçicisi; toplam yüksekliği 40–48 dp.
5. Kategori, söz ve kaynak aynı okuma grubu içinde.
6. Gezinme ve eylemler kompakt bir alt kontrol kümesi olarak.
7. Hafif, içerikle bütünleşen alt navigasyon.

Okuma grubu:

- Kategori label'ı sözün 12–16 dp üzerinde olsun; aralarında 24 dp'den fazla boşluk oluşmasın.
- Kategori önünde 24–32 dp ince bordo çizgi kullanılabilir.
- Söz genişliği büst alanı hesaba katılarak belirlensin; metin büstün yüzüne girmesin.
- Söz uzunluğuna göre font veya düzen kademesi kullan: kısa, orta, uzun. Fontu okunamayacak kadar küçültme; gerektiğinde söz bölgesini kaydır.
- Kaynak sözden 16–20 dp sonra gelsin.
- Büst ekranın sağında yüzde 5–9 opaklıkta, yumuşak maskeli ve kontrollü boyutta olsun. Yüzün sert biçimde kesilmesi yerine kenarlar zemine erisin.
- Aynı ekranda ayrıca sütun/yaprak kullanma.

Alt kontrol kümesi:

- Önceki, `1 / 60`, sonraki aynı optik çizgide olsun. Sayaç gerçek veriyle bağlı kalsın.
- Kaydet ve Paylaş eş ağırlıkta 52–56 dp yüksekliğinde olsun.
- İki butonu da yalnız konturlu yapmak zorunda değilsin: biri açık yüzey, diğeri hafif nötr dolgu olabilir. Bordo küçük ikon durumu veya seçili işaret olarak kullanılmalı.
- Kaydedildiğinde ikon biçimi ve metin `Kaydedildi` olarak değişsin; yalnız renk değişmesin.
- Üç nokta 48–52 dp sessiz daire olarak kalsın.
- Kontrol kümesi ile alt navigasyon arasında 12–16 dp güvenli nefes alanı bırak.

### C. Keşfet — mevcut Görsel 6

Sorunlar:

- Büyük bordo başlık yine ekranı yukarıdan kapatıyor.
- Arama alanı ekran görüntüsünde başlığın altında kesilmiş/görünmez durumda; kaydırma veya inset düzeni kullanıcıya arama kontrolünü kaybettiriyor.
- Sekmeler genişliğe zorla yayıldığı için tipografik denge zayıf.
- Bildirim özeti büyük pembe bir kart olarak yeni bir baskın yüzey oluşturuyor.
- Koleksiyon kartları uzun, geniş boşluklu ve birbirinin tekrarı. Dört kart ekranın büyük bölümünü kaplıyor.
- Alternatif açık pembe/gri yüzeyler yeterli anlam taşımadan dama görünümü yaratıyor.
- Kart içindeki başlık, açıklama, sayı ve ok arasında gereğinden fazla dikey mesafe var.
- Alt navigasyon içerik kartlarını kesiyor ve ağır bir taban plakası gibi duruyor.

Yeni Keşfet yapısı:

1. Açık, kompakt başlık satırı: `Keşfet` + küçük sütun çizgisi veya yaprak detayı.
2. Başlığın hemen altında her zaman ulaşılabilir genel arama.
3. Arama altında yatay, içerik genişliğine göre kayan `Koleksiyonlar / Tüm konular / Seçtiklerim` sekmeleri.
4. Seçili bildirim konuları için büyük kart yerine ince özet satırı.
5. Koleksiyonlar için daha kompakt ve anlamlı yüzeyler.

Başlık ve arama:

- Bordo başlık bandını kaldır. `Keşfet` serif başlığı açık zeminde mürekkep renginde olsun.
- Başlığın yanındaki veya arkasındaki sütun/yaprak detayı yüzde 3–5 opaklıkta kalabilir.
- Arama 52–56 dp yüksekliğinde, hafif nötr surface üzerinde olsun. Çerçeve normal durumda görünmez/hairline, odakta bordo 1.5 dp.
- Liste kaydırıldığında başlık daralabilir; arama tamamen kaybolmamalı veya kullanıcı tek küçük hareketle geri ulaşabilmeli.
- Arama temizleme, sonuç sayısı ve boş sonuç durumlarını tasarla.

Sekmeler ve bildirim özeti:

- Sekmeleri dev metinler olarak eşit aralığa yayma. İçerik genişlikli, yatay kaydırılabilir tablar kullan.
- Seçili tab: koyu metin + 2 dp bordo alt çizgi veya küçük bordo nokta. Dolu bordo kapsül kullanma.
- Bildirim özeti yaklaşık 48–56 dp yüksekliğinde bir satır olsun: küçük durum ikonu, `3 konu bildirimlerinde`, sağda ok.
- Özet yüzeyi açık nötr veya çok hafif bordo wash olabilir; büyük pembe kart gibi görünmesin.

Koleksiyon kartları:

- Normal telefonda iki sütun korunabilir; kart oranını yaklaşık 1:0.9–1:1.1 aralığında tut. Aşırı uzun kart kullanma.
- Kart yüksekliği içerikten gelsin fakat aynı satırdaki kartlar optik olarak eşleşsin.
- Başlık 2–3 satır, açıklama en fazla 2 satır, alt bilgi tek satır.
- Kart üstündeki bordo çizgiyi daha ince ve kısa tut; her kartta aynı yerde mekanik tekrar yerine bazı kartlarda küçük Roma rakamı, çizgi veya soyut yükseliş işareti gibi tek bir tutarlı varyasyon sistemi tasarla.
- Rastgele pembe/gri dama düzeni kurma. Çoğu kart beyaz veya sıcak nötr; yalnız seçilmiş/önerilen koleksiyon çok hafif bordo wash alabilir.
- Kartın tamamı dokunulabilir olsun. Ok yalnız davranışı desteklesin.
- `3 konu` bilgisi ve ok alt kenara sabitlenebilir; ortadaki boşluk kontrollü olsun.
- 360 dp altı genişlikte veya yüzde 130 üzeri yazıda tek sütuna geç.

Tüm konular ve Seçtiklerim:

- Büyük kart yerine kompakt satır listesi kullan.
- Her satırda konu adı, kısa durum/açıklama ve sağ ok bulunmalı.
- Satıra dokunmak her zaman detay açmalı; seçim değiştirme yalnız detay içindeki açık kontrolle yapılmalı.
- Açık, seçili ve kilitli durumları yalnız renkle anlatma.
- `Bildirimlerinde`, `Kilitli · Önizleme` gibi kısa ve doğru durum metinleri kullanılabilir.
- Filtre ve arama sonucundaki gerçek konu sayısını göster.

### D. Senin, Planım, Kaydedilenler ve diğer ortak ekranlar

- Aynı açık başlık dili kullanılmalı; tam genişlik bordo bant başka ekranlarda tekrar etmemeli.
- `Senin` başlığı ile ayarlar ikonu aynı sade app bar içinde yer alsın.
- İstatistikleri dev kartlar ve ağır renk blokları yerine açık bölümler, ince ayırıcılar ve bir veya iki anlamlı vurgu ile göster.
- Planım ekranındaki konu etiketleri, onboarding plan özetindeki chip sistemiyle aynı olmalı.
- Kaydedilenler ve kategori detayları ana ekranın söz tipografisini ve kaynak hiyerarşisini paylaşmalı.
- Ayarlar ekranında standart kontrol davranışlarını koru; her satırı ayrı büyük karta dönüştürme.

### E. Alt navigasyon

Mevcut navigasyon işlevsel fakat sert, geniş ve optik olarak dengesiz görünüyor.

Yeni yaklaşım:

- Açık yüzey üzerinde 72–80 dp içerik yüksekliği; sistem navigasyon inset'i ayrıca tüketilsin.
- Üstte belirgin çizgi yerine çok hafif hairline veya yumuşak gölge.
- Üç hedef eşit genişlikte ve en az 48 dp dokunma alanında.
- Aktif ikon mürekkep/bordo kombinasyonunda; ikonun üstünde 20–28 dp kısa bordo çizgi veya küçük şekil farkı.
- Aktif etiket 600 ağırlık, pasif etiket 400–500 ağırlık.
- Pasif ikonları fazla soluklaştırma; kontrast korunmalı.
- Aktif öğenin arkasında büyük bordo/pembe kapsül kullanma.
- İkon ve etiket arası 4–6 dp; bütün ikonların baseline ve optik ağırlıkları eşit olsun.

## Mikro etkileşimler

- Sekme değişimi: 160–220 ms kısa fade/position geçişi.
- Seçim: 120–180 ms sınır ve işaret geçişi; aşırı yaylanma kullanma.
- Kaydet: kalp biçimi değişimi + kısa metin değişimi; tek hafif haptik.
- Söz geçişi: metin, kategori ve kaynak birlikte 180–240 ms geçsin. Büst sabit kalsın; ekran geometrisi zıplamasın.
- Progress: adım değişiminde kısa genişlik animasyonu; açılışta uzun animasyon yok.
- Reduce Motion etkinken hareketi fade veya anlık durum değişimine indir.
- Sürekli hareket eden büst, parallax, parlayan bordo buton veya dekoratif titreşim ekleme.

## Erişilebilirlik ve duyarlı yerleşim

- Normal metinlerde en az 4.5:1 kontrast.
- Büyük metin ve işlevsel ikonlarda ilgili WCAG/Android kontrast sınırlarını doğrula.
- Tüm dokunma hedefleri en az 48 × 48 dp.
- Yüzde 100, 130, 150 ve 200 yazı ölçeğinde kontrol et.
- 320 dp, 360 dp, 411 dp ve geniş telefon düzenlerinde kontrol et.
- Uzun Türkçe kategori adları, kısa sözler ve çok uzun sözler için ayrı örnekler kullan.
- Sistem durum çubuğu, hareketle gezinme, üç düğmeli gezinme ve klavye açık durumu test edilmeli.
- Sabit alt eylem hiçbir içeriği erişilemez bırakmamalı.
- Seçili, kaydedilmiş, kilitli ve bildirimde durumlarını doğru semantiklerle sun.
- Dekoratif görselleri ekran okuyucudan gizle; ikon düğmelerine eylemi anlatan ad ver.

## Kaçınılacak tasarım kalıpları

Şunları kullanma:

- Tam genişlik kalın bordo başlık bandı.
- Her ekranda dev bordo CTA.
- Açık pembe kartların tekrarlı dama düzeni.
- Aşırı büyük serif başlık ve sözler.
- Salt estetik için `01`, `02`, `04` gibi anlamsız numaralar.
- Aynı ekranda büst + sütun + yaprak + yükselen çizginin tamamı.
- Büyük boş alanı “premium/minimal” zannetmek.
- Metni sabit butonun altında bırakmak.
- Her öğeyi yuvarlak karta veya kapsüle çevirmek.
- Aktif durumu yalnız renkle göstermek.
- Uzun satış/demoya ilişkin metni onboarding'in ana odağı yapmak.
- Varsayılan Material bileşenlerini tema dışı renklerle bırakmak.
- Görsel doğrulama yapmadan yalnız derleme başarısını tasarım başarısı saymak.

## Uygulama sırası

1. Mevcut ekranları ve ortak bileşenleri haritala; mevcut işlevleri listele.
2. Merkezi renk, tipografi, boşluk, köşe, ikon ve elevation tokenlarını tanımla.
3. Tam genişlik bordo header bileşenini açık ve kompakt ortak app bar ile değiştir.
4. Ortak alt eylem alanını ve alt navigasyonu düzelt.
5. Onboarding ortak iskeletini kur; ardından başlangıç, sorular, plan özeti, erişim ve izin ekranlarını düzenle.
6. Ana ekranın okuma geometrisini ve uzunluk uyarlamasını düzelt.
7. Keşfet başlık/arama/tab/özet hiyerarşisini kur; koleksiyon kartlarını ve konu listelerini sıkılaştır.
8. Senin, Planım, Kaydedilenler, ayarlar, kategori detayı ve paylaşım panelini aynı sisteme bağla.
9. Açık/koyu tema, büyük yazı ve dar ekran kontrollerini yap.
10. Aynı cihaz, aynı içerik ve aynı yazı ölçeğiyle önce/sonra görüntüleri üret.

## Beklenen teslimatlar

Uygulama sonunda şunları sun:

- Değişen ekran ve ortak bileşenlerin listesi.
- Nihai renk ve tipografi token tablosu.
- Onboarding, Bugün, Keşfet ve Senin için aynı cihaz boyutunda önce/sonra ekran görüntüleri.
- Uzun söz, uzun kategori adı, yüzde 200 yazı, koyu tema ve klavye açık durum görüntüleri.
- Değiştirilmeyen ürün davranışlarının kısa doğrulama listesi.
- Gerçekten çalıştırılan testlerin sonuçları.
- Fiziksel cihaz, TalkBack veya üç düğmeli navigasyon denenmediyse açık sınır notu.

## Kabul ölçütleri

Çalışma ancak aşağıdaki maddeler sağlanırsa tamamlanmış sayılır:

| Alan | Kabul ölçütü |
|---|---|
| Renk | Bordo büyük yüzeylerden çekilmiş; ince, tutarlı ve anlamlı mikro vurgularda kullanılıyor. |
| Yumuşaklık | Açık yüzey geçişleri, köşeler, çizgiler ve boşluklar sakin; arayüz soluk veya kontrastsız değil. |
| Onboarding | 20 adımın işlevi korunuyor; ortak iskelet sade; metin yoğunluğu ve boşluk dağılımı dengeli. |
| Plan özeti | Konular taranabilir; ilk söz ve kaynağı CTA altında kalmadan okunabiliyor. |
| Bildirim izni | İzin gerekçesi kısa ve anlaşılır; ana/ikincil eylem ayrımı açık. |
| Bugün | Söz ilk odak; büst geri planda; gereksiz üst/alt boşluk yok; ana eylemler rahatça erişiliyor. |
| Keşfet | Arama kolay bulunuyor; sekmeler, bildirim özeti ve koleksiyonlar birbirleriyle yarışmıyor. |
| Koleksiyonlar | Kartlar kompakt, sakin ve taranabilir; pembe/gri dama görünümü yok. |
| Navigasyon | Aktif durum renk ve biçimle anlaşılıyor; alt bar hafif ve sistem çubuğuyla çakışmıyor. |
| Tipografi | En fazla üç belirgin seviye; uzun Türkçe metinler kesilmiyor; serif ölçülü kullanılıyor. |
| Erişilebilirlik | Dokunma, kontrast, semantik durum, büyük yazı ve dar ekran koşulları doğrulanmış. |
| Kapsam | Ürün mantığı, veri, içerik, Pro/reklam, bildirim motoru ve kategori kimlikleri değiştirilmemiş. |

## Son estetik ölçüt

Ascend'e ilk kez bakan biri “bordo renkli bir form uygulaması” görmemeli. Sakin, düşünülmüş ve kişisel bir okuma deneyimi görmeli. Bordo ancak ikinci bakışta fark edilen, seçimi ve markayı birbirine bağlayan ince bir imza olmalı. Roma/mimari detayları uygulamaya ruh vermeli; içeriğin önüne geçmemeli. Her ekran daha az nesneyle daha iyi bir kompozisyon kurmalı.
