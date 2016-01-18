package uk.org.netex.netex;

import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationStructureTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    /**
     * Because of serialization and deserialization of Location,
     * it must be possible to use setters and getters without knowing
     * about the underlying geometry object.
     */
    @Test
    public void testGeometrySettersAndGetters() {

        LocationStructure locationStructure = new LocationStructure();
        locationStructure.setGeometryFactory(geometryFactory);

        BigDecimal longitude = new BigDecimal("5.0");
        BigDecimal latitude = new BigDecimal("60.0");

        locationStructure.setLatitude(latitude);
        locationStructure.setLongitude(longitude);

        assertThat(locationStructure.getLatitude().toString()).isEqualTo("60.0");
        assertThat(locationStructure.getLongitude().toString()).isEqualTo("5.0");

    }

}