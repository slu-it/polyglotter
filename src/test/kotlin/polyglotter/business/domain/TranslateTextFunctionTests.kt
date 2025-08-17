package polyglotter.business.domain

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import polyglotter.Examples.englishGb
import polyglotter.Examples.englishGbText
import polyglotter.Examples.englishUs
import polyglotter.Examples.englishUsText
import polyglotter.Examples.german
import polyglotter.Examples.germanText
import polyglotter.Examples.spanish
import polyglotter.Examples.spanishText
import polyglotter.testing.similarity

class TranslateTextFunctionTests {

    @Nested
    inner class FunctionalTests {

        private val executeAiTask: ExecuteAiTaskFunction = mockk()
        private val cut = TranslateTextFunction(executeAiTask)

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

    @Nested
    @AiIntegrationTest
    @Import(TranslateTextFunction::class)
    inner class IntegrationTests(
        @Autowired private val cut: TranslateTextFunction
    ) {

        @Test
        fun `translates from German to English UK`() {
            val translation = cut(german, englishGb, germanText)
            val similarity = similarity(translation, englishGbText)
            similarity shouldBeGreaterThan 0.9
        }

        @Test
        fun `translates from English UK to English US`() {
            val translation = cut(englishGb, englishUs, englishGbText)
            val similarity = similarity(translation, englishUsText)
            similarity shouldBeGreaterThan 0.9
        }
    }
}
