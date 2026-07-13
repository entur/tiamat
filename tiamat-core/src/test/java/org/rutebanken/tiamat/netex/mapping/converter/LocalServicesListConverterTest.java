package org.rutebanken.tiamat.netex.mapping.converter;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.rutebanken.netex.model.AssistanceAvailabilityEnumeration;
import org.rutebanken.netex.model.AssistanceFacilityEnumeration;
import org.rutebanken.netex.model.LocalService_VersionStructure;
import org.rutebanken.netex.model.LocalServices_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AssistanceService;
import org.rutebanken.tiamat.model.LocalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LocalServicesListConverterTest extends TiamatIntegrationTest  {
    private final ObjectFactory netexObjectFactory = new ObjectFactory();
    private final Type<LocalServices_RelStructure> localServices_RelStructureType = new TypeBuilder<LocalServices_RelStructure>() {}.build();
    private final Type<List<LocalService>> localServicesType = new TypeBuilder<List<LocalService>>() {}.build();
    private final MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Autowired
    private LocalServiceListConverter localServiceListConverter;

    protected org.rutebanken.netex.model.AssistanceService createNetexAssistanceService() {
        org.rutebanken.netex.model.AssistanceService assistanceService = new org.rutebanken.netex.model.AssistanceService();
        assistanceService.setAssistanceAvailability(AssistanceAvailabilityEnumeration.AVAILABLE_IF_BOOKED);
        assistanceService.withAssistanceFacilityList(AssistanceFacilityEnumeration.BOARDING_ASSISTANCE, AssistanceFacilityEnumeration.PERSONAL_ASSISTANCE);
        return assistanceService;
    }

    protected AssistanceService createTiamatAssistanceService() {
        AssistanceService assistanceService = new AssistanceService();
        assistanceService.setNetexId("FSR:AssistanceService:1");
        assistanceService.setAssistanceAvailability(org.rutebanken.tiamat.model.AssistanceAvailabilityEnumeration.AVAILABLE_IF_BOOKED);
        assistanceService.setAssistanceFacilityList(Arrays.asList(org.rutebanken.tiamat.model.AssistanceFacilityEnumeration.BOARDING_ASSISTANCE,
                org.rutebanken.tiamat.model.AssistanceFacilityEnumeration.PERSONAL_ASSISTANCE));
        return assistanceService;
    }

    protected LocalServices_RelStructure getLocalServices_RelStructure(org.rutebanken.netex.model.AssistanceService assistanceService) {
        LocalServices_RelStructure localServices_relStructure = new LocalServices_RelStructure();
        JAXBElement<LocalService_VersionStructure> localServiceVersionStructureJAXBElement = netexObjectFactory.createLocalService(assistanceService);
        return localServices_relStructure.withLocalServiceRefOrLocalService(localServiceVersionStructureJAXBElement);
    }

    @Test
    public void convertFromRelStructure() {
        org.rutebanken.netex.model.AssistanceService netexAssistanceService = createNetexAssistanceService();
        LocalServices_RelStructure localServices_relStructure = getLocalServices_RelStructure(netexAssistanceService);

        List<LocalService> localServices = localServiceListConverter.convertFrom(localServices_relStructure, localServicesType, mappingContext);
        assertThat(localServices).isNotNull();
        Assertions.assertThat(localServices.size()).isEqualTo(1);
        AssistanceService tiamatAssistanceService = (AssistanceService) localServices.get(0);
        assertThat(tiamatAssistanceService.getAssistanceAvailability().value()).isEqualTo(netexAssistanceService.getAssistanceAvailability().value());
        assertThat(tiamatAssistanceService.getAssistanceFacilityList().size()).isEqualTo(netexAssistanceService.getAssistanceFacilityList().size());
    }


    @Test
    public void convertToTiamat() {
        AssistanceService tiamatAssistanceService = createTiamatAssistanceService();
        List<LocalService> localServices = Collections.singletonList(tiamatAssistanceService);

        LocalServices_RelStructure localServices_relStructure = localServiceListConverter.convertTo(localServices, localServices_RelStructureType, mappingContext);
        assertThat(localServices_relStructure).isNotNull();
        Assertions.assertThat(localServices_relStructure.getLocalServiceRefOrLocalService().size()).isEqualTo(1);
        org.rutebanken.netex.model.AssistanceService netexAssistanceService = (org.rutebanken.netex.model.AssistanceService) localServices_relStructure.getLocalServiceRefOrLocalService().get(0).getValue();
        assertThat(netexAssistanceService.getAssistanceAvailability().value()).isEqualTo(tiamatAssistanceService.getAssistanceAvailability().value());
        assertThat(netexAssistanceService.getAssistanceFacilityList().size()).isEqualTo(tiamatAssistanceService.getAssistanceFacilityList().size());
        assertThat(netexAssistanceService.getId()).isEqualTo(tiamatAssistanceService.getNetexId());
    }
}
