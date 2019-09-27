package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

@UtilityClass
public class CollectionUtil {

    /**
     * Return the unique elements of the given {@link Collection}s.
     *
     * @param collections
     *    {@link Collection}s to concat
     *
     * @return {@link LinkedHashSet}
     */
    public static <T> Set<T> concatUniqueElements(final Collection<T>... collections) {
        return ofNullable(collections)
                .map(c -> Stream.of(c).filter(Objects::nonNull)
                                      .flatMap(Collection::stream)
                                      .collect(toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>());
    }

}
