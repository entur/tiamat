package org.rutebanken.tiamat.model;

import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MetaValue;
import org.hibernate.mapping.Property;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_navigation_paths_rel_structure" )
public class NavigationPaths_RelStructure        extends ContainmentAggregationStructure {

    @ManyToAny(metaColumn = @Column(name = "item_type"))
    @AnyMetaDef(
            idType = "integer", metaType = "string",
            metaValues = {
                    @MetaValue(targetEntity = NavigationPathRefStructure.class, value = "navigation_path_ref_structure"),
                    @MetaValue(targetEntity = NavigationPath.class, value = "navigation_path")
            }
    )
    @JoinTable(
            name = "navigationPath",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "path_id")
    )
    protected List<Property> navigationPathRefOrNavigationPath;

    public List<Property> getNavigationPathRefOrNavigationPath() {
        if (navigationPathRefOrNavigationPath == null) {
            navigationPathRefOrNavigationPath = new ArrayList<Property>();
        }
        return this.navigationPathRefOrNavigationPath;
    }

}
