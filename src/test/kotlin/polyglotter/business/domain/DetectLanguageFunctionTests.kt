package polyglotter.business.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import polyglotter.Examples.english
import polyglotter.Examples.german
import polyglotter.Examples.germanText
import polyglotter.Examples.spanish
import polyglotter.Examples.spanishText

class DetectLanguageFunctionTests {

    @Nested
    inner class FunctionalTests {

        private val executeAiTaskWithTextInput: ExecuteAiTaskWithTextInputFunction = mockk()
        private val cut = DetectLanguageFunction(executeAiTaskWithTextInput)

        @Test
        fun `if the model is returning 'unknown', it is mapped to null`() {
            stubAiToRespondWith("unknown")
            cut("abc") shouldBe null
        }

        @Test
        fun `if the model is returning a wrong result format, exception an exception is thrown`() {
            stubAiToRespondWith("NOT_A_LANGUAGE")
            val ex = shouldThrow<IllegalStateException> {
                cut("Lorem Ipsum")
            }
            ex shouldHaveMessage "[NOT_A_LANGUAGE] is not a valid ISO 639-1 language code!"
        }

        @Test
        fun `if the model is returning a valid language code, it is returned`() {
            stubAiToRespondWith("en")
            cut("Hello World!") shouldBe english
        }

        private fun stubAiToRespondWith(result: String) {
            every { executeAiTaskWithTextInput(any(), any(), any()) } returns result
        }
    }

    @Nested
    @AiIntegrationTest
    @Import(DetectLanguageFunction::class)
    inner class IntegrationTests(
        @param:Autowired private val cut: DetectLanguageFunction
    ) {

        @Test
        fun `detects German text as 'de'`() {
            cut(germanText) shouldBe german
        }

        @Test
        fun `detects Spanish text as 'es'`() {
            cut(spanishText) shouldBe spanish
        }
    }
}
