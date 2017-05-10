package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import org.junit.Test;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertTrue;

public class CustomGraphQLTypesTest {

    @Test
    public void testCustomEnums() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Field[] declaredFields = new CustomGraphQLTypes().getClass().getFields();
        final AtomicInteger counter = new AtomicInteger(0);
        for (Field enumField : declaredFields) {
            if (enumField.getType().equals(GraphQLEnumType.class)) {

                GraphQLEnumType enumType = (GraphQLEnumType) enumField.get(new CustomGraphQLTypes());
                List<GraphQLEnumValueDefinition> values = enumType.getValues();
                for (GraphQLEnumValueDefinition value : values) {


                    if (value.getValue() instanceof Enum &&
                            !(value.getValue() instanceof LimitationStatusEnumeration)) {

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
}
