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

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;


@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AccessibilityLimitation
        extends AccessibilityLimitation_VersionedChildStructure {


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("netexId", netexId)
                .add("version", version)
                .add("wheelchairAccess", wheelchairAccess)
                .add("stepFreeAccess", stepFreeAccess)
                .add("escalatorFreeAccess", escalatorFreeAccess)
                .add("liftFreeAccess", liftFreeAccess)
                .add("audibleSignalsAvailable", audibleSignalsAvailable)
                .add("visualSignsAvailable", visualSignsAvailable)
                .toString();
    }

}
