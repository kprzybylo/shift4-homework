package eu.cronparser.utils.validator

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eu.cronparser.SpecialCharacter
import java.lang.NumberFormatException

object NumericFieldValidator : FieldValidator<IntRange> {
    private val patternValidator =
        Regex("(^\\d{1,2}-\\d{1,2}\$)|(^[*]{1}\$)|(^\\d{0,2}(,\\d{0,2})*\$)|(^\\d{0,2}\$)|(^(([*]{1})|(\\d{1,2}))/\\d{0,2}\$)")

    override fun validate(
        fieldExpression: String,
        allowedValues: IntRange,
    ): Either<AssertionError, Unit> =
        either {
            ensure(patternValidator.matches(fieldExpression)) {
                AssertionError("Invalid expression provided for numeric field. $fieldExpression")
            }
            if (fieldExpression.contains(SpecialCharacter.HYPHEN.character)) {
                val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                val lowerBoundary = expressionRange[0].toInt()
                val upperBoundary = expressionRange[1].toInt()
                ensure(
                    lowerBoundary >= allowedValues.first && upperBoundary <= allowedValues.last && lowerBoundary < upperBoundary,
                ) {
                    AssertionError(
                        "Invalid numeric range provided ($lowerBoundary..$upperBoundary). Allowed range ${allowedValues.first}..${allowedValues.last}",
                    )
                }
            }
            if (fieldExpression.contains(SpecialCharacter.COMMA.character)) {
                val expressionValues = fieldExpression.split(SpecialCharacter.COMMA.character)
                val unallowedNumberProvided =
                    expressionValues.any {
                        !allowedValues.contains(it.toInt())
                    }
                ensure(!unallowedNumberProvided) {
                    AssertionError(
                        "Unallowed numeric value ($unallowedNumberProvided) provided. Allowed range ${allowedValues.first}..${allowedValues.last}",
                    )
                }
            }
            if (fieldExpression.contains(SpecialCharacter.SLASH.character)) {
                val expressionValues = fieldExpression.split(SpecialCharacter.SLASH.character)
                val startFrom =
                    expressionValues[0].let {
                        if (it == SpecialCharacter.ASTERIKS.character.toString()) {
                            allowedValues.first
                        } else {
                            it.toInt()
                        }
                    }
                ensure(startFrom >= allowedValues.first) {
                    AssertionError("Invalid initial step provided (${expressionValues[0]}). Allowed to start from ${allowedValues.first}")
                }
                val stepValue = expressionValues[1].toInt()
                ensure(stepValue <= allowedValues.last) {
                    AssertionError("Invalid step value provided (${expressionValues[1]}). Cannot be higher than ${allowedValues.last}")
                }
            }
            try {
                val num = fieldExpression.toInt()
                ensure(allowedValues.contains(num)) {
                    AssertionError("Numeric field value ($num) out of allowed range (${allowedValues.first}..${allowedValues.last}).")
                }
            } catch (_: NumberFormatException) {
            }
        }

    override fun isApplicable(fieldExpression: String): Boolean {
        return patternValidator.matches(fieldExpression)
    }
}
