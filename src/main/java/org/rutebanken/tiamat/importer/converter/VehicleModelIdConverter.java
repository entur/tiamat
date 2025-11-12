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

package org.rutebanken.tiamat.importer.converter;

import org.rutebanken.tiamat.model.vehicle.VehicleModel;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Matches stops from nsr ID or imported/original ID
 */
@Transactional
@Component
public class VehicleModelIdConverter extends GenericIdConverter<org.rutebanken.netex.model.VehicleModel> {

    public VehicleModelIdConverter(ValidPrefixList validPrefixList, NetexIdHelper netexIdHelper, GenericEntityInVersionRepository genericEntityInVersionRepository) {
        super(validPrefixList, netexIdHelper, genericEntityInVersionRepository, VehicleModel.class);
    }
}
