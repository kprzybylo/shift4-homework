package eu

interface CronField {
    val fieldExpression: String
    val supportedSpecialCharacters: List<SpecialCharacter>
    val allowedNumbersRange: IntRange

    fun validate()

    fun interpret(): String
}
