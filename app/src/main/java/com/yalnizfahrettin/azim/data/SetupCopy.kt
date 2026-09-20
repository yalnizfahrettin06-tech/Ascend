package com.yalnizfahrettin.azim.data

/** Short, offline setup and contextual offer copy in all supported languages. */
object SetupCopy {
    private val languages = listOf("tr","en","pt","de","fr","it","ru")
    private val entries = mapOf(
        "hello" to listOf("Kendi disiplinine dön.", "Return to your discipline.", "Retoma a tua disciplina.", "Finde zu deiner Disziplin zurück.", "Retrouve ta discipline.", "Ritrova la tua disciplina.", "Вернись к своей дисциплине."),
        "helloBody" to listOf("Gününe eşlik eden sözler. Önce dilini seç.", "Words for your day. First, choose your language.", "Palavras para o teu dia. Primeiro, escolhe o idioma.", "Worte für deinen Tag. Wähle zuerst deine Sprache.", "Des mots pour ta journée. Choisis ta langue.", "Parole per la tua giornata. Scegli la lingua.", "Слова для твоего дня. Сначала выбери язык."),
        "account" to listOf("Hesap gerekmez.", "No account needed.", "Não é preciso criar conta.", "Kein Konto nötig.", "Aucun compte nécessaire.", "Non serve un account.", "Аккаунт не нужен."),
        "practice" to listOf("Bir söz. Yeniden başlamak için.", "A few words. A fresh start.", "Algumas palavras. Um novo começo.", "Ein paar Worte. Ein neuer Anfang.", "Quelques mots. Un nouveau départ.", "Qualche parola. Un nuovo inizio.", "Несколько слов. Новое начало."),
        "practiceBody" to listOf("Kaydır. Sende kalan sözü kaydet.", "Swipe. Save the words that stay with you.", "Desliza. Guarda as palavras que ficam contigo.", "Wische. Speichere Worte, die bei dir bleiben.", "Fais défiler. Garde les mots qui te parlent.", "Scorri. Salva le parole che ti restano dentro.", "Листай. Сохраняй слова, которые откликаются."),
        "saved" to listOf("Kaydedildi · Kurulumdan sonra Senin’de", "Saved · Find it in Yours after setup", "Guardado · Disponível em Teu após a configuração", "Gespeichert · Nach der Einrichtung unter Dein Bereich", "Enregistré · À retrouver dans Ton espace après la configuration", "Salvato · Dopo la configurazione nella tua sezione", "Сохранено · После настройки в твоём разделе"),
        "appearance" to listOf("Kendi atmosferini seç.", "Choose your atmosphere.", "Escolhe a tua atmosfera.", "Wähle deine Atmosphäre.", "Choisis ton atmosphère.", "Scegli la tua atmosfera.", "Выбери свою атмосферу."),
        "appearanceBody" to listOf("Önizle, seç. Daha sonra değiştirebilirsin.", "Preview, choose. Change it anytime.", "Vê, escolhe. Podes mudar depois.", "Ansehen, wählen. Jederzeit ändern.", "Découvre, choisis. Change quand tu veux.", "Guarda, scegli. Puoi cambiare quando vuoi.", "Посмотри и выбери. Можно изменить позже."),
        "openLook" to listOf("Bu görünümü aç", "Unlock this look", "Desbloquear este visual", "Diesen Look freischalten", "Débloquer ce style", "Sblocca questo stile", "Открыть этот стиль"),
        "freeLooks" to listOf("Ücretsiz görünümlere dön", "Back to free looks", "Voltar aos visuais gratuitos", "Zurück zu kostenlosen Looks", "Retour aux styles gratuits", "Torna agli stili gratuiti", "К бесплатным стилям"),
        "more" to listOf("Diğer Pro özellikleri", "More Pro features", "Mais funcionalidades Pro", "Weitere Pro-Funktionen", "Autres fonctions Pro", "Altre funzioni Pro", "Другие функции Pro"),
        "less" to listOf("Daha az göster", "Show less", "Mostrar menos", "Weniger anzeigen", "Voir moins", "Mostra meno", "Показать меньше"),
        "freeRights" to listOf("Ücretsiz bildirimlerin, kaydettiğin sözler ve geçmişin sende kalır.", "Your free reminders, saved words and history stay yours.", "Os teus lembretes gratuitos, palavras guardadas e histórico continuam teus.", "Deine kostenlosen Erinnerungen, gespeicherten Worte und dein Verlauf bleiben erhalten.", "Tes rappels gratuits, mots enregistrés et historique restent accessibles.", "Promemoria gratuiti, parole salvate e cronologia restano tuoi.", "Бесплатные напоминания, сохранённые слова и история остаются с тобой."),
        "permission" to listOf("Bildirimlerin ulaşması için izin gerekiyor.", "Permission is needed for your reminders.", "É necessária permissão para os lembretes.", "Für Erinnerungen ist deine Erlaubnis nötig.", "Une autorisation est nécessaire pour les rappels.", "Serve il permesso per i promemoria.", "Для напоминаний нужно разрешение."),
        "cancel" to listOf("Şimdilik vazgeç", "Not now", "Agora não", "Jetzt nicht", "Pas maintenant", "Non ora", "Не сейчас")
    )
    fun text(key: String, language: String): String = entries.getValue(key)[languages.indexOf(Diller.normalize(language)).takeIf { it >= 0 } ?: 1]
}
