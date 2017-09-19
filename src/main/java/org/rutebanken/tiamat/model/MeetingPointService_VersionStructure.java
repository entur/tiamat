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

public class MeetingPointService_VersionStructure
        extends CustomerService_VersionStructure {

    protected MeetingPointEnumeration meetingPointServiceType;
    protected MultilingualStringEntity label;

    public MeetingPointEnumeration getMeetingPointServiceType() {
        return meetingPointServiceType;
    }

    public void setMeetingPointServiceType(MeetingPointEnumeration value) {
        this.meetingPointServiceType = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

}
