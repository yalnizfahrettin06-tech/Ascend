# Ascend

Seçtiğin konulardan, belirlediğin saatlerde kısa ve özgün sözler gönderen günlük motivasyon uygulaması. Android · Kotlin · Jetpack Compose · Material 3.

## 10.1 — Faz 2: derinlik ve cila

- **Gerçek alıntılar:** Marcus Aurelius, Epiktetos, Seneca, Thoreau ve Emerson'dan 46 alıntı, eser ve çevirmen künyesiyle. Kaynak kamu malı çevirilerdir ve her CI çalışması orijinal metinle birebir karşılaştırır ([content/classics.json](content/classics.json)).
- **Daha derin içerik:** 490 yeni özgün söz. Her ücretsiz konuda en az 30 metin var.
- **Yeni widget:** Gerçek metin (TalkBack ve sistem yazı boyutu uyumlu), "Sonraki" düğmesi. Sade arka planlar ücretsiz.
- **7 dil:** Türkçe, İngilizce, Almanca, Fransızca, İtalyanca, Portekizce, Rusça. Henüz çevrilmemiş yeni metinler o dillerde gösterilmez.
- **Pro:** Demo yalnızca test derlemesinde. Mağaza derlemesinde "Çok yakında" görünür.

## 10.0 — Faz 1: sağlam çekirdek ve yeni deneyim

- **Bildirimler seçtiğin konulardan gelir.** Yaklaşık her yedi bildirimden biri, istersen kapatabileceğin bir "sürpriz"dir. Sürprizler yalnızca açık konulardan gelir; inanç/tasavvuf ve hassas konular (ayrılık, veda...) sen seçmedikçe gelmez.
- **Güvenilir teslimat:** AlarmManager ile tek bekleyen hatırlatma. Yeniden başlatma, güncelleme, saat dilimi ve dil değişiminde yeniden kurulur. Yaz saati geçişinde saatler kaymaz. Saatlerce geciken bildirim bağlamsız gelmek yerine atlanır.
- **Bildirimden:** "Kaydet" yalnızca ekler, söz zaten kayıtlıysa düğme görünmez. "Bugünlük yeter" o günün kalan bildirimlerini durdurur.
- **Yeni kurulum (5 adım):** Dil → Konular (1–5) → Ritim → İzin (*Şimdilik geç* seçeneği var) → Özet.
- **Dört sekme:** Bugün · Keşfet · Bildirimler · Senin, ayrıca Ayarlar (dil, sistem/açık/koyu tema, arka plan/widget/duvar kâğıdı, titreşim, Pro demo, gizlenen sözler).
- **Ücretsiz kütüphane** 6 konudan 26 konuya çıktı. Pro demosu ödeme almaz.
- **Performans:** Görseller 40 MB'tan 7,9 MB'a indi, kullanılmayan 72 görsel kaldırıldı. Widget her kaydırmada yeniden çizilmiyor. Açılışta tema ve dil yüklenene kadar splash ekranı kalıyor.
- **Diller:** Türkçe ve İngilizce (Faz 2'de 7 dile çıktı).

Ayrıntılı analiz, alınan kararlar ve iki fazlı yol haritası: [docs/ANALIZ_VE_ILERLEME_RAPORU.md](docs/ANALIZ_VE_ILERLEME_RAPORU.md)

## Mimari

| Katman | Dosyalar |
|---|---|
| Model ve kalıcılık | `data/UserState.kt`, `data/AscendStore.kt` (tek DataStore, atomik güncelleme) |
| Saf kurallar | `data/UserActions.kt`, `data/Policies.kt` (`ReminderPlan`, `QuotePicker`, `HomeFeed`), `data/Access.kt` |
| Bildirim | `notif/Notifier.kt`, `notif/ReminderScheduler.kt` (alarm, teslimat, sistem olayları, bildirim eylemleri) |
| Arayüz | `ui/AppViewModel.kt`, `ui/AscendApp.kt`, `ui/*Screen.kt`, `ui/Components.kt` |
| İçerik | `content/source.en.json` → `tools/icerik_derle.py` → `data/IcerikVerisi.kt` |
| Metinler | `tools/strings_source.py` → `res/values{,-tr}/strings.xml` |

## Geliştirme

JDK 17 ve Android SDK 35:

```sh
python tools/icerik_derle.py --check
python tools/klasik_derle.py --check --verify   # alıntıları kaynakla karşılaştırır
python tools/strings_source.py      # 7 dilin strings.xml dosyalarını üretir
python tools/check_assets.py        # görsel bütçesi ve referanslar
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
./gradlew :app:connectedDebugAndroidTest
```

Test APK'sı her çalışmada GitHub Actions'ta **Artifacts → Ascend-test-APK** olarak üretilir. Bu bir geliştirme imzalı test sürümüdür. 10.0, veri modelini sıfırdan kurduğu için önceki test sürümlerinin yerel verilerini taşımaz.

Sürüm imzası için `ASCEND_KEYSTORE_PATH`, `ASCEND_STORE_PASSWORD`, `ASCEND_KEY_ALIAS` ve `ASCEND_KEY_PASSWORD` kullanılır.

## İçerik ilkesi

Ascend düşünceleri özgün metinlerdir. Düşünür ve inanç koleksiyonlarındaki özgün metinler bu kişilerden ya da geleneklerden esinlenir. Gerçek alıntılar yalnızca kamu malı çevirilerden birebir alınır, yazar, eser ve çevirmen künyesiyle gösterilir. Türkçeleri Ascend'e aittir.

Yeni içerik `content/additions/*.txt` dosyalarına `konu | English | Türkçe` biçiminde eklenir; `python tools/add_content.py` ve ardından `python tools/icerik_derle.py` ile kataloğa girer. Gerçek alıntılar için `python tools/klasik_derle.py --verify` kullanılır.

Önceki sürümlerin tarihsel kayıtları [docs/archive](docs/archive/README.md) altındadır.
