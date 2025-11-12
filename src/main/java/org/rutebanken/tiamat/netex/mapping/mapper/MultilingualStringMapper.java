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

package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MultilingualStringMapper extends CustomMapper<MultilingualString, EmbeddableMultilingualString> {
    @Override
    public void mapAtoB(MultilingualString netexMLString, EmbeddableMultilingualString tiamatMLString, MappingContext context) {
        if(netexMLString == null ||
                netexMLString.getContent() == null ||
                netexMLString.getContent().isEmpty()) {
            return;
        }

        tiamatMLString.setLang(netexMLString.getLang());
        tiamatMLString.setValue(netexMLString.getContent().get(0).toString());
    }

    @Override
    public void mapBtoA(EmbeddableMultilingualString tiamatMLString, MultilingualString netexMLString, MappingContext context) {
        if(tiamatMLString == null ||
                tiamatMLString.getValue() == null ||
                tiamatMLString.getValue().isEmpty()) {
            return;
        }

        netexMLString.withLang(tiamatMLString.getLang());
        netexMLString.withContent(tiamatMLString.getValue());
    }
}
