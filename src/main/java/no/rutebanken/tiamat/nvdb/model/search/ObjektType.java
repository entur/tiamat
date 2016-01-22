
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
    "id",
    "antall",
    "filter"
})
public class ObjektType {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("antall")
    private Integer antall;
    @JsonProperty("filter")
    private List<Filter> filter = new ArrayList<Filter>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ObjektType(Integer id, Integer antall, List<Filter> filter) {
        this.id = id;
        this.antall = antall;
        this.filter = filter;
    }

    public ObjektType() {}

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The antall
     */
    @JsonProperty("antall")
    public Integer getAntall() {
        return antall;
    }

    /**
     * 
     * @param antall
     *     The antall
     */
    @JsonProperty("antall")
    public void setAntall(Integer antall) {
        this.antall = antall;
    }

    /**
     * 
     * @return
     *     The filter
     */
    @JsonProperty("filter")
    public List<Filter> getFilter() {
        return filter;
    }

    /**
     * 
     * @param filter
     *     The filter
     */
    @JsonProperty("filter")
    public void setFilter(List<Filter> filter) {
        this.filter = filter;
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
