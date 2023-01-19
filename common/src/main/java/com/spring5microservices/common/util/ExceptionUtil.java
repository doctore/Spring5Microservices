package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * Helper class to manage and get information related with {@link Throwable}.
 */
@UtilityClass
public class ExceptionUtil {

    /**
     *    Introspects the {@link Throwable} to obtain the root cause, walking through the exception chain to the last element,
     * "root" of the tree. If a cyclic is detected returns the previous {@link Throwable} to the repeated one.
     *
     * @param sourceThrowable
     *    {@link Throwable} to get the root cause for
     *
     * @return {@link Optional} containing the root cause of the provided {@link Throwable} or {@code sourceThrowable} if has no cause,
     *         {@link Optional#empty()} if {@code sourceThrowable} is {@code null}.
     */
    public static Optional<Throwable> getRootCause(final Throwable sourceThrowable) {
        return ofNullable(sourceThrowable)
                .map(ExceptionUtil::getThrowableList)
                .filter(l -> 0 < l.size())
                .map(l -> l.get(l.size() - 1));
    }


    /**
     *    Gets the {@link List} of {@link Throwable} objects in the exception chain included in provided {@code sourceThrowable}.
     * A {@link Throwable} without cause will return a list containing one element: {@code sourceThrowable}.
     * <p>
     *    This method handles recursive cause structures that might otherwise cause infinite loops. The cause chain is processed
     * until the end is reached, or until the next item in the chain is already in the result set.
     *
     * @param sourceThrowable
     *    {@link Throwable} to inspect
     *
     * @return {@link List} of {@link Throwable}
     */
    public static List<Throwable> getThrowableList(final Throwable sourceThrowable) {
        return ofNullable(sourceThrowable)
                .map(t -> {
                    final Set<Throwable> throwables = new LinkedHashSet<>();
                    for(Throwable current = t; nonNull(current) && !throwables.contains(current); current = current.getCause()) {
                        throwables.add(current);
                    }
                    return new ArrayList<>(throwables);
                })
                .orElseGet(ArrayList::new);
    }

}
