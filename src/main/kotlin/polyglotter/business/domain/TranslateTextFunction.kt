package polyglotter.business.domain

import org.springframework.stereotype.Component
import java.util.*
import java.util.Locale.ENGLISH

@Component
class TranslateTextFunction(
    private val executeAiTask: ExecuteAiTaskFunction
) {

    fun translate(sourceLanguage: Locale, targetLanguage: Locale, text: String): String {
        val sld = description(sourceLanguage)
        val tld = description(targetLanguage)

        val result = executeAiTask(
            taskDescription = {
                $$"""
                You are a $$sld → $$tld translator.

                Task: Translate the INPUT text into $$tld. If the target language includes a country in parentheses (e.g., "English (United Kingdom)"), follow that locale’s spelling, vocabulary, and punctuation.
                
                Output: Only the translation. No explanations.
                
                Rules:
                - Preserve meaning, tone, and register.
                - Keep formatting: line breaks, whitespace, markdown, and HTML tags.
                - Do not translate or alter: URLs, emails, code, file paths, numbers, units, variables/placeholders ({name}, {{handlebars}}, $VAR), hashtags, @mentions, emojis.
                - Keep proper names and brand names unless a well-established localized form exists.
                - If parts are already in $$tld, leave them unchanged.
                - If $$sld equals $$tld, convert to the target locale’s conventions.
                - Prefer natural phrasing over literal when choices conflict.
                
                INPUT:
                """
            },
            input = text,
            temperature = 0.2,
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
