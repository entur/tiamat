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

package org.rutebanken.tiamat.repository.search;


import org.hibernate.SQLQuery;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.rutebanken.tiamat.exporter.params.SearchObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class SearchHelper {

    private static final Logger logger = LoggerFactory.getLogger(SearchHelper.class);

    protected static BasicFormatterImpl basicFormatter = new BasicFormatterImpl();

    public void addWheres(StringBuilder queryString, List<String> wheres, List<String> operators) {

        for (int i = 0; i < wheres.size(); i++) {
            if (i > 0) {
                queryString.append(operators.get(i - 1));
            } else {
                queryString.append("where");
            }
            queryString.append(' ').append(wheres.get(i)).append(' ');
        }
    }

    public void addParams(SQLQuery sqlQuery, Map<String, Object> parameters) {
        parameters.forEach((parameter, value) -> {
            if (value instanceof Collection) {
                sqlQuery.setParameterList(parameter, (Collection) value);
            } else {
                sqlQuery.setParameter(parameter, value);
            }
        });
    }

    public String format(String query) {
        return basicFormatter.format(query);
    }

    public void addOrderByStatements(StringBuilder queryString, List<String> orderByStatements) {

        if(orderByStatements.isEmpty()) {
            return;
        }

        queryString.append( "order by ");
        for (int i = 0; i < orderByStatements.size(); i++) {
            if (i > 0) {
                queryString.append(',');
            }
            queryString.append(' ').append(orderByStatements.get(i)).append(' ');
        }
    }

    public void logIfLoggable(String generatedSql, Map<String, Object> parameters, SearchObject searchObject, Logger relevantLogger) {
        if (relevantLogger.isInfoEnabled()) {
            relevantLogger.info("sql: {}\nparams: {}\nSearch object: {}", generatedSql, parameters.toString(), searchObject.toString());
        }
    }
}
