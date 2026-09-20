package com.yalnizfahrettin.azim.data

/** UI additions for days 5–9. Every entry is complete in the seven shipped languages. */
object JourneyCopy {
    val languages = listOf("tr", "en", "pt", "de", "fr", "it", "ru")
    val entries = mapOf(
        "searchAll" to listOf("Tüm konularda ve düşünürlerde ara", "Search all topics and thinkers", "Pesquisar todos os temas e pensadores", "Alle Themen und Denker durchsuchen", "Rechercher tous les thèmes et penseurs", "Cerca in tutti i temi e pensatori", "Поиск по всем темам и мыслителям"),
        "allResults" to listOf("Tüm katalogdaki sonuçlar", "Results from the whole library", "Resultados de toda a biblioteca", "Ergebnisse aus der gesamten Bibliothek", "Résultats de toute la bibliothèque", "Risultati dell’intera raccolta", "Результаты по всей библиотеке"),
        "savedOrder" to listOf("Kaydettiğin sırayla", "In the order you saved them", "Pela ordem em que guardaste", "In deiner Speicherreihenfolge", "Dans l’ordre d’enregistrement", "Nell’ordine di salvataggio", "В порядке сохранения"),
        "historyNote" to listOf("Son 30 günde gönderilenler. Bu liste, sözleri okuduğun anlamına gelmez.", "Sent in the last 30 days. This list does not mean you read the quotes.", "Enviadas nos últimos 30 dias. Esta lista não indica que leste as mensagens.", "In den letzten 30 Tagen gesendet. Diese Liste bedeutet nicht, dass du die Texte gelesen hast.", "Envoyées au cours des 30 derniers jours. Cette liste ne signifie pas que tu as lu les textes.", "Inviate negli ultimi 30 giorni. Questo elenco non indica che hai letto i testi.", "Отправлены за последние 30 дней. Этот список не означает, что вы прочитали тексты."),
        "threeUses" to listOf("Bir sanat, üç kullanım", "One artwork, three uses", "Uma imagem, três utilizações", "Ein Motiv, drei Möglichkeiten", "Une image, trois usages", "Un’immagine, tre utilizzi", "Одно изображение — три варианта"),
        "separate" to listOf("Her birini ayrı seç. Tema uygulamanı, duvar kâğıdı telefon ekranını değiştirir.", "Choose each separately. A theme changes the app; wallpaper changes your phone screen.", "Escolhe cada um separadamente. O tema muda a aplicação; o fundo muda o ecrã do telemóvel.", "Wähle jede Nutzung einzeln. Das Design ändert die App, das Hintergrundbild den Handybildschirm.", "Choisis chaque usage séparément. Le thème change l’application, le fond d’écran change ton téléphone.", "Scegli ogni utilizzo separatamente. Il tema cambia l’app, lo sfondo cambia lo schermo del telefono.", "Выбирайте отдельно. Тема меняет приложение, обои — экран телефона."),
        "widgetHint" to listOf("Arka planı ve boyutu seç, ekle. Her gün yeni bir söz.", "Choose a background and size, then add. A new quote each day.", "Escolhe o fundo e o tamanho e adiciona. Uma nova frase por dia.", "Hintergrund und Größe wählen, dann hinzufügen. Jeden Tag ein neuer Text.", "Choisis le fond et la taille, puis ajoute le widget. Un nouveau texte par jour.", "Scegli sfondo e dimensione, poi aggiungi. Un nuovo testo ogni giorno.", "Выберите фон и размер, затем добавьте. Новый текст каждый день."),
        "cancelled" to listOf("İşlem iptal edildi. Yeniden deneyebilirsin.", "Cancelled. You can try again.", "Operação cancelada. Podes tentar novamente.", "Abgebrochen. Du kannst es erneut versuchen.", "Opération annulée. Tu peux réessayer.", "Operazione annullata. Puoi riprovare.", "Отменено. Можно попробовать снова."),
        "ancient" to listOf("Antik düşünce", "Ancient thought", "Pensamento antigo", "Antikes Denken", "Pensée antique", "Pensiero antico", "Античная мысль"),
        "modern" to listOf("Yeniçağ düşüncesi", "Modern philosophy", "Filosofia moderna", "Philosophie der Neuzeit", "Philosophie moderne", "Filosofia moderna", "Философия Нового времени"),
        "poetry" to listOf("Şiir ve iç dünya", "Poetry and inner life", "Poesia e vida interior", "Dichtung und inneres Leben", "Poésie et vie intérieure", "Poesia e vita interiore", "Поэзия и внутренний мир"),
        "ethics" to listOf("Etik ve yaşam", "Ethics and life", "Ética e vida", "Ethik und Leben", "Éthique et vie", "Etica e vita", "Этика и жизнь"),
        "next" to listOf("Sıradaki durak", "Next stop", "Próxima etapa", "Nächste Etappe", "Prochaine étape", "Prossima tappa", "Следующий шаг")
    )
    fun text(key: String, language: String) = entries.getValue(key)[languages.indexOf(Diller.normalize(language)).coerceAtLeast(0)]
    fun thinker(id: String, language: String): String = text(when {
        id in setOf("mevlana","yunus","sems","hafiz","sadi","attar","hayyam") -> "poetry"
        id == "konfucyus" -> "ethics"
        id in setOf("sokrates","demokritos","plotinos","pyrrhon","marcus","seneca","epiktetos","platon","aristoteles","epikuros","diogenes","herakleitos","cicero") -> "ancient"
        else -> "modern"
    }, language)
}
