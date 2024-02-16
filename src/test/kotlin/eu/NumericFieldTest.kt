package eu

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NumericFieldTest {
    private val testRange = 0..59

    @Test
    fun `should construct valid numeric field`() {
        val testFieldExpressions = listOf("*", "0-59", "1,2,3,4,5", "0", "*/5", "1/5")

        testFieldExpressions.forEach {
            shouldNotThrow<AssertionError> {
                NumericField(it, testRange).validate()
            }
        }
    }

    @Test
    fun `should not construct numeric field with invalid values`() {
        val testFieldExpressions = listOf("*-,", "-1-59", "0-120", "1,2,3,*", "1,2,3,4,60", "1/${testRange.last + 1}")

        testFieldExpressions.forEach {
            assertThrows<AssertionError> {
                NumericField(it, testRange).validate()
            }
        }
    }

    @Test
    fun `should properly interpret wildcard numeric field`() {
        val testFieldExpression = "*"

        val minutesField = NumericField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (0..59).joinToString(" ")
    }

    @Test
    fun `should properly interpret range numeric field`() {
        val testFieldExpression = "0-20"

        val minutesField = NumericField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (0..20).joinToString(" ")
    }

    @Test
    fun `should properly interpret array numeric field`() {
        val testFieldExpression = "1,2,3,4,5"

        val minutesField = NumericField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (1..5).joinToString(" ")
    }

    @Test
    fun `should properly interpret step numeric field`() {
        val testFieldExpression = "*/5"

        val minutesField = NumericField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe "0 5 10 15 20 25 30 35 40 45 50 55"
    }
}
