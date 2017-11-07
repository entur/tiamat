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
import org.rutebanken.tiamat.rest.graphql.mappers.AlternativeNameMapper;

import static org.assertj.core.api.Assertions.assertThat;


public class AlternativeNameUpdaterTest {

    AlternativeNameUpdater alternativeNameUpdater = new AlternativeNameUpdater();

    @Test
    public void allowMultipleAlternativenamesPerTypeAndValue() {

        StopPlace stopPlace = new StopPlace();

        AlternativeName alias1 = new AlternativeName();
        alias1.setName(new EmbeddableMultilingualString("Oslo lufthavn"));
        alias1.setNameType(NameTypeEnumeration.ALIAS);

        stopPlace.getAlternativeNames().add(alias1);

        AlternativeName newAlternativeName = new AlternativeName();
        newAlternativeName.setName(new EmbeddableMultilingualString("Oslo lufthavn"));
        newAlternativeName.setNameType(NameTypeEnumeration.ALIAS);

        alternativeNameUpdater.updateAlternativeName(stopPlace, newAlternativeName);
        assertThat(stopPlace.getAlternativeNames()).hasSize(2);

        stopPlace.getAlternativeNames().forEach(alternativeName -> System.out.println(alternativeName));
    }


    @Test
    public void doNotAllowMultipleAlternativenamesPerTranslation() {

        StopPlace stopPlace = new StopPlace();

        AlternativeName alias1 = new AlternativeName();
        alias1.setName(new EmbeddableMultilingualString("Oslo lufthavn"));
        alias1.setNameType(NameTypeEnumeration.TRANSLATION);

        stopPlace.getAlternativeNames().add(alias1);

        AlternativeName newAlternativeName = new AlternativeName();
        newAlternativeName.setName(new EmbeddableMultilingualString("new value"));
        newAlternativeName.setNameType(NameTypeEnumeration.TRANSLATION);

        alternativeNameUpdater.updateAlternativeName(stopPlace, newAlternativeName);
        assertThat(stopPlace.getAlternativeNames()).hasSize(1);

        stopPlace.getAlternativeNames().forEach(alternativeName -> System.out.println(alternativeName));
    }

}