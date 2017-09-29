
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

package org.rutebanken.tiamat.nvdb.model.search;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "lokasjon",
    "objektTyper"
})
public class Kriterie {

    @JsonProperty("objektTyper")
    private List<ObjektType> objektType = new ArrayList<ObjektType>();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Kriterie(SokeLokasjon lokasjon, List<ObjektType> objektType) {
        this.lokasjon = lokasjon;
        this.objektType = objektType;
    }

    private SokeLokasjon lokasjon;

    public Kriterie(List<ObjektType> objektType) {
        this.objektType = objektType;
    }

    public Kriterie() {
    }

    @JsonProperty("lokasjon")
    public SokeLokasjon getLokasjon() {
        return lokasjon;
    }

    @JsonProperty("lokasjon")
    public void setLokasjon(SokeLokasjon lokasjon) {
        this.lokasjon = lokasjon;
    }

        /**
         *
         * @return
         *     The objektType
         */
    @JsonProperty("objektTyper")
    public List<ObjektType> getObjektType() {
        return objektType;
    }

    /**
     * 
     * @param objektType
     *     The objektType
     */
    @JsonProperty("objektTyper")
    public void setObjektType(List<ObjektType> objektType) {
        this.objektType = objektType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
