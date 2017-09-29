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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class AccessibilityLimitation_VersionedChildStructure
        extends VersionedChildStructure {

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration wheelchairAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration stepFreeAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration escalatorFreeAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration liftFreeAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration audibleSignalsAvailable;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration visualSignsAvailable;

    public LimitationStatusEnumeration getWheelchairAccess() {
        return wheelchairAccess;
    }

    public void setWheelchairAccess(LimitationStatusEnumeration value) {
        this.wheelchairAccess = value;
    }

    public LimitationStatusEnumeration getStepFreeAccess() {
        return stepFreeAccess;
    }

    public void setStepFreeAccess(LimitationStatusEnumeration value) {
        this.stepFreeAccess = value;
    }

    public LimitationStatusEnumeration getEscalatorFreeAccess() {
        return escalatorFreeAccess;
    }

    public void setEscalatorFreeAccess(LimitationStatusEnumeration value) {
        this.escalatorFreeAccess = value;
    }

    public LimitationStatusEnumeration getLiftFreeAccess() {
        return liftFreeAccess;
    }

    public void setLiftFreeAccess(LimitationStatusEnumeration value) {
        this.liftFreeAccess = value;
    }

    public LimitationStatusEnumeration getAudibleSignalsAvailable() {
        return audibleSignalsAvailable;
    }

    public void setAudibleSignalsAvailable(LimitationStatusEnumeration value) {
        this.audibleSignalsAvailable = value;
    }

    public LimitationStatusEnumeration getVisualSignsAvailable() {
        return visualSignsAvailable;
    }

    public void setVisualSignsAvailable(LimitationStatusEnumeration value) {
        this.visualSignsAvailable = value;
    }

}
