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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class PostalAddress extends EntityInVersionStructure {

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "address_line1_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "address_line1_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString addressLine1;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "town_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "town_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString town;
    protected String postCode;

    public EmbeddableMultilingualString getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(EmbeddableMultilingualString addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public EmbeddableMultilingualString getTown() {
        return town;
    }

    public void setTown(EmbeddableMultilingualString town) {
        this.town = town;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof PostalAddress)) {
            return false;
        }

        PostalAddress other = (PostalAddress) object;

        return Objects.equals(this.addressLine1, other.addressLine1)
                && Objects.equals(this.town, other.town)
                && Objects.equals(this.postCode, other.postCode)
                && Objects.equals(this.created, other.created);
    }
}
