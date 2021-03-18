package org.rutebanken.tiamat.rest.graphql.fetchers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to implement simple paging functionality.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 * @version 2020-06-08
 */
public class PagedList<E> {
    private final List<E> dataList;
    private final int pageSize;

    /**
     * Create a PagedList object with the supplied list of data and the specified page size.
     *
     * @param dataList the total list of data
     * @param pageSize the size of a page
     */
    public PagedList(List<E> dataList, int pageSize) {
        this.dataList = dataList;
        this.pageSize = pageSize;
    }

    /**
     * Retrieve a list with the data on the specified page. If the requested page is beyond the list
     * then an empty list is returned.
     *
     * @param page the requested page
     * @return a list with the data on the page
     */
    public List<E> getPage(int page) {
        return dataList.stream().skip(page * pageSize).limit(pageSize).collect(Collectors.toList());
    }
}
