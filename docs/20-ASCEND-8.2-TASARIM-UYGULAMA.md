# Ascend 8.2 — Tasarım raporunun uygulama karşılığı

Kaynak: kullanıcının **Ascend_UI_UX_Yeniden_Tasarim_AI_Promptu.md** raporu. Bu belge tasarım kararlarını, kapsamı ve doğrulama sınırlarını kaydeder. Referans sürüm: 8.1.0, kaynak 3fda8334132b834259ebaddb85274d525f020aa5.

## Tasarım kararı

Beyaz okuma yüzeyi, koyu mürekkep ve kompakt bordo marka yüzeyleri. Sütun, Keşfet başlığında ve karşılama kompozisyonunda mimari iz olarak kullanılır. Ana ekranda yalnızca büst kalır. Görsel galeri paylaşım araçlarında korunur; konu seçimi fotoğraf galerisine dönüşmez.

İkon yeniden çizilmedi. Mevcut art_launcher_column.webp dosyasının 75 piksellik kenar bölgesinde, 4 piksel aralıkla örneklenen RGB kanallarının medyanı **108, 41, 50 — #6C2932**. Ana marka yüzeyi bu renktir. Diğer tokenlar: beyaz #FFFFFF; mürekkep #211D1F; ikincil metin #655D61; açık bordo #F4EAED; ayırıcı #E6DDE0; marka üzeri #FFF9F5. Koyu/OLED seçenekleri aynı semantik rolleri korur. Eski kayıtlı palet adları okunmaya devam eder. MONO seçeneği nötr araç vurgularını korurken marka başlıkları bordo kalır.

Altın, sarı, mavi, turuncu ve yeşilimsi arayüz vurguları eklenmedi. Paylaşım görsellerinin kendi renkleri bu arayüz kuralından ayrı kalır.

## Rapor maddeleri ve uygulama

| Rapor alanı | Uygulanan karşılık | Dosya / kontrol |
|---|---|---|
| Kapsam, mevcut kazanımlar | 3 sekme, söz içeriği, kayıtlar, bildirim planı, erişim sınırları ve ikon korunur | Uygulama, veri göçü ve erişim regresyon testleri |
| Marka mimarisi | Ortak MarkaBasligi; opak bordo yüzey ve açık başlık, ölçümü büyütmeyen dekor katmanı | KlasikDil, Tasarim, Paletler |
| Renk ve kontrast | Yeşilimsi nötrler kaldırıldı; 18 palet/mod kombinasyonunda metin, kontrol sınırı, seçili yüzey, dekor altı ve marka kontrastı ölçülür | KontrastTest |
| Tipografi | Mevcut Lora + sistem sans korunur; Türkçe karakter desteği mevcut. Newsreader + Inter alternatifleri yeni bir indirme/bağımlılık eklemek için yeterli üstünlük sunmuyor | Tasarim; gerçek TextLayoutResult testleri |
| Keşfet üst alanı | Bordo başlık, genel arama, Koleksiyonlar / Tüm konular / Seçtiklerim, dokunulabilir bildirim özeti | KategorilerEkrani |
| Keşfet satış ağırlığı | PRO DEMO başlık rozeti kaldırıldı. Kilitli konu içindeki mevcut demo açıklaması korunur | KategorilerEkrani; kilit diyaloğu |
| Koleksiyonlar | Mevcut 12 gerçek grup; kısa açıklama, gerçek konu sayısı, hafif değişen açık yüzeyler, 2 sütun / dar veya büyük yazıda 1 sütun | KoleksiyonKarti |
| Liste ve detay | Anlamsız sıra numaraları yok; her satır tutarlı detay oku. Seçili konu işaretli; diğer açık konularda söz sayısı, kilitli konularda önizleme etiketi | KategorilerEkrani |
| Erişim ve seçim ayrımı | Açık konunun tüm sözleri, kilitlinin 2 sözlük önizlemesi. Yalnız açıkça kullanılan bildirim anahtarı seçim değiştirir | UygulamaTest |
| Arama ve filtreler | Türkçe yerel büyük/küçük harf; arama tüm konulara uygulanır; grup/sekme aramayı sınırlamaz. Erişim filtresi görünür ve ayrı temizlenebilir | LibraryQuery, LibraryQueryTest |
| Geri dönüş | Arama, koleksiyon, tüm liste ve seçili liste için ayrı kaydırma durumu; detay kapanışı ve yeniden oluşturma mevcut bağlamı korur. Sekme durumları SaveableStateHolder içinde | DesignV82Test, Uygulama |
| Teslimat gerçeği | Konu seçili olması izin açık demek değildir. Seçtiklerim, teslimat kapalıysa bunu ayrıca açıklar | category-delivery-off |
| Ana ekran | Kompakt marka başlığı; doğru anlamlı “Sana göre”; tek okuma grubu biraz yukarı dengelenir. Sayfa sayacı gerçek sıradır. Kaydet / Paylaş eş ağırlıklı kapsüller; gereksiz alt slogan kaldırılır | AnaEkran |
| Onboarding karşılama | Başlığı aşağı iten büyük ikon + küçük büst düzeni yerine tek sütun kompozisyonu. 20 adım bilgisi ikincil, hızlı başlama açık | Onboarding |
| Onboarding 20 adım | Ortak bordo başlık, seçili kontur ve işaret, açık okuma yüzeyi, sabit erişilebilir ilerleme düğmesi. Soru, geri, atla, kaydetme ve izin davranışı değişmez | OnboardingTest |
| Plan önizleme | Gerçek başlangıç konuları ayrı satıra akabilen etiketler; eksiksiz söz ve kaynak. Sonraki erişim ve izin adımlarının kaldığı açık | DesignV82Test |
| Plan açıklaması | Belirsiz “arada yeni bir bakış” yerine seçili konuların önceliği ve diğer açık konuların gelebileceği belirtilir | PersonalPlan.summary; ağırlık algoritması değişmez |
| Ortak ekranlar | Senin’de kompakt marka başlığı; Planım terminolojisi; gereksiz ikincil dekor kaldırılır. Alt navigasyonda bordo aktif işaret ve nötr pasif ikon | IstatistikEkrani, KisiselPlanPaneli, Iskelet |
| Erişilebilirlik | En az 48 dp eylemler; seçili/anahtar rolleri; dekorların erişilebilirlik ağacından çıkarılması; tüm metinlerde sarmalama; normal, %130, %150, %200 matris | DesignV82Test, ResponsiveV6Test |
| Koruma | 700 İngilizce özgün kaynak / 700 bağlı Türkçe çeviri / 70 sabit konu kimliği. 107 paylaşım arka planı, mevcut Pro demosu ve 3 ücretsiz arka plan değişmez | İçerik ve görsel araç kontrolleri; medya/erişim testleri |

Kilitli konuya doğrudan “Pro” etiketi konmadı: mevcut erişim modeli o konuyu tek tek demo yoluyla da açtırıyor. “Kilitli · Önizleme” mevcut gerçeği anlatıyor; abonelik zorunluluğu izlenimi yaratmıyor. Bu, raporun erişim gerçeğini koruma şartının uygulamasıdır.

## Ölçüm ve test planı

- Önceki sürümün GitHub UI artefaktı indirildi. Ana ekran karşılaştırmasında aynı “Çabanın karşılığını…” sözü, aynı Pixel 2 viewport’u kullanılır. Önceki gerçek akış 70 söz; karşılaştırma testinin tek sözlük sabit verisi 1/1 gösterir — sayaç farkı tasarım farkı değildir.
- Yeni Android testi %100 ve %130’da 411 dp, %150 ve %200’de 320 dp kullanır. Tam Türkçe sözün TextLayoutResult taşması ve plan kaynağının alt düğmenin üzerinde kalması denetlenir; ekran görüntüleri üretilir.
- Koleksiyon → arama → detay → yeniden oluşturma → önceki koleksiyon → Seçtiklerim akışı; aramanın koleksiyon dışındaki Marcus konusunu bulması ve seçim değiştirmemesi denetlenir.
- Mevcut izin, bildirim, medya PNG/MP4, taslak, büyük yazı, karanlık mod, erişim ve uygulama içi gezinme regresyonları devam eder.
- GitHub derlemesi tamamlanmadan “doğrulandı” kabul edilmez. Fiziksel cihaz/TalkBack ile elle kullanım veya tüm üretici klavyeleri denenmiş sayılmaz.
- Üç düğmeli sistem navigasyonu ve gerçek cihaz karşılaştırması yapılmadıysa teslimatta ayrı belirtilir. Otomatik semantik kontrol gerçek TalkBack kullanımının yerine geçmez.

## Araştırma dayanakları

- [NN/g — Progressive Disclosure](https://www.nngroup.com/articles/progressive-disclosure/): İkincil erişim ve koleksiyon filtrelerini tek filtre paneline taşıma; başlangıçta temel görünümleri sunma kararı.
- [Android — Window Insets](https://developer.android.com/develop/ui/compose/system/insets): Sistem çubukları, klavye ve alt navigasyonun kapladığı alanın okunabilir bölgeden ayrılması.
- [Android — Semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics): Salt çizilen işaret yerine gerçek seçili/anahtar durumlarının sunulması.
- Kullanıcının sunduğu rapordaki Newsreader/Inter önerisi değerlendirildi. Mevcut Lora ailesi değiştirilmeden, UI sans metniyle rol ayrımı sürdürülür. Bir font ailesi değişikliğinin kendiliğinden okunabilirlik kanıtı olmadığı kabul edilir.

## Derleme sonrası kanıt

Bu bölüm son GitHub çalışmasının sonucu ve incelenen ekranlar ile tamamlanacaktır.

