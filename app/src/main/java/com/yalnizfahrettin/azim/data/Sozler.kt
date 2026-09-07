package com.yalnizfahrettin.azim.data

/**
 * Bir söz, desteklenen her dilde ayrı metin taşır.
 *
 * Rapor 5.1: Önceki sürümde arayüz İngilizce'ye çevrilebiliyordu ama sözlerin
 * hepsi Türkçe'ydi — "English" seçen kullanıcı Türkçe söz okuyordu. Bu bir
 * arayüz kusuru değil, üründe tutulmayan bir vaatti. Artık metin dile bağlı.
 */
data class Soz(
    val tr: String,
    val en: String,
    val yazar: String,
    val kategori: String,
) {
    fun metin(dil: String): String = if (dil == "en") en else tr

    /** Kimlik: dilden bağımsız, geçmiş takibi ve favoriler için. */
    val kimlik: String get() = "$kategori:${tr.hashCode()}"

    fun bildirimeUygun(dil: String) = metin(dil).length <= Sozler.BILDIRIM_SINIRI

    /** Bu söz o dilde bildirimde kısaltılacak mı? */
    fun kisaltilirMi(dil: String) = metin(dil).length > Sozler.BILDIRIM_SINIRI

    /**
     * Bildirimde gösterilecek metin.
     *
     * Sınırın altındaki sözler olduğu gibi geçer. Uzun sözler KELİME
     * SINIRINDA kesilir ve "…" eklenir — kesilme kaza değil niyet olur,
     * haber uygulamalarının "devamını oku" deseni gibi. Kullanıcı
     * bildirime dokununca uygulama o sözün TAM metnine açılır
     * (MainActivity zaten EXTRA_KIMLIK ile bunu yapıyor).
     *
     * Böylece Kur'an/İncil ayetleri, uzun felsefe alıntıları gibi doğası
     * gereği uzun içerikler bildirim havuzundan sessizce dışlanmaz.
     */
    fun bildirimMetni(dil: String): String {
        val tam = metin(dil)
        if (tam.length <= Sozler.BILDIRIM_SINIRI) return tam
        val kirpik = tam.take(Sozler.KISALTMA_UZUNLUGU)
        val bosluk = kirpik.lastIndexOf(' ')
        val govde = if (bosluk > Sozler.KISALTMA_UZUNLUGU / 2) kirpik.take(bosluk) else kirpik
        return govde.trimEnd(' ', ',', ';', ':', '.', '-', '—') + "…"
    }
}

/*
 * İÇERİK HAVUZU
 *
 * TEK KURAL: her sözün HER DİLDEKİ metni BILDIRIM_SINIRI'nı aşmamalı.
 * SozUzunlukTest bunu zorlar — sınırı aşan bir söz eklenirse
 * `./gradlew test` kırmızı yanar ve kırpılmış bildirim üretime çıkamaz.
 */
object Sozler {

    /**
     * Bildirim metni için karakter tavanı.
     *
     * Gerekçe: toplu (collapsed) bildirimde gövde tek satıra kırpılır —
     * stok Android'de ~90, Samsung One UI gibi kabuklarda ~45-50 karakter.
     * BigTextStyle genişletilmiş alanda 5120 karaktere kadar gösterir ama
     * bildirimi zorla açık göstermenin API'si yoktur. 120 karakter =
     * genişletilmiş görünümde en fazla 3 satır, kırpılma yok.
     */
    const val BILDIRIM_SINIRI = 120
    const val IDEAL_SINIR = 90

    /** Kısaltılan sözlerin gövde uzunluğu. "…" ile birlikte sınırın altında kalır. */
    const val KISALTMA_UZUNLUGU = 100

    /**
     * Havuza kabul edilen mutlak tavan. Bunun üstü bir söz değil, paragraftır;
     * ne bildirimde ne de söz kartında düzgün durur.
     */
    const val HAVUZ_TAVANI = 400

    private val havuz: List<Soz> = listOf(
        // --- Azim & Dayanıklılık: yeni alt başlıklar ---
        Soz("Yorgunluk bir işaret, bir emir değil.",
            "Fatigue is a signal, not an order.", "Anonim", "yorgunluk_sabir"),
        Soz("Bedenin durmak isterken zihnin karar verir.",
            "When the body wants to stop, the mind decides.", "Anonim", "yorgunluk_sabir"),
        Soz("Bugün az yaptın ama yaptın; sayılan bu.",
            "You did little today, but you did it. That counts.", "Anonim", "yorgunluk_sabir"),
        Soz("Yeniden başlamak, hiç başlamamış olmaktan iyidir.",
            "Starting again beats never having started.", "Anonim", "yeniden"),
        Soz("Sıfırdan değil, deneyimden başlıyorsun.",
            "You are not starting from zero, but from experience.", "Anonim", "yeniden"),
        Soz("Her sabah yeni bir ilk gündür.",
            "Every morning is a new first day.", "Anonim", "yeniden"),
        Soz("Uzak hedef, yakın adımlarla yürünür.",
            "A distant goal is walked with near steps.", "Anonim", "uzun_soluk"),
        Soz("Bir yıl sonra, bugün başlamış olmayı isteyeceksin.",
            "A year from now you will wish you had started today.", "Anonim", "uzun_soluk"),
        Soz("Zorluk seni değil, kararını sınar.",
            "Hardship tests your decision, not you.", "Anonim", "zorluk_sabir"),

        // --- Disiplin & Odak ---
        Soz("Yarın yapacağım demek, bugün yapmamanın kibar hâlidir.",
            "Saying tomorrow is the polite way of not doing it today.", "Anonim", "erteleme"),
        Soz("Beş dakika başla; gerisi kendiliğinden gelir.",
            "Start for five minutes; the rest follows on its own.", "Anonim", "erteleme"),
        Soz("Erteleme işi büyütmez, sadece korkuyu büyütür.",
            "Delay does not grow the task, only the fear.", "Anonim", "erteleme"),
        Soz("Zor olanı önce yap, günün geri kalanı hafifler.",
            "Do the hard thing first and the day gets lighter.", "Anonim", "erteleme"),
        Soz("Derin iş, sessizlik ister; gürültü hep bahane bulur.",
            "Deep work needs silence; noise always finds an excuse.", "Anonim", "derin_odak"),
        Soz("Bölünmüş bir saat, tam bir dakika etmez.",
            "An interrupted hour is not worth one whole minute.", "Anonim", "derin_odak"),
        Soz("İstek geçicidir, karar kalıcı.",
            "Urges are temporary; decisions are lasting.", "Anonim", "durtu"),
        Soz("Hayır demek de bir kas; kullandıkça güçlenir.",
            "Saying no is a muscle; it strengthens with use.", "Anonim", "durtu"),
        Soz("Rutin, her gün yeniden karar vermekten kurtarır.",
            "A routine spares you from deciding all over each day.", "Anonim", "rutin"),
        Soz("Küçük ama her gün; büyük ama bazen değil.",
            "Small but daily beats large but occasional.", "Anonim", "rutin"),
        Soz("Alışkanlık kurulana kadar zordur, sonra taşır.",
            "A habit is hard until it forms, then it carries you.", "Anonim", "rutin"),
        Soz("Telefonu bırakmak, iradenin değil ortamın işidir.",
            "Putting the phone down is about setting, not willpower.", "Anonim", "dagilma"),
        Soz("Dikkatini koruyamayan, zamanını da koruyamaz.",
            "Whoever cannot guard attention cannot guard time.", "Anonim", "dagilma"),
        Soz("Her bildirim, bıraktığın yeri biraz daha uzaklaştırır.",
            "Each alert pushes the place you left a little further away.", "Anonim", "dagilma"),

        // motivasyon
        Soz("Bugün atacağın küçük adım, yarının alışkanlığıdır.",
            "Today's small step is tomorrow's habit.", "Anonim", "motivasyon"),
        Soz("Hazır olmayı bekleme; hazırlık başladıktan sonra gelir.",
            "Don't wait to feel ready; readiness comes after you begin.", "Anonim", "motivasyon"),
        Soz("En zor kısım, işe oturduğun ilk beş dakikadır.",
            "The hardest part is the first five minutes at the desk.", "Anonim", "motivasyon"),
        Soz("Mükemmeli bekleyen, iyiyi de kaçırır.",
            "Waiting for perfect costs you the good.", "Anonim", "motivasyon"),

        // azim
        Soz("Yavaş gitmenin önemi yok, durmadığın sürece.",
            "It does not matter how slowly you go, as long as you don't stop.", "Konfüçyüs", "azim"),
        Soz("Damla taşı deler; gücüyle değil, sürekliliğiyle.",
            "The drop carves the stone not by force, but by falling often.", "Ovidius", "azim"),
        Soz("Azim, yetenek biterken devreye giren şeydir.",
            "Persistence is what starts where talent runs out.", "Anonim", "azim"),

        // pes
        Soz("Vazgeçmek, sonucu asla öğrenememektir.",
            "To quit is to never find out how it ends.", "Anonim", "pes"),
        Soz("Düştüğün yerden değil, kalktığın yerden sayılırsın.",
            "You are measured by where you rise, not where you fell.", "Anonim", "pes"),
        Soz("Karanlık en çok şafaktan hemen önce koyulaşır.",
            "It is always darkest just before the dawn.", "Anonim", "pes"),

        // zorluk_sabir
        Soz("Sabır acıdır ama meyvesi tatlıdır.",
            "Patience is bitter, but its fruit is sweet.", "Rousseau", "zorluk_sabir"),
        Soz("Acele eden, yolu değil kendini yorar.",
            "Haste tires the traveler, not the road.", "Anonim", "zorluk_sabir"),

        // tukenmislik
        Soz("Dinlenmek işi bırakmak değil, işe devam edebilmektir.",
            "Rest is not quitting; it is how you keep going.", "Anonim", "tukenmislik"),
        Soz("Boş bir kaptan kimseye su veremezsin.",
            "You cannot pour water from an empty cup.", "Anonim", "tukenmislik"),

        // durtu
        Soz("Dürtü ile eylem arasındaki boşluk, özgürlüğündür.",
            "Between impulse and action lies your freedom.", "Viktor Frankl", "durtu"),
        Soz("On dakika bekle. Çoğu istek o kadar yaşamaz.",
            "Wait ten minutes. Most urges don't live that long.", "Anonim", "durtu"),

        // derin_odak
        Soz("İki tavşanı kovalayan ikisini de kaçırır.",
            "Chase two rabbits and you will catch neither.", "Anonim", "derin_odak"),
        Soz("Dikkat, verebileceğin en pahalı şeydir.",
            "Attention is the most expensive thing you can give.", "Anonim", "derin_odak"),

        // umut
        Soz("Umut, uyanıkken görülen rüyadır.",
            "Hope is a waking dream.", "Aristoteles", "umut"),
        Soz("Kış ne kadar sürerse sürsün, bahar gelir.",
            "However long the winter, spring will come.", "Anonim", "umut"),

        // epiktetos
        Soz("Seni olaylar değil, olaylar hakkındaki fikirlerin üzer.",
            "You are disturbed not by events, but by your views of them.", "Epiktetos", "epiktetos"),
        Soz("Elinde olana hükmet, olmayana razı ol.",
            "Command what is yours; accept what is not.", "Epiktetos", "epiktetos"),

        // marcus
        Soz("Mutlu bir hayat için gereken azdır; her şey düşüncendedir.",
            "Very little is needed for a happy life; it is all in your thinking.",
            "Marcus Aurelius", "marcus"),

        // seneca
        Soz("Zor olduğu için cesaret edemiyoruz; cesaret etmediğimiz için zorlaşıyor.",
            "It is not because things are hard that we do not dare.", "Seneca", "seneca"),

        // epiktetos
        Soz("Bir şey yapmak istiyorsan, önce ne olmak istediğine karar ver.",
            "First say what you would be; then do what you have to do.", "Epiktetos", "epiktetos"),

        // mevlana
        Soz("Dün dünde kaldı, bugün yeni şeyler söylemek lazım.",
            "Yesterday is gone; today we must speak anew.", "Mevlânâ", "mevlana"),

        // minnettarlik
        Soz("Sahip olduklarını say; eksiklerin kısalır.",
            "Count what you have and the list of lacks gets shorter.", "Anonim", "minnettarlik"),

        // zaman
        Soz("Zaman en adil şeydir: herkese günde yirmi dört saat verir.",
            "Time is the fairest thing: everyone gets the same day.", "Anonim", "zaman"),

        // para
        Soz("Para için yaşayan, hayatını paraya satar.",
            "Whoever lives for money sells their life for it.", "Anonim", "para"),

        // basari
        Soz("Başarı, hazırlık ile fırsatın kesiştiği yerdir.",
            "Success is where preparation meets opportunity.", "Seneca", "basari"),
    )

    private val icerik by lazy { Olumlamalar.tumu + havuz }

    fun tumu(): List<Soz> = icerik

    fun kategoriden(anahtar: String): List<Soz> = icerik.filter { it.kategori == anahtar }

    /**
     * Seçili kategorilerden bildirime gidebilecek sözler.
     *
     * ÖNCEKİ DAVRANIŞ: sınırı aşan sözler icerikdan tamamen ELENİYORDU.
     * Bu, uzun içerikli kategorilerin (ayetler, felsefe alıntıları) bildirimde
     * hiç görünmemesine yol açıyordu ve kullanıcı bunun sebebini göremiyordu.
     *
     * YENİ DAVRANIŞ: hepsi icerikda; uzun olanlar bildirimde kısaltılıp
     * tamamı uygulamada açılıyor. Eleme yalnız HAVUZ_TAVANI için var.
     */
    fun bildirimHavuzu(secili: Set<String>, dil: String): List<Soz> =
        icerik.filter { it.kategori in secili && it.metin(dil).length <= HAVUZ_TAVANI }

    /**
     * Rapor 4.3: Geçmişte gösterilenleri eleyerek seçer; icerik tükenirse
     * geçmişi yok sayar. Böylece aynı söz kısa aralıkla tekrar gelmez.
     */
    fun rastgele(secili: Set<String>, gecmis: Set<String> = emptySet()): Soz? {
        val uygun = icerik.filter { it.kategori in secili }.ifEmpty { icerik }
        return uygun.filterNot { it.kimlik in gecmis }.randomOrNull()
            ?: uygun.randomOrNull()
    }

    fun kimlikten(kimlik: String): Soz? = icerik.firstOrNull { it.kimlik == kimlik }

    /**
     * Ana ekranın kaydırmalı akışı. Yakında görülenler sona atılır ki
     * kullanıcı akışa girer girmez tekrarla karşılaşmasın.
     */
    fun akis(secili: Set<String>, gecmis: Set<String> = emptySet()): List<Soz> {
        val uygun = icerik.filter { it.kategori in secili }.ifEmpty { icerik }
        val (gorulmus, yeni) = uygun.partition { it.kimlik in gecmis }
        return yeni.shuffled() + gorulmus.shuffled()
    }
}
