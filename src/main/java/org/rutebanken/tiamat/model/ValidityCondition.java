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

package org.rutebanken.tiamat.model;

import javax.persistence.*;


@MappedSuperclass
public abstract class ValidityCondition extends DataManagedObjectStructure {

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "name_lang"))
    })
    @Embedded
    private EmbeddableMultilingualString name;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "description_lang"))
    })
    @Embedded
    private EmbeddableMultilingualString description;

    @Transient
    private VersionOfObjectRefStructure conditionedObjectRef;

    @Transient
    private ValidityConditionRefStructure withConditionRef;

    public EmbeddableMultilingualString getDescription() {
        return description;
    }

    public void setDescription(EmbeddableMultilingualString description) {
        this.description = description;
    }

    public VersionOfObjectRefStructure getConditionedObjectRef() {
        return conditionedObjectRef;
    }

    public void setConditionedObjectRef(VersionOfObjectRefStructure conditionedObjectRef) {
        this.conditionedObjectRef = conditionedObjectRef;
    }

    public ValidityConditionRefStructure getWithConditionRef() {
        return withConditionRef;
    }

    public void setWithConditionRef(ValidityConditionRefStructure withConditionRef) {
        this.withConditionRef = withConditionRef;
    }


    public EmbeddableMultilingualString getName() {
        return name;
    }

    public void setName(EmbeddableMultilingualString name) {
        this.name = name;
    }
}
