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

package org.rutebanken.tiamat.config;

import org.rutebanken.tiamat.exporter.*;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import java.io.IOException;

@Configuration
public class StreamingPublicationDeliveryConfig {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    @Autowired
    private DeckPlanRepository deckPlanRepository;

    @Autowired
    private PublicationDeliveryCreator publicationDeliveryCreator;

    @Autowired
    private TiamatSiteFrameExporter tiamatSiteFrameExporter;

    @Autowired
    private TiamatServiceFrameExporter tiamatServiceFrameExporter;

    @Autowired
    private TiamatFareFrameExporter tiamatFareFrameExporter;

    @Autowired
    private TiamatResourceFrameExporter tiamatResourceFrameExporter;

    @Autowired
    private TiamatComositeFrameExporter tiamatComositeFrameExporter;

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private FareZoneRepository fareZoneRepository;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Autowired
    private GroupOfStopPlacesRepository groupOfStopPlacesRepository;

    @Autowired
    private GroupOfTariffZonesRepository groupOfTariffZonesRepository;

    @Autowired
    private NetexIdHelper netexIdHelper;

    @Value("${asyncNetexExport.validateAgainstSchema:false}")
    private boolean validateAsyncExport;

    @Value("${syncNetexExport.validateAgainstSchema:true}")
    private boolean validateSyncExport;

//    @Bean("asyncStreamingPublicationDelivery") TODO
//    public StreamingPublicationDelivery asyncStreamingPublicationDelivery() throws IOException, SAXException {
//        return createStreamingPublicationDelivery(validateAsyncExport);
//    }

    @Bean("syncStreamingPublicationDelivery")
    public StreamingPublicationDelivery syncStreamingPublicationDelivery() throws IOException, SAXException {
        return createStreamingPublicationDelivery(validateSyncExport);
    }

    private StreamingPublicationDelivery createStreamingPublicationDelivery(boolean validate) throws IOException, SAXException {
        return new StreamingPublicationDelivery(stopPlaceRepository, parkingRepository, vehicleRepository, vehicleTypeRepository, vehicleModelRepository, deckPlanRepository, publicationDeliveryCreator,
                tiamatSiteFrameExporter,tiamatServiceFrameExporter,tiamatFareFrameExporter,tiamatResourceFrameExporter, tiamatComositeFrameExporter, netexMapper, tariffZoneRepository, fareZoneRepository, topographicPlaceRepository,
                groupOfStopPlacesRepository,groupOfTariffZonesRepository, netexIdHelper, /* TODO validate,*/ purposeOfGroupingRepository);
    }
}
