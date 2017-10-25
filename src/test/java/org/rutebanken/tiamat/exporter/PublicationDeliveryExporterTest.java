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

package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.xml.bind.JAXBException;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.exporter.params.ExportParams.newExportParamsBuilder;

public class PublicationDeliveryExporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryExporter publicationDeliveryExporter;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void exportPublicationDeliveryWithStopPlace() throws JAXBException {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:987");
        stopPlaceRepository.save(stopPlace);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(
                newExportParamsBuilder()
                        .setStopPlaceSearch(new StopPlaceSearch())
                        .build());

        String expectedId = "NSR:StopPlace:987";
        StopPlace actual = publicationDeliveryTestHelper.findStopPlace(publicationDeliveryStructure, expectedId);
        assertThat(actual).isNotNull();
    }

    @Test
    public void exportPlacesWithEffectiveChangeInPeriodWithChildren() throws JAXBException {

        Instant now = Instant.now();

        org.rutebanken.tiamat.model.StopPlace parent = new org.rutebanken.tiamat.model.StopPlace();
        parent.setValidBetween(new ValidBetween(now));
        parent.setParentStopPlace(true);
        parent.setNetexId("NSR:StopPlace:987");
        parent.setVersion(1L);
        stopPlaceRepository.save(parent);


        org.rutebanken.tiamat.model.StopPlace child = new org.rutebanken.tiamat.model.StopPlace();
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        child.setNetexId("NSR:StopPlace:321321");
        stopPlaceRepository.save(child);

        parent.getChildren().add(child);
        stopPlaceRepository.save(parent);


        Pageable pageable = new PageRequest(0, 10);
        ChangedStopPlaceSearch changedStopPlaceSearch = new ChangedStopPlaceSearch(now.minusSeconds(100), now.plusSeconds(100), pageable);

        ExportParams exportParams = ExportParams.newExportParamsBuilder().build();

        PublicationDeliveryStructurePage publicationDeliveryStructurePage = publicationDeliveryExporter.exportStopPlacesWithEffectiveChangeInPeriod(changedStopPlaceSearch, exportParams);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryStructurePage.publicationDeliveryStructure;

        StopPlace actualParent = publicationDeliveryTestHelper.findStopPlace(publicationDeliveryStructure, parent.getNetexId());
        assertThat(actualParent).isNotNull();
        StopPlace actualChild = publicationDeliveryTestHelper.findStopPlace(publicationDeliveryStructure, child.getNetexId());
        assertThat(actualChild).as("child stop "+child.getNetexId() + " should be included").isNotNull();
    }

    @Test
    public void exportStopPlacesWithEffectiveChangeInPeriodIgnoreChildrenThatHaveBecomePartOfMultimodalStopPlace() {

        Instant now = Instant.now();

        org.rutebanken.tiamat.model.StopPlace monomodal = new org.rutebanken.tiamat.model.StopPlace();
        monomodal.setValidBetween(new ValidBetween(now));
        monomodal.setParentStopPlace(false);
        monomodal.setNetexId("NSR:StopPlace:666");
        monomodal.setVersion(1L);
        stopPlaceRepository.save(monomodal);

        org.rutebanken.tiamat.model.StopPlace parent = new org.rutebanken.tiamat.model.StopPlace();
        parent.setValidBetween(new ValidBetween(now.plusSeconds(10)));
        parent.setParentStopPlace(true);
        parent.setNetexId("NSR:StopPlace:187");
        parent.setVersion(1L);
        stopPlaceRepository.save(parent);

        org.rutebanken.tiamat.model.StopPlace child = new org.rutebanken.tiamat.model.StopPlace();
        child.setParentStopPlace(false);
        child.setNetexId("NSR:StopPlace:666");
        // The monomodal stop place has been moved into a multi modal stop place as a child
        child.setVersion(2L);
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        stopPlaceRepository.save(child);

        parent.getChildren().add(child);
        stopPlaceRepository.save(parent);


        Pageable pageable = new PageRequest(0, 10);
        ChangedStopPlaceSearch changedStopPlaceSearch = new ChangedStopPlaceSearch(now.minusSeconds(100), now.plusSeconds(100), pageable);

        ExportParams exportParams = ExportParams.newExportParamsBuilder().build();

        PublicationDeliveryStructurePage publicationDeliveryStructurePage = publicationDeliveryExporter.exportStopPlacesWithEffectiveChangeInPeriod(changedStopPlaceSearch, exportParams);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryStructurePage.publicationDeliveryStructure;

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(publicationDeliveryStructure);

        assertThat(result)
                .as("Expecting two stop places. The parent and the child. The monomodal stop place shall not be returned because it has been moved into the multi modal stop place")
                .hasSize(2);

        StopPlace actualParent = publicationDeliveryTestHelper.findStopPlace(publicationDeliveryStructure, parent.getNetexId());
        assertThat(actualParent).isNotNull();
        StopPlace actualChild = publicationDeliveryTestHelper.findStopPlace(publicationDeliveryStructure, child.getNetexId());
        assertThat(actualChild).as("child stop "+child.getNetexId() + " should be included").isNotNull();
        assertThat(actualChild.getParentSiteRef()).as("child stop "+child.getNetexId() + " should have parent site ref").isNotNull();

    }
}