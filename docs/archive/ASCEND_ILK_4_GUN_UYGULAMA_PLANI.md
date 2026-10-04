# Ascend — İlk dört günün ayrıntılı uygulama planı

Tarih: 20 Eylül 2026  
Başlangıç: 9.27.0; kaynak 41d6c8e ve teslim belgesi 2a735bf.  
Durum: Uygulamaya hazır plan. Bu belgede tarif edilen tasarımlar henüz koda uygulanmadı. Günler çalışma paketleridir; otomatik çalışma veya kesin süre taahhüdü değildir.

## 1. Dört gün sonunda elde etmek istediğimiz sonuç

Kullanıcı uygulamayı açtığında antik/savaşçı dünyasını hissedecek; kısa bir sözü deneyimleyecek, bildirim ritmini kuracak ve görünümünü seçecek. Pro ile karşılaşınca hangi somut sonucu açacağını anlayacak. Beş sayfa boyunca aynı fotoğraf, uzun açıklamalar ve tekrarlanan bildirim maketleri görmeyecek.

**Tasarım cümlesi: Sessiz güç. Sana ait bir ritim.**

Gladyatör kimliğini kan, bağıran sloganlar veya her yere kılıç koyarak kurmayacağız. Taş, metal, arena eşiği, hazırlık ve kararlı duruş ortak dünyayı oluşturacak. Metinler doğal ve insani kalacak: “Bugün zorlanman, dün öğrendiklerini silmiyor.” gibi.

Dört günün kapsamı: ortak tasarım kuralları + beş onboarding ekranı + bağlamsal Pro teklifleri. Ana ekranın kompozisyonunu, Keşfet mimarisini, alt navigasyonu veya içerik veritabanını yeniden kurmak bu pakete dahil değil.

## 2. Güncel koddan doğrulanan başlangıç

| Bulgu | Plan üzerindeki etkisi |
|---|---|
| Onboarding zaten beş adımlı; animasyon, taslak ve sabit alt eylem var. | Bunları yeniden icat etmeyeceğiz. Düzen ve görsel içerik değişecek; devamlılık korunacak. |
| Son adımda Pro olmayan kullanıcı, ücretsiz tema seçse de deneme teklifine gidiyor. | Gün 4'te ücretsiz seçimden doğrudan ana ekrana geçiş tasarlanacak. |
| Pro ekranı bağlamı biliyor; dört fayda bloğu ve ücretsiz haklar açıklaması gösteriyor. | Mevcut bağlam modelini koruyup ilk alanı seçilen sonuç + en fazla üç faydaya indireceğiz. |
| Onboarding tema listesi Beyaz, Siyah, Roma, Atlı Yolcu, İmparator, Düello. | Altı seçenek korunacak; fotoğraflı seçeneklerin görsel önceliği değişecek. |
| Beyaz, Siyah, Roma ve Atlı Yolcu ücretsiz. | Yeni tasarım bu hakları daraltmayacak. |
| Pro, ödeme almayan yerel demo. | Gerçek üç günlük deneme, fiyat, abonelik ve satın alma iddiası olmayacak. |
| Tema/widget/duvar kâğıdı için mevcut seçim ve Pro dönüş yolları var. | Gün 4 bunları ortak davranış kurallarına bağlayacak; wallpaper uygulaması ayrıca onay gerektirecek. |

Bu tur kaynak ve önceki rapor okundu; yeni APK üzerinde yeniden kullanıcı gezintisi yapılmadı. Aşağıdaki boyutlar tasarım hedefidir, mevcut ekranlarda ölçülmüş kusur listesi değildir.

## 3. Değişmeyecek kararlar

- Beş onboarding adımı; fazladan anket, kullanıcı hesabı, cinsiyet veya ilgi formu yok.
- Bildirimsiz devam et seçeneği eklenmeyecek. İzin verilmediğinde açık durum açıklaması ve ayarlara erişim olacak; sistem kararı aşılmayacak.
- Yedi mevcut dil korunacak: Türkçe, English, Português, Deutsch, Français, Italiano, Русский.
- İki ücretsiz sanat ve iki düz tema korunacak. Ücretsiz olanlar “kısıtlı, kötü” gösterilmeyecek.
- Pro denemesi mevcut demo kapsamında kalacak. Gerçek ödeme ayrı çalışma.
- Ana ekranın söz yerleşimi ve mevcut iyi çalışan görünüm galerisi korunacak.
- Mevcut kullanıcıya sırf tasarım değişti diye onboarding yeniden zorlanmayacak.

## 4. Gün 1 — Tasarım sistemi ve beş ekranın kompozisyonu

### 4.1 Tasarım paleti

Aşağıdaki değerler başlangıç paletidir. Gerçek cihazda metin ve fotoğraf üstü kontrast kontrolünden sonra gerekirse küçük düzeltme yapılır.

| Rol | Önerilen değer | Kullanım |
|---|---|---|
| Ana zemin | #111315 | Koyu onboarding kabuğu |
| Kart yüzeyi | #1D2023 | Seçenekler, kısa bilgi alanları |
| Yükseltilmiş yüzey | #292D31 | Seçili olmayan kontrol ayrımı |
| Ana metin | #F4F2ED | Başlık ve temel içerik |
| İkincil metin | #B9BDC2 | Açıklama; soluk ince yazı değil |
| Ayırıcı | #454B51 | Sadece gerekli sınırlar |
| Ana eylem | #E4E7EB / #141719 | Gümüş yüzey, koyu yazı |
| Bronz ayrıntı | #B69B76 | Dekoratif küçük vurgu; önemli metin için otomatik tercih değil |

Koyu onboarding kimliği, kullanıcının sonunda açık Roma veya Beyaz seçmesini engellemez. Kurulum kabuğu ayrı, seçilen uygulama teması ayrı sorumluluktur. Tema önizlemesine basmak bütün kurulumun rengini aniden değiştirmeyecek.

Gümüşü tüm ekranın üzerine gri perde gibi yaymayacağız. Kart kenarında, kontrol yüzeyinde ve seçili durumda kullanacağız. Fotoğrafların özgün ışığı ve dokusu korunacak.

### 4.2 Yazı, ölçü ve boşluk kuralları

| Öğe | Hedef |
|---|---|
| Marka | Mevcut güncel işaret ve arayüz yazısı; yeni bir logo çalışması açılmayacak |
| Ana başlık | Inter 28 sp, 34 sp satır yüksekliği; uzun dilde 26 sp başlangıç alternatifi |
| Gövde | Inter 15–16 sp, 22–24 sp satır yüksekliği |
| Söz | Lora 26–30 sp; yalnız söz alanında |
| Yardım | 13–14 sp; kritik bilgi küçük dipnota gizlenmez |
| Ekran kenarı | Normal 24 dp, dar ekranda 16 dp |
| Aralık sistemi | 8 / 12 / 16 / 24 dp |
| Ana düğme | En az 56 dp; metin büyürse yükseklik artar |
| Küçük eylem | Görünür ikon 20–24 dp, dokunma alanı en az 48 dp |
| Kart yarıçapı | 20–24 dp; düğme 16–18 dp |
| Geçiş | 180–240 ms solma ve küçük kayma; azaltılmış harekette sade geçiş |

Bütün elemanlar kalan yüksekliği eşit paylaşmayacak. İçerik grupları birbirine yakın duracak; boş alan görselin kompozisyonuna ait olacak. Alt düğmenin üstünde sebepsiz büyük boş blok oluşturulmayacak. Dolu görünmek için açıklama veya rozet eklenmeyecek.

### 4.3 Ortak ekran kabuğu

Üst: küçük marka, geri oku, 1/5 gibi ilerleme bilgisi. Ortada kaydırılabilir içerik. Altta daima görünen tek ana eylem. Alt eylem sistem navigasyonunun üstünde kalacak; arka planı ekranla aynı olacak, ayrı gri bant oluşmayacak.

Kısa telefon veya büyük yazıda önce dekoratif görsel küçülür, sonra içerik kayar. Dil seçimi, izin açıklaması ve eylem metni kesilmez. Büyük yazıyı sığdırmak için sistem font ölçeği kapatılmaz.

### 4.4 Beş ekranın farklı görsel görevi

| Adım | Ana görsel/etkileşim | Neden farklı? |
|---|---|---|
| 1 Dil | Arena kapısı/antik eşik detayından kısa yatay kırpma | İlk karşılaşma; dekor kullanıcı seçimini bastırmaz |
| 2 İlk söz | Sakin savaşçı arkasında gerçek kaydırılabilir söz kartı | Ürünün kendisini denetir |
| 3 Ritim | Sayı + saat aralığı + küçük gün çizgisi | Ayarı görünür kılar; fotoğraf zorunlu değil |
| 4 İzin | Tek dokunmayla açılan bildirim maketi | Bildirimin faydasını gösterir |
| 5 Görünüm | Gerçek altı tema önizlemesi | Karar verdirir; başka dekor gerekmez |

Aynı fotoğraf beş sayfaya taşınmayacak. Adım 3'te büyük bir savaşçı fotoğrafı olmaması kimliği bozmaz; malzeme, renk ve kontroller aynı dili taşır.

### 4.5 Gün sonu teslimi

Beş ekranın normal telefon ve kısa ekran taslağı; ortak renk/ölçü tablosu; her görselin odak noktası; korunacak davranış listesi. Taslaklar gerçek kaynak görsellerle hazırlanır. Görselde olmayan detay tarif edilerek varmış gibi kabul edilmez.

**Kabul:** Her ekranda tek ana görev anlaşılır; Devam görünür; aynı resim tekrar etmez; fotoğraf üzerine yıkanmış gri katman yok; ana ekranda değişiklik yok.

## 5. Gün 2 — Dil ve ilk gerçek deneyim

### 5.1 Ekran 1: Dil

**Başlık:** Kendi disiplinine dön.  
**Açıklama:** Gününe eşlik eden sözler. Önce dilini seç.  
**Alt güven notu:** Hesap gerekmez.  
**Ana düğme:** Devam

Diller kendi adlarıyla, tek liste içinde gösterilir. Satır seçildiğinde işaret ve belirgin yüzey birlikte değişir. Bayrak kullanılmaz; ülke ve dil aynı şey değildir. Yedi büyük ayrı kart yerine ince, dokunması kolay satırlar kullanılır. Desteklenen cihaz dili başlangıçta seçilebilir; bilinmeyen dilde mevcut güvenli varsayılan korunur.

Dil değişince başlık ve düğme anında o dile döner; liste başa sıçramaz. Devam düğmesi listeyle kaymaz. Dil seçimi ekranında arama/klavye gereksizdir. İkinci sayfadan geri dönünce seçili dil kalır.

**Kompozisyon:** Kısa marka satırı → antik eşik görseli → başlık/açıklama → dil listesi → alt eylem. Kısa ekranda ilk küçülecek alan dekor görselidir. Aa/Merhaba/Hello kartı eklenmez.

### 5.2 Ekran 2: Bir sözü deneyimle

**Başlık:** Bir söz. Yeniden başlamak için.  
**Açıklama:** Kaydır. Sende kalan sözü kaydet.  
**Örnek:** Bugün zorlanman, dün öğrendiklerini silmiyor.  
**Ana düğme:** Ritmimi ayarla

Tek büyük kart ana odaktır. Altında yalnız küçük sayfa göstergesi ve erişilebilir “Başka bir söz” eylemi olur. Kaydırma tek erişim yolu değildir. Kalbe basınca 180 ms civarı küçük tepki ve “Kaydedildi” geri bildirimi verilir; sürekli parlayan animasyon yok.

Burada önemli davranış kararı: “Kaydedildi” diyorsak söz gerçekten Kaydedilenler'e aktarılmalı. Uydurma örneği kaydolmuş gibi göstermeyeceğiz. Mevcut katalogda kalıcı kimliği olan, erişime açık bir söz seçilecek. Metin/kimlik bulunamıyorsa katalogdan uygun başka söz kullanılacak. Taslak kayıtlar kurulum bitince bir kez işlenecek; geri dönmek veya tekrar basmak kopya üretmeyecek. Kalbe basmak sonraki sayfaya geçmenin şartı olmayacak.

Kart üzerindeki metin ile savaşçı figürü yarışırsa bütün resim karartılmaz: metin tarafında yerel yumuşak gölge veya ayrı yazı alanı kullanılır. Bir sonraki sözde kart yüksekliği zıplamaz; uzun içerik seçimi editoryal olarak kısıtlanır, kullanıcının yazı büyütme hakkı kısıtlanmaz.

### 5.3 Gün 2 uygulama sırası

1. Ortak kabuğu yeni tasarım kurallarına bağla; mevcut alt eylem ve taslak kaydı koru.
2. Dil listesini ve geri dönüş davranışını uygula.
3. İlk söz etkileşimini gerçek kayıt kimliğiyle ilişkilendir.
4. TR/EN metinlerini yerleştir; diğer beş dil için aynı anahtarları tamamla.
5. Dar ekran, büyük yazı, iki kez kaydetme ve uygulama yeniden açma görevlerini dene.

**Gün sonu teslimi:** Çalışan ilk iki adım, TR/EN karşılaştırma kareleri ve üç davranış kanıtı: dil kalır, düğme erişilir, gerçek kayıt bir kez oluşur.

## 6. Gün 3 — Ritim, izin ve görünüm

### 6.1 Ekran 3: Gününün ritmi

**Başlık:** Gününe kaç kez eşlik edelim?  
**Açıklama:** Sayıyı ve saat aralığını sen belirle.  
**Ana düğme:** Devam

İlk blok tek satırlık eksi / sayı / artı kontrolü. Altında başlangıç ve bitiş için iki kompakt satır. Mevcut 1–7 aralığı ve zamanlama politikası korunur; yeni bir aralık bu tasarım işinin yan etkisi olarak eklenmez.

Alt kısımda tek küçük gün çizgisi seçilen aralığı gösterir. Aynı bilgiyi ikinci kez saat chip'leri, uzun özet ve bildirim kartıyla tekrarlamayacağız. Örnek bildirim yalnız izin sayfasında gösterilecek. Bu karar, önceki geniş plandaki ritim ekranında bildirim örneği önerisini sadeleştirir; güncel kodun yalnız izin sayfasında önizleme yaklaşımını korur.

Saatler yakın veya eşit seçildiğinde mevcut zamanlama kuralı açık mesajla anlatılmalı; arayüzün kabul ettiği plan ile hesaplanan plan farklı olmamalı. Sessizce başka saate çevirmek yok. Teknik incelemede desteklenmeyen aralık görünürse aynı satırda nedenini söyle ve düzeltmeye izin ver.

### 6.2 Ekran 4: Bildirim

**Başlık:** İyi bir söz seni bulsun.  
**Açıklama:** Seçtiğin saatlerde kısa bir hatırlatma.  
**Ana düğme:** Bildirimleri aç  
**Görünür yardım:** Açılır bildirim görünümünü ayarla

Bildirim maketi bir kere gösterilir. Üzerinde Ascend işareti, kısa gerçek söz ve açık “Örnek bildirim” etiketi bulunur. Dokununca içerik genişler; bunun cihazın sistem ayarını değiştirmediği anlaşılır olur. Bildirim içinde gerçek hayatta sunulmayan tam boy arka plan vaat edilmez.

| Durum | Metin/eylem | Beklenen sonuç |
|---|---|---|
| Henüz sorulmadı | Bildirimleri aç | Kullanıcı eylemiyle sistem izni istenir |
| İzin verildi | Bildirimler açık / Temamı seç | Yeni izin penceresi yok |
| İzin reddedildi | Bildirimlerin ulaşması için izin gerekiyor | Otomatik tekrar isteme yok; kullanıcı yeniden deneyebilir |
| Sistem tekrar sormuyor | Ayarlardan aç | Uygulamanın bildirim ayarına gider |
| Ayarlardan dönüldü | Gerçek durum yeniden okunur | Varsayımsal başarı yok |
| Kanal kapalı | Bildirim kanalını aç | Genel izin ile kanal durumu ayrı ele alınır |
| Açılır görünüm cihazda farklı | Kısa yardım | Her üreticide aynı davranış garantisi yok |

Yardım satırı ekranın en altında görünmeyen dipnot olmayacak. Bildirim kartının hemen altında yer alacak. Ayara gidip dönünce seçilen saatler ve dil korunacak. Bildirim izni reddinde bildirimsiz atlama eklenmeyecek; geri gitme ve taslağı koruma çalışmaya devam edecek.

### 6.3 Ekran 5: Sana ait görünüm

**Başlık:** Kendi atmosferini seç.  
**Açıklama:** Önizle, seç. Daha sonra değiştirebilirsin.  
**Ana düğme:** Ücretsiz seçimde Ascend'e başla; Pro seçimde Bu görünümü aç.

Önerilen sıralama:

| Sıra | Önizleme | Erişim |
|---|---|---|
| İlk sıra | Atlı Yolcu / Roma | İkisi de ücretsiz |
| İkinci sıra | İmparator / Düello | Pro |
| Kompakt alt sıra | Siyah / Beyaz | İkisi de ücretsiz |

Böylece altı seçenek kalır, dört fotoğraf asıl görsel görevi üstlenir. Beyaz/Siyah gizlenmez ama iki boş büyük kart ilk ekranı tüketmez. Küçük ekranda kartlar kayar; alt eylem sabittir. 2× yazıda tek sütuna düşebilir. Seçili kartta işaret + çerçeve, Pro kartta aynı köşede rozet kullanılır.

Yeni kurulumda Atlı Yolcu önerilen başlangıç olabilir; bu bir varsayılan tasarım kararıdır. Kullanıcının mevcut veya taslak seçimini asla ezmez. Kartın üzerindeki söz örneği kısa olur; resmin kendisini örten uzun alıntı kullanılmaz.

Tema seçimi yalnız önizlemeyi günceller. Ücretsiz seçimle tamamlanınca ana ekran o temayla açılır. Pro seçimde teklif kapanırsa aynı seçim ekranına dönülür; ücretli seçim sessizce Beyaz'a çevrilmez. Kullanıcı başka ücretsiz görünümü kendisi seçer.

### 6.4 Gün sonu teslimi

Tam beş adım; izin durumları; tema seçim/geri dönüş matrisi; yedi dil anahtar kontrolü. Gece/gündüz teması ve sistem navigasyon rengi geçişi bir kez kısa görsel kontrolden geçer. Kurulumu tamamlayan eski kullanıcı etkilenmez.

## 7. Gün 4 — Pro'nun değerini sonuç üzerinden göster

### 7.1 Temel akış değişikliği

Bugünkü son adım mantığında ücretsiz kullanıcının önüne de teklif geliyor. Yeni davranış:

- Ücretsiz görünüm → kurulumu bitir → seçili görünümle ana ekran.
- Pro görünüm → o görselin teklifi → demo açılırsa aynı temayla bitir.
- Teklif kapatılırsa → aynı tema seçimine dön; ayarlar kaybolmasın.
- Kurulumdan sonra Pro özellik kullanma isteği → ilgili sonucun teklifi.
- Ana ekrana her gelişte otomatik satış penceresi yok.

Bu seçim daha az teklif gösterimi yaratabilir. Başarıyı gösterim sayısıyla değil, teklifin anlaşılması ve açılan özelliğin gerçekten kullanılmasıyla değerlendireceğiz. Gelir artışı garantisi verilmez.

### 7.2 Tek teklif iskeleti, farklı içerik

Üstte küçük PRO DEMO ve kapatma. Sonra kullanıcının seçtiği sonucun gerçek önizlemesi, kısa başlık ve en fazla üç somut fayda. “Diğer Pro özellikleri” isteğe bağlı açılır bölümde. Alt eylem sabit; hemen üstünde “Ödeme alınmaz. Abonelik başlatılmaz.” görünür.

Birincil düğme: **Demoyu aç ve devam et**. İkincil eylem bağlama göre **Şimdilik vazgeç** veya **Ücretsiz görünümlere dön**. “Ücretsiz devam et” basınca ücretli görünüm uygulanmış gibi davranılmayacak.

| Bağlam | Başlık önerisi | İlk kanıt | Ana fayda |
|---|---|---|---|
| Tema | İmparator, ana ekranında. | Seçilen tema ve gerçek söz yerleşimi | Bu görünümü kullan |
| Duvar kâğıdı | Bu atmosferi telefonuna taşı. | Yazısız seçili resim | Ana/kilit ekranı seçimi |
| Widget | Günün sözü, gözünün önünde. | Seçilmiş kare/geniş widget | Günlük içerik ve seçili arka plan |
| Seri | Başladığın seriye devam et. | Gerçek bir sonraki günün başlığı | Mevcut özel içeriğe devam |
| Konu | Bu konuda daha derine in. | Gerçek erişilebilir örnek | Konuyu okuma ve bildirim seçimine ekleme |
| Video/paylaşım | Bu sözü seçtiğin görünümle paylaş. | Üretilecek biçimin önizlemesi | İstenen paylaşım biçimini aç |

Her teklif bütün ürün kataloğunu sıralamaz. Örneğin wallpaper niyetinde “tüm düşünürler, video, kısa seri, widget...” ilk ekranı doldurmaz. En ilgili fayda önce, diğer iki fayda kısa tutulur.

### 7.3 Açıldıktan sonra ne olur?

| İş | Demo açılınca | Demo reddedilince |
|---|---|---|
| Onboarding Pro tema | Aynı tema ile kurulum tamamlanır | Tema seçimine geri dönülür |
| Uygulama teması | Başlatılmış tema kullanma işlemi bir kez tamamlanır | Önceki tema korunur |
| Wallpaper | Seçili hedef ve önizleme korunur; Uygula beklenir | Telefonun duvar kâğıdı değişmez |
| Widget | Önizlemeye dönülür; Ekle ile sistem isteği yapılır | Widget oluşturulmuş sayılmaz |
| Seri | İlgili gün/okuma ekranı açılır | İlerleme ve ücretsiz önizleme korunur |
| Paylaşım | Hazırlanmış önizlemeye dönülür; Paylaş/Kaydet kullanıcı eylemi kalır | Seçili söz kaybolmaz |

Demo açılınca kullanıcı adına dış uygulamaya paylaşım veya sistem duvar kâğıdı değişimi yapılmayacak. Aynı düğmeye hızlı iki dokunma çift kayıt/çift işlem yaratmayacak. Yükleme, hata ve tekrar deneme açık olacak.

### 7.4 Ücretsiz/Pro anlatımı

İlk ekranda sayılardan çok sonuç: “Ücretsiz bildirimlerin ve kaydettiğin sözler sende kalır.” Ayrıntılı farklar mevcut erişim verisinden üretilmeli; elle yazılan 6 konu/4 tema gibi metinler erişim kuralları değişince yanlış kalmamalı. Pro kapatıldığında kayıtlar, geçmiş ve seri ilerlemesi silinmemeli. Sistem duvar kâğıdı kendiliğinden geri alınmamalı.

Gerçek satın alma eklenmediği sürece 3 günlük deneme, iptal tarihi, indirim veya ödeme kartı akışı gösterilmeyecek. Bu plan satış tasarımını test edilebilir hale getirir; Play Billing entegrasyonu değildir.

### 7.5 Ölçüm ve gün sonu teslimi

Mevcut yerel ProductSignals yaklaşımını koru. Yeni analiz sunucusu kurma. Teklif görüntüleme, kapatma, demo açma ve ilgili özelliği kullanma ayrı olaylar olarak değerlendirilsin; ekran yeniden çizildi diye görüntüleme tekrar sayılmasın. Demo açma “satın alma” olarak raporlanmasın.

Teslim: dört ana bağlamın (tema, wallpaper, widget, seri) örnek ekranları; diğer girişlerin aynı kabukla uyumu; dönüş/iptal testleri; ücretsiz kurulumun satış ekranına uğramadan tamamlandığı kanıt.

## 8. En az ama anlamlı doğrulama paketi

| No | Görev | Başarı ölçütü |
|---|---|---|
| 1 | Yedi dil arasından seç, kaydır, geri dön | Seçim kalır; Devam ekran içinde |
| 2 | İlk sözü kaydet, geri dön, tekrar aç | Aynı kayıt çoğalmaz; sonuç gerçekten bulunur |
| 3 | Sayı/saat değiştir, uygulamayı yeniden aç | Taslak korunur; hesaplanan plan uyumlu |
| 4 | İzni reddet, ayarlardan aç, dön | Gerçek durum okunur; plan kaybolmaz |
| 5 | Ücretsiz tema ile bitir | Satış ekranı zorunlu değil; doğru tema |
| 6 | Pro tema seç, teklifi kapat | Ücretli seçim otomatik uygulanmaz; geri dönüş net |
| 7 | Pro açıp aynı işleme dön | Bağlam korunur; işlem yalnız bir kez |
| 8 | Wallpaper teklifinden vazgeç | Sistemde değişiklik yok |
| 9 | 320/360/412 dp ve büyük yazı | Ana eylem kesilmez; metin çakışmaz |
| 10 | Eski kurulumlu veriyle güncelle | Kayıtlar/tema/izinler korunur; zorunlu yeniden onboarding yok |

Bütün cihaz × dil × yazı boyutu kombinasyonlarını uzun emülatör maratonuna çevirmeyeceğiz. TR/EN temel yol; DE/RU uzun metin örnekleri; yedi dil anahtar kontrolü; kısa ekran ve 2× yazı için kritik eylem testi yeterli ilk kapıdır. APK oluşunca kısa dosya/bütünlük kontrolü ve seçilmiş birkaç ekran yapılır. Fiziksel cihazda denenmeyen davranış açık bırakılır.

## 9. Uygulama dosyaları ve iş sırası

| Bölge | Planlanan iş |
|---|---|
| ui/Onboarding.kt | Beş adım, metinler, ücretsiz/Pro final yönlendirmesi |
| ui/OnboardingSahne.kt ve ortak UI rolleri | Görsel kabuk, ölçüler, geçişler |
| ui/TemaSecimi.kt / data/AnaTemalar.kt | Onboarding sıralaması ve kompakt düz tema seçimi; kimlikler korunur |
| ui/ProEkrani.kt / DenemeTeklifi | Ortak teklif iskeleti ve kısa faydalar |
| data/ProOffer.kt | Mevcut sabit seçim kimlikleriyle bağlamın korunması |
| Mevcut dil kaynakları | Yedi dilde yeni kısa metinler; eksik anahtar denetimi |
| Android akış testleri | İzin, sabit eylem, gerçek kayıt, teklif sonrası dönüş |

Önce veri ve erişim davranışları sabitlenir, sonra görsel yerleşim yapılır. Birbirinden kopuk iki ayrı Pro tasarımı büyütülmez. DenemeTeklifi ve ProEkrani aynı görsel/işlevsel kuralları paylaşır; çağıran akışın bitirme davranışı ayrı kalır.

Her gün sonunda yalnız ilgili değişiklikler kaydedilir. Kabul kapısı geçmeyen gün, yapılmış sayılmaz. Kritik veri kaybı veya erişilemeyen düğme varsa dekoratif animasyon ertelenir. Özellik sayısını artırmak için günü genişletmeyiz.

## 10. Dört gün sonunda teslim listesi

- Beş adımın birbirinden farklı ama aynı marka dilindeki çalışan tasarımı.
- Ücretsiz kurulum ve bağlamsal Pro için net akış.
- Dört ana Pro kullanımının gerçek sonuç önizlemesi.
- Yedi dilin yeni metinleri; kaynak anahtarlarının kontrolü.
- Korunan eski veriler, taslaklar ve erişim hakları.
- Kısa doğrulama sonucu, açık cihaz sınırları ve APK.
- Önce/sonra birkaç karşılaştırma karesi; uzun APK incelemesi yok.

**En önemli bitiş sorusu:** Kullanıcı ilk açılışta “Bu uygulama bana ne sunuyor?” ve Pro ekranında “Bunu açarsam ne elde edeceğim?” sorularını açıklama almadan cevaplayabiliyor mu? Güzel görünen fakat bu soruları yanıtlamayan ekran bitmiş sayılmayacak.

## 11. Kaynak ve tasarım kararlarının ayrımı

Palet, metinler, sıralama ve boyutların büyük bölümü Ascend için tasarım önerisidir; bilimsel olarak kanıtlanmış dönüşüm oranı iddiası taşımaz.

- Proje kaynağı: Onboarding.kt, ProEkrani.kt, ProOffer.kt, AnaTemalar.kt; 9.27 teslimi ve 10 günlük yol haritası.
- [Android Compose erişilebilirlik rehberi](https://developer.android.com/codelabs/jetpack-compose-accessibility): küçük ikonların dokunma alanı ve anlamsal erişilebilirlik için referans. En az 48 dp hedef bu yaklaşımla uyumludur.
- [Android izin isteme rehberi](https://developer.android.com/training/permissions/requesting): izin öncesi bağlam, sistem kararını okuma ve reddetme durumunu ele alma için referans.

Yeni kullanıcı araştırması yapılmadı. Kullanıcı tepkisiyle ilgili hedefler sonraki uygulamanın kısa görev testlerinde doğrulanacak.
