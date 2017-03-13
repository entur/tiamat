package org.rutebanken.tiamat.netex.id;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class GaplessIdGeneratorTest extends CommonSpringBootTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private HibernateEntityManagerFactory hibernateEntityManagerFactory;

    @Test
    public void verifyNetexIdAssignedToStop() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);
        assertThat(stopPlace.getNetexId()).isNotNull();
    }

    @Ignore // Using H2 with unique constraint for id_generator table.
    @Test
    public void explicitIdMustBeInsertedIntoHelperTable() {

        String wantedId = NetexIdMapper.getNetexId("Quay", "1");

        Quay quay = new Quay();
        quay.setNetexId(wantedId);

        quayRepository.save(quay);

        Session session = hibernateEntityManagerFactory.getSessionFactory().openSession();

        SQLQuery query = session.createSQLQuery("SELECT id_value FROM id_generator WHERE table_name = '" + Quay.class.getSimpleName() + "' AND id_value = '" + wantedId + "'");

        List list = query.list();
        BigInteger actual = (BigInteger) list.get(0);

        assertThat(actual.longValue()).describedAs("Expecting to find the ID in the id_generator table").isEqualTo(wantedId);
    }

    @Test
    public void generateIdAfterExplicitIDs() throws InterruptedException {

        // Use first 500 IDs
        for(long explicitId = 1; explicitId <= 600; explicitId ++) {
            Quay quay = new Quay();
            quay.setNetexId(NetexIdMapper.getNetexId(Quay.class.getSimpleName(), String.valueOf(explicitId)));
            quayRepository.save(quay);
            System.out.println("Saved quay: " + quay.getNetexId());
        }

        Quay quay = new Quay();
        quayRepository.save(quay);
        assertThat(NetexIdMapper.getNetexIdPostfix(quay.getNetexId())).isEqualTo(601);
    }

    /**
     * Was implemented under the supsicion that OptionalIdCreator caused a bug.
     * But it was a matter of keeping the attached returned entity from save (in case the entity was merged)
     * See NRP-1171
     */
    @Test
    public void testUpdatingStopPlace() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("test"));
        stopPlace = stopPlaceRepository.save(stopPlace);

        assertThat(stopPlace.getNetexId()).isNotNull();
        String id = stopPlace.getNetexId();

        Quay quay = new Quay(new EmbeddableMultilingualString("quayTest"));
        quay = quayRepository.save(quay);

        //Add Quay, and save StopPlace
        stopPlace.getQuays().add(quay);
        stopPlace = stopPlaceRepository.save(stopPlace);

        Quay quay2 = new Quay(new EmbeddableMultilingualString("quay2Test"));
        quay2 = quayRepository.save(quay2);

        //Add another Quay and save StopPlace
        stopPlace.getQuays().add(quay2);
        stopPlace = stopPlaceRepository.save(stopPlace);

        assertEquals(id, stopPlace.getNetexId());
    }

}
