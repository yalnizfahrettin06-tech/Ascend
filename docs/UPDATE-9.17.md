# Ascend 9.17 — sadeleştirme güncellemesi

## Son davranış
- Senin: Kaydedilenler ve Geçmiş. Haftanın izi, ziyaret/seri sayaçları ve kilometre taşı kutlamaları ana kullanıcı yolundan çıkarıldı. Eski kayıtlar silinmedi.
- Kısa seriler Keşfet ana listesinde tek küçük girişten açılır. Gün ilerlemesi ve kaydet/paylaş eylemleri korunur.
- Keşfet ve arama yalnız içerik bulunan kategorileri gösterir. Mevcut 126 kategorinin tamamı dolu olduğu için yapay bir kategori silme yapılmadı. Gelecekte taslak açıldığında boş liste kullanıcıya sunulmaz.
- Azim, Pes Etmemek ve Yeniden Başlamak için amaç cümleleri eklendi: sürdürmek, vazgeçme eşiği, aradan sonra dönmek. İncelenen içerikler farklı ihtiyaçlar taşıdığı için kalıcı kimlikleri ve erişimleri birleştirilmedi.
- Paylaşım görselle açılır. Video, Biçim kontrolünün altında isteğe bağlıdır. Güvenli alt alan, yüksek çözünürlüklü çıktı ve Pro denetimi korunur.
- Görünüm için tek düzenleme alanı Görünüm sekmesidir. Genel ayarlardaki çakışan tema/palet kontrolleri kaldırıldı; buradan ilgili bölüme gidilir.
- Bildirim konuları, saat/sıklık, içerik sınırları ve cihaz kurulumu ortak bildirim akışından erişilir. Genel ayarlarda ikinci bir saat editörü yoktur.
- Bildirimden açılan söz ayrı okuyucuda gösterilir; ana akış yenilense de hedef söz değişmez. Aynı bildirimin yeniden açılması ayrı istek sayılır. PendingIntent kimliği sözün tam kimliğini taşıyan URI ile ayrılır.
- İzin verilmiş ama kanal kapalıysa izin düğmesi sistem kanal ayarlarını açar. Uygulamanın kendi kapalı/ara verilmiş durumu ayrı belirtilir. Cihazın pil veya üretici kısıtları için kesin teslim garantisi verilmez.
- Başarılı gönderimin tekrar hafızası ve 30 günlük geçmişi tek DataStore işlemiyle kaydedilir. Android'e gönderim ile diske yazma farklı sistemler olduğundan süreç tam o aralıkta öldürülürse mutlak exactly-once garantisi yoktur.
- Uzun sözlerin okuyucu yazısı dengelendi; tam metin kaydırılarak erişilebilir. Bildirim genişletilince tam metin gösterilir.

## İçerik elemesi ve kaynak ayrımı
1.270 kaydın tamamında kimlik, kategori, sayı, kaynak parmak izi, Unicode ve birebir tekrar kontrolleri yapıldı. Sözcük örtüşmesiyle bulunan en yakın 12 çift editoryal olarak gözden geçirildi; bu ölçü anlam kalitesi puanı değildir.

Beş kayıtta tekrar eden soyut ifade yerine somut davranış yazıldı. Türkçe, İngilizce ve beş yeni dil birlikte güncellendi:
- v5_sinirlar_06: incitici konuşmada şimdi/sonra sınırı.
- v5_yunus_03: bilgi paylaşmadan önce sorunun bitmesini beklemek.
- v5_machiavelli_03: başarılı planın insan/kaynak koşullarını yeniden kontrol etmek.
- v5_kendini_ifade_08: ihtiyacı tek açık cümlede söylemek.
- v5_dinlemek_05: öğüt vermeden beklentiyi sormak.

Düşünür ve gelenek kategorilerinin kaynak etiketi “Ascend · Esinlenilmiş düşünce” oldu. Bunlar tarihî kişilere atfedilmiş doğrudan alıntılar değildir. Yeni metinler eklenmedi; kimlikler ve 10/15 adetlik düzen korundu.

Yedi dilin tümü yapısal olarak doğrulandı; beş değişikliğin çevirileri elle düzenlendi. Katalogdaki tüm yabancı dil metinlerinin bağımsız ana dil editörü onayı bu teslimin kapsamında değildir.

## Kısa doğrulama
Birim testleri ve lint; boş kategori filtresi, kaynak etiketi, kalıcı tekrar geçmişi, kişisel sekmeler, serilerin Keşfet'te açılması, aynı bildirim hedefinin iki kez doğru açılması. Mevcut dil ve görsel/video kaydetme testleri korunur. Emülatörde yalnız bu odaklı test grubu çalışır; tam cihaz matrisi veya uzun APK taraması yapılmaz.

## Kategori kapsamı — otomatik sayım
| Kategori kimliği | İçerik sayısı |
|---|---:|
| ozsefkat | 10 |
| ic_huzur | 10 |
| kendine_guven | 10 |
| motivasyon | 10 |
| azim | 10 |
| pes | 10 |
| zorluk_sabir | 10 |
| yorgunluk_sabir | 10 |
| tukenmislik | 10 |
| yeniden | 10 |
| uzun_soluk | 10 |
| umut | 10 |
| erteleme | 10 |
| derin_odak | 10 |
| durtu | 10 |
| rutin | 10 |
| dagilma | 10 |
| ozguven | 10 |
| korku | 10 |
| reddedilme | 10 |
| risk | 10 |
| utangaclik | 10 |
| marcus | 10 |
| seneca | 10 |
| epiktetos | 10 |
| platon | 10 |
| aristoteles | 10 |
| nietzsche | 10 |
| konfucyus | 10 |
| machiavelli | 10 |
| mevlana | 10 |
| yunus | 10 |
| sems | 10 |
| hafiz | 10 |
| zen | 10 |
| kuran | 10 |
| incil | 10 |
| tevrat | 10 |
| dua | 10 |
| sukur | 10 |
| antrenman | 10 |
| dayaniklilik | 10 |
| sakatlik | 10 |
| sabah_rutini | 10 |
| beslenme | 10 |
| girisimcilik | 10 |
| kariyer | 10 |
| liderlik | 10 |
| para | 10 |
| zaman | 10 |
| basarisizlik | 10 |
| basari | 10 |
| ask | 10 |
| ayrilik | 10 |
| aile | 10 |
| arkadaslik | 10 |
| yalnizlik | 10 |
| affetmek | 10 |
| kaygi | 10 |
| stres | 10 |
| minnettarlik | 10 |
| simdiki_an | 10 |
| uyku | 10 |
| karamsarlik | 10 |
| huzur | 10 |
| merak | 10 |
| okumak | 10 |
| hata | 10 |
| aliskanlik | 10 |
| sinav | 10 |
| epikuros | 10 |
| diogenes | 10 |
| herakleitos | 10 |
| cicero | 10 |
| montaigne | 10 |
| bacon | 10 |
| descartes | 10 |
| spinoza | 10 |
| pascal | 10 |
| locke | 10 |
| hume | 10 |
| rousseau | 10 |
| kant | 10 |
| schopenhauer | 10 |
| kierkegaard | 10 |
| emerson | 10 |
| thoreau | 10 |
| sadi | 10 |
| attar | 10 |
| hayyam | 10 |
| dustukten_sonra | 10 |
| kucuk_adim | 10 |
| gelisimi_gormek | 10 |
| beklemek | 10 |
| niyetine_sadik | 10 |
| hayir_demek | 10 |
| sinirlar | 10 |
| onay | 10 |
| kiyas | 10 |
| elestiri | 10 |
| kendini_ifade | 10 |
| kararsizlik | 10 |
| kontrol | 10 |
| fazla_dusunmek | 10 |
| ofke | 10 |
| pismanlik | 10 |
| kendini_affet | 10 |
| mukemmeliyet | 10 |
| belirsizlik | 10 |
| degisim | 10 |
| vedalar | 10 |
| tek_basina | 10 |
| destek | 10 |
| dinlemek | 10 |
| onarim | 10 |
| dinlenme | 10 |
| beden | 10 |
| is_sinir | 10 |
| yaraticilik | 10 |
| anlam | 10 |
| sokrates | 10 |
| demokritos | 10 |
| plotinos | 10 |
| pyrrhon | 10 |
| tao | 15 |
| budist_dusunce | 15 |
