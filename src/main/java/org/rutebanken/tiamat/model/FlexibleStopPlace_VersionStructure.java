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

import java.util.ArrayList;
import java.util.List;

public class FlexibleStopPlace_VersionStructure
        extends Place {

    protected MultilingualStringEntity nameSuffix;

    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    protected VehicleModeEnumeration transportMode;
    protected String publicCode;
    protected Areas areas;

    public MultilingualStringEntity getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(MultilingualStringEntity value) {
        this.nameSuffix = value;
    }

    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public Areas getAreas() {
        return areas;
    }

    public void setAreas(Areas value) {
        this.areas = value;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
