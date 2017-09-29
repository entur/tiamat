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

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class VersionFrameRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends VersionFrameRefStructure>> versionFrameRef;

    public List<JAXBElement<? extends VersionFrameRefStructure>> getVersionFrameRef() {
        if (versionFrameRef == null) {
            versionFrameRef = new ArrayList<JAXBElement<? extends VersionFrameRefStructure>>();
        }
        return this.versionFrameRef;
    }

}
