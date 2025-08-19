package polyglotter

import java.util.*

@Suppress("MayBeConst")
object Examples {
    val german = Locale.of("de")
    val germanText = "Ein Capybara ist in einem Aufzug stecken geblieben. " +
        "Es hat 3 stunden gedauert, aber dann war es wieder frei."
    val germanTextWithTypos = "Ein capybara, ist in einem aufzug setcken geblieben"
    val spanish = Locale.of("es")
    val spanishText = "Un capibara quedó atrapado en un ascensor. Tardaron 3 horas, pero luego volvió a estar libre."
    val english = Locale.of("en")
    val englishGb = Locale.of("en", "GB")
    val englishGbText = "A capybara got stuck in a lift. It took 3 hours, but then it was free again."
    val englishUs = Locale.of("en", "US")
    val englishUsText = "A capybara got stuck in an elevator. It took 3 hours, but then it was free again."
}
