package org.rutebanken.tiamat.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.samtrafiken.util.Assert;

public class DbQueryUtil {
    private static final Logger logger = LoggerFactory.getLogger(DbQueryUtil.class);

    /**
     * If we want to export 50k stop Areas without having a ridiculously large SQL Server instance,
     * we need to create efficient WHERE clauses instead of putting 50K ids inside one IN(...) operator.
     *
     * @param idsToResultInTrue The ids for which the where ... parameter should be generated
     * @return A boolean expression defining "true" rangesm, e.g. (x > 5 AND < 10) OR ...
     */
    static String createSaneWhereClause(String fieldname, Set<Long> idsToResultInTrue) {
        Assert.assertNotNull(fieldname,"Database fieldname cannot be null when generating a boolean statement for WHERE clauses");
        Assert.assertFalse(fieldname.isBlank(),"Database fieldname cannot be empty when generating a boolean statement for WHERE clauses");
        Assert.assertNotNull(idsToResultInTrue,"Id series cannot be null when generating a boolean statement for WHERE clauses");
        Assert.assertFalse(idsToResultInTrue.isEmpty(),"Id series cannot be empty when generating a boolean statement for WHERE clauses");

        Map<Long, Long> sequentialIds = new HashMap<>();
        Queue<Long> ids = new PriorityQueue<>(idsToResultInTrue);

        Long start = ids.remove();
        Long previousValue = start;
        while (!ids.isEmpty()) {
            Long currentValue = ids.remove();
            if (currentValue == previousValue + 1) {
                // Sequential series, continue
                previousValue = currentValue;
                continue;
            }
            // currentvalue doesnt fit the sequence starting at start
            sequentialIds.put(start, previousValue);
            start = currentValue;
            previousValue = currentValue;
        }
        sequentialIds.put(start, previousValue);

        logger.info("Mapped " + ids.size() + " values to " + sequentialIds.size() + " sequential ranges, avg "
                + ids.size() / sequentialIds.size() + " values per range");

        // Now we have a map of continuous ranges
        String expression = sequentialIds.entrySet().stream()
                .map(e -> !e.getKey().equals(e.getValue())
                        ? "(" + fieldname + " >= " + e.getKey() + " AND " + fieldname + " <= " + e.getValue() + ")"
                        : fieldname + " = " + e.getKey())
                .collect(Collectors.joining(" OR "));

        logger.debug(expression);

        return "(" + expression + ")";
    }
}
