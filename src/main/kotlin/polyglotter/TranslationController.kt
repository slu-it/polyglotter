package polyglotter

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/translate")
class TranslationController(
    private val translator: MassTranslator
) {

    @PostMapping
    fun translate(@RequestBody request: TranslationRequest): TranslationResponse {
        val result = translator.translate(
            sourceLanguage = request.sourceLanguage,
            targetLanguages = request.targetLanguages,
            text = request.text
        )
        return TranslationResponse(result)
    }

    data class TranslationRequest(
        val sourceLanguage: Locale,
        val targetLanguages: Set<Locale>,
        val text: String
    ) {
        init {
            require(targetLanguages.isNotEmpty()) { "You need to provide at least one target language!" }
            require(text.isNotBlank()) { "The text to be translated is not allowed to be blank!" }
            require(!sourceLanguage.language.isNullOrBlank()) { "Source LANGUAGE must be specified!" }
            require(targetLanguages.all { !it.language.isNullOrBlank() }) { "Target LANGUAGES must be specified!" }
        }
    }

    data class TranslationResponse(
        val translations: Map<Locale, String>
    )
}
