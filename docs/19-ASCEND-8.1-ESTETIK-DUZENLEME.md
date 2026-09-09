# Ascend 8.1 — okuma ritmi, keşif ve bordo kimlik

9 Eylül 2026. Bu güncelleme, kullanıcının paylaştığı estetik değerlendirmeyi mevcut 8.0 koduyla karşılaştırır. Hedef daha çok süs eklemek değil; ana ekranda tek bir sözün okunmasını, Keşfet'te konu bulunmasını kolaylaştırmaktır. Metnin sonundaki bordo/koyu kırmızı dokunuş ve yaprak sarılı antik sütun ikonu isteği de uygulama kapsamındadır.

## 1. Eleştirinin tasarım kararına çevrilmesi

| Gözlem | Uygulanan karar | Kullanıcıya etkisi |
|---|---|---|
| Kategori, söz ve kaynak birbirinden kopuyor | Kategori–söz aralığı 12 dp; söz–kaynak 16 dp; okuma grubu kalan alan içinde birlikte merkezlenir | Boşluk tek tek parçaları ayırmak yerine okuma bütününü çevreler |
| Üst kontrol alanı fazla yayılıyor | Marka satırının asgari yüksekliği 60'tan 52 dp'ye indirilir | “Şimdi” ve okuma alanı birbirine yaklaşır |
| Her söz aynı dizgiye zorlanıyor | Metin uzunluğu, dar ekran ve büyütülmüş yazı için birlikte değişen boyut, ölçü ve satır yüksekliği | Büyük serif karakter korunurken uzun satırların ritmi iyileşir |
| Alt bölümde iki slogan ve dekor birikiyor | “Bir söz. Bir adım.” kaldırılır; okların arasında sade söz sayacı; tek kişisel alt ifade korunur | Oklar ile işlemler ortak kontrol alanı olur |
| Yaprak, heykel ve sloganlar rekabet ediyor | Ana ekrandaki sol alt sütun/yaprak kaldırılır; sağdaki soluk büst korunur | Ana ekranda tek dekoratif imza kalır |
| Keşfet bir kapak sayfası gibi başlıyor | Bölüm numarası, büyük tanıtım ve uzun açıklamalar kaldırılır | Başlık, arama, kısa seçim bilgisi ve filtrelerden sonra konulara ulaşılır |
| Erişim ve bildirim seçimi karışıyor | Tüm kategori satırlarında aynı ileri oku; erişim ve bildirim durumu ayrı bilgi | Satıra dokunmanın sonucu öngörülebilir olur |
| “Alanlar” diğer filtrelerle aynı görünüyordu | Tümü / Seçtiklerim / Açık aynı filtre ailesinde; ayrı “Grup:” seçicisi | Liste filtresi ve konu grubu birbirinden ayrılır |
| Büyük adlar ve çok sayıda çerçeve listeyi ağırlaştırıyor | 18 sp serif adlar, 24 sp satır aralığı; 68 dp asgari satır; çerçevesiz sekmeler ve sade arama | Kategori listesi daha kolay taranır |
| Beyaz/siyah görünüm küçük bir renk imzası istiyor | Sınırlı bordo vurgu, çok soluk gül seçim zemini | Ana metin siyah kalırken seçim ve temel işlemler belirginleşir |

Boşlukla gruplanabilen öğelerin her birini kutuya almak görsel karmaşıklığı artırabilir. Bu nedenle ana değişiklik yeni kartlar değil, yakınlık ve hiyerarşidir. [NN/g — Common Region](https://www.nngroup.com/articles/common-region/), [NN/g — Visual Hierarchy](https://www.nngroup.com/articles/visual-hierarchy-ux-definition/)

Bu kararlar tasarım değerlendirmesidir; ölçülmüş dönüşüm oranı veya kullanıcı deneyi sonucu olarak sunulmaz.

## 2. Sözün dizgisi

Kısa sözler 80 karaktere kadar, orta sözler 81–150, uzun sözler 150 üzeri olarak ele alınır. Normal genişlikte temel boyutlar 36 / 33 / 30 sp; dar ekranda 33 / 30 / 28 sp; büyük yazı tercihinde 30 / 28 / 26 sp'dir. Sistem yazı ölçeği uygulanmaya devam eder. Kısa sözde yaklaşık yüzde 94 satır genişliği, diğerlerinde tam genişlik kullanılır; geniş ekranlarda metin ölçüsü 560 dp ile sınırlanır. Satır aralığı boyutla birlikte değişir.

Kısa metinde Heading, diğerlerinde Paragraph satır kırılımı kullanılır. Bunlar elle her cümleyi yeniden yazmaz; dilin ve paragrafın dizgisini platformun satır kırılımı desteğine bırakır. [Android — Paragraph styling](https://developer.android.com/develop/ui/compose/text/style-paragraph)

Kategori, metin ve kaynak aynı dikey okuma grubu içindedir. Temel eylemler sabit kalır; çok uzun söz ve büyük yazı birleştiğinde yalnız söz bölümü kaydırılabilir. Tam cümle saklanmaz veya üç noktayla kesilmez. Hiçbir içerik sırf daha güzel satıra otursun diye yeniden yazılmaz.

Son sürümde düzeltilmiş anlık ihtiyaç seçicisi ve değişen söz listesinin sayfa yönetimi korunur.

## 3. Keşfet ve bildirimler için açık sözleşme

1. Her kategori satırı kategori ayrıntısını açar.
2. Açık kategoride bütün sözler okunabilir; kilitli kategoride iki söz önizlenir.
3. Konuyu okumak, bildirim seçimine müdahale etmez.
4. Açık kategorinin üstündeki “Bildirim konularımda” anahtarı konuyu ekler veya çıkarır.
5. Son seçili konu, başka bir konu seçilmeden kaldırılamaz; bu durum yanında açıklanır.
6. Demo ile bir kategorinin erişimini kazanmak o kategoriyi otomatik olarak bildirimlere eklemez.
7. Pro, erişim kapsamını genişletir; kişinin içerik türü, kaçınma ve bildirim tercihlerini değiştirmez.

Satır altındaki “Açık · Bildirimde” ve “Açık · Bildirimde değil” ifadeleri iki bağımsız durumu birlikte açıklar. Kilitli satırda “Kilitli” görünür; o satır da aynı ileri oku ile önizlemeye gider. Tick, kilit ve ok artık tek bir işlem alanında dönüşümlü kullanılmaz.

Konu ayrıntısı; açık/kilitli açıklaması, bildirim anahtarı veya erişim eylemi ve ardından sözler şeklinde okunur. Kapatma düğmesi başlıktadır. Filtreler, arama temizleme ve grup seçimi ayrı ve isimlendirilmiş kontrollerdir.

## 4. Bordo dokunuşlar

| Rol | Aydınlık Mermer | Gece Mermer |
|---|---|---|
| Vurgu | #713C49 | #DBB6C0 |
| Seçim zemini | #F7EEF0 | #302529 |
| Ana yüzey / ana metin | Önceki beyaz / koyu mürekkep | Önceki nötr kömür / açık mürekkep |

Bordo, temel düğme, küçük kategori çizgisi, filtre işareti ve seçili kontrollerde kullanılır. Gövde yazıları ve tüm büyük yüzeyler kırmızıya çevrilmez. Mürekkep seçeneği nötr kalır; mevcut Bordo varyantı korunur.

18 palet/tema birleşiminin bağımsız renk hesabında mevcut kontrast eşikleri korunmuştur: en düşük temel düğme kontrastı 8,59:1; işlevli kontrol çizgisi 3,60:1. Bu hesap, ekranın tamamına ilişkin cihaz veya erişilebilirlik sertifikası değildir.

## 5. Yeni uygulama ikonu

Bordo, çok hafif dokulu zemin üzerinde beyaz antik Roma sütunu; gövdeye sarılan ince dal ve geri planda kalan küçük yeşil yapraklar. Yapraklar sütunun yerini alan bir çelenk değildir. Uygulama içindeki yükseliş işareti ve ana ekran büstü korunur.

Renkli ikon, yerleşik image_gen aracıyla bu güncelleme için üretildi. Yalnız dağıtım boyutuna küçültme ve WebP kodlama yapıldı; resim üzerinde ek boyama veya birleştirme yapılmadı. Android adaptif ikonunda 6 dp iç boşluk, sütunun maskenin içinde kalmasına yardım eder. Android'in tek renkli ikon seçeneği için sütun silueti ayrıca vektör olarak güncellendi. Açılış ekranı aynı launcher ikonunu kullanır. [Android — Adaptive icons](https://developer.android.com/develop/ui/compose/system/icon_design_adaptive)

[İkon dosyası](../app/src/main/res/drawable-nodpi/art_launcher_column.webp) · [Kullanılan tam üretim promptu ve işlem kaydı](../content/launcher-v81-provenance.json)

## 6. Sınırları belli teslim

Sürüm 8.1.0, kod 18; paket ve veri kimlikleri korunur. İngilizce ana kaynak, Türkçe çeviriler, 700 metin, 70 kategori, kaydedilenler, 20 ekranlık kişisel başlangıç ve paylaşım eserleri bu turda değiştirilmez. Pro ve reklam davranışı açıkça belirtilen demo olarak kalır.

Derlemeden önce katalog/görsel kontrolleri, mevcut birim testleri, lint ve Android test kodunun derlenmesi GitHub üzerinde çalışır. Kategori erişimi ve bildirim seçimi ayrımının mevcut Android kontrolü güncellendi; kategoriye göz atmanın seçimi değiştirmemesi için bir davranış kontrolü eklendi. Ayrı emülatör işi mevcut iş akışında devam eder.

Kullanıcının isteği doğrultusunda APK hazır olduğunda teslim edilir; APK sonrasında ek görsel inceleme turu veya uzun emülatör bekleme yapılmaz. 8.0 testlerinin sonuçları 8.1 için otomatik olarak geçerli sayılmaz. Bu belge, derleme sonucu yerine tasarım kararlarını kaydeder; güncel sonuç [GitHub Actions](https://github.com/yalnizfahrettin06-tech/Ascend/actions/workflows/android.yml) üzerinde görülebilir.

