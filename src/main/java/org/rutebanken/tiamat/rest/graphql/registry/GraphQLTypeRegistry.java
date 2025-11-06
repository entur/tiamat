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

package org.rutebanken.tiamat.rest.graphql.registry;

import org.rutebanken.tiamat.rest.graphql.factories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Registry that aggregates all GraphQL type factories.
 * Reduces dependency injection complexity by providing a single point of access
 * to all type-related factories.
 */
@Component
public class GraphQLTypeRegistry {

    private final ValidBetweenTypeFactory validBetweenTypeFactory;
    private final CommonFieldsFactory commonFieldsFactory;
    private final QuayTypeFactory quayTypeFactory;
    private final AddressablePlaceTypeFactory addressablePlaceTypeFactory;
    private final TopographicPlaceTypeFactory topographicPlaceTypeFactory;
    private final TariffZoneTypeFactory tariffZoneTypeFactory;
    private final FareZoneTypeFactory fareZoneTypeFactory;
    private final PurposeOfGroupingTypeFactory purposeOfGroupingTypeFactory;
    private final GroupOfStopPlacesTypeFactory groupOfStopPlacesTypeFactory;
    private final GroupOfTariffZonesTypeFactory groupOfTariffZonesTypeFactory;
    private final EntityRefTypeFactory entityRefTypeFactory;
    private final PathLinkEndTypeFactory pathLinkEndTypeFactory;
    private final PathLinkTypeFactory pathLinkTypeFactory;
    private final TagTypeFactory tagTypeFactory;
    private final StopPlaceTypeFactory stopPlaceTypeFactory;
    private final ParentStopPlaceTypeFactory parentStopPlaceTypeFactory;
    private final GroupOfStopPlacesInputTypeFactory groupOfStopPlacesInputTypeFactory;
    private final PurposeOfGroupingInputTypeFactory purposeOfGroupingInputTypeFactory;
    private final ParentStopPlaceInputTypeFactory parentStopPlaceInputTypeFactory;

    @Autowired
    public GraphQLTypeRegistry(
            ValidBetweenTypeFactory validBetweenTypeFactory,
            CommonFieldsFactory commonFieldsFactory,
            QuayTypeFactory quayTypeFactory,
            AddressablePlaceTypeFactory addressablePlaceTypeFactory,
            TopographicPlaceTypeFactory topographicPlaceTypeFactory,
            TariffZoneTypeFactory tariffZoneTypeFactory,
            FareZoneTypeFactory fareZoneTypeFactory,
            PurposeOfGroupingTypeFactory purposeOfGroupingTypeFactory,
            GroupOfStopPlacesTypeFactory groupOfStopPlacesTypeFactory,
            GroupOfTariffZonesTypeFactory groupOfTariffZonesTypeFactory,
            EntityRefTypeFactory entityRefTypeFactory,
            PathLinkEndTypeFactory pathLinkEndTypeFactory,
            PathLinkTypeFactory pathLinkTypeFactory,
            TagTypeFactory tagTypeFactory,
            StopPlaceTypeFactory stopPlaceTypeFactory,
            ParentStopPlaceTypeFactory parentStopPlaceTypeFactory,
            GroupOfStopPlacesInputTypeFactory groupOfStopPlacesInputTypeFactory,
            PurposeOfGroupingInputTypeFactory purposeOfGroupingInputTypeFactory,
            ParentStopPlaceInputTypeFactory parentStopPlaceInputTypeFactory) {
        this.validBetweenTypeFactory = validBetweenTypeFactory;
        this.commonFieldsFactory = commonFieldsFactory;
        this.quayTypeFactory = quayTypeFactory;
        this.addressablePlaceTypeFactory = addressablePlaceTypeFactory;
        this.topographicPlaceTypeFactory = topographicPlaceTypeFactory;
        this.tariffZoneTypeFactory = tariffZoneTypeFactory;
        this.fareZoneTypeFactory = fareZoneTypeFactory;
        this.purposeOfGroupingTypeFactory = purposeOfGroupingTypeFactory;
        this.groupOfStopPlacesTypeFactory = groupOfStopPlacesTypeFactory;
        this.groupOfTariffZonesTypeFactory = groupOfTariffZonesTypeFactory;
        this.entityRefTypeFactory = entityRefTypeFactory;
        this.pathLinkEndTypeFactory = pathLinkEndTypeFactory;
        this.pathLinkTypeFactory = pathLinkTypeFactory;
        this.tagTypeFactory = tagTypeFactory;
        this.stopPlaceTypeFactory = stopPlaceTypeFactory;
        this.parentStopPlaceTypeFactory = parentStopPlaceTypeFactory;
        this.groupOfStopPlacesInputTypeFactory = groupOfStopPlacesInputTypeFactory;
        this.purposeOfGroupingInputTypeFactory = purposeOfGroupingInputTypeFactory;
        this.parentStopPlaceInputTypeFactory = parentStopPlaceInputTypeFactory;
    }

    // Getters for all type factories

    public ValidBetweenTypeFactory getValidBetweenTypeFactory() {
        return validBetweenTypeFactory;
    }

    public CommonFieldsFactory getCommonFieldsFactory() {
        return commonFieldsFactory;
    }

    public QuayTypeFactory getQuayTypeFactory() {
        return quayTypeFactory;
    }

    public AddressablePlaceTypeFactory getAddressablePlaceTypeFactory() {
        return addressablePlaceTypeFactory;
    }

    public TopographicPlaceTypeFactory getTopographicPlaceTypeFactory() {
        return topographicPlaceTypeFactory;
    }

    public TariffZoneTypeFactory getTariffZoneTypeFactory() {
        return tariffZoneTypeFactory;
    }

    public FareZoneTypeFactory getFareZoneTypeFactory() {
        return fareZoneTypeFactory;
    }

    public PurposeOfGroupingTypeFactory getPurposeOfGroupingTypeFactory() {
        return purposeOfGroupingTypeFactory;
    }

    public GroupOfStopPlacesTypeFactory getGroupOfStopPlacesTypeFactory() {
        return groupOfStopPlacesTypeFactory;
    }

    public GroupOfTariffZonesTypeFactory getGroupOfTariffZonesTypeFactory() {
        return groupOfTariffZonesTypeFactory;
    }

    public EntityRefTypeFactory getEntityRefTypeFactory() {
        return entityRefTypeFactory;
    }

    public PathLinkEndTypeFactory getPathLinkEndTypeFactory() {
        return pathLinkEndTypeFactory;
    }

    public PathLinkTypeFactory getPathLinkTypeFactory() {
        return pathLinkTypeFactory;
    }

    public TagTypeFactory getTagTypeFactory() {
        return tagTypeFactory;
    }

    public StopPlaceTypeFactory getStopPlaceTypeFactory() {
        return stopPlaceTypeFactory;
    }

    public ParentStopPlaceTypeFactory getParentStopPlaceTypeFactory() {
        return parentStopPlaceTypeFactory;
    }

    public GroupOfStopPlacesInputTypeFactory getGroupOfStopPlacesInputTypeFactory() {
        return groupOfStopPlacesInputTypeFactory;
    }

    public PurposeOfGroupingInputTypeFactory getPurposeOfGroupingInputTypeFactory() {
        return purposeOfGroupingInputTypeFactory;
    }

    public ParentStopPlaceInputTypeFactory getParentStopPlaceInputTypeFactory() {
        return parentStopPlaceInputTypeFactory;
    }
}
