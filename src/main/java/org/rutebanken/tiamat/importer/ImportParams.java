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

package org.rutebanken.tiamat.importer;

import io.swagger.annotations.ApiParam;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImportParams {

    /**
     * Only import stops that matches the list of counties.
     */
    @QueryParam(value = "targetTopographicPlaces")
    public List<String> targetTopographicPlaces = new ArrayList<>();

    /**
     * Look for matching stops outside the given counties.
     * If there is a match, only append the original ID.
     */
    @QueryParam(value = "onlyMatchOutsideTopographicPlaces")
    public List<String> onlyMatchOutsideTopographicPlaces = new ArrayList<>();

    @QueryParam(value = "importType")
    public ImportType importType = ImportType.MERGE;
    
    @QueryParam(value = "skipOutput")
    public boolean skipOutput = false;

    @QueryParam(value = "ignoreStopTypes")
    public Set<StopTypeEnumeration> ignoreStopTypes;

    @QueryParam(value = "allowOnlyStopTypes")
    public Set<StopTypeEnumeration> allowOnlyStopTypes;

    @ApiParam("Specify this to erase existing topographic places with matching id and topographic place type. Example: 'OSM;PLACE_OF_INTEREST'")
    @QueryParam(value = "eraseTopographicPlaceWithIdPrefixAndType")
    public String eraseTopographicPlaceWithIdPrefixAndType;
}
