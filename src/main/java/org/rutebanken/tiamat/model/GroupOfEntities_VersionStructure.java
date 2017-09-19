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
public abstract class GroupOfEntities_VersionStructure
        extends DataManagedObjectStructure {

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "name_lang", length = 5))
    })
    @Embedded
    protected EmbeddableMultilingualString name;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "short_name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "short_name_lang", length = 5))
    })
    @Embedded
    protected EmbeddableMultilingualString shortName;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description_value", length = 4000)),
            @AttributeOverride(name = "lang", column = @Column(name = "description_lang", length = 5))
    })
    @Embedded
    protected EmbeddableMultilingualString description;

    @Transient
    protected PurposeOfGroupingRefStructure purposeOfGroupingRef;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "private_code_value")),
            @AttributeOverride(name = "type", column = @Column(name = "private_code_type"))
    })
    @Embedded
    protected PrivateCodeStructure privateCode;


    public GroupOfEntities_VersionStructure() {
    }

    public GroupOfEntities_VersionStructure(EmbeddableMultilingualString name) {
        this.name = name;
    }

    public EmbeddableMultilingualString getName() {
        return name;
    }

    public void setName(EmbeddableMultilingualString value) {
        this.name = value;
    }

    public EmbeddableMultilingualString getShortName() {
        return shortName;
    }

    public void setShortName(EmbeddableMultilingualString value) {
        this.shortName = value;
    }

    public EmbeddableMultilingualString getDescription() {
        return description;
    }

    public void setDescription(EmbeddableMultilingualString value) {
        this.description = value;
    }

    public PurposeOfGroupingRefStructure getPurposeOfGroupingRef() {
        return purposeOfGroupingRef;
    }

    public void setPurposeOfGroupingRef(PurposeOfGroupingRefStructure value) {
        this.purposeOfGroupingRef = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public String importedIdAndNameToString() {
        return getOriginalIds() + " - " + (getName() != null ? getName().getValue() : "");
    }
}
