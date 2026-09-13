package com.yalnizfahrettin.azim.data

/** One registry for onboarding, preferences, content and background workers. */
object Diller {
    val secenekler = listOf("tr" to "Türkçe", "en" to "English", "pt" to "Português",
        "de" to "Deutsch", "fr" to "Français", "it" to "Italiano", "ru" to "Русский")
    val kodlar = secenekler.map { it.first }.toSet()
    fun normalize(code: String): String = code.lowercase(java.util.Locale.ROOT).substringBefore('-').substringBefore('_')
        .takeIf { it in kodlar } ?: "en"

    private data class Pattern(val regex: Regex, val target: String, val slots: List<String>)
    private val patterns = mutableMapOf<String, List<Pattern>>()
    private val results = object : LinkedHashMap<Pair<String, String>, String>(256, .75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Pair<String, String>, String>?) = size > 512
    }
    private val placeholder = Regex("\\{[0-9]+}")

    fun metin(dil: String, tr: String, en: String): String {
        val lang = normalize(dil)
        if (lang == "tr") return tr
        if (lang == "en") return en
        val table = LocaleCatalog.ui(lang)
        table[en]?.let { return it }
        // Legacy bilingual call sites evaluate interpolation before this boundary.
        // Match only validated templates; inserted values are never interpreted as replacement code.
        return synchronized(results) {
            results.getOrPut(lang to en) {
                val templates = patterns.getOrPut(lang) {
                    table.entries.filter { placeholder.containsMatchIn(it.key) }
                        .sortedByDescending { placeholder.replace(it.key, "").length }
                        .map { (source, target) ->
                            val slots = placeholder.findAll(source).toList()
                            var end = 0
                            val pattern = buildString {
                                append("^")
                                slots.forEach { m ->
                                    append(Regex.escape(source.substring(end, m.range.first)))
                                    append("([\\s\\S]*?)")
                                    end = m.range.last + 1
                                }
                                append(Regex.escape(source.substring(end))); append("$")
                            }
                            Pattern(Regex(pattern), target, slots.map { it.value })
                        }
                }
                templates.firstNotNullOfOrNull { p ->
                    p.regex.matchEntire(en)?.let { match ->
                        val arguments = p.slots.mapIndexed { index, slot -> slot to match.groupValues[index + 1] }.toMap()
                        placeholder.replace(p.target) { arguments[it.value] ?: it.value }
                    }
                } ?: en
            }
        }
    }
    fun soz(dil: String, id: String): String? = LocaleCatalog.quotes(normalize(dil))[id]
}
