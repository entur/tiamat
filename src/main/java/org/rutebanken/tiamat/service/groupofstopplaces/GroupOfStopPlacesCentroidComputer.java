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

package org.rutebanken.tiamat.service.groupofstopplaces;

import org.locationtech.jts.geom.Point;
import org.rutebanken.netex.model.GroupCheckInEnumeration;
import org.rutebanken.tiamat.geo.CentroidComputer;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class GroupOfStopPlacesCentroidComputer {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesCentroidComputer.class);

    private final CentroidComputer centroidComputer;

    private final GroupOfStopPlacesMembersResolver groupOfStopPlacesMembersResolver;

    @Autowired
    public GroupOfStopPlacesCentroidComputer(CentroidComputer centroidComputer, GroupOfStopPlacesMembersResolver groupOfStopPlacesMembersResolver) {
        this.centroidComputer = centroidComputer;
        this.groupOfStopPlacesMembersResolver = groupOfStopPlacesMembersResolver;
    }

    public Optional<Point> compute(GroupOfStopPlaces groupOfStopPlaces) {
        logger.info("Computing centroid for group of stop places: {}", groupOfStopPlaces.getNetexId());
        return centroidComputer.compute(new HashSet<>(groupOfStopPlacesMembersResolver.resolve(groupOfStopPlaces)));
    }

}
