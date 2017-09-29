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

public class TopographicPlace_DerivedViewStructure extends DerivedViewStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity qualifierName;
    protected CountryRef countryRef;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(MultilingualStringEntity value) {
        this.qualifierName = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

}
