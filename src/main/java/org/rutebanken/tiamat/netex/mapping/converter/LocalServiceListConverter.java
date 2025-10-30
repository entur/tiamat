package org.rutebanken.tiamat.netex.mapping.converter;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.AssistanceService;
import org.rutebanken.netex.model.LocalService_VersionStructure;
import org.rutebanken.netex.model.LocalServices_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.model.LocalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocalServiceListConverter extends BidirectionalConverter<List<LocalService>, LocalServices_RelStructure>  {
    private static final Logger logger = LoggerFactory.getLogger(LocalServiceListConverter.class);
    final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public LocalServices_RelStructure convertTo(List<LocalService> localServices, Type<LocalServices_RelStructure> type, MappingContext mappingContext) {
        if(localServices == null || localServices.isEmpty()) {
            return null;
        }

        LocalServices_RelStructure localServices_relStructure = new LocalServices_RelStructure();

        logger.debug("Mapping {} local services to netex", localServices.size());

        localServices.forEach(localService -> {
            org.rutebanken.netex.model.AssistanceService netexLocalService = mapperFacade.map(localService, AssistanceService.class);
            JAXBElement<AssistanceService> localServiceVersionStructureJAXBElement = objectFactory.createAssistanceService(netexLocalService);
            localServices_relStructure.getLocalServiceRefOrLocalService().add(localServiceVersionStructureJAXBElement);
        });
        return localServices_relStructure;
    }

    @Override
    public List<LocalService> convertFrom(LocalServices_RelStructure localServices_relStructure, Type<List<LocalService>> type, MappingContext mappingContext) {
        logger.debug("Mapping {} local services to internal model", localServices_relStructure != null ? localServices_relStructure.getLocalServiceRefOrLocalService().size() : 0);
        List<LocalService> localServices = new ArrayList<>();
        if(localServices_relStructure != null && localServices_relStructure.getLocalServiceRefOrLocalService() != null) {
            localServices_relStructure.getLocalServiceRefOrLocalService().stream()
                    .map(object -> {
                        return (LocalService_VersionStructure) object.getValue(); })
                    .filter(netexLocalService -> netexLocalService instanceof AssistanceService)
                    .map(netexLocalService -> {
                        return mapperFacade.map(netexLocalService, LocalService.class);
                    })
                    .forEach(localServices::add);
        }

        return localServices;
    }
}
