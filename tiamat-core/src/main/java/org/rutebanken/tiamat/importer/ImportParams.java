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

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.ws.rs.QueryParam;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

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

    @Parameter(description = "Force stop place type for all stop place in file. Useful if no modality defined in the netex file.")
    @QueryParam(value = "forceStopType")
    public StopTypeEnumeration forceStopType;

    @Parameter(description = "Specify this to erase existing topographic places with matching id and topographic place type. Example: 'OSM;PLACE_OF_INTEREST'")
    @QueryParam(value = "eraseTopographicPlaceWithIdPrefixAndType")
    public String eraseTopographicPlaceWithIdPrefixAndType;

    @Parameter(description = "Import only tariff zones, fare zones and group of tariff zones, ignore rest e.g. stop places, topographic places etc")
    @QueryParam(value = "importOnlyTariffZones")
    public boolean importOnlyTariffZones = false;

    @Parameter(description = "Specify which frame to use for fare zone import: SITE_FRAME (default) or FARE_FRAME")
    @QueryParam(value = "fareZoneFrameSource")
    public FareZoneFrameSource fareZoneFrameSource;

    @Parameter(description = "Disable pre and post processing steps, import raw data as is")
    @QueryParam(value = "disablePreAndPostProcessing")
    public boolean disablePreAndPostProcessing = false;
}
