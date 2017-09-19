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

import javax.xml.bind.JAXBElement;


public class PointInJourneyPattern_VersionedChildStructure
        extends PointInLinkSequence_VersionedChildStructure {

    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected DestinationDisplayRefStructure destinationDisplayRef;
    protected DestinationDisplayView destinationDisplayView;
    protected Vias_RelStructure vias;
    protected FlexiblePointProperties flexiblePointProperties;
    protected Boolean changeOfDestinationDisplay;
    protected Boolean changeOfServiceRequirements;
    protected NoticeAssignments noticeAssignments;

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    public DestinationDisplayView getDestinationDisplayView() {
        return destinationDisplayView;
    }

    public void setDestinationDisplayView(DestinationDisplayView value) {
        this.destinationDisplayView = value;
    }

    public Vias_RelStructure getVias() {
        return vias;
    }

    public void setVias(Vias_RelStructure value) {
        this.vias = value;
    }

    public FlexiblePointProperties getFlexiblePointProperties() {
        return flexiblePointProperties;
    }

    public void setFlexiblePointProperties(FlexiblePointProperties value) {
        this.flexiblePointProperties = value;
    }

    public Boolean isChangeOfDestinationDisplay() {
        return changeOfDestinationDisplay;
    }

    public void setChangeOfDestinationDisplay(Boolean value) {
        this.changeOfDestinationDisplay = value;
    }

    public Boolean isChangeOfServiceRequirements() {
        return changeOfServiceRequirements;
    }

    public void setChangeOfServiceRequirements(Boolean value) {
        this.changeOfServiceRequirements = value;
    }

    public NoticeAssignments getNoticeAssignments() {
        return noticeAssignments;
    }

    public void setNoticeAssignments(NoticeAssignments value) {
        this.noticeAssignments = value;
    }

}
