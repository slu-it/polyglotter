package polyglotter.ui

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import polyglotter.business.TranslateFunction
import polyglotter.business.exceptions.IndeterminableLanguageException
import polyglotter.business.model.TranslationQuery
import polyglotter.ui.UiConfig.maxInputChars

@Controller
@RequestMapping("/")
class UiController(
    private val translate: TranslateFunction,
) {

    @GetMapping
    fun init(model: Model): String {
        model.defaultAttributes()
        model.addAttribute("form", TranslateForm())
        return "translation"
    }

    @PostMapping
    fun translate(@ModelAttribute("form") form: TranslateForm, model: Model): String {
        validate(form)

        model.defaultAttributes()
        val sourceLanguage = form.source?.locale
        val targetLanguage = form.target.locale
        val query = TranslationQuery(
            text = form.text,
            sourceLanguage = sourceLanguage,
            targetLanguages = setOf(targetLanguage)
        )
        try {
            val translations = translate(query)
            form.source = (translations.keys - targetLanguage).firstOrNull()?.let(::Language)
            model.addAttribute("result", translations.getValue(targetLanguage))
        } catch (e: IndeterminableLanguageException) {
            model.addAttribute("error", e.message)
        }
        model.addAttribute("form", form)
        return "translation"
    }

    private fun validate(form: TranslateForm) {
        check(form.text.length <= maxInputChars) { "Text must not be longer than $maxInputChars characters!" }
    }

    private fun Model.defaultAttributes() {
        addAttribute("config", UiConfig)
        addAttribute("result", "")
        addAttribute("error", null)
    }
}
