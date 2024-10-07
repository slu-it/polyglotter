package polyglotter

import org.springframework.stereotype.Component
import java.util.Locale
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor

@Component
class MassTranslator(
    private val translator: Translator
) {

    private val executor = newVirtualThreadPerTaskExecutor()

    fun translate(sourceLanguage: Locale, targetLanguages: Set<Locale>, text: String): Map<Locale, String> {
        val original = mapOf(sourceLanguage to text)
        val translations = targetLanguages.sortedBy { it.language }
            .map { language -> language to asyncTranslate(sourceLanguage, language, text) }
            .associate { (language, supplier) -> language to supplier.get() }

        return original + translations
    }

    private fun asyncTranslate(sourceLanguage: Locale, language: Locale, text: String) =
        supplyAsync({ translator.translate(sourceLanguage, language, text) }, executor)

}
