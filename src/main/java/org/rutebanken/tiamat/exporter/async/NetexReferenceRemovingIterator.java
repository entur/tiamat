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

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import java.util.Iterator;
import java.util.function.Consumer;

public class NetexReferenceRemovingIterator implements Iterator<StopPlace> {

    public final Iterator<StopPlace> iterator;

    private final Consumer<StopPlace> tariffZoneVersionRemover;

    private final Consumer<StopPlace> topographicPlaceVersionRemover;

    private final Consumer<StopPlace> fareZoneRefRemover;

    public NetexReferenceRemovingIterator(Iterator<StopPlace> iterator, ExportParams exportParams) {
        this.iterator = iterator;
        Consumer<StopPlace> doNothingConsumer = s -> {};

        if (exportParams.getTariffZoneExportMode().equals(ExportParams.ExportMode.NONE)) {
            tariffZoneVersionRemover = stopPlace -> removeTariffZoneRefsVersion(stopPlace,"TariffZone");
        } else {
            tariffZoneVersionRemover = doNothingConsumer;
        }

        if(exportParams.getFareZoneExportMode().equals(ExportParams.ExportMode.NONE)) {
            fareZoneRefRemover = stopPlace -> removeTariffZoneRefsVersion(stopPlace,"FareZone");
        } else {
            fareZoneRefRemover = doNothingConsumer;
        }


        if (exportParams.getTopographicPlaceExportMode().equals(ExportParams.ExportMode.NONE)) {
            topographicPlaceVersionRemover = this::removeTopographicPlaceRef;
        } else {
            topographicPlaceVersionRemover = doNothingConsumer;
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
            fareZoneRefRemover.accept(next);
            topographicPlaceVersionRemover.accept(next);
        }
        return next;
    }

    private void removeTariffZoneRefsVersion(StopPlace stopPlace, String type) {
        if(stopPlace.getTariffZones() != null && stopPlace.getTariffZones().getTariffZoneRef() != null) {
            stopPlace.getTariffZones().getTariffZoneRef().forEach(tariffZoneRef -> {
                if(tariffZoneRef.getRef().contains(type)) {
                    tariffZoneRef.setVersion(null);
                }
            });
        }
    }

    private void removeTopographicPlaceRef(StopPlace stopPlace) {
        if(stopPlace.getTopographicPlaceRef() != null) {
            stopPlace.getTopographicPlaceRef().setVersion(null);
        }
    }
}
