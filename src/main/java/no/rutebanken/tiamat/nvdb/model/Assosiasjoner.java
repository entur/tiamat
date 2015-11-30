
package no.rutebanken.tiamat.nvdb.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "relasjon"
})
public class Assosiasjoner {

    @JsonProperty("relasjon")
    private Relasjon relasjon;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The relasjon
     */
    @JsonProperty("relasjon")
    public Relasjon getRelasjon() {
        return relasjon;
    }

    /**
     * 
     * @param relasjon
     *     The relasjon
     */
    @JsonProperty("relasjon")
    public void setRelasjon(Relasjon relasjon) {
        this.relasjon = relasjon;
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
