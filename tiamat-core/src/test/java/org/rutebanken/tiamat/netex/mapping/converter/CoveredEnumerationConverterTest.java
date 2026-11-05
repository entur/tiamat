package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.entur.netex2.model.CoveredEnumeration;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CoveredEnumerationConverterTest {

    private final CoveredEnumerationConverter converter = new CoveredEnumerationConverter();
    private final MappingContext mappingContext = new MappingContext(new HashMap<>());
    private final Type<Boolean> booleanType = new TypeBuilder<Boolean>() {}.build();
    private final Type<CoveredEnumeration> coveredType = new TypeBuilder<CoveredEnumeration>() {}.build();

    @Test
    public void convertFromAcceptsTrueAndFalse() {
        assertThat(converter.convertFrom(CoveredEnumeration.TRUE, booleanType, mappingContext)).isTrue();
        assertThat(converter.convertFrom(CoveredEnumeration.FALSE, booleanType, mappingContext)).isFalse();
    }

    @Test
    public void convertFromRejectsValuesOtherThanTrueAndFalse() {
        assertThatThrownBy(() -> converter.convertFrom(CoveredEnumeration.MIXED, booleanType, mappingContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'mixed'");
    }

    @Test
    public void convertToWritesTrueAndFalse() {
        assertThat(converter.convertTo(true, coveredType, mappingContext)).isEqualTo(CoveredEnumeration.TRUE);
        assertThat(converter.convertTo(false, coveredType, mappingContext)).isEqualTo(CoveredEnumeration.FALSE);
    }
}
