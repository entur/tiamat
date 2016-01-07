package no.rutebanken.tiamat.pelias;

import no.rutebanken.tiamat.pelias.model.ReverseLookupResult;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PeliasReverseLookupServiceTest {

    @Test
    public void testReverseLookup() throws Exception {

        PeliasReverseLookupClient peliasReverseLookupClient = new PeliasReverseLookupClient("http://localhost:3000/v1/reverse");

        ReverseLookupResult reverseLookupResult = peliasReverseLookupClient.reverseLookup("60.440860", "5.300058", 1);

        assertThat(reverseLookupResult.getFeatures()).isNotEmpty();

        String region = reverseLookupResult.getFeatures().get(0).getProperties().getRegion();

        String locality = reverseLookupResult.getFeatures().get(0).getProperties().getLocality();

        System.out.println("Got result with region: "+region + " and locality: "+locality);
    }
}