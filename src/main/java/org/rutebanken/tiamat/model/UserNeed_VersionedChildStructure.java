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

import java.math.BigInteger;


public class UserNeed_VersionedChildStructure
        extends VersionedChildStructure {

    protected MobilityEnumeration mobilityNeed;
    protected PyschosensoryNeedEnumeration psychosensoryNeed;
    protected MedicalNeedEnumeration medicalNeed;
    protected EncumbranceEnumeration encumbranceNeed;
    protected Boolean excluded;
    protected BigInteger needRanking;

    public MobilityEnumeration getMobilityNeed() {
        return mobilityNeed;
    }

    public void setMobilityNeed(MobilityEnumeration value) {
        this.mobilityNeed = value;
    }

    public PyschosensoryNeedEnumeration getPsychosensoryNeed() {
        return psychosensoryNeed;
    }

    public void setPsychosensoryNeed(PyschosensoryNeedEnumeration value) {
        this.psychosensoryNeed = value;
    }

    public MedicalNeedEnumeration getMedicalNeed() {
        return medicalNeed;
    }

    public void setMedicalNeed(MedicalNeedEnumeration value) {
        this.medicalNeed = value;
    }

    public EncumbranceEnumeration getEncumbranceNeed() {
        return encumbranceNeed;
    }

    public void setEncumbranceNeed(EncumbranceEnumeration value) {
        this.encumbranceNeed = value;
    }

    public Boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean value) {
        this.excluded = value;
    }

    public BigInteger getNeedRanking() {
        return needRanking;
    }

    public void setNeedRanking(BigInteger value) {
        this.needRanking = value;
    }

}
