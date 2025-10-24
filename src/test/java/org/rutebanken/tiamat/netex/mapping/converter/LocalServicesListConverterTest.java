package org.rutebanken.tiamat.netex.mapping.converter;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.netex.model.AssistanceAvailabilityEnumeration;
import org.rutebanken.netex.model.AssistanceFacilityEnumeration;
import org.rutebanken.netex.model.LocalService_VersionStructure;
import org.rutebanken.netex.model.LocalServices_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.model.LocalService;

import java.util.HashMap;
import java.util.List;

public class LocalServicesListConverterTest {
    private final ObjectFactory netexObjectFactory = new ObjectFactory();
    private final Type<LocalServices_RelStructure> localServices_RelStructureType = new TypeBuilder<LocalServices_RelStructure>() {}.build();
    private final Type<List<LocalService>> localServicesType = new TypeBuilder<List<LocalService>>() {}.build();
    private MappingContext mappingContext = new MappingContext(new HashMap<>());
    private LocalServiceListConverter localServiceListConverter = new LocalServiceListConverter();

    protected org.rutebanken.netex.model.AssistanceService createNetexAssistanceService() {
        org.rutebanken.netex.model.AssistanceService assistanceService = new org.rutebanken.netex.model.AssistanceService();
        assistanceService.setAssistanceAvailability(AssistanceAvailabilityEnumeration.AVAILABLE_IF_BOOKED);
        assistanceService.withAssistanceFacilityList(AssistanceFacilityEnumeration.BOARDING_ASSISTANCE, AssistanceFacilityEnumeration.PERSONAL_ASSISTANCE);
        return assistanceService;
    }

    protected LocalServices_RelStructure getLocalServices_RelStructure(org.rutebanken.netex.model.AssistanceService assistanceService) {
        LocalServices_RelStructure localServices_relStructure = new LocalServices_RelStructure();
        JAXBElement<LocalService_VersionStructure> localServiceVersionStructureJAXBElement = netexObjectFactory.createLocalService(assistanceService);
        return localServices_relStructure.withLocalServiceRefOrLocalService(localServiceVersionStructureJAXBElement);
    }

    @Test
    public void convertFromRelStructure() throws Exception {
        org.rutebanken.netex.model.AssistanceService netexAssistanceService = createNetexAssistanceService();
        LocalServices_RelStructure localServices_relStructure = getLocalServices_RelStructure(netexAssistanceService);

        List<LocalService> localServices = localServiceListConverter.convertFrom(localServices_relStructure, localServicesType, mappingContext);
    }
}
