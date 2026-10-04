# Azim — Yeniden Yazım, Teslimat Notları

**Sürüm:** 1.0.0 (versionCode 1) · **Paket:** `com.yalnizfahrettin.azim`
**Hedef:** minSdk 26 / targetSdk 35 · Kotlin 2.0.21 · Compose BOM 2024.10.01

Bu proje eski `com.example.azim` (v5.7.0 / build 228) kod tabanının devamı
değil, sıfırdan yazımıdır. Veri modeli fikirleri (kategori anahtarları, seri
mantığı) korundu; arayüz, tema, bildirim hattı ve yapı yeniden kuruldu.

---

## 1. Eski sürümdeki yayın engelleri — durum

| Engel | Eski | Yeni |
|---|---|---|
| Paket adı | `com.example.azim` (Play kabul etmez) | `com.yalnizfahrettin.azim` |
| `debuggable` | `true` | yok |
| R8 / kaynak küçültme | kapalı, 43.5 MB dex | açık |
| İmza | debug | release keystore |
| Sahte reklam | `google.com` açıp bedava açıyordu | kaldırıldı, bkz. §4 |

> **Paket adı kalıcıdır.** Play Store'a ilk yükleme sonrası değiştirilemez.
> Farklı bir ad istiyorsan yayından ÖNCE `app/build.gradle.kts` içindeki
> `namespace` + `applicationId` ve tüm `package` satırlarını değiştir.

---

## 2. Bildirim okunabilirliği — karar ve gerekçe

Bu, projenin 2. öncelikli maddesiydi: "gelen bildirimdeki cümle tam okunsun."

### Araştırma bulguları

- **Toplu (collapsed) bildirim** gövde metnini tek satıra kırpar. Stok
  Android'de ~90 karakter görünür; Samsung One UI, MIUI gibi OEM kabukları
  daha agresif kırpar, pratikte ~45–50 karakter.
- **`NotificationCompat.BigTextStyle`** genişletilmiş alanda çok satırlı
  metin gösterir; teknik sınır 5120 karakter.
- **Bildirimi programatik olarak "zorla açık" göstermenin API'si yok.**
  `setCustomContentView` denenebilir ama Android 12+ özel görünümleri yeniden
  dekore eder ve toplu görünüm yüksekliği yine sabittir.

### Karar: ikisi birlikte

Tek başına "bildirimi büyütmek" güvenilir değil. Bu yüzden iki katmanlı:

**(a) Yapı — `notif/Bildirimler.kt`**
- `setContentTitle` = kategori adı (kısa, hiçbir cihazda kırpılmaz)
- `setContentText` = sözün kendisi (toplu görünümdeki tek satır)
- `BigTextStyle.bigText` = söz + yazar (genişletilmiş tam metin)
- `VISIBILITY_PUBLIC` — kilit ekranında da tam metin görünür
- `IMPORTANCE_DEFAULT` — gölgeliğe düşer, ekranı basmaz

**(b) İçerik — `data/Sozler.kt`**
- `BILDIRIM_SINIRI = 120` karakter (tavan)
- `IDEAL_SINIR = 90` karakter (hedef)
- `SozWorker` yalnızca `bildirimeUygun` sözleri gönderir

**(c) Bekçi — `test/SozUzunlukTest.kt`**
Havuza sınırı aşan bir söz eklenirse `./gradlew test` kırılır. Kırpılmış
bildirim üretime çıkamaz. Havuzu doldururken bunu çalıştır.

**Sonuç:** 120 karakter genişletilmiş görünümde en fazla 3 satır — kırpılma
yok. 90 karakterin altındaki sözler çoğu cihazda toplu görünümde bile tam
okunur.

---

## 3. UI/UX — neyi neden değiştirdim

### Alt navigasyon (en görünür hata)
Ekran görüntülerinde aktif öğe bir "hap" içindeydi ve etiket kelime
ortasından bölünüyordu: `Ana / Ekran`, `Kategori / ler`, `İstatistik / ler`.

Çözüm kozmetik değil yapısal (`ui/Iskelet.kt`):
1. Dört öğe eşit ağırlıklı sütun (`weight(1f)`) — genişlik içeriğe bağlı değil
2. Etiket `maxLines = 1` + `softWrap = false` + `Ellipsis` — bölünme imkânsız
3. Tek kelimelik etiketler: "Ana", "Kategori", "Favori", "İstatistik"
4. Aktiflik hap ile değil; dolu ikon + accent + 18dp ince gösterge çizgisi

### Renk sistemi (`core/Tasarim.kt`)
Eskide iki paralel `AzimTheme` vardı, biri ölüydü. Artık tek dosya.

Asıl sorun accent rengin aynı anda nav, kart kenarlığı, checkbox, başlık ve
istatistikte "ana karakter" olmasıydı — sebebi nötr yüzeyler (lacivert) ile
doygun accent (bordo) arasında **ara ton olmamasıydı**. Vurgulanacak her şey
doğrudan bordoya sıçrıyordu.

Palete `metinIkincil`, `kenarlikGuclu`, `accentSonuk`, `accentZemin` eklendi.
**Accent kuralı:** yalnızca (1) aktif nav öğesi, (2) birincil eylem —
dolu kalp, seri alevi, (3) ilerleme çubuğunun dolu kısmı.

### Ölçü sistemi
Eski `DpSpacing`'de `xs/sm/md` yanında `s5, s6, s7, s10, s14, s18, s22, s26,
s34, s36` gibi 14 adet "isim verilmiş magic number" vardı; 4/8 ızgarası zaten
kırıktı. Yeni `Olcu` yalnızca ızgaraya oturan 9 değer içerir. Ara değer
gerekiyorsa bu bir tasarım hatasıdır, yeni token değil.

### Kategoriler ekranı
Eskide seçili her satır doygun bordo blok halinde doluyordu; 35 satırın
yarısı ekranı boğuyordu ve başlık üstte kırpılıyordu.

Yeni durum dili — doygun dolgu hiçbir durumda yok:
- **seçili** → ince accent kenarlık + çok hafif accent zemin + dolu tik
- **açık, seçili değil** → nötr yüzey + boş halka
- **kilitli** → nötr yüzey, sönük metin + kilit ikonu

Satırlar 56dp sabit yükseklikte; liste ritim kazandı.

### Ayarlar
Eskisi 911 satır, 8 bölüm ve içinde bir **arama kutusu** ("Ayarları
filtrele...") barındırıyordu. Bir ayar ekranına arama koymak zorunda kalmak,
o ekranın artık bir ekran olmadığının kanıtıdır.

Yeni: üç bölüm (Görünüm / Bildirimler / Hakkında), tek ekran, arama yok.
Kart stili + kart teması + kart fontu + kart rengi + kart fotoğrafı + kart
boyutu özelleştirmeleri kaldırıldı — kombinasyon uzayı test edilemiyordu ve
kullanıcının ilgilendiği şey söz, kartın fontu değil.

### Ana ekran
Kaldırılanlar: aurora animasyonlu arka plan, konfeti, kutlama katmanı, eğim
(tilt) tepkisi, "0 Favori · 12 Keşif · 5 İlerleme" accent istatistik satırı,
hafta çubuğu, dört farklı şekilde çizilmiş eylem düğmeleri.

Hiyerarşi: **söz > yazar > kategori > eylemler > seri**. Dört eylem düğmesi
aynı boy (52dp), aynı şekil, aynı renk. Seri üst köşede tek satır. Diğer
istatistikler kendi sekmesine taşındı.

### İstatistik
Eskide etiket yazımı tutarsızdı ("Bugün", "favoriler", "Gün", "En çok" yan
yana) ve tüm sayılar accent renkteydi. Yeni: sayılar nötr ve büyük, etiketler
küçük ve sönük, accent yalnızca seri alevinde.

---

## 4. Reklam / kilit mekanizması — DİKKAT

Eski `ReklamYoneticisi.goster()` şunu yapıyordu:

```kotlin
startActivity(Intent(ACTION_VIEW, Uri.parse("https://www.google.com")))
onTamamlandi()   // kategori bedava açılıyordu
```

Sahte reklamdı (Play politika ihlali) ve kilidi anlamsız kılıyordu.
v3.6.1'den v5.7.0'a 228 derlemede düzeltilmemişti.

Yeni `ui/ReklamKapisi.kt` bir arayüz:
- `VarsayilanKapi` — **ödül vermez** (sürüm derlemesinde bağlı)
- `TestKapisi` — her zaman ödül verir, **yalnızca debug**

Ödül gelmezse kategori açılmaz. Sahte açma yok.

### AdMob bağlama adımları

1. `app/build.gradle.kts`:
   `implementation("com.google.android.gms:play-services-ads:23.6.0")`
2. `AndroidManifest.xml` → `<application>` içine:
   `<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXX~YYYY"/>`
3. `AdMobKapisi : ReklamKapisi` yaz; `RewardedAd.load(...)` + `show(...)`,
   `onSonuc(true)` **yalnızca** `onUserEarnedReward` geldiğinde çağrılsın.
4. `MainActivity`'de `VarsayilanKapi()` yerine `AdMobKapisi(...)` geç.

Test birimleri için Google'ın resmi test ID'lerini kullan; kendi ID'ni
yayından önce koy.

---

## 5. Derleme

```bash
export ANDROID_HOME=/yol/android-sdk
./gradlew :app:assembleRelease     # imzalı APK
./gradlew :app:bundleRelease       # Play için AAB
./gradlew test                     # söz uzunluğu bekçisi dahil
```

Çıktı: `app/build/outputs/apk/release/app-release.apk`

**Keystore:** `app/azim-release.jks` (alias `azim`, şifre `azim2026`).
Bu geliştirme anahtarıdır. Play'e yükleyeceğin anahtarı **kaybedersen
uygulamayı bir daha güncelleyemezsin** — Play App Signing kullan ve `.jks`
dosyasını versiyon kontrolüne KOYMA (`.gitignore` hazır).

---

## 6. Kapsam — şu an ne var, ne yok

**Var:** 5 ekran (Ana, Kategoriler, Favoriler, İstatistik, Ayarlar), 35
kategori (8 ücretsiz + 27 reklamla açılan), bildirim planlayıcı (WorkManager,
1–7/gün, saat aralığı, ±20 dk kayma, yeniden başlatma dayanıklı), favoriler,
seri/rekor, TR + EN, 4 tema (sistem/aydınlık/karanlık/OLED).

**Yok (bilinçli):** widget, onboarding akışı, paylaşım kartı özelleştirme,
10 dil, dolu söz havuzu. Senin dediğin gibi içerik sonraya bırakıldı —
şu an her kategoride birkaç söz var, bazıları boş. UI bunu normal karşılar.

**Sonraki adım için önerim:** önce söz havuzunu doldur (`data/Sozler.kt`,
`./gradlew test` ile doğrula), sonra widget'ı tek bir yolla ekle. Eski
projede üç paralel widget çizim yolu vardı — o hatayı tekrarlamayalım.

---

## 7. Otomasyon uyarısı

Eski projedeki hayalet kodun kaynağı, var olmayan dosya yollarına yazan
otomasyon vardiyalarıydı (`ui/theme/Color.kt`, `SILIN-DIkkat-hedef-yok`).
Sistem sadece `assembleDebug` doğruluyor, `assembleRelease` hiç
çalıştırmıyordu — yayın engellerinin 228 derlemede fark edilmemesinin sebebi
buydu.

Bu projeyi o otomasyona bağlamadan önce: hedef yol doğrulaması ekle ve
doğrulama adımını `assembleRelease` + `test` yap.

---

## 8. Sürüm 1.1.0 — eksiklik raporundan uygulananlar

`AZIM_EKSIKLIK_RAPORU.md` maddelerine göre yapılanlar:

| Rapor # | Madde | Nerede |
|---|---|---|
| 1.1 | 3 adımlı onboarding | `ui/Onboarding.kt` |
| 1.2 | Launcher ikonu yeniden tasarlandı | `res/drawable/ic_launcher_*.xml` |
| 1.3 | Markalı splash | `res/values/themes.xml`, `ic_splash.xml` |
| 1.4 | Bildirim izni bağlam içinde | onboarding son adımı |
| 1.5 | İlk açılışta kategori seçimi | `Onboarding.AdimKategori` |
| 2.1 | Haptik geri bildirim | `core/Etkilesim.kt` |
| 2.2 | Basma animasyonu (%96 + yay) | `Modifier.azimTikla` |
| 2.3 | Yönlü sekme geçişleri | `ui/Uygulama.kt` |
| 2.7 | Dinamik renk (Android 12+) | `core/Tasarim.kt` |
| 3.1 | Widget — Glance ile **tek** yol | `widget/AzimWidget.kt` |
| 4.1 | Seri kilometre taşları | `ui/Kutlama.kt` |
| 4.2 | Paylaşım kartı görseli (1080×1350) | `paylas/PaylasimKarti.kt` |
| 4.3 | Söz tekrarı önleme (geçmiş) | `Depo.gosterildi`, `Sozler.rastgele` |
| 4.5 | Launcher kısayolları | `res/xml/kisayollar.xml` |
| 5.1 | İngilizce sözler | `data/Sozler.kt` — `Soz(tr, en, ...)` |
| 7.3 | Yedek dışa/içe aktarma | `Depo.disaAktar` / `iceAktar` |
| 7.4 | Test 4 → 12 | `SozUzunlukTest`, `KilometreTest` |

### Yapılmayanlar ve sebebi

| Rapor # | Madde | Neden bende değil |
|---|---|---|
| 7.1 | Crash reporting | Firebase projesi + `google-services.json` gerekli |
| 7.2 | Analytics | aynı |
| 8.1 | Gizlilik politikası | barındırılmış URL + hukuki karar gerekli |
| 8.2 | Mağaza görselleri | ürün kararı |
| 8.3 | Play App Signing | Play Console erişimi gerekli |
| 5.2 | Diğer diller | önce tr/en havuzu dolmalı |
| 5.3 | Uzak içerik güncelleme | backend kararı gerekli |
| 9.x | Premium satın alma | fiyatlandırma kararı gerekli |
| 6.x | Erişilebilirlik doğrulaması | gerçek cihazda TalkBack testi gerekli |
| 2.5, 2.6, 2.8, 10.x | İllüstrasyon, ses, tablet | sonraki faz |

### ⚠️ Bu APK R8'siz derlendi

Geliştirme konteyneri tek çekirdek / 3.9 GB olduğu için R8 küçültme adımı
bellek yetmediğinden düştü. Teslim edilen APK `-PazimMinify=false` ile
üretildi: **8.5 MB, küçültme yok.**

Kaynakta R8 varsayılan olarak AÇIK. Kendi makinende:

```bash
./gradlew :app:assembleRelease          # R8 açık — yayın için bunu kullan
./gradlew :app:bundleRelease            # Play için AAB
```

1.0.0'da R8 açık derleme 1.4 MB veriyordu; 1.1.0'da Glance eklendiği için
biraz büyüyecek ama yine 2 MB civarı beklenir. **Play'e yüklemeden önce
mutlaka R8 açık bir derleme al.**

---

## 9. Sürüm 1.2.0 — renk, yumuşatma, alt nav, ana ekran

### Renk paleti — asıl sorun neydi

Şikâyet "renkler tutarsız" idi ve haklıydı, ama sebep tek tek renk seçimleri
değildi: **aydınlık tema krem kâğıt (sıcak), karanlık tema lacivert (soğuk)**
idi. Marka aydınlıkta sıcak, karanlıkta soğuk hissettiriyordu. Accent
(bordo/gül) da ikisiyle tam oturmuyordu — lacivertle çamurlaşıyor, kremle
tozlu duruyordu.

Çözüm: `core/Paletler.kt`. Üç palet, her biri **kendi içinde ısı bakımından
tutarlı** — bir paletin aydınlık ve karanlık sürümü aynı sıcaklıkta, accent
ikisinde de aynı aileden. Tema değiştirmek artık markayı değiştirmiyor.

| Palet | Nötrler | Accent | Karakter |
|---|---|---|---|
| **Kehribar** (varsayılan) | sıcak kömür | kehribar `#E0A94D` | Lora serif ile en iyi eşleşen; kâğıt/mürekkep hissi |
| **Deniz** | soğuk arduvaz | çam-teal `#4FB3A4` | sakin, odaklı |
| **Gül** | erik kömürü | rafine gül `#D9819A` | eski bordonun temizlenmiş hali |

Gül paleti önemli: eski bordoyu atmadım, **düzelttim**. Nötrler artık lacivert
değil erik kömürü (accent ile aynı ısıda, bu yüzden çamurlaşmıyor) ve accent'in
doygunluğu düşürülüp bir ton açıldı — "ucuz pembe" hissi böyle kayboluyor.
Marka sürekliliğini isteyip yeni hissi de isteyenler için bu seçenek duruyor.

Her palette nötr ölçek 6 basamak. Accent hâlâ yalnız üç yerde: aktif nav,
birincil eylem, ilerleme dolgusu.

> Üç palet ayarlardan seçilebiliyor. Bu, eski sürümün kart stili × font ×
> renk × foto × boyut kombinasyon patlamasından farklı: serbest karışım yok,
> üç bütünlüklü set var — her biri tek başına tasarlanmış.

### Yumuşatma

`Yaricap` bir kademe yukarı: 8/12/16 → **12/18/24 + yeni xl 30**.
Ana ekrandaki eylem düğmeleri artık tam daire (56dp). Dokunulabilir her
yüzey belirgin şekilde yuvarlak.

### Alt navigasyon — üçüncü ve son tasarım

Geçmişteki iki hata:
1. İlk sürümde hap ikon+metni birlikte sarmalıyor, etiket kelime ortasından
   bölünüyordu (`Kategori / ler`).
2. Sonraki denemede ikonun ÜSTÜNE ince bir gösterge çizgisi konmuştu; çizgi
   ikondan kopuk duruyor, öğeyi cılız gösteriyordu.

Şimdi Material 3'ün kanıtlanmış deseni: aktif ikon **yalnız ikonu saran**
sabit boyutlu (64×34) yuvarlak kapsülün içinde, etiket kapsülün dışında
altta. Kapsül metni hiç sarmaladığı için bölünme yapısal olarak imkânsız;
gösterge de ikondan kopuk değil, onu kucaklıyor. Kapsül aktifken 46→64dp
yayla genişliyor.

### Ana ekran — boşluk sorunu

Eski sürüm fazla kalabalıktı (aurora, konfeti, eğim, kutlama katmanı),
1.0/1.1 ise fazla boştu: ekranda yalnız günün sözü vardı.

Denge: **kahraman söz korunur, derinlik sözün ALTINA inilerek kazanılır.**
Kullanıcı isterse hiç kaydırmadan tek sözle çıkar; isterse aşağı iner.

1. **Kaydırmalı söz akışı** (`HorizontalPager`) — sağa/sola kaydır, yeni söz.
   Bir söz uygulamasında en doğal etkileşim; "Yenile" düğmesine basmak yerine
   akışta gezinmek beklenen davranış. Kenardaki sayfalar soluk, akış hissi
   verir. Bu yüzden ayrı "Yenile" düğmesi kaldırıldı — üç düğme kaldı.
2. **Haftalık şerit** — son 7 günün aktifliği. Seriyi bir sayıdan görülebilir
   bir örüntüye çevirir; boşluk görmek geri dönmeyi tetikler.
3. **Bugün gelenler** — bildirimle gelen ama kaçırılan sözler. Uygulamanın
   çekirdek vaadi bildirim; kaçırılan bildirimin gideceği bir yer olmalıydı.
   Dokununca akışta o söze gidiyor.

---

## 10. Sürüm 1.3.0 — ana ekran tasarım raporu uygulandı

Ana ekran raporundaki **Katman 1'in tamamı** ve Katman 2'den öncelik verdiğim
keşif ipucu eklendi. Her madde "kahraman söz korunur, hiçbir ekleme onun önüne
geçmez" kuralından geçirildi.

| # | Madde | Nerede |
|---|---|---|
| 1.1 | Günün ilerleme halkası | `AnaEkran.GunlukHalka` + `Depo.bugunGorulen` |
| 1.2 | Sıradaki bildirime geri sayım | `Depo.sonrakiBildirim` + `Planlayici.planliSaatleriYaz` |
| 1.3 | Sesli okuma düğmesi | `core/Seslendirme.kt` |
| 1.4 | Haftalık şeride dokunma | `AnaEkran.HaftalikSerit` + `Depo.gunlukGelenler` |
| 1.5 | Zamana duyarlı karşılama | `AnaEkran.Ustluk` |
| 2.2 | Kilitli kategori keşif ipucu | `AnaEkran.KesifIpucu` |

### Tasarım notları

**İlerleme halkası** sayı yazmıyor — dolgunun kendisi anlatıyor. Ortadaki
rakam yalnız hedefe ulaşınca alev ikonuna dönüşüyor. Pasif bir sayacı
"günü tamamlama" hissine çeviriyor.

**Sesli okuma** iki işi birden yapıyor: erişilebilirlik raporundaki (6.1/6.2)
boşluğun bir kısmını kapatıyor ve sabah gözü kapalı dinlemek isteyen kullanıcı
için gerçek bir kullanım sağlıyor. Android'in yerleşik `TextToSpeech`'i
kullanılıyor, yeni bağımlılık yok. Cihazda dil paketi yoksa düğme sessizce
soluklaşıyor — kullanıcıya hata gösterilmiyor. Sayfa değişince okuma duruyor.

**Haftalık şeride dokunma** için `bugunGeldi` artık 1 gün değil 7 gün
saklıyor. Bir güne dokununca o gün gelen sözler açılıyor, sözlere dokununca
akışta o söze gidiliyor.

**Keşif ipucu** günde bir kez, kapatılabilir, akışın EN ALTINDA. Kilit açma
hunisinin görünürlüğü şu ana kadar sıfırdı: Kategoriler sekmesine hiç girmeyen
kullanıcı kilitlerin varlığından haberdar olmuyordu. İpucuna dokununca
doğrudan o kategorinin kilit açma diyaloğu geliyor.

**Zamana duyarlı karşılama** yalnız saatten okunuyor — hiçbir kişisel veri
toplanmıyor, isim sorulmuyor.

### Raporda erteledikleri — hâlâ bekliyor

Katman 2'den: kâğıt dokusu, yazar hakkında bilgi sayfası, haftanın öne çıkanı.
Katman 3'ün tamamı (mikro-günlük, zamana duyarlı söz seçimi, niyet belirleme) —
bunlar ayrı altyapı istiyor, ana ekrana sıkıştırılmamalı.

---

## 11. Sürüm 1.4.0 — bildirim raporu uygulandı

### Uzun cümle politikası DEĞİŞTİ

**Önceki davranış:** 120 karakteri aşan söz bildirim havuzuna hiç girmiyordu.
İki sorunu vardı: (a) Kur'an/İncil ayetleri, uzun felsefe alıntıları gibi
doğası gereği uzun içerikler bildirimde hiç görünmüyordu, (b) bu "sessiz" bir
dışlamaydı — kullanıcı bazı kategorilerin neden az çıktığını anlamıyordu.

**Yeni davranış:** hepsi havuzda. Uzun sözler bildirimde kelime sınırında
kesilip "…" ile bitiyor, `BigTextStyle.summaryText` "Tamamını okumak için
dokun" diyor, dokununca uygulama o sözün TAM metnine açılıyor. Deep-link
altyapısı zaten vardı (`EXTRA_KIMLIK`), yeni bir şey gerekmedi.

Kesilme artık kaza değil **niyet** — haber uygulamalarının "devamını oku"
deseni.

| Sabit | Değer | Anlamı |
|---|---:|---|
| `BILDIRIM_SINIRI` | 120 | Bunun altı olduğu gibi gider |
| `KISALTMA_UZUNLUGU` | 100 | Kısaltılan gövde uzunluğu |
| `HAVUZ_TAVANI` | 400 | Bunun üstü söz değil paragraf — havuza girmez |

Testler de politikaya göre değişti: artık "hiçbir söz uzun olmasın" demiyor,
**"kısaltma her koşulda sınırın altında bir metin üretsin"** diyor. Yeni
testler kısaltmanın kelime ortasından bölmediğini ve kısa sözlerin
değişmeden geçtiğini de doğruluyor.

### Teslimat yardımı — Ayarlar > "Bildirimler gecikmesin"

Bildirimin okunabilirliği ayrı, **teslim edilmesi** ayrı bir problem;
bildirim hiç gelmiyorsa metnin kısalığı önemsiz.

Üç satır eklendi (`notif/TeslimatYardimi.kt`):
1. **Pil optimizasyonu** — muafiyet varsa yeşil tik gösterir, yoksa sistem
   ayarlarına yönlendirir. Kullanıcı ayarlardan dönünce durum tazelenir.
2. **Otomatik başlatma** — yalnız Xiaomi/Oppo/Vivo/Huawei gibi üreticilerde
   görünür. Bilinen üretici ekranlarını dener, hiçbiri açılmazsa uygulama
   ayarlarına düşer.
3. **Sistem bildirim ayarları** — kanal önceliği, kilit ekranı görünürlüğü.

Hiçbiri zorunlu değil, hiçbiri onboarding'i bloklamıyor.

### ⚠️ BİLEREK EKLENMEYEN İZİN

`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` manifest'e **eklenmedi.**

Google'ın resmi dokümantasyonu: Play politikaları, uygulamanın çekirdek
işlevi etkilenmediği sürece Doze/App Standby'dan **doğrudan** muafiyet
istemeyi yasaklıyor. Bir söz uygulaması bu istisnaya girmez — `Planlayici`
zaten ±20 dakikalık kaymayı tolere edecek şekilde tasarlandı, yani "tam
saatinde gelmezse uygulama kırılır" savunması yapılamaz. O izni bildirmek
Play incelemesinde red/askıya alma riski taşır.

Bunun yerine `ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS` kullanılıyor:
kullanıcıyı sistem ayarlarına yönlendirir, izin gerektirmez, politikaya
uygundur. Gerekçe hem `AndroidManifest.xml`'de hem `TeslimatYardimi.kt`'de
yorum olarak duruyor — ileride biri "neden eklemedik" diye sormasın.

### Hâlâ bizim kontrolümüzde OLMAYANLAR

- **Bildirim görünümü.** Bildirim Compose ile değil sistemin `RemoteViews`
  mekanizmasıyla çiziliyor; Kehribar/Deniz/Gül paletleri ve Lora fontu
  bildirime **yansımıyor**. Yazı tipi ve boyutu tamamen cihazın kendi
  bildirim temasına bağlı.
- **Üretici kesme agresifliği.** Samsung One UI stok Android'den daha dar
  alan bırakıyor ve daha erken kesiyor. Karakter sayısını düşürmek dışında
  bir levere sahip değiliz.
- **Android 16 "Bildirim Soğuması".** Aynı uygulamadan kısa sürede art arda
  gelen bildirimlerde sonrakiler kısılıp küçültülüyor. `Planlayici`'deki
  ±20 dk kayma çoğunlukla bunu tetiklemez ama günlük 6-7 bildirim seçen bir
  kullanıcıda iki bildirim şansla yakın düşerse ikincisi sönük gelebilir.
  Şu an özel bir önlem yok.

### Yapılmayan: gerçek cihaz test matrisi

Raporun 5. maddesi: en az bir Samsung (One UI), bir Xiaomi (MIUI), mümkünse
bir stok/Pixel cihazda test. **Emülatörde üretici davranışları hiç
görünmüyor**, bu yüzden bu adım bende yapılamaz — sende yapılması gerekiyor.

---

## 12. Sürüm 1.5.0 — renk sistemi yeniden kuruldu

### Kehribar neden elendi

Sorun altın rengin kendisi değil, vurgunun **doygun ve parlak** olmasıydı.
`#E0A94D` nötr zeminin üstünde bağırıyor; küçük bir alanda bile göz doğrudan
ona gidiyor ve sözün önüne geçiyor. Sıcak kömür zeminle birleşince ekran
tümden "altın temalı" hissettiriyordu.

### Yeni ilke: monokrom omurga + tek kısık vurgu

Üç paletin de **omurgası birebir aynı**: ısısız, hafif soğuk grafit nötr
ölçek. Saf gri değil — saf gri ölü görünür; içinde çok az mavi var, bu yüzden
"ısısız ama cansız değil". Renk yalnızca vurguda var ve vurgu kısık: doygunluğu
düşük, derin tonlar. **Ekranın ~%95'i monokrom, ~%5'i renkli.**

Palet değiştirmek ekranın karakterini değil, yalnızca tek bir aksanı değiştirir.

| Palet | Karanlık accent | Aydınlık accent |
|---|---|---|
| **Bordo** (varsayılan) | `#BE6E80` | `#8A3245` |
| **Lacivert** | `#6688AE` | `#3B5F87` |
| **Yosun** | `#639277` | `#2F6B4F` |

### Kontrast — göz kararı değil, ölçüldü (WCAG 2.1)

Hedef "orta": okunaklı ama bağırmayan.

| Çift | Oran | Not |
|---|---:|---|
| metin / zemin | 11.65:1 | saf beyaz 21:1 olurdu — kasten kısıldı |
| metinIkincil / zemin | 6.66:1 | AA normal metin eşiği 4.5:1 |
| metinSonuk / zemin | 3.78:1 | AA büyük metin / UI eşiği 3:1 |
| accent / zemin | ~5.0:1 | ikon ve metin olarak okunabilir |

Aydınlık tema: metin 13.38:1, ikincil 5.65:1, sönük 3.11:1, accent 5.8–7.3:1.

**`KontrastTest` bu eşikleri koruyor.** Altı test, üç palet × üç mod (karanlık,
aydınlık, OLED) = 9 renk seti üzerinde çalışıyor. Biri değerleri "biraz daha
güzel dursun" diye değiştirir ve okunabilirlik düşerse derleme kırılır.
Üst sınır da var: metin kontrastı 16:1'i aşarsa test kırılır — saf siyah/beyaz
keskinliğine kaymayı engelliyor.

### Ana ekran sol tarafı sadeleşti

Üstlük dört bilgi birden taşıyordu: AZİM + selamlama (aynı satırda), tarih,
"sıradaki söz ~14:20". Sol taraf yığılmıştı.

Şimdi üstlükte yalnız **iki satır**: marka ve tarih. Selamlama ile sıradaki
bildirim tek sessiz satırda birleşip haftalık şeridin üstüne taşındı
(`DurumSatiri`) — orada bağlamı da daha doğru, "günün durumu" bölümünün
girişi oluyor.

### Seslendirme düğmesi geri çekildi

Dördüncü dairesel düğme eylem sırasını kalabalıklaştırıyordu ve TTS hazır
değilken soluk hali kötü duruyordu. Artık:

- Dairesel sırada **üç** düğme var (favori, kopyala, paylaş)
- Seslendirme, sözün altında sessiz bir metin satırı
- **Yalnız cihazda dil paketi varsa görünüyor** — devre dışı bir düğme hiç
  görünmüyor, soluk hal tamamen kalktı

---

## 13. Sürüm 1.6.0 — bordo derinleşti, takımyıldız, onboarding canlandı

### Bordo: sorun parlaklıkta değil TONDAYDI

"Pembeye kaçıyor" şikâyetinin sayısal karşılığını çıkardım. Doğrudan
koyulaştırma **çalışmıyor** — accent yalnız dekoratif değil, aktif nav ikonu
ve favori kalbi olarak taşıyıcı eleman:

| Renk | Parlaklık | Kontrast | Durum |
|---|---:|---:|---|
| `#B05F72` | 53% | 4.16:1 | eşik altı |
| `#A85567` | 50% | 3.66:1 | okunmaz |
| `#8E3348` | 38% | 2.39:1 | tamamen okunmaz |

Asıl sorun **ton**: `#BE6E80` → 346°, pembe/gül tarafı. Şarap 336–342°'de
başlıyor. Tonu 339°'ye çekince parlaklık korunuyor, pembelik gidiyor:

**`#BB6885`** — ton 339°, kontrast 4.78:1 ✓

### accent ikiye bölündü

Tek accent değeri hem okunması gereken (ikon, metin) hem gerekmeyen (dolgu,
çizgi, halka zemini) yerlerde kullanılıyordu; en zayıf halka olan okunabilirlik
tüm sistemi rehin alıp accent'in koyulaşmasını engelliyordu.

Yeni token: **`accentDerin`** — kontrast şartına tabi değil, gerçekten koyu
şarap olabilir (`#421A28`). Böylece geniş yüzeyler belirgin koyulaşırken küçük
öğeler okunur kalıyor.

Aynı bölme üç palete de uygulandı (Lacivert `#1E3047`, Yosun `#1B2E23`).

### Takımyıldız — 120 günlük yolculuk

Uygulamanın açıldığı her gün bir yıldız. 120 günde takımyıldız tamamlanıyor,
sonra yeni tur başlıyor.

**Seriden farkı: kırılmaz.** Bir gün kaçırmak yıldız SİLMEZ, sadece yeni yıldız
eklenmez. Ceza değil birikim — "azim" tam olarak bu.

Görsel tasarım kararları (öncelik buradaydı):

- **Jittered grid yerleşim** — saf rastgelelik kümelenme ve boşluk yapar,
  düzgün ızgara yapay durur. Tuval 12×10 hücreye bölünüp her yıldız kendi
  hücresinde rastgele bir noktaya konuyor: hem dengeli hem organik.
- **Sabit tohum** — aynı yıldız her açılışta AYNI yerde. Kullanıcı kendi
  takımyıldızını tanıyor; her açılışta karılsa hiç aidiyet oluşmazdı.
  Her tur farklı desen üretiyor.
- **Üç katmanlı derinlik** — arkada sönük toz zerreleri, ortada yolu çizen
  çizgiler, önde yıldızlar.
- **Zaman görsel olarak okunuyor** — eski yıldızlar sönük ve küçük, yeniler
  parlak ve büyük. Yol çizgileri de sona doğru belirginleşiyor.
- **Tek hareket noktası** — yalnız EN SON yıldız nefes alıyor (2.6 sn).
  Eski sürümün aurora/konfeti hatasını tekrarlamamak için ekran titremiyor.
- **Uzun sıçrama yok** — tuval genişliğinin %42'sini aşan bağlantılar
  çizilmiyor, yoksa çirkin uzun çizgiler oluşuyordu.

`TakimyildizTest` (9 test) sınır durumlarını koruyor — en kritiği: tam 120.
günde takımyıldız **dolu** görünmeli, modülo tek başına 0 verirdi.

### Onboarding — "sert" hissi kalktı

Teşhis: önceki hali tamamen statikti. Metin anında beliriyor, kategoriler bir
anda doluyordu — hiçbir şey *gelmiyor*, her şey zaten *orada*.

- **Kademeli giriş** (`Kademeli`) — başlık, alt başlık, açıklama 160–340 ms
  arayla soluklaşarak ve hafifçe yukarı kayarak geliyor
- **Kategori kartları dalga halinde** — 55 ms arayla yukarıdan aşağı oturuyor
- **Seçim tiki yaydan çıkıyor** — `dampingRatio = 0.42`, seçim anı hissediliyor
- **İlerleme göstergesi akıyor** — aktif nokta zıplayarak değil, yayla uzayarak

### Rapordan uygulanmayanlar

Marka logosunun kendini çizmesi (2.1), sıklık ekranında canlı gün önizlemesi
(2.4), kapanışta ilk sözün doğuşu (2.6), onboarding zemin gradyanı (2.7) —
hepsi duruyor. Kâğıt dokusu, yazar bilgi sayfası ve kaydırma paralaksı da
bekliyor.

---

## 14. Sürüm 1.7.0 — "boğuk ve hissiz" teşhisi

Ekran görüntüsü üzerinden bakınca sorun renk seçiminde değil, **yüzey
ayrışmasında** çıktı.

### Kök neden: yüzeyler birbirinden ayrışmıyordu

`yuzey #1A1D20` ile `zemin #121416` arasındaki kontrast **1.09:1** idi —
yani neredeyse aynı renk. Kartlar ve düğmeler yüzey tonundan değil, YALNIZ
sert 1dp kenarlıktan okunuyordu. "Boğuk" hissi de "sert kenar" şikâyeti de
aynı kökten geliyordu: ayrım tek bir keskin çizgiye yüklenmişti.

Düzeltmeler:

| Ne | Önce | Sonra |
|---|---|---|
| yuzey | `#1A1D20` (1.09:1) | `#1E2227` (1.15:1) |
| yuzeyYuksek | `#24282B` | `#2A3037` |
| kenarlik | `#2F3438` | `#333940` |

Yüzeyi daha fazla açmak mümkün değildi — `KontrastTest`'in metin/yüzey (≥9:1)
ve accent/yüzey (≥4:1) eşikleri tavan koyuyor. Gerisi aşağıdaki iki değişiklikle
çözüldü.

### Gradyan zemin — düz siyah tuval bırakıldı

Referans uygulamaların canlı hissetmesinin sebebi arka planın hep hafif bir
derinlik taşıması. İki kısık katman eklendi (`zeminFircasi`, `accentIzi`):
üstten aşağı açılan çok hafif dikey gradyan ve en üstte accent'in neredeyse
görünmez izi (%5.5 alfa).

**Eski "aurora" hatasından farkı: bu HAREKETSİZ.** Animasyon yok, dikkat
dağıtmıyor, yalnız derinlik veriyor.

### Sert kenarlıklar kaldırıldı

- **Eylem düğmeleri** — 1dp halka gitti, 56→58dp, dolgu `yuzeyYuksek`,
  favorideyken `accentDerin`. Ayrım artık çizgiden değil yüzeyden geliyor.
- **Haftalık şerit** — boş günler sert kenarlıklı kutu değil; 7 ölü kutu
  ekranı ağırlaştırıyordu. Dolu gün `accentDerin` dolgulu ve alevli,
  boş gün sessiz bir nokta.
- **Bugün gelenler / keşif ipucu** — kenarlık yerine yüzey dolgusu.

### Dikey boşluklar sıkıldı

Söz ile sayfa göstergesi arasında ve "Sesli oku" ile durum satırı arasında
ölü alanlar vardı. Pager minimum yüksekliği 280→210dp; aradaki boşluklar
`x4/xl` → `xxl/lg` seviyesine indi.

### Gökyüzü artık ilk günden var

Önceden yıldız yokken kart boş kalıp "İlk yıldızın yarın doğacak" yazıyordu.
Boş bir kutu "henüz bir şey yok" der; gökyüzü "yolculuk burada başlıyor" der.

`gokyuzunuUret()` — kullanıcının ilerlemesinden **bağımsız**, sabit tohumlu,
96 uzak yıldız, üç derinlik katmanında:

| Katman | Adet | Yarıçap | Alfa |
|---|---:|---:|---:|
| çok uzak | 60 | 0.55 | 0.07 |
| orta | 26 | 0.85 | 0.14 |
| yakın | 10 | 1.15 | 0.22 |

Tek parlaklıkta hepsi düz bir doku gibi dururdu; katmanlar derinlik veriyor.
Kazanılmış accent yıldızlar bunun üstünde parlıyor. Üç yeni test gökyüzünün
her zaman dolu, sabit ve üç katmanlı olduğunu koruyor.

---

## 15. Sürüm 2.0.0 — FAZ 1 tamamlandı

Sürüm numarası 2.0'a çıktı çünkü kategori mimarisi kırıcı bir değişiklik.

### Kaldırılanlar

| Ne | Neden |
|---|---|
| Takımyıldız / yolculuk | Ana ekranın sorunu boşluk değil işlevsizlikti; dekorasyon çözmedi |
| Pil optimizasyonu bölümü | İstendiği gibi tamamen kaldırıldı (§ altta not) |
| Otomatik başlatma yönlendirmesi | Aynı bölümdeydi |
| Veri içe/dışa aktarma | `org.json` bağımlılığı da böylece bitti |

> **Not:** Pil optimizasyonu bildirim gecikmesinin Samsung/Xiaomi'deki bir
> numaralı sebebidir. Tamamen kaldırıldı; bu şikâyet gelirse elimizde bir
> çözüm yok. Planda önerdiğim "yalnız sorun tespit edilirse göster"
> uzlaşması hâlâ masada.

### Kategori mimarisi — 11 grup, 60 alt kategori

Düz 35 kategori listesi bitti. Yeni yapı iki seviyeli: grup → alt kategori.

**Kilit GRUP seviyesinde.** 60 alt kategoriyi tek tek reklamla açtırmak
işkence olurdu. Bir reklam = o grubun tamamı, ömür boyu. Kullanıcı için
adil, teklif olarak net.

Ücretsiz gruplar: **Azim & Dayanıklılık** (9 başlık), **Disiplin & Odak**
(5 başlık). Kalan 9 grup reklamla açılıyor.

Kategoriler ekranı gruplar hâlinde; gruba dokununca altları genişliyor.

**Göç:** eski anahtarlar yeni yapıya eşleniyor (`sabir → zorluk_sabir`,
`stoacilik → epiktetos`, `odak → derin_odak` …). Eşleşmeyen sessizce düşüyor,
`secili` boşalırsa varsayılana dönülüyor. Test bunu koruyor.

**İçerik:** ücretsiz grupların boş alt başlıkları dolduruldu — 30 söz eklendi
(erteleme, derin odak, dürtü, rutin, dikkat dağınıklığı, yorulmaya karşı
sabır, yeniden başlamak, uzun soluklu hedefler). Toplam 54 söz.
Yeni test ücretsiz grupların en az 10 söz taşımasını zorunlu kılıyor.

### Bildirim planı — tek bileşen, iki yerde

`ui/BildirimPlani.kt` hem ayarlarda hem onboarding'de kullanılıyor:

- Adet (1–7)
- Saat aralığı — dört ayrı −/+ düğmesi yerine **çift uçlu kaydırıcı**
- **Canlı önizleme** — gün çizgisi üzerinde, `Planlayici` ile aynı dağıtım
  mantığıyla yerleştirilmiş noktalar. Soyut sayı yerine "günüm böyle görünecek".
- **Havuz dengesi** — `4 kategori · 62 söz · günde 5 bildirim → 12 gün tekrarsız`

Havuz 7 günden azsa satır accent tonuna geçip Kategoriler'e götürüyor.
Kullanıcının seçimini sessizce değiştirmiyoruz; sonucu görünür kılıyoruz.

**Varsayılan saat aralığı 9–22 → 10–23.**

### ⭐ Samsung "Ayrıntılı açılır pencere" yönlendirmesi

Gönderdiğin ekran görüntüsündeki bulgu. One UI'ın varsayılanı "Kısa açılır
pencere" ve metni erken kırpıyor; uzun cümlelerde yaşadığımız kırpılmanın
büyük kısmı buradan geliyor.

Ayarlarda ve onboarding'in bildirim adımında yönlendirme var. Ayarlardaki
blok **iki küçük mockup** gösteriyor — solda kırpılmış "Kısa", sağda tam
"Ayrıntılı". Metinle anlatmaktan çok daha anlaşılır.

Yalnız Samsung cihazlarda görünüyor (`Build.MANUFACTURER`); diğer markalarda
bu ayar yok, göstermek kafa karıştırırdı.

> Bu ayar programatik değiştirilemiyor — Android API vermiyor. Yapabildiğimiz
> en iyi şey doğru sayfaya götürüp ne yapılacağını göstermek.

### Ana ekran — dekorasyon yerine işlev

Yeni sıralama:

```
1. Üstlük        AZİM · tarih · günlük halka · ayarlar
2. KAHRAMAN SÖZ  kaydırmalı akış
3. Eylemler      favori · kopyala · paylaş  +  sesli oku
4. BUGÜN         selamlama · sıradaki söz · gelen/planlanan çubukları
5. KEŞİF         koşullu kategori önerisi
6. Bugün gelenler
7. BU HAFTA      haftalık şerit — EN ALTA alındı
```

**BUGÜN kartı** kaldırılan takımyıldızın yerini alıyor: uygulamanın çekirdek
vaadi bildirim, artık durumu ana ekranda görünüyor. Planlanan bildirim sayısı
kadar çubuk; gelenler accent, bekleyenler nötr.

### Keşif önerisi — koşullu, tek kalıp değil

`ui/KesifOnerisi.kt`. Tek cümle hep tekrarlanınca kör noktaya döner.
Sırayla değerlendirilen dört koşul:

| Koşul | Mesaj |
|---|---|
| Havuz küçük | "Bildirimlerinde 3 kategori var — X ile genişlet" |
| Favoriler tek grupta yoğun | "Favorilerini seviyorsan X da hoşuna gidebilir" |
| Saate uygun kilitli grup | "Bu saatlere uygun: X" |
| Hiçbiri | "X grubunu denemek ister misin?" |

Kart değil tek satır, günde bir kez, kapatılabilir. Dokununca doğrudan o
grubun kilit açma diyaloğu.

### Onboarding

- Adım 3 artık "sıklık" değil **bildirim planı**: adet + saat aralığı +
  canlı önizleme, tek ekranda. Adım sayısı 3'te kaldı.
- Samsung kullanıcısına açılır pencere yönlendirmesi ilk günden geçiyor
- 1.6.0'daki kademeli giriş, dalga hâlinde kartlar, yaylı tik ve akan
  gösterge korundu

### Faz 1'de yapılmayanlar

Plandaki §3.5'ten: logo çizim animasyonu, kapanış geçişi (ilk sözün doğuşu),
onboarding zemin gradyanı. Bunlar sırada.

---

## 16. Sürüm 2.1.0 — FAZ 2: Paylaşım Stüdyosu

Paylaşım artık tek sabit tasarım değil, ayrı bir stüdyo ekranı.

### Mimari kararı: tek çizici

`paylas/KartCizici.kt` — önizleme, PNG çıktısı ve video kareleri **aynı
fonksiyondan** çiziliyor. Tüm ölçüler tuval genişliğine oranlı, bu yüzden
620px önizleme ile 1080px çıktı birebir aynı görünüyor.

Bu bilinçli: eski sürümde widget'ta "önizleme başka, gerçek başka" hatası
vardı (üç ayrı çizim yolu). Burada ayrışma yapısal olarak imkânsız.

### Zemin

- **8 düz renk** — uygulamanın kendi paletinden türetildi, paylaşılan kart
  markayla aynı dili konuşsun
- **6 gradyan** — şarap, lacivert, yosun, kâğıt
- **Fotoğraf** — Android Photo Picker (`PickVisualMedia`), **izin gerekmiyor**.
  Merkeze kırpılıyor, üstüne ayarlanabilir karartma katmanı geliyor.

Serbest renk seçici **kasten yok**. Eski sürümün kart stili × tema × font ×
renk × foto × boyut kombinasyon patlamasını tekrarlamamak için her eksen
küratörlü.

### Yazı

Lora (uygulamada zaten var, SIL OFL) + Android'in yerleşik Serif, Sans,
Monospace aileleri. **Yeni font paketlenmedi** — APK'yı şişirir ve lisans
denetimi gerektirir. İleride eklenecekse OFL veya Apache lisanslı olmalı.

Boyut (0.7×–1.4×) ve hizalama (orta / sol) ayarlanabilir.

### Format

Kare 1080×1080 · Story 1080×1920 · Yatay 1920×1080

### Video (süre seçilebilir)

**FFmpeg kullanılmadı.** Android'in kendi donanım kodlayıcısı `MediaCodec` ve
kapsayıcısı `MediaMuxer` cihazda zaten var, H.264 için lisanslı, ek indirme
gerektirmiyor. FFmpeg ikilisi taşımak APK'yı onlarca MB şişirir ve lisans
riski doğurur.

**Neden OpenGL gerekti:** `MediaCodec.createInputSurface()` ile dönen yüzey
Canvas ile çizilemez — GL için yapılandırılmıştır. Bu yüzden `EglOrtam.kt`
minimal bir EGL bağlamı kuruyor ve her kareyi tam ekran dokulu dörtgen
olarak basıyor.

Akış: `KartCizici` → Bitmap → GL dokusu → encoder yüzeyi → MP4.

| Ayar | Değer |
|---|---|
| Süre | 5 / 10 / 15 sn |
| Kare hızı | 30 fps |
| Çözünürlük | 720 taban (1080p düşük segmentte kodlamayı düşürebiliyor) |
| Animasyon | kelime kelime beliriş (ilk %45) + Ken Burns yakınlaşma |

Üretim sırasında paylaş düğmesi yüzde gösteriyor.

### ⚠️ Video cihaz testi gerektiriyor

Kodlayıcı davranışı üreticiye göre değişir ve **emülatörde doğrulanamaz.**
Bu yüzden hata yolu bilinçli tasarlandı: üretim başarısız olursa
`VideoUretici` sessizce `null` döner ve uygulama **otomatik olarak görsel
paylaşıma düşer**. Kullanıcı hiçbir zaman kırık bir çıktıyla karşılaşmaz,
hata mesajı da görmez.

Gerçek cihazda doğrulanması gerekenler:
- Üç formatta da video üretimi
- Düşük segment cihazda 15 sn'lik üretim süresi
- Fotoğraf zeminli videoda bellek davranışı

### Faz 2'de yapılmayanlar

Plandaki §6.5'e sadık kalındı: görsel stüdyo tamamlandı, video eklendi ama
saha testi bekliyor. Ek font paketi ve daha fazla gradyan sonraki tur.

---

## 17. Sürüm 2.2.0 — Faz 1'in kalan animasyonları

Plandaki §3.5'ten bekleyen üç madde tamamlandı (`ui/OnboardingSahne.kt`).

### Logo kendini çiziyor

Karşılama ekranındaki statik "AZİM" yazısı gitti. Yerine:

1. Harflerin **konturu** soldan sağa çiziliyor (~1.1 sn)
2. Çizim bitince **dolgu** beliriyor
3. Altındaki **accent çizgi** genişliyor

**Teknik:** metin `Paint.getTextPath()` ile bir `Path`'e dönüştürülüyor,
sonra `DashPathEffect` ile kısmi gösteriliyor — kesik çizgi aralığı
ilerlemeye göre büyüdükçe kontur "çiziliyor" izlenimi doğuyor. Toplam yol
uzunluğu `PathMeasure` ile tüm konturlar dolaşılarak hesaplanıyor.

### Onboarding zemin hareketi

Çok yavaş kayan radyal gradyan (14 sn'lik döngü, %7.5 alfa accent).

**Yalnız onboarding'de.** Eski sürümün "aurora" hatası bunun ana ekranda
sürekli açık durmasıydı; burada yalnız ilk kurulum ekranlarında, birkaç
dakikalığına. Ana ekranın gradyanı hâlâ hareketsiz.

### Kapanış sahnesi — ilk sözün doğuşu

"Hazırım" artık doğrudan ana ekrana atlamıyor. Sırayla:

1. Ekran sözün zeminine geçer
2. İlk söz **kelime kelime** belirir (115 ms aralıkla)
3. Yazar soluklaşarak gelir
4. 1.1 sn sonra ana ekran devralır

Uygulamanın vaadi ilk saniyede teslim ediliyor. Gösterilen söz kullanıcının
onboarding'de seçtiği kategorilerden geliyor — ilk deneyim bile kişisel.

### Faz 1 ve Faz 2 durumu

Her iki fazın da planda yazılı maddeleri tamamlandı. Bekleyenler:

- **Video saha testi** (§16) — emülatörde doğrulanamaz, gerçek cihaz şart
- **Pil optimizasyonu uzlaşması** — "yalnız sorun tespit edilirse göster"
  önerisi hâlâ masada
- **Ek font paketi ve gradyanlar** — paylaşım stüdyosu için
- **R8 açık derleme** — teslim edilen APK'lar `-PazimMinify=false` ile
  üretiliyor (konteyner sınırı); yayın derlemesi senin makinende alınmalı
