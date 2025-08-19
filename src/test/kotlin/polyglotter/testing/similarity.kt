package polyglotter.testing

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import kotlin.random.Random

private val log = LoggerFactory.getLogger("polyglotter.testing.similarity")

@Suppress("ReturnCount")
fun similarity(a: String, b: String): Double {
    if (a == b) return 1.0

    val maxLen = maxOf(a.length, b.length)
    if (maxLen == 0) return 1.0

    val similarity = 1.0 - (levenshteinDistance(a, b).toDouble() / maxLen.toDouble())
    log.debug("Not a perfect match!\nA:\n{}\n\nB:\n{}", a, b)
    return similarity
}

fun levenshteinDistance(a: String, b: String): Int {
    val n = a.length
    val m = b.length

    var prev = IntArray(m + 1) { it }
    var curr = IntArray(m + 1)

    for (i in 1..n) {
        curr[0] = i
        val si = a[i - 1]
        for (j in 1..m) {
            val cost = if (si == b[j - 1]) 0 else 1
            val deletion = prev[j] + 1
            val insertion = curr[j - 1] + 1
            val substitution = prev[j - 1] + cost
            curr[j] = minOf(deletion, insertion, substitution)
        }
        val tmp = prev
        prev = curr
        curr = tmp
    }
    return prev[m]
}

class SimilarityTests {

    @Test
    fun `similarity is the percentage of two strings overlapping`() {
        similarity("abcdefghij", "_bcdefghi_") shouldBe 0.8
    }

    @Test
    fun `equal strings have a similarity of 100 percent`() = runTest {
        forAll(Arb.string()) { similarity(it, it) == 1.0 }
    }

    @Test
    fun `similarity is the same regardless of parameter order`() = runTest {
        forAll(Arb.string(), Arb.string()) { a, b -> similarity(a, b) == similarity(b, a) }
    }

    @Test
    fun `similarity is never less than 0 percent`() = runTest {
        forAll(Arb.string(), Arb.string()) { a, b -> similarity(a, b) >= 0 }
    }

    @Test
    fun `similarity is never more than 100 percent`() = runTest {
        forAll(Arb.string(), Arb.string()) { a, b -> similarity(a, b) <= 1 }
    }
}

class LevenshteinDistanceTests {

    @Test
    fun `equal strings have a distance of 0`() = runTest {
        forAll(Arb.string()) { levenshteinDistance(it, it) == 0 }
    }

    @Test
    fun `distance is the same regardless of parameter order`() = runTest {
        forAll(Arb.string(), Arb.string()) { a, b -> levenshteinDistance(a, b) == levenshteinDistance(b, a) }
    }

    @Test
    fun `any non-empty string has its length as the distance compared to an empty string`() = runTest {
        forAll(Arb.string(minSize = 1)) { levenshteinDistance("", it) == it.length }
    }

    @Test
    fun `distance is never negative`() = runTest {
        forAll(Arb.string(), Arb.string()) { a, b -> levenshteinDistance(a, b) >= 0 }
    }

    @Test
    fun `single substitution returns distance of 1`() = runTest {
        val alphanumeric = Arb.string(1..25, codepoints = Codepoint.alphanumeric()) // random without '#'
        forAll(alphanumeric) { a ->
            val i = Random.nextInt(a.length)
            val b = a.take(i) + "#" + a.substring(i + 1) // substitute character at index 'i'
            levenshteinDistance(a, b) == 1
        }
    }

    @Test
    fun `single insertion returns distance of 1`() = runTest {
        val alphanumeric = Arb.string(1..25, codepoints = Codepoint.alphanumeric()) // random without '#'
        forAll(alphanumeric) { a ->
            val i = Random.nextInt(a.length)
            val b = a.take(i) + "#" + a.substring(i) // add character at index 'i'
            levenshteinDistance(a, b) == 1
        }
    }

    @Test
    fun `single deletion returns distance of 1`() = runTest {
        val alphanumeric = Arb.string(1..25, codepoints = Codepoint.alphanumeric()) // random without '#'
        forAll(alphanumeric) { a ->
            val i = Random.nextInt(a.length)
            val b = a.take(i) + a.substring(i + 1) // remove character at index 'i'
            levenshteinDistance(a, b) == 1
        }
    }
}
