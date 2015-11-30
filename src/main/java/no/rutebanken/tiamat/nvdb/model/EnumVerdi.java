
package no.rutebanken.tiamat.nvdb.model;

import java.util.HashMap;
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
    "id",
    "kortVerdi",
    "verdi",
    "sorteringsnummer"
})
public class EnumVerdi {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("kortVerdi")
    private String kortVerdi;
    @JsonProperty("verdi")
    private String verdi;
    @JsonProperty("sorteringsnummer")
    private Integer sorteringsnummer;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The kortVerdi
     */
    @JsonProperty("kortVerdi")
    public String getKortVerdi() {
        return kortVerdi;
    }

    /**
     * 
     * @param kortVerdi
     *     The kortVerdi
     */
    @JsonProperty("kortVerdi")
    public void setKortVerdi(String kortVerdi) {
        this.kortVerdi = kortVerdi;
    }

    /**
     * 
     * @return
     *     The verdi
     */
    @JsonProperty("verdi")
    public String getVerdi() {
        return verdi;
    }

    /**
     * 
     * @param verdi
     *     The verdi
     */
    @JsonProperty("verdi")
    public void setVerdi(String verdi) {
        this.verdi = verdi;
    }

    /**
     * 
     * @return
     *     The sorteringsnummer
     */
    @JsonProperty("sorteringsnummer")
    public Integer getSorteringsnummer() {
        return sorteringsnummer;
    }

    /**
     * 
     * @param sorteringsnummer
     *     The sorteringsnummer
     */
    @JsonProperty("sorteringsnummer")
    public void setSorteringsnummer(Integer sorteringsnummer) {
        this.sorteringsnummer = sorteringsnummer;
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
