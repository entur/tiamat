package org.rutebanken.tiamat.autosys;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileOutputStream;
import java.io.IOException;

import org.entur.autosys.model.GetKjoretoyResponse;
import org.entur.autosys.service.AutosysVehicleService;
import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.autosys.MapperService;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryStreamingOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.xml.bind.JAXBException;

public class MapperServiceTest extends TiamatIntegrationTest {

    @Autowired
    private AutosysVehicleService autosysVehicleService;
    @Autowired
    private MapperService mapperService;

    @Test
    public void TestPrivateVehicleMapping() throws WebApplicationException, IOException, JAXBException, SAXException {
        GetKjoretoyResponse autosysData = autosysVehicleService.getVehicle("EV25288");

        assertNotNull(autosysData);

        var netexData = mapperService.exportPublicationDeliveryWithAutosysVehicle(new ExportParams(), autosysData.getKjoretoydataListe());

        assertNotNull(netexData);

        write2File(netexData, "target/ev25288.xml");

    }

    @Test
    public void TestBusVehicleMapping() throws WebApplicationException, IOException, JAXBException, SAXException {
        GetKjoretoyResponse autosysData = autosysVehicleService.getVehicle("EH84771");

        assertNotNull(autosysData);

        var netexData = mapperService.exportPublicationDeliveryWithAutosysVehicle(new ExportParams(), autosysData.getKjoretoydataListe());

        assertNotNull(netexData);

        write2File(netexData, "target/eh84771.xml");

    }

    private void write2File(PublicationDeliveryStructure netexData, String filePath) throws WebApplicationException, IOException, JAXBException, SAXException {
        PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput = new PublicationDeliveryStreamingOutput();
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            var output = publicationDeliveryStreamingOutput.stream(netexData);
            output.write(fileOutputStream);
        }
    }
}
