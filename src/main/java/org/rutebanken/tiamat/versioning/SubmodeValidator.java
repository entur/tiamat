package org.rutebanken.tiamat.versioning;

import com.google.common.collect.Sets;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

/**
 * A stop place should not have multiple sub modes.
 * Validate this when saving stop places.
 * ROR-310
 */
@Service
public class SubmodeValidator {

    private static final Logger logger = LoggerFactory.getLogger(SubmodeValidator.class);


    public void validate(StopPlace stopToSave) {

        Set<String> subModes = Sets.newHashSet("airSubmode", "busSubmode", "coachSubmode", "funicularSubmode", "metroSubmode", "tramSubmode", "telecabinSubmode", "railSubmode", "waterSubmode");

        Optional<String> foundSubmode = Optional.empty();
        try {
            for(String submode : subModes) {
                logger.trace("Checking {}", submode);
                Object enumObject = getFieldValue(stopToSave, submode);

                if(enumObject == null) {
                    continue;
                }

                Object enumObjectValue = getFieldValue(enumObject, "value");
                if(enumObjectValue == null) {
                    continue;
                }

                if(!BusSubmodeEnumeration.UNKNOWN.value().equals(enumObjectValue) && !BusSubmodeEnumeration.UNDEFINED.value().equals(enumObjectValue)) {

                    if(foundSubmode.isPresent()) {
                        throw new IllegalArgumentException("A stop place cannot have multiple submodes. Found " + enumObjectValue + " and " + foundSubmode.get());
                    }

                    foundSubmode = Optional.of((String) enumObjectValue);
                }

            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not check submodes", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not access submodes", e);
        }

    }

    private Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }


}
