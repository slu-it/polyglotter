package polyglotter.business.domain

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*

class TranslateTextFunctionTests {

    private val executeAiTask: ExecuteAiTaskFunction = mockk()
    private val cut = TranslateTextFunction(executeAiTask)

    private val german = Locale.of("de")
    private val germanText = "Ein Capybara ist in einem Aufzug stecken geblieben."
    private val spanish = Locale.of("es")
    private val spanishText = "Un carpincho se quedó atrapado en un ascensor."
    private val englishGb = Locale.of("en", "GB")
    private val englishGbText = "A capybara got stuck in a lift."
    private val englishUs = Locale.of("en", "US")
    private val englishUsText = "A capybara got stuck in an elevator."

    @Test
    fun `returns the model result as the translation`() {
        every { executeAiTask(any(), any(), any()) } returns englishGbText
        cut(german, englishGb, germanText) shouldBe englishGbText
    }

    @Test
    fun `specifies source and target language without country if unavailable`() {
        every { executeAiTask(any(), any(), any()) } returns spanishText
        cut(german, spanish, germanText)
        val taskDescription = verifyModelCallAndReturnTaskDescription()
        taskDescription shouldContain "German → Spanish"
    }

    @Test
    fun `specifies source and target language with country if available`() {
        every { executeAiTask(any(), any(), any()) } returns englishUsText
        cut(englishGb, englishUs, englishGbText)
        val taskDescription = verifyModelCallAndReturnTaskDescription()
        taskDescription shouldContain "English (United Kingdom) → English (United States)"
    }

    private fun verifyModelCallAndReturnTaskDescription(): String {
        val taskDescriptionSlot = slot<Function0<String>>()
        verify { executeAiTask(capture(taskDescriptionSlot), any(), any()) }
        return taskDescriptionSlot.captured()
    }
}
