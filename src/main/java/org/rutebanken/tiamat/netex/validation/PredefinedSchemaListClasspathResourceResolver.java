package org.rutebanken.tiamat.netex.validation;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PredefinedSchemaListClasspathResourceResolver implements LSResourceResolver {

    private Map<String, String> resourceToPathMap = new HashMap<>();

    public PredefinedSchemaListClasspathResourceResolver(String schemaList) throws IOException {

        InputStream resourceAsStream = getClass().getResourceAsStream(schemaList);
        BufferedReader r = new BufferedReader(new InputStreamReader(resourceAsStream));
        String resource;
        while ((resource = r.readLine()) != null) {
            String name = resource.substring(resource.lastIndexOf('/') + 1);
            String existing = resourceToPathMap.put(name, resource);
            if (existing != null) {
                throw new RuntimeException("Duplicate resource file on classpath " + name);
            }
        }
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        try {

            String[] parts = StringUtils.split(systemId, "/");
            String filename = parts[parts.length - 1];

            LSInput lsInput = createInput(systemId, baseURI);
            String path = resourceToPathMap.get(filename);
            if (path != null) {
                InputStream stream = getClass().getResourceAsStream(path);
                lsInput.setByteStream(stream);
            }
            return lsInput;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected LSInput createInput(final String systemId, final String baseUri) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return new LSInput() {

            private InputStream is;

            @Override
            public void setSystemId(String systemId) {
            }

            @Override
            public void setStringData(String stringData) {
            }

            @Override
            public void setPublicId(String publicId) {
            }

            @Override
            public void setEncoding(String encoding) {
            }

            @Override
            public void setCharacterStream(Reader characterStream) {
            }

            @Override
            public void setCertifiedText(boolean certifiedText) {
            }

            @Override
            public void setByteStream(InputStream byteStream) {
                is = byteStream;
            }

            @Override
            public void setBaseURI(String baseURI) {
            }

            @Override
            public String getSystemId() {
                return systemId;
            }

            @Override
            public String getStringData() {
                return null;
            }

            @Override
            public String getPublicId() {
                return null;
            }

            @Override
            public String getEncoding() {
                return null;
            }

            @Override
            public Reader getCharacterStream() {
                return null;
            }

            @Override
            public boolean getCertifiedText() {
                return false;
            }

            @Override
            public InputStream getByteStream() {
                return is;
            }

            @Override
            public String getBaseURI() {
                return baseUri;
            }
        };

    }
}
