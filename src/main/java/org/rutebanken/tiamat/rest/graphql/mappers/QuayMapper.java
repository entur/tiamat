package org.rutebanken.tiamat.rest.graphql.mappers;

import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class QuayMapper {

    public static final Logger logger = LoggerFactory.getLogger(QuayMapper.class);

    @Autowired
    private SiteElementMapper siteElementMapper;

    public boolean populateQuayFromInput(StopPlace stopPlace, Map quayInputMap) {
        Quay quay;
        if (quayInputMap.get(ID) != null) {
            Optional<Quay> existingQuay = stopPlace.getQuays().stream()
                    .filter(q -> q.getNetexId() != null)
                    .filter(q -> q.getNetexId().equals(quayInputMap.get(ID))).findFirst();

            Preconditions.checkArgument(existingQuay.isPresent(),
                    "Attempting to update Quay [id = %s] on StopPlace [id = %s] , but Quay does not exist on StopPlace",
                    quayInputMap.get(ID),
                    stopPlace.getNetexId());

            quay = existingQuay.get();
            logger.info("Updating Quay {} for StopPlace {}", quay.getNetexId(), stopPlace.getNetexId());
        } else {
            quay = new Quay();
            logger.info("Creating new Quay");
        }
        boolean isQuayUpdated = siteElementMapper.populate(quayInputMap, quay);

        if (quayInputMap.get(COMPASS_BEARING) != null) {
            quay.setCompassBearing(((BigDecimal) quayInputMap.get(COMPASS_BEARING)).floatValue());
            isQuayUpdated = true;
        }
        if (quayInputMap.get(PUBLIC_CODE) != null) {
            quay.setPublicCode((String) quayInputMap.get(PUBLIC_CODE));
            isQuayUpdated = true;
        }

        if(quayInputMap.get(PRIVATE_CODE) != null) {
            Map privateCodeInputMap = (Map) quayInputMap.get(PRIVATE_CODE);
            if(quay.getPrivateCode() == null) {
                quay.setPrivateCode(new PrivateCodeStructure());
            }
            quay.getPrivateCode().setType((String) privateCodeInputMap.get(TYPE));
            quay.getPrivateCode().setValue((String) privateCodeInputMap.get(VALUE));
            isQuayUpdated = true;
        }

        if (isQuayUpdated) {
            quay.setChanged(Instant.now());

            if (quay.getNetexId() == null) {
                stopPlace.getQuays().add(quay);
            }
        }
        return isQuayUpdated;
    }

}
