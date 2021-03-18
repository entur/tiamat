package org.rutebanken.tiamat.rest.graphql.mappers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.InterchangeWeightingEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.rest.graphql.GraphQLNames;

public class StopPlaceMapperTest {

    private StopPlaceMapper stopPlaceMapper;
    private StopPlace stopPlace;
    private Map input;

    @BeforeEach
    void beforeEach() {
        QuayMapper quayMapper = mock(QuayMapper.class);
        GroupOfEntitiesMapper groupOfEntitiesMapper = mock(GroupOfEntitiesMapper.class);
        StopPlaceTariffZoneRefsMapper stopPlaceTariffZoneRefsMapper = mock(StopPlaceTariffZoneRefsMapper.class);
        ValidBetweenMapper validBetweenMapper = mock(ValidBetweenMapper.class);
        stopPlaceMapper = new StopPlaceMapper(quayMapper,
                groupOfEntitiesMapper,
                stopPlaceTariffZoneRefsMapper,
                validBetweenMapper);
        stopPlace = new StopPlace();
        input = new HashMap();
    }

    @Test
    public void populateStopPlaceFromInput_noInputDate_returnFalse() {
        boolean updated = stopPlaceMapper.populateStopPlaceFromInput(input, stopPlace);

        assertEquals(false, updated);
    }

    @Test
    public void populateStopPlaceFromInput_changedStopType_returnTrue() {
        input.put(GraphQLNames.STOP_PLACE_TYPE, StopTypeEnumeration.BUS_STATION);

        boolean updated = stopPlaceMapper.populateStopPlaceFromInput(input, stopPlace);

        assertEquals(true, updated);
    }

    @Test
    public void populateStopPlaceFromInput_changedWeighting_returnTrue() {
        input.put(GraphQLNames.WEIGHTING, InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED);

        boolean updated = stopPlaceMapper.populateStopPlaceFromInput(input, stopPlace);

        assertEquals(true, updated);
    }

    @Test
    public void populateStopPlaceFromInput_changedPublicCode_returnTrue() {
        input.put(GraphQLNames.PUBLIC_CODE, "123");

        boolean updated = stopPlaceMapper.populateStopPlaceFromInput(input, stopPlace);

        assertEquals(true, updated);
    }

    @Test
    public void populateStopPlaceFromInput_changedPrivateCode_returnTrue() {
        Map privateCodeInputMap = new HashMap();
        privateCodeInputMap.put("type", GraphQLNames.TYPE);
        privateCodeInputMap.put("value", GraphQLNames.VALUE);
        input.put(GraphQLNames.PRIVATE_CODE, privateCodeInputMap);

        boolean updated = stopPlaceMapper.populateStopPlaceFromInput(input, stopPlace);

        assertEquals(true, updated);
    }

    @Test
    public void populateStopPlaceFromInput_changedShortName_returnTrue() {
        Map map = new HashMap();
        map.put("type", GraphQLNames.TYPE);
        map.put("value", GraphQLNames.VALUE);
        input.put(GraphQLNames.SHORT_NAME, map);

        boolean updated = stopPlaceMapper.populateStopPlaceFromInput(input, stopPlace);

        assertEquals(true, updated);
    }
}