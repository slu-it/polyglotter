package polyglotter.business.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.Locale

class DetectLanguageFunctionTests {

    private val executeAiTask: ExecuteAiTaskFunction = mockk()
    private val cut = DetectLanguageFunction(executeAiTask)

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
        cut("Hello World!") shouldBe Locale.of("en")
    }

    private fun stubAiToRespondWith(result: String) {
        every { executeAiTask(any(), any(), any()) } returns result
    }
}
