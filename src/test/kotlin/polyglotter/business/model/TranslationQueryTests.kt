package polyglotter.business.model

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import polyglotter.Examples.german
import java.util.*

class TranslationQueryTests {

    @Test
    fun baseline() {
        shouldNotThrowAny { query() }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "\n"])
    fun `text must no be blank`(text: String) {
        shouldThrow<IllegalArgumentException> { query(text = text) }
    }

    @Test
    fun `at least one target language needs to be specified`() {
        shouldThrow<IllegalArgumentException> { query(targetLanguages = emptySet()) }
    }

    private fun query(
        text: String = "Hello World!",
        sourceLanguage: Locale? = null,
        targetLanguages: Set<Locale> = setOf(german),
    ) = TranslationQuery(text = text, sourceLanguage = sourceLanguage, targetLanguages = targetLanguages)
}
