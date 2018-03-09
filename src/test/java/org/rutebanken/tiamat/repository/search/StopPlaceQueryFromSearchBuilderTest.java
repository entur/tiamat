package org.rutebanken.tiamat.repository.search;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StopPlaceQueryFromSearchBuilderTest {

    @Test
    public void handleCommonWordsInQuery() {

        StopPlaceQueryFromSearchBuilder stopPlaceQueryFromSearchBuilder = new StopPlaceQueryFromSearchBuilder("des,de,ould,wordToBeSkipped");

        String result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Gare de dax");

        Assertions.assertThat(result).isEqualToIgnoringCase("Gare%dax");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Place des cyprès");

        Assertions.assertThat(result).isEqualToIgnoringCase("Place%cyprès");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("ShouldNotBeReplaced");

        Assertions.assertThat(result).isEqualToIgnoringCase("ShouldNotBeReplaced");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Should wordToBeSkipped contain only one joker between each word");

        Assertions.assertThat(result).isEqualToIgnoringCase("Should%contain%only%one%joker%between%each%word");
    }
}