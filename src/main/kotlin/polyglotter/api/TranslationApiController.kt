package polyglotter.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import polyglotter.business.TranslateFunction
import polyglotter.business.model.TranslationQuery
import java.util.*

@RestController
@RequestMapping("/api/translate")
class TranslationApiController(
    private val translate: TranslateFunction,
) {

    @PostMapping
    fun doTranslation(@RequestBody query: TranslationQuery): TranslationResponse =
        TranslationResponse(
            translations = translate(query)
        )

    data class TranslationResponse(val translations: Map<Locale, String>)
}
