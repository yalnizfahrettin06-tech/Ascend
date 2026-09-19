package com.yalnizfahrettin.azim.data

object CollectionCopy {
    private fun row(vararg text: String) = text.toList()
    private val languages = listOf("tr","en","pt","de","fr","it","ru")
    private val entries = mapOf(
        "title" to row("İmparator · Yaşayan koleksiyon","Emperor · Living collection","Imperador · Coleção viva","Imperator · Lebendige Kollektion","Empereur · Collection vivante","Imperatore · Collezione animata","Император · Живая коллекция"),
        "badge" to row("HAREKETLİ KOLEKSİYON · PRO","LIVING COLLECTION · PRO","COLEÇÃO VIVA · PRO","LEBENDIGE KOLLEKTION · PRO","COLLECTION VIVANTE · PRO","COLLEZIONE ANIMATA · PRO","ЖИВАЯ КОЛЛЕКЦИЯ · PRO"),
        "tagline" to row("Sessiz güç. Aynı dünyaya açılan üç görünüm.","Quiet strength. Three views into one world.","Força tranquila. Três vistas do mesmo mundo.","Stille Stärke. Drei Ansichten einer Welt.","Une force tranquille. Trois vues d’un même monde.","Forza tranquilla. Tre viste dello stesso mondo.","Спокойная сила. Три взгляда на один мир."),
        "scene" to row("Sahne","Scene","Cena","Szene","Scène","Scena","Сцена"),
        "wallpaper" to row("Duvar kâğıdı","Wallpaper","Papel de parede","Hintergrund","Fond d’écran","Sfondo","Обои"),
        "preview" to row("Sabit önizleme · Sistem duvar kâğıdını değiştirmez.","Still preview · Does not change your system wallpaper.","Pré-visualização estática · Não altera o papel de parede do sistema.","Statische Vorschau · Ändert den Systemhintergrund nicht.","Aperçu fixe · Ne modifie pas le fond d’écran du système.","Anteprima statica · Non cambia lo sfondo di sistema.","Статичный просмотр · Системные обои не меняются."),
        "motion" to row("Hareket","Motion","Movimento","Bewegung","Animation","Movimento","Движение"),
        "apply" to row("Hareketli sahneyi kullan","Use living scene","Usar cena animada","Animierte Szene verwenden","Utiliser la scène animée","Usa la scena animata","Использовать живую сцену"),
        "pro" to row("Pro ile bu koleksiyonu aç","Unlock this collection with Pro","Abrir esta coleção com Pro","Diese Kollektion mit Pro öffnen","Débloquer cette collection avec Pro","Sblocca questa collezione con Pro","Открыть коллекцию с Pro"),
        "still" to row("Sabit görünümü kullan","Use still scene","Usar cena estática","Statische Szene verwenden","Utiliser l’image fixe","Usa la scena statica","Использовать статичную сцену"),
        "widget" to row("Uyumlu widget oluştur","Create matching widget","Criar widget a condizer","Passendes Widget erstellen","Créer le widget assorti","Crea il widget abbinato","Создать подходящий виджет"),
        "prototype" to row("Koleksiyon 01 · Prototip","Collection 01 · Prototype","Coleção 01 · Protótipo","Kollektion 01 · Prototyp","Collection 01 · Prototype","Collezione 01 · Prototipo","Коллекция 01 · Прототип"),
        "read" to row("İlk günü ücretsiz oku","Read day one free","Ler o primeiro dia grátis","Tag eins kostenlos lesen","Lire le premier jour gratuitement","Leggi gratis il primo giorno","Первый день бесплатно"),
        "exclusive" to row("ÖZEL SERİ · PRO","EXCLUSIVE SERIES · PRO","SÉRIE EXCLUSIVA · PRO","EXKLUSIVE REIHE · PRO","SÉRIE EXCLUSIVE · PRO","SERIE ESCLUSIVA · PRO","ОСОБАЯ СЕРИЯ · PRO"),
        "step" to row("Bugünün küçük adımı · İsteğe bağlı","Today’s small step · Optional","Pequeno passo de hoje · Opcional","Kleiner Schritt heute · Freiwillig","Le petit pas du jour · Facultatif","Il piccolo passo di oggi · Facoltativo","Маленький шаг сегодня · По желанию"),
        "start" to row("Pro ile yedi güne başla","Start seven days with Pro","Começar sete dias com Pro","Sieben Tage mit Pro beginnen","Commencer sept jours avec Pro","Inizia sette giorni con Pro","Начать семь дней с Pro"),
        "promise" to row("7 özgün okuma · 7 küçük adım","7 original readings · 7 small steps","7 leituras originais · 7 pequenos passos","7 eigene Texte · 7 kleine Schritte","7 textes originaux · 7 petits pas","7 letture originali · 7 piccoli passi","7 авторских текстов · 7 маленьких шагов"),
        "quiet" to row("Hareket cihazın animasyon ve pil tasarrufu ayarlarına uyar.","Motion respects your device’s animation and battery saver settings.","O movimento respeita as definições de animação e poupança de bateria.","Die Bewegung beachtet Animations- und Energiespareinstellungen.","L’animation respecte les réglages d’animation et d’économie d’énergie.","Il movimento rispetta le impostazioni di animazione e risparmio energetico.","Движение учитывает настройки анимации и экономии энергии."),
        "benefit" to row("Yaşayan koleksiyon ve özel seri","Living collection and exclusive series","Coleção viva e série exclusiva","Lebendige Kollektion und exklusive Reihe","Collection vivante et série exclusive","Collezione animata e serie esclusiva","Живая коллекция и особая серия")
    )
    fun text(key: String, language: String) = entries.getValue(key)[languages.indexOf(Diller.normalize(language))]
}
