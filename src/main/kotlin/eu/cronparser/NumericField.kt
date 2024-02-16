package eu.cronparser

import java.lang.NumberFormatException

open class NumericField(
    override val fieldExpression: String,
    override val allowedNumbersRange: IntRange,
    override val supportedSpecialCharacters: List<SpecialCharacter> =
        listOf(SpecialCharacter.ASTERIKS, SpecialCharacter.COMMA, SpecialCharacter.HYPHEN),
) : CronField {
    private val patternValidator =
        Regex("(^\\d{1,2}-\\d{1,2}\$)|(^[*]{1}\$)|(^\\d{0,2}(,\\d{0,2})*\$)|(^\\d{0,2}\$)|(^(([*]{1})|(\\d{1,2}))/\\d{0,2}\$)")

    override fun validate() {
        assert(patternValidator.matches(fieldExpression)) {
            "Invalid expression provided for numeric field. $fieldExpression"
        }
        if (fieldExpression.contains(SpecialCharacter.HYPHEN.character)) {
            val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
            val lowerBoundary = expressionRange[0].toInt()
            val upperBoundary = expressionRange[1].toInt()
            assert(
                lowerBoundary >= allowedNumbersRange.first && upperBoundary <= allowedNumbersRange.last && lowerBoundary < upperBoundary,
            ) {
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
        if (fieldExpression.contains(SpecialCharacter.SLASH.character)) {
            getStartAndStepValueForSlash()
        }
        try {
            val num = fieldExpression.toInt()
            assert(allowedNumbersRange.contains(num)) {
                "Numeric field value ($num) out of allowed range (${allowedNumbersRange.first}..${allowedNumbersRange.last})."
            }
        } catch (_: NumberFormatException) {
        }
    }

    override fun interpret(): String {
        return when {
            fieldExpression.contains(SpecialCharacter.SLASH.character) ->
                getStartAndStepValueForSlash().let {
                    IntProgression.fromClosedRange(it.first, allowedNumbersRange.last, it.second)
                }.joinToString(" ")
            fieldExpression.contains(SpecialCharacter.COMMA.character) -> fieldExpression.replace(",", " ")
            fieldExpression.contains(SpecialCharacter.HYPHEN.character) -> {
                val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                val lowerBoundary = expressionRange[0].toInt()
                val upperBoundary = expressionRange[1].toInt()
                (lowerBoundary..upperBoundary).joinToString(" ")
            }
            fieldExpression.contains(SpecialCharacter.ASTERIKS.character) -> allowedNumbersRange.joinToString(" ")
            else -> fieldExpression
        }
    }

    private fun getStartAndStepValueForSlash(): Pair<Int, Int> {
        val expressionValues = fieldExpression.split(SpecialCharacter.SLASH.character)
        val startFrom =
            expressionValues[0].let {
                if (it == SpecialCharacter.ASTERIKS.character.toString()) {
                    allowedNumbersRange.first
                } else {
                    it.toInt()
                }
            }
        assert(startFrom >= allowedNumbersRange.first) {
            "Invalid initial step provided (${expressionValues[0]}. Allowed to start from ${allowedNumbersRange.first}"
        }
        val stepValue = expressionValues[1].toInt()
        assert(stepValue <= allowedNumbersRange.last) {
            "Invalid step value provided (${expressionValues[1]}. Cannot be higher than ${allowedNumbersRange.last}"
        }
        return Pair(startFrom, stepValue)
    }

    companion object {
        fun minutes(fieldExpression: String) = NumericField(fieldExpression, 0..59)

        fun hours(fieldExpression: String) = NumericField(fieldExpression, 0..23)

        fun dayOfMonth(fieldExpression: String) = NumericField(fieldExpression, 1..31)

        fun dayOfWeek(fieldExpression: String) = NumericField(fieldExpression, 0..6)
    }
}
