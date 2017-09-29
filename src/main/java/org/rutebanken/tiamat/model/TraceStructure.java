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

import javax.xml.datatype.XMLGregorianCalendar;


public class TraceStructure {

    protected VersionOfObjectRefStructure objectRef;
    protected XMLGregorianCalendar changedAt;
    protected String changedBy;
    protected String description;
    protected DeltaStructure delta;
    protected String id;
    protected XMLGregorianCalendar created;

    public VersionOfObjectRefStructure getObjectRef() {
        return objectRef;
    }

    public void setObjectRef(VersionOfObjectRefStructure value) {
        this.objectRef = value;
    }

    public XMLGregorianCalendar getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(XMLGregorianCalendar value) {
        this.changedAt = value;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String value) {
        this.changedBy = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public DeltaStructure getDelta() {
        return delta;
    }

    public void setDelta(DeltaStructure value) {
        this.delta = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public XMLGregorianCalendar getCreated() {
        return created;
    }

    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

}
