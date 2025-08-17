package polyglotter.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.json.JsonCompareMode.STRICT
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.ContentResultMatchersDsl
import polyglotter.Examples.englishUs
import polyglotter.Examples.german
import polyglotter.Examples.spanish
import polyglotter.business.TranslateFunction
import polyglotter.business.model.TranslationQuery

@MockkBean(TranslateFunction::class)
@WebMvcTest(TranslationController::class)
class TranslationControllerTests(
    @Autowired private val translate: TranslateFunction,
    @Autowired private val mockMvc: MockMvc,
) {

    @Test
    fun `maximum request with maximum response`() {
        val expectedQuery = TranslationQuery(
            text = "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
            sourceLanguage = german,
            targetLanguages = setOf(englishUs, spanish)
        )
        every { translate(expectedQuery) } returns mapOf(
            german to "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
            englishUs to "A capybara goes shopping quickly before the holiday.",
            spanish to "Un capibara va de compras rápidamente antes del día festivo.",
        )

        postApiTranslate(
            """
            {
              "text": "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
              "sourceLanguage": "de",
              "targetLanguages": [
                "en_US",
                "es"
              ]
            }
            """
        ).andExpect {
            status { isOk() }
            content {
                contentType(APPLICATION_JSON)
                strictJson(
                    """
                    {
                      "translations": {
                        "de": "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
                        "en_US": "A capybara goes shopping quickly before the holiday.",
                        "es": "Un capibara va de compras rápidamente antes del día festivo."
                      }
                    }
                    """
                )
            }
        }
    }

    @Test
    fun `minimal request with minimal response`() {
        every { translate(any()) } returns mapOf(
            german to "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
            spanish to "Un capibara va de compras rápidamente antes del día festivo.",
        )

        postApiTranslate(
            """
            {
              "text": "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
              "targetLanguages": ["es"]
            }
            """
        ).andExpect {
            status { isOk() }
            content {
                contentType(APPLICATION_JSON)
                strictJson(
                    """
                    {
                      "translations": {
                        "de": "Ein Capybara geht noch schnell vor dem Feiertag einkaufen.",
                        "es": "Un capibara va de compras rápidamente antes del día festivo."
                      }
                    }
                    """
                )
            }
        }
    }

    private fun postApiTranslate(@Language("json") json: String) =
        mockMvc.post("/api/translate") {
            contentType = APPLICATION_JSON
            content = json
        }

    private fun ContentResultMatchersDsl.strictJson(@Language("json") json: String) =
        json(jsonContent = json.trimIndent(), compareMode = STRICT)
}
