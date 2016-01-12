
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
public class Kriterie {

    @JsonProperty("objektTyper")
    private List<ObjektType> objektType = new ArrayList<ObjektType>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Kriterie(List<ObjektType> objektType) {
        this.objektType = objektType;
    }

    public Kriterie() {
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
