package org.rutebanken.tiamat.importer.modifier;

import org.junit.Test;
import org.rutebanken.tiamat.importer.modifier.WordsRemover;

import static org.assertj.core.api.Assertions.assertThat;

public class WordsRemoverTest {

    private static final WordsRemover wordsRemover = new WordsRemover();

    @Test
    public void removeBoatInSquareBrackets() {
        String actual = wordsRemover.remove("Drøbak [båt]");
        assertThat(actual).isEqualTo("Drøbak");
    }

    @Test
    public void removeBoatInBraces() {
        String actual = wordsRemover.remove("Drøbak (båt)");
        assertThat(actual).isEqualTo("Drøbak");
    }

    @Test
    public void removeBusInBraces() {
        String actual = wordsRemover.remove("Kambosenteret (buss)");
        assertThat(actual).isEqualTo("Kambosenteret");
    }

    @Test
    public void removeBusTerminalInBracesWithTrailingSpaces() {
        String actual = wordsRemover.remove("Helsfyr T (Bussterminal)      ");
        assertThat(actual).isEqualTo("Helsfyr T");
    }

    @Test
    public void removeCapitalizedBusInBraces() {
        String actual = wordsRemover.remove("Kambosenteret (Buss)");
        assertThat(actual).isEqualTo("Kambosenteret");
    }

    @Test
    public void trimTrailingSpaces() {
        String actual = wordsRemover.remove("Kambosenteret  ");
        assertThat(actual).isEqualTo("Kambosenteret");
    }

    @Test
    public void removeByBaneStopp() {
        String actual = wordsRemover.remove("Byparken, bybanestopp");
        assertThat(actual).isEqualTo("Byparken");
    }

    @Test
    public void doNotRemoveBåtHavn() {
        String actual = wordsRemover.remove("Sæbøvik, båthavn");
        assertThat(actual).isEqualTo("Sæbøvik, båthavn");
    }

}