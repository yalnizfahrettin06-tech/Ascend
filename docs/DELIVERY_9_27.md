# Ascend 9.27.0 — Keşfet kimliği ve duvar kâğıdı

## Değişiklikler

- Keşfet kapakları, kaydedilenlerin konu görselleri ve kısa seri kapakları antik/savaşçı sanat yönünde eşlendi. Yeni resim üretmek yerine mevcut koleksiyondaki uygun eserler kullanıldı.
- Görünüm üç sekme oldu: Uygulama teması, Widget, Duvar kâğıdı.
- Duvar kâğıdında özgün dosyadan ana ekran, kilit ekranı veya ikisine uygulama; ayrı hedef seçimi, Pro erişim kontrolü ve gerçek işlem sonucu var.
- Roma ve Atlı Yolcu ücretsiz kalır. Diğer sanatlar Pro; önizlemeler ücretsizdir. Demo açılması telefonu kendiliğinden değiştirmez.
- Duvar kâğıdı görseline söz, filigran veya yeni karartma eklenmez. Kaynakların çözünürlüğü yükseltilmiş gibi sunulmaz.
- Koleksiyondaki wallpaper prototipi çalışan uygulama paneline bağlandı.
- Pro ekranında seçilen wallpaper ve tema/widget/wallpaper faydası görünür.
- Yeni metinler yedi dilde. Genel onboarding yeniden tasarımı bu sürümde değil; teslim edilen 10 günlük planın ilk üç günündedir.

## Doğrulama

Son kaynak: `41d6c8e26f599d68bc910f4303cc2b50f737e7aa`. VersionCode: 58.

[Derleme, testler ve yayın başarılı](https://github.com/yalnizfahrettin06-tech/Ascend/actions/runs/35515092661). Birim/lint işi, paylaşım regresyonu ve UI işi geçti. Android UI kanıtında 29 ekran/akış testi, 1 genel yolculuk testi ve 1 gerçek sistem duvar kâğıdı testi sıfır hatayla tamamlandı.

Önceki çalışmalarda emülatör bağlantısı kesilmesi ve duvar kâğıdı değişiminin Android renk yapılandırmasını yenileyerek sonraki test ekranını yeniden oluşturması saptandı. Sürekli paralel ekran aktarımı kaldırıldı. Gerçek sistem uygulama testi ayrı ve son sıraya alındı. İşlem durumu ViewModel ile ekran yeniden oluşturulmasına dayanıklı hale getirildi; bunu doğrulayan test eklendi. Başarısız testler atlanmadı.

APK: `Ascend-9.27.0.apk`, 54.440.151 bayt. ZIP bütünlüğü, manifest ve DEX varlığı doğrulandı.

SHA256: `baabfcaa72a0434cca596ba5b8b9002d78a81622a35faf7c29b6a3fb4afafa66`

[APK indir](https://github.com/yalnizfahrettin06-tech/Ascend/releases/download/v9.27.0-ui/Ascend-9.27.0.apk)

Kısa görsel kontrolde wallpaper önizlemesinin hedefleri aşağı ittiği görüldü ve genişliği azaltıldı. Hedeflerin görünürlüğü test beklentisine eklendi.

Fiziksel Samsung/Xiaomi/diğer başlatıcı kontrolü yapılmadı. Duvar kâğıdı Android üzerinde uygulansa da başlatıcı son çerçeveyi değiştirebilir. Testler emülatör kanıtıdır. Gerçek Play Billing eklenmedi.

## Plan dosyaları

- [10 günlük ayrıntılı plan](ASCEND_10_GUN_YOL_HARITASI.md)
- [Kısa özet](ASCEND_10_GUN_KISA_OZET.md)
