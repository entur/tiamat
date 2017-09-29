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

public class StairEndStructure {

    protected Boolean continuingHandrail;
    protected Boolean texturedSurface;
    protected Boolean visualContrast;

    public Boolean isContinuingHandrail() {
        return continuingHandrail;
    }

    public void setContinuingHandrail(Boolean value) {
        this.continuingHandrail = value;
    }

    public Boolean isTexturedSurface() {
        return texturedSurface;
    }

    public void setTexturedSurface(Boolean value) {
        this.texturedSurface = value;
    }

    public Boolean isVisualContrast() {
        return visualContrast;
    }

    public void setVisualContrast(Boolean value) {
        this.visualContrast = value;
    }

}
