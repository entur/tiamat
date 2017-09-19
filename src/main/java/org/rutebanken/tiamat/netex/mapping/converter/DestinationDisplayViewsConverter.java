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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.DestinationDisplayViews_RelStructure;
import org.rutebanken.tiamat.model.DestinationDisplayView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DestinationDisplayViewsConverter extends BidirectionalConverter<List<DestinationDisplayView>, DestinationDisplayViews_RelStructure> {

    @Override
    public DestinationDisplayViews_RelStructure convertTo(List<DestinationDisplayView> destinationDisplayViews, Type<DestinationDisplayViews_RelStructure> type, MappingContext mappingContext) {
        return null;
    }

    @Override
    public List<DestinationDisplayView> convertFrom(DestinationDisplayViews_RelStructure destinationDisplayViews_relStructure, Type<List<DestinationDisplayView>> type, MappingContext mappingContext
    ) {
        return null;
    }
}
