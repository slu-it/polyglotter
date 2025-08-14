package polyglotter.business.domain

import org.slf4j.LoggerFactory.getLogger
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Component

@Component
class ExecuteAiTaskFunction(
    private val chatClient: ChatClient
) {

    private val log = getLogger(javaClass)

    operator fun invoke(taskDescription: () -> String, input: String, temperature: Double): String {
        val system = taskDescription().trimIndent()
        val options = OpenAiChatOptions.builder()
            .temperature(temperature)
            .build()
        val result = chatClient.prompt()
            .options(options)
            .system(system)
            .user(input)
            .call()
            .content()
        logInteraction(system = system, user = input, result = result)
        check(result != null) { "AI did not return a result!" }
        return result
    }

    private fun logInteraction(system: String, user: String, result: String?) {
        val message = buildString {
            appendLine()
            appendLine("SYSTEM : $system")
            appendLine("USER   : $user")
            appendLine("RESULT : $result")
        }
        log.debug(message)
    }
}
