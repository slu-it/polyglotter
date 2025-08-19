package polyglotter.business.domain

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import polyglotter.config.AiConfiguration
import java.lang.annotation.Inherited

@Retention
@Inherited
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
@SpringBootTest(classes = [AiConfiguration::class])
@Import(ExecuteAiTaskWithTextInputFunction::class)
annotation class AiIntegrationTest
