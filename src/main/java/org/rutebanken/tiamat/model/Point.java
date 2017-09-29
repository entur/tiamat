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

public class Point
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected String pointNumber;
    protected TypeOfPointRefs_RelStructure types;
    protected Projections_RelStructure projections;
    protected GroupMembershipRefs_RelStructure groupMemberships;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public String getPointNumber() {
        return pointNumber;
    }

    public void setPointNumber(String value) {
        this.pointNumber = value;
    }

    public TypeOfPointRefs_RelStructure getTypes() {
        return types;
    }

    public void setTypes(TypeOfPointRefs_RelStructure value) {
        this.types = value;
    }

    public Projections_RelStructure getProjections() {
        return projections;
    }

    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    public GroupMembershipRefs_RelStructure getGroupMemberships() {
        return groupMemberships;
    }

    public void setGroupMemberships(GroupMembershipRefs_RelStructure value) {
        this.groupMemberships = value;
    }

}
