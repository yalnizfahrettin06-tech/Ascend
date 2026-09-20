package com.yalnizfahrettin.azim.data

object WallpaperCopy {
    val languages = listOf("tr","en","pt","de","fr","it","ru")
    internal val entries = mapOf(
        "title" to listOf("Duvar kâğıdı","Wallpaper","Papel de parede","Hintergrund","Fond d’écran","Sfondo","Обои"),
        "intro" to listOf("Seçtiğin dünyayı telefonuna taşı.","Bring your chosen world to your phone.","Leva o teu mundo para o telemóvel.","Deine Welt auf deinem Smartphone.","Retrouve ton univers sur ton téléphone.","Porta il tuo mondo sul telefono.","Перенеси свой мир на экран телефона."),
        "detail" to listOf("Yazısız, sabit görseller. Roma ve Atlı Yolcu ücretsiz; diğerleri Pro.","Still artwork without text. Rome and Dark Rider are free; the rest are Pro.","Imagens estáticas sem texto. Roma e Viajante a cavalo são grátis; as restantes são Pro.","Statische Bilder ohne Text. Rom und Dunkler Reiter sind kostenlos; die übrigen sind Pro.","Images fixes sans texte. Rome et Cavalier sombre sont gratuits ; les autres sont Pro.","Immagini statiche senza testo. Roma e Cavaliere oscuro sono gratuiti; le altre sono Pro.","Статичные изображения без текста. Рим и Тёмный всадник бесплатны; остальные — Pro."),
        "home" to listOf("Ana ekran","Home screen","Ecrã inicial","Startbildschirm","Écran d’accueil","Schermata Home","Главный экран"),
        "lock" to listOf("Kilit ekranı","Lock screen","Ecrã de bloqueio","Sperrbildschirm","Écran verrouillé","Schermata di blocco","Экран блокировки"),
        "both" to listOf("İkisi de","Both","Ambos","Beide","Les deux","Entrambi","Оба экрана"),
        "target" to listOf("Nereye uygulansın?","Where should it appear?","Onde aplicar?","Wo soll es erscheinen?","Où l’appliquer ?","Dove applicarlo?","Где применить?"),
        "apply" to listOf("Duvar kâğıdını uygula","Apply wallpaper","Aplicar papel de parede","Hintergrund anwenden","Appliquer le fond d’écran","Applica sfondo","Установить обои"),
        "pro" to listOf("Bu duvar kâğıdını Pro ile aç","Unlock this wallpaper with Pro","Desbloquear este papel de parede com Pro","Diesen Hintergrund mit Pro freischalten","Débloquer ce fond d’écran avec Pro","Sblocca questo sfondo con Pro","Открыть эти обои с Pro"),
        "working" to listOf("Uygulanıyor…","Applying…","A aplicar…","Wird angewendet…","Application…","Applicazione…","Установка…"),
        "done" to listOf("Duvar kâğıdı uygulandı.","Wallpaper applied.","Papel de parede aplicado.","Hintergrund angewendet.","Fond d’écran appliqué.","Sfondo applicato.","Обои установлены."),
        "error" to listOf("Uygulanamadı. Cihaz izinlerini kontrol edip yeniden dene.","Could not apply. Check device restrictions and try again.","Não foi possível aplicar. Verifica as restrições do dispositivo e tenta novamente.","Nicht angewendet. Prüfe die Geräteeinschränkungen und versuche es erneut.","Échec. Vérifie les restrictions de l’appareil et réessaie.","Impossibile applicare. Controlla le restrizioni del dispositivo e riprova.","Не удалось установить. Проверь ограничения устройства и повтори."),
        "note" to listOf("Önizlemedeki kırpma kullanılır; başlatıcın görünümü biraz değiştirebilir.","Uses the preview crop; your launcher may adjust the framing.","Usa o recorte da pré-visualização; o lançador pode ajustar o enquadramento.","Der Vorschauausschnitt wird verwendet; dein Launcher kann ihn anpassen.","Le cadrage suit l’aperçu ; le lanceur peut l’ajuster.","Usa il ritaglio dell’anteprima; il launcher può modificarlo.","Используется кадрирование предпросмотра; лаунчер может его изменить."),
        "benefit" to listOf("Tema, widget ve duvar kâğıdı","Theme, widget and wallpaper","Tema, widget e papel de parede","Thema, Widget und Hintergrund","Thème, widget et fond d’écran","Tema, widget e sfondo","Тема, виджет и обои"),
        "promise" to listOf("Aynı koleksiyonu uygulamada ve telefonunda kullan.","Use one collection in the app and on your phone.","Usa a mesma coleção na app e no telemóvel.","Eine Kollektion in der App und auf deinem Smartphone.","Une même collection dans l’app et sur ton téléphone.","Una sola collezione nell’app e sul telefono.","Одна коллекция в приложении и на телефоне.")
    )
    fun text(key: String, language: String) = entries.getValue(key)[languages.indexOf(Diller.normalize(language))]
}
