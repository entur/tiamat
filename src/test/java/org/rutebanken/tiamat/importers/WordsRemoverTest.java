package org.rutebanken.tiamat.importers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class WordsRemoverTest {

    private final WordsRemover wordsRemover = new WordsRemover();

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
    public void removeBusTerminalInBraces() {
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
}