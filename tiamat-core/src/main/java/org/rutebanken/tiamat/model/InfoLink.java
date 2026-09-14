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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;

/**
 * NeTEx {@code InfoLink} entry (declared on the abstract {@code GroupOfEntities_VersionStructure},
 * see {@link GroupOfEntities_VersionStructure#getInfoLinks()}). Persisted only where a concrete
 * subclass shadows the field (currently {@link Parking}).
 * <p>
 * NeTEx's {@code InfoLinkStructure.typeOfInfoLink} is an XML list attribute, but only a single
 * value is stored here, matching every current producer (exactly one type is ever supplied).
 * Values beyond the first are dropped on import — the same restriction applied to
 * {@code AvailabilityCondition.dayTypeRef}.
 */
@Embeddable
public class InfoLink {

    @Column(name = "uri", nullable = false, length = 512)
    private String uri;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_info_link")
    private TypeOfInfolinkEnumeration typeOfInfoLink;

    public InfoLink() {
    }

    public InfoLink(String uri, TypeOfInfolinkEnumeration typeOfInfoLink) {
        this.uri = uri;
        this.typeOfInfoLink = typeOfInfoLink;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public TypeOfInfolinkEnumeration getTypeOfInfoLink() {
        return typeOfInfoLink;
    }

    public void setTypeOfInfoLink(TypeOfInfolinkEnumeration typeOfInfoLink) {
        this.typeOfInfoLink = typeOfInfoLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InfoLink that)) return false;
        return Objects.equals(uri, that.uri) &&
                typeOfInfoLink == that.typeOfInfoLink;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, typeOfInfoLink);
    }

    @Override
    public String toString() {
        return "InfoLink{uri='" + uri + "', typeOfInfoLink=" + typeOfInfoLink + "}";
    }
}
