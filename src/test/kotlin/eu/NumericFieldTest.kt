package eu

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NumericFieldTest {
    private val testRange = 0..59

    @Test
    fun `should construct valid numeric field`() {
        val testFieldExpressions = listOf("*", "0-59", "1,2,3,4,5", "0")

        testFieldExpressions.forEach {
            NumericField(it, testRange) shouldNotBe null
        }
    }

    @Test
    fun `should not construct numeric field with invalid values`() {
        val testFieldExpressions = listOf("*-,", "-1-59", "0-120", "1,2,3,*", "1,2,3,4,60")

        testFieldExpressions.forEach {
            assertThrows<AssertionError> {
                NumericField(it, testRange)
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
}
