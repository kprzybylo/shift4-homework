package eu

class CronExpressionService {
    fun parseCronExpression(expression: String): CronExpression {
        val cronFields = expression.split(" ")

        assert(cronFields.size == 6) {
            "Invalid cron expression format. It should contain five time fields and a command."
        }

        return CronExpression(
            minutes = NumericField.minutes(cronFields[0]),
            hours = NumericField.hours(cronFields[1]),
            dayOfMonth = NumericField.dayOfMonth(cronFields[2]),
            month = NumericField.month(cronFields[3]),
            dayOfWeek = NumericField.dayOfWeek(cronFields[4]),
            command = cronFields[5],
        )
    }

    fun interpretCronExpression(cronExpression: CronExpression): String {
        return """
            minute          ${cronExpression.minutes.interpret()}
            hour            ${cronExpression.hours.interpret()}
            day of month    ${cronExpression.dayOfMonth.interpret()}
            month           ${cronExpression.month.interpret()}
            day of week     ${cronExpression.dayOfWeek.interpret()}
            command         ${cronExpression.command}
            """.trimIndent()
    }
}
