package polyglotter

import org.springframework.ai.chat.client.ChatClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient = builder.build()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
