package eu.cronparser.utils.validator

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import eu.cronparser.SpecialCharacter

object TextFieldValidator : FieldValidator<List<String>> {
    private val textPatternValidator = Regex("(^[A-Z]{3}-[A-Z]{3}\$)|(^[A-Z]{3}\$)|(^[A-Z]{3}(,[A-Z]{3})*\$)")

    override fun validate(
        fieldExpression: String,
        allowedValues: List<String>,
    ): Either<AssertionError, Unit> =
        either {
            ensure(textPatternValidator.matches(fieldExpression)) {
                AssertionError("Invalid text field value provided.")
            }
            if (fieldExpression.contains(SpecialCharacter.HYPHEN.character)) {
                val expressionRange = fieldExpression.split(SpecialCharacter.HYPHEN.character)
                val lowerBoundaryIndex = allowedValues.indexOf(expressionRange[0])
                val upperBoundaryIndex = allowedValues.indexOf(expressionRange[1])
                ensure(lowerBoundaryIndex >= 0 && upperBoundaryIndex >= 0 && lowerBoundaryIndex < upperBoundaryIndex) {
                    AssertionError(
                        "Invalid text range provided ($fieldExpression). Allowed range ${allowedValues[0]}-${allowedValues[allowedValues.size - 1]}",
                    )
                }
            }
            if (fieldExpression.contains(SpecialCharacter.COMMA.character)) {
                val expressionValues = fieldExpression.split(SpecialCharacter.COMMA.character)
                val unallowedMonthsProvided =
                    expressionValues.any { !allowedValues.contains(it) }
                ensure(!unallowedMonthsProvided) {
                    AssertionError("Unallowed text value ($unallowedMonthsProvided) provided. Allowed values: $allowedValues.")
                }
            }
            if (fieldExpression.length == 3) {
                val isIncluded = allowedValues.contains(fieldExpression)
                ensure(isIncluded) {
                    AssertionError("Provided text name is not valid ($fieldExpression).")
                }
            }
        }

    override fun isApplicable(fieldExpression: String): Boolean {
        return textPatternValidator.matches(fieldExpression)
    }
}
