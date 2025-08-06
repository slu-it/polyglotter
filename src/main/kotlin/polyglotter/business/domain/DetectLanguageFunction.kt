package polyglotter.business.domain

import org.springframework.stereotype.Component
import java.util.*

@Component
class DetectLanguageFunction(
    private val executeAiTask: ExecuteAiTaskFunction
) {

    private val languageCodePattern = Regex("[a-z]{2}")

    operator fun invoke(text: String): Locale {
        val result = executeAiTask(
            taskDescription = {
                """
                You are a language detection engine.
                Your job is to detect the language of a given text and respond with only the corresponding ISO 639-1 language code.
                What is the language of the given text?
                """
            },
            input = text
        )
        check(result matches languageCodePattern) { "[$result] is not a valid ISO 639-1 language code!" }
        return Locale.of(result)
    }
}
