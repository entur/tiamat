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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;

import java.util.HashSet;
import java.util.Set;


@Entity
public class FareZone extends Zone_VersionStructure {

    @Enumerated(EnumType.STRING)
    protected ScopingMethodEnumeration scopingMethod;

    @Enumerated(EnumType.STRING)
    private ZoneTopologyEnumeration zoneTopology;

    @ElementCollection(targetClass = TariffZoneRef.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "fare_zone_neighbours"
    )
    private Set<TariffZoneRef> neighbours = new HashSet<>();

    @ElementCollection(targetClass = StopPlaceReference.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "fare_zone_members"
    )
    private Set<StopPlaceReference> fareZoneMembers = new HashSet<>();


    private String transportOrganisationRef;

    public ScopingMethodEnumeration getScopingMethod() {
        return scopingMethod;
    }

    public void setScopingMethod(ScopingMethodEnumeration scopingMethod) {
        this.scopingMethod = scopingMethod;
    }

    public ZoneTopologyEnumeration getZoneTopology() {
        return zoneTopology;
    }

    public void setZoneTopology(ZoneTopologyEnumeration zoneTopology) {
        this.zoneTopology = zoneTopology;
    }

    public Set<TariffZoneRef> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<TariffZoneRef> neighbours) {
        this.neighbours = neighbours;
    }

    public String getTransportOrganisationRef() {
        return transportOrganisationRef;
    }

    public void setTransportOrganisationRef(String transportOrganisationRef) {
        this.transportOrganisationRef = transportOrganisationRef;
    }

    public Set<StopPlaceReference> getFareZoneMembers() {
        return fareZoneMembers;
    }

    public void setFareZoneMembers(Set<StopPlaceReference> fareZoneMembers) {
        this.fareZoneMembers = fareZoneMembers;
    }

}
