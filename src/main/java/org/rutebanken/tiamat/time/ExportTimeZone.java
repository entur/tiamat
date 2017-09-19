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

package org.rutebanken.tiamat.time;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
/**
 * Default time zone for exported dates.
 *
 * Should never be used for parsing incoming data!
 *
 * It does not make any assumptions for incoming time zones.
 * It should be able to run in any timezone and parse dates from any timezone.
 **/
@Component
public class ExportTimeZone {

    @Value("${tiamat.time.zone.default:Europe/Oslo}")
    private String defaultTimeZoneName;
            
    private ZoneId defaultTimeZone;


    public ZoneId getDefaultTimeZone() {
        if (defaultTimeZone==null){
            defaultTimeZone=ZoneId.of(defaultTimeZoneName);
        }
        return defaultTimeZone;
    }

}
