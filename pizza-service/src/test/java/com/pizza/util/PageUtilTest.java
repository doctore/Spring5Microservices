package com.pizza.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageUtilTest {

    @Autowired
    private PageUtil pageUtil;


    @Test
    public void buildPageRequest_whenNullSortIsGiven_thenNoSortIsConfigured() {
        // Given
        int page = 1;
        int size = 2;

        // When
        PageRequest pageRequest = pageUtil.buildPageRequest(page, size, null);

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
        PageRequest pageRequest = pageUtil.buildPageRequest(page, size, sort);

        // Then
        assertEquals(page, pageRequest.getPageNumber());
        assertEquals(size, pageRequest.getPageSize());
        assertEquals(sort, pageRequest.getSort());
    }

}
