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

package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import org.junit.Test;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomGraphQLTypesTest {

    @Test
    public void testAllCustomGraphQLEnumTypes() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Field[] declaredFields = new CustomGraphQLTypes().getClass().getFields();
        final AtomicInteger counter = new AtomicInteger(0);
        for (Field enumField : declaredFields) {
            if (enumField.getType().equals(GraphQLEnumType.class)) {

                GraphQLEnumType enumType = (GraphQLEnumType) enumField.get(new CustomGraphQLTypes());
                List<GraphQLEnumValueDefinition> values = enumType.getValues();
                for (GraphQLEnumValueDefinition value : values) {


                    if (value.getValue() instanceof Enum) {
                        if (value.getValue() instanceof LimitationStatusEnumeration) {
                            // Exception from the rule to avoid colliding with Boolean-values
                            continue;
                        }

                        //Calculate enum-object from name
                        Method m = value.getValue().getClass().getMethod("fromValue", String.class);

                        Object enumObject = m.invoke(value.getValue(), value.getName());

                        //Compare with resolved Enum-value
                        assertTrue(enumObject.equals(value.getValue()));
                        counter.incrementAndGet();
                    }
                }
            }
        }
        assertTrue("No custom GraphQL-enums have been tested!", counter.get() > 0);
    }

    @Test
    public void testVersionValidityEnum() {


        GraphQLEnumType myFreshEnum = CustomGraphQLTypes.createCustomEnumType("myFreshEnum", ExportParams.VersionValidity.class);
        assertThat(myFreshEnum.getValues()).extracting(graphQLEnumValueDefinition -> graphQLEnumValueDefinition.getName()).contains(
                ExportParams.VersionValidity.CURRENT.toString(),
                ExportParams.VersionValidity.CURRENT_FUTURE.toString(),
                ExportParams.VersionValidity.MAX_VERSION.toString(),
                ExportParams.VersionValidity.ALL.toString()
        );

    }
}
