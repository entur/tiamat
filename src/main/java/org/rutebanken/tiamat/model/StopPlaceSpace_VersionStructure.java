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

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class StopPlaceSpace_VersionStructure
        extends StopPlaceComponent_VersionStructure {

    @Embedded
    protected EmbeddableMultilingualString label;

    @Transient
    protected SiteEntrances_RelStructure entrances;

    public StopPlaceSpace_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public StopPlaceSpace_VersionStructure() {
    }

    public EmbeddableMultilingualString getLabel() {
        return label;
    }

    public void setLabel(EmbeddableMultilingualString value) {
        this.label = value;
    }

    public SiteEntrances_RelStructure getEntrances() {
        return entrances;
    }

    public void setEntrances(SiteEntrances_RelStructure value) {
        this.entrances = value;
    }

}
