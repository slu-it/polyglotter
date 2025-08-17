package polyglotter.business.model

import java.util.*

data class TranslationQuery(
    val text: String,
    val sourceLanguage: Locale?,
    val targetLanguages: Set<Locale>,
) {
    init {
        require(text.isNotBlank()) { "The text to be translated is not allowed to be blank!" }
        require(targetLanguages.isNotEmpty()) { "You need to provide at least one target language!" }
    }
}
