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

package org.rutebanken.tiamat.service;

import org.junit.Test;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.NameTypeEnumeration;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


public class AlternativeNameUpdaterTest {

    AlternativeNameUpdater alternativeNameUpdater = new AlternativeNameUpdater();

    @Test
    public void allowMultipleAlternativenamesPerTypeAndValue() {

        StopPlace stopPlace = new StopPlace();

        AlternativeName alias1 = new AlternativeName();
        alias1.setName(new EmbeddableMultilingualString("Oslo lufthavn", "nb"));
        alias1.setNameType(NameTypeEnumeration.ALIAS);

        AlternativeName alias2 = new AlternativeName();
        alias2.setName(new EmbeddableMultilingualString("Gardermoen lufthavn", "nb"));
        alias2.setNameType(NameTypeEnumeration.ALIAS);

        boolean isUpdated = alternativeNameUpdater.updateAlternativeNames(stopPlace, Arrays.asList(alias1, alias2));
        assertThat(isUpdated).isTrue();
        assertThat(stopPlace.getAlternativeNames()).hasSize(2);
    }

    @Test
    public void keepExistingAlias() {

        StopPlace stopPlace = new StopPlace();

        AlternativeName existingAlias = new AlternativeName();
        existingAlias.setNetexId("NSR:AlternativeName:1");
        existingAlias.setName(new EmbeddableMultilingualString("Oslo lufthavn", "nb"));
        existingAlias.setNameType(NameTypeEnumeration.ALIAS);

        stopPlace.getAlternativeNames().add(existingAlias);

        AlternativeName incomingAlias = new AlternativeName();
        incomingAlias.setName(new EmbeddableMultilingualString("Oslo lufthavn", "nb"));
        incomingAlias.setNameType(NameTypeEnumeration.ALIAS);

        boolean isUpdated = alternativeNameUpdater.updateAlternativeNames(stopPlace, Arrays.asList(incomingAlias));
        assertThat(isUpdated).isFalse();
        assertThat(stopPlace.getAlternativeNames()).hasSize(1);
        assertThat(stopPlace.getAlternativeNames().iterator().next().getNetexId()).isNotNull();

    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAllowMultipleTranslationsPerLanguage() {

        StopPlace stopPlace = new StopPlace();

        AlternativeName translation1 = new AlternativeName();
        translation1.setName(new EmbeddableMultilingualString("Oslo lufthavn", "nb"));
        translation1.setNameType(NameTypeEnumeration.TRANSLATION);

        AlternativeName translation2 = new AlternativeName();
        translation2.setName(new EmbeddableMultilingualString("new value", "nb"));
        translation2.setNameType(NameTypeEnumeration.TRANSLATION);

        alternativeNameUpdater.updateAlternativeNames(stopPlace, Arrays.asList(translation1, translation2));

    }
}