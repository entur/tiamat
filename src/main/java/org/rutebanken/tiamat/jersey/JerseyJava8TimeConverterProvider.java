package org.rutebanken.tiamat.jersey;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class JerseyJava8TimeConverterProvider implements ParamConverterProvider {

    private static final Logger logger = LoggerFactory.getLogger(JerseyJava8TimeConverterProvider.class);

    /**
     * See ${{@link org.rutebanken.tiamat.rest.graphql.scalars.DateScalar}}
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX";
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(Instant.class)) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(String incomingValueAsString) {
                    if (incomingValueAsString == null) {
                        return null;
                    }
                    try {
                        return rawType.cast(Instant.from(FORMATTER.parse(incomingValueAsString)));
                    } catch (DateTimeException e) {
                        String message = "Cannot parse string " + incomingValueAsString + " into Instant";

                        Optional<String> parameterName = resolveParameterName(annotations);
                        if(parameterName.isPresent()) {
                            message += " for parameter " + parameterName.get();
                        } else {
                            message += " for unresolvable parameter name";
                        }
                        throw new NotFoundException(message);
                    }
                }

                @Override
                public String toString(T t) {
                    return t.toString();
                }
            };
        }
        return null;
    }

    /**
     * Implemented to produce exception message. Not indended to perform well.
     * @param annotations
     * @return
     */
    private Optional<String> resolveParameterName(Annotation[] annotations) {

        try {
            for(Annotation annotation : annotations) {

                if(annotation.annotationType().isAssignableFrom(QueryParam.class)) {
                    return Optional.of(((QueryParam) annotation).value());
                }
            }
        } catch (Exception e) {
            logger.warn("Could not resolve jersey query parameter name from annotations.");
        }
        return Optional.empty();
    }
}
