package eu

data class CronExpression(
    val minutes: CronField,
    val hours: CronField,
    val dayOfMonth: CronField,
    val month: CronField,
    val dayOfWeek: CronField,
    val command: String,
)
