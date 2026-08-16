package org.rutebanken.tiamat.repository.search;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.util.Map;

public class StopPlaceQueryFromSearchBuilderTest extends TiamatIntegrationTest {

    @Autowired
    StopPlaceQueryFromSearchBuilder stopPlaceQueryFromSearchBuilder;

    @Test
    public void handleCommonWordsInQuery() {

        ExportParamsAndStopPlaceSearchValidator exportParamsAndStopPlaceSearchValidator = new ExportParamsAndStopPlaceSearchValidator();
        stopPlaceQueryFromSearchBuilder = new StopPlaceQueryFromSearchBuilder("des,de,ould,wordToBeSkipped", exportParamsAndStopPlaceSearchValidator);

        String result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Gare de dax");

        Assertions.assertThat(result).isEqualToIgnoringCase("Gare%dax");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Place des cyprès");

        Assertions.assertThat(result).isEqualToIgnoringCase("Place%cyprès");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("ShouldNotBeReplaced");

        Assertions.assertThat(result).isEqualToIgnoringCase("ShouldNotBeReplaced");

        result = stopPlaceQueryFromSearchBuilder.handleCommonWordsInQuery("Should wordToBeSkipped contain only one joker between each word");

        Assertions.assertThat(result).isEqualToIgnoringCase("Should%contain%only%one%joker%between%each%word");
    }

    @Test
    public void handleQueryWithQuayPrivateAndPublicCodes() {
        String privateCode = "somePrivateCode";
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                    StopPlaceSearch.newStopPlaceSearchBuilder()
                            .setQuery(privateCode)
                            .setWithQuayPublicAndPrivateCodes(true)
                            .build())
                .build();

        Pair<String, Map<String, Object>> queryString = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);

        Assertions.assertThat(queryString.getFirst()).contains("quay_query_search");
        Assertions.assertThat(queryString.getSecond().containsKey("query")).isTrue();
        Assertions.assertThat(queryString.getSecond().get("query")).isEqualTo(privateCode);
    }
}