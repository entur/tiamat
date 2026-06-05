package org.rutebanken.tiamat.jersey.interceptor;

import jakarta.ws.rs.BadRequestException;
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
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("root/secret");
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
                .isInstanceOf(BadRequestException.class)
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
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("root");
    }

    @Test
    public void emptyAllowedSetBlocksAllLeaves() {
        byte[] xml = xml("<root><name>Oslo</name></root>");
        Set<String> allowed = Set.of();

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, allowed))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void malformedXmlThrowsBadRequest() {
        byte[] xml = xml("<root><unclosed>");

        assertThatThrownBy(() -> XmlPathValidator.validate(xml, Set.of("root")))
                .isInstanceOf(BadRequestException.class)
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
                .isInstanceOf(BadRequestException.class);
    }
}
