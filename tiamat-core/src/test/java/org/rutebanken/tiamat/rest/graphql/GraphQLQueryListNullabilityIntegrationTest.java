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

package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class GraphQLQueryListNullabilityIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void queryListsDeclareNonNullElements() {
        String query = "{ __schema { queryType { fields { name type { kind ofType { kind } } } } } }";
        for (String field : new String[]{"stopPlace", "stopPlaceBBox", "topographicPlace", "pathLink", "parking"}) {
            executeGraphqQLQueryOnly(query)
                    .body("data.__schema.queryType.fields.find { it.name == '" + field + "' }.type.kind", equalTo("LIST"))
                    .body("data.__schema.queryType.fields.find { it.name == '" + field + "' }.type.ofType.kind", equalTo("NON_NULL"));
        }
    }
}
