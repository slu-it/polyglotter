package polyglotter.business.domain

import org.slf4j.LoggerFactory.getLogger
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Component

@Component
class ExecuteAiTaskWithTextInputFunction(
    private val chatClient: ChatClient
) {

    private val log = getLogger(javaClass)

    operator fun invoke(taskDescription: () -> String, input: String, temperature: Double): String {
        val options = OpenAiChatOptions.builder()
            .temperature(temperature)
            .build()
        val system = taskDescription().trimIndent()
        val user = buildString {
            appendLine("INPUT")
            appendLine("----")
            appendLine(input.trim())
            appendLine("----")
        }

        val result = chatClient.prompt()
            .options(options)
            .system(system)
            .user(user)
            .call()
            .content()

        logInteraction(system = system, user = user, result = result)
        check(result != null) { "AI did not return a result!" }
        return result
    }

    private fun logInteraction(system: String, user: String, result: String?) {
        log.debug("Model Interaction\nSYSTEM:\n{}\n\nUSER:\n{}\n\nRESULT:\n{}\n\n", system, user, result)
    }
}
