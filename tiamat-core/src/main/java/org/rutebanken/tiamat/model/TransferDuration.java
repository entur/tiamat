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

import jakarta.persistence.Embeddable;

import java.time.Duration;


@Embeddable
public class TransferDuration {

    protected Duration defaultDuration;

    protected Duration frequentTravellerDuration;

    protected Duration occasionalTravellerDuration;

    protected Duration mobilityRestrictedTravellerDuration;

    public Duration getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(Duration value) {
        this.defaultDuration = value;
    }

    public Duration getFrequentTravellerDuration() {
        return frequentTravellerDuration;
    }

    public void setFrequentTravellerDuration(Duration value) {
        this.frequentTravellerDuration = value;
    }

    public Duration getOccasionalTravellerDuration() {
        return occasionalTravellerDuration;
    }

    public void setOccasionalTravellerDuration(Duration value) {
        this.occasionalTravellerDuration = value;
    }

    public Duration getMobilityRestrictedTravellerDuration() {
        return mobilityRestrictedTravellerDuration;
    }

    public void setMobilityRestrictedTravellerDuration(Duration value) {
        this.mobilityRestrictedTravellerDuration = value;
    }

}
