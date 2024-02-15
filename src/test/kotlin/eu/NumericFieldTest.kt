package eu

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NumericFieldTest {
    private val testRange = 0..59

    @Test
    fun `should construct valid numeric field`() {
        val testFieldExpressions = listOf("*", "0-59", "1,2,3,4,5", "0")

        testFieldExpressions.forEach {
            assertNotNull(NumericField(it, testRange))
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

        assertEquals(minutesField.interpret(), (0..59).joinToString(" "))
    }

    @Test
    fun `should properly interpret range numeric field`() {
        val testFieldExpression = "0-20"

        val minutesField = NumericField(testFieldExpression, testRange)

        assertEquals(minutesField.interpret(), (0..20).joinToString(" "))
    }

    @Test
    fun `should properly interpret array numeric field`() {
        val testFieldExpression = "1,2,3,4,5"

        val minutesField = NumericField(testFieldExpression, testRange)

        assertEquals(minutesField.interpret(), (1..5).joinToString(" "))
    }
}
