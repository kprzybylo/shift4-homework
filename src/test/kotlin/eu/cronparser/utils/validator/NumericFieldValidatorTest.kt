package eu.cronparser.utils.validator

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class NumericFieldValidatorTest {
    private val testRange = 0..59

    @Test
    fun `should validate numeric field`() {
        val testFieldExpressions = listOf("*", "0-59", "1,2,3,4,5", "0", "*/5", "1/5")

        testFieldExpressions.forEach {
            NumericFieldValidator.validate(it, testRange).isRight() shouldBe true
        }
    }

    @Test
    fun `should throw upon invalid numeric field`() {
        val testFieldExpressions = listOf("*-,", "-1-59", "0-120", "1,2,3,*", "1,2,3,4,60", "1/${testRange.last + 1}")

        testFieldExpressions.forEach {
            NumericFieldValidator.validate(it, testRange).isLeft() shouldBe true
        }
    }
}
