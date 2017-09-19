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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;


@MappedSuperclass
public class BoardingPosition_VersionStructure
        extends StopPlaceSpace_VersionStructure {

    protected String publicCode;

    @Enumerated(EnumType.STRING)
    protected BoardingPositionTypeEnumeration boardingPositionType;

    @Transient
    protected EntranceRefs_RelStructure boardingPositionEntrances;

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public BoardingPositionTypeEnumeration getBoardingPositionType() {
        return boardingPositionType;
    }

    public void setBoardingPositionType(BoardingPositionTypeEnumeration value) {
        this.boardingPositionType = value;
    }

    public EntranceRefs_RelStructure getBoardingPositionEntrances() {
        return boardingPositionEntrances;
    }

    public void setBoardingPositionEntrances(EntranceRefs_RelStructure value) {
        this.boardingPositionEntrances = value;
    }

}
