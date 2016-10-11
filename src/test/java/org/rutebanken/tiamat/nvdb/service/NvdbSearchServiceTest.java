package org.rutebanken.tiamat.nvdb.service;

import com.vividsolutions.jts.geom.Envelope;
import org.rutebanken.tiamat.nvdb.model.VegObjekt;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Intended for manual testing.
 */
@Ignore
public class NvdbSearchServiceTest {

    @Test
    public void testSearch() throws Exception {

        Envelope envelope = new Envelope(10.486457, 10.491006, 59.864783, 59.866432);

        VegObjekt vegObjekt = new NvdbSearchService().search("Nesbru", envelope);


    }
}