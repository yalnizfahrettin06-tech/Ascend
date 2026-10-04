# Ascend 6 — Test ve kabul planı

**Tarih:** 8 Eylül 2026

**Hedef:** Ascend 6.0.0 / versionCode 15

**Durum:** GitHub Actions üzerinde APK, 48 JVM ve 26 Android testi geçti; lint hatası yok. Gerçek ekranlar ve dışa aktarılan medya incelendi. Fiziksel Samsung/OEM, animasyon hissi ve eski APK üzerine uyumlu imzayla güncelleme ayrıca doğrulanmalıdır.

## 1. Doğrulama ortamı ve kanıt düzeni

Ağır Android derlemesi ve emülatör kullanıcı bilgisayarında çalıştırılmaz. `.github/workflows/android.yml`, GitHub Actions üzerinde APK, birim testleri ve lint çalıştırır; ardından API 35, Google APIs, x86_64, Pixel 2 profiliyle Android testlerini başlatır. Yerelde içerik, görsel eşleme ve dosya kontrolleri yapılabilir.

Her teslim için aşağıdakiler aynı kod sürümüne bağlanmalıdır:

- Tam test edilen commit kimliği ve Actions bağlantısı.
- JVM ve Android testlerinin XML/HTML sonuçları; hata ve atlanan test sayısı.
- Lint hata sayısı ve önemli uyarıların açıklaması.
- Gerçek uygulama ekran görüntüleri; yalnız tasarım önizlemesi yeterli değildir.
- Oluşturulmuş PNG ve oynatılabilir MP4 örneği.
- APK sürümü, paket adı, imza doğrulaması, boyutu ve SHA-256 özeti.

Actions emülatör ayarında animasyonlar kapalıdır. Bu ortam durum geçişi, yerleşim ve erişim davranışını sınar; **180 ms geçişin hissini, akıcılığını veya gerçek cihaz pil koşullarını doğrulamaz**. Animasyon kalitesi ayrıca normal animasyon ölçeğiyle gözlenmelidir. Tek cihaz profili bütün Android cihazlarını temsil etmez.

## 2. İçerik ve görsel paket kabulü

| Kod | Kontrol | Geçme ölçütü / kanıt |
|---|---|---|
| C01 | İngilizce ham kaynak | Tek kanonik dosyada 70 kategori ve 700 kayıt vardır; her kategoride 10 ayrı kayıt bulunur. |
| C02 | Türkçe eşleşme | 700 Türkçe çeviri aynı sabit kimliklerle eşleşir; eksik/yetim kayıt veya kaynak özeti uyuşmazlığı yoktur. |
| C03 | Üretilmiş uygulama verisi | `tools/icerik_derle.py --check` değişiklik gerektirmeden geçer. |
| C04 | Dil ve arşiv | TR/EN aynı söz kimliğini kullanır; eski 82 arşiv kimliği çözülür, yeni akışa ve bildirim havuzuna eklenmez. Desteklenmeyen içerik dili İngilizceye döner. |
| C05 | Özgün düşünce etiketi | Düşünür/inanç kategorileri doğrudan alıntı veya kutsal metin gibi sunulmaz; kategori ayrıntısı ayrımı açıklar. |
| G01 | 70 görselin envanteri | `docs/category-art.json` bütün kategori anahtarlarını bir kez içerir; her paketlenmiş dosyanın yolu, boyutu, özeti ve üretim istemi vardır. |
| G02 | Görsel kaynak kontrolü | `tools/check_category_art.py` 70 ayrı kaynak ve çıktı özetini, WebP başlığını, boyut sınırlarını ve Kotlin eşlemelerini doğrular. |
| G03 | Görsel anlamı | Kategori isimli bir temas sayfasında 70 görsel gözle incelenir. Yanlış konu, fark edilmeyen tekrar, kesilmiş ana konu, bozuk anatomi/nesne veya görsel içine üretilmiş yazı işaretlenip düzeltilir. Farklı dosya özeti tek başına geçme ölçütü değildir. |
| G04 | Yeni geniş sahneler | Toplam 23 sahne; 6 Manzara, 10 Efsane ve 7 Doku içinde eksiksiz görünür. Yeni Kar muhafızı, Atlı yolcu, Orman muhafızı, Taş salon, Çöl yolcusu, Kıyı nöbeti ve Kale nöbeti görselleri birbirinden ayrılır. Doku renkleri adlarına uyar. Bütün sahneler ana ekranda seçilir; Zirve dışındakiler paylaşımda Pro olarak görünür. Uzun EN etiketleri 1,5× yazıda kesilmez. |
| G05 | Fotoğraf üzerinde metin | Açık/koyu bölgelerde söz, imza, eylemler ve kategori adı okunur. Kısa/uzun TR/EN örnekleri ve kare/dikey/yatay paylaşım ayrı incelenir. |
| G06 | Marka simgeleri | A–dağ işareti dairesel ve yuvarlak kare launcher kırpımında kesilmez; küçük boyutta okunur. Bildirim ve monochrome simge dolu kare olarak görünmez. |
| G07 | Ana ekranın arka plan penceresi | İki sütunlu ızgarada üç filtre çalışır; her ailenin son öğesine ulaşılır. Kıyı nöbeti seçip pencere yeniden açıldığında Efsaneler filtresi ve seçili kart görünür. Yalnız Dokular filtresine dokunmak ana arka planı değiştirmez. Otomatik satırı her filtrede ulaşılabilir; seçince konuya göre görsel geri gelir. Seçili işareti ile TalkBack seçim durumu uyuşur. |

İlgili kaynaklar: `KatalogTest`, `SozUzunlukTest`, `KatalogDepoTest`, içerik derleyicisi ve görsel doğrulayıcısı. Görsel üretim ve arayüz doğrulaması bitmeden G01–G07 tamamlandı olarak işaretlenmez.

## 3. Erişim ve geçiş: veri kaybını önleyen kabul durumları

| Kod | Başlangıç ve eylem | Beklenen sonuç |
|---|---|---|
| E01 | Temiz kurulum; Pro kapalı | Yalnız Motivasyon, Kendime nazik davranmak, Marcus Aurelius, Derin Odak, Azim ve Biraz yavaşlamak açıktır. Toplam 6 kategori / 60 söz. |
| E02 | Yeni kurulumda onboarding tamamlanır; veri okuyucuları farklı sırayla başlar | Kurulum eski kullanıcı sanılıp 25 kategori açılmaz. Yeniden başlatmada sayı 6 kalır. |
| E03 | V5 verisi: onboarding tamamlanmış, eski ücretsiz aileler ve açılmış bir ücretli aile mevcut | Önceki 25 ücretsiz kategori ve kazanılmış ailenin bütün altları erişilebilir kalır. Kaydedilenler, geçmiş ve geçerli seçim silinmez. |
| E04 | Aynı eski veri ikinci kez okunur; erişim geçişi tekrar tetiklenir | Sonuç değişmez. Daha sonra açılmış kategori kaybolmaz; geçiş ikinci kez başlangıç durumuna dönmez. |
| E05 | Birden çok okuyucu ve tek kategori açma aynı anda çalışır | Yeni ödül kaybolmaz; eski haklar korunur; son seçim kullanılabilir kategorilerden oluşur. |
| E06 | Kilitli Özgüven kategorisi demo ile açılır | Yalnız `ozguven` kazanılır ve seçilir. Aynı ailenin Korkuyla Yüzleşme/Reddedilme gibi kardeşleri açılmaz. |
| E07 | Aynı ödül tekrar işlenir | Yeni veya yinelenen başka hak eklenmez; veri tutarlılığı korunur. |
| E08 | Kilitli ya da bilinmeyen kategori doğrudan seçim işlemine gönderilir | Bildirim havuzuna girmez. En son geçerli seçim veya güvenli başlangıç seçimi korunur. |
| E09 | Son seçili kategori çıkarılmaya çalışılır | Havuz boşalmaz. Ekran başka bir konu seçilmesi gerektiğini açıklar. |
| E10 | Eski APK üzerine kurulum denenir | Yalnız imzalar uyumluysa veriyi koruyan yerinde güncelleme kanıtı sayılır. İmza farkı yüzünden silip kurma, geçiş testi olarak raporlanmaz. |

İlgili otomatik testler: `ErisimTest`, gerçek ve izole Android DataStore kullanan `ErisimDepoTest`, mevcut arşiv/geçmiş testleri. Gerçek kullanıcı veri deposu test için temizlenmez. Sabit test imzası yoksa E10'un fiziksel güncelleme kısmı ayrı sınırlama olarak yazılır.

## 4. Google.com kategori demosu

Bu bölüm gerçek reklam SDK'sını sınamaz. Demosu yapılan sözleşme: açık bilgilendirme → kullanıcının bağlantıyı açması → tarayıcıya gerçek ayrılma → uygulamaya dönüş → yalnız seçilen kategorinin verilmesi.

| Kod | Eylem | Beklenen sonuç |
|---|---|---|
| D01 | Kilitli kategori ayrıntısı açılır | 10 söz kapsamı ve iki örnek görünür; başlık açılmadan bütün bildirim havuzuna katılmaz. |
| D02 | Demo açıklaması okunur | Gerçek reklam olmadığı, Google.com açılacağı, dönüşte yalnız bu kategorinin açılıp bildirimlere ekleneceği anlaşılır. Vazgeç yolu çalışır. |
| D03 | Açma reddedilir veya tarayıcı niyeti başlatılamaz | Hak verilmez; hata sonrası yeniden deneme mümkündür. |
| D04 | Başarılı tarayıcı açılışı ve gerçek uygulama ayrılma/dönüşü | Yalnız seçilen kategori verilir. Kategori kartı, seçili konular, ana akış ve veri deposu aynı sonucu gösterir. |
| D05 | İlk lifecycle RESUME, art arda tıklama, tekrarlı dönüş | Sahte ilk dönüş ödül vermez; tamamlanmış dönüş ikinci kez farklı kategori açmaz. |
| D06 | Demo beklerken Activity yeniden yaratılır | Bekleyen kategori doğru korunur; yanlış kategori açılmaz. |

Chrome dönüş testinde uygulamanın gerçekten arka plana geçtiği görülmeli; ardından sistem üzerinden uygulamaya dönülmelidir. Testi geçirtmek için uygulama içi hak verme işlevini doğrudan çağırmak, D04'ün yerine geçmez.

Bu demo, sayfanın yüklenmesini veya izlenme süresini ölçmez. İnternetin olmaması ile Android'in tarayıcı açılışını reddetmesi aynı olay değildir. Gerçek reklam tamamlanması ya da gelir üretimi bu sonuçlardan çıkarılmaz. `DemoReklamTest` ve ana `UygulamaTest` bu ayrım korunarak çalıştırılmalıdır.

## 5. Pro demosu ve paylaşım erişimi

| Kod | Eylem | Beklenen sonuç |
|---|---|---|
| P01 | Pro teklifi açılır | “PRO DEMO” görünür. “Ödeme alınmaz. Abonelik başlatılmaz.” cümlesi etkinleştirme düğmesinin yanında görünür; hiçbir fiyat/ödeme akışı açılmaz. |
| P02 | Pro demosu etkinleştirilir | 70 kategori erişilir; mevcut bildirim seçimi genişlemez. Seçilen 3 konu varsa 3 olarak kalır. |
| P03 | Uygulama yeniden başlatılır | Pro demo durumu korunur; bu durum satın alma kaydı olarak adlandırılmaz. |
| P04 | Pro ile birkaç yeni konu seçilip demo kapatılır | Yalnız ücretsiz veya tek tek kazanılmış kategoriler kalır. Seçimden erişimi bitenler çıkarılır. |
| P05 | Yalnız Pro kategorileri seçiliyken Pro kapatılır | Kullanılabilir başlangıç konularına dönülür; boş bildirim havuzu ve çökme olmaz. |
| P06 | Tek kategori kazanıldıktan sonra Pro açılıp kapatılır | Tek tek kazanılmış kategori ve eski haklar korunur. |
| P07 | Ücretsiz kullanıcı üç temel arka planı seçer | Zirve, Gece ve Kâğıt ayrı ayrı standart Story PNG olarak paylaşılabilir/kaydedilebilir; Pro penceresi açılmaz. |
| P08 | Ücretsiz kullanıcı diğer sahne/renk/gradyan/konu görseli/fotoğraf seçer | Pro açıklaması açılır; ücretli seçenek sessizce uygulanmaz. |
| P09 | Ücretsiz kullanıcı Video veya Biçim ve yazı seçer | Pro açıklaması açılır. İptal edilince ücretsiz görsel düzeni değişmez. |
| P10 | Pro video ve 45s seçilir; Activity yeniden yaratılır | Video ve 45s seçimi geri gelir. Gerçek Pro durumu yüklenirken geçici başlangıç değeri kayıtlı tercihi yanlışlıkla silmez. |
| P11 | Pro ayarları kayıtta bulunduğu halde etkin erişim ücretsizdir | Önizleme ve çıktı ücretsiz kurala döner. Gelişmiş biçim, fotoğraf, font, boyut, hizalama veya video kayıtlı durum üzerinden kaçamaz. |
| P12 | Dışa aktarım sırasında Pro kapatılır | İş iptal edilir; tamamlanmamış dosya başarı gibi sunulmaz. Ücretsiz ayarlar gösterilir ve yeni ücretsiz işlem yapılabilir. |
| P13 | Android dosya seçicisinde bekleyen Pro çıktısı varken erişim kaldırılır | Dönüşte o premium dosya kaydedilmez. |
| P14 | Paylaşım arka plan filtreleri ve seçim korunması | İlk görünüm Ücretsiz'de Zirve, Gece ve Kâğıt görünür. Beş filtre birlikte 37 hazır zeminin tamamına erişim sağlar; konu görseli Manzaralar'da bulunur, fotoğraf düğmesi her filtrede erişilebilirdir. Pro ile Kale nöbeti seçip Renkler'e geçince önizleme ve Seçili satırı Kale nöbeti olarak kalır. Pro kapatılınca ücretsiz zemin ve Ücretsiz filtresi gösterilir. |

`PaylasimErisimiTest` bütün arka plan ve gelişmiş ayar kurallarını sınar. `ErisimDepoTest` Pro'nun veri ve seçim etkisini sınar. Ana UI testi ücretsiz video kapısını, demo etkinleştirmeyi ve video durumunun yeniden yaratılmasını birlikte doğrulamalıdır. P12/P13 için ayrı test veya gözlem kaydı yoksa otomatik doğrulandı denmez.

Kararlı ekran etiketleri: `pro-sheet`, `pro-demo-enable`, `pro-demo-disable`, `pro-close`, `share-image`, `share-video`, `share-duration-45`, `share-background-summit`, `share-background-night`, `share-background-paper`, `share-background-topic`, `share-advanced`, `share-preview`. Yeni arka plan seçiminde `scene-picker`, `scene-grid`, `scene-filter-manzara/efsane/doku`, `scene-choice-<küçük harfli enum>`, `scene-auto`, `scene-close` kullanılır. Paylaşım filtreleri `share-filter-ucretsiz/manzara/efsane/doku/renkler`, seçenekler `share-background-<küçük harfli enum>` ve mevcut seçim `share-selected-background` etiketindedir. Örnek kabul yolu: `scene-choice-bakir_doku` → Activity yeniden yaratma → seçili Bakır; ücretsiz `share-background-bakir_doku` → Pro kapısı → `share-background-kale_nobeti` → gerçek PNG.

## 6. Gerçek PNG, MP4 ve kaydetme

Dosyanın yalnız oluşturulmuş olması yeterli değildir; çözülmesi veya oynatılması gerekir.

| Kod | Kontrol | Geçme ölçütü |
|---|---|---|
| M01 | Ücretsiz Story PNG | Gerçek dosya açılır; boyut 1080×1920; metin ve imza önizlemeyle aynı; boş/siyah kart değildir. |
| M02 | Pro kare/yatay PNG | Gerçek dosya seçilen 1080×1080 veya 1920×1080 oranındadır; yazı kenardan kesilmez. |
| M03 | MP4 süreleri | 5, 10, 30 ve 45 saniye ayrı ayrı üretilir. Dosya oynatılabilir; bildirilen süre hedefe yakın, geç bölümden çözülen kare okunabilir olmalıdır. Mevcut testte süre toleransı 150 ms'dir. |
| M04 | Video ölçüsü ve içerik | Varsayılan Story videoda genişlik 720 pikseldir; seçilen arka plan ve imza bulunur; animasyon sonunda metnin tamamı okunur. Video sessizdir. |
| M05 | Galeri kaydı | MediaStore kaydı okunur, `IS_PENDING` sıfırdır; başarı mesajı kayıt tamamlanınca görünür. |
| M06 | Paylaşma | Android paylaşım seçicisi doğru MIME türü ve okunabilir URI alır. En az bir hedef uygulamada görsel/video açma gerçek cihazda ayrıca denenir. |
| M07 | Kullanıcının fotoğrafı | Seçilen fotoğraf açılır; iptal edilince önceki arka plan kalır. Okunamayan fotoğrafta hata ve alternatif arka plan seçimi mümkündür. |
| M08 | İptal/hata | Kullanıcı iptalinde başarı mesajı çıkmaz. Yetersiz alan/çizim hatası kullanıcıya açıklanır; sonraki işlem yeniden başlatılabilir. |

Mevcut `MedyaTest` dört video süresini, varsayılan PNG ölçülerini, galeri kaydını ve uzun bildirimi sınar. Pro kare/yatay, fotoğraf, gerçek hedef uygulama ve iptal yolları için kanıt ayrıca kaydedilir; mevcut üç medya testinin geçtiği bilgisi bütün M maddelerine yayılmaz.

## 7. Bildirim planı, izin ve seçim

| Kod | Eylem | Beklenen sonuç |
|---|---|---|
| N01 | Sayı 1'e indirilir, 7'ye çıkarılır | Sınırlar aşılmaz. İlgili eksi/artı düğmesi sınırda kapanır; erişilebilir açıklamalar doğru kalır. |
| N02 | Başlangıç/bitiş değiştirilir | Başlangıç 0–23, bitiş başlangıçtan sonraki saat–24 aralığındadır. Ters veya eşit aralık seçilemez. |
| N03 | 09:00–21:00 ve 3 bildirim | Ekrandaki plan 11:00 / 15:00 / 19:00 gösterir; zamanlayıcı aynı hesaplayıcıyı kullanır. |
| N04 | Tek bildirim, 09:00–21:00 | Örnek saat 15:00 olur. Çalışan zamanlayıcı yalnız gelecekteki teslimatları planlar; Günün planı tam gün örneğidir. |
| N05 | Bitiş 24 seçilir | Kullanıcıya 00:00 ve Gece yarısı birlikte açıklanır. Sonraki günün başlangıcıyla yanlış tarih oluşturulmaz. |
| N06 | Plan veya saat seçicisi açıkken Activity yeniden yaratılır | Adım, sayı, saatler ve açık seçim korunur. Başlık ilerleme çizgisiyle çakışmaz. |
| N07 | Sistem bildirim izni reddedilir | İzin verilmiş gibi işaretlenmez. Kullanıcı bildirimsiz ilerleyebilir; okuma/kaydetme/paylaşma çalışır. |
| N08 | İzin verilir, başlangıç ayrıca onaylanır | İzin verme tek başına onboarding'i tamamlamaz; kullanıcının son eylemi ve seçili plan kaydedilir. |
| N09 | İzin ayarlardan kapatılıp uygulamaya dönülür | Güncel izin durumu görünür; önceki açık durumdan başarı izlenimi kalmaz. |
| N10 | Bildirim görünümü/üretici rehberi açılır | Ayrıntılı metin gösterme ve uygulama bildirim ayarları anlatılır; rehber zorunlu bir reklam/Pro adımına dönüştürülmez. |
| N11 | Uzun söz gerçekten bildirilir | Genişletilmiş bildirimde sözün tamamı ve imza bulunur; kaydetme eylemi doğru kimliği kullanır. |
| N12 | Kategori seçimi veya Pro erişimi değişir | Ana akış, sıradaki bildirim planı ve widget aynı erişilebilir seçimden beslenir; kilitli veya kaldırılmış konu yeni bildirim havuzunda kalmaz. |
| N13 | Seçilen içerik havuzunda iki tam bildirim turu | Seçilmemiş kategoriye çıkılmaz; kullanılmamış söz varken erken tur başlamaz; tur sınırında hemen aynı söz tekrarlanmaz. |

İlgili kaynaklar: `BildirimZamanlari`, `OnboardingTest`, `KatalogTest`, `KatalogDepoTest`, `MedyaTest` ve kök uygulama testi. Gerçek güç tasarrufu, yeniden başlatma ve Samsung/OEM davranışı yalnız Pixel emülatörüyle kanıtlanmaz; ayrı cihaz gözlemi olarak işaretlenmelidir.

## 8. Görsel kabul ve erişilebilirlik

Ekran görüntülerini kontrol eden kişi yalnız ekranın açılmasına bakmamalı; şu görevleri doğrudan yapabilmelidir:

1. Ana ekranda ilk görünen şeyin söz ve dağ görseli olduğunu ayırt etmek; kaydet/paylaş/dinle/kopyala eylemlerine rahatça ulaşmak.
2. Bildirim konularını adlarıyla bulmak ve bunları okuma filtresinden ayırmak.
3. Keşfet'te Motivasyon, Azim ve Pes Etmemek'i ayrı bulmak; yalnız bir kategoriyi açmak; açık ama seçilmemiş durumu ayırt etmek.
4. Bildirim ekranında sayı ve saatleri sürgü hassasiyetine ihtiyaç duymadan değiştirmek.
5. Alt gezinmede Bugün dağ ikonunu, seçili sekmeyi ve bütün etiketleri okumak.
6. Yolculuk'ta gerçek son yedi günü, sıfır veya tek günlük başlangıcı ve mevcut sayaçları anlamak.
7. Pro ücret ödemeden denenirken gerçek bir satın alma olmadığını görmek.

TR ve EN, açık ve koyu tema; normal yazı ve en az **1,5× yazı ölçeği** incelenmelidir. 320–360 dp dar genişlikte uzun etiketler ayrıca kontrol edilir. Hiçbir başlık ilerleme çizgisinin altına taşmamalı; düğme metinleri kırpılmamalı; dört alt sekme erişilebilir kalmalıdır. Tıklanabilir öğeler için en az 48 dp hedef gözetilir; tarih noktası ve dekoratif simge gibi tıklanmayan öğeler bu gereksinimle karıştırılmaz.

TalkBack denetiminde sekmeler seçili durumunu, kategori kartları erişim/seçim durumunu, arka planlar isimlerini, haftalık günler tam tarihi ve ziyaret bilgisini okumalıdır. Dekoratif fotoğraf/path öğeleri aynı metni ikinci kez okutmaz. Renk tek durum taşıyıcısı olmamalıdır.

Normal animasyon ölçeğinde sayı geçişi, alt sekme vurgusu ve kaydetme geri bildirimi kısa ve sakin gözlenmelidir. Sistem animasyon ölçeği sıfır olduğunda gezinme ve son durum yine doğru olmalıdır. Bu kontrole ait hareketli kanıt yoksa yalnız statik yerleşim doğrulandı denir.

## 9. Kanıt dosyaları ve sonuç kapısı

Ekran kanıtı için asgari set: karşılama, altı konu, yeni bildirim planı, izin, ayrıntı rehberi, ana ekran, ücretsiz paylaşım, Pro açıklaması, video/süre, kaydedilenler, Keşfet, iki örnekli kategori ayrıntısı, tek kategori demo öncesi/sonrası, Yolculuk ve İngilizce ana ekran. Büyük yazı ile arka plan penceresinin Manzaralar/Efsaneler/Dokular aileleri ve son öğeleri ayrıca görünmelidir. Yedi şövalye varyantı ve yedi dokunun tamamı önizlenir. Paylaşımda beş filtre üzerinden 37 hazır zemine, ayrıca kategori görseline ve kişisel fotoğraf seçimine ulaşılabilmelidir. Filtre değiştirme ile arka plan seçme işlemlerinin ayrı kaldığı ekran kaydıyla doğrulanır.

Teslim kapısı, aşağıdaki kritik durumlar çözülmeden tamamlanmaz:

- Derleme/lint hatası veya test çökmesi.
- Kaydedilmiş içerik/hak kaybı; yeni kurulumun yanlış eski erişim alması.
- Pro ya da tek kategori açmanın belirtilenden fazla bildirim konusu seçmesi.
- Ücretsiz/pro erişiminin önizleme veya dışa aktarımda aşılması.
- İzin reddinde kapanan temel kullanım veya izin verilmiş gibi gösterilen durum.
- Yanlış kategori görseli, eksik kaynak, okunmayan ana kart veya kırpılan temel eylem.
- Oynatılamayan video, boş PNG veya tamamlanmamış kayda başarı mesajı.

Kullanılmayan eski kaynaklar gibi düşük etkili uyarılar sonucu ayrı açıklanabilir. Bir uyarının bulunması otomatik başarısızlık değildir; bir testin yeşil olması da görsel hatayı kabul edilebilir yapmaz.

### Son çalıştırma kaydı — doğrulandı

| Kanıt | Sonuç |
|---|---|
| Test edilen commit | `b8d2d6ff6621ee896d1d03c4101aeae9dcaedc8b` |
| GitHub Actions çalışması | [Başarılı çalışma 34229375936](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34229375936) |
| APK sürümü / paket / boyut | 6.0.0 (15) · `com.yalnizfahrettin.azim.debug` · 30,223,088 bayt |
| APK SHA-256 / imza doğrulaması | `74bb4c86054892cd873d3201dd959fcc348bacdf718631aba8b9424572011fcc` · apksigner doğrulandı |
| İçerik ve görsel doğrulaması | 700 EN + 700 TR; 70 kategori ve 19 yeni sahne benzersiz; 23 sahne kaynağı mevcut |
| JVM testleri: geçen / hatalı / atlanan | 48 / 0 / 0 |
| Android testleri: geçen / hatalı / atlanan | 26 / 0 / 0 |
| Lint: hata / uyarı, önemli bulgular | 0 / 141; ayrıntı aşağıda |
| Gerçek ekran ve medya kanıtı | 28 ekran; 1080×1920 galeri PNG; H.264 MP4 ve dört sürenin Android testi |
| Animasyon açık gözlem | Ayrı kanıt gerekiyor; CI animasyonları kapatır. |
| Fiziksel Samsung/OEM, gerçek paylaşım hedefi | Ayrı cihaz gözlemi gerekiyor. |
| Eski APK üzerine veri koruyan kurulum | Sabit/uyumlu imzayla ayrıca doğrulanmalı. |

Bu alanlar doldurulmadan rapor “tüm testler geçti”, “bütün cihazlar doğrulandı” veya “mağazaya hazır” ifadesine dönüştürülmez.

## 10. Çalıştırma değerlendirmesi

İlk çalışmada 320 dp / 2× yazı ölçeğinde alt menü etiketi taşması yakalandı; kolon ölçümü ve dar ekran boşlukları düzeltildi. Sonraki çalışmada testin yatay kart atası yerine dış dikey alana gerçek kaydırmalar göndermesi sağlandı. Son çalışmada aynı görünürlük, taşma, tıklama ve geri çağrı koşulları korunarak bütün testler geçti.

Lint uyarıları: OldTargetApi: 1, UnusedAttribute: 3, GradleDependency: 13, ModifierParameter: 1, PluralsCandidate: 19, ObsoleteSdkInt: 1, UnusedResources: 103. Kullanılmayan tarihsel kaynaklar, yeni SDK/bağımlılık önerileri ve çoğul metin tavsiyeleri teslimi durduran hata değildir; mağaza hazırlığında ayrıca ele alınmalıdır.

Gerçek görsel gözlem: normal TR/EN ana ekran, plan, izin rehberi, kategori ayrıntısı, Pro demosu, doku seçici, şövalye paylaşımı ve büyük yazı ekranları incelendi. Uzun sayfalardaki devam içeriğine kaydırma ile ulaşılır; geçici bildirim çubuğu kısa süre alt içerik üstünde görünebilir. Ekranlar bütün fiziksel cihazları veya animasyon kalitesini kanıtlamaz.

Kalıcı geliştirme imzası yapılandırılmadığından, önceki test APK’sı ile imza uyuşmazlığı olabilir. Eski test sürümünü kaldırmak verilerini siler. DataStore geçiş testlerinin başarılı olması bu kaldırma işleminin veri koruduğu anlamına gelmez.
