package eu

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CronExpressionServiceTest {
    private val cronExpressionService = CronExpressionService()

    @Test
    fun `should properly parse valid cron expression`() {
        val validCronExpression = "1,15 0 1-3 * 1-5 /usr/bin/find"

        val cronExpression = cronExpressionService.parseCronExpression(validCronExpression)

        assertEquals(cronExpression.command, "/usr/bin/find")
        assertEquals(cronExpression.minutes.interpret(), "1 15")
        assertEquals(cronExpression.hours.interpret(), "0")
        assertEquals(cronExpression.dayOfMonth.interpret(), "1 2 3")
        assertEquals(cronExpression.month.interpret(), "1 2 3 4 5 6 7 8 9 10 11 12")
        assertEquals(cronExpression.dayOfWeek.interpret(), "1 2 3 4 5")
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
        val validCronExpression = "1,15 0 1-3 * 1-5 /usr/bin/find"

        val cronExpression = cronExpressionService.parseCronExpression(validCronExpression)

        val result = cronExpressionService.interpretCronExpression(cronExpression)

        val expectedResult =
            """
            minute          1 15
            hour            0
            day of month    1 2 3
            month           1 2 3 4 5 6 7 8 9 10 11 12
            day of week     1 2 3 4 5
            command         /usr/bin/find
            """.trimIndent()

        assertEquals(result, expectedResult)
    }
}
