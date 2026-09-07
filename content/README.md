# Ascend content guide

[The English source](source.en.json) is the **only canonical content master**. Translations refer to its stable quote IDs. Do not create a second bilingual master or edit the generated Kotlin catalogue by hand.

The current catalogue contains 700 original Ascend texts in 70 categories across 12 groups. The app currently ships English and Turkish. The file format and template command provide a starting point for seven or eight additional languages; adding files alone does not add those languages to the app.

## Files and ownership

| File | Purpose | Edit directly? |
| --- | --- | --- |
| `content/source.en.json` | Canonical English texts, stable IDs, category/group metadata, catalogue version and editorial note | Yes, for source edits |
| `content/translations/tr.json` | Turkish text keyed by the exact English quote ID, with a source fingerprint | Yes, for reviewed translations |
| `content/translations/<locale>.json` | The same ID-linked format for a future language | Yes, after creating a template |
| `tools/icerik_derle.py` | Validates the current English/Turkish catalogue and generates offline Kotlin | Developers only |
| `app/src/main/java/com/yalnizfahrettin/azim/data/IcerikVerisi.kt` | Generated app data containing the current English and Turkish runtime strings | No; regenerate |

The generated app file contains both runtime languages because the app works offline. It is a build product, not an independently maintained source. Research notes explain editorial choices; they are not an alternative content master.

## Source records and stable identity

The English master has `schemaVersion: 1`, `sourceLanguage: "en"`, `author: "Ascend"`, a `catalogVersion`, group/category metadata and a `quotes` array. Each quote has exactly three fields:

```json
{
  "id": "v5_<category>_01",
  "category": "<category>",
  "text": "<English source text>"
}
```

This is a shape illustration; angle-bracket placeholders are not valid catalogue values.

Use the existing ID and category when correcting or translating an existing thought. IDs are not derived from translated wording. Do not renumber quotes when sorting a file or reuse an established ID for an unrelated replacement: saved items and reading/notification history depend on these identities. A substantial replacement requires an explicit identity/migration decision.

Category IDs are language-independent keys even when they contain Turkish words. Translators must not translate those keys. Group/category names in the master provide context; the current quote compiler does not generate the app's category labels or other interface copy from this metadata.

The compiler currently requires exactly 700 distinct quote IDs, 70 categories, 12 groups and 10 quotes per category. It also checks each ID against `v5_<category>_<two digits>`. Expanding the catalogue is a developer change to the current validation and generation rules, not just appending another quote.

## Translation records and source fingerprints

A translation file has this structure:

```json
{
  "schemaVersion": 1,
  "locale": "tr",
  "sourceCatalogVersion": "5.0.0",
  "quotes": {
    "v5_<category>_01": {
      "text": "<Reviewed translated text>",
      "sourceHash": "<SHA-256 of the exact English source text>"
    }
  }
}
```

This is also a shape illustration. Copy the actual IDs, current version and generated fingerprints; do not paste the placeholders.

`sourceHash` is the SHA-256 hex digest of the English quote's exact UTF-8 text. It fingerprints the English source, **not the translation, category, ID or whole file**. A punctuation or wording change in English changes the hash. The compiler uses that difference to report a stale Turkish translation.

After an English edit:

1. Keep the existing ID when it is still the same thought.
2. Review its Turkish translation against the revised English. Revise the translation if necessary.
3. Update that translation's `sourceHash` only after the review. If the translation remains accurate, the review may result in a fingerprint-only update.
4. If the catalogue version changes, update the translation's `sourceCatalogVersion` to the same version.
5. Regenerate and run the check below.

Do not bulk-refresh fingerprints merely to silence validation: doing so would hide translations that have not been reviewed. The compiler never translates text and never automatically marks a translation as reviewed.

To inspect the current English text and fingerprint for one existing ID, run from the repository root:

```sh
python -c "import hashlib,json; m=json.load(open('content/source.en.json',encoding='utf-8')); q=next(q for q in m['quotes'] if q['id']=='v5_ozsefkat_01'); print(q['text']); print(hashlib.sha256(q['text'].encode('utf-8')).hexdigest())"
```

## Writing and translation requirements

Every English and Turkish quote must:

- Be a single line of 40–120 Unicode characters, including spaces and punctuation.
- Have no leading/trailing whitespace, control characters or line breaks.
- Use NFC-normalized Unicode.
- Be distinct within its language after case folding and removal of non-word characters.
- Fit its assigned category and preserve the intended meaning without adding promises, blame or a new factual claim.

Prefer direct, natural language that works in a notification and on a share card. Keep the concrete situation and emotional meaning; an idiomatic translation is better than word-for-word syntax. Preserve uncertainty where the source leaves room for it. Do not convert “can” into a guaranteed result or ordinary encouragement into medical, financial or religious assurance.

These texts are original Ascend writing. A philosopher's name or a sacred-text category describes the context of an original reflection; it is not the author of the displayed sentence. Do not add quotation marks, verse numbers, a historical person's signature or an invented attribution. Preserve the distinction between affirmations, original reflections, personal prayers and historical archive entries in the app's presentation.

## Generate and validate

Use normal Python 3 from the repository root; the script uses only the Python standard library. Do not run it with Python optimization flags such as `-O`, because its validation uses assertions.

Regenerate the offline Kotlin catalogue after an approved content/translation edit:

```sh
python tools/icerik_derle.py
```

Then verify that committed/generated data matches the source without rewriting it:

```sh
python tools/icerik_derle.py --check
```

The normal command validates the English master and the Turkish translation before writing `IcerikVerisi.kt`. The check command performs the same validation and compares the expected generated text with the existing Kotlin file. It fails if the file is missing or differs.

Current checks include catalogue counts, stable-ID uniqueness, valid category/group references, exact quote fields, text limits and normalization, duplicate text, missing/orphaned Turkish IDs, catalogue-version mismatch and stale source hashes. A passing check verifies data consistency; it does not replace language/editorial review or Android tests.

Common failures:

| Failure | Action |
| --- | --- |
| `Stale translation: <id>` | Compare the current English text with its translation, review it, then update the fingerprint |
| `Missing or orphaned Turkish IDs` | Match the Turkish ID set exactly to the English quote ID set |
| `Generated Kotlin differs` | Run the generator, inspect the generated change, then run `--check` |
| Text length/whitespace/Unicode failure | Edit or normalize the specified record; do not truncate it mechanically |
| Repeated text | Rewrite the duplicate to serve its own category and situation |
| Version mismatch | Make `sourceCatalogVersion` agree with the reviewed master version |

Commit the English source or translation changes together with their regenerated Kotlin output.

## Prepare another language

Create an empty template without copying English into every translation record:

```sh
python tools/icerik_derle.py --template de --output content/translations/de.json
```

Regional language tags are also accepted:

```sh
python tools/icerik_derle.py --template pt-BR --output content/translations/pt-BR.json
```

The command:

- Validates the English master first.
- Requires `--output`.
- Creates a new file only and refuses to overwrite an existing one.
- Writes all 700 IDs, the current source catalogue version and each English `sourceHash`.
- Leaves every translated `text` empty for the translator.

The output's parent directory must already exist. The accepted locale pattern is two or three lowercase letters, optionally followed by a hyphen and a two-to-four-letter suffix. Examples include `de`, `es`, `fr`, `ar`, `pt-BR` and `zh-Hant`. This is the script's limited pattern, not validation of every possible language tag.

Keep existing translation work when the English source changes. To compare against refreshed fingerprints, generate a **new temporary template path** and merge only reviewed changes by ID. There is no automatic template merge or translation service in this script.

## Before shipping additional languages

Currently `render()` validates and compiles **Turkish only** alongside English; ordinary generation and `--check` do not load arbitrary new locale files. Empty templates are intentionally unfinished. A completed `de.json` or another language file is not automatically validated, compiled or selectable in the app.

For each additional shipped language, developers must extend translation validation/generation and the runtime text model, define missing-translation fallback, and integrate language selection and interface/category labels. Notifications, sharing/exported attribution, speech-language selection and layout must also use the chosen locale. Languages that need right-to-left layout or other font coverage require corresponding UI support and device checks.

This separation keeps one English source for future translation work while stating accurately what the current app supports: English and Turkish.
