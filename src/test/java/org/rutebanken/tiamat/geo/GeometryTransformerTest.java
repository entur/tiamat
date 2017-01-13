package org.rutebanken.tiamat.geo;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeometryTransformerTest {
    
    @Test
    public void findUtmZone32() {
        // Nesbru, Asker: 59.858690, 10.493860
        String zone = GeometryTransformer.findUtmCrs(10.493860);
        assertThat(zone).isEqualTo("EPSG:32632");
    }

    @Test
    public void findUtmZone33() {
        // Somewhere in Narvik: 68.437437, 17.426283
        String zone = GeometryTransformer.findUtmCrs(17.426283);
        assertThat(zone).isEqualTo("EPSG:32633");
    }

    @Test
    public void findUtmZone35() {
        // Mehamn: 71.035717, 27.848786
        String zone = GeometryTransformer.findUtmCrs(27.848786);
        assertThat(zone).isEqualTo("EPSG:32635");
    }
}