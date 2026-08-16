package org.rutebanken.tiamat.ext.fintraffic.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AreaCodeMappingConfigTest {

    private AreaCodeMappingConfig buildConfig(Map<String, String> areaCodes) {
        return buildConfig(areaCodes, Map.of());
    }

    private AreaCodeMappingConfig buildConfig(Map<String, String> areaCodes, Map<String, String> displayNames) {
        AreaCodeMappingConfig config = new AreaCodeMappingConfig();
        config.setAreaCodes(areaCodes);
        config.setAreaCodeDisplayNames(displayNames);
        config.buildReverseIndex();
        return config;
    }

    @Test
    void getAreaCodesForMunicipalityCode_knownCode_returnsUppercaseTvvCode() {
        AreaCodeMappingConfig config = buildConfig(Map.of("lft", "499,905"));

        assertThat(config.getAreaCodesForMunicipalityCode("499")).containsExactly("LFT");
        assertThat(config.getAreaCodesForMunicipalityCode("905")).containsExactly("LFT");
    }

    @Test
    void getAreaCodesForMunicipalityCode_unknownCode_returnsEmptySet() {
        AreaCodeMappingConfig config = buildConfig(Map.of("lft", "499,905"));

        assertThat(config.getAreaCodesForMunicipalityCode("123")).isEmpty();
    }

    @Test
    void getAreaCodesForMunicipalityCode_emptyConfig_returnsEmptySet() {
        AreaCodeMappingConfig config = buildConfig(Map.of());

        assertThat(config.getAreaCodesForMunicipalityCode("499")).isEmpty();
    }

    @Test
    void getAreaCodesForMunicipalityCode_municipalityInMultipleTvvAreas_returnsAllMatchingCodes() {
        AreaCodeMappingConfig config = buildConfig(Map.of(
                "lft", "499,905",
                "tfc", "049,091,499,753"
        ));

        assertThat(config.getAreaCodesForMunicipalityCode("499")).containsExactlyInAnyOrder("LFT", "TFC");
    }

    @Test
    void getAreaCodesForMunicipalityCode_tvvKeyIsUppercased() {
        AreaCodeMappingConfig config = buildConfig(Map.of("hsl", "091,092,235"));

        assertThat(config.getAreaCodesForMunicipalityCode("091")).containsExactly("HSL");
    }

    @Test
    void getAreaCodesForMunicipalityCode_handlesWhitespaceAroundMunicipalityCodes() {
        AreaCodeMappingConfig config = buildConfig(Map.of("lft", " 499 , 905 "));

        assertThat(config.getAreaCodesForMunicipalityCode("499")).containsExactly("LFT");
        assertThat(config.getAreaCodesForMunicipalityCode("905")).containsExactly("LFT");
    }

    @Test
    void getAreaCodesForMunicipalityCode_nordicCharacterKeys_areValid() {
        AreaCodeMappingConfig config = buildConfig(
                Map.of("atk", "035,043"),
                Map.of("atk", "ÅTK")
        );

        assertThat(config.getAreaCodesForMunicipalityCode("035")).containsExactly("ÅTK");
    }

    @Test
    void buildReverseIndex_invalidAreaCodeKey_throwsException() {
        assertThatThrownBy(() -> buildConfig(Map.of("ab", "499")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid TVV area code key");
    }

    @Test
    void buildReverseIndex_invalidMunicipalityCode_throwsException() {
        assertThatThrownBy(() -> buildConfig(Map.of("lft", "499,abc")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid municipality code");
    }
}
