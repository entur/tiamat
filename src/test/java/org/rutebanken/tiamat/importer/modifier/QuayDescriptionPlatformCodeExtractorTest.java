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

package org.rutebanken.tiamat.importer.modifier;

import org.junit.Test;
import org.rutebanken.tiamat.importer.modifier.QuayDescriptionPlatformCodeExtractor;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuayDescriptionPlatformCodeExtractorTest {

    private static final QuayDescriptionPlatformCodeExtractor extractor = new QuayDescriptionPlatformCodeExtractor();

    @Test
    public void plattform25() {
        Quay quay = quayWithDescription("Plattform 25");
        assertThat(quay.getPublicCode()).isEqualTo("25");
    }

    @Test
    public void plattform19b() {
        Quay quay = quayWithDescription("Plattform 19b");
        assertThat(quay.getPublicCode()).isEqualTo("19b");
    }

    @Test
    public void emptyDescriptionIfNothingLeft() {
        Quay quay = quayWithDescription("Plattform 19b");
        assertThat(quay.getDescription()).isNull();
    }

    @Test
    public void plfAWithDescriptikon() {
        Quay quay = quayWithDescription("Plf. A  ved apoteket");
        assertThat(quay.getPublicCode()).isEqualTo("A");
        assertThat(quay.getDescription().getValue()).isEqualTo("ved apoteket");
    }

    @Test
    public void plfNWithDashAndDescriptikon() {
        Quay quay = quayWithDescription("Plf. N - mot øst");
        assertThat(quay.getPublicCode()).isEqualTo("N");
        assertThat(quay.getDescription().getValue()).isEqualTo("mot øst");
    }
    @Test
    public void gateterminalenPlf4() {
        Quay quay = quayWithDescription("gateterminalen plf. 4");
        assertThat(quay.getPublicCode()).isEqualTo("4");
        assertThat(quay.getDescription().getValue()).isEqualTo("gateterminalen");
    }

    @Test
    public void ignoreIfDescriptionIsNull() {
        Quay quay = new Quay();
        extractor.extractPlatformCode(quay);
    }

    @Test
    public void plfG() {
        Quay quay = quayWithDescription("Plattform G");
        assertThat(quay.getPublicCode()).isEqualTo("G");
    }

    private Quay quayWithDescription(String description) {
        Quay quay = new Quay();
        quay.setDescription(new EmbeddableMultilingualString(description));
        extractor.extractPlatformCode(quay);
        return quay;
    }

}