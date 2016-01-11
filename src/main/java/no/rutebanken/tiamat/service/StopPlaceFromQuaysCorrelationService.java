package no.rutebanken.tiamat.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.StopPlace;

@Service
public class StopPlaceFromQuaysCorrelationService {
	private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromQuaysCorrelationService.class);

	@Autowired
	public QuayRepository quayRepository;

	@Autowired
	private StopPlaceRepository stopPlaceRepository;


	/**
	 * Creates stopPlace objects based on quays by combining quays with the same
	 * name and close location
	 */
	public void correlate() {
		List<Quay> quays = quayRepository.findAll();
		logger.debug("Sorting all quays. Total amount: {}", quays.size());
		for (int i = 0; i < 10; i++) {
			logger.debug(quays.get(i).getName().toString());
		}
		
		List<String> usedQuays = new ArrayList<>();
		Map<String, List<Quay>> distinctQuays = quays.stream()
				.collect(Collectors.groupingBy(q -> q.getName().getValue()));
		distinctQuays.keySet().forEach(name -> {
			StopPlace sp = new StopPlace();
			sp.setName(new MultilingualString(name, "no", ""));
			//sp.setQuays(distinctQuays.get(name));
			sp.setQuays(new ArrayList<>());
			distinctQuays.get(name).forEach(item -> {
				logger.trace("About to add the following quay: {}. contains? {}", item.getId(), usedQuays.contains(item.getId()));
				if (usedQuays.contains(item.getId())){
					logger.warn("Already created QUAY WITH ID {}", item.getId());
				}else{
					sp.getQuays().add(item);
					usedQuays.add(item.getId());
			logger.trace("Adding quay to used quay: {}", item.getId());
				}
			});
			try{	
				stopPlaceRepository.save(sp);
			}
			catch(Exception e){
				logger.debug(e.getMessage());
			}
				//usedQuayIds = new ArrayList<String>(0);
				logger.trace("Created new stop place# {}: {} with id {}", stopPlaceRepository.count(), sp.getName(),
						sp.getId());
				

		});

		// quays.stream()
		// .map(Wrapper::new).distinct().map(Wrapper::unwrap)
		// .forEach(item -> {
		// List<Quay> relatedQuays = quays.stream()
		// .filter(q ->
		// q.getName().getValue().equals(item.getName().getValue()))
		// .collect(Collectors.toList());
		// relatedQuays.forEach(q -> q.setId(null));
		// StopPlace sp = new StopPlace();
		// sp.setName(new MultilingualString(item.getName().getValue(), "no",
		// ""));
		// sp.setDescription(new
		// MultilingualString(item.getDescription().getValue(), "no", ""));
		// sp.setQuays(relatedQuays);
		// stopPlaceRepository.save(sp);
		// });

		logger.debug("Amount of created stop places: {}", stopPlaceRepository.count());
	}

}
