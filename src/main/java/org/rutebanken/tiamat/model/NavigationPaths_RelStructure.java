

package org.rutebanken.tiamat.model;

import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MetaValue;
import org.hibernate.mapping.Property;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


@Entity
public class NavigationPaths_RelStructure
    extends ContainmentAggregationStructure
{

    @ManyToAny(metaColumn = @Column(name = "item_type"))
    @AnyMetaDef(
            metaValues = {
            }
    )
    @JoinTable(
            joinColumns = @JoinColumn( name = "id" ),
            inverseJoinColumns = @JoinColumn( name = "path_id" )
    )
    protected List<Property> navigationPathRefOrNavigationPath;

    public List<Property> getNavigationPathRefOrNavigationPath() {
        if (navigationPathRefOrNavigationPath == null) {
            navigationPathRefOrNavigationPath = new ArrayList<Property>();
        }
        return this.navigationPathRefOrNavigationPath;
    }

}
