package org.rutebanken.tiamat.exporter;


import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

import static org.rutebanken.tiamat.exporter.StreamingPublicationDelivery.LINE_SEPARATOR;

@Service
public class IterableMarshaller {

    private static final Logger logger = LoggerFactory.getLogger(IterableMarshaller.class);

    private final NetexMapper netexMapper;

    @Autowired
    public IterableMarshaller(NetexMapper netexMapper) {
        this.netexMapper = netexMapper;
    }

    public <TIAMAT extends IdentifiedEntity, NETEX extends EntityStructure> void marshal(Iterator<TIAMAT> iterator,
                                                                                         BufferedWriter bufferedWriter,
                                                                                         Marshaller marshaller,
                                                                                         Class<NETEX> netexClass,
                                                                                         String nodeName,
                                                                                         Function<NETEX, JAXBElement<NETEX>> createJaxBFunction)
            throws IOException, JAXBException {

        logger.info("Marshalling {}", nodeName);

        int count = 0;

        long startTime = System.currentTimeMillis();

        while (iterator.hasNext()) {
            TIAMAT tiamatEntity = iterator.next();

            if (count == 0) {
                bufferedWriter.write("<"+nodeName+">");
                bufferedWriter.write(LINE_SEPARATOR);
            }

            ++count;

            if (count % 1000 == 0 && logger.isInfoEnabled()) {
                String entityPerSecond = "NA";

                long duration = System.currentTimeMillis() - startTime;
                if (duration >= 1000) {
                    entityPerSecond = String.valueOf(count / (duration / 1000f));
                }
                logger.info("{} marshalled: {}. {} per second: {}", nodeName, nodeName, count, entityPerSecond);
            } else {
                logger.debug("Marshalling {} {}: {}", nodeName,  count, tiamatEntity);
            }

            NETEX netexEntity = netexMapper.getFacade().map(tiamatEntity, netexClass);
            JAXBElement<NETEX> jaxBElement = createJaxBFunction.apply(netexEntity);
            marshaller.marshal(jaxBElement, bufferedWriter);
            bufferedWriter.write(LINE_SEPARATOR);
        }
        if (count > 0) {
            bufferedWriter.write("</"+nodeName+">");
            bufferedWriter.write(LINE_SEPARATOR);
        }

    }

}
