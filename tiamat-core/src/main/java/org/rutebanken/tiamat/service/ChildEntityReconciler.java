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

package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

/**
 * Matches each GraphQL child input to a stored child of the same parent by {@code id}.
 * <p>
 * The reconciler populates a matched child in place, so its {@code netexId} stays the same.
 * An input without an {@code id} creates a new child. The reconciler rejects an {@code id}
 * that matches no stored child of this parent, and an {@code id} that the same list uses twice.
 */
@Component
public class ChildEntityReconciler {

    /**
     * Reconciles a whole list of child inputs against the stored children of one parent.
     *
     * @param existing the stored children of the parent, searched by {@code netexId}
     * @param inputs the GraphQL maps for the children, in the order to keep
     * @param parentId the parent's id, named in the error message
     * @param factory creates a new child when an input carries no id
     * @param populate copies the input fields onto the new or matched child
     * @throws IllegalArgumentException if an id matches no stored child of this parent, or occurs twice in the list
     */
    public <T extends IdentifiedEntity> List<T> reconcileAll(
            Collection<T> existing,
            Collection<Map<String, Object>> inputs,
            String parentId,
            Supplier<T> factory,
            BiConsumer<Map<String, Object>, T> populate) {

        Set<Object> seenIds = new HashSet<>();
        List<T> result = new ArrayList<>();
        for (Map<String, Object> input : inputs) {
            Object id = input.get(ID);
            if (id != null && !seenIds.add(id)) {
                throw new IllegalArgumentException(
                        "Child [id = " + id + "] of parent [id = " + parentId + "] occurs more than once in the input");
            }
            result.add(reconcile(existing, input, parentId, factory, populate));
        }
        return result;
    }

    /**
     * Reconciles one child input against the stored children of one parent.
     *
     * @param existing the stored children of the parent, searched by {@code netexId}
     * @param input the GraphQL map for one child
     * @param parentId the parent's id, named in the error message
     * @param factory creates a new child when the input carries no id
     * @param populate copies the input fields onto the new or matched child
     * @throws IllegalArgumentException if the input carries an id that no stored child of this parent has
     */
    public <T extends IdentifiedEntity> T reconcile(
            Collection<T> existing,
            Map<String, Object> input,
            String parentId,
            Supplier<T> factory,
            BiConsumer<Map<String, Object>, T> populate) {

        Object incomingId = input.get(ID);
        T target;
        if (incomingId != null) {
            target = existing.stream()
                    .filter(candidate -> incomingId.equals(candidate.getNetexId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Attempting to update child [id = " + incomingId + "] of parent [id = " + parentId
                                    + "], but the parent has no such child"));
        } else {
            target = factory.get();
        }
        populate.accept(input, target);
        return target;
    }
}
