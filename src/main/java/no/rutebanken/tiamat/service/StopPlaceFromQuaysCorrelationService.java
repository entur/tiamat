package no.rutebanken.tiamat.service;

import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.StopPlace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class StopPlaceFromQuaysCorrelationService {
	private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromQuaysCorrelationService.class);

	@Autowired
	public QuayRepository quayRepository;

	@Autowired
	private StopPlaceRepository stopPlaceRepository;

    private final AtomicInteger stopPlaceCounter = new AtomicInteger();


	/**
	 * Creates stopPlace objects based on quays by combining quays with the same
	 * name and close location
	 */
	public void correlate() {

        logger.trace("Loading quays from repository");
		List<Quay> quays = quayRepository.findAll();

        logger.trace("Got {} quays", quays.size());

		Map<String, List<Quay>> distinctQuays = quays.stream()
				.collect(Collectors.groupingBy(quay -> quay.getName().getValue()));

        logger.trace("Got {} distinct quays based on name", distinctQuays.size());

        List<String> quaysAlreadyProcessed = new ArrayList<>();

        distinctQuays.keySet().forEach(quayGroupName -> {

            logger.trace("Processing quay with name {}", quayGroupName);

			StopPlace stopPlace = new StopPlace();
			stopPlace.setName(new MultilingualString(quayGroupName, "no", ""));

			stopPlace.setQuays(new ArrayList<>());

            distinctQuays.get(quayGroupName).forEach(item -> {

				logger.trace("About to add Quay with name {} and id {} to stop place", item.getName(), item.getId());
				if (quaysAlreadyProcessed.contains(item.getId())){
					logger.warn("Already created quay with name {} and id {}", item.getName(), item.getId());
				}else{
					stopPlace.getQuays().add(item);
                    quaysAlreadyProcessed.add(item.getId());
                    quayRepository.save(item);
				}
			});

			try{	
				stopPlaceRepository.save(stopPlace);
                logger.trace("Created stop place number {} with name {} and id {}",
                        stopPlaceCounter.incrementAndGet(), stopPlace.getName(), stopPlace.getId());
			}
			catch(Exception e){
				logger.warn("Caught exception when saving stop place with name {}", quayGroupName, e);
			}
		});

		logger.debug("Amount of created stop places: {}", stopPlaceCounter.get());
	}

}
