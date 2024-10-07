package polyglotter

import org.slf4j.LoggerFactory.getLogger
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component
import java.util.Locale
import java.util.Locale.ENGLISH

@Component
class Translator(
    private val chatClient: ChatClient
) {

    private val log = getLogger(javaClass)

    fun translate(sourceLanguage: Locale, targetLanguage: Locale, text: String): String {
        val sourceLanguageDescription = description(sourceLanguage)
        val targetLanguageDescription = description(targetLanguage)

        val systemPrompt = "You are a $sourceLanguageDescription to $targetLanguageDescription translator."
        val userPrompt = "Translate: $text"

        val result = chatClient.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .call()
            .content()

        logInteraction(systemPrompt, userPrompt, result)

        return result
    }

    private fun logInteraction(systemPrompt: String, userPrompt: String, result: String?) {
        val message = buildString {
            appendLine()
            appendLine("SYSTEM : $systemPrompt")
            appendLine("USER   : $userPrompt")
            appendLine("RESULT : $result")
        }
        log.debug(message)
    }

    private fun description(locale: Locale): String =
        buildString {
            append(locale.getDisplayLanguage(ENGLISH))
            if (!locale.country.isNullOrBlank()) {
                append(" (${locale.getDisplayCountry(ENGLISH)})")
            }
        }
}
