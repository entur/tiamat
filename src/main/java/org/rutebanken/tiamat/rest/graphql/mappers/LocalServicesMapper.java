package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.AssistanceAvailabilityEnumeration;
import org.rutebanken.tiamat.model.AssistanceFacilityEnumeration;
import org.rutebanken.tiamat.model.AssistanceService;
import org.rutebanken.tiamat.model.LocalService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ASSISTANCE_AVAILABILITY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ASSISTANCE_FACILITY_LIST;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ASSISTANCE_SERVICE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LOCAL_SERVICES;


@Component(value = "GraphQLLocalServicesMapper")
public class LocalServicesMapper {
    public Optional<List<LocalService>> map(Map input) {
        if (input.get(LOCAL_SERVICES) != null) {
            List<LocalService> localServices = new ArrayList<>();

            Map<String, Object> localServiceInput = (Map) input.get(LOCAL_SERVICES);

            if (localServiceInput.get(ASSISTANCE_SERVICE) != null) {

                List localService = (List) localServiceInput.get(ASSISTANCE_SERVICE);
                for (Object item : localService) {
                    Map<String, Object> assistanceServiceInput = (Map<String, Object>) item;

                    AssistanceService assistanceService = new AssistanceService();
                    assistanceService.setAssistanceAvailability((AssistanceAvailabilityEnumeration) assistanceServiceInput.get(ASSISTANCE_AVAILABILITY));
                    assistanceService.setAssistanceFacilityList((List<AssistanceFacilityEnumeration>) assistanceServiceInput.get(ASSISTANCE_FACILITY_LIST));
                    localServices.add(assistanceService);
                }
            }

            return Optional.of(localServices);
        }
        return Optional.empty();
    }
}
