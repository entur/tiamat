package org.rutebanken.tiamat.rest.netex.publicationdelivery.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;

public class TypesEventFilter implements EventFilter {

    private static final Logger logger = LoggerFactory.getLogger(TypesEventFilter.class);

    private boolean deleteSection = false;
    private final List<String> ignoreTypes;

    public TypesEventFilter(String ...ignoreTypes) {
        this.ignoreTypes = new ArrayList<>(ignoreTypes.length);
        for(String ignoreType : ignoreTypes) {
            this.ignoreTypes.add(ignoreType);
        }
    }

    @Override
    public boolean accept(XMLEvent event) {
        if(event.isEndElement()) {
            EndElement endElement = event.asEndElement();
            if(doesMatch(endElement.getName().getLocalPart())) {
                deleteSection = false;
                logger.trace("NO ACCEPT. Release delete section: {}", event);
                return false;
            }
        }

        if(deleteSection) {
            logger.trace("NO ACCEPT. Deleting section: {}", event);
            return false;
        }

        if (event.isStartElement()) {
            StartElement startElement = event.asStartElement();
            String localPartOfName = startElement.getName().getLocalPart();

            if(doesMatch(localPartOfName)) {
                deleteSection = true;
                return false;
            }
        }

        logger.trace("Accept: {}", event);
        return true;
    }

    private boolean doesMatch(String localPartOfName) {
        return ignoreTypes.contains(localPartOfName);
    }
}
