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

import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.MetaValue;
import org.hibernate.mapping.Property;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class NavigationPaths_RelStructure extends ContainmentAggregationStructure {

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
