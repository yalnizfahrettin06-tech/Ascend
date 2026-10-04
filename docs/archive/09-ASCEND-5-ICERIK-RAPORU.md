# Ascend 5 — İçerik yenileme, araştırma ve entegrasyon raporu

**Rapor tarihi:** 8 Eylül 2026

**Katalog sürümü:** 5.0.0

**Kapsam:** 12 koleksiyon, 70 kategori, 700 özgün İngilizce söz ve kimlikle bağlı 700 Türkçe çeviri.

**Doğrulama durumu:** Aşağıdaki dosya ölçümleri güncel kaynaklardan alındı. Teslim edilen APK'nın test edilen commit'i, derleme sonucu ve cihaz kontrolleri [doğrulama raporunda](10-ASCEND-5-DOGRULAMA.md) kayıtlıdır.

## 1. Kullanıcıya sunulan değişiklik

Ascend'in temel içeriği baştan yazıldı. Günlük olumlamalar, motivasyon, azim, disiplin, düşünürlerden ilham alan düşünceler, inanç, ilişkiler, beden, iş ve öğrenme tek bir genel motivasyon dili altında eritilmedi. Her kategori, kullanıcıya farklı bir durumda eşlik edecek 10 ayrı metinle dolduruldu.

Yeni aktif katalog **700 ayrı düşünceden** oluşur. İngilizce ve Türkçe metinleri toplamak, birbirinden bağımsız 1.400 söz bulunduğu anlamına gelmez: her düşüncenin tek sabit kimliği, bir kanonik İngilizce metni ve bu metne bağlı Türkçe çevirisi vardır.

Önceki 82 kayıt aktif içerikten çıkarıldı. Kaydedilmiş eski sözlerin kaybolmaması için yalnız kimlikle çözümleme yapan bir arşiv korundu. Yeni akış, kategori listeleri ve bildirim havuzu 700 yeni kayıt üzerinden çalışır.

## 2. Başlangıç durumunun gerçek kapsamı

Karşılaştırma, değişiklik öncesinde çalışma alanına alınan `../content5-before/legacy.json` içindeki 82 kayıt sayılarak yapıldı. Bu dosya uygulamanın veya yeni içerik kaynağının parçası değildir.

Önceki sürümün denetlenebilir kaynakları: [Sozler.kt](https://github.com/yalnizfahrettin06-tech/Ascend/blob/fa37847aa417b5038cc4baed5479cf1d7eef8db8/app/src/main/java/com/yalnizfahrettin/azim/data/Sozler.kt) ve [Olumlamalar.kt](https://github.com/yalnizfahrettin06-tech/Ascend/blob/fa37847aa417b5038cc4baed5479cf1d7eef8db8/app/src/main/java/com/yalnizfahrettin/azim/data/Olumlamalar.kt).

Eski katalogda 70 kategorinin **25'inde içerik vardı; 45 kategori boştu**. Dolu kategorilerde 1–6 kayıt bulunuyordu. Dolayısıyla önceki ekranda bir kategori adının görünmesi, o kategoriye ait zengin bir içerik havuzu bulunduğunu göstermiyordu.

Yeni katalogda boş kategori kalmadı: 70 kategorinin her birinde tam 10 kayıt var. Aktif içerik sayısı 82'den 700'e çıktı; bu sayısal fark 618 kayıttır, ancak uygulanan değişiklik eski metinlerin üstüne ekleme değil, aktif kataloğun tamamen yenilenmesidir.

| Koleksiyon | Kategori | Önceki söz | Yeni söz | Varsayılan erişim |
|---|---:|---:|---:|---|
| Günlük olumlamalar | 3 | 18 | 30 | Ücretsiz |
| Azim & Dayanıklılık | 9 | 29 | 90 | Ücretsiz |
| Disiplin & Odak | 5 | 20 | 50 | Ücretsiz |
| Özgüven & Cesaret | 5 | 0 | 50 | Kilitli; demo ile açılır |
| Filozoflar | 8 | 10 | 80 | Ücretsiz |
| Tasavvuf & Doğu | 5 | 1 | 50 | Kilitli; demo ile açılır |
| İnanç | 5 | 0 | 50 | Kilitli; demo ile açılır |
| Spor & Beden | 5 | 0 | 50 | Kilitli; demo ile açılır |
| İş & Başarı | 7 | 3 | 70 | Kilitli; demo ile açılır |
| İlişkiler | 6 | 0 | 60 | Kilitli; demo ile açılır |
| Zihin & Huzur | 7 | 1 | 70 | Kilitli; demo ile açılır |
| Öğrenme & Gelişim | 5 | 0 | 50 | Kilitli; demo ile açılır |
| **Toplam** | **70** | **82** | **700** | **25 ücretsiz / 45 kilitli kategori** |

Buradaki eski içerikli 25 kategori ile yeni ücretsiz 25 kategori aynı kümeyi ifade etmez. İlki dosyada içerik bulunmasını, ikincisi uygulamanın erişim düzenini ölçer.

## 3. Editoryal yaklaşım

Metinlerin her biri, kategori adı görünmese de kısa bir bildirim içinde anlaşılabilecek tek bir düşünce taşır. Kullanıcının gerçek gündelik koşulları seçildi: boş sayfa, toplantıda söz alma, karşılıksız mesaj, sofrada dinleme, değişen çalışma saatleri, alışkanlığı hatırlatan bir eşya veya yarım kalan işi tanımlama.

Üç günlük olumlama kategorisi birinci tekil anlatımla kişinin kendi sesine yakın yazıldı. Diğer kategorilerde kısa gözlemler, düşünmeye çağıran sorular, somut seçenekler ve yer yer kişisel niyetler birlikte kullanıldı. Bütün kategoriler aynı cümle iskeletinin kelime değiştirerek çoğaltılmasıyla doldurulmadı.

Özellikle yakın başlıkların görevleri ayrıldı:

- **Kendime güvenmek** kişinin kendi değerlendirmesi ve varlığıyla ilişkisini; **Kendine Güven** sosyal ortamda becerisini ifade etmesini işler.
- **Biraz yavaşlamak** iç deneyime yönelik birinci tekil olumlamaları; **Huzur** günlük ortam ve programdaki sakinlik alanlarını kapsar.
- **Azim / uzun soluk** emek ve hedeflerin zamana yayılmasını; **Dayanıklılık** fiziksel çaba, tempo ve koşulları ele alır.
- **Rutin kurmak** birden fazla işin günlük düzenini; **Alışkanlık** tek bir davranışın bağlamını; **Sabah rutini** güne başlama koşullarını işler.
- **Minnettarlık** somut emek ve iyiliği fark etmeye; **Şükür** inanç bağlamındaki kişisel tefekküre ayrılır.
- **Merak** bir soru açmaya; **Okumak** metinle ilişkiye; **Sınav & Öğrencilik** öğrenileni ve bilinmeyeni görmeye odaklanır.

Duyguları küçümseyen kesinlik, herkese uygun tek yaşam düzeni, başarı veya şifa garantisi, yorgunluğu ahlaki kusur sayma ve ilişkide sınırsız fedakârlığı övme dışlandı. Bir sözün iyi yazılması, kullanıcının hayatındaki sonucu garanti etmez.

## 4. Araştırma, atıf ve özgünlük sınırları

Araştırma, kategori sınırlarını ve yazım kararlarını belirledi. Uygulama metinleri araştırma sonuçlarının alıntısı, düşünürlerin yeni bulunmuş sözleri veya kutsal metinlerin çevirisi olarak sunulmaz. Bütün yeni metinlerin yazarı **Ascend**'dir.

Düşünür kategorilerinde ilgili eser veya akademik incelemenin farklı yönleri gözetildi. Stoacıların gündelik yargı ve sorumluluk vurgusu, Aristoteles'in bağlama uygun muhakemesi, Nietzsche'nin değerleri sorgulaması ve Machiavelli'nin kararların gerçek etkilerine dikkati birbirine karıştırılmadı. Şems, Yunus ve Hafız için doğrulanmamış sosyal medya sözleri kaynak yapılmadı.

İnanç başlıkları **Kur'an üzerine düşünceler**, **İncil üzerine düşünceler** ve **Tevrat üzerine düşünceler** olarak açıkça ayrıldı. Bunlar ayet, meal, hadis veya dinsel hüküm değildir. Dua alanı Ascend için yazılan kişisel yakarışları, Şükür alanı inançla ilişkilendirilen gündelik düşünceleri içerir. Tanrı'nın ağzından bildirim yazılmaz; başka inançlara karşı üstünlük veya ödül garantisi kurulmaz.

Kaynak araştırması dört ayrıntılı notta belgelenmiştir:

| Araştırma notu | Kapsam | Başlıca araştırma yönü |
|---|---|---|
| [Yaşam, ilişkiler ve iç denge](icerik-arastirma/yasam.md) | 23 kategori / 230 düşünce | Özşefkat; sosyal bağ ve sınırlar; kaygı, uyku ve merak. NIMH, NIH, NHS, Neff ve özgün merak araştırması. |
| [Eylem, emek ve çalışma hayatı](icerik-arastirma/eylem.md) | 21 kategori / 210 düşünce | Başlama ve planlama; alışkanlık; yorgunluk ve iş yükü; ekip içinde öğrenme; finansal koşullar. Özgün araştırmalar ve resmî kurumlar. |
| [Düşünce ve inanç](icerik-arastirma/dusunce.md) | 18 kategori / 180 düşünce | Her düşünür veya gelenek için özgül tema, eser/bölüm bağlantısı, akademik veya birincil kaynak ve atıf sınırı. |
| [Beden, alışkanlık ve öğrenme](icerik-arastirma/beden-ogrenme.md) | 8 kategori / 80 düşünce | Hareket, beslenme, sakatlıktan dönüş, hatırlama ve alışkanlık. WHO, özgün çalışmalar ve uzman uzlaşısı. |

Notlar araştırmanın ayrı iş bölümleridir; dört ayrı içerik kaynağı değildir. Güncel içerik, tek İngilizce dosya ve kimlikle bağlı Türkçe çeviriden okunur.

Bu araştırma **Ascend bildirimlerinin sağlık, öğrenme veya başarı üzerinde kanıtlanmış etkisi olduğunu göstermez**. Klinik rehber, deney, gözlemsel çalışma, tarihsel eser ve uzman uzlaşısının kanıt türleri eşitlenmedi. Kaynakların tarihsel görüşleri bağlamdan çıkarılıp güncel tıbbi veya ahlaki talimata dönüştürülmedi.

Katalog içindeki tam tekrarlar denetlendi ve yazım editoryal olarak gözden geçirildi. İnternette bugüne kadar yazılmış bütün kısa metinlerle evrensel benzerlik taraması yapıldığı ya da mutlak benzersizliğin matematiksel olarak kanıtlandığı iddia edilmez.

## 5. Tek İngilizce kaynak ve çeviri düzeni

### Dosyaların sorumluluğu

- [content/source.en.json](../content/source.en.json): 12 grup, 70 kategori, 700 İngilizce kayıt, yazar ve katalog sürümünü içeren **tek kanonik içerik kaynağı**.
- [content/translations/tr.json](../content/translations/tr.json): kaynağın sabit kimliklerine bağlı Türkçe metinler ve kaynak özetleri.
- [tools/icerik_derle.py](../tools/icerik_derle.py): içerik doğrulaması ve Android veri dosyasının üretimi.
- [IcerikVerisi.kt](../app/src/main/java/com/yalnizfahrettin/azim/data/IcerikVerisi.kt): kaynaktan üretilen uygulama verisi; içerik düzeltmelerinin başlangıç noktası değildir.
- [content/README.md](../content/README.md): geliştirici ve çevirmen için ayrıntılı İngilizce rehber.

Araştırma sırasında oluşturulan iki dilli paket taslakları depo dışındaki çalışma arşivine taşındı. Uygulamanın derlemesi onlara bağlı değildir ve yeni çeviriler onlardan başlatılmaz.

### Sabit kimlik ve sourceHash

Bir sözün kimliği, örneğin `v5_ozsefkat_01`, metnin yazılışından bağımsızdır. Yazım veya çeviri iyileştirmesi, kaydedilmiş sözün yeni bir kimlik kazanmasına neden olmaz. Aynı kimlik Türkçe, İngilizce, favoriler ve bildirim geçmişinde aynı düşünceyi temsil eder. Kimlik ilgisiz yeni bir düşünce için yeniden kullanılmamalıdır.

İngilizce kayıtta `id`, `category` ve `text` bulunur. Türkçe dosyası aynı kimliğin altında `text` ve `sourceHash` taşır. `sourceHash`, İngilizce `text` değerinin tam UTF-8 içeriğinin SHA-256 özetidir. Noktalama veya boşluk dahil kaynak metin değişince özet de değişir.

Güncelleme sırası şöyledir:

1. İngilizce ana metni düzenle; mevcut düşünce için kimliği koru.
2. Bağlı Türkçe çeviriyi anlam, ton ve uzunluk bakımından yeniden incele.
3. İncelemenin ardından çevirinin `sourceHash` değerini güncel kaynakla eşleştir. Yalnızca kontrolü susturmak için özet değiştirme.
4. İçerik derleyicisini çalıştırarak Kotlin verisini yeniden üret.
5. `--check` ile kaynağın ve üretilen dosyanın eşleştiğini doğrula; ardından değişikliğe uygun Android kontrollerini tamamla.

Depo kökünde kullanılan komutlar:

```text
python tools/icerik_derle.py
python tools/icerik_derle.py --check
```

`--check` dosyayı yeniden yazmaz. Kimlik ve kategori kapsamı, her kategoride tam 10 kayıt, iki dilin uzunluğu, biçimi, tekrarlar, Türkçe kaynak özetleri ve üretilen verinin güncelliği kontrol edilir. Bir özet eşleşmesi yalnız kaynak sürümünü izler; çevirinin dilsel doğruluğunu kendi başına kanıtlamaz.

### Gelecekteki diller

Şu anda uygulamada kullanılan içerik dilleri **yalnızca İngilizce ve Türkçe**dir. Yedi veya sekiz dil hedefi için dosya düzeni hazır bir başlangıç sağlar; yeni bir çeviri dosyası oluşturmak o dili uygulamada açmaz.

Örneğin henüz var olmayan Almanca dosyası için:

```text
python tools/icerik_derle.py --template de --output content/translations/de.json
```

Bu komut aynı kimlikler ve güncel kaynak özetleriyle boş çeviri alanları oluşturur; çeviri yapmaz ve mevcut dosyanın üzerine yazmaz. Yeni dilin çeviri incelemesine ek olarak veri derleyicisine, uygulamanın dil seçimine, arayüz etiketlerine, sesli okumaya, bildirim ve paylaşım akışlarına bağlanması gerekir. Gerekiyorsa sağdan sola yerleşim ve o dile ait cihaz kontrolleri de eklenmelidir.

## 6. Eski favoriler ve yeni içerik birbirinden nasıl ayrılıyor?

Önceki katalogdaki 82 kayıt [EskiSozler.kt](../app/src/main/java/com/yalnizfahrettin/azim/data/EskiSozler.kt) içinde sabitlenmiş eski kimlikleriyle tutulur. Bu arşiv, mevcut favori veya eski bir içerik bağlantısının çözümlenmesi içindir.

[Sozler.kt](../app/src/main/java/com/yalnizfahrettin/azim/data/Sozler.kt) yeni katalog aramasını önce yapar; yalnız kimlikten çözümleme gerektiğinde eski arşive bakar. Tüm aktif sözler, kategori seçimi, ana akış ve bildirim havuzu arşive eklenmez. Böylece eski bir favoriyi korumak, eski metni yeniden günlük bildirimlere dağıtmak anlamına gelmez.

Eski kayıtlardaki doğrulanmamış kişi isimleri yeni bir atıf olarak sürdürülmez. Arşiv içerikleri **Arşiv · Önceki sürüm** şeklinde işaretlenir. Yeni özgün düşüncelerin yazarı Ascend'dir; günlük olumlamalar türüne göre olumlama etiketi alır. Düşünür veya gelenek adı kategori bağlamıdır, yeni metnin imzası değildir.

**Kurulum sınırı:** Bu koruma, mevcut uygulama verisinin korunduğu, uyumlu imzayla yerinde güncelleme için geçerlidir. Actions'ta üretilen debug APK'ların imzası sabit bir anahtarla korunmadığında önceki kurulumun üzerine yükleme kabul edilmeyebilir. Uygulamayı kaldırıp yeniden kurmak yerel favori ve tercihleri silebilir; arşiv kodu silinmiş uygulama verisini geri getirmez. Kesintisiz güncelleme veya tüm APK'lar arasında veri koruma sözü verilmez. Güncel APK ve imzalama koşulları [proje README'sinde](../README.md) izlenmelidir.

## 7. Bildirim çeşitliliği ve geçmiş düzeltmeleri

700 kaydın bulunması tek başına çeşitli bildirimler sağlamaz. Bildirim seçimi, kullanıcının seçtiği kategorilerdeki aktif içerik ve o turun geçmişi üzerinden yapılır.

| Durum | Uygulanan davranış |
|---|---|
| Seçili kategorilerde görülmemiş içerik var | Önce bu içerikler arasından seçim yapılır. |
| Seçili havuzun tamamı görülmüş | Yeni tur başlatılır; yalnız seçili kategorilerin tur geçmişi temizlenir. |
| Tur değişiminde son bildirim yeniden aday olur | Başka aday varsa hemen önceki bildirimin kimliği dışlanır. |
| Diğer kategorilerin geçmişi mevcut | Seçili havuz yenilenirken bu geçmiş topluca silinmez. |
| Kullanıcı uygulamada bir söz okur | Aktif kimlik aynı çeşitlilik geçmişine de işlenir. |
| Eski arşiv veya artık aktif olmayan kimlik geçmiştedir | Aktif içerik kontrolüyle tur hesabından çıkarılır. |
| İzin veya hatırlatıcı kapalıdır; saat uygun değildir | Bildirim gönderimi yapılmaz. |

Önceki sabit büyüklükte geçmiş sınırı kaldırıldı. 350 kayıtla sınırlandırılan bir geçmiş, 700 kayıtlık seçili havuzun tamamını hatırlayamaz; bu nedenle daha önce gönderilmiş sözler havuz bitmeden tekrar aday olabilirdi. Güncel geçmiş, sayıya göre kırpılmak yerine aktif kimliklerle sınırlandırılır.

Günlük okuma sayacı ile bildirim turu farklı ömürlere sahiptir. Aynı sözün aynı gün ikinci kez okunması günlük sayacı yeniden artırmaz; ancak tur sıfırlandıktan sonra aynı gün tekrar okunan söz, çeşitlilik geçmişine yine yazılır. Günlük sayaç kontrolünün bu yazmayı atlaması düzeltildi.

Aynı süreçte çakışan bildirim çalışanlarının aynı son adayı seçmesini önlemek için seçim, gönderim ve kayıt bölümü birlikte sıralanır. Android bildirimi kabul ettikten sonra yeniden planlama çalışanı iptal ederse, geçmişin ve bugünkü gönderim kaydının yarım kalmaması için bu kısa yazma bölümü iptalden korunur.

Bu düzen, her yeni turda aynı cümlenin hemen tekrar gelmesini önler; bütün tur boyunca önceden belirlenmiş sabit bir sıra vaat etmez. Ayrıca süreç kilidi ve iptal koruması, uygulama sürecinin zorla kapanmasına veya cihazın aniden kapanmasına karşı kalıcı bir işlem günlüğü değildir. Android'in arka plan zamanlaması da bildirimlerin tam saniyesini garanti etmez.

## 8. Ücretsiz içerik ve mevcut demo kilidi

Ücretsiz dört koleksiyon **Günlük olumlamalar**, **Azim & Dayanıklılık**, **Disiplin & Odak** ve **Filozoflar**dır. Toplam 25 kategori ve 250 söz içerirler. Diğer sekiz koleksiyon 45 kategori ve 450 söz içerir; başlangıçta kilitlidir.

Kilit kategori başına değil, koleksiyon düzeyindedir. Mevcut önizleme akışı açıkça demo olarak anlatılır: **Google.com tarayıcıda açılır; kullanıcı uygulamaya döndüğünde yalnız seçilen koleksiyon açılır**. Tarayıcı başlatılamazsa kilit açılmaz.

Bu sürümde bu akış gerçek ödüllü reklam, reklam geliri, AdMob entegrasyonu, ücretli abonelik veya ödeme olarak sunulmaz. İçerik sayıları bu geçici erişim modelinden bağımsızdır; 700 kaydın tamamı katalogda bulunur.

## 9. Yetmiş kategorinin eksiksiz dökümü

“Önceki” sütunu değişiklik öncesi 82 kayıtlık dosyadan sayılmıştır. “Yeni” sütunu aktif 5.0.0 kataloğuna aittir. Kategori anahtarları dil bağımsız teknik kimliklerdir; ekranda gösterilen adın çevirisiyle karıştırılmaz.

| # | Koleksiyon | Türkçe kategori | Anahtar | Önceki | Yeni |
|---|---|---|---|---:|---:|
| 1 | Günlük olumlamalar | Kendime nazik davranmak | `ozsefkat` | 6 | 10 |
| 2 | Günlük olumlamalar | Biraz yavaşlamak | `ic_huzur` | 6 | 10 |
| 3 | Günlük olumlamalar | Kendime güvenmek | `kendine_guven` | 6 | 10 |
| 4 | Azim & Dayanıklılık | Motivasyon | `motivasyon` | 4 | 10 |
| 5 | Azim & Dayanıklılık | Azim | `azim` | 3 | 10 |
| 6 | Azim & Dayanıklılık | Pes Etmemek | `pes` | 3 | 10 |
| 7 | Azim & Dayanıklılık | Zorluğa Karşı Sabır | `zorluk_sabir` | 3 | 10 |
| 8 | Azim & Dayanıklılık | Yorulmaya Karşı Sabır | `yorgunluk_sabir` | 3 | 10 |
| 9 | Azim & Dayanıklılık | Tükenmişlik | `tukenmislik` | 2 | 10 |
| 10 | Azim & Dayanıklılık | Yeniden Başlamak | `yeniden` | 5 | 10 |
| 11 | Azim & Dayanıklılık | Uzun Soluklu Hedefler | `uzun_soluk` | 2 | 10 |
| 12 | Azim & Dayanıklılık | Umut | `umut` | 4 | 10 |
| 13 | Disiplin & Odak | Erteleme | `erteleme` | 4 | 10 |
| 14 | Disiplin & Odak | Derin Odak | `derin_odak` | 6 | 10 |
| 15 | Disiplin & Odak | Dürtü Kontrolü | `durtu` | 4 | 10 |
| 16 | Disiplin & Odak | Rutin Kurmak | `rutin` | 3 | 10 |
| 17 | Disiplin & Odak | Dikkat Dağınıklığı | `dagilma` | 3 | 10 |
| 18 | Özgüven & Cesaret | Kendine Güven | `ozguven` | 0 | 10 |
| 19 | Özgüven & Cesaret | Korkuyla Yüzleşme | `korku` | 0 | 10 |
| 20 | Özgüven & Cesaret | Reddedilme | `reddedilme` | 0 | 10 |
| 21 | Özgüven & Cesaret | Risk Almak | `risk` | 0 | 10 |
| 22 | Özgüven & Cesaret | Utangaçlık | `utangaclik` | 0 | 10 |
| 23 | Filozoflar | Marcus Aurelius | `marcus` | 6 | 10 |
| 24 | Filozoflar | Seneca | `seneca` | 1 | 10 |
| 25 | Filozoflar | Epiktetos | `epiktetos` | 3 | 10 |
| 26 | Filozoflar | Platon | `platon` | 0 | 10 |
| 27 | Filozoflar | Aristoteles | `aristoteles` | 0 | 10 |
| 28 | Filozoflar | Nietzsche | `nietzsche` | 0 | 10 |
| 29 | Filozoflar | Konfüçyüs | `konfucyus` | 0 | 10 |
| 30 | Filozoflar | Machiavelli | `machiavelli` | 0 | 10 |
| 31 | Tasavvuf & Doğu | Mevlânâ | `mevlana` | 1 | 10 |
| 32 | Tasavvuf & Doğu | Yunus Emre | `yunus` | 0 | 10 |
| 33 | Tasavvuf & Doğu | Şems | `sems` | 0 | 10 |
| 34 | Tasavvuf & Doğu | Hafız | `hafiz` | 0 | 10 |
| 35 | Tasavvuf & Doğu | Zen | `zen` | 0 | 10 |
| 36 | İnanç | Kur'an üzerine düşünceler | `kuran` | 0 | 10 |
| 37 | İnanç | İncil üzerine düşünceler | `incil` | 0 | 10 |
| 38 | İnanç | Tevrat üzerine düşünceler | `tevrat` | 0 | 10 |
| 39 | İnanç | Dua | `dua` | 0 | 10 |
| 40 | İnanç | Şükür | `sukur` | 0 | 10 |
| 41 | Spor & Beden | Antrenman | `antrenman` | 0 | 10 |
| 42 | Spor & Beden | Dayanıklılık | `dayaniklilik` | 0 | 10 |
| 43 | Spor & Beden | Sakatlıktan Dönüş | `sakatlik` | 0 | 10 |
| 44 | Spor & Beden | Sabah Rutini | `sabah_rutini` | 0 | 10 |
| 45 | Spor & Beden | Beslenme Disiplini | `beslenme` | 0 | 10 |
| 46 | İş & Başarı | Girişimcilik | `girisimcilik` | 0 | 10 |
| 47 | İş & Başarı | Kariyer | `kariyer` | 0 | 10 |
| 48 | İş & Başarı | Liderlik | `liderlik` | 0 | 10 |
| 49 | İş & Başarı | Para | `para` | 1 | 10 |
| 50 | İş & Başarı | Zaman Yönetimi | `zaman` | 1 | 10 |
| 51 | İş & Başarı | Başarısızlık | `basarisizlik` | 0 | 10 |
| 52 | İş & Başarı | Başarı | `basari` | 1 | 10 |
| 53 | İlişkiler | Aşk | `ask` | 0 | 10 |
| 54 | İlişkiler | Ayrılık | `ayrilik` | 0 | 10 |
| 55 | İlişkiler | Aile | `aile` | 0 | 10 |
| 56 | İlişkiler | Arkadaşlık | `arkadaslik` | 0 | 10 |
| 57 | İlişkiler | Yalnızlık | `yalnizlik` | 0 | 10 |
| 58 | İlişkiler | Affetmek | `affetmek` | 0 | 10 |
| 59 | Zihin & Huzur | Kaygı | `kaygi` | 0 | 10 |
| 60 | Zihin & Huzur | Stres | `stres` | 0 | 10 |
| 61 | Zihin & Huzur | Minnettarlık | `minnettarlik` | 1 | 10 |
| 62 | Zihin & Huzur | Şimdiki An | `simdiki_an` | 0 | 10 |
| 63 | Zihin & Huzur | Uyku | `uyku` | 0 | 10 |
| 64 | Zihin & Huzur | Karamsarlık | `karamsarlik` | 0 | 10 |
| 65 | Zihin & Huzur | Huzur | `huzur` | 0 | 10 |
| 66 | Öğrenme & Gelişim | Merak | `merak` | 0 | 10 |
| 67 | Öğrenme & Gelişim | Okumak | `okumak` | 0 | 10 |
| 68 | Öğrenme & Gelişim | Hata Yapmak | `hata` | 0 | 10 |
| 69 | Öğrenme & Gelişim | Alışkanlık | `aliskanlik` | 0 | 10 |
| 70 | Öğrenme & Gelişim | Sınav & Öğrencilik | `sinav` | 0 | 10 |
| | **Toplam** | **70 kategori** | | **82** | **700** |

## 10. Her koleksiyondan bir güncel örnek

Aşağıdaki örnekler yeni metin üretimi değildir; rapor hazırlanırken İngilizce kaynaktan ve aynı kimliğin Türkçe çevirisinden aynen okunmuştur. Düşünür ve inanç örnekleri de **Ascend özgün yazılarıdır**.

### Günlük olumlamalar

Kimlik: `v5_ozsefkat_01` · Kategori: Kendime nazik davranmak

**EN:** When I feel hurt, I listen for what I need and offer myself a gentler response.

**TR:** Kırıldığım yerde kendimi azarlamak yerine neye ihtiyacım olduğunu dinliyorum.

### Azim & Dayanıklılık

Kimlik: `v5_motivasyon_01` · Kategori: Motivasyon

**EN:** The blank page on your desk asks for one visible thought, not a flawless performance.

**TR:** Masandaki boş sayfa senden kusursuzluk beklemiyor; bir düşünceni görünür kılmanı bekliyor.

### Disiplin & Odak

Kimlik: `v5_erteleme_01` · Kategori: Erteleme

**EN:** Describe the delayed task with a verb: open the file, write the title, read the first question.

**TR:** Ertelediğin işi bir fiille tarif et: dosyayı aç, başlığı yaz, ilk soruyu oku. Başlangıç görünür olsun.

### Özgüven & Cesaret

Kimlik: `v5_ozguven_01` · Kategori: Kendine Güven

**EN:** You do not need a perfect sentence before speaking; a clear thought is enough.

**TR:** Söz almadan önce kusursuz bir cümle kurman gerekmiyor; anlaşılır bir düşünce yeter.

### Filozoflar

Kimlik: `v5_marcus_01` · Kategori: Marcus Aurelius

**EN:** Even in a crowded morning, I can make a little room to act with fairness.

**TR:** Kalabalık bir sabahın içinde de adil davranmak için kendime küçük bir alan açabilirim.

### Tasavvuf & Doğu

Kimlik: `v5_mevlana_01` · Kategori: Mevlânâ

**EN:** A conversation finds depth in the attention we offer each other before any grand words.

**TR:** Bir sohbetin derinliği, söylediğimiz büyük sözlerden önce birbirimize ayırdığımız dikkatte başlar.

### İnanç

Kimlik: `v5_kuran_01` · Kategori: Kur'an üzerine düşünceler

**EN:** The time I take to check a story before spreading it is part of my respect for other people.

**TR:** Bir haberi hemen yaymak yerine doğruluğunu araştırmaya ayırdığım vakit, insana duyduğum saygının parçasıdır.

### Spor & Beden

Kimlik: `v5_antrenman_01` · Kategori: Antrenman

**EN:** Putting on your trainers can mark the start of time you have set aside for yourself.

**TR:** Ayakkabılarını giydiğin an, kendine ayırdığın zamanın da başlangıcı olabilir.

### İş & Başarı

Kimlik: `v5_girisimcilik_01` · Kategori: Girişimcilik

**EN:** Listen to someone's problem before explaining your idea; that may be where what you build meets real life.

**TR:** Fikrini anlatmadan önce birinin yaşadığı sorunu dinle; kuracağın şeyin hayatla buluştuğu yer orası olabilir.

### İlişkiler

Kimlik: `v5_ask_01` · Kategori: Aşk

**EN:** Love learns to ask how the other person feels, alongside trying to understand them.

**TR:** Sevgi, karşındakinin ne hissettiğini tahmin etmek kadar, ona sormayı da öğrenir.

### Zihin & Huzur

Kimlik: `v5_kaygi_01` · Kategori: Kaygı

**EN:** If your mind keeps returning to a possibility, you can remind yourself that it is a prediction.

**TR:** Zihnin bir ihtimali tekrar tekrar getiriyorsa, onun bir tahmin olduğunu hatırlatabilirsin.

### Öğrenme & Gelişim

Kimlik: `v5_merak_01` · Kategori: Merak

**EN:** Noticing that you do not understand something is fertile ground for a good question.

**TR:** Bir şeyi anlamadığını fark etmek, iyi bir sorunun başlayabileceği verimli bir yerdir.

## 11. Veri ölçümleri ve doğrulama kapsamı

Bu rapor için iki güncel JSON dosyası birlikte okunarak elde edilen sonuçlar:

| Kontrol | Dosyalarda gözlenen sonuç |
|---|---|
| Koleksiyon / kategori / aktif düşünce | 12 / 70 / 700 |
| Kategori başına kayıt | Tam 10 |
| Türkçe karşılık | 700; kaynakla kimlik kümesi aynı |
| Yinelenen kimlik | 0 |
| Birebir aynı İngilizce / Türkçe tam metin | 0 / 0 |
| Kaynak özeti uyuşmayan Türkçe kayıt | 0 |
| İngilizce uzunluk | 54–119 karakter |
| Türkçe uzunluk | 53–117 karakter |
| İstenen uzunluk | Her iki dilde 40–120 karakter |
| Önceki sürüm arşivi | 82 kayıt; aktif kataloğa dahil değil |

Karakterler boşluk ve noktalama dahil sayıldı. Bu ölçümler içerik bütünlüğü hakkındadır; Android derlemesinin, bildirim tesliminin veya paylaşım dosyasının cihazda çalıştığının yerine geçmez.

İçerik derleyicisinin `--check` sonucu, Android birim testleri ve derleme, teslim edilen kodla eşleştirildi. Aşağıdaki başlıkların otomatik testle doğrulanan bölümleri ve gerçek cihazlarda kalan kapsam, ayrı doğrulama raporunda açıklanır:

- Yeni kurulumda ve mevcut verinin üzerinde 70 kategorinin içeriği; ücretsiz/kilitli koleksiyon ayrımı.
- Eski favorinin arşiv etiketiyle açılması; yeni akışa ve bildirime arşiv metni karışmaması.
- Dil değiştiğinde aynı kimliğin karşılığının, doğru atıfla kartta, bildirimde ve paylaşımda görünmesi.
- Seçili havuz bitene kadar çeşitlilik, tur değişimi ve hemen önceki bildirimin tekrarlanmaması.
- Aynı gün okuma, tur yenilenmesi, çakışan çalışanlar ve yeniden planlama sırasında geçmişin tutarlılığı.
- Uzun Türkçe/İngilizce sözlerde büyük yazı boyutu, kart taşması, paylaşım görseli/video ve sesli okuma.
- Demo tarayıcı açılış hatası, uygulamadan ayrılma ve geri dönme durumlarında yalnız hedef koleksiyonun açılması.

**CI kaydı:** Nihai APK bağlantısı, test sayıları ve bilinen sınırlar için [doğrulama raporuna](10-ASCEND-5-DOGRULAMA.md); testten alınan gerçek görüntüler için [ekran kaydına](11-ASCEND-5-EKRANLAR.md) bakılabilir. Önceki sürüm raporları tarihsel kayıtlardır.
