

package org.rutebanken.tiamat.model;

import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MetaValue;
import org.hibernate.mapping.Property;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


@Entity
public class ExplicitEquipments_RelStructure
    extends ContainmentAggregationStructure
{

    @ManyToAny(metaColumn = @Column(name = "item_type"))
    @AnyMetaDef(
            metaValues = {
            }
    )
    @JoinTable(
            joinColumns = @JoinColumn( name = "id" ),
            inverseJoinColumns = @JoinColumn( name = "equipment_id" )
    )
    protected List<Property> installedEquipment;

    public List<Property> getInstalledEquipment() {
        if (installedEquipment == null) {
            installedEquipment = new ArrayList<>();
        }
        return this.installedEquipment;
    }

}
