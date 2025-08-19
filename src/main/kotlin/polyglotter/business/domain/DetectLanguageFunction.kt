package polyglotter.business.domain

import org.springframework.stereotype.Component
import java.util.*

@Component
class DetectLanguageFunction(
    private val executeAiTaskWithTextInput: ExecuteAiTaskWithTextInputFunction
) {

    private val languageCodePattern = Regex("[a-z]{2}")

    operator fun invoke(text: String): Locale? {
        val result = executeAiTaskWithTextInput(
            taskDescription = {
                """
                You are a language detection engine.
                
                Task: Detect the language of the INPUT text and output only its ISO 639-1 code.
                
                Rules:
                - Output must be exactly two lowercase letters (e.g., "en", "de", "pt", "zh").
                - If the language has no ISO 639-1 code or the text is too short/ambiguous to decide, output "unknown".
                - For regional variants, return the base language (e.g., "pt" for pt-BR).
                - For scripts, return the language code only (e.g., "zh" for Chinese, not script tags).
                - If multiple languages appear, return the dominant one by character count.
                - Ignore names, URLs, emojis, numbers, and brand terms when determining language.
                - Do not add explanations or punctuation.
                """
            },
            input = text,
            temperature = 0.0,
        )

        return when (result) {
            "unknown" -> null
            else -> {
                check(result matches languageCodePattern) { "[$result] is not a valid ISO 639-1 language code!" }
                Locale.of(result)
            }
        }
    }
}
