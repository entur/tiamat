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

public class GroupOfPlaces_VersionStructure
        extends GroupOfEntities_VersionStructure {

    protected PlaceRefs_RelStructure members;
    protected CountryRef countryRef;
    protected PlaceRefStructure mainPlaceRef;

    public PlaceRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(PlaceRefs_RelStructure value) {
        this.members = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public PlaceRefStructure getMainPlaceRef() {
        return mainPlaceRef;
    }

    public void setMainPlaceRef(PlaceRefStructure value) {
        this.mainPlaceRef = value;
    }

}
