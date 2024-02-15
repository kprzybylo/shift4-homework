package eu

class App {
    private val cronExpressionService = CronExpressionService()

    fun main(args: String) {
        val cronExpression = cronExpressionService.parseCronExpression(args)
    }
}
