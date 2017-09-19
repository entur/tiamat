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

public class PointOfInterestClassificationHierarchyMemberStructure
        extends VersionedChildStructure {

    protected PointOfInterestHierarchyRefStructure pointOfInterestHierarchyRef;
    protected PointOfInterestClassificationRefStructure parentClassificationRef;
    protected PointOfInterestClassificationRefStructure pointOfInterestClassificationRef;

    public PointOfInterestHierarchyRefStructure getPointOfInterestHierarchyRef() {
        return pointOfInterestHierarchyRef;
    }

    public void setPointOfInterestHierarchyRef(PointOfInterestHierarchyRefStructure value) {
        this.pointOfInterestHierarchyRef = value;
    }

    public PointOfInterestClassificationRefStructure getParentClassificationRef() {
        return parentClassificationRef;
    }

    public void setParentClassificationRef(PointOfInterestClassificationRefStructure value) {
        this.parentClassificationRef = value;
    }

    public PointOfInterestClassificationRefStructure getPointOfInterestClassificationRef() {
        return pointOfInterestClassificationRef;
    }

    public void setPointOfInterestClassificationRef(PointOfInterestClassificationRefStructure value) {
        this.pointOfInterestClassificationRef = value;
    }

}
