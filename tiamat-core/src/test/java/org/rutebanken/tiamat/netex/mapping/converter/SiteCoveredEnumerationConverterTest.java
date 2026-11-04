package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.tiamat.model.CoveredEnumeration;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SiteCoveredEnumerationConverterTest {

    private final SiteCoveredEnumerationConverter converter = new SiteCoveredEnumerationConverter();
    private final MappingContext mappingContext = new MappingContext(new HashMap<>());
    private final Type<CoveredEnumeration> tiamatType = new TypeBuilder<CoveredEnumeration>() {}.build();
    private final Type<org.entur.netex2.model.CoveredEnumeration> netexType = new TypeBuilder<org.entur.netex2.model.CoveredEnumeration>() {}.build();

    @Test
    public void convertFromMapsSiteValuesByName() {
        assertThat(converter.convertFrom(org.entur.netex2.model.CoveredEnumeration.MIXED, tiamatType, mappingContext))
                .isEqualTo(CoveredEnumeration.MIXED);
    }

    @Test
    public void convertFromRejectsTrueAndFalse() {
        assertThatThrownBy(() -> converter.convertFrom(org.entur.netex2.model.CoveredEnumeration.TRUE, tiamatType, mappingContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'true'");
        assertThatThrownBy(() -> converter.convertFrom(org.entur.netex2.model.CoveredEnumeration.FALSE, tiamatType, mappingContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'false'");
    }

    @Test
    public void convertToMapsEveryTiamatValue() {
        for (CoveredEnumeration value : CoveredEnumeration.values()) {
            assertThat(converter.convertTo(value, netexType, mappingContext).name()).isEqualTo(value.name());
        }
    }
}
