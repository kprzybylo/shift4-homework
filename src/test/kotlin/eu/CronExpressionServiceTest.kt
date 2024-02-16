package eu

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CronExpressionServiceTest {
    private val cronExpressionService = CronExpressionService()

    @Test
    fun `should properly parse valid cron expression`() {
        val validCronExpression = "1,15 0 1-3 * 1-5 /usr/bin/find"

        val cronExpression = cronExpressionService.parseCronExpression(validCronExpression)

        cronExpression.command shouldBe "/usr/bin/find"
        cronExpression.minutes.interpret() shouldBe "1 15"
        cronExpression.hours.interpret() shouldBe "0"
        cronExpression.dayOfMonth.interpret() shouldBe "1 2 3"
        cronExpression.month.interpret() shouldBe "1 2 3 4 5 6 7 8 9 10 11 12"
        cronExpression.dayOfWeek.interpret() shouldBe "1 2 3 4 5"
    }

    @Test
    fun `should fail parsing cron expression if it is not in expected format`() {
        val invalidCronExpressions = listOf(" ", "* * * * /usr/bin/find", "*,15 * * * * /usr/bin/find")

        invalidCronExpressions.forEach {
            assertThrows<AssertionError> {
                cronExpressionService.parseCronExpression(it)
            }
        }
    }

    @Test
    fun `should properly interpret cron expression`() {
        val validCronExpression = "*/15 0 1-3 * 1-5 /usr/bin/find"

        val cronExpression = cronExpressionService.parseCronExpression(validCronExpression)

        val result = cronExpressionService.interpretCronExpression(cronExpression)

        val expectedResult =
            """
            minute          0 15 30 45
            hour            0
            day of month    1 2 3
            month           1 2 3 4 5 6 7 8 9 10 11 12
            day of week     1 2 3 4 5
            command         /usr/bin/find
            """.trimIndent()

        result shouldBe expectedResult
    }

    @Test
    fun `should properly interpret cron expression with months names`() {
        val validCronExpression = "*/15 0 1-3 JAN-DEC 1-5 /usr/bin/find"

        val cronExpression = cronExpressionService.parseCronExpression(validCronExpression)

        val result = cronExpressionService.interpretCronExpression(cronExpression)

        val expectedResult =
            """
            minute          0 15 30 45
            hour            0
            day of month    1 2 3
            month           1 2 3 4 5 6 7 8 9 10 11 12
            day of week     1 2 3 4 5
            command         /usr/bin/find
            """.trimIndent()

        result shouldBe expectedResult
    }
}
