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

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class GroupOfPoints_VersionStructure
        extends GroupOfEntities_VersionStructure {

    @Transient
    protected PointRefs_RelStructure members;

    public GroupOfPoints_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public GroupOfPoints_VersionStructure() {
    }

    public PointRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(PointRefs_RelStructure value) {
        this.members = value;
    }

}
