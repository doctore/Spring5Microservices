package com.spring5microservices.common.util.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import static java.util.Optional.ofNullable;

@Getter
@EqualsAndHashCode
public class ValidationError implements Comparable<ValidationError> {

    private final int priority;          // Greater means more priority
    private final String errorMessage;


    /**
     * Construct an {@code ValidationError}.
     *
     * @param priority
     *    The importance of the current error
     * @param errorMessage
     *    Error message
     */
    private ValidationError(final int priority,
                            final String errorMessage) {
        this.priority = priority;
        this.errorMessage = errorMessage;
    }


    /**
     * Returns an {@code ValidationError} adding the given {@code priority} and {@code errorMessage}.
     *
     * @param priority
     *    The importance of the current error
     * @param errorMessage
     *    Error message
     *
     * @return {@code ValidationError}
     */
    public static ValidationError of(final int priority,
                                     final String errorMessage) {
        return new ValidationError(
                priority,
                errorMessage
        );
    }


    @Override
    public int compareTo(final ValidationError other) {
        return ofNullable(other)
                .map(o -> priority - o.priority)
                .orElse(1);
    }

}