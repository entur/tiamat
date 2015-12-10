package no.rutebanken.tiamat.repository.ifopt;


public class StopPlaceRepositoryTest {

    /*
    @Test
    public void findByCentroidLocationLatitudeLike() {


        StopPlace stopPlace = new StopPlace();
        SimplePoint centroid = new SimplePoint();

        Location location = new Location();

        location.setLongitude(new BigDecimal("33.942886").setScale(10, BigDecimal.ROUND_CEILING));
        location.setLatitude(new BigDecimal("-118.411713").setScale(10, BigDecimal.ROUND_CEILING));

        centroid.setLocation(location);

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Pageable pageable = new PageRequest(0,100);
        Page<StopPlace> result = stopPlaceRepository.findNearby(location.getLatitude(), location.getLongitude(), pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getId()).isEqualTo(stopPlace.getId());
    }*/

}