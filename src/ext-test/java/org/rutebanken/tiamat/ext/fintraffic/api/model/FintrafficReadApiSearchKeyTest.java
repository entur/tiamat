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
    }

    @Test
    void constructorWithValues() {
        String[] modes = {"bus", "tram"};
        String[] codes = {"ABC", "DEF"};

        FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(modes, codes);

        assertThat(searchKey.transportModes(), equalTo(modes));
        assertThat(searchKey.areaCodes(), equalTo(codes));
    }

    @Test
    void constructorWithNullValues() {
        FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(null, null);

        assertThat(searchKey.transportModes(), equalTo(null));
        assertThat(searchKey.areaCodes(), equalTo(null));
    }
}

