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
