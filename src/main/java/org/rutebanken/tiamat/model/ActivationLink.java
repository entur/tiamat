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

public class ActivationLink extends Link {

    protected TypeOfActivationRefStructure typeOfActivationRef;
    protected ActivationPointRefStructure fromPointRef;
    protected ActivationPointRefStructure toPointRef;

    public TypeOfActivationRefStructure getTypeOfActivationRef() {
        return typeOfActivationRef;
    }

    public void setTypeOfActivationRef(TypeOfActivationRefStructure value) {
        this.typeOfActivationRef = value;
    }

    public ActivationPointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(ActivationPointRefStructure value) {
        this.fromPointRef = value;
    }

    public ActivationPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(ActivationPointRefStructure value) {
        this.toPointRef = value;
    }

}
