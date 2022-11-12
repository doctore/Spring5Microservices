package com.pizza.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static java.util.Optional.ofNullable;

/**
 *    Helper functions to work with ecosystem related with {@link Page} like:
 * {@link PageRequest}, {@link Sort}, etc
 */
@UtilityClass
public class PageUtil {

    /**
     * Generates a {@link PageRequest} to use in a database query
     *
     * @param page
     *    Number of page to get
     * @param size
     *    Number of elements in every page
     * @param sort
     *    {@link Sort} with the sorting configuration
     *
     * @return {@link PageRequest}
     */
    public static PageRequest buildPageRequest(final int page,
                                               final int size,
                                               final Sort sort) {
        return ofNullable(sort)
                .map(s ->
                        PageRequest.of(
                                page,
                                size,
                                sort
                        )
                )
                .orElseGet(() ->
                        PageRequest.of(
                                page,
                                size
                        )
                );
    }

}
