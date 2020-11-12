package com.atiurin.sampleapp.async

/**
 * An algebraic data type to provide either a [Failure][F] or a [Success][S] result.
 */
sealed class Either<out F, out S> {

    /**
     * Calls [failed] with the [failure][Failure.failure] value if result is a [Failure]
     * or [succeeded] with the [success][Success.success] value if result is a [Success]
     */
    inline fun <T> fold(failed: (F) -> T, succeeded: (S) -> T): T =
        when (this) {
            is Failure -> failed(failure)
            is Success -> succeeded(success)
        }
}

data class Failure<out F>(val failure: F) : Either<F, Nothing>()

data class Success<out S>(val success: S) : Either<Nothing, S>()

/**
 * Allows chaining of multiple calls taking as argument the [success][Success.success] value of the previous call and
 * returning an [Either].
 *
 * 1. Unwrap the result of the first call from the [Either] wrapper.
 * 2. Check if it is a [Success].
 * 3. If yes, call the next function (passed as [ifSucceeded]) with the value of the [success][Success.success]
 * property as an input parameter (chain the calls).
 * 4. If no, just pass the [Failure] through as the end result of the whole call chain.
 *
 * In case any of the calls in the chain returns a [Failure], none of the subsequent flatmapped functions is called
 * and the whole chain returns this failure.
 *
 * @param ifSucceeded next function which should be called if this is a [Success]. The [success][Success.success]
 * value will be then passed as the input parameter.
 */
inline fun <F, S1, S2> Either<F, S1>.flatMap(succeeded: (S1) -> Either<F, S2>): Either<F, S2> =
    fold({ this as Failure }, succeeded)

/**
 * Map the [Success] value of the [Either] to another value.
 *
 * You can for example map an `Success<String>` to an `Success<Int>` by
 * using the following code:
 * ```
 * val fiveString: Either<Nothing, String> = Success("5")
 * val fiveInt : Either<Nothing, Int> = fiveString.map { it.toInt() }
 * ```
 */
inline fun <F, S1, S2> Either<F, S1>.map(f: (S1) -> S2): Either<F, S2> =
    flatMap { Success(f(it)) }