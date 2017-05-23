package org.rutebanken.tiamat.diff;

import com.google.common.collect.Sets;
import org.javers.core.diff.Diff;
import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;


public class EntityInVersionDifferTest {


    @Test
    public void diff() {

        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("name1"));
        stopPlace1.setNetexId("NSR:StopPlace:1");

        Quay commonQuay = new Quay();
        commonQuay.setNetexId("NSR:Quay:23");
        stopPlace1.setQuays(Sets.newHashSet(commonQuay));

        Quay quayToRemove = new Quay();
        quayToRemove.setNetexId("NSR:Quay:321");
        quayToRemove.setCompassBearing(1f);
        stopPlace1.getQuays().add(quayToRemove);
        System.out.println(stopPlace1);



        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("name2"));
        stopPlace2.setNetexId("NSR:StopPlace:1");

        Quay commonQuay2 = new Quay();
        commonQuay2.setNetexId("NSR:Quay:23");
        commonQuay2.setName(new EmbeddableMultilingualString("got this name"));
        stopPlace2.setQuays(Sets.newHashSet(commonQuay2));

        Quay addedQuay = new Quay();
        addedQuay.setNetexId("NSR:Quay:2");
        addedQuay.setCompassBearing(100f);
        stopPlace2.getQuays().add(addedQuay);

        EntityInVersionDiffer entityInVersionDiffer = new EntityInVersionDiffer();
        Diff diff = entityInVersionDiffer.diff(stopPlace1, stopPlace2);

        System.out.println(diff);
        System.out.println("---------");
        System.out.println(entityInVersionDiffer.customDiffString(diff));

    }


}