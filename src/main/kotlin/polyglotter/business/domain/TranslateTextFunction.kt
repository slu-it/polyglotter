package polyglotter.business.domain

import org.springframework.stereotype.Component
import java.util.*
import java.util.Locale.ENGLISH

@Component
class TranslateTextFunction(
    private val executeAiTask: ExecuteAiTaskFunction
) {

    fun translate(sourceLanguage: Locale, targetLanguage: Locale, text: String): String {
        val sourceLanguageDescription = description(sourceLanguage)
        val targetLanguageDescription = description(targetLanguage)

        val result = executeAiTask(
            taskDescription = {
                """
                You are a $sourceLanguageDescription to $targetLanguageDescription translator.
                Translate the given text!
                """
            },
            input = text
        )

        return result
    }

    private fun description(locale: Locale): String =
        buildString {
            append(locale.getDisplayLanguage(ENGLISH))
            if (!locale.country.isNullOrBlank()) {
                append(" (${locale.getDisplayCountry(ENGLISH)})")
            }
        }
}
