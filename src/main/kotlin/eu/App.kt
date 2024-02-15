package eu

fun main(args: Array<String>) {
    val cronExpressionService = CronExpressionService()

    val cronExpression = cronExpressionService.parseCronExpression(args.first())

    try {
        val interpretationResult = cronExpressionService.interpretCronExpression(cronExpression)
        println(interpretationResult)
    } catch (error: AssertionError) {
        println("Invalid cron expression: ${error.message}")
    }
}
