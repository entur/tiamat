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

package org.rutebanken.tiamat.versioning.util;


import org.rutebanken.tiamat.model.EntityInVersionStructure;

/**
 * Wrapper object for holding entity and copied version.
 * It could also hold a parent entity with copy, if such exist.
 * @param <T>
 */
public class CopiedEntity<T extends EntityInVersionStructure> {

    private T existingEntity;
    private T copiedEntity;
    private T existingParent;
    private T copiedParent;

    public CopiedEntity(T existingEntity, T copiedEntity, T existingParent, T copiedParent) {
        this.existingEntity = existingEntity;
        this.copiedEntity = copiedEntity;
        this.existingParent = existingParent;
        this.copiedParent = copiedParent;
    }

    public T getExistingEntity() {
        return existingEntity;
    }

    public T getCopiedEntity() {
        return copiedEntity;
    }

    public T getExistingParent() {
        return existingParent;
    }

    public T getCopiedParent() {
        return copiedParent;
    }

    public boolean hasParent() {
        return existingParent != null;
    }
}
