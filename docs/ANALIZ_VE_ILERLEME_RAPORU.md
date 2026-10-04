# Ascend: Kapsamlı Analiz ve İlerleme Raporu

**Sürüm:** 9.29.0 (versionCode 60) · **Tarih:** 4 Ekim 2026 · **Dal:** `claude/practical-bell-lgsw3x`

> **Ürünün vaadi:** Kişi kategori seçer, seçtiği kategorilerden gün içinde bildirim alır. Bu bir günlük motivasyon uygulamasıdır.
>
> Bu rapor kodun bu vaadi ne ölçüde yerine getirdiğini inceler. Sorunlar mantık, bildirim güvenilirliği, UI/UX, performans, mimari, içerik ve yerelleştirme başlıkları altında toplanmıştır. Raporun sonunda fazlara ayrılmış bir yol haritası ve ilerleme tablosu bulunur.

---

## 0. Yöntem ve sınırlar

- Ana kaynağın tamamı satır satır okundu: yaklaşık 13.000 satır Kotlin, buna 5 × 2.340 satırlık üretilmiş dil dosyaları dahil değil. Manifest, kaynak XML'leri, içerik verisi, CI akışı ve testler de incelendi.
- **Derleme ve test çalıştırılamadı.** Ortamda Android SDK yok. Bulgular statik inceleme ve veri analizine dayanır. İçerik sayıları ve görsel boyutları gibi ölçümler betiklerle doğrulandı.
- Her bulguda bir önem derecesi ve dosya/satır referansı var:

| Derece | Anlamı |
|---|---|
| 🔴 **Kritik** | Ürün vaadini bozuyor veya kullanıcıyı engelliyor |
| 🟠 **Yüksek** | Belirgin hata ya da ciddi UX/performans kaybı |
| 🟡 **Orta** | Tutarsızlık, kafa karışıklığı, teknik borç |
| ⚪ **Düşük** | Rötuş ve temizlik |

---

## 1. Yönetici özeti: en önemli 12 sorun

| # | Derece | Sorun | Etki |
|---|---|---|---|
| 1 | 🔴 | **Bildirimler seçilen kategorilerden gelmiyor.** Varsayılan ayarda tüm açık kategorilerden geliyor. | Uygulamanın temel vaadi bozuk |
| 2 | 🔴 | **Onboarding'de kategori seçimi yok.** Herkese aynı 3 kategori atanıyor. | Kişiselleştirme yok |
| 3 | 🔴 | **Bildirim izni verilmeden onboarding bitmiyor.** | Kullanıcı uygulamaya giremiyor (Play politikası riski) |
| 4 | 🔴 | **Ana akış seçimi tamamen yok sayıyor.** Ekrandaki "3 konu seçili" yazısı yanıltıcı. | Güven kaybı |
| 5 | 🟠 | Bildirimler WorkManager ile kuruluyor. 60 dakikadan fazla geciken bildirim **sessizce atılıyor**. | Doze/pil tasarrufunda bildirimler kayboluyor |
| 6 | 🟠 | Bildirimdeki "Favorilere ekle" düğmesi **aç/kapa** çalışıyor. Söz zaten kayıtlıysa siliyor. | Veri kaybı |
| 7 | 🟠 | Bildirim ayarları 4 katman derinde. Ayarlar ekranı bildirim kontrolü içermiyor. | Temel özelliğe ulaşmak zor |
| 8 | 🟠 | Kategori başına yalnızca **10 söz** var (toplam 1.270). Ücretsiz kullanıcı 2–3 haftada tekrara düşüyor. | Elde tutma zayıf |
| 9 | 🟠 | 40 MB'lık görsel klasörü var. Tek görsel 2–2,8 MB. | APK boyutu, bellek, takılma |
| 10 | 🟠 | Ölü özellikler: "Şu an ne iyi gelir", istatistik/seri ekranı, reklam kapısı, "Yeni söz" kısayolu, widget yenileme. | Yarım kalmış ürün hissi, bakım yükü |
| 11 | 🟡 | Mimari: 430 satırlık tek kök Composable, 35+ akış, ViewModel yok. Her akış toplayıcısı DataStore'a `edit` yazıyor. | Gereksiz recomposition ve I/O |
| 12 | 🟡 | Varsayılan değerler dağınık. Saat aralığı 10–23, 9–21 ve 9–22 olarak üç farklı yerde tanımlı. Dil varsayılanı cihaz dilinden bağımsız olarak "tr". | Tutarsız davranış |

---

## 2. Çekirdek mantık: vaat ile gerçek davranış arasındaki fark

### 2.1 🔴 Bildirimler seçilen kategorilerle sınırlı değil
`data/PersonalPlan.kt:167-171`
```kotlin
val valid = scores.keys.intersect(access)
val base = selected.intersect(valid).ifEmpty { ... }
return if ("none" in profile.answer("discovery")) base else valid
```
- `discovery` yanıtı `"none"` olmadıkça bildirim havuzu **erişilebilir bütün kategoriler** oluyor. Seçili kategoriler yalnızca ağırlık çarpanı alıyor (×5, "wide" seçiliyse ×2).
- `discovery` sorusu artık onboarding'de sorulmuyor (bkz. 3.1). Bu yüzden **neredeyse tüm kullanıcılar** bu varsayılanla çalışıyor.
- Ücretsiz kullanıcı 3 kategori seçse bile 6 ücretsiz kategorinin hepsinden bildirim alıyor. Pro demosu açıkken havuz **126 kategoriye** çıkıyor: aşk, ayrılık, para ve benzerleri.
- `PersonalPlanTest.discoveryCannotCrossEntitlementsAndCanBeDisabled` testi bu davranışı **doğru kabul ediyor**. Yani hata testlerle sabitlenmiş durumda.

**Öneri:** Bildirim havuzu = seçili kategoriler ∩ erişim ∩ içerik sınırları. Keşif isteğe bağlı bir ayar olsun (ör. "Haftada birkaç kez farklı bir konudan söz göster", varsayılanı **kapalı**). Testler buna göre güncellenmeli.

### 2.2 🔴 Ana akış seçimi tamamen yok sayıyor
`ui/Uygulama.kt:61`, `ui/Uygulama.kt:201`, `data/PersonalPlan.kt:150`
- `homeCategories(profile, access)` seçili kümeyi hiç almıyor. Ana sayfadaki sözler bütün açık kategorilerden geliyor.
- Hemen üstteki "**3 konu seçili**" kartı (`AnaEkran.kt:107-113`) kullanıcıya akışın bu konulardan oluştuğu izlenimini veriyor.
- `PersonalPlan.feed()` içindeki ağırlıklı ve uzunluk tercihine duyarlı sıralama ana ekranda **kullanılmıyor**. Yalnızca onboarding önizlemesinde çalışıyor. Ana ekran düz `QuietFeed.order` kullanıyor.

**Öneri:** Ana akış varsayılan olarak seçili kategorilerden oluşsun. "Her şeyden karışık" ayrı bir mod olsun. `PersonalPlan.feed` ile `QuietFeed` tek bir sıralama politikasında birleştirilsin.

### 2.3 🟠 Kişiselleştirme soruları ölü kod
`data/PersonalPlan.kt:180-240`: 13 soru, ağırlık tabloları ve `summary()` tanımlı. Onboarding'de bu soruların **hiçbiri** sorulmuyor. Ayarlardan yalnızca 4'üne (avoid, spirituality, format, discovery) ulaşılabiliyor. `goal`, `tone`, `energy`, `challenge`, `context`, `values` ve `length` yanıtları hiçbir kullanıcıda bulunmuyor, dolayısıyla ağırlıkların tamamı 1 oluyor.

### 2.4 🟡 Kısa yazı tercihi iki farklı metinle ölçülüyor
Akışta `quote.en.length`, bildirimde `quote.metin(language).length` kullanılıyor (`PersonalPlan.kt` feed/notification). Aynı tercih dile göre farklı sonuç veriyor.

### 2.5 🟡 Gizleme akışı başa sarıyor
`ui/Uygulama.kt:199-204`: Bir söz gizlenince `hidden` değişiyor, akış yeniden karılıyor ve `indeks = 0` oluyor. Kullanıcı okuduğu yerden atılıyor ve az önce gördüğü sözlerle karşılaşabiliyor. Akış `remember` ile tutulduğu için döndürme veya süreç ölümünde de yeniden karılıyor.

---

## 3. Onboarding

### 3.1 🔴 Kategori seçimi ve tercih soruları yok
`ui/Onboarding.kt:138-153`. Akış 5 adımdan oluşuyor:
1. Dil
2. Örnek sözler
3. Ritim
4. İzin
5. Tema

**Kullanıcıya hangi konulardan söz istediği hiç sorulmuyor.** `initialCategories` boş profil için ağırlıkların hepsini 1 sayıyor, `starterOrder` ile sıralıyor ve herkese `motivasyon, ozsefkat, marcus` atıyor. README hâlâ "20 ekran, 13 soru" diyor.

**Öneri:** 2. adım "Neye ihtiyacın var?" olsun: ızgara hâlinde kategori kartları, en az 1 en fazla 5 seçim. Ücretsizler önde, Pro olanlar kilit rozetiyle gösterilsin. Örnek söz adımı isteğe bağlı olabilir.

### 3.2 🔴 İzin verilmeden ilerlemek mümkün değil
`ui/Onboarding.kt:165-167`
```kotlin
if (step == 3 && !bildirimIzni) izinIste()
...
else if (bildirimIzni) { ... finish(true) } else move(3)
```
- Kullanıcı izni reddederse düğme ya tekrar izin istiyor ya sistem ayarlarını açıyor. **"Şimdilik geç" seçeneği yok.**
- Android 13 ve sonrasında ikinci retten sonra sistem diyaloğu bir daha çıkmıyor. Kullanıcı ayarlara gönderiliyor ve onboarding'e kilitli kalıyor.
- Google Play, bir uygulamanın izin olmadan hiç kullanılamamasını kötü uygulama olarak değerlendiriyor. Kullanıcı açısından da uygulama "açılmıyor" gibi görünüyor.

**Öneri:** "Şimdilik bildirimsiz devam et" bağlantısı eklenmeli. İzin kapalıyken ana ekranda kalıcı ama kibar bir "Bildirimler kapalı · Aç" şeridi gösterilmeli.

### 3.3 🟡 Eski ve yeni tamamlama yolları birlikte duruyor
`Uygulama.kt:156-185`: `finishProfile` (yeni) ve `bitir` (eski `onboardingKaydet`) iki ayrı kayıt yolu. Biri kullanılmıyor ama imzada zorunlu.

### 3.4 🟡 Hazır tema seçimi Pro teklifine götürebiliyor
Varsayılan tema "rider" ücretsiz. Kullanıcı Pro bir temayı önizleyip uygularsa son düğme "Görünümü aç" oluyor ve deneme teklifi açılıyor. İlk kurulumun son adımı bir satış ekranına bağlanıyor. Bu kurulum tamamlama oranını düşürebilir.

### 3.5 ⚪ Diğer
- İlerleme çubuğu "x / 5" gösteriyor, `LAST_STEP = 19` ise eski sürümden kalma bir sabit.
- Saat seçimi 25 satırlık bir liste diyaloğu. `TimePicker` ya da kaydırıcı daha hızlı olur.

---

## 4. Bildirim sistemi

### 4.1 🟠 Teslimat güvenilirliği
`notif/Planlayici.kt`, `notif/DeliveryPolicy.kt:5-8`
- Zamanlama `OneTimeWorkRequest.setInitialDelay` ile yapılıyor. WorkManager **kesin zaman garantisi vermiyor**. Doze ve App Standby kovaları gecikmeyi saatlere çıkarabiliyor.
- `DeliveryPolicy` 60 dakikadan fazla geciken işi **sessizce atıyor**. Kullanıcı açısından sonuç "bugün bildirim gelmedi" oluyor ve uygulama bunu hiçbir yerde göstermiyor.
- Ertesi günün planı 00:05'te bir Worker ile kuruluyor. Bu Worker da gecikirse sabah slotları kaçıyor.

**Öneri:** `AlarmManager.setAndAllowWhileIdle()` kullanılmalı. Tam saatte gelmesi istenirse `setExactAndAllowWhileIdle()` ile `SCHEDULE_EXACT_ALARM`/`USE_EXACT_ALARM` gerekir, ama bu Play beyanı ister. Gecikme toleransı en az 3 saate çıkarılmalı ya da kaçan slot bir sonrakiyle birleştirilmeli. "Son 7 günde X/Y bildirim teslim edildi" gibi bir teşhis satırı da eklenmeli.

### 4.2 🟠 "Favorilere ekle" aksiyonu silebiliyor
`notif/FavoriAlicisi.kt:21` `favoriDegistir` çağırıyor, bu da aç/kapa çalışıyor. Söz önceden kaydedilmişse bildirimdeki "Favorilere ekle" düğmesi onu **kaydedilenlerden çıkarıyor**.
**Öneri:** Yalnızca ekleyen bir `favoriEkle(id)` fonksiyonu yazılmalı. Bildirim kayıtlıysa aksiyon hiç gösterilmemeli.

### 4.3 🟡 Her `onResume`'da yeniden planlama
`MainActivity.kt:58, 99-104`: `onCreate` ve her `onResume`'da `Planlayici.yenidenKur`. Her seferinde 7 slot iptal edilip yeniden kuruluyor ve DataStore'a yazılıyor. Uygulamayı her açıp kapatmak pil ve I/O tüketiyor.
**Öneri:** Yeniden planlama yalnızca ayar değişikliği, açılış ve izin değişikliğinde yapılsın. Yazmadan önce yeni plan eskisiyle karşılaştırılsın.

### 4.4 🟡 Zaman hesaplarında saat dilimi ve yaz saati
`Planlayici.kt:46,49-52`: `Duration.between(LocalDateTime, LocalDateTime)` saat dilimini hesaba katmıyor. Yaz saati geçişi olan gecelerde gecikme 1 saat kayıyor. Hesap `ZonedDateTime` ile yapılmalı.

### 4.5 🟡 Bildirim kanalı ve önem düzeyi
- Kanal `IMPORTANCE_HIGH` ve `PRIORITY_HIGH`. Günde 7'ye kadar heads-up bildirim motivasyon uygulaması için rahatsız edici olabiliyor ve kullanıcıyı kanalı kapatmaya itiyor. Varsayılan `DEFAULT` olmalı, açılır bildirim isteğe bağlı bir ayar olmalı.
- `PendingIntent` ve bildirim kimlikleri `hashCode()` ile üretiliyor, çakışma ihtimali düşük ama var.

### 4.6 🟡 Bildirim durumu ana ekranda görünmüyor
`AnaEkran` `bildirimIzni`, `hatirlaticiAcik` ve `sonrakiBildirim` parametrelerini alıyor ama **hiçbirini çizmiyor**. İzni kapalı olan kullanıcı neden bildirim gelmediğini anlayamıyor. "Sonraki söz 14:30'da" bilgisi hesaplanıyor ama gösterilmiyor.

### 4.7 ⚪ Diğer
- `AcilisAlicisi` uygulama güncellemesini (`MY_PACKAGE_REPLACED`) dinlemiyor. WorkManager işleri korunuyor, ama AlarmManager'a geçilirse bu gerekecek.
- Duraklatma ekranı `"Yarına kadar ara verildi"` değerini `System.currentTimeMillis()` ile kompozisyon sırasında hesaplıyor. Gece yarısı geçince ekran kendiliğinden güncellenmiyor (`Uygulama.kt:369`).

---

## 5. Bilgi mimarisi, navigasyon ve UI/UX

### 5.1 🟠 Ana gezinme ürünün önceliklerini yansıtmıyor
Alt sekmeler: **Bugün · Keşfet · Görünüm · Senin**.
- Bir motivasyon uygulamasında "Görünüm" (arka plan teması) birinci seviye sekme olarak duruyor.
- **Bildirimlerin** sekmesi yok.
- Ayarlara yalnızca "Senin" sekmesindeki küçük dişli ikonundan ulaşılıyor.

**Öneri:** Sekmeler **Bugün · Keşfet · Bildirimler/Plan · Senin** olsun. Görünüm, Ayarlar altına taşınsın.

### 5.2 🟠 Bildirim ayarlarına ulaşmak 4 adım sürüyor
Senin → ⚙️ Ayarlar → "Bildirimlerin" → (konu paneli) "Ayarlar" → Kişisel plan paneli → "Saat ve sıklık" → Kaydet.
- `AyarlarEkrani` `adetSec`, `saatSec`, `temaSec`, `paletSec`, `dinamikSec` ve `hatirlaticiSec` parametrelerini alıyor ama **hiçbirini kullanmıyor** (`AyarlarEkrani.kt:85-138`). Ayarlarda bildirim aç/kapa anahtarı bile yok.
- Konu paneli (`BildirimKonulariPaneli`) yalnızca hâlihazırda seçili konuları listeliyor. Yeni konu eklemek için Keşfet'e gidip kategoriyi açmak ve alt sayfadaki anahtarı çevirmek gerekiyor.

**Öneri:** Tek bir "Bildirimler" ekranı yapılmalı. Ekranın içeriği:
- durum ve izin şeridi
- aç/kapa
- sıklık
- saat aralığı
- sonraki saatler
- konu listesi (ekle/çıkar, tümü bir arada)
- duraklat
- deneme bildirimi

### 5.3 🟡 Ana ekran
- `AnaEkran` 30'a yakın parametre alıyor. Bunların yaklaşık 17'si kullanılmıyor: `seri`, `haftalik`, `bugunGelenler`, `gunlukGelenler`, `bugunGorulen`, `gunlukHedef`, `sonrakiBildirim`, `oneri`, `bugunPlanlanan`, `hatirlaticiAcik`, `bildirimIzni`, `sozSecildi`, `kesfeGit`, `ipucunuKapat`, `ayarlaraGit`, `atmosferSec`, `kullaniciAdi`, `ihtiyac/ihtiyacSec`.
- Kullanıcının adı onboarding'de soruluyor, saklanıyor ama hiçbir yerde kullanılmıyor.
- "Sonraki söz" menüsü son sözden sonra başa dönüyor. Bu bir hata değil, ama sona gelindiğini belli eden bir geri bildirim yok.
- Söz metni `semantics { heading() }` ile işaretlenmiş. Ekran okuyucuda her söz başlık olarak okunuyor (bkz. 11).
- 10–12 sp etiketler (`sunumEtiketi` 10 sp, kategori 12 sp) çok küçük.
- İki ayrı `SnackbarHost` var: biri `AnaEkran` içinde, biri `Uygulama` içinde. İkisi aynı noktaya çiziliyor ve üst üste binebiliyor.

### 5.4 🟡 Keşfet
- Kategoriyi bildirimlere eklemek için: kategori satırı → alt sayfa → anahtar. Listede doğrudan seçim ya da onay kutusu yok.
- "PRO" rozeti sabit siyah zemin ve beyaz yazı ile çiziliyor (`KategorilerEkrani.kt:132`), koyu temada zeminle karışıyor.
- `LibraryQuery.filter` her recomposition'da yeniden hesaplanıyor, `remember` ile tutulmuyor.
- Yalnızca 3 kategorinin "amaç" açıklaması var (`azim`, `pes`, `yeniden`). Kalan 123 kategoride açıklama yok.

### 5.5 🟡 Tema ve karanlık mod
- Karanlık/aydınlık mod **arka plan görseline bağlı** (`Depo.kt:212-215`). Koyu bir görsel seçmek modu karanlığa çeviriyor, "Beyaz" seçmek görseli kaldırıyor.
- **Sistemi takip et** seçeneği yok.
- `temaAyarla` ve `paletSec` ayarlarda kullanılmıyor, `dinamikRenk` geçişte zorla `false` yapılıyor. Bu ayarlar fiilen ölü.

### 5.6 🟡 Açılışta yanıp sönme
`MainActivity`'deki akışların başlangıç değerleri `AYDINLIK`, `MERMER` ve `"tr"`. Karanlık temalı ya da İngilizce kullanıcı ilk karede beyaz ve Türkçe arayüz görüyor. Splash ekranı DataStore yüklenene kadar tutulmalı (`setKeepOnScreenCondition`).

### 5.7 ⚪ Uygulama kısayolu
`res/xml/kisayollar.xml` içindeki "Yeni söz" (`YENI_SOZ`) kısayolu `MainActivity.niyetiOku` içinde **ele alınmıyor**. Kısayol sadece uygulamayı açıyor.

---

## 6. Widget

- 🟡 **Tıklayınca yeni söz** davranışı yazılmış (`YenileEylemi`) ama **bağlanmamış**.
- 🟡 Ana ekranda her kaydırmada `AzimWidget.tazele` çağrılıyor (`Uygulama.kt:298`). Her kaydırma tüm widget'lar için tam boyutlu bitmap üretip `updateAll` yapıyor. Bu ciddi bir CPU ve pil israfı. Widget günün sözünü gösterdiği için kaydırmalarda güncellenmesine gerek yok.
- 🟡 Widget içeriği tek bir **bitmap** olarak çiziliyor. Erişilebilirlik (TalkBack yalnızca contentDescription görüyor), sistem yazı boyutu, karanlık mod geçişi ve büyük widget'larda bellek limiti (`TransactionTooLargeException` riski) bundan etkileniyor.
- ⚪ Ücretsiz kullanıcı widget ekleyince yalnızca "Widget'lar Ascend Pro ile" yazısını görüyor. Ürün kararı olabilir, ama widget seçicide bu belirtilmiyor.

---

## 7. Pro, demo ve gelir modeli

- 🟠 **Gerçek ödeme altyapısı yok.** "Pro demosu" tek tuşla, ücretsiz ve süresiz olarak her şeyi açıyor (`Depo.proDemoAyarla`). Bu hâliyle Pro'nun bir değeri yok, kilitler yalnızca sürtünme yaratıyor.
- 🟠 Reklamla kategori açma: `ReklamKapisi` `MainActivity`'de oluşturulup `Uygulama`'ya geçiriliyor ama **hiç kullanılmıyor**. `rememberDemoReklam` de ölü. README'deki "Google.com'u açan reklam demosu" artık yok.
- 🟡 `ProductSignals` olayları cihazda kaydediyor ama **hiçbir yerde okunmuyor**.
- **Öneri:** Google Play Billing (abonelik + ömür boyu), 7 günlük deneme, sunucu doğrulaması (ya da en azından `BillingClient` ile satın alma doğrulama) ve demo anahtarının yalnızca debug derlemesinde görünmesi.

---

## 8. İçerik

| Ölçüm | Değer |
|---|---|
| Toplam söz | **1.270** |
| Kategori | **126** (README 70/90 diyor) |
| Kategori başına söz | 124 kategoride **10**, 2 kategoride 15 |
| Ücretsiz kategori | 6 → **60 söz** |
| En uzun söz (EN) | 119 karakter |

- 🟠 **Derinlik yetersiz.** Günde 3 bildirim ve 3 kategori 30 söz demek, yani yaklaşık **10 günde** tekrara düşülüyor. Ücretsiz katalogda 20 gün. Günlük motivasyon uygulamaları için bu kısa. Rakipler kategori başına yüzlerce içerik sunuyor.
- 🟡 Bütün metinler "Ascend · Özgün düşünce". Filozof kategorilerinde (Marcus Aurelius, Seneca vb.) gerçek alıntı yok, "esinlenilmiş düşünce" var. Uydurma atıf yapmamak doğru bir karar. Ancak kullanıcı "Seneca" kategorisini açtığında Seneca'nın sözünü bekliyor. **Kamu malı gerçek alıntılar** (kaynağı doğrulanmış: *Meditations*, *Letters to Lucilius* vb.) ayrı etiketle eklenebilir.
- 🟡 Almancada hitap tutarsız: sözlerde `Sie` biçimi 1.471, `du` biçimi 49 kez geçiyor. Arayüzde `Sie` 197, `du` 61. Türkçe "sen" diliyle uyumlu olan `du`'dur.
- ⚪ Çeviriler makine çevirisi izlenimi veriyor. Örnek: "…außer mir bleiben…" yanlış anlam taşıyor, noktalı virgülden sonra büyük harf kullanılmış. Anadil kontrolü gerekli.

---

## 9. Yerelleştirme mimarisi

- 🟠 Arayüz metinlerinin büyük kısmı kod içinde `cevir(dil, "tr", "en")` çiftleri olarak yazılmış: **317 çağrı**. Diğer 5 dil çalışma zamanında **İngilizce cümleyi anahtar** olarak kullanıp tabloda arıyor. Değişkenli cümleler regex şablon eşlemesiyle çözülüyor (`Diller.metin`).
  - İngilizce metinde tek karakter değişirse diğer 5 dil **sessizce İngilizceye** düşüyor.
  - Regex eşleme her yeni cümlede maliyetli. Önbellek var ama ilk eşleme pahalı.
  - Çoğul ekleri ve sayı/tarih biçimleri yönetilemiyor.
- 🟡 `strings.xml` (140 anahtar) ile kod içi metinler karışık kullanılıyor. Varsayılan `values/` Türkçe. Desteklenmeyen bir dildeki cihazda (ör. İspanyolca) sistem kaynakları Türkçe, uygulama içi `Diller.normalize` ise İngilizce oluyor.
- 🟡 `Depo.dil` varsayılanı cihaz dilinden bağımsız olarak `"tr"` (`Depo.kt:218`).
- **Öneri:** Arayüz metinleri `strings.xml` ve `plurals` yapısına taşınmalı, tek kaynak olmalı. Sözler JSON/asset'ten yüklenmeli. Varsayılan dil cihaz dilinden türetilmeli.

---

## 10. Performans ve optimizasyon

### 10.1 🟠 Görseller
| Ölçüm | Değer |
|---|---|
| `drawable-nodpi` toplamı | **39,7 MB / 109 dosya** |
| En büyük | `art_onboarding_gateway.png` 2,8 MB (1536×1024) |
| Savaşçı görselleri | 1024×1536 WebP, **1,6–2,1 MB** (kayıpsız ya da çok yüksek kalite) |
| PNG sahneler | `scene_forest/wisdom/sea/summit` 1,5–2,4 MB |

- Aynı görseller %80 kaliteli WebP ile **150–300 KB** olabilir. Beklenen kazanç: **~30 MB APK küçülmesi**.
- `painterResource` ile büyük görseller (`KlasikDil.kt`, `Atmosfer.kt:83`, `KategoriGorseli.kt`) ana iş parçacığında, örnekleme yapılmadan tam çözünürlükte decode ediliyor.
- `KartCizici.kt:245`: paylaşım kartı için sahne görseli `inSampleSize` olmadan tam boy decode ediliyor.
- Tema yükleyici (`ThemeImages`) doğru yaklaşımı kullanıyor (örnekleme ve LRU önbellek). Tüm görsel yüklemeleri bu yoldan geçmeli ya da Coil'e geçilmeli.

### 10.2 🟡 DataStore ve recomposition
- `Depo.erisimVerisi` soğuk bir `flow`. **Her toplayıcı** başlarken `store.edit { göç }` çalıştırıyor (`Depo.kt:73-76`). `secili`, `acik`, `proDemo`, `arkaPlan`, `acikGruplar`, `tema` ve `palet` akışları, bunlara `MainActivity`'deki ikinci kopyalar ve her Worker/Widget çağrısı da ekleniyor. DataStore değişmeyen veriyi diske yazmıyor, ama her `edit` tek yazıcı kuyruğundan geçiyor ve açılışı yavaşlatıyor. Göç, uygulama başlarken bir kez yapılmalı.
- `Uygulama` 35+ akışı tek Composable içinde topluyor. Herhangi bir anahtar değişince bütün ağaç yeniden değerlendiriliyor.
- `Depo` her Worker ve Widget çağrısında yeniden oluşturuluyor. DataStore tekil olduğu için güvenli, ama flow zinciri her seferinde baştan kuruluyor.

### 10.3 🟡 Kaydırma başına yapılan işler
Ana ekranda her sayfa değişiminde:
1. `depo.gosterildi` (DataStore yazımı: RECENT, GECMIS, OKUNAN, GORULEN, BUGUN_GORULEN)
2. `AzimWidget.tazele` (tüm widget'lar için bitmap render)

Widget yenilemesi kaldırılmalı. Sayaç yazımları debounce edilmeli ya da tek anahtarda birleştirilmeli.

### 10.4 🟡 Metin okuma (TTS)
`rememberSeslendirici` her `AnaEkran` kompozisyonunda yeni bir `TextToSpeech` motoru başlatıyor (`AnaEkran.kt:69`). Sekme geçişinde `AnimatedContent` ekranı yeniden oluşturduğu için motor her seferinde bağlanıp kapanıyor. Motor ilk "Sesli dinle" isteğinde tembel olarak başlatılmalı.

### 10.5 ⚪ Diğer
- `Sozler.kategoriden` her çağrıda 1.270 elemanlık listeyi filtreliyor. Kategoriye göre `groupBy` dizini tutulmalı.
- `QuietFeed.order` içinde `recent.indexOf` sıralaması O(n·m). `recent` 700 eleman tutabiliyor. Kimlikten sıraya bir harita kurulmalı.
- 5 dil dosyası (her biri 2.340 satır, `mapOf` ile) dex boyutunu ve ilk erişim süresini artırıyor. JSON asset ve tembel yükleme daha hafif olur.

---

## 11. Erişilebilirlik

- 🟡 Söz metni `heading()` olarak işaretli (ana ekran). Başlık gezinmesini bozuyor.
- 🟡 10–11 sp metinler: söz imzası, alt gezinme etiketleri, rozetler.
- 🟡 Widget tek bitmap (bkz. 6).
- 🟡 Pro rozeti ve bazı kartlarda sabit renk kullanılıyor, tema kontrastı garanti değil.
- ✅ Olumlu: 48 dp dokunma hedefleri, büyük yazı düzenleri, `liveRegion`, `selectableGroup` ve kontrast testleri (`KontrastTest`) mevcut.

---

## 12. Mimari ve kod kalitesi

- 🟡 **Durum yönetimi:** ViewModel yok, ekran durumu `rememberSaveable` dizeleri ve `Boolean` bayrakları ile tutuluyor (`ayarlardaMi`, `planGoster`, `proGoster`, `notificationTopicsOpen`, `readerId`, `paylasilanKimlik`...). `navigation-compose` bağımlılığı ekli ama **kullanılmıyor**.
- 🟡 **Geri tuşu:** Kökteki `BackHandler` (`Uygulama.kt:128-130`) panelleri (plan, konu, Pro, okuyucu) bilmiyor. Bu davranış her panelin kendi ModalBottomSheet'ine kalıyor ve tutarsız.
- 🟡 **Karışık dil:** Tanımlayıcılar Türkçe ve İngilizce karışık (`favoriDegistir` / `completePersonalPlan`, `Sekme.ISTATISTIK` / `personalPage`). Okunabilirliği ve yeni katkıcı alımını zorlaştırıyor.
- 🟡 **Ölü kod.** Tek referanslı (tanım dışında kullanılmayan) bildirimler:
  - Ekranlar: `IstatistikEkrani`, `AtmosferSecici`, `KoleksiyonKarti`, `KilitDialog`, `SeciliKonular`, `AnlikIhtiyacSecimi`, `KapanisSahnesi`, `OnboardingZemini`, `LogoCizimi`, `KategoriGorseli`, `Kilometre/KilometreKutlamasi` (`kutlamaGunu` hiçbir yerde set edilmiyor)
  - Bileşenler: `AyarSatiri`, `BolumBasligi`, `BosDurum`, `IlerlemeCubugu`, `KucukBaslik`, `YuvarlakIkon`, `ProOzelligi`, `PaylasimBolumBasligi`, `PlanKonuEtiketi`, `KoleksiyonRolefi`, `MimariIsik`
  - Yardımcılar: `azimTikla`, `accentIzi`, `zeminFircasi`, `kontrastOrani`, `paletiCozTest`, `kisaGrupAdi`, `baslangicAdi`, `bildirimSecimOzeti`, `kategoriDurumu`, `rememberDemoReklam`, `TestKapisi`, `YenileEylemi`
  - Veri: `seri`, `rekor`, `haftalikAktiflik`, `degerlendirmeSoruldu`, `kilometreKutlandi`, `KesifMotoru` önerisi (hesaplanıyor, gösterilmiyor)
- 🟡 **Göç katmanları:** `erisimiGocur`, `gorseliGocur`, `VISUAL_V7`/`V8`, `EskiSozler`, `acikGruplar` gibi eski sürüm uyumluluk kodları birikmiş. Uygulama Play'de yayımlanmadıysa (APK test sürümü) bu göçlerin çoğu sadeleştirilebilir.
- ⚪ ProGuard yalnızca `notif.**` paketini koruyor. Release + R8 ile Glance, WorkManager ve Compose testleri yapılmamış görünüyor.

---

## 13. Test, CI ve dokümantasyon

- 🟡 Testlerin bir kısmı **hatalı davranışı sabitliyor** (2.1).
- 🟡 Testler çoğunlukla belirli bir sürüm adımına bağlı (`Phase34Test`, `Days59Test`, `AuditPhasesTest`, `DesignV82Test`...). Özellik odaklı değiller, bakım yükü yüksek.
- 🟡 CI'da PR'larda UI testleri çalışmıyor, yalnızca `push` ve elle tetiklemede çalışıyor. Build işi gereksiz yere `contents: write` yetkisi istiyor.
- 🟡 **README güncel değil:** 70/90 kategori, 700/1.200 metin, "20 ekranlı onboarding", "yalnız TR/EN" ifadeleri var. APK bağlantısı 8.2'yi gösteriyor, "6.0" bağlantısı 7.0 sürümüne gidiyor.
- 🟡 `docs/` altında **69 dosya** var (sürüm notları, eski ekran görüntüleri). Güncel durumu anlatan tek bir kaynak yok.

---

## 14. Kararlar ve iki fazlı yol haritası

### Alınan kararlar (4 Ekim 2026)
| Soru | Karar |
|---|---|
| Keşif | Bildirimler seçili konulardan gelir; **küçük oranda (≈%15) sürpriz söz** olur, ayarlardan kapatılabilir |
| İzin | Onboarding'de **"Şimdilik geç"** seçeneği olur |
| Sekmeler | "Görünüm" kalkar, yerine **Bildirimler** gelir |
| Ödeme | Play Billing **şimdilik yok**; Pro demosu sürer |
| Gerçek alıntılar | **Eklenecek** (kaynağı doğrulanmış, kamu malı) |
| Yayın | Yayında değil → eski sürüm göç kodları silinebilir |
| Dil | Öncelik **Türkçe + İngilizce**; 7 dil sonra |

### Faz 1: Sağlam çekirdek ve yeni deneyim (şu an)
| ID | İş | Durum |
|---|---|---|
| F1-1 | Veri katmanı baştan: tek durum modeli, temiz varsayılanlar, eski göçlerin silinmesi, cihaz diline göre TR/EN | ⬜ |
| F1-2 | Bildirim motoru: AlarmManager, seçili konular + %15 sürpriz, gecikme toleransı, yaz saati güvenli hesap, açılış/güncelleme alıcıları, sessiz (DEFAULT) kanal | ⬜ |
| F1-3 | Bildirimdeki "Kaydet" yalnızca ekler; kayıtlıysa düğme görünmez | ⬜ |
| F1-4 | Yeni onboarding: Dil → Konular (1–5) → Ritim → İzin (geçilebilir) → Hazır | ⬜ |
| F1-5 | Yeni navigasyon: Bugün · Keşfet · Bildirimler · Senin; tek ViewModel | ⬜ |
| F1-6 | Bugün: seçili konulardan akış, durum şeridi, sonraki bildirim saati, konum korunarak gizleme | ⬜ |
| F1-7 | Keşfet: arama, koleksiyonlar, listeden doğrudan "bildirimlere ekle" | ⬜ |
| F1-8 | Bildirimler merkezi: izin durumu, aç/kapa, sıklık, saat aralığı, saat önizlemesi, konular, sürpriz, bugün ara ver, deneme bildirimi | ⬜ |
| F1-9 | Senin: seri, istatistik, kaydedilenler, geçmiş; Ayarlar: dil, tema (sistem/açık/koyu), arka plan, titreşim, Pro demo, gizlenenler | ⬜ |
| F1-10 | Yeni görsel kimlik (sıcak kâğıt zemin + amber vurgu), açılışta yanıp sönme yok | ⬜ |
| F1-11 | Ücretsiz katalog 6 → 26 konu | ⬜ |
| F1-12 | Görsel optimizasyonu (~40 MB → ~8 MB), ölü kod temizliği, widget'ın kaydırmada yenilenmemesi | ⬜ |
| F1-13 | TR/EN `strings.xml`; diğer 5 dil geçici olarak kapalı | ⬜ |
| F1-14 | Testler, CI ve README güncellemesi | ⬜ |

### Faz 2: Derinlik ve cila
| ID | İş | Durum |
|---|---|---|
| F2-1 | Doğrulanmış kamu malı gerçek alıntılar (Marcus Aurelius, Seneca, Epiktetos, Mevlana...) ayrı etiketle | ⬜ |
| F2-2 | İçerik derinliği: konu başına 30+ söz | ⬜ |
| F2-3 | Widget yenileme: dokununca yeni söz, bitmapsiz erişilebilir düzen | ⬜ |
| F2-4 | Paylaşım stüdyosu, duvar kâğıdı ve kısa serilerin yeni tasarıma uyarlanması | ⬜ |
| F2-5 | Erişilebilirlik turu ve yeni UI testleri (emülatör) | ⬜ |
| F2-6 | 5 dilin `strings.xml` ile geri dönüşü ve anadil kontrolü | ⬜ |
| F2-7 | Pro deneyimi: billing hazırlığı, demo yalnızca debug derlemede | ⬜ |
| F2-8 | Dokümantasyon arşivi: `docs/` sadeleştirme | ⬜ |
