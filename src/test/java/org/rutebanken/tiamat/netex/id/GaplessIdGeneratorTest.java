package org.rutebanken.tiamat.netex.id;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class GaplessIdGeneratorTest extends CommonSpringBootTest {

//    @Autowired
//    private StopPlaceRepository stopPlaceRepository;
//
//    @Autowired
//    private QuayRepository quayRepository;
//
//    @Autowired
//    private HibernateEntityManagerFactory hibernateEntityManagerFactory;
//
//    @Test
//    public void verifyStopPlaceIsAssignedId() {
//        StopPlace stopPlace = new StopPlace();
//        stopPlaceRepository.save(stopPlace);
//        assertThat(stopPlace.getId()).isNotNull();
//    }
//
//    @Test
//    public void explicitIdMustBeInserted() {
//
//        GaplessOptionalGenerator gaplessOptionalGenerator = new GaplessOptionalGenerator();
//
//        Session session = hibernateEntityManagerFactory.getSessionFactory().openSession();
//
//        long wantedId = 12L;
//
//        Quay quay = new Quay();
//        quay.setId(wantedId);
//
//        Serializable serializable = gaplessOptionalGenerator.generate((SessionImplementor) session, quay);
//        Long gotId = (Long) serializable;
//        assertThat(gotId).isNotNull();
//        assertThat(gotId).isEqualTo(wantedId);
//
//        SQLQuery query = session.createSQLQuery("SELECT id_value FROM id_generator WHERE table_name = 'quay' AND id_value = " + wantedId + "");
//
//        List list = query.list();
//        BigInteger actual = (BigInteger) list.get(0);
//
//        assertThat(actual.longValue()).describedAs("Expecting to find the ID in the id_generator table").isEqualTo(wantedId);
//    }
//
//    @Test
//    public void generateIdAfterExplicitIDs() {
//
//        GaplessOptionalGenerator gaplessOptionalGenerator = new GaplessOptionalGenerator();
//
//        SessionImplementor session = (SessionImplementor) hibernateEntityManagerFactory.getSessionFactory().openSession();
//
//        // Use first 500 IDs
//        for(long explicitId = 1; explicitId <= 600; explicitId ++) {
//            Quay quay = new Quay();
//            quay.setId(explicitId);
//            gaplessOptionalGenerator.generate(session, quay);
//        }
//
//        // Get next ID which should be 501
//        Serializable serializable = gaplessOptionalGenerator.generate(session, new Quay());
//        Long gotId = (Long) serializable;
//        assertThat(gotId).isEqualTo(601);
//    }
//
//    /**
//     * Was implemented under the supsicion that OptionalIdCreator caused a bug.
//     * But it was a matter of keeping the attached returned entity from save (in case the entity was merged)
//     * See NRP-1171
//     */
//    @Test
//    public void testUpdatingStopPlace() {
//        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("test"));
//        stopPlace = stopPlaceRepository.save(stopPlace);
//
//        assertThat(stopPlace.getId()).isNotNull();
//        Long id = stopPlace.getId();
//
//        Quay quay = new Quay(new EmbeddableMultilingualString("quayTest"));
//        quay = quayRepository.save(quay);
//
//        //Add Quay, and save StopPlace
//        stopPlace.getQuays().add(quay);
//        stopPlace = stopPlaceRepository.save(stopPlace);
//
//        Quay quay2 = new Quay(new EmbeddableMultilingualString("quay2Test"));
//        quay2 = quayRepository.save(quay2);
//
//        //Add another Quay and save StopPlace
//        stopPlace.getQuays().add(quay2);
//        stopPlace = stopPlaceRepository.save(stopPlace);
//
//        assertEquals(id, stopPlace.getId());
//    }

}
