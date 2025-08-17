package polyglotter.business

import org.springframework.stereotype.Component
import polyglotter.business.domain.DetectLanguageFunction
import polyglotter.business.domain.TranslateTextFunction
import polyglotter.business.exceptions.IndeterminableLanguageException
import polyglotter.business.model.TranslationQuery
import java.util.*
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor

@Component
class TranslateFunction(
    private val detectLanguage: DetectLanguageFunction,
    private val translateText: TranslateTextFunction,
) {

    private val executor = newVirtualThreadPerTaskExecutor()

    operator fun invoke(query: TranslationQuery): Map<Locale, String> =
        translate(
            sourceLanguage = query.sourceLanguage
                ?: detectLanguage(query.text)
                ?: throw IndeterminableLanguageException(query.text),
            targetLanguages = query.targetLanguages,
            text = query.text
        )

    private fun translate(sourceLanguage: Locale, targetLanguages: Set<Locale>, text: String): Map<Locale, String> {
        val original = mapOf(sourceLanguage to text)
        val translations = targetLanguages.sortedBy { it.language }
            .map { language -> language to asyncTranslate(sourceLanguage, language, text) }
            .associate { (language, supplier) -> language to supplier.get() }
        return original + translations
    }

    private fun asyncTranslate(sourceLanguage: Locale, language: Locale, text: String) =
        supplyAsync({ translateText(sourceLanguage, language, text) }, executor)
}
