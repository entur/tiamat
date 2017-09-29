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

public class PassengerAccessibilityNeedsStructure {

    protected Boolean accompaniedByCarer;
    protected UserNeeds userNeeds;
    protected Suitabilities suitabilities;

    public Boolean isAccompaniedByCarer() {
        return accompaniedByCarer;
    }

    public void setAccompaniedByCarer(Boolean value) {
        this.accompaniedByCarer = value;
    }

    public UserNeeds getUserNeeds() {
        return userNeeds;
    }

    public void setUserNeeds(UserNeeds value) {
        this.userNeeds = value;
    }

    public Suitabilities getSuitabilities() {
        return suitabilities;
    }

    public void setSuitabilities(Suitabilities value) {
        this.suitabilities = value;
    }

}
