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

public class DeltaValueStructure {

    protected String deltaRef;
    protected ModificationEnumeration modification;
    protected String valueName;
    protected Object oldValue;
    protected Object newValue;
    protected String id;

    public String getDeltaRef() {
        return deltaRef;
    }

    public void setDeltaRef(String value) {
        this.deltaRef = value;
    }

    public ModificationEnumeration getModification() {
        return modification;
    }

    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String value) {
        this.valueName = value;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object value) {
        this.oldValue = value;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object value) {
        this.newValue = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
