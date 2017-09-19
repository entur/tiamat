
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "sokeObjekt",
    "totaltAntallReturnert",
    "resultater"
})
public class SearchResult {

    @JsonProperty("sokeObjekt")
    private SokeObjekt sokeObjekt;
    @JsonProperty("totaltAntallReturnert")
    private Integer totaltAntallReturnert;
    @JsonProperty("resultater")
    private List<Resultater> resultater = new ArrayList<Resultater>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The sokeObjekt
     */
    @JsonProperty("sokeObjekt")
    public SokeObjekt getSokeObjekt() {
        return sokeObjekt;
    }

    /**
     * 
     * @param sokeObjekt
     *     The sokeObjekt
     */
    @JsonProperty("sokeObjekt")
    public void setSokeObjekt(SokeObjekt sokeObjekt) {
        this.sokeObjekt = sokeObjekt;
    }

    /**
     * 
     * @return
     *     The totaltAntallReturnert
     */
    @JsonProperty("totaltAntallReturnert")
    public Integer getTotaltAntallReturnert() {
        return totaltAntallReturnert;
    }

    /**
     * 
     * @param totaltAntallReturnert
     *     The totaltAntallReturnert
     */
    @JsonProperty("totaltAntallReturnert")
    public void setTotaltAntallReturnert(Integer totaltAntallReturnert) {
        this.totaltAntallReturnert = totaltAntallReturnert;
    }

    /**
     * 
     * @return
     *     The resultater
     */
    @JsonProperty("resultater")
    public List<Resultater> getResultater() {
        return resultater;
    }

    /**
     * 
     * @param resultater
     *     The resultater
     */
    @JsonProperty("resultater")
    public void setResultater(List<Resultater> resultater) {
        this.resultater = resultater;
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
