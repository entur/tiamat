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

import org.entur.netex2.model.MultilingualString;
import org.entur.netex2.model.ObjectFactory;
import org.entur.netex2.model.TextType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NetexMultilingualStringHelperTest {

    @Test
    public void writesValueAsInlineText() {
        MultilingualString multilingualString = NetexMultilingualStringHelper.toNetexModel("Oslo S", "nor");

        assertThat(multilingualString.getContent()).containsExactly("Oslo S");
        assertThat(multilingualString.getLang()).isEqualTo("nor");
    }

    @Test
    public void writesNullForNullValue() {
        assertThat(NetexMultilingualStringHelper.toNetexModel(null, "nor")).isNull();
    }

    @Test
    public void readsInlineText() {
        MultilingualString multilingualString = new MultilingualString().withContent("Oslo S").withLang("nor");

        assertThat(NetexMultilingualStringHelper.getValue(multilingualString)).isEqualTo("Oslo S");
        assertThat(NetexMultilingualStringHelper.getLang(multilingualString)).isEqualTo("nor");
    }

    @Test
    public void readsTextElement() {
        TextType text = new TextType().withValue("Oslo S").withLang("nor");
        MultilingualString multilingualString = new MultilingualString()
                .withContent(new ObjectFactory().createMultilingualStringText(text));

        assertThat(NetexMultilingualStringHelper.getValue(multilingualString)).isEqualTo("Oslo S");
        assertThat(NetexMultilingualStringHelper.getLang(multilingualString)).isEqualTo("nor");
    }

    @Test
    public void roundTripsValueAndLang() {
        MultilingualString multilingualString = NetexMultilingualStringHelper.toNetexModel("Oslo S", "nor");

        assertThat(NetexMultilingualStringHelper.getValue(multilingualString)).isEqualTo("Oslo S");
        assertThat(NetexMultilingualStringHelper.getLang(multilingualString)).isEqualTo("nor");
    }
}
