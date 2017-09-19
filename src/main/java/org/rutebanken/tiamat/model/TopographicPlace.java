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

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        indexes = {
                @Index(name = "topographic_place_name_value_index", columnList = "name_value")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "topographic_place_netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class TopographicPlace extends Place {

    protected String isoCode;

    @Enumerated(EnumType.STRING)
    protected TopographicPlaceTypeEnumeration topographicPlaceType;

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "country_ref")),
            @AttributeOverride(name = "value", column = @Column(name = "country_ref_value"))
    })
    @Embedded
    protected CountryRef countryRef;

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "parent_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "parent_ref_version"))
    })
    @Embedded
    protected TopographicPlaceRefStructure parentTopographicPlaceRef;

    public TopographicPlace(EmbeddableMultilingualString name) {
        super(name);
    }

    public TopographicPlace() {
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String value) {
        this.isoCode = value;
    }

    public TopographicPlaceTypeEnumeration getTopographicPlaceType() {
        return topographicPlaceType;
    }

    public void setTopographicPlaceType(TopographicPlaceTypeEnumeration value) {
        this.topographicPlaceType = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public TopographicPlaceRefStructure getParentTopographicPlaceRef() {
        return parentTopographicPlaceRef;
    }

    public void setParentTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.parentTopographicPlaceRef = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("netexId", netexId)
                .add("name", name != null ? name.getValue() : null)
                .add("isoCode", isoCode)
                .add("topographicPlaceType", topographicPlaceType)
                .add("countryRef", countryRef)
                .toString();
    }
}
