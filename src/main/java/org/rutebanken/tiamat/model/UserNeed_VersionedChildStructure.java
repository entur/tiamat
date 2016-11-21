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
