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

import org.rutebanken.tiamat.model.FareFrame;
import org.rutebanken.tiamat.model.LocaleStructure;
import org.rutebanken.tiamat.model.MultilingualStringEntity;
import org.rutebanken.tiamat.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TiamatFareFrameExporter {

    private final NetexIdHelper netexIdHelper;

    private final ExportTimeZone exportTimeZone;


    @Autowired
    public TiamatFareFrameExporter(NetexIdHelper netexIdHelper, ExportTimeZone exportTimeZone) {

        this.netexIdHelper = netexIdHelper;
        this.exportTimeZone = exportTimeZone;
    }


    public FareFrame createTiamatFareFrame(String description) {
        FareFrame fareFrame = new FareFrame();
        fareFrame.setDescription(new MultilingualStringEntity(description));
        setFrameDefaultLocale(fareFrame);
        fareFrame.setVersion(1L);
        fareFrame.setNetexId(netexIdHelper.getNetexId(fareFrame, fareFrame.hashCode()));

        return fareFrame;
    }



    public void setFrameDefaultLocale(FareFrame fareFrame) {

        LocaleStructure localeStructure = new LocaleStructure();
        localeStructure.setTimeZone(exportTimeZone.getDefaultTimeZoneId().toString());
        VersionFrameDefaultsStructure versionFrameDefaultsStructure = new VersionFrameDefaultsStructure();
        versionFrameDefaultsStructure.setDefaultLocale(localeStructure);
        fareFrame.setFrameDefaults(versionFrameDefaultsStructure);
    }

}
