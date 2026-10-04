# Faz 2 — İhtiyaç kategorileri, 9.14.0

Bu teslim 30 yeni ihtiyaç kategorisine 10'ar Türkçe–İngilizce özgün metin ekler.
Önceden bulunan 58 kişi dışı kategori ve 580 kaydı korunur; kişi bölümleriyle
birlikte toplam 120 kategori, 1200 kayıt ve 2400 dil metni vardır.
Kişiler Keşfet'te tek “Ünlü düşünürler” koleksiyonunda bulunur.

## Editoryal kararlar

- Yeni 300 çift Ascend için yazılmıştır; tarihî kişilere ait alıntılar değildir.
- Cümleler bir gündelik durum, fark edilebilir düşünce veya uygulanabilir küçük seçim taşır.
- Türkçe ve İngilizce aynı anlamı taşır; Türkçe kelime kelime çeviri gibi kurulmaz.
- Kayıp için iyileşme takvimi, beden için görünüş hedefi, öfke için bastırma emri kullanılmaz.
- Mucize, kesin başarı, tedavi vaadi ve utandırıcı motivasyon ifadeleri kullanılmaz.
- Her dilde 40–120 karakter sınırı, benzersiz kimlik, tam çeviri eşleşmesi ve kaynak özeti doğrulanır.
- Sözcük kümesi benzerliği taramasında yeni taslak ile eski erteleme metni arasında
  yüksek örtüşme bulundu. Yeni mükemmeliyetçilik metni, eğri çizgi yüzünden bütün resmi
  silmeme örneğiyle değiştirildi. Bu kontrol anlam benzerliğini bütünüyle ölçmez.
- Mevcut özgüven, antrenman ve dayanıklılık kategorilerinde üç Türkçe ifade sadeleştirildi;
  bu kayıtların anlamı ve kimliği korunur.

Yeni metinlerin tamamı yazım sırasında anlam ve kategori amacı yönünden değerlendirildi.
Eski katalogda otomatik kontroller tüm metinlere uygulandı; elle inceleme özellikle
yakın anlamlı konulara ve inanç, kayıp, beden, sakatlık, para gibi hassas alanlara odaklandı.
Bağımsız editör incelemesi veya kullanıcı etkisi araştırması yapılmış gibi bir kalite puanı verilmez.

## Kategori sınırları

- Düştükten sonra kalkmak: aksilik sonrası ilk hareket. Küçük adımlar: işi yapılabilir parçaya ayırmak.
- İlerlediğini fark etmek: geçmişteki kendinle karşılaştırma. Sonucu beklemek: emek sonrası haber bekleme.
- Hayır demek: talebe yanıt. Sınırları korumak: söylenmiş sınırı sürdürmek.
- Onay: kabul arayışı. Kıyas: başkasının koşullarıyla kendini ölçmek. Eleştiri: işe yarayan bilgiyi ayırmak.
- Kararsızlık: seçenekler arasında karar. Belirsizlik: bilinmeyenle yaşamak. Zihinsel tekrar: yeni bilgi üretmeyen düşünme.
- Pişmanlık: geçmiş seçim ve bugünkü onarım. Kendini affetmek: sorumluluk alırken kendini cezalandırmamak.
- Vedalar: yokluğa alan. Tek başına zaman: seçilmiş yalnızlık. Destek: ihtiyaç bildirmek ve yardım kabul etmek.
- Dinlenme: molayı hak etme zorunluluğundan çıkmak. Beden: görünüşten bağımsız bakım. İş sınırı: kişisel zamanı korumak.

Tüm yeni çiftlerin okunabilir inceleme kopyası:
`content/katalog/faz2-ihtiyaclar-pairs.txt`.
Asıl kaynak `content/source.en.json`, bağlı Türkçe dosyası `content/translations/tr.json`.
İnceleme kopyasıyla kaynak arasındaki sapma `tools/check_need_content.py` ile engellenir.

## Uygulama ve doğrulama

Eski 70 kategoriye ait toplu erişim hakları sabit tutulur; yeni kategoriler eski bir
grup anahtarı yüzünden yanlışlıkla açılmaz. Pro demosu yeni kategorileri açar.
Kaydedilmiş eski söz kimlikleri ve 82 arşiv kaydı korunur.
Yeni kategoriler arama, konu detayları ve bildirim seçimine katılır.
Bu sürüm yeni görsel gerektirmez; mevcut 70 kategori görseli ve sahne varlıkları korunur.

CI: içerik ve varlık doğrulama; tüm birim testleri; Android lint; uygulama ve test
derlemesi; emülatörde Türkçe/İngilizce yeni konu açma, seçim saklama ve Pro iptali;
1200 kayıtlık bildirim geçmişi; gerçek görsel/video dışa aktarma ve büyük yazıda buton yerleşimi.
APK yayını derleme ve ilgili emülatör işi başarılı olduktan sonra yapılır.
Fiziksel telefonların tamamı veya mağaza yayını için uygunluk iddiası değildir.
