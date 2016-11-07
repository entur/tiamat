package org.rutebanken.tiamat.importers;

import org.junit.Test;
import org.rutebanken.tiamat.model.MultilingualString;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;

public class NameToDescriptionMoverTest {


    /**
     *
     *  (i Str�msveien)
     *  Dam (i Kvernstuveien)
     *  Finsbr�ten (snuplass)
     *  Hauger (KB mot sentrum)
     *  H�kvikdalen kryss (gamle E6)
     *  Linderudsletta (nordsiden)
     *  Linderudsletta (sydsiden)
     *  Skulane (Sykkylven)
     *
     *  Ikke flyttes
     *  Tonstadkrysset 4
     */

    private NameToDescriptionMover nameToDescriptionMover = new NameToDescriptionMover();

    @Test
    public void strømsveien() {
      expectNameAndDescriptionToBeMoved("Alfaset (i Strømsveien)", "Alfaset", "i Strømsveien");
    }


    private void expectNameAndDescriptionToBeMoved(String originalName, String expectedName, String expectedDescription) {
        StopPlace stopPlace = new StopPlace(new MultilingualString(originalName));
        nameToDescriptionMover.move(stopPlace);
        assertThat(stopPlace.getName().getValue()).isEqualTo(expectedName);
        assertThat(stopPlace.getDescription().getValue()).isEqualTo(expectedDescription);
    }
}