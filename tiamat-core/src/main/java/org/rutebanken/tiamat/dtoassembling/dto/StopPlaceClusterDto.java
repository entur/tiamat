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

package org.rutebanken.tiamat.dtoassembling.dto;

/**
 * A group of stop places aggregated in the database for map display.
 * The position is the mean of the member centroids; the bounds enclose all members.
 * When the cluster holds a single stop place, singleStopPlaceId is its database id, otherwise null.
 */
public record StopPlaceClusterDto(
        long count,
        double longitude,
        double latitude,
        double lonMin,
        double latMin,
        double lonMax,
        double latMax,
        Long singleStopPlaceId) {
}
