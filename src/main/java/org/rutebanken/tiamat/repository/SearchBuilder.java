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

package org.rutebanken.tiamat.repository;


import org.hibernate.SQLQuery;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class SearchBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SearchBuilder.class);

    protected static BasicFormatterImpl basicFormatter = new BasicFormatterImpl();

    public void addWheres(StringBuilder generatedSql, List<String> wheres, List<String> operators) {

        for (int i = 0; i < wheres.size(); i++) {
            if (i > 0) {
                generatedSql.append(operators.get(i - 1));
            } else {
                generatedSql.append("where");
            }
            generatedSql.append(' ').append(wheres.get(i)).append(' ');
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
}
