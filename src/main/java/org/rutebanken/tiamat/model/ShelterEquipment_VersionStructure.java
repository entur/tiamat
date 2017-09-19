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

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;

@MappedSuperclass
public class ShelterEquipment_VersionStructure
        extends WaitingEquipment_VersionStructure {

    protected Boolean enclosed;
    @Transient
    protected BigDecimal distanceFromNearestKerb;

    public Boolean isEnclosed() {
        return enclosed;
    }

    public void setEnclosed(Boolean value) {
        this.enclosed = value;
    }

    public BigDecimal getDistanceFromNearestKerb() {
        return distanceFromNearestKerb;
    }

    public void setDistanceFromNearestKerb(BigDecimal value) {
        this.distanceFromNearestKerb = value;
    }

}
