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
            form.result = translations.getValue(targetLanguage)
        } catch (e: IndeterminableLanguageException) {
            model.addAttribute("error", e.message)
            form.result = ""
        }

        model.addAttribute("form", form)
        return "translation"
    }

    private fun Model.defaultAttributes() {
        addAttribute("config", UiConfig)
        addAttribute("error", null)
    }
}
