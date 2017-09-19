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

package org.rutebanken.tiamat.model.tag;

import java.io.Serializable;

public class TagPK implements Serializable {

    private String idReference;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagPK tagPK = (TagPK) o;

        if (idReference != null ? !idReference.equals(tagPK.idReference) : tagPK.idReference != null) return false;
        if (name != null ? !name.equals(tagPK.name) : tagPK.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idReference != null ? idReference.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
