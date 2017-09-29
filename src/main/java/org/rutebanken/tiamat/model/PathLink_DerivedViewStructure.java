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

public class PathLink_DerivedViewStructure
        extends DerivedViewStructure {

    protected Boolean hideLink;
    protected Boolean hideDestination;
    protected Boolean showEntranceSeparately;
    protected Boolean showExitSeparately;
    protected Boolean showHeadingSeparately;

    public Boolean isHideLink() {
        return hideLink;
    }

    public void setHideLink(Boolean value) {
        this.hideLink = value;
    }

    public Boolean isHideDestination() {
        return hideDestination;
    }

    public void setHideDestination(Boolean value) {
        this.hideDestination = value;
    }

    public Boolean isShowEntranceSeparately() {
        return showEntranceSeparately;
    }

    public void setShowEntranceSeparately(Boolean value) {
        this.showEntranceSeparately = value;
    }

    public Boolean isShowExitSeparately() {
        return showExitSeparately;
    }

    public void setShowExitSeparately(Boolean value) {
        this.showExitSeparately = value;
    }

    public Boolean isShowHeadingSeparately() {
        return showHeadingSeparately;
    }

    public void setShowHeadingSeparately(Boolean value) {
        this.showHeadingSeparately = value;
    }

}
