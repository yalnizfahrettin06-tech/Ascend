# Ascend 8.3 — soft minimal tasarım uygulaması

Bu sürümün tasarım dayanağı [21 numaralı UI/UX promptudur](21-ASCEND-SOFT-MINIMAL-UI-UX-YENIDEN-TASARIM-PROMPTU.md). Ekran görüntülerindeki büyük bordo başlık, bordo CTA, dengesiz boşluk, fazla büyük önizleme ve uzun koleksiyon kartları ele alındı.

## Görsel karar

Ana kimlik sıcak kâğıt, mürekkep ve küçük bordo izlerden oluşur. Bordo bir alanın zemini yerine o alanın durumunu anlatır. Başlıklar sistem çubuğuyla aynı açık yüzeyde devam eder. Düğmelerin varsayılan ana rengi mürekkeptir; uzun açıklamalar nötr gri kalır.

| Rol | Açık temadaki değer | Kullanım |
|---|---|---|
| Kâğıt | #FCFAF8 | Ekran ve başlık zemini |
| Hafif yüzey | #F7F4F3 | Arama, önizleme, koleksiyon kartları |
| Yükseltilmiş yüzey | #F0ECE9 | İkincil eylem ayrımı |
| Mürekkep | #242022 | Metin, aktif navigasyon, ana düğme |
| İkincil metin | #6E676A | Açıklama ve kaynak |
| Bordo | #6C2932 | İnce çizgi, seçim sınırı, küçük ikon |
| Seçim tonu | #F5EFF0 | Yalnızca gerçekten seçilmiş seçenek |
| İnce sınır | #E8E1E3 | Sakin bölüm ayrımı |

Koyu ve OLED temaların bağımsız kontrast çiftleri korunur. Mavi, turuncu, sarı veya altın yeni arayüz vurgusu eklenmez. Paylaşım için seçilmiş resimler bu arayüz paletinin dışındadır.

## Promptun ekranlara karşılığı

| Alan | Uygulanan düzen |
|---|---|
| Ortak başlık | Büyük bordo yüzey kaldırıldı. 64 dp asgari açık başlık, büyüyebilen metin, yüzde 3,5 dekor izi. |
| Onboarding iskeleti | Açık başlık, nötr adım sayacı, 2 dp yuvarlatılmış ilerleme izi. Eski göstergenin uç nokta ve kesik çizgi görünümü kaldırıldı. |
| Karşılama | İçerik kümesi kullanılabilir alanın ortasına yerleşir; kısa içerik üstte yığılıp alt yarıyı boş bırakmaz. Uzun yazı kaydırılabilir. Sütun düşük yoğunlukta kalır. |
| Sorular | Tek soru odağı, 30/37 sp başlık ve 14/22 sp açıklama, 60 dp asgari seçim satırı. Seçim yumuşak yüzey, ince bordo kontur ve çizgisel tik ile anlaşılır. |
| Ad/klavye | İsteğe bağlı ad, IME Tamam, geri/atla, taslak ve klavye inset davranışları korunur. |
| Bildirim ritmi | Ortak aileden artı/eksi ve saat ikonları. Daha küçük sayı, daha az kart içi boşluk, gerçek saat önizlemesi. |
| Plan özeti | Dekoratif 01/02 listeleri yerine küçük tikler. Ortak nötr konu etiketleri, daha kısa açıklama, 22/30 sp ilk söz ve erişilebilir kaynak. |
| Erişim | Üç kompakt ikonlu satır; ücretsiz başlangıç, kategori açma demosu ve Pro demosu. Dürüst demo açıklamaları korunur. |
| Bildirim izni | Kompakt, sans yazılı bildirim önizlemesi; izin isteği ve bildirimsiz devam yolu ayrı ve görünür. |
| Bugün | Açık marka satırı, küçük Planım oku, kaynağa daha yakın okuma grubu. Bürstün boyutu daraltıldı ve yüzde 9 opaklık sınırı ile yatay/dikey maske kullanıldı. |
| Bugün eylemleri | Önceki/sonraki gerçek vektör ikonları, gerçek sayaç, 52 dp asgari Kaydet/Paylaş. Paylaş hafif nötr dolgu; üç nokta ortak ikon. Navigasyon öncesi 16 dp boşluk. |
| Keşfet araması | Liste dışına taşındı; kaydırırken sabit ve ulaşılabilir. Odakta 1,5 dp bordo sınır, temizleme ve klavye araması. |
| Keşfet sekmeleri | İçerik genişlikli yatay liste. Aktif yazı mürekkep, gösterge küçük bordo çizgi. |
| Seçim özeti | Büyük pembe blok yerine nötr 48 dp asgari satır, küçük bildirim ikonu, gerçek sayı ve açık yön oku. |
| Koleksiyonlar | Dama renkleri kaldırıldı. Aynı nötr yüzey, 20 dp köşe, ince sınır, 20/26 sp başlık, 8 dp öğe aralığı. Konunun anlamına bağlı küçük ikonlar. |
| Duyarlı kütüphane | 360 dp altında veya yüzde 130 üzerinde yazıda tek sütun. Aynı sıradaki kart yükseklikleri eşleşir; metin kesilmez. |
| Konu ayrıntısı | Satır her zaman ayrıntı açar. Bildirim seçimi ayrı açık kontrolle yapılır; arama, filtre, gerçek sayılar ve geri dönüş konumu korunur. |
| Senin ve Planım | Açık başlık ve ortak ikon sistemi. İç içe başlık kenar boşluğu düzeltildi. Planım ile onboarding aynı konu etiketini kullanır. |
| Ayarlar ve paylaşım | Mürekkep eylemler ve nötr ortak yüzeyler. Artı/eksi, fotoğraf ekleme ve ayarlar yeni ikon ailesine bağlandı. Büyük izin açıklama yüzeyindeki pembe kaldırıldı. |
| Alt navigasyon | 76 dp asgari içerik alanı ve ayrıca sistem inset'i. Üç eşit hedef, 24 dp ikon, 12/16 sp etiket. Aktif ikon mürekkep; yalnız 22 × 2 dp üst işaret bordo. |

### Tipografik ölçülerin yorumu

Prompttaki sp aralıkları başlangıç önerileridir. Mevcut Türkçe metinlerin uzunluğu ve 320–360 dp ekranlardaki alan dikkate alınarak soru başlıklarında 30 sp, koleksiyonlarda 20 sp kullanıldı. Bu seçim, raporun asıl talebi olan daha küçük, sakin ve kesilmeyen hiyerarşiyi izler. Sözler kısa/orta/uzun ve erişilebilir yazı ölçeğine göre kademelenir. Büyük yazıda metin veya kaynak kesilmek yerine yalnız içerik bölgesi kayar; temel eylemler ayrı alanda kalır.

## Yeni uygulama içi ikon sistemi

Tek vektör ailesi: 24 × 24 koordinat alanı, 1,75 dp çizgi, yuvarlatılmış uç ve birleşimler. Font karakterinden üretilmiş ok, üç nokta ve artı/eksi eylemleri ilgili ekranlarda gerçek ikonlara dönüştürüldü.

- Bugün: yükselen çizgi. Keşfet: pusula. Senin: profil.
- Ayarlar: sivri çark yerine yuvarlak hatlı ayar sürgüleri.
- Eylemler: geri/ileri, dışa yön, aşağı açılır, artı/eksi, üç nokta.
- Plan ve ritim: bildirim, saat, tik ve yol.
- Koleksiyonlar: kalp, yükseliş, pusula, kitap, yaprak ve yol; rastgele süs seçimi yerine içerik ailesine bağlı eşleme.
- Kaydet, kopyala, paylaş ve kilit aynı çizgi kalınlığını kullanır.

Dekorasyonlar ekran okuyucuya okunmaz. İkonlu kontrolün açıklaması dokunma hedefinde veya ikonun kendisinde bir kez bulunur. Aktif sekme yalnız renk ile değil üst işaret ve seçili semantiğiyle; seçimler tik ve checkbox/radio semantiğiyle anlatılır. Uygulamanın telefondaki launcher ikonu bu görevde değiştirilmedi.

## Hareket ve erişilebilirlik

Seçim yüzeyi ve sınırı 140 ms; onboarding içeriği 180 ms giriş/100 ms çıkış; ilerleme izi 180 ms; navigasyon rengi 150 ms. Compose animasyonları sistemin animasyon süre ölçeğini kullanır. Sonsuz dekor animasyonu veya parallax yoktur. Kaydet mevcut tek haptik geri bildirimi ve biçim/metin değişimini korur.

Başlık, ana eylem, ikincil yazı, seçili yüzey ve odak rengi kontrastları mevcut otomatik kontrast kontrollerine dahil edildi. Açık başlık üstündeki dekor ile mürekkep düğmenin metin kontrastı da kontrol edilir.

UI denetiminde ana eylemler, kaynakların CTA üzerine kaydırılabilmesi, yüzde 100/130/150/200 yazı ölçeği, 320/360/411 dp örnekleri, koyu tema, arama görünürlüğü, klavye ve durumun yeniden oluşturulması ele alınır. Emülatör kontrolü fiziksel cihazdaki font/sistem farklılıklarını kapsamaz.

## Korunan davranışlar

20 adım ve mevcut sorular, hızlı başlangıç, yerel taslak, kişiselleştirme motoru, 70 konu ve içerik kimlikleri, bildirim planlama, demo erişim kuralları, kaydedilenler ve paylaşım üretimi korunur. İngilizce ana içerik kaynağına veya çeviri eşlemelerine dokunulmadı.

## Teslim denetimi

Kaynak ve görsel katalog kontrolleri yerelde geçti. Android derleme, test ve ekran kanıtlarının nihai sonucu GitHub Actions çalışması ve sürüm notlarında yayımlanacaktır. Kontrol tamamlanmadan bu belge tam cihaz doğrulaması iddiasında bulunmaz.
