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

public class LinkProjection_VersionStructure
        extends Projection_VersionStructure {

    protected LinkRefStructure projectedLinkRef;
    protected LinkRefStructure projectToLinkRef;
    protected PointOnLinkRefStructure startPointOnLinkRef;
    protected PointOnLinkByValueStructure startPointOnLinkByValue;
    protected PointOnLinkRefStructure endPointOnLinkRef;
    protected PointOnLinkByValueStructure endPointOnLinkByValue;

    public LinkRefStructure getProjectedLinkRef() {
        return projectedLinkRef;
    }

    public void setProjectedLinkRef(LinkRefStructure value) {
        this.projectedLinkRef = value;
    }

    public LinkRefStructure getProjectToLinkRef() {
        return projectToLinkRef;
    }

    public void setProjectToLinkRef(LinkRefStructure value) {
        this.projectToLinkRef = value;
    }

    public PointOnLinkRefStructure getStartPointOnLinkRef() {
        return startPointOnLinkRef;
    }

    public void setStartPointOnLinkRef(PointOnLinkRefStructure value) {
        this.startPointOnLinkRef = value;
    }

    public PointOnLinkByValueStructure getStartPointOnLinkByValue() {
        return startPointOnLinkByValue;
    }

    public void setStartPointOnLinkByValue(PointOnLinkByValueStructure value) {
        this.startPointOnLinkByValue = value;
    }

    public PointOnLinkRefStructure getEndPointOnLinkRef() {
        return endPointOnLinkRef;
    }

    public void setEndPointOnLinkRef(PointOnLinkRefStructure value) {
        this.endPointOnLinkRef = value;
    }

    public PointOnLinkByValueStructure getEndPointOnLinkByValue() {
        return endPointOnLinkByValue;
    }

    public void setEndPointOnLinkByValue(PointOnLinkByValueStructure value) {
        this.endPointOnLinkByValue = value;
    }

}
