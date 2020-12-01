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

import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;

import java.time.Instant;

/**
 * DTO for stop place change log
 */
public class StopPlaceChangelogDto {


    public String netexId;
    public String name;
    public int version;
    public StopTypeEnumeration stopTypeEnumeration;
    public Instant changeAt;

    public ValidBetween validBetween;



    public StopPlaceChangelogDto(String netexId, String name, int version, StopTypeEnumeration stopTypeEnumeration, Instant changeAt, ValidBetween validBetween) {
        this.netexId = netexId;
        this.name = name;
        this.version = version;
        this.stopTypeEnumeration = stopTypeEnumeration;
        this.changeAt = changeAt;
        this.validBetween=validBetween;
    }

}
