package eu.cronparser

class MonthField(
    fieldExpression: String,
    allowedNumbersRange: IntRange = 1..12,
    supportedSpecialCharacters: List<SpecialCharacter> =
        listOf(
            SpecialCharacter.ASTERIKS,
            SpecialCharacter.COMMA,
            SpecialCharacter.HYPHEN,
        ),
) : NumericField(fieldExpression, allowedNumbersRange, supportedSpecialCharacters) {
    private val allowedTextValues = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
    private val textPatternValidator = Regex("(^[A-Z]{3}-[A-Z]{3}\$)|(^[A-Z]{3}\$)|(^[A-Z]{3}(,[A-Z]{3})*\$)")

    override fun validate() {
        val validationResult: AssertionError? =
            try {
                super.validate()
                null
            } catch (error: AssertionError) {
                error
            }
        if (validationResult != null) {
            assert(textPatternValidator.matches(fieldExpression)) {
                "Invalid months field value provided."
            }
            if (fieldExpression.contains(SpecialCharacter.HYPHEN.character)) {
                val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                val lowerBoundaryIndex = allowedTextValues.indexOf(expressionRange[0])
                val upperBoundaryIndex = allowedTextValues.indexOf(expressionRange[1])
                assert(lowerBoundaryIndex >= 0 && upperBoundaryIndex >= 0 && lowerBoundaryIndex < upperBoundaryIndex) {
                    "Invalid months range provided ($fieldExpression). Allowed range ${allowedNumbersRange.first}-${allowedNumbersRange.last}"
                }
            }
            if (fieldExpression.contains(SpecialCharacter.COMMA.character)) {
                val expressionValues = fieldExpression.split(SpecialCharacter.COMMA.character)
                val unallowedMonthsProvided =
                    expressionValues.any { !allowedTextValues.contains(it) }
                assert(!unallowedMonthsProvided) {
                    "Unallowed month value ($unallowedMonthsProvided) provided. Allowed months: $allowedTextValues."
                }
            }
            if (fieldExpression.length == 3) {
                val isIncluded = allowedTextValues.contains(fieldExpression)
                assert(isIncluded) {
                    "Provided month name is not valid ($fieldExpression)."
                }
            }
        }
    }

    override fun interpret(): String {
        if (textPatternValidator.matches(fieldExpression)) {
            return when {
                fieldExpression.contains(SpecialCharacter.COMMA.character) -> fieldExpression.replace(",", " ")
                fieldExpression.contains(SpecialCharacter.HYPHEN.character) -> {
                    val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                    val lowerBoundaryIndex = allowedTextValues.indexOf(expressionRange[0])
                    val upperBoundaryIndex = allowedTextValues.indexOf(expressionRange[1])
                    (lowerBoundaryIndex + 1..upperBoundaryIndex + 1).joinToString(" ")
                }
                else -> fieldExpression
            }
        }
        return super.interpret()
    }
}
