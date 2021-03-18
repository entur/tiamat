package org.rutebanken.tiamat.rest.graphql.fetchers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.Test;

/**
 * This is a test for the PagedList class.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 * @version 2020-06-08
 */
public class PagedListTest {

    @Test
    public void getPage_normalTinyPage_shouldSucceed() {
        List<String> data = List.of("a", "b", "c", "d", "e", "f", "g");
        PagedList<String> pagedList = new PagedList<>(data, 1);
        assertEquals(List.of("a"), pagedList.getPage(0));
        assertEquals(List.of("b"), pagedList.getPage(1));
        assertEquals(List.of("c"), pagedList.getPage(2));
        assertEquals(List.of("d"), pagedList.getPage(3));
        assertEquals(List.of("e"), pagedList.getPage(4));
        assertEquals(List.of("f"), pagedList.getPage(5));
        assertEquals(List.of("g"), pagedList.getPage(6));
        assertEquals(List.of(), pagedList.getPage(7));
        assertEquals(List.of(), pagedList.getPage(8));
    }

    @Test
    public void getPage_normalPage_shouldSucceed() {
        List<String> data = List.of("a", "b", "c", "d", "e", "f", "g");
        PagedList<String> pagedList = new PagedList<>(data, 2);
        assertEquals(List.of("a", "b"), pagedList.getPage(0));
        assertEquals(List.of("c", "d"), pagedList.getPage(1));
        assertEquals(List.of("e", "f"), pagedList.getPage(2));
        assertEquals(List.of("g"), pagedList.getPage(3));
        assertEquals(List.of(), pagedList.getPage(4));
        assertEquals(List.of(), pagedList.getPage(5));
    }

    @Test
    public void getPage_hugePage_shouldSucceed() {
        List<String> data = List.of("a", "b", "c", "d", "e", "f", "g");
        PagedList<String> pagedList = new PagedList<>(data, 23452);
        assertEquals(List.of("a", "b", "c", "d", "e", "f", "g"), pagedList.getPage(0));
        assertEquals(List.of(), pagedList.getPage(1));
        assertEquals(List.of(), pagedList.getPage(2));
    }
}