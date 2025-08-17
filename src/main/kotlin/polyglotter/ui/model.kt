package polyglotter.ui

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*

data class TranslateForm(
    var text: String = "",
    var source: Language? = null,
    var target: Language = Language("en"),
)

@Suppress("MemberNameEqualsClassName")
data class Language(val locale: Locale) {
    constructor(language: String) : this(Locale.of(language))
    constructor(language: String, country: String) : this(Locale.of(language, country))

    val displayName: String = locale.getDisplayName(Locale.of("en"))
    val language: String = locale.language
    val direction: String =
        when (language) {
            "ar", "he" -> "rtl"
            else -> "ltr"
        }

    override fun toString() = locale.toString()
}

@Component
class StringToLanguageConverter : Converter<String, Language> {
    override fun convert(source: String): Language? {
        if (source == "") return null
        return StringUtils.parseLocale(source)?.let(::Language) ?: error("invalid locale: $source")
    }
}
