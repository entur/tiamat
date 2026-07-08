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
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.SiteRefs_RelStructure;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Converts between Set of SiteRefStructure (tiamat) and SiteRefs_RelStructure (NeTEx)
 * See also {@link StopPlaceRefsRelStructureConverter}
 */
@Component
public class SiteRefsRelStructureConverter extends BidirectionalConverter<Set<SiteRefStructure>, SiteRefs_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(SiteRefsRelStructureConverter.class);

    ObjectFactory netexObjectFactory = new ObjectFactory();

    @Override
    public SiteRefs_RelStructure convertTo(Set<SiteRefStructure> siteRefStructures, Type<SiteRefs_RelStructure> type, MappingContext mappingContext) {

        if (!CollectionUtils.isEmpty(siteRefStructures)) {
            SiteRefs_RelStructure siteRefs_relStructure = new SiteRefs_RelStructure();

            siteRefStructures.stream()
                    .peek(siteref -> System.out.println(siteref))
                    .map(siteRef -> mapperFacade.map(siteRef, org.rutebanken.netex.model.SiteRefStructure.class))
                    .map(netexSiteRef -> netexObjectFactory.createSiteRef(netexSiteRef))
                    .forEach(jaxbElement -> siteRefs_relStructure.getSiteRef().add(jaxbElement));

            return siteRefs_relStructure;
        }
        return null;
    }

    @Override
    public Set<SiteRefStructure> convertFrom(SiteRefs_RelStructure siteRefs_relStructure, Type<Set<SiteRefStructure>> type, MappingContext mappingContext) {
        logger.info("Converting from SiteRefs_RelStructure to tiamat internal model is not implemented");
        return null;
    }
}