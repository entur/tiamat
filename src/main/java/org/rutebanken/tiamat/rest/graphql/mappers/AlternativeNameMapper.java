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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Component
public class AlternativeNameMapper {

    private static final Logger logger = LoggerFactory.getLogger(AlternativeNameMapper.class);

    public AlternativeName mapToAlternativeName(Map entry) {
        NameTypeEnumeration nameType = (NameTypeEnumeration) entry.getOrDefault(NAME_TYPE, NameTypeEnumeration.OTHER);
        EmbeddableMultilingualString name = getEmbeddableString((Map) entry.get(NAME));
        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setNameType(nameType);
        alternativeName.setName(name);
        return alternativeName;
    }
}
