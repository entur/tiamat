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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.locationtech.jts.geom.LineString;
import org.rutebanken.tiamat.model.PathLink;
import org.springframework.stereotype.Component;

/**
 * DataFetcher for the geometry field on PathLink.
 * Returns the lineString as a JTS Geometry object for GeoJSON serialization.
 */
@Component
public class PathLinkGeometryFetcher implements DataFetcher<LineString> {

    @Override
    public LineString get(DataFetchingEnvironment environment) {
        if (environment.getSource() instanceof PathLink pathLink) {
            return pathLink.getLineString();
        }
        return null;
    }
}

