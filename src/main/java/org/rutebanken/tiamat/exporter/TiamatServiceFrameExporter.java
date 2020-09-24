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

import org.rutebanken.tiamat.model.LocaleStructure;
import org.rutebanken.tiamat.model.MultilingualStringEntity;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.ScheduledStopPoint;
import org.rutebanken.tiamat.model.ScheduledStopPointsInFrame_RelStructure;
import org.rutebanken.tiamat.model.ServiceFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VersionFrameDefaultsStructure;

import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TiamatServiceFrameExporter {

    private final NetexIdHelper netexIdHelper;

    private final ExportTimeZone exportTimeZone;

    private static final Logger logger = LoggerFactory.getLogger(TiamatServiceFrameExporter.class);

    @Autowired
    public TiamatServiceFrameExporter(NetexIdHelper netexIdHelper, ExportTimeZone exportTimeZone) {

        this.netexIdHelper = netexIdHelper;
        this.exportTimeZone = exportTimeZone;
    }


    public ServiceFrame createTiamatServiceFrame(String description) {
        ServiceFrame serviceFrame= new ServiceFrame();
        serviceFrame.setDescription(new MultilingualStringEntity(description));
        setFrameDefaultLocale(serviceFrame);
        serviceFrame.setVersion(1L);
        serviceFrame.setNetexId(netexIdHelper.getNetexId(serviceFrame, serviceFrame.hashCode()));

        return serviceFrame;
    }

    public void addScheduledStopPointToTiamatServiceFrame(ServiceFrame serviceFrame, Iterable<StopPlace> iterableStopPlaces) {

        final ScheduledStopPointsInFrame_RelStructure scheduledStopPointsInFrame_relStructure = new ScheduledStopPointsInFrame_RelStructure();

        if (iterableStopPlaces != null) {
            iterableStopPlaces.forEach(stopPlace -> scheduledStopPointsInFrame_relStructure.getScheduledStopPoint().addAll(covertStopPlaceToScheduledStopPoint(stopPlace)));
            logger.info("Adding {} scheduled stop points", scheduledStopPointsInFrame_relStructure.getScheduledStopPoint().size());
            serviceFrame.setScheduledStopPoints(scheduledStopPointsInFrame_relStructure);
            if (serviceFrame.getScheduledStopPoints().getScheduledStopPoint().isEmpty()) {
                serviceFrame.setScheduledStopPoints(null);
            }
        }
    }

    private List<ScheduledStopPoint> covertStopPlaceToScheduledStopPoint(StopPlace stopPlace) {
        List<ScheduledStopPoint> scheduledStopPoints = new ArrayList<>();
        // Add stop place
        var stopPlaceNetexId = stopPlace.getNetexId().split(":")[0];
        var scheduledStopPointNetexId="NSR:NSR:ScheduledStopPoint:S"+stopPlaceNetexId;
        final ScheduledStopPoint scheduledStopPoint = new ScheduledStopPoint(scheduledStopPointNetexId, 1L);
        scheduledStopPoints.add(scheduledStopPoint);

        // Add quays
        final Set<Quay> quays = stopPlace.getQuays();
        for (Quay quay : quays) {
            var quayNetexId = "NSR:NSR:ScheduledStopPoint:Q"+quay.getNetexId().split(":")[0];
            scheduledStopPoints.add(new ScheduledStopPoint(quayNetexId,1L));
        }

        return scheduledStopPoints;
    }


    public void setFrameDefaultLocale(ServiceFrame serviceFrame) {

        LocaleStructure localeStructure = new LocaleStructure();
        localeStructure.setTimeZone(exportTimeZone.getDefaultTimeZoneId().toString());
        VersionFrameDefaultsStructure versionFrameDefaultsStructure = new VersionFrameDefaultsStructure();
        versionFrameDefaultsStructure.setDefaultLocale(localeStructure);
        serviceFrame.setFrameDefaults(versionFrameDefaultsStructure);
    }

}
