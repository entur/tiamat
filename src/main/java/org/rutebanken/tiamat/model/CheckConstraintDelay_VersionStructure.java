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


public class CheckConstraintDelay_VersionStructure
        extends Assignment_VersionStructure {

    protected CheckConstraintRefStructure checkConstraintRef;
    protected ClassOfUseRef classOfUseRef;
    protected Duration minimumLikelyDelay;
    protected Duration averageDelay;
    protected Duration maximumLikelyDelay;

    public CheckConstraintRefStructure getCheckConstraintRef() {
        return checkConstraintRef;
    }

    public void setCheckConstraintRef(CheckConstraintRefStructure value) {
        this.checkConstraintRef = value;
    }

    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    public void setClassOfUseRef(ClassOfUseRef value) {
    }

    public Duration getMinimumLikelyDelay() {
        return minimumLikelyDelay;
    }

    public void setMinimumLikelyDelay(Duration value) {
        this.minimumLikelyDelay = value;
    }

    public Duration getAverageDelay() {
        return averageDelay;
    }

    public void setAverageDelay(Duration value) {
        this.averageDelay = value;
    }

    public Duration getMaximumLikelyDelay() {
        return maximumLikelyDelay;
    }

    public void setMaximumLikelyDelay(Duration value) {
        this.maximumLikelyDelay = value;
    }

}
