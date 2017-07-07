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
