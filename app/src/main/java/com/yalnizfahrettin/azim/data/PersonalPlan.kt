package com.yalnizfahrettin.azim.data

import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDate
import kotlin.random.Random

/** Only explicit app preferences. This is not a psychological assessment. */
data class PersonalProfile(
    val answers: Map<String, Set<String>> = emptyMap(),
    val name: String = "",
    val step: Int = 0,
    val dailyCount: Int = 3,
    val startHour: Int = 9,
    val endHour: Int = 21,
    val setupVersion: Int = 2,
) {
    fun answer(key: String): Set<String> = answers[key].orEmpty()
    fun choose(key: String, value: String, multiple: Boolean = false): PersonalProfile {
        val next = if (multiple) answer(key).let { if (value in it) it - value else it + value } else setOf(value)
        return copy(answers = answers + (key to next))
    }
    fun skip(key: String) = copy(answers = answers - key)
    fun encode(): String {
        fun enc(s: String) = URLEncoder.encode(s, "UTF-8")
        return listOf("v=1", "setup=$setupVersion", "name=${enc(name.take(40))}", "step=$step", "count=$dailyCount", "start=$startHour", "end=$endHour")
            .plus(answers.toSortedMap().map { (key, value) -> "a.${enc(key)}=${enc(value.sorted().joinToString(","))}" }).joinToString("&")
    }
    companion object {
        fun decode(raw: String?): PersonalProfile = runCatching {
            if (raw == null) return PersonalProfile()
            val map = raw.split('&').associate { item ->
                URLDecoder.decode(item.substringBefore('='), "UTF-8") to URLDecoder.decode(item.substringAfter('=', ""), "UTF-8")
            }
            val start = map["start"]?.toIntOrNull()?.coerceIn(0, 23) ?: 9
            PersonalProfile(
                answers = map.filterKeys { it.startsWith("a.") }.mapKeys { it.key.removePrefix("a.") }
                    .mapValues { it.value.split(',').filter(String::isNotBlank).toSet() },
                setupVersion = map["setup"]?.toIntOrNull() ?: 1,
                name = map["name"].orEmpty().take(40), step = (map["step"]?.toIntOrNull() ?: 0).coerceIn(0, PersonalPlan.LAST_STEP),
                dailyCount = (map["count"]?.toIntOrNull() ?: 3).coerceIn(1, 7), startHour = start,
                endHour = (map["end"]?.toIntOrNull() ?: 21).coerceIn(start + 1, 24),
            )
        }.getOrDefault(PersonalProfile())
    }
}

data class PlanOption(val id: String, val tr: String, val en: String, val categories: Set<String> = emptySet()) {
    fun label(language: String) = if (language == "tr") tr else en
}
data class PlanQuestion(val id: String, val tr: String, val en: String, val hintTr: String, val hintEn: String,
    val options: List<PlanOption>, val multiple: Boolean = false) {
    fun title(language: String) = if (language == "tr") tr else en
    fun hint(language: String) = if (language == "tr") hintTr else hintEn
}

/** The same explicit ranking/filter policy powers setup previews, the feed and reminders. */
object PersonalPlan {
    const val LAST_STEP = 19
    private fun option(id: String, tr: String, en: String, categories: String = "") =
        PlanOption(id, tr, en, categories.split(' ').filter(String::isNotBlank).toSet())
    val questions = listOf(
        PlanQuestion("format", "Sana nasıl seslenelim?", "What would you like to read?", "Sözlerin biçimini seç. İstersen hepsini karıştırabiliriz.", "Choose a style, or leave room for a mix.", listOf(
            option("affirmation", "Kendime söyleyeceğim olumlamalar", "Affirmations I can say to myself"),
            option("motivation", "Harekete geçiren sözler", "Words that help me take action"),
            option("reflection", "Felsefe ve düşündüren sözler", "Philosophy and reflections"),
            option("mixed", "Hepsinden biraz", "A little of everything"))),
        PlanQuestion("goal", "Şu sıralar neye yer açıyorsun?", "What are you making room for?", "Planının ana yönünü buradan oluşturacağız.", "This will shape the main direction of your plan.", listOf(
            option("action", "Bir adım atmak", "Taking a step", "motivasyon erteleme azim yeniden"),
            option("calm", "İçimde biraz sakinlik", "A little inner calm", "ic_huzur huzur simdiki_an stres"),
            option("confidence", "Kendime güvenmek", "Trusting myself", "ozsefkat kendine_guven ozguven korku"),
            option("perspective", "Yeni bir bakış kazanmak", "A fresh perspective", "marcus epiktetos merak zen"))),
        PlanQuestion("energy", "Bu dönemde enerjin nasıl?", "How is your energy these days?", "Sözlerini buna göre sıralayacağız. İstediğin zaman değiştir.", "This shapes your words. Change it whenever you need.", listOf(
            option("low", "Biraz yavaşlamaya ihtiyacım var", "I need to slow down", "ic_huzur ozsefkat yorgunluk_sabir tukenmislik"),
            option("steady", "Sakin ve dengeliyim", "Calm and steady", "marcus minnettarlik derin_odak"),
            option("ready", "Başlamaya hazırım", "Ready to begin", "motivasyon azim risk antrenman"))),
        PlanQuestion("tone", "Hangi ses sana daha yakın?", "Which voice feels closer?", "Sana eşlik eden sözlerin yönünü belirler.", "This shapes the kind of support you see.", listOf(
            option("gentle", "Nazik bir hatırlatma", "A gentle reminder", "ozsefkat ic_huzur umut affetmek"),
            option("direct", "Net bir harekete çağrı", "A clear call to action", "motivasyon azim erteleme rutin derin_odak"),
            option("thoughtful", "Üzerinde düşüneceğim bir fikir", "An idea to think about", "marcus seneca epiktetos platon merak"))),
        PlanQuestion("challenge", "Başlamanı en çok ne zorlaştırıyor?", "What makes starting difficult?", "Sadece kendine yakın geleni seç.", "Choose what feels closest to you.", listOf(
            option("delay", "Sürekli ertelemek", "Putting things off", "erteleme motivasyon rutin"),
            option("noise", "Dikkatimin dağılması", "Distractions", "derin_odak dagilma durtu"),
            option("doubt", "Kendimden şüphe etmek", "Doubting myself", "ozsefkat kendine_guven ozguven korku"),
            option("tired", "Yorgun hissetmek", "Feeling tired", "ic_huzur yorgunluk_sabir tukenmislik"))),
        PlanQuestion("momentum", "Kendi yolunun neresindesin?", "Where are you on your path?", "Başlamakla devam etmek aynı desteği gerektirmeyebilir.", "Beginning and continuing can need different words.", listOf(
            option("start", "Yeni başlıyorum", "Just beginning", "motivasyon yeniden erteleme"),
            option("continue", "Devam etmeye çalışıyorum", "Working on consistency", "azim pes rutin uzun_soluk"),
            option("return", "Bir aradan sonra dönüyorum", "Returning after a pause", "ozsefkat yeniden umut hata"))),
        PlanQuestion("context", "Günlük hayatında neresi öne çıkıyor?", "Which part of life is in focus?", "Birden fazla seçebilirsin. Bu alanlar önerilerini etkiler.", "Choose more than one. These areas shape recommendations.", listOf(
            option("work", "İş ve üretmek", "Work and creating", "derin_odak kariyer zaman girisimcilik"),
            option("study", "Öğrenmek ve okul", "Learning and study", "merak sinav okumak derin_odak"),
            option("self", "Kendimle ilişkim", "My relationship with myself", "ozsefkat ic_huzur kendine_guven"),
            option("people", "İnsanlarla bağlarım", "My connections with others", "aile arkadaslik affetmek yalnizlik"),
            option("body", "Hareket ve alışkanlıklar", "Movement and habits", "antrenman aliskanlik sabah_rutini")), true),
        PlanQuestion("values", "Sana güç veren ne?", "What gives you strength?", "Sana yakın değerlerden bir karışım oluşturacağız.", "We will build a mix around what matters to you.", listOf(
            option("growth", "Dünden bir şey öğrenmek", "Learning from yesterday", "merak hata okumak"),
            option("connection", "Birine iyi gelmek", "Being there for someone", "aile arkadaslik ozsefkat"),
            option("freedom", "Kendi yolumu seçmek", "Choosing my own path", "nietzsche risk ozguven"),
            option("patience", "Emek verdiğimi görmek", "Seeing my effort grow", "azim uzun_soluk zorluk_sabir")), true),
        PlanQuestion("inspiration", "Nasıl bir bakış seni besler?", "What kind of perspective inspires you?", "Bildirimlerin yalnızca aynı konuya dönüp durmasın.", "Your reminders can draw from more than one perspective.", listOf(
            option("practical", "Günlük hayattan küçük fikirler", "Small, practical ideas", "motivasyon derin_odak rutin"),
            option("wisdom", "Zamana yayılan düşünceler", "Thoughts that invite reflection", "marcus seneca epiktetos aristoteles"),
            option("kindness", "İçimde daha nazik bir ses", "A kinder inner voice", "ozsefkat ic_huzur minnettarlik"))),
        PlanQuestion("length", "Bir söz için ne kadar alanın var?", "How much space do you have for a quote?", "Kısalar öne gelsin ya da düşünce biraz daha açılsın.", "Prefer a quick line, or allow a little more depth.", listOf(
            option("short", "Kısa, bir bakışta okuyayım", "Short, at a glance"),
            option("long", "Biraz derinleşebilirim", "I have room for some depth"),
            option("any", "İkisi de olur", "Either is good"))),
        PlanQuestion("discovery", "Sürpriz bir söze yer var mı?", "Room for a little surprise?", "Yeni konular yalnızca erişimin olan içeriklerden gelir.", "New topics come only from content you can access.", listOf(
            option("none", "Planımdaki konularda kal", "Stay with my plan"),
            option("balanced", "Arada farklı bir bakış getir", "Occasionally bring a new perspective"),
            option("wide", "Keşfetmeyi seviyorum", "I enjoy discovering new things"))),
        PlanQuestion("avoid", "Şimdilik görmek istemediğin var mı?", "Anything you would rather skip?", "Seçtiklerin akışından ve bildirimlerinden çıkarılır.", "Selected topics stay out of your feed and reminders.", listOf(
            option("relationships", "Aşk ve ayrılık", "Love and breakup", "ask ayrilik"),
            option("work", "İş, başarı ve para", "Work, success and money", "kariyer para basari basarisizlik girisimcilik liderlik"),
            option("body", "Beden ve beslenme", "Body and nutrition", "beslenme sakatlik antrenman dayaniklilik"),
            option("hardship", "Zorluk ve tükenmişlik", "Hardship and burnout", "tukenmislik yorgunluk_sabir zorluk_sabir")), true),
        PlanQuestion("spirituality", "Manevi düşüncelere yer verelim mi?", "Would you like spiritual reflections?", "İnanç ve tasavvuf içerikleri ancak sen istersen karışıma girer.", "Faith and spiritual topics enter your mix only if you choose.", listOf(
            option("no", "Daha genel bir dil tercih ederim", "I prefer a general perspective"),
            option("spiritual", "Tasavvuf ve Doğu düşüncesi olabilir", "Sufi and Eastern reflections are welcome"),
            option("faith", "İnanç üzerine düşünceler de olsun", "Include reflections on faith too"))),
    )

    private val reflectionGroups = setOf("filozoflar", "tasavvuf", "inanc")
    private fun allowed(profile: PersonalProfile, category: Kategori, excluded: Set<String>): Boolean {
        if (category.anahtar in excluded) return false
        val spiritual = profile.answer("spirituality")
        if (category.grup == "inanc" && "faith" !in spiritual) return false
        if (category.grup == "tasavvuf" && spiritual.none { it == "faith" || it == "spiritual" }) return false
        return when (profile.answer("format").firstOrNull()) {
            "affirmation" -> category.grup == "olumlamalar"
            "reflection" -> category.grup in reflectionGroups
            "motivation" -> category.grup !in reflectionGroups && category.grup != "olumlamalar"
            else -> true
        }
    }

    fun weights(profile: PersonalProfile): Map<String, Int> {
        val excluded = questions.first { it.id == "avoid" }.options.filter { it.id in profile.answer("avoid") }.flatMap { it.categories }.toSet()
        val chosen = questions.filterNot { it.id == "avoid" }.flatMap { question ->
            question.options.filter { it.id in profile.answer(question.id) }.map {
                it.categories to (if (question.id == "goal") 12 else if (question.id == "tone") 8 else 5)
            }
        }
        return Kategoriler.tumAltlar.filter { allowed(profile, it, excluded) }.associate { category ->
            category.anahtar to (1 + chosen.sumOf { (categories, weight) -> if (category.anahtar in categories) weight else 0 })
        }
    }
    private val starterOrder = listOf("motivasyon", "ozsefkat", "marcus")
    private fun recommended(scores: Map<String, Int>): List<String> = scores.entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }
            .thenBy { starterOrder.indexOf(it.key).takeIf { index -> index >= 0 } ?: starterOrder.size }
            .thenBy { it.key }).map { it.key }
    fun recommendedCategories(profile: PersonalProfile): List<String> = recommended(weights(profile))

    fun initialCategories(profile: PersonalProfile, access: Set<String>): Set<String> =
        recommendedCategories(profile).filter { it in access }.take(3).toSet()

    fun effectiveCategories(profile: PersonalProfile?, selected: Set<String>, access: Set<String>): Set<String> {
        if (profile == null) return selected.intersect(access)
        return effectiveCategories(profile, selected, access, weights(profile))
    }
    private fun effectiveCategories(profile: PersonalProfile, selected: Set<String>, access: Set<String>, scores: Map<String, Int>): Set<String> {
        val valid = scores.keys.intersect(access)
        val base = selected.intersect(valid).ifEmpty { recommended(scores).filter { it in access }.take(3).toSet() }
        return if ("none" in profile.answer("discovery")) base else valid
    }

    private fun boostedWeights(profile: PersonalProfile, selected: Set<String>, scores: Map<String, Int>) =
        scores.mapValues { (category, score) -> score * if (category !in selected) 1 else if ("wide" in profile.answer("discovery")) 2 else 5 }

    fun feed(profile: PersonalProfile?, selected: Set<String>, access: Set<String>, seen: Set<String> = emptySet()): List<Soz> {
        if (profile == null) return Sozler.akis(selected.intersect(access), seen)
        val scores = weights(profile)
        val categories = effectiveCategories(profile, selected, access, scores)
        val boosted = boostedWeights(profile, selected, scores)
        // Progress and the greeting name must not reshuffle the first quote after setup.
        val random = Random((LocalDate.now().toEpochDay().toInt() * 31) + profile.copy(name = "", step = 0).encode().hashCode())
        val pool = Sozler.tumu().filter { it.kategori in categories }
        // Weighted shuffle, without duplicate cards; unseen cards always precede seen cards.
        return pool.map { quote ->
            val length = when (profile.answer("length").firstOrNull()) {
                "short" -> if (quote.en.length <= 115) 2.0 else .65
                "long" -> if (quote.en.length > 115) 2.0 else .75
                else -> 1.0
            }
            Triple(quote, quote.kimlik in seen, -kotlin.math.ln(random.nextDouble().coerceAtLeast(.00001)) / (boosted.getValue(quote.kategori) * length))
        }.sortedWith(compareBy<Triple<Soz, Boolean, Double>> { it.second }.thenBy { it.third }).map { it.first }
    }

    fun notification(profile: PersonalProfile?, selected: Set<String>, access: Set<String>, language: String,
        seen: Set<String>, lastId: String?): BildirimSecimi? {
        val scores = profile?.let(::weights).orEmpty()
        val categories = if (profile == null) selected.intersect(access) else effectiveCategories(profile, selected, access, scores)
        val pool = Sozler.bildirimHavuzu(categories, language)
        if (pool.isEmpty()) return null
        val fresh = pool.filterNot { it.kimlik in seen }
        val cycle = fresh.isEmpty()
        val candidates = fresh.ifEmpty { pool }.let { list -> list.filterNot { it.kimlik == lastId }.ifEmpty { list } }
        if (profile == null) return BildirimSecimi(candidates.random(), cycle)
        val boosted = boostedWeights(profile, selected, scores)
        val weighted = candidates.map { quote ->
            val lengthBoost = when (profile.answer("length").firstOrNull()) {
                "short" -> if (quote.metin(language).length <= 115) 2 else 1
                "long" -> if (quote.metin(language).length > 115) 2 else 1
                else -> 1
            }
            quote to boosted.getValue(quote.kategori) * lengthBoost
        }
        var position = Random.nextInt(weighted.sumOf { it.second }.coerceAtLeast(1))
        val quote = weighted.firstOrNull { (_, weight) -> position -= weight; position < 0 }?.first ?: candidates.first()
        return BildirimSecimi(quote, cycle)
    }

    fun summary(profile: PersonalProfile, language: String): List<String> {
        fun text(tr: String, en: String) = if (language == "tr") tr else en
        fun label(key: String) = questions.first { it.id == key }.options.filter { it.id in profile.answer(key) }.joinToString { it.label(language) }
        return buildList {
            listOf("format", "goal", "tone").forEach { key -> label(key).takeIf(String::isNotBlank)?.let(::add) }
            add(when (profile.answer("discovery").firstOrNull()) {
                "none" -> text("Sadece planındaki konular", "Only the topics in your plan")
                "wide" -> text("Yeni konulara daha çok yer", "More room for new topics")
                else -> text("Seçtiğin konular öncelikli; açık konulardan yeni sözler de gelir", "Selected topics have priority; other unlocked topics also appear")
            })
            label("avoid").takeIf(String::isNotBlank)?.let { add(text("Gösterilmez: $it", "Excluded: $it")) }
            add(text("Yanıtların uygulamada saklanır; istediğin zaman değiştirilebilir.", "Your answers are saved in the app and can be changed anytime."))
        }
    }
}
