package org.rutebanken.tiamat.repository.search;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

public class StopPlaceQueryFromSearchBuilderTest {

    @Test
    public void handleCommonWordsInQuery() {

        Set<String> commonWordsToIgnore = new LinkedHashSet<>();
        commonWordsToIgnore.add("des");
        commonWordsToIgnore.add("de");

        StopPlaceQueryFromSearchBuilder stopPlaceQueryFromSearchBuilder = new StopPlaceQueryFromSearchBuilder(commonWordsToIgnore);

        String result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Gare de dax");

        Assertions.assertThat(result).isEqualToIgnoringCase("Gare%dax");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Place des cyprès");

        Assertions.assertThat(result).isEqualToIgnoringCase("Place%cyprès");
    }
}