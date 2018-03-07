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

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;


@Embeddable
public class CountryRef implements Serializable {

    protected String value;

    @Enumerated(EnumType.STRING)
    protected IanaCountryTldEnumeration ref;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IanaCountryTldEnumeration getRef() {
        return ref;
    }

    public void setRef(IanaCountryTldEnumeration value) {
        this.ref = value;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("value", value)
                .add("ref", ref)
                .toString();
    }
}
