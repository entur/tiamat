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

public abstract class Layer_VersionStructure
        extends GroupOfEntities_VersionStructure {

    protected String locationSystem;
    protected VersionFrameRefs_RelStructure versionFrames;
    protected ObjectRefs_RelStructure members;

    public String getLocationSystem() {
        return locationSystem;
    }

    public void setLocationSystem(String value) {
        this.locationSystem = value;
    }

    public VersionFrameRefs_RelStructure getVersionFrames() {
        return versionFrames;
    }

    public void setVersionFrames(VersionFrameRefs_RelStructure value) {
        this.versionFrames = value;
    }

    public ObjectRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(ObjectRefs_RelStructure value) {
        this.members = value;
    }

}
