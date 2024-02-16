package eu.cronparser

interface CronField {
    val fieldExpression: String
    val allowedNumbersRange: IntRange

    fun validate()

    fun interpret(): String
}
