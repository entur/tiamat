
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

package org.rutebanken.tiamat.nvdb.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "nummer",
    "navn",
    "periode"
})
public class Riksvegruter {

    @JsonProperty("nummer")
    private String nummer;
    @JsonProperty("navn")
    private String navn;
    @JsonProperty("periode")
    private String periode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The nummer
     */
    @JsonProperty("nummer")
    public String getNummer() {
        return nummer;
    }

    /**
     * 
     * @param nummer
     *     The nummer
     */
    @JsonProperty("nummer")
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    /**
     * 
     * @return
     *     The navn
     */
    @JsonProperty("navn")
    public String getNavn() {
        return navn;
    }

    /**
     * 
     * @param navn
     *     The navn
     */
    @JsonProperty("navn")
    public void setNavn(String navn) {
        this.navn = navn;
    }

    /**
     * 
     * @return
     *     The periode
     */
    @JsonProperty("periode")
    public String getPeriode() {
        return periode;
    }

    /**
     * 
     * @param periode
     *     The periode
     */
    @JsonProperty("periode")
    public void setPeriode(String periode) {
        this.periode = periode;
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
