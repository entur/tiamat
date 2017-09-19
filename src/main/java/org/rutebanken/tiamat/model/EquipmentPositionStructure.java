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
import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;


@MappedSuperclass
public class EquipmentPositionStructure extends DataManagedObjectStructure {
    @Transient
    protected JAXBElement<? extends EquipmentRefStructure> equipmentRef;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity description;

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "reference_point_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "reference_point_version"))
    })
    @Embedded
    protected PointRefStructure referencePointRef;

    protected BigDecimal xOffset;

    protected BigDecimal yOffset;

    public JAXBElement<? extends EquipmentRefStructure> getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(JAXBElement<? extends EquipmentRefStructure> value) {
        this.equipmentRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public PointRefStructure getReferencePointRef() {
        return referencePointRef;
    }

    public void setReferencePointRef(PointRefStructure value) {
        this.referencePointRef = value;
    }

    public BigDecimal getXOffset() {
        return xOffset;
    }

    public void setXOffset(BigDecimal value) {
        this.xOffset = value;
    }

    public BigDecimal getYOffset() {
        return yOffset;
    }

    public void setYOffset(BigDecimal value) {
        this.yOffset = value;
    }

}
