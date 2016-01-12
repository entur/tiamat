package no.rutebanken.tiamat.nvdb.service;

import org.junit.Test;

public class NvdbSearchServiceTest {

    @Test
    public void testSearch() throws Exception {


        new NvdbSearchService().search("Gamle Somavei");

    }
}