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

package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.tiamat.exporter.params.ExportParams;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NetexReferenceRemovingIterator implements Iterator<StopPlace> {

    public final Iterator<StopPlace> iterator;

    private final Consumer<StopPlace> tariffZoneVersionRemover;

    private final Consumer<StopPlace> topographicPlaceVersionRemover;

    private final Consumer<StopPlace> fareZoneRefRemover;

    private final Consumer<StopPlace> doNothingConsumer = s -> {
    };

    public NetexReferenceRemovingIterator(Iterator<StopPlace> iterator, ExportParams exportParams) {
        this.iterator = iterator;

        if (exportParams.getTariffZoneExportMode().equals(ExportParams.ExportMode.NONE)) {
            tariffZoneVersionRemover = this::removeTariffZoneRefsVersion;
        } else {
            tariffZoneVersionRemover = doNothingConsumer;
        }


        if (exportParams.getTopographicPlaceExportMode().equals(ExportParams.ExportMode.NONE)) {
            topographicPlaceVersionRemover = this::removeTopographicPlaceRef;
        } else {
            topographicPlaceVersionRemover = doNothingConsumer;
        }

        if (!exportParams.getServiceFrameExportMode().equals(ExportParams.ExportMode.ALL)) {
            fareZoneRefRemover = this::removeFareZoneRef;
        } else {
            fareZoneRefRemover = doNothingConsumer;
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public StopPlace next() {
        StopPlace next = iterator.next();
        if(next != null) {
            tariffZoneVersionRemover.accept(next);
            topographicPlaceVersionRemover.accept(next);
            fareZoneRefRemover.accept(next);
        }
        return next;
    }

    private void removeTariffZoneRefsVersion(StopPlace stopPlace) {
        if(stopPlace.getTariffZones() != null && stopPlace.getTariffZones().getTariffZoneRef() != null) {
            stopPlace.getTariffZones().getTariffZoneRef().forEach(tariffZoneRef -> tariffZoneRef.setVersion(null));
        }
    }

    private void removeTopographicPlaceRef(StopPlace stopPlace) {
        if(stopPlace.getTopographicPlaceRef() != null) {
            stopPlace.getTopographicPlaceRef().setVersion(null);
        }
    }

    private void removeFareZoneRef(StopPlace stopPlace) {
        if (stopPlace.getTariffZones() != null && !stopPlace.getTariffZones().getTariffZoneRef().isEmpty()) {
                final List<TariffZoneRef> tariffZoneRefs = stopPlace.getTariffZones().getTariffZoneRef().stream()
                        .filter(tariffZoneRef -> !tariffZoneRef.getRef().contains("FareZone"))
                        .map(tariffZoneRef -> new ObjectFactory().createTariffZoneRef().withRef(tariffZoneRef.getRef()).withVersion(tariffZoneRef.getVersion()))
                        .collect(Collectors.toList());
                stopPlace.withTariffZones(new TariffZoneRefs_RelStructure().withTariffZoneRef(tariffZoneRefs));

        }
    }
}
