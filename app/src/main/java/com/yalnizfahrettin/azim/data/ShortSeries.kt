package com.yalnizfahrettin.azim.data

import java.time.LocalDate

data class SeriesProgress(val id: String, val completed: Int = 0, val lastDay: LocalDate? = null) {
    fun canComplete(today: LocalDate) = completed < 7 && (lastDay == null || today.isAfter(lastDay))
    fun complete(today: LocalDate) = if(canComplete(today)) copy(completed = completed + 1, lastDay = today) else this
    fun encode() = "$id|$completed|${lastDay ?: ""}"
    companion object {
        fun decode(raw: String): SeriesProgress? = runCatching {
            val parts = raw.split('|')
            require(parts.size == 3 && ShortSeries.all.any { it.id == parts[0] })
            val n = parts[1].toInt(); require(n in 0..7)
            val day = parts[2].takeIf { it.isNotBlank() }?.let(LocalDate::parse)
            require(n == 0 || day != null)
            SeriesProgress(parts[0], n, day)
        }.getOrNull()
    }
}

data class ShortSeries(val id: String, val tr: String, val en: String, val category: String, val prompts: List<Pair<String,String>>) {
    fun title(dil: String) = if(dil == "tr") tr else en
    val quotes get() = Sozler.kategoriden(category).take(7)
    fun prompt(index: Int, dil: String) = prompts[index].let { if(dil == "tr") it.first else it.second }
    companion object {
        val all = listOf(
            ShortSeries("kindness", "Kendine daha nazik", "A little kinder to yourself", "ozsefkat", listOf(
                "Bugün kendine hangi cümleyi daha yumuşak söyleyebilirsin?" to "Which sentence could you say to yourself more gently today?",
                "Bir arkadaşın aynı şeyi yaşasaydı ona ne söylerdin?" to "What would you say to a friend going through the same thing?",
                "Bugün yeterli olan küçük bir şey neydi?" to "What small thing was enough today?",
                "Kendinden beklediğin hangi şeyi biraz hafifletebilirsin?" to "Which expectation of yourself could you soften?",
                "Dinlenmek için kendine nasıl küçük bir yer açabilirsin?" to "How could you make a little room to rest?",
                "Görünmeyen hangi çabanı bugün fark ettin?" to "What unseen effort did you notice today?",
                "Bu haftadan kendine söylemeye devam etmek istediğin cümle hangisi?" to "Which sentence from this week would you like to keep telling yourself?")),
            ShortSeries("steps", "Küçük adımlar", "Small steps", "motivasyon", listOf(
                "Bugün başlayabileceğin en küçük şey ne?" to "What is the smallest thing you could start today?",
                "Bir işi kolaylaştırmak için ilk adımı nasıl küçültebilirsin?" to "How could you make the first step of a task smaller?",
                "Bugün hangi küçük ilerlemeyi fark ettin?" to "What small progress did you notice today?",
                "Plan aksarsa geri dönebileceğin basit bir adım ne olabilir?" to "What simple step could help you return if plans change?",
                "Devam etmene yardımcı olan bir şey ne?" to "What is one thing that helps you keep going?",
                "Bugün bitirmek yerine başlamayı seçebileceğin bir şey var mı?" to "Is there something you could choose to start rather than finish today?",
                "Önümüzdeki haftaya hangi küçük adımı taşımak istersin?" to "Which small step would you like to carry into next week?")),
            ShortSeries("focus", "Dikkatine alan aç", "Room for your attention", "derin_odak", listOf(
                "Bugün dikkatini vermek istediğin tek bir şey ne?" to "What is one thing you want to give your attention to today?",
                "Çevrende sadeleştirebileceğin küçük bir şey var mı?" to "Is there something small you could simplify around you?",
                "Dikkatin dağıldığında geri dönmene ne yardımcı olur?" to "What helps you return when your attention wanders?",
                "Hangi işi birkaç dakika boyunca tek başına yapabilirsin?" to "Which task could you do on its own for a few minutes?",
                "Kısa bir mola vermek için uygun bir an ne zaman?" to "When would be a good moment for a short break?",
                "Bugün hangi gereksiz geçişi azaltabilirsin?" to "Which unnecessary switch between tasks could you reduce today?",
                "Bu hafta sana iyi gelen hangi düzeni korumak istersin?" to "Which helpful routine from this week would you like to keep?"))
        )
    }
}
