# Ascend 9.7.1 — Tema ve onboarding

Son kullanıcı kararı: **Beyaz ve Siyah ücretsiz; Roma ve Koyu Atlı Yolcu Pro.** Önceki prompttaki ücretsiz Atlı Yolcu kararı geçersizdir.

## Uygulanan akış

1. Türkçe / English dil seçimi.
2. Kaydırma ve kaydetme etkileşimleri olan kısa tanıtım.
3. Günlük bildirim sayısı ve saat aralığı aynı sayfada.
4. Genişletilebilir bildirim örneği, izin ve cihaz ayarlarına yardım bağlantısı.
5. Üstte Beyaz/Siyah, altta Roma/Koyu Atlı Yolcu Pro önizlemeleri.

Bildirim izni olmadan bitirme düğmesi yok. Android izin reddi korunur; kalıcı ret halinde sistem ayarları açılır. Tema önizlemek seçim veya ödeme oluşturmaz; seçim onaylandığında kaydedilir.

## Tema ve Keşfet

Tema görseli yalnızca ana ekranda; diğer ekranlarda uyumlu nötr açık/koyu renkler. Keşfet büyük tanıtım başlığından ve Roma dekorlarından arındırıldı. Konular / Özelleştir ayrımı eklendi. Paylaşımın mevcut görsel kataloğu Pro ana ekran temalarına da açıldı; paylaşım hakları değiştirilmedi. Pro demo kapanınca kilitlenen ana ekran teması uygun ücretsiz Beyaz/Siyah temaya döner.

## Widget

Tüm çalışan widget özellikleri Pro; önizleme ücretsiz. Sade ve görselli arka planlar, hizalama, yazı büyüklüğü, geniş/kare boyut önizlemeleri. Düzenleyici ile gerçek widget aynı çizim yolunu kullanır. Her widget ayarı ayrı saklanır; Android yapılandırma, yeniden boyutlandırma ve uygulamadan ekleme desteklenir. Ekleme penceresi iptal edilince başarı iddia edilmez. Desteksiz başlatıcıda elle ekleme anlatılır. Kart dokunuşu görünen sözü açar. Pro kapanınca mevcut widget bilgilendirme görünümüne geçer.

Pro hâlâ yerel test demosudur. Gerçek Play Billing ve ödeme yoktur.

## Doğrulama sınırı

GitHub üzerinde kaynak/varlık denetimleri, JVM testleri, lint, Android test kaynaklarının derlenmesi ve APK üretimi çalıştırılır. Son kullanıcı yönlendirmesi gereği son APK sonrasında uzun emülatör/görsel kontrol turu yapılmaz. Fiziksel cihazdaki launcher, widget ekleme ve bildirim görünümü kullanıcı tarafından denenmelidir.

Android widget davranışı için başvurulan resmi belgeler:
- https://developer.android.com/develop/ui/views/appwidgets/discoverability
- https://developer.android.com/reference/android/appwidget/AppWidgetManager
- https://developer.android.com/develop/ui/compose/glance/user-interaction
