package eu

class App {
    private val cronExpressionService = CronExpressionService()

    fun main(args: String) {
        val cronExpression = cronExpressionService.parseCronExpression(args)

        try {
            val interpretationResult = cronExpressionService.interpretCronExpression(cronExpression)
            println(interpretationResult)
        } catch (error: AssertionError) {
            println("Invalid cron expression: ${error.message}")
        }
    }
}
