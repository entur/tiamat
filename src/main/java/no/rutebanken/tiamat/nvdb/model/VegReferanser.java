
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
    "fylke",
    "kommune",
    "kategori",
    "status",
    "nummer",
    "hp",
    "fraMeter"
})
public class VegReferanser {

    @JsonProperty("fylke")
    private Integer fylke;
    @JsonProperty("kommune")
    private Integer kommune;
    @JsonProperty("kategori")
    private String kategori;
    @JsonProperty("status")
    private String status;
    @JsonProperty("nummer")
    private Integer nummer;
    @JsonProperty("hp")
    private Integer hp;
    @JsonProperty("fraMeter")
    private Integer fraMeter;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The fylke
     */
    @JsonProperty("fylke")
    public Integer getFylke() {
        return fylke;
    }

    /**
     * 
     * @param fylke
     *     The fylke
     */
    @JsonProperty("fylke")
    public void setFylke(Integer fylke) {
        this.fylke = fylke;
    }

    /**
     * 
     * @return
     *     The kommune
     */
    @JsonProperty("kommune")
    public Integer getKommune() {
        return kommune;
    }

    /**
     * 
     * @param kommune
     *     The kommune
     */
    @JsonProperty("kommune")
    public void setKommune(Integer kommune) {
        this.kommune = kommune;
    }

    /**
     * 
     * @return
     *     The kategori
     */
    @JsonProperty("kategori")
    public String getKategori() {
        return kategori;
    }

    /**
     * 
     * @param kategori
     *     The kategori
     */
    @JsonProperty("kategori")
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The nummer
     */
    @JsonProperty("nummer")
    public Integer getNummer() {
        return nummer;
    }

    /**
     * 
     * @param nummer
     *     The nummer
     */
    @JsonProperty("nummer")
    public void setNummer(Integer nummer) {
        this.nummer = nummer;
    }

    /**
     * 
     * @return
     *     The hp
     */
    @JsonProperty("hp")
    public Integer getHp() {
        return hp;
    }

    /**
     * 
     * @param hp
     *     The hp
     */
    @JsonProperty("hp")
    public void setHp(Integer hp) {
        this.hp = hp;
    }

    /**
     * 
     * @return
     *     The fraMeter
     */
    @JsonProperty("fraMeter")
    public Integer getFraMeter() {
        return fraMeter;
    }

    /**
     * 
     * @param fraMeter
     *     The fraMeter
     */
    @JsonProperty("fraMeter")
    public void setFraMeter(Integer fraMeter) {
        this.fraMeter = fraMeter;
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
