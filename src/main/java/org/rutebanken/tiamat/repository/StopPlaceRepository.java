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

package org.rutebanken.tiamat.repository;

import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface StopPlaceRepository extends StopPlaceRepositoryCustom, EntityInVersionRepository<StopPlace> {

    Page<StopPlace> findAllByOrderByChangedDesc(Pageable pageable);

    StopPlace findByNameValueAndCentroid(String name, Point geometryPoint);

    @Query(value = "select s.* from stop_place s where s.parent_site_ref = :#{#ref} and s.parent_site_ref_version = :#{#version}", nativeQuery = true)
    List<StopPlace> findByParentRef(@Param("ref") String ref, @Param("version") String version);

    Page<StopPlace> findByNameValueContainingIgnoreCaseOrderByChangedDesc(String name, Pageable pageable);

    @Override
    Iterator<StopPlace> scrollStopPlaces();

    @Override
    Iterator<StopPlace> scrollStopPlaces(ExportParams exportParams);

    @Override
    Iterator<StopPlace> scrollStopPlaces(Set<Long> stopPlacePrimaryIds);

    @Override
    Iterator<StopPlace> scrollSchedulesStopPlaces(Set<Long> stopPlacePrimaryIds);
}

