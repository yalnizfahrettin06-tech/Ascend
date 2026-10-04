> **12 Eylül 2026 — Son kullanıcı kararı bu belgede önceki tema kararlarının yerine geçer:** Ücretsiz temalar Beyaz ve Siyah. Onboarding tema adımında bunların altında Roma ve Koyu Atlı Yolcu Pro etiketleriyle önizlenir. Tüm çalışan widget özellikleri Pro kapsamındadır. Son APK üretiminden sonra ek uzun kontrol turu istenmiyor.

# Ascend — Tema, Onboarding, Keşfet ve Pro Widget
## Yol haritası ve yeniden kullanılabilir uygulama promptu

Tarih: 12 Eylül 2026. Durum: Planlama; bu belge hazırlanırken uygulama kodu değiştirilmedi, APK üretilmedi. Başlangıç: yerelde incelenen 9.6.3 kaynakları ve tasarım kaydı.

## 1. Amaç ve kullanım

Bu dosya uygulama talebiyle sana yeniden verildiğinde aşağıdaki yol haritasını Ascend'in mevcut Android projesine uygula. Önce son kaynak durumunu doğrula; tamamlanmış işleri yeniden yazma. Kesin kararlarla önerileri ayırt et. Bu belgenin kendisi bugün kod değişikliği yapılması anlamına gelmez.

Ascend'i belirli bir tarihî görsel kimliğe bağlı olmaktan çıkar. Uygulamanın temel kimliği sade, anlaşılır, nötr, gümüş gri ve grafit siyah olsun. Roma ve şövalye uygulamanın tamamının kimliği değil, seçilebilir ana ekran temaları olsun. Ana ekranın beğenilen söz odaklı yapısını koru.

Kaliteyi yalnızca derleme başarısıyla değerlendirme. Uygulama aşamasında gerçek Android ekranlarını aç, incele, sorunları düzelt ve aynı son kaynak sürümünün APK'sını teslim et. Sadelik, işlevsiz dev boşluk anlamına gelmez; zenginlik de daha fazla kart ve açıklama eklemek değildir.

## 2. Kesinleşmiş kullanıcı kararları

| Alan | Kesin karar |
|---|---|
| Onboarding kimliği | Roma'dan bağımsız, geniş kitleye hitap eden anlatım |
| İlk ekran | Türkçe ve English seçimi; yeni ve tam çeviri sistemi şimdilik zorunlu değil |
| Sıralama | Dil → tanıtım → bildirim kurulumu ve izni → tema seçimi |
| Tema seçimi | Seçenekler örnek sözle birlikte önizlenebilir |
| Ücretsiz tema | Tam olarak iki: Açık/Sade ve Koyu Atlı Yolcu |
| Diğer temalar | Paylaşım görsellerinden uygun olanlar tema kataloğuna alınabilir |
| Görsel kapsam | Tematik fotoğraf ve figür yalnızca uygulamanın ana ekranında |
| Diğer ekranlar | Seçime uygun açık/koyu nötr renk düzenine geçebilir |
| Keşfet | Büyük ASCEND / Keşfet / Sana iyi gelecek düşünceyi bul bloğu kaldırılacak |
| Özelleştirme | Keşfet üzerinden erişilecek; kategori bulmayı zorlaştırmayacak |
| Widget erişimi | Temel model dahil tüm kullanılabilir widget özellikleri Pro |
| Widget görseli | Mevcut görsellerle özelleştirilebilir |
| Pro | Demo; gerçek Play Billing, ücret tahsilatı ve abonelik yok |

Aşağıdaki sekme düzeni, mikro metinler, ölçüler, faz dağılımı ve geçiş politikaları tasarım önerileridir. Kullanıcının ayrıca verdiği kararlar gibi anlatma. Bu plan uygulama talebiyle geri verildiğinde çalışma yönünü oluştururlar. Fiyat veya ticari taahhüt icat etme.

## 3. Mevcut kaynak bulguları

- Arayüz Inter, söz ve marka alanları Lora kullanıyor. Bu sistemi başlangıç al; bütün uygulamaya yeniden başka font ekleme.
- Onboarding beş sayfalı; mimari karşılama görseli nötr ürün anlatımıyla değişmeli.
- Keşfet başlığında ve koleksiyon kartlarında klasik mimari motifler var. Kategori arayüzünden kaldır; konuya uygun sade çizgi ikonlar kalabilir.
- `scene_atli_yolcu.webp` ve `scene_sovalye.webp` farklı varlıklardır. Ücretsiz tema Atlı Yolcu; ayakta duran şövalyeyi yanlışlıkla onun yerine kullanma.
- Atlı Yolcu'nun mevcut görselinde turuncu/altın gün batımı var. Öneri: aslı korunarak nötr gri bir ana ekran türevi hazırlanması. Bu renkleri yeni uygulama vurgu sistemine dönüştürme.
- `widget/AzimWidget.kt` içinde temel Glance widget'ı mevcut; sabit koyu zemin ve metin gösteriyor. Buna paralel ikinci çizim sistemi kurma.
- İncelenen widget tazeleme yolu bütün örnekleri güncelliyor. Yeni yapıda her widget'ın kendi tercihleri ayrı korunmalı.
- İncelenen widget çiziminde Pro kontrolü görünmüyor. Yalnızca uygulama içindeki ekleme düğmesini kilitlemek yeterli olmaz; Android widget seçicisinden giriş ve mevcut widget güncellemesi de yetki kontrolüne uymalı.

Uygulama öncesi bu bulguları yeniden doğrula. Mevcut içerik, favoriler ve bildirim planını görsel yenileme nedeniyle sıfırlama.

## 4. Tema ile arayüz rengini ayır

Dört ayrı tercih alanı oluştur:

1. Ana ekran teması: görsel, kırpma, sözün güvenli alanı, okunurluk katmanı.
2. Arayüz görünümü: nötr açık/koyu zeminler, kart, metin ve ikon renkleri.
3. Paylaşım tasarımı: o paylaşıma ait görsel, oran, yazı düzeni ve desteklenen video seçeneği.
4. Widget tasarımı: her Android widget örneğinin kendi görseli, düzeni ve içeriği.

Aynı görsel kütüphanesini kullanabilirler; aynı kaydedilmiş tercih olmak zorunda değiller.

| İşlem | Etki | Değişmemesi gereken |
|---|---|---|
| Ana ekran temasını uygulama | Ana ekran görseli ve varsayılan açık/koyu arayüz | Favoriler, saatler, mevcut widget tasarımı, bitmiş paylaşım |
| Paylaşım görselini seçme | O paylaşım önizlemesi ve çıktısı | Ana ekran ve widget'lar |
| Bir widget'ı düzenleme | Yalnızca seçili widget | Diğer widget'lar ve bildirim planı |
| Temayı önizleme | Geçici seçim | Uygula denmeden kalıcı ayarlar |

İlk sürümde Açık/Sade açık arayüzü, Atlı Yolcu koyu arayüzü önerir. Kullanıcıyı başlangıçta ayrıca üç görünüm düğmesiyle yorma. Mevcut açık/koyu/sistem ayarı varsa davranışını açıkça eşleştir; bazı ekranlar eski palete rastgele dönmesin. Tema figürü Keşfet, ayarlar veya onboarding'e taşınmasın.

## 5. Yeni onboarding: önerilen beş adım

Dil ve tema eklendi diye akışı yedi-sekiz sayfaya çıkarma. Sıklık ve saat aralığını tek bildirim düzeni sayfasında birleştir. Her sayfa tek ana amaç ve tek devam eylemi taşısın.

### 5.1 Dil

Başlık: “Hoş geldin / Welcome”. Açıklama: “Dilini seç / Choose your language”. Seçenekler: Türkçe ve English. Eylem: “Devam / Continue”.

Bayrak kullanma; dil bir ülkeyle eşitlenmesin. İki sade seçim satırı, açık seçili durum ve tik yeterlidir. Mevcut dil önerilen varsayılan olabilir. Büyük dünya küresi, bayrak kolajı veya boş telefon maketi ekleme. Gerekli görsel ilgi, iki dilde kısa bir sözün ölçülü tipografik örneğiyle sağlanabilir; iki büyük tekrar kartı oluşturma.

Tam çeviri altyapısı bu fazın zorunluluğu değildir. Mevcut TR/EN desteği kolayca bağlanabiliyorsa kullan. Yalnızca seçim prototipi yapılırsa kontrol gerçekten seçilebilsin ve tercih saklansın; ancak bütün uygulamanın İngilizceye geçtiği iddia edilmesin. Çalışmayan düğme bırakma. Gerçek dil bağlantısı/prototip ayrımını teslim raporunda yaz.

### 5.2 Uygulamayı deneyerek tanı

Başlık: “Gününe küçük bir durak.” Açıklama: “Bir söz oku. Sende kalanı sakla.” Eylem: “Bildirimleri ayarla”.

Nötr gümüş yüzeyde kaydırılabilen kısa söz kartı ve kalp denemesi. Kartların arkasındaki küçük katman farkı kaydırmayı hissettirebilir. Kısa söz dikey dengelensin. Dev boşluğu yalnızca bir kart sınırı içine almak çözüm sayılmasın.

Bu sayfada Roma, heykel, atlı, sütun, kemer, defne, kale ve zırh yok. Anlamı belirsiz dekoratif üç boyutlu objeler yerine gerçek kullanım göster. En fazla bir yardımcı ifade: “Kaydır ve dene”. Kalp küçük hareketle geri bildirim versin; örnek beğenme gerçek favorileri doldurmasın. Bildirim maketini bu sayfaya da kopyalama.

### 5.3 Bildirim düzeni

Başlık: “Ne zaman hatırlatalım?” Eylem: “Devam”.

Tek düzen kartında günlük adet için küçük eksi/sayı/artı, saat aralığı için yan yana başlangıç/bitiş, en altta bir özet: “Günde 3 kez · 09:00–21:00”. Mevcut geçerli adet sınırlarını koru. Büyük yazıda saat kontrolleri alt alta geçsin.

Küçük noktalı zaman çizgisi aralığı açıklayabilir. Aynı bilgiyi halka, büyük sayı kartı, bildirim maketi ve ayrı saat listesiyle dört kez anlatma. Ayrıntılı saat seçici dokununca açılır. Varsayılan plan kullanılabilir olsun; her kontrolle etkileşim zorunlu olmasın. Örnek saatler gerçek planlayıcıyla eşleşsin.

Küçük ekranda sıkışırsa metni kısalt, grupları düzenle ve kaydırmayı destekle. Yazıyı okunmaz kadar küçültme; akışı otomatik uzatma.

### 5.4 Bildirim izni

Başlık: “İyi bir söz seni bulsun.” Eylem: “Bildirimleri aç”.

Tek, açılıp kapanabilen gerçekçi bildirim örneği. Henüz tema seçilmediği için nötr görünür; Roma/Atlı Yolcu fotoğrafı yok. Küçük “Örnek bildirim” ibaresi bulunur. Gerçek bildirimin desteklemediği bir görsel zenginliği vaat etme.

Altında “Bildirim nasıl görünecek?” satırı kısa yardım ve ilgili cihaz ayarlarına gider. Her Android cihazında aynı isimde evrensel bir “ayrıntılı bildirim” anahtarı varmış gibi anlatma. İzin, kanal, kilit ekranı görünümü ve genişletme davranışı ayrıdır. Özel ayar bulunamazsa uygulamanın bildirim ayarlarına dönüş yolu sun.

Ayrı “bildirimsiz devam et” eylemi ekleme. Sistem penceresindeki reddetme kararı kaldırılamaz veya verilmiş izin gibi yorumlanamaz. Ret sonrası sakin açıklama ve izin yeniden istenebiliyorsa yeniden deneme, aksi durumda ayarları açma sun. Otomatik izin döngüsü kurma. Geri dönüş ve uygulamadan çıkış çalışsın. İzin zaten açıksa tekrar istemeden “Tema seç” ile ilerle. Ayarlardan dönüşte gerçek durumu yeniden oku. Android izin davranışı için Kaynak 6'yı doğrula.

### 5.5 Tema seçimi

Başlık: “Nasıl bir görünüm istersin?” Açıklama: “İstediğin zaman değiştirebilirsin.” Eylem: “Bu temayla başla”.

Yalnızca iki ücretsiz tema: Açık/Sade ve Atlı Yolcu. İkisi de tam kullanılabilir. Onboarding'e Pro kataloğu, fiyat, widget satışı veya büyük bir mağaza alanı ekleme.

Önizlemeler fotoğrafın yanında aynı kısa örnek sözü ve küçük ana ekran eylemlerini gösterir. Karşılaştırma için iki kartta da metin aynıdır. Normal genişlikte iki portre kart; dar ekran/büyük yazıda seçili büyük önizleme ve iki kısa seçim kartı kullanılabilir. İkinci seçeneğin keşfini yalnızca yatay kaydırmaya bırakma.

Seçili kart: çerçeve, tik ve erişilebilir “Seçili” durumu. Yalnızca renk yetmez. Kart seçimi geçici önizlemeyi değiştirir; ana düğme tercihi kaydeder. Tema seçilirken bütün onboarding ani biçimde koyulaşmasın; görünüm kart içinde kalsın. Son onayda ana ekran ve nötr arayüz birlikte kısa geçişle değişsin.

### 5.6 Ortak yerleşim ve etkileşim

Kompakt üst bölüm: geri, küçük marka ve ilerleme. Başlangıç tipografisi Inter 26–30 sp başlık, 14–16 sp açıklama; bunlar cihazda sınanacak başlangıç aralıklarıdır. İkinci büyük slogan yok.

Ana eylem yeterli dokunma alanıyla yaklaşık 48–56 dp yükseklikte. Alt güvenli alanı korur ve son anlamlı içerikten kopmaz. İlerleme/başlık arasında dev boşluk veya açıklama/düğme arasında ekranın üçte biri kadar işlevsiz alan kalmasın. Boşluğu gidermek için görseli sınırsız büyütme; 9.6 serisindeki sahne büyütme mantığını her yeni sayfaya körlemesine taşıma.

320 dp genişlik, uzun telefon ve iki kat yazıda kontrol et. Küçük ekranda kaydırma; uzun ekranda dengeli görsel, metin ve CTA ilişkisi. Yaklaşık 180–300 ms kısa hareketler; sonsuz animasyon, otomatik karusel ve bekleten intro yok. Sistem hareket azaltma tercihine uy. Geri dönüş ve uygulamayı yeniden açma seçimleri sıfırlamasın.

## 6. Ücretsiz iki temanın tanımı

### Açık/Sade

Nötr açık gri, çok hafif gümüş ışık/doku, grafit metin. Belirli kültürel motif yok. Beyaz boş ekran hissini düşük yoğunluklu yüzey farkı ve ölçülü koyu eylemlerle gider. Griyi maviye, açık yüzeyi sarı kreme dönüştürme. Söz odakta; sayaç, gereksiz halka ve ek açıklama satırı ekleme. Kısa ve uzun sözler eylemleri kaybettirmeden dengeli görünmeli.

### Atlı Yolcu

Mevcut atlı sahnesinden nötr siyah/gümüş türev. At ve yolcu algılanabilir kalsın; tüm fotoğrafı siyaha gömme. Söz için sakin üst/sol alan, yolcu için alt/sağ kırpma değerlendirilsin. Yerel ve yumuşak karartma kullanılabilir. Her uzunluğa aynı dev fontu zorlama. Favori/paylaş eylemleri okunur açık renk alsın; seçili kalp bordo olmasın.

Keşfet ve ayarlar grafit yüzeylere geçer, atlı veya kale fotoğrafı taşımaz. Android sistem çubukları açık/koyu yüzeyle uyumlu olur.

### Ek tema kataloğu

Roma, Şövalye, Kale, Doku ve diğer mevcut paylaşım görselleri uygun kırpma/kontrast sonrası Pro ana ekran teması olabilir. Paylaşımda bulunan her dosyayı otomatik kaliteli tema sayma. Her sahneye ayrı söz alanı gerekir. Kullanılamayan seçeneklerle veya “yakında” kartlarıyla kataloğu doldurma. Video kaynaklarının sabit kapağı başlangıç için kullanılabilir; hareketli ana ekran bu fazın zorunluluğu değildir.

## 7. Keşfet: daha küçük üst alan, daha net amaç

Büyük “ASCEND / Keşfet / Sana iyi gelecek düşünceyi bul” bloğunu tamamen kaldır. Aynı yüksekliği boş dekor alanı olarak koruma.

Önerilen yapı:

1. Güvenli alanın hemen altında “Konu veya düşünür ara” alanı.
2. İki üst düzey görünüm: **Konular / Özelleştir**.
3. Konular içinde mevcut koleksiyon/konu yapısı ve küçük “Bildirim konularım” filtresi veya girişi.
4. Özelleştir içinde Temalar, Widget'lar ve Paylaşım tasarımları için üç net giriş.

Dördüncü alt navigasyon sekmesi ekleme. Kullanıcı Keşfet'teyken büyük başlıkla bunu yeniden açıklama. Yeni iki görünümü mevcut üç filtre sekmesinin üstüne bindirme; eski filtrelerin gerekenlerini Konular içinde küçük filtre veya alt sayfaya taşı. Özelleştir görünümünde konu araması yerine bağlama uygun kompakt başlık kullanılabilir; kategori aramasını widget aramasıymış gibi gösterme.

Koleksiyonlar nötr ve okunabilir kartlarla sunulur. Defne, sütun, kemer, mermer ve şövalye görsellerini kategori kartlarından çıkar. Konuya uygun sade ikonlar aynı çizgi kalınlığında kalabilir. Konuyu okumakla bildirim listesine eklemek ayrı ve anlaşılır eylemler olsun.

Kilitli konuda “Pro” işareti görünür olsun; yalnızca gri “kilitli” metnine güvenme. Kartı bütünüyle bulanıklaştırma. Koleksiyonun tamamı mı bazı konuları mı Pro, doğru anlat. Temayı değiştirmek içerik seçimini veya bildirim konularını değiştirmesin.

| Özelleştirme girişi | Açıklama | Küçük önizleme |
|---|---|---|
| Temalar | Ana ekranının görünümünü seç | Açık/Sade ve Atlı Yolcu kompozisyonları |
| Widget'lar · Pro | Sözlerini telefonunun ana ekranına taşı | Gerçekçi bir Android widget örneği |
| Paylaşım tasarımları | Sözünü görsel veya video olarak hazırla | Mevcut paylaşım tasarımından örnek |

Özelleştir bir afiş mağazasına dönüşmesin. Üç giriş yeterli; bağımsız büyük tanıtım bantları, fırsat listeleri ve reklamlar ekleme.

## 8. Tema düzenleyicisi ve paylaşım ilişkisi

Keşfet → Özelleştir → Temalar. Üstte seçili temanın okunabilir önizlemesi, altta adlandırılmış küçük kartlar ve tek Uygula düğmesi. İki ücretsiz tema ilk sırada, diğerlerinde Pro etiketi.

Kilitli tema önizlenebilir. Seçildiğinde eylem “Pro ile kullan”, ücretsiz tema seçildiğinde “Uygula” olur. Pro ekranından geri dönüş taslağı korur. Önizleme yapmak kullanım hakkı kazandırmaz. Düzenleyiciden vazgeçmek mevcut temayı değiştirmez.

Paylaşım düzenleyicisi bağımsızdır. İlk açılışta ana ekran teması başlangıç önerisi olabilir; orada farklı sahne seçmek uygulama temasını değiştirmez. Aynı fotoğrafın ana ekran, paylaşım ve widget için farklı oranlara uygun kırpmaları gerekir. 9:16 kırpmayı her yere yapıştırma.

**Mevcut ücretsiz paylaşım hakları kendiliğinden kaldırılmasın.** Kullanıcının kesin kararı ana ekranda iki ücretsiz tema ve bütün widget işlevlerinin Pro olmasıdır. Paylaşımın mevcut erişim listesini önce çıkar ve koru. Yeni hak değişikliklerini ayrıca belirt. Ortak Pro mantığı, bütün yüzeylerin aynı ücretsiz varlık listesine sahip olması anlamına gelmez.

Video ve fotoğraf seçimleri mevcut paylaşım akışında kalır. Ana ekrana veya widget'a taşınırken desteklenen sabit türev kullanılabilir. Kullanıcının kendi fotoğrafını ekleyen yeni bir galeri/izin akışı bu planın zorunlu kısmı değildir.

## 9. Widget ürünü: tamamı Pro

Widget'ın ana işi bir bakışta bir söz sunmaktır; küçük bir tam uygulama veya bildirim ayar paneli değildir. Tek birincil amaç ve farklı boyutlarda sınama yaklaşımı Android tasarım rehberine dayanır. [Kaynak 1]

Ücretsiz kullanıcı widget galerisini görebilir ve uygulama içi önizlemede tasarımı deneyebilir. Çalışan widget'ı telefon ekranına ekleme, kaydetme ve kullanma Pro gerektirir. Önizleme, ücretsiz widget hakkı diye sunulmasın.

Pro demo kullanıcısı yapılandırıp ekleyebilir. Birden fazla ücretli alt paket oluşturma. Okunurluk, büyütme ve standart boyut uyumu tüm Pro widget'larında temel kalite koşuludur.

### 9.1 Başlangıç düzenleri

- **Söz kartı:** Yaklaşık 4×2 alan için yatay söz, küçük kaynak/imza ve açma eylemi.
- **Görsel kart:** Yaklaşık 4×3 alan için söz ve arka plan görseli; sakin metin alanı korunur.

Hücre ölçüsü Android başlatıcısına göre değişir; gerçek genişlik ve yüksekliğe uyarlanır. İlk teslimde önce söz kartını tamamla, sonra görsel kartı ekle. Çok küçük 2×1 karta uzun söz sıkıştırma. Küçük boyut desteklenecekse kısa içerik ve doğru minimum boyut gereklidir.

Widget'ta Atlı Yolcu, Roma ve diğer uygun eski görseller kullanılabilir. Bu kullanıcı tarafından widget'a seçilmiş görseldir; Keşfet'in kendisini yeniden Roma'ya döndürmez.

### 9.2 Özelleştirme ekranı

Üstte gerçek boyut oranında tek canlı önizleme; telefon duvar kâğıdını temsil eden nötr yüzey üzerinde. Kocaman telefon çerçevesi alanı tüketmesin. Altında en fazla üç kontrol:

1. Arka plan: uygun mevcut görseller, sade açık ve sade koyu.
2. Düzen: söz/görsel kart ve desteklenen boyut örnekleri.
3. Yazı: okunur boyut aralığı ve güvenli hizalama.

Renk çarkı, onlarca font, serbest sürüklenen metin kutuları veya altı ayrı gölge/kontrast kaydırıcısı ekleme. Görsele uygun metin rengi ve okunurluk katmanı otomatik önerilsin. Gerçek widget çiziminde desteklenmeyen özel fontu önizlemede vaat etme; gerekirse uygun sistem fontuyla tutarlı sonuç sağla.

Başlangıç içerik önerisi: mevcut bildirim konularından sözler. Bunu küçük ve açık “Bildirim konularından” etiketiyle belirt. Görsel seçim içerik tercihini değiştirmez. Favorilerden widget gibi ek içerik modları sonraya bırakılabilir.

Pro açıkken “Telefon ekranına ekle”, kapalıyken “Pro ile kullan”; mevcut widget düzenleniyorsa “Değişiklikleri kaydet”. “Ana ekran”ın uygulama ana ekranı mı telefon ekranı mı olduğu belirsiz kalmasın.

### 9.3 Gerçek Android davranışı

- Desteklenen başlatıcıda sistemin widget ekleme akışını kullan. Desteklenmiyorsa kısa elle ekleme anlatımı sun; başarısızlıkta “eklendi” deme.
- Sistem ekleme işlemi iptal edilirse widget gerçekten yerleştirilmiş gibi başarı kaydetme.
- Android widget seçicisinden doğrudan gelen kullanıcı da Pro kontrolünden geçsin. Yetki kapalıyken çalışan söz widget'ı yerine açık Pro bilgilendirmesi göster; arka kapı bırakma.
- Her örneğin ayarı ayrı saklansın. Birini düzenlemek veya yenilemek diğerlerini istemeden değiştirmesin.
- Öneri: kartın gövdesine dokunmak görünen sözün uygulamadaki ayrıntısını açsın. Mevcut “her dokunuşta yeni söz” davranışını ayrı küçük yenileme eylemine taşı; bütün kartı sürpriz yenileme düğmesi yapma.
- Metin sığmazsa okunmaz kadar küçültme; anlamlı kesme ve tam açma yolu sun. İçerik bulunamama/erişim durumları kısa açıklamayla ele alınsın.
- Video kaynağından sabit kapak kullan. Standart Android widget'ına sürekli video veya uygulama içi kaydırma animasyonu vaat etme. [Kaynak 2]
- Widget güncellemelerini kesin dakikada teslim edilen bildirim gibi sunma. Android arka plan ve başlatıcı koşullarını hesaba kat.
- Sistem widget seçicisindeki önizlemeler gerçek tasarımla eşleşsin. Android sürümüne uygun önizleme yöntemi ve geri dönüş görseli değerlendirilsin. [Kaynak 3]
- Desteklenen cihazlarda yeniden boyutlandırma ve sonradan düzenleme çalışsın. [Kaynak 4]
- Pro kontrolü yalnızca görünür düğmede değil, yapılandırma, ekleme, çizim ve güncelleme yollarında tutarlı olsun. Demo yetkisi değişince mevcut widget'lar uygun duruma geçsin.

## 10. Pro ile sade bütünleşme

Tek paket önerisi: **Ascend Pro**. Ayrı Tema Pro, Widget Pro ve Paylaşım Pro paketleri oluşturma.

| Yetki | Ücretsiz | Pro demo |
|---|---|---|
| Temel uygulama ve bildirim planı | Mevcut ücretsiz kapsam | Aynı temel işlevler |
| Açık/Sade ve Atlı Yolcu ana ekran teması | Kullanım | Kullanım |
| Ek ana ekran temaları | Önizleme | Kullanım |
| Widget galerisi | Önizleme | Önizleme ve kullanım |
| Widget ekleme/düzenleme/çalışan içerik | Yok | Var |
| Konular | Mevcut ücretsiz konular | Mevcut Pro konu erişimi |
| Paylaşım | Mevcut ücretsiz haklar | Mevcut Pro kapsam ve açıkça tanımlanan yeni eşlemeler |

Pro ekranı bağlama uygun örnek gösterir: widget'tan gelene seçtiği widget, temadan gelene tema. En fazla üç fayda: Tüm temalar, Özelleştirilebilir widget'lar, Pro konular ve paylaşım araçları. Gerçekte olmayan erişimi vaat etme.

Demo eylemi “Pro demosunu etkinleştir”, açıklama “Bu sürümde ödeme alınmaz”. Uydurma fiyat, geri sayım, sahte indirim, gerçek satın alma tamamlanmış mesajı veya otomatik abonelik yok. Seçim korunarak editöre dön; demo açılınca widget kullanıcı onayı olmadan yerleştirilmesin.

Gelir mantığı önerisi: ücretsiz iki güçlü görünüm ürün kalitesini gösterir; Pro daha geniş görsel seçim ve telefon ekranında kullanım sağlar. Widget'ların tamamının Pro olması kullanıcı kararıdır. Bunun dönüşüm etkisi denenmeden “kesin daha çok kazandırır” veya “piyasada en çok tutulan model” deme. Fiyat ve gerçek ödeme yayını ayrı gelecek kararıdır. Yeni analitik altyapısı da bu tasarım işinin otomatik parçası değildir.

## 11. Eski kullanıcılar ve geçiş politikası önerisi

Favori ve bildirim planını sıfırlama; eski kullanıcıyı tüm onboardinge zorla geri sokma.

Öneri: eski ana ekran tercihi kullanıcı yeni tema seçene kadar korunabilir; yeni katalogda iki ücretsiz tema açıkça sunulur. Eski Roma tercihi Pro satın alımı sayılmaz. Kullanıcı ücretsiz iki temadan birine geçtikten sonra normal katalog kuralları uygulanır. Bu, önerilen geçiş istisnasıdır; üçüncü bir ücretsiz katalog teması veya daha önce verilmiş ödeme hakkı değildir.

Eski temel widget'lar yeni “tamamı Pro” kararından etkilenir. Ayarları silme veya widget'ı telefon ekranından zorla kaldırma. Pro kapalıyken nötr “Widget'lar artık Pro kapsamında” durumu ve uygulamaya açma yolu göster; çalışan içerik güncellemesini Pro kuralına göre durdur. Pro demo tekrar açıldığında korunmuş tasarım geri gelsin. Demo kapatılırken aktif widget'lara etkisini bildir. Bu geçiş hem uygulamadan hem sistem seçicisinden sınansın.

## 12. Görsel sistem

Inter arayüz ailesi korunsun. Lora söz alanlarında kontrollü kalabilir; onboarding başlıkları ve kategori isimleri yeniden serifleşmesin. Dil ekranı aynı aileyi kullanır.

Öneri paleti:

| Rol | Açık | Koyu |
|---|---|---|
| Zemin | #ECEDEF | #151618 |
| Kart | #F7F7F8 | #222428 |
| İkincil yüzey | #DFE1E4 | #2D3035 |
| Ana yazı | #191A1D | #F4F5F6 |
| İkincil yazı | #575B63 | #B9BDC4 |
| Ana eylem | Grafit dolgu / açık yazı | Açık dolgu / koyu yazı |

Bunlar başlangıç önerileri; gerçek eşleşmelerde ölçüm yapmadan erişilebilirlik iddiası yok. Fotoğraf üzerindeki yerel kontrast düz renk hesabıyla geçiştirilemez. Bordo, altın, sarı, turuncu ve mavi vurgu sistemi ekleme.

Tek çizgi ikon ailesi, tutarlı 20–24 dp görünür ölçü ve yeterli dokunma alanı. Pro için altın taç yerine sade yazı etiketi. Seçim yalnızca renk değil tik/çerçeve ve erişilebilir durumla anlatılır. Köşeler yaklaşık 16–22 dp aralığında tutarlı; bütün arayüz dev kapsüllere dönüşmesin.

Hareket seçimi açıklamalıdır: tema değişiminde kısa geçiş, kartta seçim geri bildirimi, widget'ta önizleme yenilenmesi. Sonsuz animasyon, otomatik karusel, dikkat çeken sürekli parıltı ve geciktirilmiş CTA yok.

## 13. Fazlara bölünmüş yol haritası

### Faz 1 — Nötr kimlik ve yeni onboarding

Dil başlangıcı, nötr tanıtım, birleşik bildirim düzeni, izin adımı ve iki ücretsiz tema seçimi. Ana ekran görseli ile nötr açık/koyu görünümü ayır; önizleme ve kaydetmeyi bağla.

Çıkış koşulu: Beş adım tamamlanır. Tema önizlemesi gerçek ana ekranla eşleşir. Seçimler geri dönüşte korunur. Bildirim davranışı bozulmaz. İki ücretsiz tema da kullanılabilir. Widget ve geniş Pro kataloğu bu fazı geciktirmesin.

### Faz 2 — Keşfet ve özelleştirme merkezi

Büyük üst tanıtım bloğunu kaldır. Konular / Özelleştir ayrımını kur. Kategori yüzeylerinden Roma motiflerini çıkar. Tema galerisi ve uygun paylaşım görsellerinin Pro tema eşlemesini yap. Pro konu işaretlerini netleştir; mevcut ücretsiz paylaşım haklarını koru.

Çıkış koşulu: Arama üstte hemen bulunur. Tema az adımda değiştirilir. Atlı tema seçiliyken Keşfet nötr koyudur ve tematik figür içermez. Yeni alt navigasyon veya ikinci kalabalık filtre katmanı oluşmaz.

### Faz 3 — Pro widget ve gerçek Android doğrulaması

Mevcut widget yolunu geliştir. Önizleme, başlangıç düzenleri, mevcut görsellerle özelleştirme, Pro kontrolü, sisteme ekleme, yeniden boyutlandırma ve örnek başına bağımsız tercihleri tamamla. Demo Pro ekranı bağlama uygun örnek gösterir.

Çıkış koşulu: Önizleme ile eklenen gerçek widget eşleşir. Pro kapalı/açık, doğrudan sistemden ekleme, eklemeyi iptal etme, birden fazla widget, boyutlandırma, yeniden açma ve güncelleme senaryoları çalışır. Video widget vaadi yok.

### Sonraya bırakılacaklar

Gerçek Play Billing, fiyat ve abonelik stratejisi; çok sayıda dil için yeni altyapı; canlı video ana ekranı; sürekli animasyonlu widget; serbest tasarım editörü; yapay zekâ görsel üretimi; sosyal akış; hesap/bulut. Yeni talep olmadan bunları ekleme.

## 14. Kabul ve görsel inceleme listesi

- [ ] İlk ekranda Türkçe/English var; seçili durum anlaşılır.
- [ ] Onboarding beş adım; tarihî figürlü karşılama yok.
- [ ] Tema seçimi bildirim izninden sonra geliyor.
- [ ] Adet ve saat aralığı aynı anlaşılır sayfada; bilgi tekrarları yok.
- [ ] Ayrı bildirimsiz devam yok; sistem ret kararı doğru ele alınıyor.
- [ ] İzin açıkken gereksiz yeniden izin penceresi gösterilmiyor.
- [ ] Ücretsiz iki tema var; Atlı Yolcu doğru görseli kullanıyor.
- [ ] Önizleme söz yerleşimini gösteriyor; Uygula öncesi kalıcı seçim değişmiyor.
- [ ] Ana ekran seçimi paylaşım taslağını ve mevcut widget'ları değiştirmiyor.
- [ ] Keşfet'te eski büyük tanıtım alanı ve klasik motifler yok.
- [ ] Koyu tema Keşfet'i koyulaştırıyor ama oraya at/kale taşımıyor.
- [ ] Özelleştirme kategori aramasını gizlemiyor; yeni alt navigasyon yok.
- [ ] Pro konu etiketleri doğru erişim kapsamını anlatıyor.
- [ ] Bütün kullanılabilir widget işlevleri Pro; doğrudan sistem girişinde de geçerli.
- [ ] Önizleme ve gerçek widget'ın font, kırpma, renk ve boyutu denetlenmiş.
- [ ] Bir widget'ı düzenlemek diğerlerini değiştirmiyor.
- [ ] Ekleme iptal edilirse başarı gösterilmiyor.
- [ ] Widget gövdesi görünen sözü açıyor; yenileme eylemi açıkça ayırt ediliyor.
- [ ] Pro demosunda gerçek ödeme yapılmış gibi ifade yok.
- [ ] Eski favoriler, içerik ve bildirim planları korunmuş.
- [ ] Türkçe uzun başlıklar, İngilizce, kısa/uzun sözler ve iki kat yazı denenmiş.
- [ ] Dar/uzun ekranlarda CTA ve geri eylemi erişilebilir.
- [ ] Sistem çubukları ve alt güvenli alan açık/koyu yüzeylerle uyumlu.
- [ ] Her sayfada üst boşluk, CTA mesafesi, kontrast ve dokunma hedefleri incelenmiş.

Son APK üretildikten sonra gerçek Android ekran görüntülerini aç. Her önemli kusur için “nerede / neden / düzeltme / son görüntü” kaydı tut. Görsel inceleme yerine yalnızca test sonucu sunma. Widget için mümkünse iki Android başlatıcısında ekleme ve boyutlandırma dene; denenmeyeni açıkça belirt. Aynı kaynak commit'inin APK'sı ve kanıtlarıyla teslim et; kontrol edilmemiş son dakika kodunu farklı APK'ya ekleme.

## 15. Beklenen uygulama teslimi

1. Uygulanan fazlar ve kalan kapsamın kısa kaydı.
2. Son kaynak sürümüne ait indirilebilir APK.
3. Beş onboarding sayfası, iki ana ekran teması, açık/koyu Keşfet, tema galerisi, Pro widget düzenleyicisi ve gerçek widget görüntüleri.
4. Araştırma dayanakları ve tasarım kararları.
5. Görsel kusurlar ve yapılan düzeltmeler.
6. Demo kalan alanlar: özellikle dil bağlantısı ve ödeme altyapısı.

“Kusursuz”, “herkes indirecek” veya “milyonlar kazandırır” yerine değişen davranışı ve gerçek görüntüleri göster. Sadelik hedefini koru: her kontrol hangi kullanıcı kararını kolaylaştırdığını açıklayabilmeli.

## 16. Araştırma kaynakları

1. [Android: Widget tasarım rehberi](https://developer.android.com/design/ui/mobile/guides/widgets) — Tek birincil kullanım amacı ve farklı boyutlarda değerlendirme; söz odaklı düzenin dayanağı.
2. [Android: App widgets overview](https://developer.android.com/develop/ui/views/appwidgets/overview) — Widget etkileşim ve yerleşim sınırları; tam uygulama veya sürekli video deneyimi vaat etmeme yaklaşımı.
3. [Android: Widget önizlemeleri](https://developer.android.com/develop/ui/views/appwidgets/previews) — Sistem seçicisindeki önizlemenin gerçek görünümle eşleşmesi ve sürüme uygun sunum.
4. [Android: Widget yapılandırması](https://developer.android.com/develop/ui/views/appwidgets/configuration) — İlk kurulum ve sonradan düzenleme.
5. [Android: Widget kalite rehberi](https://developer.android.com/docs/quality-guidelines/widget-quality) — Okunurluk, işlev, boyut ve keşfedilebilirlik kontrolü.
6. [Android: Bildirim izni](https://developer.android.com/develop/ui/compose/notifications/notification-permission) — Gerçek izin durumunu izleme ve kullanıcı kararıyla ilerleme sınırları.

Bu kaynaklar teknik ve kullanılabilirlik dayanağıdır. İki ücretsiz tema, tüm widget'ların Pro olması, sekme isimleri, renkler ve beş adım Ascend'e özel kararlardır; Android'in zorunlu ticari modeli değildir. Widget'ların piyasada en çok tercih edilen özellik olduğu veya belirli bir gelir sağlayacağı bu araştırmada doğrulanmış değildir.
