package polyglotter.business

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import polyglotter.Examples.englishGb
import polyglotter.Examples.englishGbText
import polyglotter.Examples.german
import polyglotter.Examples.germanText
import polyglotter.Examples.germanTextWithTypos
import polyglotter.Examples.spanish
import polyglotter.Examples.spanishText
import polyglotter.business.domain.DetectLanguageFunction
import polyglotter.business.domain.TranslateTextFunction
import polyglotter.business.exceptions.IndeterminableLanguageException
import polyglotter.business.model.TranslationQuery

class TranslateFunctionTests {

    private val detectLanguage: DetectLanguageFunction = mockk()
    private val translateText: TranslateTextFunction = mockk()
    private val cut = TranslateFunction(detectLanguage, translateText)

    private val query = TranslationQuery(
        text = germanText,
        sourceLanguage = german,
        targetLanguages = setOf(spanish, englishGb)
    )

    @BeforeEach
    fun stubBaselineBehaviour() {
        every { translateText(german, spanish, germanText) } returns spanishText
        every { translateText(german, englishGb, germanText) } returns englishGbText
    }

    @Test
    fun `translates given text into all target languages and returns combined result`() {
        val result = cut(query)

        result shouldBe mapOf(
            german to germanText,
            spanish to spanishText,
            englishGb to englishGbText
        )
    }

    @Test
    fun `detects source language if none was provided`() {
        every { detectLanguage(any()) } returns german
        cut(query.copy(sourceLanguage = null))
        verify { detectLanguage(germanText) }
    }

    @Test
    fun `throws exception if language detection failed`() {
        every { detectLanguage(any()) } returns null
        shouldThrow<IndeterminableLanguageException> {
            cut(query.copy(sourceLanguage = null))
        }
    }

    @Test
    fun `if source language is in target languages, the target translation is returned`() {
        every { translateText(german, german, germanTextWithTypos) } returns germanText

        val result = cut(
            TranslationQuery(
                text = germanTextWithTypos,
                sourceLanguage = german,
                targetLanguages = setOf(german)
            )
        )

        result shouldBe mapOf(german to germanText)
    }
}
