package org.rutebanken.tiamat.ext.fintraffic.api.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class FintrafficReadApiSearchKeyTest {

    @Test
    void emptyReturnsNonNull() {
        FintrafficReadApiSearchKey searchKey = FintrafficReadApiSearchKey.empty();
        assertThat(searchKey, notNullValue());
    }

    @Test
    void emptyReturnsEmptyArrays() {
        FintrafficReadApiSearchKey searchKey = FintrafficReadApiSearchKey.empty();
        assertThat(searchKey.transportModes().length, equalTo(0));
        assertThat(searchKey.areaCodes().length, equalTo(0));
        assertThat(searchKey.municipalityCodes().length, equalTo(0));
    }

    @Test
    void constructorWithValues() {
        String[] modes = {"bus", "tram"};
        String[] codes = {"ABC", "DEF"};
        String[] municipalities = {"091", "049"};

        FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(modes, codes, municipalities);

        assertThat(searchKey.transportModes(), equalTo(modes));
        assertThat(searchKey.areaCodes(), equalTo(codes));
        assertThat(searchKey.municipalityCodes(), equalTo(municipalities));
    }

    @Test
    void constructorWithNullValues() {
        FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(null, null, null);

        assertThat(searchKey.transportModes(), equalTo(null));
        assertThat(searchKey.areaCodes(), equalTo(null));
        assertThat(searchKey.municipalityCodes(), equalTo(null));
    }
}

