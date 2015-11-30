
package no.rutebanken.tiamat.nvdb.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "vegObjekter"
})
public class VegobjekterResultat {

    @JsonProperty("vegObjekter")
    private List<VegObjekt> vegObjekter = new ArrayList<VegObjekt>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The vegObjekter
     */
    @JsonProperty("vegObjekter")
    public List<VegObjekt> getVegObjekter() {
        return vegObjekter;
    }

    /**
     * 
     * @param vegObjekter
     *     The vegObjekter
     */
    @JsonProperty("vegObjekter")
    public void setVegObjekter(List<VegObjekt> vegObjekter) {
        this.vegObjekter = vegObjekter;
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
