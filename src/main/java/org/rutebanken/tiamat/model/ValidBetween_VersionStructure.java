

package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.time.ZonedDateTime;


@MappedSuperclass
public class ValidBetween_VersionStructure
    extends ValidityCondition_VersionStructure
{

    private ZonedDateTime fromDate;

    private ZonedDateTime toDate;


    public ZonedDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(ZonedDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public ZonedDateTime getToDate() {
        return toDate;
    }

    public void setToDate(ZonedDateTime toDate) {
        this.toDate = toDate;
    }
}
