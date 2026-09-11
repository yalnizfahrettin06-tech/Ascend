# Ascend — Sadelik odaklı üç faz

11 Eylül 2026. Bu plan mevcut özellik raporunu kullanıcının son kapsam kararıyla daraltır. Bir önerinin raporda bulunması hemen uygulanacağı anlamına gelmez. Faz 1 ve Faz 2 uygulandı; Faz 3 henüz uygulanmadı.

## Tasarım ve kapsam sınırı

Gri/mermer zemin, koyu metin ve sakin Roma esintisi korunur. Yeni ana sekme, ana ekran filtresi, sayaç, öneri şeridi, puan veya günlük soru eklenmez. Kullanıcı uygulamayı açıp söz okuyabilmelidir. Her değişiklik mevcut bir işi kolaylaştırmalı; görünür kontrol sayısını artırmak varsayılan çözüm değildir.

## Faz 1 — Kısa başlangıç (uygulandı)

Onboarding 20 sayfadan 5 sayfaya indirildi:

1. Hoş geldin: kısa marka tanıtımı, hesap gerektirmeyen başlangıç.
2. Kullanım: rastgele söz için kaydırma, kaydetme/paylaşma ve Keşfet’te konu okuma.
3. Bildirim sıklığı: günlük adet, isteğe bağlı olduğu açık.
4. Saat aralığı: başlangıç/bitiş ve yaklaşık saatler.
5. İzin: örnek bildirim, açıklama, ayrıntılı yardım ve bildirimsiz devam.

İlk dört sayfada doğrudan bildirimsiz başlama yolu vardır. İzin yalnızca son sayfadaki kullanıcı dokunuşuyla istenir; reddeden kişi devam edebilir. Sistem izni zaten açık olsa bile kullanıcı bildirimleri açmadan başlayabilir.

Ad, kişilik/enerji/hedef/üslup soruları, kişiselleştirilmiş sonuç ve Pro anlatısı bu akıştan çıkarıldı. Bildirim konularının Keşfet’ten değiştirilebileceği anlatılır. Yeni kurulum mevcut başlangıç konularını kullanır; faz kapsamında yeni konu seçme ekranı eklenmez.

Eski tamamlanmamış taslakların adımı yeni akışa uyarlanır; mevcut tercih verileri silinmez. Düzenlemede mevcut bildirim konuları korunur. Kaydetme hatası ve tekrar deneme davranışı korunur. Mevcut profilin diğer tercihlerini tek tek düzenlemek bu fazda eklenmemiştir.

Kabul: beş sayfalık normal yol, ilk sayfadan bildirimsiz çıkış, izin reddinden sonra devam, geri dönüşte saat/adet korunması, büyük yazıda alt düğmelerin ulaşılabilirliği.

## Faz 2 — Mevcut işleri kolaylaştır (uygulandı)

Öncelik sırası:

- Eski profillerin içerik dışlaması ve ana akış arasındaki uyumsuzluğu düzelt. Rastgelelik kalsın; açıkça dışlanan içerik geri gelmesin. Yeni görünür ana ekran filtresi ekleme.
- Planı yalnızca ilgili ayarın açıldığı kısa satırlara ayır. Bildirim konusu, saat/adet ve mevcut içerik sınırları ayrı düzenlenebilsin. Tüm onboarding’i tekrar dolaştırma.
- Keşfet konu detayındaki söze dokunarak okuyucuya geçiş ekle. Kaydet/paylaş aynı okuyucuda olsun; her liste satırına çok sayıda düğme ekleme. Geri dönüşte arama ve konum korunsun.
- “Seçtiklerim” ifadesini bildirim konusu anlamını açık verecek şekilde düzelt. Konu okumak bildirim tercihini değiştirmesin.
- Kaydedilenlere tek arama alanı ekle. İlk aşamada klasör, etiket ve özel koleksiyon ekleme.
- “Okunan söz” etiketini gerçek gösterilme olayıyla uyumlu hale getir. Erişim sayısını kişisel gelişim gibi sunma; haftalık iz ikincil kalsın.

Kabul: kullanıcı tek ayarı diğer tercihleri değiştirmeden günceller; Keşfet’te bulduğu sözü kaydedip tekrar bulur; ana ekranın görünür bileşen sayısı artmaz.

## Faz 3 — Yalnızca faydası belirgin küçük eklemeler (plan)

- Oturumlar arasında yakın tekrarları azalt. Ana akışta sayı, ilerleme veya puan gösterme.
- Gerekirse Senin altında sınırlı son görülenler ekle. Ayrı ana sekme olmasın. Gerçek kronolojik kayıt ve temizleme seçeneği gerekir.
- Ek menüye tek sözü gizle ve geri al koymayı değerlendir. Tek sözün reddi kategoriyi otomatik kapatmasın.
- Bildirimleri bugün duraklat seçeneği; programı bozmadan açık dönüş zamanı. Yeni takvim/program ekranı ekleme.
- Paylaşımda görsel/video çıktısını hazır sahne/kendi fotoğrafı kaynağından ayır. Mevcut dışa aktarım korunsun. Kalbin arka plan favorisi mi söz kaydı mı olduğu açık olsun.
- Son paylaşım düzenini hatırlama; şablon kitaplığı ve yeni efektler sonraya kalsın.
- İstenirse az sayıda okunaklı ana ekran sahnesi. Roma/şövalye tercihi görünüm içinde; ana ekran ve paylaşım arka planı bağımsız.

Bu maddeler aynı anda uygulanmak zorunda değil. Önce Faz 2 sonucu değerlendirilir; mevcut bir sorunu çözmeyen madde elenir. Her eklemede ana ekran sadeliği tekrar değerlendirilir.

## Rapordaki diğer maddelerin durumu

Demo reklam/Pro: ilk kullanım anlatısından çıkarıldı; gerçek ödeme/reklam modeli ayrı karar, bu fazlarda otomatik etkinleştirilmez. Gerçek alıntı/kaynak ve benzer kategori birleştirme: içerik çalışması olarak bekler; kayıtları bozacak toplu değişiklik yapılmaz. Sosyal özellik, yapay zekâ sohbeti, hesap/bulut, kapsamlı günlük ve oyunlaştırma kapsam dışı kalır. Erişilebilir dokunma alanları ve bildirimsiz kullanım bütün fazlarda korunur.

## Doğrulama kaydı

Faz 1 için onboarding davranış testleri yeni akışa uyarlandı. GitHub derlemesinin birim test/lint sonuçları teslim mesajında ayrıca belirtilir. Cihazda çalıştırılmayan testler çalıştırılmış kabul edilmez. Faz 2 için ortak içerik sınırı, Türkçe kayıt araması, ayarların birbirinden bağımsız kaydı ve okuyucudan listeye dönüş senaryoları eklendi. Faz 3 plan durumundadır.

### Faz 2 uygulama notları

- Ana ekrandaki rastgele havuz mevcut tür/kaçınma/manevi içerik sınırlarını kullanır; profil yüklenmeden havuz oluşturulmaz. Boş havuzda tercihi değiştirme yolu korunur.
- Planım üç satırdan oluşur: bildirim konuları, saat/sıklık ve içerik sınırları. Ayarlar bağımsız kaydedilir; vazgeçme kalıcı tercihleri değiştirmez. Öneri ve Pro alanı bu panelden çıkarıldı.
- Keşfet’te erişime açık söz okuyucuda açılır. Aynı okuyucu kaydedilenlerden de kullanılabilir. Altındaki liste kapanmaz; arama, konu ve kaydırma korunur. Kilitli önizlemeler tam okuyucu açmaz.
- Kaydedilenlerde metin/konu araması ve boş sonuç açıklaması eklendi. Klasör ve etiket eklenmedi.
- Görüntüleme sayacı Görülen söz olarak adlandırıldı. Açık konu sayısı gelişim bölümünden çıkarıldı.
- Faz 3 özellikleri bu sürüme eklenmedi.
