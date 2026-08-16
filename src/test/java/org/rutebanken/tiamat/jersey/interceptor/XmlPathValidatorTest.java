package org.rutebanken.tiamat.jersey.interceptor;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XmlPathValidatorTest {

    private static byte[] xml(String content) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void allowedLeafPasses() {
        byte[] xml = xml("<root><name>Oslo</name></root>");
        Set<String> allowed = Set.of("root/name");

        assertThatCode(() -> XmlPathValidator.validate(xml, allowed)).doesNotThrowAnyException();
    }

    @Test
    public void disallowedLeafThrows() {
        byte[] xml = xml("<root><secret>hidden</secret></root>");
        Set<String> allowed = Set.of("root/name");

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, allowed))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("root/secret");
    }

    /**
     * JAXB binds by namespace URI and local name, so prefixed NeTEx unmarshals exactly like the
     * default namespace form. Most NeTEx tooling, including Tiamat's own export, emits prefixed
     * elements, so validating the qualified name would reject payloads the unmarshaller accepts
     * and break an export, edit and resubmit round trip.
     */
    @Test
    public void namespacePrefixedElementsAreValidatedByLocalName() {
        byte[] xml = xml("<ns2:root xmlns:ns2=\"http://www.netex.org.uk/netex\"><ns2:name>Oslo</ns2:name></ns2:root>");
        Set<String> allowed = Set.of("root/name");

        assertThatCode(() -> XmlPathValidator.validate(xml, allowed)).doesNotThrowAnyException();
    }

    @Test
    public void disallowedLeafIsStillRejectedWhenNamespacePrefixed() {
        byte[] xml = xml("<ns2:root xmlns:ns2=\"http://www.netex.org.uk/netex\"><ns2:secret>hidden</ns2:secret></ns2:root>");
        Set<String> allowed = Set.of("root/name");

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, allowed))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("root/secret");
    }

    @Test
    public void defaultNamespaceElementsAreValidatedByLocalName() {
        byte[] xml = xml("<root xmlns=\"http://www.netex.org.uk/netex\"><name>Oslo</name></root>");
        Set<String> allowed = Set.of("root/name");

        assertThatCode(() -> XmlPathValidator.validate(xml, allowed)).doesNotThrowAnyException();
    }

    @Test
    public void allLeavesInDeepTreeMustBeAllowed() {
        byte[] xml = xml("<root><stop><name>X</name><type>bus</type></stop></root>");
        Set<String> allowed = Set.of("root/stop/name", "root/stop/type");

        assertThatCode(() -> XmlPathValidator.validate(xml, allowed)).doesNotThrowAnyException();
    }

    @Test
    public void singleDisallowedLeafInDeepTreeThrows() {
        byte[] xml = xml("<root><stop><name>X</name><secret>Y</secret></stop></root>");
        Set<String> allowed = Set.of("root/stop/name");

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, allowed))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("root/stop/secret");
    }

    @Test
    public void rootOnlyElementWithNoChildrenIsValidatedAsLeaf() {
        byte[] xml = xml("<root>value</root>");
        Set<String> allowed = Set.of("root");

        assertThatCode(() -> XmlPathValidator.validate(xml, allowed)).doesNotThrowAnyException();
    }

    @Test
    public void rootOnlyElementNotInAllowedThrows() {
        byte[] xml = xml("<root>value</root>");
        Set<String> allowed = Set.of("other");

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, allowed))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("root");
    }

    @Test
    public void emptyAllowedSetBlocksAllLeaves() {
        byte[] xml = xml("<root><name>Oslo</name></root>");
        Set<String> allowed = Set.of();

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, allowed))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void malformedXmlThrowsBadRequest() {
        byte[] xml = xml("<root><unclosed>");

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, Set.of("root")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Malformed XML");
    }

    @Test
    public void xxeDocTypeDeclIsRejected() {
        byte[] xml = xml("""
                <?xml version="1.0"?>
                <!DOCTYPE foo [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
                <root><name>&xxe;</name></root>
                """);

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, Set.of("root/name")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
