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

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;


public class Timeband_VersionedChildStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected XMLGregorianCalendar startTime;
    protected XMLGregorianCalendar endTime;
    protected BigInteger dayOffset;
    protected Duration duration;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    public void setEndTime(XMLGregorianCalendar value) {
        this.endTime = value;
    }

    public BigInteger getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(BigInteger value) {
        this.dayOffset = value;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

}
