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
public abstract class Equipment_VersionStructure
        extends EntityInVersionStructure {

    @Transient
    protected MultilingualStringEntity name;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "private_code_value")),
            @AttributeOverride(name = "type", column = @Column(name = "private_code_type"))
    })
    @Embedded
    protected PrivateCodeStructure privateCode;
    @Transient
    protected PrivateCodeStructure publicCode;
    @Transient
    protected String image;
    @Transient
    protected TypeOfEquipmentRefStructure typeOfEquipmentRef;
    @Transient
    protected MultilingualStringEntity description;
    @Transient
    protected MultilingualStringEntity note;
    protected Boolean outOfService;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public PrivateCodeStructure getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(PrivateCodeStructure value) {
        this.publicCode = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String value) {
        this.image = value;
    }

    public TypeOfEquipmentRefStructure getTypeOfEquipmentRef() {
        return typeOfEquipmentRef;
    }

    public void setTypeOfEquipmentRef(TypeOfEquipmentRefStructure value) {
        this.typeOfEquipmentRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public MultilingualStringEntity getNote() {
        return note;
    }

    public void setNote(MultilingualStringEntity value) {
        this.note = value;
    }

    public Boolean isOutOfService() {
        return outOfService;
    }

    public void setOutOfService(Boolean value) {
        this.outOfService = value;
    }

}
