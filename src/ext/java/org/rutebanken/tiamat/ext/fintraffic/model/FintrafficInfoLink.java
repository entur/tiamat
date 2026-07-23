package org.rutebanken.tiamat.ext.fintraffic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Embeddable value object for a single NeTEx {@code InfoLink} entry.
 * <p>
 * Persisted in the {@code parking_info_links} collection table owned by
 * {@link FintrafficParking}.  Only the {@code uri} (the link target) and
 * {@code typeOfInfoLink} (the first declared type from the NeTEx list) are
 * stored.  {@code targetPlatform} is intentionally not persisted in this
 * increment.
 */
@Embeddable
public class FintrafficInfoLink {

    @Column(name = "uri", nullable = false, length = 512)
    private String uri;

    /**
     * Stores the first {@code typeOfInfoLink} value from the NeTEx element as a
     * plain string (e.g. {@code "resource"}, {@code "info"}).  Nullable because
     * the attribute is optional in the NeTEx schema.
     */
    @Column(name = "type_of_info_link", length = 64)
    private String typeOfInfoLink;

    public FintrafficInfoLink() {
    }

    public FintrafficInfoLink(String uri, String typeOfInfoLink) {
        this.uri = uri;
        this.typeOfInfoLink = typeOfInfoLink;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTypeOfInfoLink() {
        return typeOfInfoLink;
    }

    public void setTypeOfInfoLink(String typeOfInfoLink) {
        this.typeOfInfoLink = typeOfInfoLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FintrafficInfoLink that)) return false;
        return Objects.equals(uri, that.uri) &&
               Objects.equals(typeOfInfoLink, that.typeOfInfoLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, typeOfInfoLink);
    }

    @Override
    public String toString() {
        return "FintrafficInfoLink{uri='" + uri + "', typeOfInfoLink='" + typeOfInfoLink + "'}";
    }
}
