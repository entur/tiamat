/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.netex.mapping;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.TextType;

import java.io.Serializable;

/**
 * Since netex-java-model 3.0.0, {@link MultilingualString} no longer holds a single value,
 * but a list of {@link TextType} elements to support multiple language variants of the same
 * text. Tiamat's own model only ever stores a single value/lang pair, so on read we pick the
 * first {@code Text} element, and on write we produce a {@link MultilingualString} with exactly
 * one {@code Text} element.
 */
public final class NetexMultilingualStringHelper {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private NetexMultilingualStringHelper() {
    }

    public static MultilingualString toNetexModel(String value) {
        return toNetexModel(value, null);
    }

    public static MultilingualString toNetexModel(String value, String lang) {
        if (value == null) {
            return null;
        }

        TextType text = new TextType().withValue(value);
        if (lang != null) {
            text.setLang(lang);
        }

        MultilingualString multilingualString = new MultilingualString().withContent(OBJECT_FACTORY.createMultilingualStringText(text));
        if (lang != null) {
            multilingualString.setLang(lang);
        }
        return multilingualString;
    }

    public static String getValue(MultilingualString multilingualString) {
        TextType text = firstText(multilingualString);
        if (text != null) {
            return text.getValue();
        }
        // Some producers (and hand-authored XML) put the text directly as the MultilingualString's
        // own content, without a nested <Text> element - the old, pre-3.0.0 shape. @XmlMixed still
        // accepts that as a plain String entry in the content list.
        return firstNonBlankString(multilingualString);
    }

    public static String getLang(MultilingualString multilingualString) {
        if (multilingualString == null) {
            return null;
        }
        TextType text = firstText(multilingualString);
        if (text != null && text.getLang() != null) {
            return text.getLang();
        }
        return multilingualString.getLang();
    }

    private static TextType firstText(MultilingualString multilingualString) {
        if (multilingualString == null || multilingualString.getContent() == null) {
            return null;
        }
        for (Serializable item : multilingualString.getContent()) {
            if (item instanceof JAXBElement<?> jaxbElement && jaxbElement.getValue() instanceof TextType textType) {
                return textType;
            }
        }
        return null;
    }

    private static String firstNonBlankString(MultilingualString multilingualString) {
        if (multilingualString.getContent() == null) {
            return null;
        }
        for (Serializable item : multilingualString.getContent()) {
            if (item instanceof String str && !str.isBlank()) {
                return str;
            }
        }
        return null;
    }
}
