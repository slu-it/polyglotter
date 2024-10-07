package polyglotter

import org.slf4j.LoggerFactory.getLogger
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class LanguageDetector(
    private val chatClient: ChatClient
) {

    private val log = getLogger(javaClass)

    fun detect(text: String): Locale {
        val systemPrompt = """
            You are a language detection engine.
            Your job is to detect the language of a given text and respond with only the corresponding ISO 639-1 language code.
            """.trimIndent()
        val userPrompt = "What language is this?: $text"

        val result = chatClient.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .call()
            .content()

        logInteraction(systemPrompt, userPrompt, result)
        check(result.length == 2) { "[$result] is not a valid ISO 639-1 language code!" }

        return Locale.of(result)
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
}
