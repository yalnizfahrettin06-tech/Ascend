# Ascend 9.16: beş yeni dil

## Kapsam

Türkçe ve İngilizceye Portekizce (`pt`), Almanca (`de`), Fransızca (`fr`), İtalyanca (`it`) ve Rusça (`ru`) eklendi. Her yeni dilde 126 kategorinin 1.270 aktif metni bulunur: toplam 6.350 yeni içerik çevirisi. Portekizce metinlerde Brezilya kullanımına yakın sözcükler kullanıldı; ayrı bir Avrupa Portekizcesi paketi hazırlanmadı.

Arayüz, kategori/tema adları, kısa seri soruları, bildirim eylemleri, widget metinleri ve paylaşım aynı dil seçimini kullanır. Onboarding ve ayarlarda yedi seçenek vardır. Mevcut metin kimlikleri, kayıtlar ve erişim hakları değişmez. Önceki sürümden kalmış arşiv metinlerinin yeni dil karşılığı yoksa İngilizce arşiv metni gösterilir; aktif katalog tam çevrilmiştir.

Çeviriler gömülüdür. Uygulamada çeviri servisi, internet izni veya ilk kullanımda dil indirme zorunluluğu yoktur.

## Çeviri niteliği

Yeni çeviriler Google Translate üzerinden makine destekli taslak olarak üretildi. Ana arayüz terimleri elle düzenlendi; her dilin içeriklerinden temsilî örnekler okunup kontrol edildi. Bunlar beş dilde bağımsız ana dil editörü tarafından bütünüyle onaylanmış metinler değildir. Mağaza çapında yayın öncesinde özellikle hitap tutarlılığı, mecazlar ve anlam nüansları için ana dil editörü incelemesi yapılmalıdır. Otomatik bütünlük testi editoryal kalite onayı yerine geçmez.

`content/ui/reviewed-terms.txt`, gözden geçirilmiş ana terimleri saklar. `content/translations/<dil>.json` dosyaları çeviri durumunu ve İngilizce kaynak parmak izini taşır. Tarihî kişilere aitmiş gibi yeni alıntı üretilmedi; mevcut özgün Ascend metinleri çevrildi.

## Sürdürülebilir düzen

- `Diller`: dil listesi, bölgesel dil kodunun normalleştirilmesi, metin ve içerik erişimi.
- `content/ui/source.en.json` ve dil dosyaları: arayüz sözlüğü. Mevcut iki dilli çağrıların dinamik değerleri doğrulanmış şablonlarla eşleşir. Sonuç önbelleği sınırlıdır.
- `content/translations/<dil>.json`: kimliğe bağlı içerik çevirileri. İngilizce veya Türkçe değişiklikler metin kimliğini değiştirmez.
- `tools/build_locales.py`: çevrimdışı Kotlin tablolarını ve Android `values-<dil>/strings.xml` dosyalarını üretir. `--check` çıktının kaynağa uygunluğunu denetler; ağ bağlantısı kullanmaz.
- Tablolar yalnız ilgili dil kullanılınca yüklenir. Kaynak kodunda üretilen dosyalar elle düzenlenmez.
- `tools/localize_catalog.py` / `review_locales.py`: açıkça çalıştırılan editoryal hazırlık araçlarıdır; uygulamanın veya CI derlemesinin parçası olarak çeviri servisine bağlanmazlar. Dolu çeviri dosyaları yeniden üretilecekse önce editoryal değişiklikler korunmalıdır.

Yeni dil iş akışı: kaynak kimliklerini ve arayüz şablonlarını al → çevir → editoryal incele → dil kaydına ekle → üret → denetle. Eksik içerik, eski kaynak parmak izi, kaybolan yer tutucular ve tekrarlar yayını durdurur. Bu sürümde yeni diller için metin sınırı 20–300 karakterdir; Türkçe/İngilizcenin 40–120 sınırı körlemesine uygulanmaz. Bildirim havuzunun sınırı 400 olduğundan yeni çeviriler havuzdan dışlanmaz; genişletilmiş bildirim tam metni taşır.

## Kontrol kapsamı

Otomatik kontroller: beş dilin tüm içerik kimlikleri, kaynak parmak izleri, tekrarlar, Unicode, şablon değerleri, dil geri dönüşü ve kategori erişimi. Kısa Android testleri: dil seçiminde beş yeni dil arasında geçiş, Rusça onboarding, tercihlerin saklanması, yerel Android kaynakları. Mevcut kısa içerik/paylaşım testleri de korunur. Bu kapsam, her ekranda her metnin tüm cihazlarda tek tek incelendiği anlamına gelmez.

Inter ve Lora'nın hem ekran hem paylaşımda kullanılan dosyalarında Kiril ve yeni Latin dili karakterleri doğrulandı.

## Android kaynakları

Android arayüz dil kaynaklarını seçilen yapılandırmayla yükler; içerik çevirileri bundan ayrı yönetilir. Dil seçeneğini eklemek içerikleri otomatik çevirmez. [Android yerelleştirme rehberi](https://developer.android.com/guide/topics/resources/localization), [Android metin kaynakları](https://developer.android.com/guide/topics/resources/string-resource).
