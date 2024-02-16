package eu.cronparser.utils.validator

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TextFieldValidatorTest {
    private val allowedTextValues = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

    @Test
    fun `should validate text field`() {
        val testFieldExpressions = listOf("JAN", "JAN-DEC", "JAN,FEB,MAR")

        testFieldExpressions.forEach {
            TextFieldValidator.validate(it, allowedTextValues).isRight() shouldBe true
        }
    }

    @Test
    fun `should not construct month field with invalid values`() {
        val testFieldExpressions = listOf("*-,", "-1-59", "0-13", "1,2,3,*", "1,2,3,4,60", "FOO-BAR", "BAR")

        testFieldExpressions.forEach {
            TextFieldValidator.validate(it, allowedTextValues).isLeft() shouldBe true
        }
    }
}
