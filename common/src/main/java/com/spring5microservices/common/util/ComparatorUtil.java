package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.Comparator;

@UtilityClass
public class ComparatorUtil {

    /**
     * Returns {@link Comparator} keeping natural order but managing {@code null} values.
     *
     * @return null safe {@link Comparator} that considers the {@code null}s the smallest values
     */
    public static <T extends Comparable<? super T>> Comparator<T> safeNaturalOrderNullFirst() {
        return Comparator.nullsFirst(Comparator.naturalOrder());
    }


    /**
     * Returns {@link Comparator} keeping natural order but managing {@code null} values.
     *
     * @return null safe {@link Comparator} that considers the {@code null}s the largest values
     */
    public static <T extends Comparable<? super T>> Comparator<T> safeNaturalOrderNullLast() {
        return Comparator.nullsLast(Comparator.naturalOrder());
    }

}
