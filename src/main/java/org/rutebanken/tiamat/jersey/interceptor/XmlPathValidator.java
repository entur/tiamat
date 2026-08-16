package org.rutebanken.tiamat.jersey.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for validating XML documents against a predefined set of allowed paths.
 */

public class XmlPathValidator {
    private static final Logger logger = LoggerFactory.getLogger(XmlPathValidator.class);

    public static void validate(byte[] xml, Set<String> allowedPaths) {
        Document doc = parse(xml);
        Element root = doc.getDocumentElement();
        validateElement(root, localName(root), allowedPaths);
    }

    /**
     * JAXB binds by namespace URI and local name, so the whitelist is matched on the local name.
     * Matching the qualified name would reject prefixed NeTEx, which the unmarshaller accepts and
     * which most NeTEx tooling emits.
     */
    private static String localName(Element element) {
        return element.getLocalName() != null ? element.getLocalName() : element.getTagName();
    }

    private static void validateElement(Element element, String path, Set<String> allowedPaths) {
        List<Element> childElements = childElements(element);

        if (childElements.isEmpty()) {
            validateLeafElement(path, allowedPaths);
        } else {

            for (Element child : childElements) {
                validateElement(child, path + "/" + localName(child), allowedPaths);
            }
        }
    }

    private static void validateLeafElement(String path, Set<String> allowedPaths) {
        if (!allowedPaths.contains(path)) {
            logger.warn("XML field not allowed: " + path);
            throw new IllegalArgumentException("XML field not allowed: " + path);
        }
    }

    private static List<Element> childElements(Element element) {
        List<Element> result = new ArrayList<>();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                result.add((Element) children.item(i));
            }
        }
        return result;
    }

    private static Document parse(byte[] xml) {
        try {
            DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // XXE protection
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        } catch (Exception e) {
            logger.warn("Receieved malformed XML");
            throw new IllegalArgumentException("Malformed XML: " + e.getMessage());
        }
    }
}