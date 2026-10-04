# Ascend 9.11.0

- Deneme penceresi tam ekran inset yönetimi ve tek kaydırılabilir gövdeye geçti. Sabit alt alanın taşması engellendi; boşluklar ve başlık ölçüleri küçültüldü. Büyük yazı/küçük ekranlarda iki eylem de kaydırılarak erişilebilir.
- Görünüm ve widget galerileri tüm kartları aynı anda oluşturan Column yerine görünür satırları oluşturan LazyColumn kullanır.
- Tema resimleri arka plan iş parçacığında, örneklenmiş çözünürlükte açılır. Küçük kartlar düşük çözünürlük; ana ekran/büyük önizleme daha yüksek çözünürlük kullanır. 16 MB sınırlı önbellek ve en fazla iki eşzamanlı çözme işlemi vardır.
- Widget bitmap önizlemeleri arayüz iş parçacığından ayrıldı. Tema değişirken eski önizleme temizlenir; yeni resim hazır olunca gösterilir.
- Uygulama teması ve Widget seçimleri ortak kutudan çıkarıldı; aralarında kısa aralık olan ayrı yüzeyler oldu. Keşfet/Görünüm/Senin başlıkları 18sp.
- Senin bölümleri Özet, Geçmiş, Seriler. Ayrı kaydırma/ekran durumları korunur. Haftalık bilgiler yalnız Özet bölümündedir.
- Geçmişte daha yumuşak kart yüzeyleri ve metinsiz, erişilebilir isimleri olan kalp/paylaş simgeleri vardır.
- Ana ekran bağlantısı yalnız N konu seçili yazar. Açılan panelde konu adları ve aç/kapat anahtarları vardır, sözler yoktur. Kapatılan satır panel kapanana kadar görünür; yeniden açılabilir. Son etkin konu kapatılamaz. Kategorileri keşfet bağlantısı ana koleksiyon listesine gider.
- Kaynak kontrolleri, birim testleri, lint ve APK derlemesi CI. Büyük yazıda deneme eylemlerine erişim, bildirim anahtarları ve lazy galerinin kapsamı için hedefli Android testleri eklendi. Bu teslimde uzun emülatör turu ve cihaz FPS/bellek profili ölçümü yapılmadı; performans sonucu cihaz üzerinde ayrıca gözlenmelidir.
