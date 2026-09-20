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

/** Curated stable IDs: catalog reordering cannot silently alter a series. */
data class ShortSeries(val id: String, val tr: String, val en: String, val category: String,
    val prompts: List<Pair<String,String>>, val quoteIds: List<String>, val description: Pair<String,String>, val pro: Boolean = false) {
    fun title(dil: String) = if(id == "restart") RestartSeries.title(dil) else Diller.metin(dil,tr,en)
    fun summary(dil: String) = if(id == "restart") RestartSeries.summary(dil) else Diller.metin(dil,description.first,description.second)
    val quotes get() = quoteIds.map { requireNotNull(Sozler.kimlikten(it)) { "Missing series quote: $it" } }
    fun prompt(index: Int, dil: String) = if(id == "restart") RestartSeries.days(dil)[index].step else prompts[index].let { Diller.metin(dil,it.first,it.second) }
    companion object {
        /** One stable continuation target, independent of map iteration order. */
        fun active(progress: Map<String, SeriesProgress>): SeriesProgress? = progress.values
            .filter { it.completed < 7 && valueExists(it.id) }
            .sortedWith(compareByDescending<SeriesProgress> { it.lastDay ?: LocalDate.MIN }.thenBy { it.id })
            .firstOrNull()
        private fun valueExists(id: String) = all.any { it.id == id }
        val all = listOf(
            ShortSeries("restart", "Yeniden Başlamak", "Begin Again", "motivasyon",
                List(7) { RestartSeries.days("tr")[it].step to RestartSeries.days("en")[it].step },
                listOf("v5_yeniden_02","v5_motivasyon_07","v5_erteleme_01","v5_erteleme_10","v5_rutin_04","v5_motivasyon_08","v5_rutin_05"),
                "Yedi günde yeniden yer aç." to "Make room to begin again in seven days.", pro = true),
            ShortSeries("kindness","Kendine daha nazik","A little kinder to yourself","ozsefkat",listOf(
                "Bugün iyi görünmeye çalışmadan kabul edebileceğin duygu ne?" to "What feeling could you acknowledge today without trying to look fine?",
                "İçindeki sert cümleyi bir arkadaşına söyleseydin nasıl değiştirirdin?" to "How would you change that harsh sentence if you were speaking to a friend?",
                "Yapılacaklar listende görünmeyen hangi yükü bugün taşıdın?" to "What burden did you carry today that does not appear on your to-do list?",
                "Bir hatanı kendine hakaret etmeden nasıl anlatabilirsin?" to "How could you describe a mistake without insulting yourself?",
                "Geçmişteki kendinin o gün bilmediği neyi şimdi biliyorsun?" to "What do you know now that your past self did not know then?",
                "Bugün dinlenmek için kendine ayırabileceğin küçük bir zaman var mı?" to "Is there a small stretch of time you could set aside to rest today?",
                "Bu haftadan, başarı sayısına bağlı olmayan hangi niteliğini yanında götürmek istersin?" to "Which quality of yours, unrelated to achievement, would you like to carry forward from this week?"
            ), listOf("v5_ozsefkat_08","v5_ozsefkat_05","v5_ozsefkat_02","v5_ozsefkat_03","v5_ozsefkat_06","v5_ozsefkat_07","v5_ozsefkat_10"), "Kendi dilini yumuşat; bir haftada kendine daha anlayışlı yaklaşmayı dene." to "Soften your inner voice; spend a week practicing understanding toward yourself."),
            ShortSeries("steps","Küçük adımlar","Small steps","motivasyon",listOf(
                "Bu hedefin hangi kısmını gerçekten sen istiyorsun?" to "Which part of this goal do you truly want for yourself?",
                "Uzakta duran hedefin için bugün yapabileceğin küçük bakım ne?" to "What small act of care could you give your distant goal today?",
                "Boş sayfaya yazabileceğin tek bir düşünce ne?" to "What is one thought you could put on the blank page?",
                "İlk denemen için bugün nerede ve ne zaman yer açabilirsin?" to "Where and when could you make room for a first attempt today?",
                "İstek gelmiyorsa bu işin hangi küçük parçası merakını uyandırıyor?" to "If motivation is absent, what small part of this work makes you curious?",
                "Henüz bitirmesen de bu denemeden ne öğrendin?" to "What have you learned from this attempt, even if it is not finished?",
                "Bu hafta attığın hangi küçük adımı birine anlatmak istersin?" to "Which small step from this week would you like to tell someone about?"
            ), listOf("v5_motivasyon_07","v5_motivasyon_10","v5_motivasyon_01","v5_motivasyon_09","v5_motivasyon_03","v5_motivasyon_08","v5_motivasyon_04"), "Sana ait bir neden bul, ilk adımı küçült ve ilerlemeni fark et." to "Find a reason of your own, make the first step smaller and notice your progress."),
            ShortSeries("focus","Dikkatine alan aç","Room for your attention","derin_odak",listOf(
                "Bugünkü çalışmanın cevaplamasını istediğin tek soru ne?" to "What one question would you like today’s work to answer?",
                "Şimdi kapatabileceğin gereksiz bir pencere veya sekme var mı?" to "Is there an unnecessary window or tab you could close now?",
                "Bu oturum bittiğinde hangi küçük parçanın tamamlanması yeterli?" to "What small piece would be enough to finish in this session?",
                "Aklına gelen başka işi nereye not edip çalışmana dönebilirsin?" to "Where could you note that other task so you can return to your work?",
                "Bugünkü odaklanma sürene hangi bitiş saatini koyabilirsin?" to "What ending time could you set for today’s period of focus?",
                "Dikkatin yorulduğunda verebileceğin kısa mola nasıl olurdu?" to "What would a short break look like when your attention gets tired?",
                "Gelecek hafta çalışma alanını korumak için çevrendekilerden ne isteyebilirsin?" to "What could you ask of people around you to protect your work time next week?"
            ), listOf("v5_derin_odak_01","v5_derin_odak_02","v5_derin_odak_07","v5_derin_odak_04","v5_derin_odak_05","v5_derin_odak_10","v5_derin_odak_08"), "Dikkatine sınır çiz; dağıldığında dönmek ve yorulduğunda durmak için alan aç." to "Give your attention a boundary; make room to return when distracted and pause when tired."),
        )
    }
}
