package com.yalnizfahrettin.azim.data

/**
 * Her kategori bağımsız bir erişim ve bildirim seçimi birimidir.
 */
data class Kategori(
    val anahtar: String,
    val adTr: String,
    val adEn: String,
    val grup: String,
) {
    fun ad(dil: String) = if (dil == "en") adEn else adTr
}

/**
 * Konuları düzenleyen görsel aile. Yeni erişim kategori seviyesinde saklanır.
 *
 * The historical free flag is retained only to migrate previous entitlements.
 * It must not be used as a current access check; use Depo.acik instead.
 */
data class KategoriGrubu(
    val anahtar: String,
    val adTr: String,
    val adEn: String,
    val ucretsiz: Boolean,
    val altlar: List<Kategori>,
) {
    fun ad(dil: String) = if (dil == "en") adEn else adTr
}

object Kategoriler {

    private fun g(
        anahtar: String, tr: String, en: String, ucretsiz: Boolean = false,
        vararg altlar: Triple<String, String, String>,
    ) = KategoriGrubu(
        anahtar, tr, en, ucretsiz,
        altlar.map { (a, atr, aen) -> Kategori(a, atr, aen, anahtar) },
    )

    val gruplar: List<KategoriGrubu> = listOf(
        g("olumlamalar", "Günlük olumlamalar", "Daily affirmations", ucretsiz = true,
            Triple("kendini_affet", "Kendini affetmek", "Forgiving yourself"),
            Triple("ozsefkat", "Kendime nazik davranmak", "Be kinder to myself"),
            Triple("ic_huzur", "Biraz yavaşlamak", "Find a moment of calm"),
            Triple("kendine_guven", "Kendime güvenmek", "Build trust in myself"),
        ),
        g("azim", "Azim & Dayanıklılık", "Grit & Endurance", ucretsiz = true,
            Triple("beklemek", "Sonucu beklemek", "Waiting for results"),
            Triple("gelisimi_gormek", "İlerlediğini fark etmek", "Noticing progress"),
            Triple("kucuk_adim", "Küçük adımlarla ilerlemek", "Small steps"),
            Triple("dustukten_sonra", "Düştükten sonra kalkmak", "Getting back up"),
            Triple("motivasyon", "Motivasyon", "Motivation"),
            Triple("azim", "Azim", "Persistence"),
            Triple("pes", "Pes Etmemek", "Never Give Up"),
            Triple("zorluk_sabir", "Zorluğa Karşı Sabır", "Patience with Hardship"),
            Triple("yorgunluk_sabir", "Yorulmaya Karşı Sabır", "Patience with Fatigue"),
            Triple("tukenmislik", "Tükenmişlik", "Burnout"),
            Triple("yeniden", "Yeniden Başlamak", "Starting Over"),
            Triple("uzun_soluk", "Uzun Soluklu Hedefler", "Long-Term Goals"),
            Triple("umut", "Umut", "Hope"),
        ),
        g("disiplin", "Disiplin & Odak", "Discipline & Focus", ucretsiz = true,
            Triple("niyetine_sadik", "Niyetine sadık kalmak", "Staying true to your intention"),
            Triple("erteleme", "Erteleme", "Procrastination"),
            Triple("derin_odak", "Derin Odak", "Deep Focus"),
            Triple("durtu", "Dürtü Kontrolü", "Impulse Control"),
            Triple("rutin", "Rutin Kurmak", "Building Routines"),
            Triple("dagilma", "Dikkat Dağınıklığı", "Distraction"),
        ),
        g("cesaret", "Özgüven & Cesaret", "Confidence & Courage", ucretsiz = false,
            Triple("kendini_ifade", "Kendini ifade etmek", "Expressing yourself"),
            Triple("elestiri", "Eleştiriyle karşılaşmak", "Receiving criticism"),
            Triple("kiyas", "Kendini kıyaslamamak", "Beyond comparison"),
            Triple("onay", "Onay beklemeden yaşamak", "Living without approval"),
            Triple("ozguven", "Kendine Güven", "Self-Confidence"),
            Triple("korku", "Korkuyla Yüzleşme", "Facing Fear"),
            Triple("reddedilme", "Reddedilme", "Rejection"),
            Triple("risk", "Risk Almak", "Taking Risks"),
            Triple("utangaclik", "Utangaçlık", "Shyness"),
        ),
        g("filozoflar", "Filozoflar", "Philosophers", ucretsiz = true,
            Triple("marcus", "Marcus Aurelius", "Marcus Aurelius"),
            Triple("seneca", "Seneca", "Seneca"),
            Triple("epiktetos", "Epiktetos", "Epictetus"),
            Triple("platon", "Platon", "Plato"),
            Triple("aristoteles", "Aristoteles", "Aristotle"),
            Triple("nietzsche", "Nietzsche", "Nietzsche"),
            Triple("konfucyus", "Konfüçyüs", "Confucius"),
            Triple("machiavelli", "Machiavelli", "Machiavelli"),
            Triple("epikuros", "Epikuros", "Epicurus"),
            Triple("diogenes", "Diyojen", "Diogenes"),
            Triple("herakleitos", "Herakleitos", "Heraclitus"),
            Triple("cicero", "Cicero", "Cicero"),
            Triple("montaigne", "Michel de Montaigne", "Michel de Montaigne"),
            Triple("bacon", "Francis Bacon", "Francis Bacon"),
            Triple("descartes", "René Descartes", "René Descartes"),
            Triple("spinoza", "Baruch Spinoza", "Baruch Spinoza"),
            Triple("pascal", "Blaise Pascal", "Blaise Pascal"),
            Triple("locke", "John Locke", "John Locke"),
            Triple("hume", "David Hume", "David Hume"),
            Triple("rousseau", "Jean-Jacques Rousseau", "Jean-Jacques Rousseau"),
            Triple("kant", "Immanuel Kant", "Immanuel Kant"),
            Triple("schopenhauer", "Arthur Schopenhauer", "Arthur Schopenhauer"),
            Triple("kierkegaard", "Søren Kierkegaard", "Søren Kierkegaard"),
            Triple("emerson", "Ralph Waldo Emerson", "Ralph Waldo Emerson"),
            Triple("thoreau", "Henry David Thoreau", "Henry David Thoreau"),
        ),
        g("tasavvuf", "Tasavvuf & Doğu", "Sufism & East", ucretsiz = false,
            Triple("mevlana", "Mevlânâ", "Rumi"),
            Triple("yunus", "Yunus Emre", "Yunus Emre"),
            Triple("sems", "Şems", "Shams"),
            Triple("hafiz", "Hafız", "Hafez"),
            Triple("zen", "Zen", "Zen"),
            Triple("sadi", "Sâdî-i Şîrâzî", "Saadi"),
            Triple("attar", "Ferîdüddin Attâr", "Attar"),
            Triple("hayyam", "Ömer Hayyam", "Omar Khayyam"),
        ),
        g("inanc", "İnanç", "Faith", ucretsiz = false,
            Triple("kuran", "Kur'an üzerine düşünceler", "Reflections on the Quran"),
            Triple("incil", "İncil üzerine düşünceler", "Reflections on the Bible"),
            Triple("tevrat", "Tevrat üzerine düşünceler", "Reflections on the Torah"),
            Triple("dua", "Dua", "Prayer"),
            Triple("sukur", "Şükür", "Gratitude in Faith"),
        ),
        g("spor", "Spor & Beden", "Sports & Body", ucretsiz = false,
            Triple("beden", "Bedenine saygı duymak", "Respecting your body"),
            Triple("dinlenme", "Dinlenmeye izin vermek", "Permission to rest"),
            Triple("antrenman", "Antrenman", "Training"),
            Triple("dayaniklilik", "Dayanıklılık", "Endurance"),
            Triple("sakatlik", "Sakatlıktan Dönüş", "Comeback from Injury"),
            Triple("sabah_rutini", "Sabah Rutini", "Morning Routine"),
            Triple("beslenme", "Beslenme Disiplini", "Nutrition Discipline"),
        ),
        g("is", "İş & Başarı", "Work & Success", ucretsiz = false,
            Triple("is_sinir", "İş ile hayat arasında sınır", "Work and life boundaries"),
            Triple("girisimcilik", "Girişimcilik", "Entrepreneurship"),
            Triple("kariyer", "Kariyer", "Career"),
            Triple("liderlik", "Liderlik", "Leadership"),
            Triple("para", "Para", "Money"),
            Triple("zaman", "Zaman Yönetimi", "Time Management"),
            Triple("basarisizlik", "Başarısızlık", "Failure"),
            Triple("basari", "Başarı", "Success"),
        ),
        g("iliskiler", "İlişkiler", "Relationships", ucretsiz = false,
            Triple("onarim", "Özür dilemek ve onarmak", "Apologizing and repairing"),
            Triple("dinlemek", "Gerçekten dinlemek", "Really listening"),
            Triple("destek", "Destek istemek", "Asking for support"),
            Triple("vedalar", "Vedalar ve kayıplar", "Goodbyes and loss"),
            Triple("sinirlar", "Sınırlarını korumak", "Keeping boundaries"),
            Triple("hayir_demek", "Hayır diyebilmek", "Saying no"),
            Triple("ask", "Aşk", "Love"),
            Triple("ayrilik", "Ayrılık", "Breakup"),
            Triple("aile", "Aile", "Family"),
            Triple("arkadaslik", "Arkadaşlık", "Friendship"),
            Triple("yalnizlik", "Yalnızlık", "Loneliness"),
            Triple("affetmek", "Affetmek", "Forgiveness"),
        ),
        g("zihin", "Zihin & Huzur", "Mind & Calm", ucretsiz = false,
            Triple("tek_basina", "Kendi başına iyi vakit geçirmek", "Time on your own"),
            Triple("belirsizlik", "Belirsizlikle yaşamak", "Living with uncertainty"),
            Triple("pismanlik", "Pişmanlık", "Regret"),
            Triple("ofke", "Öfkeyle ne yapmak", "Responding to anger"),
            Triple("fazla_dusunmek", "Aynı düşüncede dönüp durmak", "Circling thoughts"),
            Triple("kontrol", "Kontrol edemediklerini bırakmak", "What you cannot control"),
            Triple("kararsizlik", "Kararsızlık", "Making a choice"),
            Triple("kaygi", "Kaygı", "Anxiety"),
            Triple("stres", "Stres", "Stress"),
            Triple("minnettarlik", "Minnettarlık", "Gratitude"),
            Triple("simdiki_an", "Şimdiki An", "The Present"),
            Triple("uyku", "Uyku", "Sleep"),
            Triple("karamsarlik", "Karamsarlık", "Pessimism"),
            Triple("huzur", "Huzur", "Serenity"),
        ),
        g("ogrenme", "Öğrenme & Gelişim", "Learning & Growth", ucretsiz = false,
            Triple("anlam", "Kendi anlamını bulmak", "Finding your meaning"),
            Triple("yaraticilik", "Yaratıcı cesaret", "Creative courage"),
            Triple("degisim", "Değişime uyum sağlamak", "Adapting to change"),
            Triple("mukemmeliyet", "Mükemmel olmak zorunda değilsin", "Good enough"),
            Triple("merak", "Merak", "Curiosity"),
            Triple("okumak", "Okumak", "Reading"),
            Triple("hata", "Hata Yapmak", "Making Mistakes"),
            Triple("aliskanlik", "Alışkanlık", "Habits"),
            Triple("sinav", "Sınav & Öğrencilik", "Exams & Study"),
        ),
    )

    val tumAltlar: List<Kategori> = gruplar.flatMap { it.altlar }
    // Discovery is a presentation layer; keep real groups for saved grants and plan filters.
    const val DUSUNURLER = "unlu_dusunurler"
    val dusunurler: List<Kategori> = tumAltlar.filter {
        it.grup == "filozoflar" || (it.grup == "tasavvuf" && it.anahtar != "zen")
    }
    val kesfetGruplari: List<KategoriGrubu> = gruplar.map { group ->
        when (group.anahtar) {
            "filozoflar" -> KategoriGrubu(DUSUNURLER, "Ünlü düşünürler", "Famous thinkers", false, dusunurler)
            "tasavvuf" -> group.copy(anahtar = "dogu_gelenegi", adTr = "Doğu geleneği", adEn = "Eastern tradition",
                altlar = group.altlar.filterNot { it in dusunurler })
            else -> group
        }
    }.filter { it.altlar.isNotEmpty() }

    fun kesfetGrupBul(key: String): KategoriGrubu? = kesfetGruplari.firstOrNull { it.anahtar == key } ?: grupBul(key)

    val ucretsizGruplar: Set<String> = gruplar.filter { it.ucretsiz }.map { it.anahtar }.toSet()

    fun bul(anahtar: String): Kategori? = tumAltlar.firstOrNull { it.anahtar == anahtar }
    fun grupBul(anahtar: String): KategoriGrubu? = gruplar.firstOrNull { it.anahtar == anahtar }

    private val eskiKategoriAnahtarlari = setOf("ozsefkat", "ic_huzur", "kendine_guven", "motivasyon", "azim", "pes", "zorluk_sabir", "yorgunluk_sabir", "tukenmislik", "yeniden", "uzun_soluk", "umut", "erteleme", "derin_odak", "durtu", "rutin", "dagilma", "ozguven", "korku", "reddedilme", "risk", "utangaclik", "marcus", "seneca", "epiktetos", "platon", "aristoteles", "nietzsche", "konfucyus", "machiavelli", "mevlana", "yunus", "sems", "hafiz", "zen", "kuran", "incil", "tevrat", "dua", "sukur", "antrenman", "dayaniklilik", "sakatlik", "sabah_rutini", "beslenme", "girisimcilik", "kariyer", "liderlik", "para", "zaman", "basarisizlik", "basari", "ask", "ayrilik", "aile", "arkadaslik", "yalnizlik", "affetmek", "kaygi", "stres", "minnettarlik", "simdiki_an", "uyku", "karamsarlik", "huzur", "merak", "okumak", "hata", "aliskanlik", "sinav")

    /** Expands explicit legacy group grants; current screens must use Depo.acik. */
    fun acikAltlar(acikGruplar: Set<String>): Set<String> =
        gruplar.filter { it.anahtar in acikGruplar }
            .flatMap { it.altlar }.filter { it.anahtar in eskiKategoriAnahtarlari }.map { it.anahtar }.toSet() + Erisim.ucretsizKategoriler

    /** Kurulumda seçili gelen alt kategoriler. */
    val varsayilanSecili: Set<String> = setOf("motivasyon", "ozsefkat", "marcus")

    /**
     * Eski düz kategori anahtarlarından yeni alt kategorilere göç.
     * Eşleşmeyen anahtar sessizce düşer.
     */
    val eskiEslesme: Map<String, String> = mapOf(
        "sabir" to "zorluk_sabir",
        "odak" to "derin_odak",
        "stoacilik" to "epiktetos",
        "marcus_aurelius" to "marcus",
        "epictetus" to "epiktetos",
        "felsefe" to "platon",
        "saglik" to "beslenme",
        "kararlilik" to "azim",
        "tutku" to "motivasyon",
        "cesaret" to "korku",
        "ogrenme" to "merak",
    )

    fun gocur(eskiler: Set<String>): Set<String> =
        eskiler.mapNotNull { e ->
            when {
                bul(e) != null -> e
                else -> eskiEslesme[e]
            }
        }.toSet()
}
