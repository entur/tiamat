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

package org.rutebanken.tiamat.netex.mapping;


import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.Optional;

public class NetexMappingContextThreadLocal {

    private static final Logger logger = LoggerFactory.getLogger(NetexMappingContextThreadLocal.class);

    private static final InheritableThreadLocal<NetexMappingContext> threadLocalMappingContext = new InheritableThreadLocal<>();

    public static void set(NetexMappingContext netexMappingContext) {
        threadLocalMappingContext.set(netexMappingContext);
    }

    public static NetexMappingContext get() {
        return threadLocalMappingContext.get();
    }


    public static void updateMappingContext(SiteFrame netexSiteFrame) {
        String timeZoneString = Optional.of(netexSiteFrame)
                .map(SiteFrame::getFrameDefaults)
                .map(VersionFrameDefaultsStructure::getDefaultLocale)
                .map(LocaleStructure::getTimeZone)
                .orElseThrow(() -> new NetexMappingException("Cannot resolve time zone from FrameDefaults in site frame " + netexSiteFrame.getId()));

        NetexMappingContext netexMappingContext = new NetexMappingContext();
        netexMappingContext.defaultTimeZone = ZoneId.of(timeZoneString);
        NetexMappingContextThreadLocal.set(netexMappingContext);
        logger.info("Setting default time zone for netex mapping context to {}", NetexMappingContextThreadLocal.get().defaultTimeZone);
    }

    public static void updateMappingContext(FareFrame netexFareFrame) {
        String timeZoneString = Optional.of(netexFareFrame)
                .map(FareFrame::getFrameDefaults)
                .map(VersionFrameDefaultsStructure::getDefaultLocale)
                .map(LocaleStructure::getTimeZone)
                .orElseThrow(() -> new NetexMappingException("Cannot resolve time zone from FrameDefaults in fare frame " + netexFareFrame.getId()));

        NetexMappingContext netexMappingContext = new NetexMappingContext();
        netexMappingContext.defaultTimeZone = ZoneId.of(timeZoneString);
        NetexMappingContextThreadLocal.set(netexMappingContext);
        logger.info("Setting default time zone for netex mapping context to {}", NetexMappingContextThreadLocal.get().defaultTimeZone);
    }
}
