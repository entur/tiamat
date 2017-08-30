package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;

public class Operator_DerivedViewStructure
        extends DerivedViewStructure {

    protected OperatorRefStructure operatorRef;
    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity legalName;
    protected MultilingualStringEntity tradingName;

    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    public OperatorRefStructure getOperatorRef() {
        return operatorRef;
    }

    public void setOperatorRef(OperatorRefStructure value) {
        this.operatorRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getLegalName() {
        return legalName;
    }

    public void setLegalName(MultilingualStringEntity value) {
        this.legalName = value;
    }

    public MultilingualStringEntity getTradingName() {
        return tradingName;
    }

    public void setTradingName(MultilingualStringEntity value) {
        this.tradingName = value;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
