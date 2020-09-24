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

package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.ScheduledStopPoint;
import org.rutebanken.tiamat.model.ServiceFrame;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TiamatServiceFrameExporterTest extends TiamatIntegrationTest {

    @Autowired
    private TiamatServiceFrameExporter tiamatServiceFrameExporter;

    @Test
    public void exportScheduledStopPointsInServiceFrame() {

        ServiceFrame serviceFrame = new ServiceFrame();


        List<StopPlace> stopPlaces = new ArrayList<>();


        final StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:03011521");

        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:0301152101");



        stopPlace.getQuays().add(quay);

        stopPlaces.add(stopPlace);
        tiamatServiceFrameExporter.addScheduledStopPointToTiamatServiceFrame(serviceFrame,stopPlaces);

        assertThat(serviceFrame.getScheduledStopPoints()).isNotNull();
        assertThat(serviceFrame.getScheduledStopPoints().getScheduledStopPoint()).hasSize(2);

    }


    @Test
    public void expectDefaulTimzone() {


        ServiceFrame serviceFrame = tiamatServiceFrameExporter.createTiamatServiceFrame("A fine site frame");

        assertThat(serviceFrame.getFrameDefaults()).isNotNull();
        assertThat(serviceFrame.getFrameDefaults().getDefaultLocale()).isNotNull();
        assertThat(serviceFrame.getFrameDefaults().getDefaultLocale().getTimeZone()).isNotNull();
        assertThat(serviceFrame.getFrameDefaults().getDefaultLocale().getTimeZone());
    }

}