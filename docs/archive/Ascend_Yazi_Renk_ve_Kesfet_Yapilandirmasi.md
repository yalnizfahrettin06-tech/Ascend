# Ascend — Yazı, renk ve Keşfet yeniden yapılandırması

Tarih: 12 Eylül 2026. İlk analiz 9.5.0 / 71af841 üzerinden yapıldı. Bu belge tasarım kararlarının başlangıç kaydıdır. Uygulanan değişiklikler ve Android doğrulaması için [9.6.3 teslim kaydına](Ascend_9_6_Tasarim_ve_Dogrulama.md) bakın; aşağıdaki fikirlerin tamamı uygulanmış özellik olarak okunmamalıdır.

## 1. Net karar

**Arayüz ailesi Inter; temel yüzeyler gümüş gri; güçlü alanlar grafit siyah.** Roma kimliğini her başlığa serif harf koyarak taşımayacağız. Onu mimari görseller, ışık, doku ve ölçülü kompozisyon taşıyacak. Ana ekranın beğenilen söz ve heykel kompozisyonu korunacak. Keşfet, onboarding ve yardımcı ekranlar aynı okunaklı arayüz diline geçecek.

Hedef, “pahalı görünmeye çalışan” başlıkları çoğaltmak değil; birkaç saniyede ayırt edilen, dengeli ve kullanılabilir bir ürün kimliği oluşturmak. Milyonlarca indirmeyi tasarımla garanti edemeyiz; tasarımın ilgiyi artırıp artırmadığını ilk izlenim, mağaza dönüşümü, kurulum tamamlama ve geri dönüş verileriyle değerlendirebiliriz. Bu çalışma için önce görsel kalite ve anlaşılabilirlik ölçülecek; yeni analitik altyapısı veya ürün özelliği eklemek bu kapsamın parçası değildir.

## 2. Kaynakta görülen kök nedenler

| Bulgu | Görsel etkisi | Karar |
|---|---|---|
| Lora; ekran, koleksiyon, konu, filtre, boş durum ve onboarding başlıklarına yayılmış | Sözün editoryal karakteri ile uygulama kontrolleri birbirine karışıyor | Serif kullanımı söz/marka alanıyla sınırlandırılacak |
| Bazı stiller merkezî, çok sayıda stil ekran içinde ayrı ayrı tanımlı | Fontu bir yerden değiştirmek ekranın tamamını düzeltmiyor | Ekran, kart, açıklama, eylem ve yardımcı yazı rolleri tek sistemden yönetilecek |
| Keşfet koleksiyon yüzeyi yüzde 65 saydamlıkla çiziliyor | Gri kart, açık zeminle karışıp soluklaşıyor | Temel yüzeylerde opak gri renk kullanılacak |
| Krem zemin ile soğuk gri yüzeyler bir arada | Gümüş kimliği tutarlı görünmüyor | Keşfet/onboarding nötr gri zemine alınacak; ana ekran bağımsız tutulacak |
| Arama şeffaf, sekmeler çoğunlukla metin, kartlar benzer ağırlıkta | Taranacak yerler ve ilk odak belirgin değil | Arama, gezinme, koleksiyon ve durum alanları farklı görsel roller alacak |
| Kartlarda ikon, başlık, açıklama, konu sayısı, seçim etiketi ve dekor aynı anda var | Sade görünen ekran aslında küçük bilgi tekrarlarıyla yüklü | Kartta başlık, tek görsel ve tek durum satırı kalacak |
| Onboardingde yapı gelişti fakat başlıklar hâlâ büyük serif ve yüzeyler genel bir geçiş efektiyle boyanıyor | Yeni etkileşimler eski tipografik hissi taşıyor | Beş ayrı deneyim korunacak; ortak font ve yüzey dili düzeltilecek |

İncelenen dosyalar: `core/Tasarim.kt`, `core/Paletler.kt`, `ui/KategorilerEkrani.kt`, `ui/Onboarding.kt` ve font kaynakları.

## 3. Font araştırması ve seçimi

Inter'in üretici dokümanı fontu ayrıntılı arayüzlerden büyük başlıklara kadar geniş kullanım için tanımlıyor. Metin ve gösterim boyutlarına yönelik optik tasarımları ve farklı ağırlıkları var. Bu özellikleri küçük kontrol metinleri ile büyük başlıkları tek aile içinde kurabilmek açısından uygun buluyorum. Bu bir estetik garanti değil, Ascend'in ihtiyacına yönelik seçimdir. [Inter resmî sitesi](https://rsms.me/inter/).

NN/G tipografi rehberi, hiyerarşinin aileyi sürekli değiştirmek yerine ağırlık, boyut ve stillerle de kurulabileceğini açıklıyor. Bu nedenle arayüze üçüncü bir dekoratif font eklemek yerine tek aileye geçiş öneriyorum. [NN/G — Pairing Typefaces](https://www.nngroup.com/articles/pairing-typefaces/).

| Aday | Ascend için değerlendirmem | Kullanım kararı |
|---|---|---|
| **Inter** | Net, nötr; kontrolleri ve uzun Türkçe başlıkları ortak dilde toplamak için uygun | Birinci tercih; arayüzün tamamında |
| **Manrope** | Daha yuvarlak, geometrik bir his; aynı yerleşimde değerlendirilmeye değer | Alternatif; Inter ile aynı uygulama ekranında karıştırılmayacak |
| **Lora** | Ana ekrandaki sözde karakterli; küçük, sık tekrarlanan kontrollerde fazla editoryal | Arayüz başlıklarından çıkarılacak; beğenilen söz/marka bağlamında korunacak |

Manrope kaynak dosyası ve lisansı Google Fonts deposundan, Inter dosyası da aynı dağıtımdan alındı. [Manrope kaynakları](https://github.com/google/fonts/tree/main/ofl/manrope). Yerel Lora dosyası 400–700 ağırlık eksenli değişken font; varsayılanı 400. Normal ve Medium aynı dosyaya bağlı. Bu tek başına bir hata kanıtı değildir; Android'de ağırlıkların doğru çizildiği kontrol edilmelidir.

Üç dosyanın Unicode karakter eşlemelerinde **İ ı Ğ ğ Ş ş Ç ç Ö ö Ü ü** mevcut. Eksik Türkçe karakter sorunu bulunmadı. Tarayıcı örneğinde gerçek fontlar gömülü; font seçimiyle aynı ekran üzerindeki fark görülebilir. Android'e geçişte aynı dosya ve ağırlıklar doğrulanmalı, sentetik kalınlık veya rastgele sistem fontuna düşme kabul edilmemeli.

### Önerilen yazı ölçeği

| Rol | Boyut / satır yüksekliği | Ağırlık |
|---|---|---|
| Keşfet ekran başlığı | 32 / 38 sp | 600 |
| Onboarding başlığı | 28 / 34 sp | 600 |
| Koleksiyon başlığı | 18 / 24 sp | 600 |
| Konu satırı | 16 / 22 sp | 500 |
| Açıklama | 15 / 23 sp | 400 |
| Ana eylem | 15 / 20 sp | 500 |
| Sekme | 13 / 18 sp | 600 seçili, 500 normal |
| Yardımcı bilgi | 12 / 16 sp | 400 |

Başlıklarda en fazla hafif negatif harf aralığı; paragrafta varsayılan aralık. Her başlık kalın, her açıklama büyük veya her yardımcı etiket geniş aralıklı büyük harf olmayacak. Türkçe metinler ekrana göre doğal sarılacak; iki satır hedeflenebilir ama büyük yazıda zorunlu kesilmeyecek. Harfleri küçülterek taşma gizlenmeyecek.

## 4. Gri ve siyahın yapısal kullanımı

| Rol | Renk | Kullanım |
|---|---|---|
| Ana arayüz zemini | `#E9EAEC` | Keşfet ve onboarding arka planı |
| Okuma yüzeyi | `#F7F7F8` | Arama, gerektiğinde metin bildirimi |
| Koleksiyon yüzeyi | `#DFE1E4` | Orta ağırlıklı kartlar |
| İkincil alan | `#D9DBDE` | Sekme yatağı ve gruplama |
| İnce ayırıcı | `#C3C6CB` | Dekoratif sınır ve alan ayrımı |
| Güçlü sınır / odak | `#747880` | Kontrolün seçilmesi/odaklanması için gerektiğinde |
| Ana yazı | `#18191B` | Başlık ve okunacak içerik |
| İkincil yazı | `#575B61` | Açıklama ve durum |
| Grafit | `#25272B` | Ana düğme, aktif sekme, tek baskın koleksiyon alanı |

Siyahın miktarı rastgele artırılmayacak. Bir görünümde ana eylem ve tek baskın görsel alan güçlü olabilir; kalan alanlar orta gri katmanlarla ayrılır. Bütün kartları siyaha çevirmek, bu kez başka bir tekdüzelik yaratır. Her kartın etrafına kalın siyah çizgi de çizilmeyecek. İnce kenarlık, yüzey farkı ve gerektiğinde hafif gölge birlikte kullanılacak.

Bordo, altın, sarı, mavi ve turuncu vurgu yok. Nötr gümüş renk; metalik parlama efekti veya her yüzeye yayılan degrade anlamına gelmez. Parlak geçiş sadece görselin ışığında veya sınırlı etkileşim alanında yer almalı.

### Hesaplanan kontrastlar

- Ana yazı / ana zemin: **14,61:1**.
- İkincil yazı / ana zemin: **5,67:1**.
- İkincil yazı / koleksiyon kartı: **5,21:1**.
- Beyaz yazı / grafit: **14,96:1**.
- Güçlü sınır / ana zemin: **3,68:1**.
- İnce ayırıcı / ana zemin: **1,42:1**; bu renk tek başına gerekli kontrol sınırı veya durum işareti olarak kullanılmayacak.

Bunlar önerilen düz renk çiftlerinin hesaplarıdır; fotoğraf üzerine yazı için aynı sonucu varsayamayız. Normal metinde 4,5:1, gerekli grafik/kontrol göstergelerinde ilgili 3:1 kriteri esas alınmalı. [W3C metin kontrastı](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html), [W3C metin dışı kontrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html).

## 5. Keşfet'in yeni düzeni

Ekran; arama, koleksiyonları keşfetme ve bildirim konularını yönetme işlerine hizmet edecek. Yeni kişilik testi, öneri algoritması veya ikinci ana ekran eklenmeyecek.

1. **Kısa başlık:** Inter ile “Keşfet” ve tek satırlık alt açıklama. Üstüne ek marka sloganı, saat veya büyük dekor bindirilmeyecek.
2. **Belirgin arama:** Açık okuma yüzeyi üzerinde 48–52 dp alan. Sol arama ikonu, yazarken temizleme kontrolü, belirgin odak durumu.
3. **İki temel görünüm:** “Koleksiyonlar” ve “Bildirim konularım”. Tüm konular, koleksiyon başlığının yanında doğrudan erişilen eylem; arama her zaman tüm konuları tarar.
4. **Tek güçlü koleksiyon sunumu:** Var olan bir koleksiyonun grafit alan içinde mimari görseli. Bu kişiselleştirilmiş öneri veya yeni özellik değildir. Alan tüm ilk ekranı kaplamaz; diğer koleksiyonlar görünür kalır.
5. **Daha kısa koleksiyon kartları:** Tek görsel, net başlık, tek durum satırı. Fotoğraf çoğunlukla ayrı görsel bölümünde; açıklama ve başlık düz renk üzerinde. Her karta hem konu açıklaması hem seçim rozeti hem büyük ikon eklenmez.
6. **Konu listesi:** Görsel küçük resim, okunaklı ad, tek erişim durumu. Pro konu küçük ama yüksek kontrastlı `PRO` etiketiyle; seçili açık konu onay işareti ve “Bildirimlerinde” metniyle gösterilir.
7. **Koleksiyon/Pro ayrımı:** Karma erişimli bir koleksiyon bütünüyle Pro gibi etiketlenmez. Erişim durumu gerçek konu verisinden gelir. Okuma, bildirim seçimi ve Pro açma eylemleri birbirinin yerine çalışmaz.
8. **Kısa alt gezinme:** Ana ekranla tutarlı yükseklik ve sistem gezinme alanına uyum; gri zemin. Seçili sekme koyu ikon ve yazıyla anlaşılır.

Antik Roma hissi: kategoriye uygun kemer, taş, sütun ayrıntısı veya ışık. Bütün kartlara aynı heykel, arka plana çizgiler, minik okunmayan rölyefler eklenmeyecek. Her içerik fotoğrafının antik olması gerekmiyor; mimari çerçeve uygulamanın bütünlüğünü, görsel konu ise koleksiyonun anlamını taşımalı.

Arama sonucu yok, hiç konu seçilmemiş, çok uzun başlık ve kilitli kategori durumları da tasarımın parçası olacak. “Bomboş” ekran yerine tek cümle ve ilgili tek eylem bulunacak.

## 6. Onboarding'e uygulanacak yaklaşım

9.5'teki beş farklı görevi yeniden karıştırmayacağız. Sorunlu ortak dili düzelteceğiz:

- Karşılama: Inter başlık, gümüş zemin, kemer görseli, kısa fayda ve yakındaki CTA.
- Söz denemesi: Arayüz anlatımı Inter; örnek söz istenirse ana ekrandaki söz rolünde. Dev başlıklar ve kart içinde tekrar başlık yok.
- Sıklık: Gümüş kadran birincil odak. Tek büyük sayı, küçük açıklama; diğer metinler onunla yarışmaz.
- Saatler: Etkin aralığı belirgin gün çizelgesi, okunaklı saat kontrolleri, yaklaşık saat bilgisi. Aynı bildirim örneği eklenmez.
- İzin: Yalnızca burada bildirim örneği. Telefon çerçevesi ağır süs olmadan örneği taşır. İzin ve görünüm yardımı açıkça ayrılır.

Gövde ve ilerleme eylemi tek yerleşim grubunda kalır. Büyük ekranlarda boşluğun tamamı CTA'nın üzerine yığılmaz; küçük ekranda içerik kaydırılır. Animasyon; kararın sonucunu gösterir, kullanıcıyı bekletmez. Bildirimsiz devam eklenmez; Android izin reddi yine sistemin kararıdır. Pro yerel demo olarak kalır, gerçek ödeme bağlantısı eklenmez.

## 7. Uygulama sırası ve tamamlanma ölçüsü

### A — Ortak yazı ve yüzey sistemi

Önce Inter'in gerçek dosyaları, 400/500/600 ağırlıkları ve tüm UI yazı rolleri tanımlanır. Onboarding ve Keşfet içindeki bağımsız font atamaları temizlenir. Ana ekran söz stili ayrı tutulur. Gri yüzeyler opak ve rol bazlı uygulanır. Sistem çubukları yeni zemine uyar.

Bitti ölçüsü: font yalnızca bir başlıkta değişmiş değildir; sekme, arama, liste, filtre ve diyaloglarda aynı aile görülür. Türkçe harfler ve İngilizce uzun metinler doğru çizilir.

### B — Keşfet'in kompozisyonu

Arama, iki görünüm, koleksiyon alanı ve konu durumları beraber yeniden yerleştirilir. Görsellerin kırpılması kategori bazında seçilir. Bildirim seçimi ile koleksiyon açma davranışı korunur. Eski erişim diyalogları varsa çağrılma yolu incelenerek Pro demo kararına aykırı görünen metinler temizlenir; yalnızca kaynakta eski fonksiyon görmek aktif akış olduğu varsayımına yetmez.

Bitti ölçüsü: kullanıcı bir konu bulabilir, açık/Pro/seçili farkını anlayabilir, bildirim konularına ulaşabilir. İlk ekran yalnızca kontrollerden oluşmaz; koleksiyon içeriği de görünür.

### C — Onboarding ve yardımcı ekranların tutarlılığı

Yeni tipografi ve yüzeyler beş adıma, filtre sayfasına, konu detayına ve bildirim yardımına taşınır. Tasarım dışına taşan yeni özellik eklenmez.

Bitti ölçüsü: birbirinden farklı beş görev tek ürünün parçaları gibi görünür; ana ekranla marka bağlantısı kopmaz.

### D — Gerçek Android görüntüsüyle kabul

**Kullanıcının son mesajıyla APK sonrasında kontrol izni verilmiştir. Önceki “APK'dan sonra kontrol yapma” tercihi bu çalışma için geçerli değildir.**

Derleme başarısı estetik kabul değildir. Yeni sürümde Keşfet, arama sonuçları, Pro konu, seçili konu, boş durum ve beş onboarding adımı gerçek Android ekran görüntüsüyle incelenir. Ana ekran gerilemesi ayrıca karşılaştırılır. En az dar ve geniş telefon ölçüsü, normal ve büyütülmüş yazı, açık/koyu tema, üç düğmeli sistem çubuğu ve hareketli gezinme ele alınır. Bulunan hatalar düzeltilip son teslim aynı kaynak sürümüne bağlanır.

Kontrol soruları: Ana odak bir bakışta belli mi? Başlıklar doğal kırılıyor mu? Gri yüzey gerçekten zeminden ayrılıyor mu? Fotoğraf üstü yazı okunuyor mu? CTA erişilebilir mi? Pro durumu doğru mu? Dekor azaltıldığında ekran hâlâ düzenli mi? Animasyon kapatıldığında akış çalışıyor mu?

## 8. Bu teslimde hazır olanlar

- Kaynağa dayalı teşhis ve bu yeniden yapılandırma dosyası.
- `design-direction/Ascend-Tasarim-Yonu.html`: Inter/Manrope/Lora başlık karşılaştırması, Keşfet ve onboarding tasarım örnekleri, renk paleti ve konu durum örnekleri.
- Gerçek yerel font dosyaları ve lisansları. İnternet olmadan açılabilir.
- Tarayıcıda incelenmiş görünüm; font geçişi ve konu durum penceresi kontrol edildi. 360 px genişlikte yatay taşma görülmedi, tüm görseller yüklendi.

Android kaynak kodunda bu tur tasarım değişikliği yapılmadı ve yeni APK üretilmedi. Açılabilir örnek, Android'e uygulanmış sonuç olarak sunulmaz. Bir sonraki uygulamanın referansı bu somut yazı/yüzey sistemi olmalıdır; yeniden rastgele font ve kart değiştirme döngüsüne dönülmemelidir.
