
package no.rutebanken.tiamat.nvdb.model.search;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "objektTyper"
})
public class SokeObjekt {

    @JsonProperty("objektTyper")
    private List<ObjektType> objektTyper = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The objektTyper
     */
    @JsonProperty("objektTyper")
    public List<ObjektType> getObjektTyper() {
        return objektTyper;
    }

    /**
     * 
     * @param objektTyper
     *     The objektTyper
     */
    @JsonProperty("objektTyper")
    public void setObjektTyper(List<ObjektType> objektTyper) {
        this.objektTyper = objektTyper;
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
