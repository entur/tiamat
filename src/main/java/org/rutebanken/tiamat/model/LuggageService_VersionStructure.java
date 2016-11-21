package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class LuggageService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<LuggageServiceFacilityEnumeration> luggageServiceFacilityList;
    protected Boolean luggageTrolleys;
    protected Boolean wheelchairLuggageTrolleys;
    protected Boolean freeToUse;
    protected BigDecimal maximumBagWidth;
    protected BigDecimal maximumBagHeight;
    protected BigDecimal maximumBagDepth;

    public List<LuggageServiceFacilityEnumeration> getLuggageServiceFacilityList() {
        if (luggageServiceFacilityList == null) {
            luggageServiceFacilityList = new ArrayList<LuggageServiceFacilityEnumeration>();
        }
        return this.luggageServiceFacilityList;
    }

    public Boolean isLuggageTrolleys() {
        return luggageTrolleys;
    }

    public void setLuggageTrolleys(Boolean value) {
        this.luggageTrolleys = value;
    }

    public Boolean isWheelchairLuggageTrolleys() {
        return wheelchairLuggageTrolleys;
    }

    public void setWheelchairLuggageTrolleys(Boolean value) {
        this.wheelchairLuggageTrolleys = value;
    }

    public Boolean isFreeToUse() {
        return freeToUse;
    }

    public void setFreeToUse(Boolean value) {
        this.freeToUse = value;
    }

    public BigDecimal getMaximumBagWidth() {
        return maximumBagWidth;
    }

    public void setMaximumBagWidth(BigDecimal value) {
        this.maximumBagWidth = value;
    }

    public BigDecimal getMaximumBagHeight() {
        return maximumBagHeight;
    }

    public void setMaximumBagHeight(BigDecimal value) {
        this.maximumBagHeight = value;
    }

    public BigDecimal getMaximumBagDepth() {
        return maximumBagDepth;
    }

    public void setMaximumBagDepth(BigDecimal value) {
        this.maximumBagDepth = value;
    }

}
