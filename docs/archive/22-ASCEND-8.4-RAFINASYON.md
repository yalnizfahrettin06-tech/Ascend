# Ascend 8.4 — Kontrollü bordo ve koleksiyon kimliği

10 Eylül 2026. Kullanıcının onayladığı Bordo Rafinasyon raporunun A yaklaşımı.

## Uygulanan değişiklikler

- Paylaş: açık bordo yüzey, koyu bordo içerik, kısa basılı renk geçişi ve görünür klavye odağı.
- Kaydet: opak kontrol konturu, kayıtlı durumda bordo kalp; yüzey nötr kalır.
- Ana ekran: kategori çizgisi 28 × 1 dp; kaynak seçicisi 48 dp hedef; kategori/söz/kaynak grubu 24 dp üst boşlukla başlar. Mevcut Lora kademeleri, sözün kendi kaydırması ve sabit eylemler korunur.
- Mevcut gerçek sütun/yaprak varlığı marka satırında 28 dp; yükselen çizgi Bugün navigasyonunda kalır.
- Keşfet: aktif sekmede bordo metin ve kısa çizgi; sıcak taş kart yüzeyi, 13/19 sp açıklamalar, daha kısa Türkçe/İngilizce yardımcı metinler.
- Her koleksiyona ortak 24 × 24 / 1,75 çizgi ailesinden ayrı ikon.
- Koleksiyon kartında gerçek seçili alt konu sayısı ve tik; kartın tamamı seçim düğmesine dönüşmez.
- Bildirim özetinde kapalı teslimat açıkça belirtilir. Hiç konu yoksa özet tüm konuları açar.
- Koyu tema ve Mürekkep için ayrı yüzey karşılıkları. Genel Material birincil renk ve onboarding düğmeleri değiştirilmedi.

## Bilinçli olarak uygulanmayanlar

Ek sis/aura kullanılmadı: raporda koşullu olan bu dekor yerine işlevsel bordo vurgular tercih edildi. Heykel dosyası, içerik, bildirim motoru, erişimler, Pro/reklam demosu ve paylaşım üretimi değiştirilmedi. Yeni logo üretilmedi; depodaki mevcut resmî varlık kullanıldı.

## Doğrulama

Renk testleri yeni yüzeylerin metin ve odak kontrastını bütün paletlerde denetler. Mevcut Android testleri arama/detay/geri dönüş, 320–411 dp, büyük yazı, koyu tema ve onboarding akışını kapsar. Ek kontroller gerçek seçim sayısını, bildirim kapalı metnini ve boş seçimden konu bulmayı doğrular.

GitHub Actions'ta APK paketleme işi UI testlerinden sonra çalışır. Bu belge testlerin geçtiğini önceden iddia etmez; sonuçlar ilgili commit'in Actions kaydındadır. Fiziksel cihaz veya kullanıcı araştırması yapılmış sayılmaz.
