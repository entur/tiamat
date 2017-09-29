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


public class DeltaStructure {

    protected SimpleObjectRefStructure simpleObjectRef;
    protected FromVersionRef fromVersionRef;
    protected ToVersionRef toVersionRef;
    protected ModificationEnumeration modification;
    protected DeltaValues_RelStructure deltaValues;
    protected String id;
    protected XMLGregorianCalendar created;

    public SimpleObjectRefStructure getSimpleObjectRef() {
        return simpleObjectRef;
    }

    public void setSimpleObjectRef(SimpleObjectRefStructure value) {
        this.simpleObjectRef = value;
    }

    public FromVersionRef getFromVersionRef() {
        return fromVersionRef;
    }

    public void setFromVersionRef(FromVersionRef value) {
        this.fromVersionRef = value;
    }

    public ToVersionRef getToVersionRef() {
        return toVersionRef;
    }

    public void setToVersionRef(ToVersionRef value) {
        this.toVersionRef = value;
    }

    public ModificationEnumeration getModification() {
        return modification;
    }

    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }

    public DeltaValues_RelStructure getDeltaValues() {
        return deltaValues;
    }

    public void setDeltaValues(DeltaValues_RelStructure value) {
        this.deltaValues = value;
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
