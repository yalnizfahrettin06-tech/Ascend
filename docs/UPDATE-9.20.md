# Ascend 9.20 — Roma ve savaşçılar koleksiyonu

10 yeni özgün görsel: İmparator, Düello, Lejyon, Gladyatör, Taht, Spartalı, Atlı Komutan, Kalkan Duvarı, Arena Şampiyonu, Kuşatma.

Görseller yerleşik image_gen.imagegen aracıyla üretildi. Her biri 1024×1536; renk veya çözünürlük değiştirilmeden kayıpsız WebP olarak paketlendi. Tarihsel fantezi illüstrasyonlarıdır, belirli kişilere ait doğrulanmış portreler değildir. Üretim açıklamaları `docs/warrior-prompts.json`, kaynak ve bütünlük bilgileri `docs/warrior-art.json` dosyasında bulunur. Uygulamaya eklenen dosyalar `app/src/main/res/drawable-nodpi/warrior_*.webp` konumundadır.

Tema ve paylaşım seçimlerinden Cadı, renkli dokular, genel orman/deniz/zirve görselleri, çöl yolcusu ve benzeri koleksiyon dışı seçenekler çıkarıldı. Roma, şövalye, atlı yolcu, arena, kaleler ve muhafızlar korundu. Paylaşımda gereksiz renk/geçiş listeleri kaldırıldı. Beyaz ve siyah ana ekran temaları ücretsiz kaldı; yeni görseller mevcut Pro demo erişimini kullanır. Eski kaldırılmış ana ekran teması seçiliyse siyaha dönüşür. Eski görsel kaynakları kayıtlı tasarım uyumluluğu için silinmedi.

Tema seçim ekranında koleksiyon kapağı, daha uzun görsel kartları, açık Pro etiketi, seçili işareti ve dokunarak büyük önizleme var. Görselli kartlardaki tekrarlanan logo ve söz kaldırıldı. Küçük resimlerin üzerindeki ek karartma kaldırıldı. Kartlar kaydırıldıkça yüklenir; çözümleme arka planda ve sınırlı önbellekle yapılır.

Onboarding yine altı seçenek gösterir: Beyaz, Siyah, Roma, Atlı Yolcu, İmparator, Düello. Tema adları ve yeni arayüz metinleri yedi dilde mevcuttur.

Doğrulama: kaynak/çeviri kontrolleri, 10 görselin benzersizliği ve dosya bütünlüğü, Pro erişimi ve kaldırılmış temaların geçişi için birim testi; tema önizleme/Pro yönlendirmesi, büyük Fransızca yazıyla tema uygulama ve yeni görselle paylaşım dışa aktarımı için Android testleri.
