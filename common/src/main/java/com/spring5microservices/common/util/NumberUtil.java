package com.spring5microservices.common.util;

import com.spring5microservices.common.util.either.Either;
import com.spring5microservices.common.util.either.Left;
import com.spring5microservices.common.util.either.Right;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.spring5microservices.common.util.ExceptionUtil.getRootCause;
import static com.spring5microservices.common.util.ObjectUtil.getOrElse;
import static com.spring5microservices.common.util.either.Either.left;
import static com.spring5microservices.common.util.either.Either.right;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@UtilityClass
@Log4j2
public class NumberUtil {

    /**
     *    Compares provided {@link BigDecimal}s taking into account the number of decimals included in the parameter
     * {@code numberOfDecimals}. {@link RoundingMode#HALF_UP} will be used in the comparison.
     *
     * @param one
     *    {@link BigDecimal} of the "left side" of compare method
     * @param two
     *    {@link BigDecimal} of the "right side" of compare method
     * @param numberOfDecimals
     *    Number of decimals used for comparison
     *
     * @return {@code one#compareTo(two)} using {@code numberOfDecimals} as precision.
     *
     * @throws IllegalArgumentException if {@code numberOfDecimals} is less than {@code zero}
     */
    public static int compare(final BigDecimal one,
                              final BigDecimal two,
                              final int numberOfDecimals) {
        return compare(
                one,
                two,
                numberOfDecimals,
                RoundingMode.HALF_UP
        );
    }


    /**
     *    Compares provided {@link BigDecimal}s taking into account the number of decimals included in the parameter
     * {@code numberOfDecimals}.
     *
     * @param one
     *    {@link BigDecimal} of the "left side" of compare method
     * @param two
     *    {@link BigDecimal} of the "right side" of compare method
     * @param numberOfDecimals
     *    Number of decimals used for comparison
     * @param roundingMode
     *    {@link RoundingMode} used in the comparison. {@link RoundingMode#HALF_UP} if {@code null}
     *
     * @return {@code one#compareTo(two)} using {@code numberOfDecimals} as precision.
     *
     * @throws IllegalArgumentException if {@code numberOfDecimals} is less than {@code zero}
     */
    public static int compare(final BigDecimal one,
                              final BigDecimal two,
                              final int numberOfDecimals,
                              final RoundingMode roundingMode) {
        Assert.isTrue(
                0 <= numberOfDecimals,
                "numberOfDecimals must be equals or greater than 0"
        );
        if (isNull(one)) {
            return null == two
                    ? 0
                    : -1;
        }
        if (isNull(two)) {
            return 1;
        }
        final RoundingMode finalRoundingMode = getOrElse(
                roundingMode,
                RoundingMode.HALF_UP
        );
        final BigDecimal oneWithProvidedPrecision = one.setScale(
                numberOfDecimals,
                finalRoundingMode
        );
        final BigDecimal twoWithProvidedPrecision = two.setScale(
                numberOfDecimals,
                finalRoundingMode
        );
        return oneWithProvidedPrecision.compareTo(twoWithProvidedPrecision);
    }


    /**
     *    Returns an {@link Right} with {@link Integer} instance if is possible to do the conversion of given {@code potentialNumber}.
     * {@link Left} with the error message otherwise.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance.
     *
     * @return {@link Right} with {@link Optional} of {@link Integer} if provided {@code potentialNumber} could be converted,
     *         {@link Left} with the error message otherwise.
     */
    public static Either<String, Optional<Integer>> fromString(final String potentialNumber) {
        return fromString(
                potentialNumber,
                Integer.class
        );
    }


    /**
     *    Returns an {@link Right} with {@code clazzReturnedInstance} instance if is possible to do the conversion of given
     * {@code potentialNumber}. {@link Left} with the error message otherwise.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Number} instance.
     * @param clazzReturnedInstance
     *    {@link Number} subclass of the returned instance.
     *
     * @return {@link Right} with {@link Optional} of {@code clazzReturnedInstance} instance if provided {@code potentialNumber}
     *         could be converted. {@link Left} with the error message otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> Either<String, Optional<T>> fromString(final String potentialNumber,
                                                                            final Class<T> clazzReturnedInstance) {
        if (isNull(potentialNumber)) {
            return right(empty());
        }
        final Class<T> finalClazzReturnedInstance = (Class<T>) getOrElse(
                clazzReturnedInstance,
                Integer.class
        );
        try {
            Constructor<T> ctor = finalClazzReturnedInstance.getConstructor(String.class);
            return right(
                    of(
                            ctor.newInstance(potentialNumber)
                    )
            );
        }
        catch (Exception e) {
            String mainErrorMessage = format(
                    "There was an error trying to convert the string: %s to an instance of: %s",
                    potentialNumber,
                    finalClazzReturnedInstance.getName()
            );
            Throwable rootCauseOrProvided = getRootCause(e).orElse(e);
            return left(
                    format(mainErrorMessage + ". The cause was: %s with message: %s",
                            rootCauseOrProvided.getClass().getName(),
                            rootCauseOrProvided.getMessage()
                    )
            );
        }
    }


    /**
     * Converts a {@link String} to an {@link Byte}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Byte} instance
     *
     * @return {@link Byte} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Byte toByte(final String potentialNumber) {
        return toByte(
                potentialNumber,
                (byte) 0
        );
    }


    /**
     * Converts a {@link String} to an {@link Byte}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Byte} instance
     * @param defaultValue
     *    {@code byte} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Byte} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Byte toByte(final String potentialNumber,
                              final byte defaultValue) {
        return fromString(
                potentialNumber,
                Byte.class
        )
        .peekLeft(log::warn)
        .map(opt -> opt.orElse(defaultValue))
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Double}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Double} instance
     *
     * @return {@link Double} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Double toDouble(final String potentialNumber) {
        return toDouble(
                potentialNumber,
                0.0d
        );
    }


    /**
     * Converts a {@link String} to an {@link Double}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Double} instance
     * @param defaultValue
     *    {@code double} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Double} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Double toDouble(final String potentialNumber,
                                  final double defaultValue) {
        return fromString(
                potentialNumber,
                Double.class
        )
        .peekLeft(log::warn)
        .map(opt -> opt.orElse(defaultValue))
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Float}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Float} instance
     *
     * @return {@link Float} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Float toFloat(final String potentialNumber) {
        return toFloat(
                potentialNumber,
                0.0f
        );
    }


    /**
     * Converts a {@link String} to an {@link Float}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Float} instance
     * @param defaultValue
     *    {@code float} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Float} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Float toFloat(final String potentialNumber,
                                final float defaultValue) {
        return fromString(
                potentialNumber,
                Float.class
        )
        .peekLeft(log::warn)
        .map(opt -> opt.orElse(defaultValue))
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Integer}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance
     *
     * @return {@link Integer} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Integer toInteger(final String potentialNumber) {
        return toInteger(
                potentialNumber,
                0
        );
    }


    /**
     * Converts a {@link String} to an {@link Integer}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance
     * @param defaultValue
     *    {@code int} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Integer} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Integer toInteger(final String potentialNumber,
                                    final int defaultValue) {
        return fromString(
                potentialNumber,
                Integer.class
        )
        .peekLeft(log::warn)
        .map(opt -> opt.orElse(defaultValue))
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Long}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Long} instance
     *
     * @return {@link Long} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Long toLong(final String potentialNumber) {
        return toLong(
                potentialNumber,
                0
        );
    }


    /**
     * Converts a {@link String} to an {@link Long}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Long} instance
     * @param defaultValue
     *    {@code long} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Long} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Long toLong(final String potentialNumber,
                              final long defaultValue) {
        return fromString(
                potentialNumber,
                Long.class
        )
        .peekLeft(log::warn)
        .map(opt -> opt.orElse(defaultValue))
        .getOrElse(defaultValue);
    }


    /**
     * Converts a {@link String} to an {@link Short}, returning {@code zero} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Short} instance
     *
     * @return {@link Short} by the {@link String}, or {@code zero} if conversion fails.
     */
    public static Short toShort(final String potentialNumber) {
        return toShort(
                potentialNumber,
                (short) 0
        );
    }


    /**
     * Converts a {@link String} to an {@link Short}, returning {@code defaultValue} if the conversion fails.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Short} instance
     * @param defaultValue
     *    {@code short} with the value to return if {@code potentialNumber} cannot be converted
     *
     * @return {@link Short} by the {@link String}, or {@code defaultValue} if conversion fails.
     */
    public static Short toShort(final String potentialNumber,
                                final short defaultValue) {
        return fromString(
                potentialNumber,
                Short.class
        )
        .peekLeft(log::warn)
        .map(opt -> opt.orElse(defaultValue))
        .getOrElse(defaultValue);
    }

}
