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

import lombok.extern.slf4j.Slf4j;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.vehicle.CompositeFrame;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class TiamatComositeFrameExporter {

    private final String defaultLanguage;

    private final ExportTimeZone exportTimeZone;

    private final NetexIdHelper netexIdHelper;

    @Autowired
    public TiamatComositeFrameExporter(ExportTimeZone exportTimeZone,
                                       NetexIdHelper netexIdHelper,
                                       @Value("${tiamat.locals.language.default:nor}") String defaultLanguage
                                   ) {
        this.exportTimeZone = exportTimeZone;
        this.netexIdHelper = netexIdHelper;
        this.defaultLanguage = defaultLanguage;
    }


    public CompositeFrame createTiamatCompositeFrame(String description) {
        CompositeFrame compositeFrame = new CompositeFrame();
        setFrameDefaultLocale(compositeFrame);
        compositeFrame.setDescription(new MultilingualStringEntity(description));
        // siteFrame.setCreated(Instant.now()); // Disabled because of OffsetDateTimeInstantConverter issues during test
        compositeFrame.setVersion(1L);
        compositeFrame.setNetexId(netexIdHelper.getNetexId(compositeFrame, compositeFrame.hashCode()));
        return compositeFrame;
    }

    public void setFrameDefaultLocale(CompositeFrame compositeFrame) {

        LocaleStructure localeStructure = new LocaleStructure();
        localeStructure.setTimeZone(exportTimeZone.getDefaultTimeZoneId().toString());
        localeStructure.setDefaultLanguage(defaultLanguage);
        VersionFrameDefaultsStructure versionFrameDefaultsStructure = new VersionFrameDefaultsStructure();
        versionFrameDefaultsStructure.setDefaultLocale(localeStructure);
        compositeFrame.setFrameDefaults(versionFrameDefaultsStructure);
    }

}
