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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class GroupOfStopPlaces extends GroupOfEntities_VersionStructure {

    @ElementCollection(targetClass = StopPlaceReference.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "group_of_stop_place_members"
    )
    private Set<StopPlaceReference> members = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    public GroupOfStopPlaces(EmbeddableMultilingualString embeddableMultilingualString) {
        super(embeddableMultilingualString);
    }

    public GroupOfStopPlaces() {
    }

    public Set<StopPlaceReference> getMembers() {
        return members;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
