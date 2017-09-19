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
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.springframework.stereotype.Component;

@Component
public class PolygonFetcher implements DataFetcher {

    @Override
    public Object get(DataFetchingEnvironment environment) {
        if(environment.getSource() instanceof Zone_VersionStructure) {
            return ((Zone_VersionStructure) environment.getSource()).getPolygon();
        }
        return null;
    }
}
