package polyglotter.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ImportAutoConfiguration(ChatClientAutoConfiguration::class, OpenAiChatAutoConfiguration::class)
class AiConfiguration {

    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient =
        builder
            .defaultAdvisors(SimpleLoggerAdvisor())
            .build()
}
