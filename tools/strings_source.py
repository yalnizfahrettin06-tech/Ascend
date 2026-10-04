"""Single source for the app's Turkish and English UI strings. Run: python tools/strings_source.py"""
import re
import sys
from pathlib import Path
from xml.sax.saxutils import escape

sys.path.insert(0, str(Path(__file__).resolve().parent))
from strings_i18n import LANGUAGES

S = {
 "uygulama_adi": ("Ascend", "Ascend"),
 "widget_aciklama": ("Today's quote on your home screen", "Günün sözü ana ekranında"),
 "widget_square_name": ("Ascend · Square", "Ascend · Kare"),
 "widget_wide_name": ("Ascend · Wide", "Ascend · Geniş"),
 "yeni_soz": ("Today", "Bugün"),
 "widget_next": ("Next", "Sonraki"),
 "widget_empty": ("Choose topics in Ascend to see a quote here.", "Burada söz görmek için Ascend'de konu seç."),
 "favoriler_baslik": ("Saved", "Kaydedilenler"),
 "channel_name": ("Daily words", "Günlük sözler"),
 "channel_description": ("Quotes from the topics you chose, at the times you set.", "Seçtiğin konulardan sözler, belirlediğin saatlerde."),
 "notification_save": ("Save", "Kaydet"),
 "notification_pause_today": ("Enough for today", "Bugünlük yeter"),
 "notification_surprise_title": ("Surprise · %1$s", "Sürpriz · %1$s"),
 "a11y_value": ("%1$s: %2$s", "%1$s: %2$s"),
 "action_back": ("Back", "Geri"),
 "action_clear": ("Clear", "Temizle"),
 "action_continue": ("Continue", "Devam"),
 "action_copy": ("Copy text", "Metni kopyala"),
 "action_done": ("Done", "Tamam"),
 "action_hide": ("Don't show this again", "Bunu bir daha gösterme"),
 "action_listen": ("Listen", "Sesli dinle"),
 "action_more": ("More", "Diğer"),
 "action_remove": ("Remove", "Kaldır"),
 "action_resume": ("Resume", "Devam et"),
 "action_save": ("Save quote", "Sözü kaydet"),
 "action_unsave": ("Remove from saved", "Kaydedilenlerden çıkar"),
 "action_share": ("Share", "Paylaş"),
 "action_turn_on": ("Turn on", "Aç"),
 "action_undo": ("Undo", "Geri al"),
 "tab_today": ("Today", "Bugün"),
 "tab_explore": ("Explore", "Keşfet"),
 "tab_reminders": ("Reminders", "Bildirimler"),
 "tab_you": ("You", "Senin"),
 "pro_locked": ("Pro topic", "Pro konu"),
 "greeting_morning": ("Good morning", "Günaydın"),
 "greeting_afternoon": ("Good afternoon", "İyi günler"),
 "greeting_evening": ("Good evening", "İyi akşamlar"),
 "greeting_night": ("A quiet night", "Sakin bir gece"),
 "onboarding_step": ("%1$d of %2$d", "%1$d / %2$d"),
 "onboarding_welcome_title": ("A good word, every day.", "Her gün, sana iyi gelen bir söz."),
 "onboarding_welcome_body": ("Choose the topics that matter to you now. Ascend sends short reflections from them, at the hours you pick.", "Şu an sana önemli gelen konuları seç. Ascend, seçtiğin saatlerde bu konulardan kısa sözler gönderir."),
 "onboarding_topics_title": ("What do you need right now?", "Şu an neye ihtiyacın var?"),
 "onboarding_topics_body": ("Choose 1 to %1$d topics. Your reminders come from these; you can change them anytime.", "1 ile %1$d arasında konu seç. Bildirimlerin bu konulardan gelir; istediğin zaman değiştirebilirsin."),
 "onboarding_topics_count": ("%1$d of %2$d selected", "%1$d / %2$d seçildi"),
 "onboarding_topics_more": ("%1$d more topics are available with Ascend Pro.", "Ascend Pro ile %1$d konu daha açılır."),
 "onboarding_rhythm_title": ("Set your rhythm", "Ritmini belirle"),
 "onboarding_rhythm_body": ("How often, and between which hours? Outside this window Ascend stays quiet.", "Günde kaç kez, hangi saatler arasında? Bu aralığın dışında Ascend sessiz kalır."),
 "onboarding_permission_title": ("Let the words find you", "Sözler seni bulsun"),
 "onboarding_permission_body": ("Reminders need notification permission. You can save a quote or end the day's reminders right from the notification.", "Hatırlatmalar için bildirim izni gerekir. Sözü bildirimden kaydedebilir ya da günün bildirimlerini oradan bitirebilirsin."),
 "onboarding_permission_allow": ("Allow notifications", "Bildirimlere izin ver"),
 "onboarding_permission_skip": ("Not now", "Şimdilik geç"),
 "onboarding_permission_granted": ("Notifications are on.", "Bildirimler açık."),
 "onboarding_permission_later": ("You can turn them on later from the Reminders tab.", "Daha sonra Bildirimler sekmesinden açabilirsin."),
 "onboarding_ready_title": ("You're all set", "Hazırsın"),
 "onboarding_ready_body": ("Here is your plan. Everything can be changed from Reminders.", "İşte planın. Hepsini Bildirimler sekmesinden değiştirebilirsin."),
 "onboarding_ready_no_permission": ("Notifications are off for now, so reminders will wait until you allow them.", "Bildirimler şu an kapalı; izin verene kadar hatırlatmalar bekler."),
 "onboarding_start": ("Start", "Başla"),
 "today_permission_off": ("Notifications are off, so your reminders can't reach you.", "Bildirimler kapalı; hatırlatmaların sana ulaşamıyor."),
 "today_reminders_off": ("Reminders are turned off.", "Hatırlatmalar kapalı."),
 "today_paused": ("Reminders are paused until tomorrow.", "Hatırlatmalara yarına kadar ara verdin."),
 "today_next_reminder": ("Next reminder at %1$s", "Sonraki söz %1$s"),
 "today_no_more_today": ("No more reminders today", "Bugün başka hatırlatma yok"),
 "today_tts_unavailable": ("A voice for this language isn't available on your device.", "Cihazında bu dil için ses paketi yok."),
 "today_copied": ("Copied", "Kopyalandı"),
 "today_hidden": ("You won't see this quote again.", "Bu sözü bir daha görmeyeceksin."),
 "today_empty_title": ("Nothing here yet", "Burada henüz bir şey yok"),
 "today_empty_body": ("Your topics have no quotes left to show. Add a topic to see more.", "Konularında gösterilecek söz kalmadı. Daha fazlası için konu ekle."),
 "explore_search": ("Search topics and quotes", "Konu veya söz ara"),
 "explore_no_results": ("No results. Try another word.", "Sonuç yok. Başka bir kelime dene."),
 "explore_series_title": ("7-day series", "7 günlük seriler"),
 "explore_series_body": ("One quote and one small step each day.", "Her gün bir söz ve küçük bir adım."),
 "explore_series_continue": ("Continue · day %1$d of 7", "Devam et · 7 günün %1$d. günü"),
 "explore_group_selected": ("%1$d in reminders", "%1$d bildirimde"),
 "explore_pro_topic": ("Ascend Pro", "Ascend Pro"),
 "explore_in_reminders": ("In your reminders", "Bildirimlerinde"),
 "explore_add_to_reminders": ("Add %1$s to reminders", "%1$s konusunu bildirimlere ekle"),
 "explore_remove_from_reminders": ("Remove %1$s from reminders", "%1$s konusunu bildirimlerden çıkar"),
 "explore_add_switch": ("Send me quotes from this topic", "Bu konudan bildirim gelsin"),
 "explore_last_topic": ("Add another topic before removing your last one.", "Son konunu çıkarmadan önce başka bir konu ekle."),
 "explore_unlock_with_pro": ("Unlock with Ascend Pro", "Ascend Pro ile aç"),
 "explore_inspired_note": ("Quotes marked with a source are verbatim from public-domain texts; the others are original Ascend reflections inspired by this thinker.", "Kaynağı belirtilenler kamu malı metinlerden birebir alıntıdır; diğerleri bu düşünürden esinlenen özgün Ascend düşünceleridir."),
 "explore_quote_badge": ("Quote", "Alıntı"),
 "reminders_subtitle": ("When and what you receive", "Ne zaman, hangi sözler"),
 "reminders_status_on": ("Reminders are on", "Hatırlatmalar açık"),
 "reminders_status_off": ("Reminders are off", "Hatırlatmalar kapalı"),
 "reminders_status_off_body": ("Turn them on to receive quotes from your topics.", "Konularından söz almak için aç."),
 "reminders_status_paused": ("Paused for today", "Bugün ara verildi"),
 "reminders_status_paused_body": ("Your rhythm continues after midnight.", "Ritmin gece yarısından sonra devam eder."),
 "reminders_status_permission": ("Notification permission is off", "Bildirim izni kapalı"),
 "reminders_status_permission_body": ("Android blocks Ascend's reminders until you allow notifications.", "Bildirimlere izin verene kadar Android hatırlatmaları engeller."),
 "reminders_next_at": ("Next one at %1$s", "Sıradaki %1$s"),
 "reminders_enabled": ("Send reminders", "Hatırlatma gönder"),
 "reminders_pause_today": ("Pause for today", "Bugün ara ver"),
 "reminders_resume": ("Resume today", "Bugün devam et"),
 "reminders_rhythm": ("Rhythm", "Ritim"),
 "reminders_per_day": ("Reminders per day", "Günlük hatırlatma"),
 "reminders_from": ("From", "Başlangıç"),
 "reminders_until": ("Until", "Bitiş"),
 "reminders_times_preview": ("Approximate times", "Yaklaşık saatler"),
 "reminders_topics": ("Topics", "Konular"),
 "reminders_topics_body": ("Reminders come from these topics. Tap a topic to remove it.", "Hatırlatmalar bu konulardan gelir. Çıkarmak için konuya dokun."),
 "reminders_topics_edit": ("Add or change topics", "Konu ekle veya değiştir"),
 "reminders_surprise": ("Occasional surprise", "Arada bir sürpriz"),
 "reminders_surprise_body": ("About one in seven reminders comes from another unlocked topic.", "Yaklaşık her yedi bildirimden biri açık olan başka bir konudan gelir."),
 "reminders_recent": ("Recently sent", "Son gönderilenler"),
 "reminders_recent_empty": ("Your first reminder will appear here.", "İlk hatırlatman burada görünecek."),
 "reminders_see_history": ("See full history", "Tüm geçmişi gör"),
 "reminders_help_title": ("Check & help", "Kontrol ve yardım"),
 "reminders_test": ("Send a test reminder", "Deneme bildirimi gönder"),
 "reminders_test_body": ("See exactly how a reminder looks.", "Bildirimin nasıl göründüğünü gör."),
 "reminders_test_sent": ("Sent. Check your notification shade.", "Gönderildi. Bildirim panelini aç."),
 "reminders_test_failed": ("Couldn't send. Check notification permission.", "Gönderilemedi. Bildirim iznini kontrol et."),
 "reminders_help": ("Not seeing reminders?", "Bildirimler görünmüyor mu?"),
 "reminders_help_body": ("Pop-ups, lock screen and battery settings", "Açılır pencere, kilit ekranı ve pil ayarları"),
 "help_body_general": ("Reminders arrive quietly in your notification shade. Expand a notification to read the whole quote. For pop-up alerts, enable “Pop on screen” for the Daily words channel.", "Hatırlatmalar bildirim paneline sessizce gelir. Sözün tamamı için bildirimi genişlet. Ekranda açılır uyarı istersen Günlük sözler kanalında “Ekranda göster” seçeneğini aç."),
 "help_body_samsung": ("On Samsung: Notifications → Notification pop-up style → Detailed shows more of each quote.", "Samsung'da: Bildirimler → Bildirim açılır pencere stili → Ayrıntılı, sözün daha fazlasını gösterir."),
 "help_body_battery": ("If reminders are late, make sure battery saver isn't restricting Ascend.", "Hatırlatmalar gecikiyorsa pil tasarrufunun Ascend'i kısıtlamadığından emin ol."),
 "help_app_settings": ("App notification settings", "Uygulama bildirim ayarları"),
 "help_popup_settings": ("Pop-up settings for reminders", "Hatırlatma açılır pencere ayarı"),
 "help_open_failed": ("Couldn't open settings. Open Settings → Notifications on your device.", "Ayarlar açılamadı. Cihazında Ayarlar → Bildirimler bölümünü aç."),
 "you_streak": ("Day streak", "Günlük seri"),
 "you_read_today": ("Read today", "Bugün okunan"),
 "you_saved": ("Saved", "Kaydedilen"),
 "you_totals": ("Best streak %1$d days · %2$d quotes read", "En iyi seri %1$d gün · %2$d söz okundu"),
 "you_saved_tab": ("Saved", "Kaydedilenler"),
 "you_history_tab": ("History", "Geçmiş"),
 "settings_title": ("Settings", "Ayarlar"),
 "settings_language": ("Language", "Dil"),
 "settings_theme": ("Theme", "Tema"),
 "settings_theme_system": ("System", "Sistem"),
 "settings_theme_light": ("Light", "Açık"),
 "settings_theme_dark": ("Dark", "Koyu"),
 "settings_appearance": ("Backgrounds, widget & wallpaper", "Arka plan, widget ve duvar kâğıdı"),
 "settings_haptics": ("Haptic feedback", "Titreşimli geri bildirim"),
 "settings_pro_on": ("Pro demo is on", "Pro demosu açık"),
 "settings_pro_off": ("Try Ascend Pro", "Ascend Pro'yu dene"),
 "settings_pro_body": ("All topics, backgrounds and share tools. No payment is taken.", "Tüm konular, arka planlar ve paylaşım araçları. Ödeme alınmaz."),
 "settings_content": ("Content", "İçerik"),
 "settings_hidden_none": ("No hidden quotes.", "Gizlenen söz yok."),
 "settings_about": ("About", "Hakkında"),
 "settings_about_body": ("Ascend's reflections are original texts. Quotes shown with an author and work are verbatim public-domain translations; their renderings in other languages are Ascend's own.", "Ascend'deki düşünceler özgün metinlerdir. Yazar ve eser adıyla gösterilen alıntılar kamu malı çevirilerden birebir alınmıştır; Türkçeleri Ascend'e aittir."),
 "settings_version": ("Version %1$s", "Sürüm %1$s"),
}

P = {
 "streak_days": (("%1$d day", "%1$d days"), ("%1$d gün", "%1$d gün")),
 "topics_count": (("%1$d topic", "%1$d topics"), ("%1$d konu", "%1$d konu")),
 "quotes_count": (("%1$d quote", "%1$d quotes"), ("%1$d söz", "%1$d söz")),
 "explore_subtitle": (("%1$d topics · %2$d in your reminders", "%1$d topics · %2$d in your reminders"), ("%1$d konu · %2$d tanesi bildirimlerinde", "%1$d konu · %2$d tanesi bildirimlerinde")),
 "explore_locked_more": (("%1$d more quote with Pro", "%1$d more quotes with Pro"), ("Pro ile %1$d söz daha", "Pro ile %1$d söz daha")),
 "reminders_times_day": (("Once a day", "%1$d times a day"), ("Günde 1 kez", "Günde %1$d kez")),
 "reminders_summary": (("Once a day, %2$s–%3$s", "%1$d times a day, %2$s–%3$s"), ("Günde 1 kez, %2$s–%3$s", "Günde %1$d kez, %2$s–%3$s")),
 "settings_hidden_restore": (("Restore %1$d hidden quote", "Restore %1$d hidden quotes"), ("%1$d gizlenen sözü geri getir", "%1$d gizlenen sözü geri getir")),
}

def esc(t):
    return escape(t).replace("'", "\\'").replace('"', '\\"')

def tokens(t):
    return sorted(re.findall(r'%\d+\$[ds]', t))

def write(path, strings, plurals, quantities):
    lines = ['<?xml version="1.0" encoding="utf-8"?>', '<!-- GENERATED by tools/strings_source.py. Edit that file instead. -->', '<resources>']
    for k, v in strings.items():
        lines.append(f'    <string name="{k}">{esc(v)}</string>')
    for k, forms in plurals.items():
        lines.append(f'    <plurals name="{k}">')
        for q, text in zip(quantities, forms):
            lines.append(f'        <item quantity="{q}">{esc(text)}</item>')
        lines.append('    </plurals>')
    lines.append('</resources>')
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")

root = Path(__file__).resolve().parents[1] / "app/src/main/res"
write(root / "values/strings.xml", {k: v[0] for k, v in S.items()}, {k: v[0] for k, v in P.items()}, ("one", "other"))
write(root / "values-tr/strings.xml", {k: v[1] for k, v in S.items()}, {k: v[1] for k, v in P.items()}, ("one", "other"))
for lang, (strings, plurals) in LANGUAGES.items():
    assert set(strings) == set(S), f"{lang}: missing {sorted(set(S) - set(strings))} extra {sorted(set(strings) - set(S))}"
    assert set(plurals) == set(P), f"{lang}: plural keys differ"
    for k, v in strings.items():
        assert tokens(v) == tokens(S[k][0]) and v.strip(), (lang, k)
    quantities = ("one", "few", "many", "other") if lang == "ru" else ("one", "other")
    for k, forms in plurals.items():
        assert len(forms) == len(quantities), (lang, k)
        assert tokens(forms[-1]) == tokens(P[k][0][1]), (lang, k)
    write(root / f"values-{lang}/strings.xml", strings, plurals, quantities)
print(f"{len(S)} strings and {len(P)} plurals written for en, tr, {', '.join(LANGUAGES)}.")
