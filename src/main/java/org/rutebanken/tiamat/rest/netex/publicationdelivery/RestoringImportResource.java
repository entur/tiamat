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

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hazelcast.core.HazelcastInstance;
import io.swagger.annotations.Api;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.restore.GenericRestoringImporter;
import org.rutebanken.tiamat.importer.restore.RestoringParkingImporter;
import org.rutebanken.tiamat.importer.restore.RestoringStopPlaceImporter;
import org.rutebanken.tiamat.importer.restore.RestoringTopographicPlaceImporter;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.async.EntityQueueProcessor;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.async.PublicationDeliveryPartialUnmarshaller;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.async.UnmarshalResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_ADMIN_PATH;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_PATH;
import static org.rutebanken.tiamat.rest.netex.publicationdelivery.async.RunnableUnmarshaller.POISON_PARKING;
import static org.rutebanken.tiamat.rest.netex.publicationdelivery.async.RunnableUnmarshaller.POISON_STOP_PLACE;

/**
 * Restore tiamat from earlier export
 */
@Component
@Api
@Path("netex")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RestoringImportResource {

    private static final Logger logger = LoggerFactory.getLogger(RestoringImportResource.class);
    private static final String KEY_RESTORING_IMPORT_LOCK = "restoring_import_lock";

    private final PublicationDeliveryPartialUnmarshaller publicationDeliveryPartialUnmarshaller;
    private final NetexMapper netexMapper;
    private final RestoringTopographicPlaceImporter restoringTopographicPlaceImporter;
    private final RestoringParkingImporter restoringParkingImporter;
    private final RestoringStopPlaceImporter restoringStopPlaceImporter;
    private final HazelcastInstance hazelcastInstance;
    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final GenericRestoringImporter genericRestoringImporter;

    @Autowired
    public RestoringImportResource(PublicationDeliveryPartialUnmarshaller publicationDeliveryPartialUnmarshaller,
                                   NetexMapper netexMapper,
                                   RestoringTopographicPlaceImporter restoringTopographicPlaceImporter,
                                   RestoringStopPlaceImporter restoringStopPlaceImporter,
                                   RestoringParkingImporter restoringParkingImporter,
                                   HazelcastInstance hazelcastInstance, PublicationDeliveryHelper publicationDeliveryHelper,
                                   GenericRestoringImporter genericRestoringImporter) {
        this.publicationDeliveryPartialUnmarshaller = publicationDeliveryPartialUnmarshaller;
        this.netexMapper = netexMapper;
        this.restoringTopographicPlaceImporter = restoringTopographicPlaceImporter;
        this.restoringStopPlaceImporter = restoringStopPlaceImporter;
        this.restoringParkingImporter = restoringParkingImporter;
        this.hazelcastInstance = hazelcastInstance;
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.genericRestoringImporter = genericRestoringImporter;
    }

    /**
     * This method requires all incoming data to have IDs that are previously generated by Tiamat and that they are unique.
     * IDs for quays and stop places will not be generated. They will be used as is.
     * TODO: Move this to PublicationDeliveryImporter class
     */
    @POST
    @Path("restoring_import")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response importPublicationDeliveryOnEmptyDatabase(InputStream inputStream) throws IOException, JAXBException, SAXException, XMLStreamException, InterruptedException, ParserConfigurationException {

        Lock lock = hazelcastInstance.getLock(KEY_RESTORING_IMPORT_LOCK);

        if (lock.tryLock()) {
            int threads = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = Executors.newFixedThreadPool(threads, new ThreadFactoryBuilder().setNameFormat("importer-%d").build());

            try {
                UnmarshalResult unmarshalResult = publicationDeliveryPartialUnmarshaller.unmarshal(inputStream);
                AtomicInteger topographicPlacesCounter = new AtomicInteger();

                SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(unmarshalResult.getPublicationDeliveryStructure());
                List<org.rutebanken.netex.model.TopographicPlace> netexTopographicPlaces = publicationDeliveryHelper.extractTopographicPlaces(netexSiteFrame);

                if (netexTopographicPlaces != null) {
                    logger.info("Importing {} topographic places", netexTopographicPlaces.size());
                    restoringTopographicPlaceImporter.importTopographicPlaces(topographicPlacesCounter, netexTopographicPlaces);
                    logger.info("Finished importing {} topographic places", topographicPlacesCounter);
                }

                AtomicInteger tariffZonesCounter = new AtomicInteger();
                if(publicationDeliveryHelper.hasTariffZones(netexSiteFrame)) {
                    genericRestoringImporter.importObjects(tariffZonesCounter, netexSiteFrame.getTariffZones().getTariffZone(), TariffZone.class);
                } else {
                    logger.info("No tariff zones detected");
                }

                AtomicInteger pathLinksCounter = new AtomicInteger();
                if(publicationDeliveryHelper.hasPathLinks(netexSiteFrame)) {
                    genericRestoringImporter.importObjects(pathLinksCounter, netexSiteFrame.getPathLinks().getPathLink(), PathLink.class);
                } else {
                    logger.info("No path links to import");
                }

                logger.info("Importing stops");
                AtomicInteger stopPlacesImported = new AtomicInteger(0);
                AtomicBoolean stopStopPlaceExecution = new AtomicBoolean(false);
                Consumer<StopPlace> stopPlaceConsumer = stopPlace -> restoringStopPlaceImporter.importStopPlace(stopPlacesImported, netexMapper.mapToTiamatModel(stopPlace));
                submitNTimes(threads, executorService, new EntityQueueProcessor<>(unmarshalResult.getStopPlaceQueue(), stopStopPlaceExecution, stopPlaceConsumer, POISON_STOP_PLACE));

                logger.info("Importing parkings");
                AtomicInteger parkingsImported = new AtomicInteger(0);
                AtomicBoolean stopParkingExecution = new AtomicBoolean(false);
                Consumer<Parking> parkingConsumer = parking -> restoringParkingImporter.importParking(parkingsImported, netexMapper.mapToTiamatModel(parking));
                submitNTimes(threads, executorService, new EntityQueueProcessor<>(unmarshalResult.getParkingQueue(), stopParkingExecution, parkingConsumer, POISON_PARKING));

                logger.info("Waiting for all import tasks to finish");

                executorService.shutdown();
                executorService.awaitTermination(150, TimeUnit.MINUTES);

                return Response.ok("Imported " + stopPlacesImported.get() + " stop places, "
                        + parkingsImported.get() + " parkings, "
                        + topographicPlacesCounter.get() + " topographic places,"
                        + pathLinksCounter.get() + " path links,"
                        + tariffZonesCounter + " tariff zones")
                        .build();

            } catch (Exception e) {
                logger.error("Caught exception while importing publication delivery initially", e);
                executorService.shutdownNow();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception while import publication delivery: " + e.getMessage()).build();
            } finally {
                lock.unlock();
            }
        }
        return Response.status(Response.Status.CONFLICT).entity("There is already an import job running").build();
    }

    private void submitNTimes(int times, ExecutorService executorService, EntityQueueProcessor task) {
        for (int i = 0; i < times; i++) {
            executorService.submit(task);
        }
    }
}
