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
            luggageServiceFacilityList = new ArrayList<>();
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
