package org.rutebanken.tiamat.ext.fintraffic.xml;

import javax.xml.stream.XMLStreamException;

public class NoNameSpaceXMLStreamWriter implements javax.xml.stream.XMLStreamWriter {
    private final javax.xml.stream.XMLStreamWriter writer;

    public NoNameSpaceXMLStreamWriter(javax.xml.stream.XMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeStartElement(String localName) throws javax.xml.stream.XMLStreamException {
        writer.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws javax.xml.stream.XMLStreamException {
        writer.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws javax.xml.stream.XMLStreamException {
        writer.writeStartElement(localName);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        writer.writeEmptyElement(localName);
    }

    @Override public void writeNamespace(String prefix, String namespaceURI) {}
    @Override public void writeDefaultNamespace(String namespaceURI) {}

    // Delegate all other methods
    @Override public void writeEndElement() throws javax.xml.stream.XMLStreamException { writer.writeEndElement(); }
    @Override public void writeEndDocument() throws javax.xml.stream.XMLStreamException { writer.writeEndDocument(); }
    @Override public void close() throws javax.xml.stream.XMLStreamException { writer.close(); }
    @Override public void flush() throws javax.xml.stream.XMLStreamException { writer.flush(); }
    @Override public void writeAttribute(String localName, String value) throws javax.xml.stream.XMLStreamException { writer.writeAttribute(localName, value); }
    @Override public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws javax.xml.stream.XMLStreamException { writer.writeAttribute(localName, value); }
    @Override public void writeAttribute(String namespaceURI, String localName, String value) throws javax.xml.stream.XMLStreamException { writer.writeAttribute(localName, value); }
    @Override public void writeCData(String data) throws javax.xml.stream.XMLStreamException { writer.writeCData(data); }
    @Override public void writeCharacters(String text) throws javax.xml.stream.XMLStreamException { writer.writeCharacters(text); }
    @Override public void writeCharacters(char[] text, int start, int len) throws javax.xml.stream.XMLStreamException { writer.writeCharacters(text, start, len); }
    @Override public void writeComment(String data) throws javax.xml.stream.XMLStreamException { writer.writeComment(data); }
    @Override public void writeDTD(String dtd) throws javax.xml.stream.XMLStreamException { writer.writeDTD(dtd); }
    @Override public void writeEntityRef(String name) throws javax.xml.stream.XMLStreamException { writer.writeEntityRef(name); }
    @Override public void writeStartDocument() {}
    @Override public void writeStartDocument(String version) {}
    @Override public void writeStartDocument(String encoding, String version) {}
    @Override public void writeProcessingInstruction(String target) throws javax.xml.stream.XMLStreamException { writer.writeProcessingInstruction(target); }
    @Override public void writeProcessingInstruction(String target, String data) throws javax.xml.stream.XMLStreamException { writer.writeProcessingInstruction(target, data); }
    @Override public Object getProperty(String name) throws IllegalArgumentException { return writer.getProperty(name); }
    @Override public void setDefaultNamespace(String uri) throws javax.xml.stream.XMLStreamException { writer.setDefaultNamespace(uri); }
    @Override public void setNamespaceContext(javax.xml.namespace.NamespaceContext context) throws javax.xml.stream.XMLStreamException { writer.setNamespaceContext(context); }
    @Override public javax.xml.namespace.NamespaceContext getNamespaceContext() { return writer.getNamespaceContext(); }
    @Override public String getPrefix(String uri) throws javax.xml.stream.XMLStreamException { return writer.getPrefix(uri); }
    @Override public void setPrefix(String prefix, String uri) throws javax.xml.stream.XMLStreamException { writer.setPrefix(prefix, uri); }
}
