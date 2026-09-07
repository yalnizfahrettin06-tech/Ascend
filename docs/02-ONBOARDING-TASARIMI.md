# Ascend — Onboarding ve günlük deneyim tasarımı

## 1. Tasarım hedefi

Kullanıcı ilk dakikada üç şeyi anlamalı: Ascend ne sunuyor, içerik kendisine nasıl uyarlanıyor ve bildirim kontrolü kimde. Hedef süre bir tasarım hedefidir, ölçülmüş sonuç değildir. Kurulumda hesap, ödeme, doğum tarihi, uzun anket, widget ayarı veya zorunlu bildirim bulunmaz.

Akış: **ilk olumlama → ihtiyaç seçimi → isteğe bağlı hatırlatıcı → Bugün**. Olumlamaya ulaşmak için üç adımın tamamlanması gerekmez; ilk örnek karşılama ekranındadır.

## 2. Ekran 1 — Değeri deneyimle

| Alan | İçerik ve davranış |
|---|---|
| Marka | Ascend; sabit, hemen okunur |
| Konum | 1 / 3 adım ve ilerleme çizgisi |
| Üst etiket | Kendine ayırdığın küçük bir an |
| Başlık | Kendine iyi gelen bir başlangıç. |
| Açıklama | Günlük olumlamalarla dur, nefes al ve kendine nazikçe hatırlat. |
| Olumlama | Bugün kendime daha nazik davranmayı seçiyorum. |
| Kullanım ipucu | Bir nefes al. Bu cümleyi içinden yavaşça tekrar et. |
| Güven metni | Hesap açman gerekmiyor. Başlangıç konuları ücretsiz; bildirimler senin seçimin. |
| Birincil eylem | Bana göre düzenle |

Yerleşim: üstte marka/ilerleme, ortada kaydırılabilir metin ve büyük kart, altta sabit eylem. Örnek içerik rastgele değişmez; ekran döndürülmesi veya yeniden çizilmesi ilk metni değiştirmez. Metin animasyon beklemeden görünür. Sistem geri bu ilk adımda standart uygulamadan çıkış davranışını korur.

## 3. Ekran 2 — İhtiyaca göre kişiselleştir

Başlık: **Bugün neye yer açalım?** Açıklama: **Sana yakın gelen konuları seç. Daha sonra değiştirebilirsin.**

| Seçenek | Kaynak anahtarı | Durum |
|---|---|---|
| Kendime nazik davranmak | `ozsefkat` | Ücretsiz, başlangıçta seçili |
| Biraz yavaşlamak | `ic_huzur` | Ücretsiz, başlangıçta seçili |
| Kendime güvenmek | `kendine_guven` | Ücretsiz |
| Yeniden başlamak | `yeniden` | Ücretsiz |
| Derin odak | `derin_odak` | Ücretsiz |
| Umut | `umut` | Ücretsiz |

Kartlar tek sütundur. Bu tercih uzun Türkçe/İngilizce etiketleri dar bir iki sütuna sıkıştırmaz. Her satır en az 64dp yüksekliktedir ve gerekirse büyür. Kartın tamamı checkbox rolünde seçilebilir; iç checkbox aynı eylemi iki defa çalıştırmaz. Seçim, renk dışında onay işareti ve erişilebilir açık/kapalı durumu taşır.

Kullanıcı varsayılanların tamamını kaldırabilir. Son öğeyi kaldırmayı sessizce engellemek yerine sıfır seçim durumu açıklanır ve Devam devre dışı kalır. Yeniden bir öğe seçildiğinde Devam açılır. Geri dönüp tekrar ilerlemek seçimi değiştirmez. Desteklenmeyen/kilitli anahtarlar kayıt aşamasında başlangıç kurallarıyla süzülür.

## 4. Ekran 3 — Kullanıcının ritmi

Başlık: **Senin günün. Senin ritmin.** Açıklama: günde bir nazik hatırlatma ile başlayabileceği ve sıklığı değiştirebileceği belirtilir.

Başlangıç önerisi günde 1, 09.00–21.00. Sıklık kontrolü 1–7 seçeneklidir; 48dp hedefler dar ekranda ikinci satıra sarılır. Saat aralığı çift uçlu kaydırıcıdır ve yazıyla da gösterilir. Görsel çizgi yaklaşık dağılımı açıklar; kesin saat garantisi verilmez.

İki eylem:

1. **Hatırlatıcıları aç ve başla:** tercihler tek işlemde kaydolur; kurulum tamamlanır; gerekiyorsa Android izin penceresi açılır. İşletim sistemi reddederse uygulama kullanılabilir kalır ve durum ana ekran/ayarlarda görünür.
2. **Şimdilik bildirimsiz devam et:** aynı seçimler kaydolur; hatırlatıcı tercihi kapalıdır; izin penceresi açılmaz. Sonradan Ayarlar’dan etkinleştirilebilir.

İkinci eylem soluk, küçük veya içerik altında gizli bir link değildir; alt eylem alanında en az 48dp yüksekliğinde metin düğmesidir. Kayıt sürerken iki eylem de kapanır. Dosya yazma hatası olursa açıklama ve yeniden deneme mümkün kalır.

## 5. Durum tablosu

| Durum | Görünen deneyim | Kalıcı etki |
|---|---|---|
| Başlangıç verisi okunuyor | Nötr yükleme durumu | Henüz yönlendirme yok |
| İlk kurulum | Karşılama | Tamamlandı işareti yok |
| Konu seçimi sıfır | Açıklama + pasif Devam | Geçersiz kayıt yok |
| Kayıt sürüyor | Hazırlanıyor; tekrar tıklama engellenir | Tek DataStore işlemi |
| Kayıt hatası | Tekrar denemeye izin veren mesaj | Tamamlandı işareti yazılmamış kalır |
| Bildirimsiz tamamlandı | Bugün; hatırlatıcılar kapalı kartı | Tercih kapalı |
| İzin istendi ve verildi | Bugün; yaklaşık plan bilgisi | Tercih açık, sistem izni açık |
| İzin reddedildi/kapatıldı | Okuma çalışır; izin durumunu açıklayan kart | Kullanıcı tercihi ile sistem izni ayrı |
| Sistem ayarlarından izin açıldı | Uygulamaya dönünce izin tazelenir | Plan yeniden değerlendirilir |
| Tekrar açılış | Tamamlanmışsa Bugün | Onboarding tekrar zorlanmaz |

`rememberSaveable` adım/seçimler/saatler için Android’in kayıtlı durum mekanizmasını kullanır. Kullanıcının uygulama verisini silmesi veya zorla durdurma sonrası işletim sisteminin görev durumunu atması, tamamlanmamış sihirbazın kesin korunacağı anlamına gelmez. Tamamlanmış kurulum DataStore’da kalıcıdır.

## 6. İlk kullanım sonrası devamlılık

Bugün ekranı kullanıcının seçtiği konulardan bir akış açar. Varsayılan iki konu yalnız özgün olumlamalar içerir. Yeniden başlamak, odak ve umut gibi eski konularda olumlamalarla önceki alıntılar birlikte bulunabilir; tüm eski içerik yeniden yazılmamıştır.

Bir olumlama kartı, sıra bilgisi ve açık gezinme kontrolleri bulunur. Kaydet durumu yazıyla ve ikonla belirtilir. Paylaş mevcut stüdyoya geçer; geri yeniden okuma ekranına döner. Kopyalama kısa geri bildirim verir. Cihazdaki konuşma motoru destekliyorsa sesli okuma görünür. Sesli okuma olanağının cihaz/dil paketi bağımlılığı devam eder.

Hatırlatıcı kartı dört durumu ayırır: kullanıcı kapattı, sistem izni kapalı, sıradaki yaklaşık saat var, plan açık fakat bugün için gelecek saat yok. “Bildirim gelmedi” durumunda kullanıcıya tamamlanmış bir teslimat gösterilmez. Kaydedilenler ve Keşfet ayrı alt navigasyon hedefleridir; seri sayısı olumlama kartının önüne geçmez.

## 7. Erişilebilirlik ve uyarlama kabul kriterleri

- Onboarding alt eylemleri normal ve 2× yazı ölçeğinde ulaşılabilir olmalıdır.
- İçerik büyüyebilir; sabit 56dp metin kartlarına veya tek satır sığdırmaya zorlanmamalıdır.
- Seçimlerin ekran okuyucuda checkbox/radio/switch durumları bulunmalıdır.
- Sadece kaydırma hareketine bağlı içerik değiştirme olmamalıdır; Önceki/Sonraki alternatifleri vardır.
- Arka plan/metin ve düğme dolgu/metin kontrastları ayrı sınanmalıdır.
- Karanlık, aydınlık ve OLED durumları değerlendirilmelidir; büyük yazı ekran görüntüsü her temanın tam erişilebilirlik sertifikası değildir.
- Sistem geri ve görünür Geri aynı kurulum adımına dönmelidir.
- TalkBack’in okuma sırası, switch etiket ilişkisi, yatay ekran ve tablet düzeni fiziksel/etkileşimli son kontrolde ayrıca değerlendirilmelidir.

## 8. Ölçüm planı

Önerilen başarı tanımları: yardımsız bildirimsiz kurulum tamamlamak; ilk favoriyi kaydedip tekrar açmak; hatırlatıcıyı kapatmak; bir konu tercihini değiştirip yeni içerik görmek. Kurulum süresi, ilk kaydetmeye kadar geçen süre ve görev sırasında yanlış beklentiler kaydedilebilir.

Dönüşüm artışı veya elde tutma yüzdesi bu çalışma kapsamında ölçülmemiştir. Karşılaştırma yapılacaksa aynı kaynak sürüm, cihaz sınıfı, dil ve kullanıcı profili tanımlanmalıdır. “Yeni tasarım daha güzel” tek başına işlevsel başarı ölçütü değildir.

## 9. Uygulama eşlemesi

`ui/Onboarding.kt` ekran/CTA; `data/Baslangic.kt` seçim kuralları; `data/Olumlamalar.kt` içerik; `data/Depo.kt` atomik kayıt; `ui/Uygulama.kt` yükleme/yönlendirme; `MainActivity.kt` Android izni ve dil bağlamı; `notif/BildirimZamanlari.kt` sınır içinde saat üretimi; `ui/BildirimPlani.kt` ortak kontrol.

Referans: [Android izin akışı](https://developer.android.com/develop/ui/compose/notifications/notification-permission) ve [Compose erişilebilirlik](https://developer.android.com/develop/ui/compose/accessibility/api-defaults). Ekran metinlerinin tamamı Türkçe ve İngilizce kaynaklarda bulunur.
