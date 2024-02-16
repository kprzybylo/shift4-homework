package eu.cronparser

import arrow.core.getOrElse
import eu.cronparser.utils.validator.NumericFieldValidator
import eu.cronparser.utils.validator.TextFieldValidator

class MonthField(
    override val fieldExpression: String,
    override val allowedNumbersRange: IntRange = 1..12,
    override val supportedSpecialCharacters: List<SpecialCharacter> =
        listOf(
            SpecialCharacter.ASTERIKS,
            SpecialCharacter.COMMA,
            SpecialCharacter.HYPHEN,
        ),
) : CronField {
    private val allowedTextValues = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

    override fun validate() {
        if (NumericFieldValidator.isApplicable(fieldExpression)) {
            NumericFieldValidator.validate(fieldExpression, allowedNumbersRange).getOrElse { throw it }
        } else {
            TextFieldValidator.validate(fieldExpression, allowedTextValues).getOrElse { throw it }
        }
    }

    override fun interpret(): String {
        return if (NumericFieldValidator.isApplicable(fieldExpression)) {
            return NumericField(fieldExpression, allowedNumbersRange, supportedSpecialCharacters).interpret()
        } else {
            when {
                fieldExpression.contains(SpecialCharacter.COMMA.character) ->
                    fieldExpression
                        .split(",")
                        .map { allowedTextValues.indexOf(it) + 1 }
                        .joinToString(" ")
                fieldExpression.contains(SpecialCharacter.HYPHEN.character) -> {
                    val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                    val lowerBoundaryIndex = allowedTextValues.indexOf(expressionRange[0])
                    val upperBoundaryIndex = allowedTextValues.indexOf(expressionRange[1])
                    (lowerBoundaryIndex + 1..upperBoundaryIndex + 1).joinToString(" ")
                }
                else -> fieldExpression
            }
        }
    }
}
