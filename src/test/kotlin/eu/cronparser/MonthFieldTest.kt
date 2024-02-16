package eu.cronparser

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MonthFieldTest {
    private val testRange = 1..12

    @Test
    fun `should construct valid month field`() {
        val testFieldExpressions = listOf("*", "1-12", "1,2,3,4,5", "1", "JAN", "JAN-DEC", "JAN,FEB,MAR")

        testFieldExpressions.forEach {
            shouldNotThrow<AssertionError> {
                MonthField(it, testRange).validate()
            }
        }
    }

    @Test
    fun `should not construct month field with invalid values`() {
        val testFieldExpressions = listOf("*-,", "-1-59", "0-13", "1,2,3,*", "1,2,3,4,60", "FOO-BAR", "BAR")

        testFieldExpressions.forEach {
            assertThrows<AssertionError> {
                MonthField(it, testRange).validate()
            }
        }
    }

    @Test
    fun `should properly interpret wildcard month field`() {
        val testFieldExpression = "*"

        val minutesField = MonthField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (1..12).joinToString(" ")
    }

    @Test
    fun `should properly interpret range month numeric field`() {
        val testFieldExpression = "1-12"

        val minutesField = MonthField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (1..12).joinToString(" ")
    }

    @Test
    fun `should properly interpret range month text field`() {
        val testFieldExpression = "JAN-DEC"

        val minutesField = MonthField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (1..12).joinToString(" ")
    }

    @Test
    fun `should properly interpret array month numeric field`() {
        val testFieldExpression = "1,2,3,4,5"

        val minutesField = MonthField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe (1..5).joinToString(" ")
    }

    @Test
    fun `should properly interpret array month text field`() {
        val testFieldExpression = "JAN,FEB,MAR"

        val minutesField = MonthField(testFieldExpression, testRange)

        minutesField.interpret() shouldBe "1 2 3"
    }
}
