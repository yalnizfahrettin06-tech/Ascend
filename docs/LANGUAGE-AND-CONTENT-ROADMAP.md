# Ascend: dil ve içerik büyüme planı

13 Eylül 2026 · 9.15.0 / katalog 8.0.0

## Karar

Şimdilik mevcut GitHub deposunu ve uygulamaya gömülü Türkçe–İngilizce içeriği koruyalım. Yeni depo açılması veya yeni bir hesap bağlanması gerekmiyor. 30–35 dil için önce tek bir dil ekleme düzeni kurulmalı; indirme sistemi ikinci adım olmalı. Bugünkü APK yalnızca Türkçe ve İngilizce sunar; uzaktan dil indirme henüz uygulanmadı.

## Mevcut yapı ve sınırı

- `content/source.en.json`: İngilizce ana kaynak; sabit söz kimlikleri ve kategori ilişkileri.
- `content/translations/tr.json`: aynı kimliklere bağlı Türkçe metinler. İngilizce metnin parmak izi, çeviri eski kaldığında bunu yakalar.
- `tools/icerik_derle.py`: çevrimdışı Kotlin verisini üretir. Şu anda çalışma zamanı EN/TR olarak tasarlanmıştır. Başka bir JSON dosyası koymak tek başına yeni dili etkinleştirmez.
- Kategori adları ve arayüz metinleri de iki dil seçen kod içeriyor. Bunların bağımsız dil kaynaklarına taşınması gerekir.
- Bu sürümde kategori başına tam 10 zorunluluğu kaldırıldı. Editoryal teslimler 10, 15, 20 ve üzeri beşin katlarıyla doğrulanıyor. Eski kimlikler korunuyor.

## Neden hemen sunucu gerekmiyor?

Bugünkü 1.270 metinlik Türkçe JSON, kimlik ve kaynak parmak izleri dahil 296.897 bayt; gzip ile 106.222 bayt. Aynı boyutta 35 dil yaklaşık 10,4 MB ham / 3,7 MB gzip eder. Bu yalnızca metin paketleri için yaklaşık ölçek hesabıdır; APK boyutu tahmini değildir. Farklı alfabeler, fontlar ve gelecekteki içerik artışı boyutu değiştirecektir.

Görseller dil başına çoğaltılmamalı. Aynı arka planın üstüne seçilen dilin metni uygulamada çizilir. Asıl karar boyuttan çok içerikleri ne sıklıkla APK dışında güncellemek istediğimizdir.

## Faz 1 — Bir dil eklemeyi kolaylaştır

1. Menü, onboarding, izin, hata, Pro demo, paylaşım ve widget metinlerini Android dil kaynaklarında topla; sayı içeren metinlerde çoğul kurallarını kullan.
2. Kategori adlarını ve açıklamalarını sabit kimliklerle dil dosyalarına ayır. İngilizce kaynak ve özgün metnin kimliği değişmesin.
3. Desteklenen dilleri tek kayıt üzerinden yönet: dil etiketi, kendi dilinde adı, yazım yönü, arayüz ve içerik tamamlanma durumu.
4. Söz okuyucusunu EN/TR alanlarına bağımlı olmaktan çıkar; seçilen dil → İngilizce yedek sırasını tutarlı uygula. Bildirim, ana ekran, geçmiş, paylaşım ve widget aynı kaynağı kullansın.
5. Android uygulama dili tercihini sistem ayarıyla uyumlu hale getir. Dil seçeneğini göstermek çeviri üretmez; hazır olmayan dili yayımlama. [Android uygulama dili rehberi](https://developer.android.com/guide/topics/resources/app-languages)
6. İlk deneme olarak bir uzun metinli Latin dili ve bir sağdan sola dil üzerinde yerleşimi sınayalım. Ardından dilleri küçük gruplarla yayımlayalım. Sağdan sola akış, font kapsamı ve satır kırılması ayrıca kontrol edilmeli.

Kabul ölçütü: Yeni dil eklemek yeni bir ekran kodu yazmayı gerektirmemeli; kaynak dosyası, dil kaydı, editoryal onay ve kontroller yeterli olmalı. Dil değişiminde kaydedilenler ve bildirim konuları kaybolmamalı.

## Faz 2 — Çeviri kalitesi ve düzenli yayın

- Ana metin, çeviri, kaynak sürümü, inceleme durumu ayrı tutulmalı. Kaynak değişince çeviri yeniden incelenmeli.
- Eksik/fazla kimlik, tekrar eden metin, bozuk Unicode ve yer tutucu hataları otomatik yakalanmalı.
- Türkçe/İngilizce için mevcut 40–120 karakter sınırı diğer dillere körlemesine taşınmamalı. Dil başına uygun sınır ve gerçek bildirim görünümü kontrol edilmeli; doğal ifade sayıya feda edilmemeli.
- Makine çevirisi taslak olabilir; yayın öncesi o dili bilen bir editör anlamı, doğallığı ve kültürel uygunluğu incelemeli.
- Üslup: somut düşünce veya eylem; baskıcı başarı vaadi, kesin psikolojik sonuç, boş slogan ve yanlış kişiye atıf olmamalı.
- Üretim sayısı hedef değil düzen ölçütü: iyi 10 metin, dolgu 20 metinden daha değerlidir. Yeterli kaliteli metin hazırsa sonraki beşli eklenir.

Kullanıcıdan ileride gereken karar: İlk dil grubu ve ana dilde incelemeyi kimin yapacağı. Şu an depo veya teknik kurulum yapmana gerek yok.

## Faz 3 — İhtiyaç doğarsa indirilebilir dil paketleri

Mevcut depoda sürümlü GitHub Release dosyalarıyla başlanabilir. Ayrı içerik deposu ancak editörlere uygulama kodundan bağımsız erişim ve yayın döngüsü gerektiğinde anlamlı olur. GitHub Releases sürümlere dosya eklemeyi destekler. [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases)

Önerilen akış: Dil seç → paket boyutunu göster → arka planda indir → doğrula → tek işlemle etkinleştir. Başarısız indirmede eski çalışan paket korunur; internet yokken kurulu içerik kullanılmaya devam eder. Bu yaklaşım Android'in yerel veri kaynağına dayanan çevrimdışı mimarisiyle uyumludur. [Android offline-first](https://developer.android.com/topic/architecture/data-layer/offline-first)

Paket manifesti: dil, şema sürümü, katalog sürümü, minimum uygulama sürümü, dosya boyutu, özet değeri ve sürümlü URL. Kimlik kapsamı kontrol edilmeli; kısmi indirme etkinleştirilmemeli. Özet değeri bozulmayı yakalar; yayıncı doğrulaması gerekiyorsa ayrıca imzalı manifest kullanılır. Her açılışta dosya indirilmez. Arka planda periyodik kontrol yapılır; yeni paket bildirim çalışması sırasında mevcut kaynağı yarım bırakmaz.

GitHub herkese açık içerik dağıtımı, Pro yetkilendirme sistemi değildir. Mevcut demo Pro aynı kalır; gerçek ödeme veya özel içerik sunucusu bu çalışmaya dahil değildir. Görsel indirme daha sonra ayrı bir paket olarak ele alınabilir ve dile bağlanmamalıdır.

## Bu sürümün içerik ve arayüz değişiklikleri

- 4 yeni kişi: Sokrates, Demokritos, Plotinos, Pyrrhon; 10'ar özgün yorum.
- Tao ve Budist düşünce: 15'er özgün yorum. Doğu geleneği artık Zen dahil 3 bölümdür.
- Toplam 126 kategori / 1.270 EN–TR metin çifti; Ünlü düşünürler 36 kişi.
- Konu detayında tekrarlanan grup, slogan ve uzun bildirim açıklaması kaldırıldı; başlık, sayı ve bildirim anahtarı kompakt tutuldu.
- Görsel/Video seçimine karşıt renk ve seçili işareti eklendi.
- Paylaşım arka planları kategorilerden ayrıldı; aynı yedek görsele bağlanan kişi listesi kaldırıldı. Gerçek atmosfer görselleri görsel adlarıyla sunuluyor ve aynı kaynak tekrarlanmıyor.

## Yeni metinlerin kaynak yaklaşımı

Bu 70 metin doğrudan tarihî alıntı veya çeviri değildir. Düşünürlerin ve geleneklerin temaları üzerine özgün Ascend yorumlarıdır; uygulamada da böyle belirtilir. Konu araştırmasında aşağıdaki maddeler kullanıldı. Özellikle Demokritos'a atfedilen etik metinlerin kaynak tartışması nedeniyle kesin tarihî alıntı iddiasında bulunulmadı.

- [Sokrates'in sorulama geleneği / Platon](https://plato.stanford.edu/entries/plato/)
- [Demokritos](https://plato.stanford.edu/entries/democritus/)
- [Plotinos](https://plato.stanford.edu/archives/fall2020/entries/plotinus/)
- [Pyrrhon](https://plato.stanford.edu/entries/pyrrho/)
- [Tao geleneği](https://plato.stanford.edu/entries/daoism/)
- [Budist etik](https://plato.stanford.edu/entries/ethics-indian-buddhism/)

Doğrudan alıntılar ileride ayrı türde eklenirse eser, bölüm ve güvenilir metin kaynağı kaydedilmeli; özgün yorumla karıştırılmamalıdır.
