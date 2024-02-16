package eu.cronparser.utils.validator

import arrow.core.Either

interface FieldValidator<T> {
    fun isApplicable(fieldExpression: String): Boolean

    fun validate(
        fieldExpression: String,
        allowedValues: T,
    ): Either<AssertionError, Unit>
}
