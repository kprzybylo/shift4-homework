package eu

class NumericField(
    override val fieldExpression: String,
    override val allowedNumbersRange: IntRange,
    override val supportedSpecialCharacters: List<SpecialCharacter> =
        listOf(SpecialCharacter.ASTERIKS, SpecialCharacter.COMMA, SpecialCharacter.HYPHEN),
) : CronField {
    private val patternValidator = Regex("(^\\d{1,2}-\\d{1,2}\$)|(^[*]{1}\$)|(^\\d{0,2}(,\\d{0,2})*\$)|(^\\d{0,2}\$)")

    init {
        validate()
    }

    override fun validate() {
        assert(patternValidator.matches(fieldExpression)) {
            "Invalid expression provided for numeric field. $fieldExpression"
        }
        if (fieldExpression.contains(SpecialCharacter.HYPHEN.character)) {
            val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
            val lowerBoundary = expressionRange[0].toInt()
            val upperBoundary = expressionRange[1].toInt()
            assert(lowerBoundary >= allowedNumbersRange.first && upperBoundary <= allowedNumbersRange.last) {
                "Invalid numeric range provided ($lowerBoundary..$upperBoundary). Allowed range ${allowedNumbersRange.first}..${allowedNumbersRange.last}"
            }
        }
        if (fieldExpression.contains(SpecialCharacter.COMMA.character)) {
            val expressionValues = fieldExpression.split(SpecialCharacter.COMMA.character)
            val unallowedNumberProvided =
                expressionValues.any {
                    !allowedNumbersRange.contains(it.toInt())
                }
            assert(!unallowedNumberProvided) {
                "Unallowed numeric value ($unallowedNumberProvided) provided. Allowed range ${allowedNumbersRange.first}..${allowedNumbersRange.last}"
            }
        }
    }

    override fun interpret(): String {
        return when {
            fieldExpression.contains(SpecialCharacter.ASTERIKS.character) -> allowedNumbersRange.joinToString(" ")
            fieldExpression.contains(SpecialCharacter.COMMA.character) -> fieldExpression.replace(",", " ")
            fieldExpression.contains(SpecialCharacter.HYPHEN.character) -> {
                val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                val lowerBoundary = expressionRange[0].toInt()
                val upperBoundary = expressionRange[1].toInt()
                (lowerBoundary..upperBoundary).joinToString(" ")
            }
            else -> fieldExpression
        }
    }

    companion object {
        fun minutes(fieldExpression: String) = NumericField(fieldExpression, 0..59)

        fun hours(fieldExpression: String) = NumericField(fieldExpression, 0..23)

        fun dayOfMonth(fieldExpression: String) = NumericField(fieldExpression, 1..31)

        fun month(fieldExpression: String) = NumericField(fieldExpression, 1..12)

        fun dayOfWeek(fieldExpression: String) = NumericField(fieldExpression, 0..6)
    }
}
