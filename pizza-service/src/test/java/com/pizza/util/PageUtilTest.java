package com.pizza.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageUtilTest {

    @Test
    public void buildPageRequest_whenNullSortIsGiven_thenNoSortIsConfigured() {
        // Given
        int page = 1;
        int size = 2;

        // When
        PageRequest pageRequest = PageUtil.buildPageRequest(page, size, null);

        // Then
        assertEquals(page, pageRequest.getPageNumber());
        assertEquals(size, pageRequest.getPageSize());
        assertEquals(Sort.unsorted(), pageRequest.getSort());
    }


    @Test
    public void buildPageRequest_whenNotNullSortIsGiven_thenItWillBeIncludedInPageRequest() {
        // Given
        int page = 1;
        int size = 2;
        Sort sort = Sort.by(Sort.Direction.ASC, "property1");

        // When
        PageRequest pageRequest = PageUtil.buildPageRequest(page, size, sort);

        // Then
        assertEquals(page, pageRequest.getPageNumber());
        assertEquals(size, pageRequest.getPageSize());
        assertEquals(sort, pageRequest.getSort());
    }

}
