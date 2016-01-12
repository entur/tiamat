
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
    "typeId",
    "operator",
    "verdi"
})
public class Filter {

    @JsonProperty("typeId")
    private Integer typeId;
    @JsonProperty("operator")
    private String operator;
    @JsonProperty("verdi")
    private List<String> verdi = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Filter(Integer typeId, String operator, List<String> verdi) {
        this.typeId = typeId;
        this.operator = operator;
        this.verdi = verdi;
    }

    public Filter() {
    }

    /**
     * 
     * @return
     *     The typeId
     */
    @JsonProperty("typeId")
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * 
     * @param typeId
     *     The typeId
     */
    @JsonProperty("typeId")
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    /**
     * 
     * @return
     *     The operator
     */
    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    /**
     * 
     * @param operator
     *     The operator
     */
    @JsonProperty("operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * 
     * @return
     *     The verdi
     */
    @JsonProperty("verdi")
    public List<String> getVerdi() {
        return verdi;
    }

    /**
     * 
     * @param verdi
     *     The verdi
     */
    @JsonProperty("verdi")
    public void setVerdi(List<String> verdi) {
        this.verdi = verdi;
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
