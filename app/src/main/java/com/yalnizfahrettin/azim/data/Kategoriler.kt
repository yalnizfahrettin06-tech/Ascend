package com.yalnizfahrettin.azim.data

/**
 * Alt kategori. Kilit GRUP seviyesindedir — tek tek açılmaz.
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
 * Kategori grubu — kilit birimi.
 *
 * 60 alt kategoriyi tek tek reklamla açtırmak işkenceye dönerdi.
 * Bir reklam = bir grubun tamamı, ömür boyu. Kullanıcı için adil,
 * teklif olarak da net: "bir reklam, bir dünya".
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
            Triple("ozsefkat", "Kendime nazik davranmak", "Be kinder to myself"),
            Triple("ic_huzur", "Biraz yavaşlamak", "Find a moment of calm"),
            Triple("kendine_guven", "Kendime güvenmek", "Build trust in myself"),
        ),
        g("azim", "Azim & Dayanıklılık", "Grit & Endurance", ucretsiz = true,
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
            Triple("erteleme", "Erteleme", "Procrastination"),
            Triple("derin_odak", "Derin Odak", "Deep Focus"),
            Triple("durtu", "Dürtü Kontrolü", "Impulse Control"),
            Triple("rutin", "Rutin Kurmak", "Building Routines"),
            Triple("dagilma", "Dikkat Dağınıklığı", "Distraction"),
        ),
        g("cesaret", "Özgüven & Cesaret", "Confidence & Courage", ucretsiz = false,
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
        ),
        g("tasavvuf", "Tasavvuf & Doğu", "Sufism & East", ucretsiz = false,
            Triple("mevlana", "Mevlânâ", "Rumi"),
            Triple("yunus", "Yunus Emre", "Yunus Emre"),
            Triple("sems", "Şems", "Shams"),
            Triple("hafiz", "Hafız", "Hafez"),
            Triple("zen", "Zen", "Zen"),
        ),
        g("inanc", "İnanç", "Faith", ucretsiz = false,
            Triple("kuran", "Kur'an", "Quran"),
            Triple("incil", "İncil", "Bible"),
            Triple("tevrat", "Tevrat", "Torah"),
            Triple("dua", "Dua", "Prayer"),
            Triple("sukur", "Şükür", "Gratitude in Faith"),
        ),
        g("spor", "Spor & Beden", "Sports & Body", ucretsiz = false,
            Triple("antrenman", "Antrenman", "Training"),
            Triple("dayaniklilik", "Dayanıklılık", "Endurance"),
            Triple("sakatlik", "Sakatlıktan Dönüş", "Comeback from Injury"),
            Triple("sabah_rutini", "Sabah Rutini", "Morning Routine"),
            Triple("beslenme", "Beslenme Disiplini", "Nutrition Discipline"),
        ),
        g("is", "İş & Başarı", "Work & Success", ucretsiz = false,
            Triple("girisimcilik", "Girişimcilik", "Entrepreneurship"),
            Triple("kariyer", "Kariyer", "Career"),
            Triple("liderlik", "Liderlik", "Leadership"),
            Triple("para", "Para", "Money"),
            Triple("zaman", "Zaman Yönetimi", "Time Management"),
            Triple("basarisizlik", "Başarısızlık", "Failure"),
            Triple("basari", "Başarı", "Success"),
        ),
        g("iliskiler", "İlişkiler", "Relationships", ucretsiz = false,
            Triple("ask", "Aşk", "Love"),
            Triple("ayrilik", "Ayrılık", "Breakup"),
            Triple("aile", "Aile", "Family"),
            Triple("arkadaslik", "Arkadaşlık", "Friendship"),
            Triple("yalnizlik", "Yalnızlık", "Loneliness"),
            Triple("affetmek", "Affetmek", "Forgiveness"),
        ),
        g("zihin", "Zihin & Huzur", "Mind & Calm", ucretsiz = false,
            Triple("kaygi", "Kaygı", "Anxiety"),
            Triple("stres", "Stres", "Stress"),
            Triple("minnettarlik", "Minnettarlık", "Gratitude"),
            Triple("simdiki_an", "Şimdiki An", "The Present"),
            Triple("uyku", "Uyku", "Sleep"),
            Triple("karamsarlik", "Karamsarlık", "Pessimism"),
            Triple("huzur", "Huzur", "Serenity"),
        ),
        g("ogrenme", "Öğrenme & Gelişim", "Learning & Growth", ucretsiz = false,
            Triple("merak", "Merak", "Curiosity"),
            Triple("okumak", "Okumak", "Reading"),
            Triple("hata", "Hata Yapmak", "Making Mistakes"),
            Triple("aliskanlik", "Alışkanlık", "Habits"),
            Triple("sinav", "Sınav & Öğrencilik", "Exams & Study"),
        ),
    )

    val tumAltlar: List<Kategori> = gruplar.flatMap { it.altlar }
    val ucretsizGruplar: Set<String> = gruplar.filter { it.ucretsiz }.map { it.anahtar }.toSet()

    fun bul(anahtar: String): Kategori? = tumAltlar.firstOrNull { it.anahtar == anahtar }
    fun grupBul(anahtar: String): KategoriGrubu? = gruplar.firstOrNull { it.anahtar == anahtar }

    /** Bir grup açıksa altlarının hepsi açıktır. */
    fun acikAltlar(acikGruplar: Set<String>): Set<String> =
        gruplar.filter { it.anahtar in acikGruplar || it.ucretsiz }
            .flatMap { it.altlar }.map { it.anahtar }.toSet()

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
