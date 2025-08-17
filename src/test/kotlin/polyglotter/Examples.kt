package polyglotter

import java.util.*

object Examples {
    val german = Locale.of("de")
    val germanText = "Ein Capybara ist in einem Aufzug stecken geblieben."
    val germanTextWithTypos = "Ein capybara, ist in einem aufzug setcken geblieben"
    val spanish = Locale.of("es")
    val spanishText = "Un carpincho se quedó atrapado en un ascensor."
    val english = Locale.of("en")
    val englishGb = Locale.of("en", "GB")
    val englishGbText = "A capybara got stuck in a lift."
    val englishUs = Locale.of("en", "US")
    val englishUsText = "A capybara got stuck in an elevator."
}
