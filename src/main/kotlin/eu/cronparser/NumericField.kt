package eu.cronparser

import arrow.core.getOrElse
import eu.cronparser.utils.validator.NumericFieldValidator

open class NumericField(
    override val fieldExpression: String,
    override val allowedNumbersRange: IntRange,
) : CronField {
    override fun validate() {
        NumericFieldValidator.validate(fieldExpression, allowedNumbersRange).getOrElse { throw it }
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
