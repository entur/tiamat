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


public class HeadingSignStructure
        extends SignEquipment_VersionStructure {

    protected MultilingualStringEntity placeName;
    protected MultilingualStringEntity lineName;
    protected VehicleModeEnumeration transportMode;
    protected TransportSubmodeStructure transportSubmode;
    protected String lineMap;
    protected DirectionRefStructure directionRef;
    protected MultilingualStringEntity directionName;
    protected DestinationDisplayRefStructure destinationDisplayRef;
    protected String linePublicCode;

    public MultilingualStringEntity getPlaceName() {
        return placeName;
    }

    public void setPlaceName(MultilingualStringEntity value) {
        this.placeName = value;
    }

    public MultilingualStringEntity getLineName() {
        return lineName;
    }

    public void setLineName(MultilingualStringEntity value) {
        this.lineName = value;
    }

    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    public TransportSubmodeStructure getTransportSubmode() {
        return transportSubmode;
    }

    public void setTransportSubmode(TransportSubmodeStructure value) {
        this.transportSubmode = value;
    }

    public String getLineMap() {
        return lineMap;
    }

    public void setLineMap(String value) {
        this.lineMap = value;
    }

    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

    public MultilingualStringEntity getDirectionName() {
        return directionName;
    }

    public void setDirectionName(MultilingualStringEntity value) {
        this.directionName = value;
    }

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    public String getLinePublicCode() {
        return linePublicCode;
    }

    public void setLinePublicCode(String value) {
        this.linePublicCode = value;
    }

}
