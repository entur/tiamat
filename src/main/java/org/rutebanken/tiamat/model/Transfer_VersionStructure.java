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


public abstract class Transfer_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;

    protected MultilingualStringEntity description;
    protected BigDecimal distance;
    protected TransferDuration transferDuration;
    protected TransferDuration walkTransferDuration;
    protected Boolean bothWays;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public TransferDuration getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(TransferDuration value) {
        this.transferDuration = value;
    }

    public TransferDuration getWalkTransferDuration() {
        return walkTransferDuration;
    }

    public void setWalkTransferDuration(TransferDuration value) {
        this.walkTransferDuration = value;
    }

    public Boolean isBothWays() {
        return bothWays;
    }

    public void setBothWays(Boolean value) {
        this.bothWays = value;
    }

}
