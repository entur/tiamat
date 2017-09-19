/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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