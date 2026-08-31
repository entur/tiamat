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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.GeneralSign;
import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateCodeFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(PrivateCodeFetcher.class);

    @Override
    public PrivateCodeStructure get(DataFetchingEnvironment dataFetchingEnvironment) {

        logger.trace("Fetching private code from source {}", (Object) dataFetchingEnvironment.getSource());

        PrivateCodeStructure privateCode = null;
        if(dataFetchingEnvironment.getSource() instanceof GeneralSign) {
            privateCode = ((GeneralSign) dataFetchingEnvironment.getSource()).getPrivateCode();
        } else if(dataFetchingEnvironment.getSource() instanceof GroupOfEntities_VersionStructure) {
            privateCode = ((GroupOfEntities_VersionStructure) dataFetchingEnvironment.getSource()).getPrivateCode();
        }

        return privateCode;
    }
}
