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


public class TypeOfValidity_ValueStructure
        extends TypeOfValue_VersionStructure {

    protected Duration periodicity;
    protected FrameNatureEnumeration nature;
    protected ClassRefs_RelStructure classes;

    public Duration getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Duration value) {
        this.periodicity = value;
    }

    public FrameNatureEnumeration getNature() {
        return nature;
    }

    public void setNature(FrameNatureEnumeration value) {
        this.nature = value;
    }

    public ClassRefs_RelStructure getClasses() {
        return classes;
    }

    public void setClasses(ClassRefs_RelStructure value) {
    }

}
