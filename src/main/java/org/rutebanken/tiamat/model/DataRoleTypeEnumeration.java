

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum DataRoleTypeEnumeration {

    ALL("all"),
    COLLECTS("collects"),
    VALIDATES("validates"),
    AGGREGATES("aggregates"),
    DISTRIBUTES("distributes"),
    REDISTRIBUTES("redistributes"),
    CREATES("creates");
    private final String value;

    DataRoleTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataRoleTypeEnumeration fromValue(String v) {
        for (DataRoleTypeEnumeration c: DataRoleTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
