# Ascend 9.6.3 — Tasarım ve doğrulama kaydı

## Tasarım kararı

Arayüzde Inter; sözlerde ve marka yazısında Lora. Ekran başlıkları 28–32 sp, yarı kalın; açıklamalar normal ağırlıkta. Font uygulamaya gömülüdür, internetten yüklenmez. Türkçe karakter desteği bulunan resmi font kullanılmıştır; lisansı uygulama kaynaklarına eklenmiştir.

Zemin #E9EAEC, okunacak yüzey #F7F7F8, koleksiyon #DFE1E4, ana metin #18191B, ikincil metin #575B61, güçlü alan #25272B. Dekoratif sınırlar ile dokunulabilir kontrol sınırları farklı ağırlık taşır. Bordo, sarı, altın, turuncu veya mavi vurgu eklenmemiştir.

## Onboarding

1. **Karşılama:** Tek mimari görsel; iki satırlık fayda ve hesap gerektirmediği bilgisi. Görselin girişinde kısa yakınlaşma geçişi.
2. **Söz denemesi:** Fotoğraf tekrarı yerine kaydırılabilen söz kartı. Kalp ve erişilebilir “Başka bir söz” eylemiyle kullanım denenebilir. Örnek beğenme gerçek favorileri değiştirmez.
3. **Sıklık:** Seçilen sayıyla hareket eden dairesel gösterim. Birden yediye kadar tek karar. Bu sayfada bildirim önizlemesi tekrar edilmez.
4. **Saat aralığı:** Gün çizimi ve başlangıç/bitiş kontrolleri. Gösterilen örnek saatler gerçek planlayıcıdan hesaplanır. Büyük yazıda kontroller dikey dizilir.
5. **Bildirim:** Açılıp kapanabilen tek bildirim örneği ve cihaz ayarları yardımcısı. İzin verilmeden kurulum tamamlanmaz; izin reddi otomatik izin gibi kaydedilmez.

İçerik ile tek devam düğmesi aynı ölçülen grubun içindedir. Aralarında 24 dp mesafe vardır. Uzun ekranlarda görsel sahneye en fazla 130 dp ek yükseklik verilir; dış boşluğu büyütmek yerine mimari görsel, ritim halkası ve gün yayı genişler. Kısa ekranlarda ve büyük yazıda içerik kaydırılır. İki kat yazıda görsel ek yüksekliği kapatılır; düğmeye erişim korunur. Kullanılabilir alanı gereksiz metinle doldurmak yerine görsel sahne ekran yüksekliğine uyarlanır. Sayfalar kısa yatay hareket ve saydamlık geçişiyle değişir.

## Keşfet

Başlık, konu ve koleksiyon adları Inter'e geçti. Arama alanı dolu açık yüzey kazandı. Koleksiyon kartları saydamlıktan çıkarılarak belirgin gri yüzeye ve daha yumuşak köşelere alındı. İlk mevcut koleksiyon ayrı bir grafit giriş alanında sunuluyor; yeni öneri algoritması veya ekstra ürün modu eklenmedi. Kilitli konu ayrıntısının ana eylemi Pro demosuna yönelir; tarayıcı bağlantısı sunmaz. Koleksiyona göz atmak bildirim tercihini kendiliğinden değiştirmez.

## Araştırma dayanağı

- [Inter üretici dokümanı](https://rsms.me/inter/): arayüz fontu ailesi ve değişken ağırlıklar.
- [Android Compose font rehberi](https://developer.android.com/develop/ui/compose/text/fonts): gömülü font ailesi ve ağırlıkların açık tanımlanması.
- [NN/G: Onboarding ve bağlamsal yardım](https://www.nngroup.com/articles/onboarding-tutorials/): uzun açıklamalar yerine kullanım anında anlaşılır yönlendirme. Ascend'deki uygulaması, söz denemesi ve son adımda açılabilen bildirim yardımcısıdır.
- [WCAG kontrast](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html): metin ve yüzey ayrımının değerlendirilmesi.

## Doğrulama

**Son kaynak:** `aba2c187d69776e410486f06cefc00bed1bea314`; sürüm 9.6.3, versionCode 33.

- Kaynak kontrolleri, birim testleri, lint ve APK derlemesi geçti.
- Android 15 emülatöründe 11 onboarding testi ve 1 ana ekran/Keşfet/arama testi geçti: toplam 12 Android testi.
- Son sürümde 720×1600 piksel, 280 dpi uzun telefon düzeninde 15 ekran görüntüsü açılıp görsel olarak incelendi: 5 normal onboarding, 5 iki kat yazı/koyu onboarding, genişletilmiş bildirim, ana ekran, Keşfet, koleksiyon ayrıntısı ve arama sonucu.
- Daha önceki 9.6.0 incelemesinde daha kısa 1080×1920 ekran da değerlendirildi; son APK'nın görsel kanıtı yukarıdaki uzun ekran düzenidir.
- Büyük yazı görüntüleri devam düğmesine kaydırıldıktan sonra yakalandı. Bu nedenle bazı görüntülerde başlığın üst kısmı görünmez; içerik kaydırılarak erişilir. İleri düğmesi ve geri dönüş testlerle erişilebilir bulundu.
- Bildirim reddi, son adımda izin isteme, sayfa geri dönüşü, seçimlerin korunması, saatlerin planla eşleşmesi, bildirim açma/kapatma ve söz denemesi kontrol edildi.

[Başarılı Android çalışması](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/34691287645)

[APK indir](https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v9.6.3-ui/Ascend-9.6.3.apk) · [15 ekran görüntüsü ve test kanıtları](https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v9.6.3-ui/Ascend-9.6.3-visual-evidence.zip)

Bu kayıt fiziksel Samsung cihazında bildirim açılır pencere görünümünün doğrulandığı anlamına gelmez. Pro bu sürümde demodur; gerçek ödeme altyapısı eklenmemiştir.


## Görsel incelemede bulunan ve giderilen sorunlar

- **Bildirim kartında kesilen gölge:** Telefon çerçevesi içindeki gölge dikdörtgen bir leke oluşturuyordu. Gölge kaldırılıp ince sınır kullanıldı; telefon içindeki kartın köşeleri temizlendi.
- **Koyu temada kaybolan artı/eksi:** Kontrolün varsayılan rengi, uygulamanın özel koyu paletiyle eşleşmiyordu. Etkin/pasif ikon ve çerçeve renkleri açıkça tema paletine bağlandı.
- **Uzun telefonlarda dış boşluk:** Sabit 310 dp sahne üst sınırı, uzun ekranlarda içeriğin çevresinde büyük boşluk bırakıyordu. Görsel sahnelere kullanılabilir yüksekliğe bağlı ek alan tanımlandı. Düğme içerikten koparılmadı.
- **Küçük metinde gevşek satırlar:** Küçültülen metinler büyük gövde stilinin satır yüksekliğini devralıyordu. Yardım metinlerine 18–20 sp satır yüksekliği verildi.
- **Keşfet'te yüzeylerin birbirine karışması:** Arama, koleksiyon ve sayfa zemini ayrı nötr tonlara taşındı; mevcut ilk koleksiyon grafit alanla belirginleştirildi.

- **Söz kartındaki tek taraflı boşluk:** Kısa örnek söz üst kenara yığılmak yerine kartın okuma alanında dikey ortalandı. Beğenme eylemi ayrı alt satırda kaldı.
- **Bildirim çerçevesinin belirsizliği:** Üst ve alt telefon işaretleri çerçevenin uçlarına dağıtıldı; bildirim örneği bu iki sınır arasında yer alıyor.

## Kontrast ölçümleri

| Kullanım | Renkler | Hesaplanan oran |
|---|---|---|
| Ana metin / zemin | #18191B / #E9EAEC | 14,61:1 |
| İkincil metin / zemin | #575B61 / #E9EAEC | 5,67:1 |
| İkincil metin / koleksiyon | #575B61 / #DFE1E4 | 5,21:1 |
| Beyaz / grafit eylem | #FFFFFF / #25272B | 14,96:1 |
| Güçlü kontrol sınırı / zemin | #747880 / #E9EAEC | 3,68:1 |

Bunlar tanımlanan düz renk çiftlerinin hesabıdır. Fotoğraf üzerindeki bütün piksellerin otomatik kontrast denetimi değildir.

## Teslim kapsamının sınırı

Ana ekranın fotoğraf ve söz kompozisyonu korundu. Bu çalışma yeni öneri algoritması, hesap sistemi, reklam veya gerçek ödeme eklemez. Gömülü font çevrimdışı çalışır. Hareketler kısa sayfa geçişleri, söz değişimi, ritim ve gün aralığı animasyonlarıyla sınırlıdır; sürekli dikkat çeken bir döngü eklenmemiştir. Emülatör ekran görüntülerinde deterministik yakalama için sistem animasyonları kapalıdır; bu görüntüler hareket akıcılığının fiziksel cihaz ölçümü değildir.


APK SHA-256: `bbc086eca32be474ad1f39b82663ed3c55d21ed8d651a43b19c03b70726ad842`
