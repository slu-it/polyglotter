package polyglotter.business.model

import java.util.Locale

data class TranslationQuery(
    val text: String,
    val sourceLanguage: Locale?,
    val targetLanguages: Set<Locale>,
) {
    init {
        require(text.isNotBlank()) { "The text to be translated is not allowed to be blank!" }
        require(targetLanguages.isNotEmpty()) { "You need to provide at least one target language!" }
        require(targetLanguages.all { !it.language.isNullOrBlank() }) { "Target LANGUAGES must be specified!" }
    }
}
