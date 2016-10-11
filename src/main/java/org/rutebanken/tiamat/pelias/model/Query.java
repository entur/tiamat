
package org.rutebanken.tiamat.pelias.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "size",
    "private",
    "point.lat",
    "point.lon",
    "boundary.circle.radius",
    "boundary.circle.lat",
    "boundary.circle.lon",
    "querySize"
})
public class Query {

    @JsonProperty("size")
    private int size;
    @JsonProperty("private")
    private boolean _private;
    @JsonProperty("point.lat")
    private double pointLat;
    @JsonProperty("point.lon")
    private double pointLon;
    @JsonProperty("boundary.circle.radius")
    private int boundaryCircleRadius;
    @JsonProperty("boundary.circle.lat")
    private double boundaryCircleLat;
    @JsonProperty("boundary.circle.lon")
    private double boundaryCircleLon;
    @JsonProperty("querySize")
    private int querySize;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * 
     * @return
     *     The size
     */
    @JsonProperty("size")
    public int getSize() {
        return size;
    }

    /**
     * 
     * @param size
     *     The size
     */
    @JsonProperty("size")
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 
     * @return
     *     The _private
     */
    @JsonProperty("private")
    public boolean isPrivate() {
        return _private;
    }

    /**
     * 
     * @param _private
     *     The private
     */
    @JsonProperty("private")
    public void setPrivate(boolean _private) {
        this._private = _private;
    }

    /**
     * 
     * @return
     *     The pointLat
     */
    @JsonProperty("point.lat")
    public double getPointLat() {
        return pointLat;
    }

    /**
     * 
     * @param pointLat
     *     The point.lat
     */
    @JsonProperty("point.lat")
    public void setPointLat(double pointLat) {
        this.pointLat = pointLat;
    }

    /**
     * 
     * @return
     *     The pointLon
     */
    @JsonProperty("point.lon")
    public double getPointLon() {
        return pointLon;
    }

    /**
     * 
     * @param pointLon
     *     The point.lon
     */
    @JsonProperty("point.lon")
    public void setPointLon(double pointLon) {
        this.pointLon = pointLon;
    }

    /**
     * 
     * @return
     *     The boundaryCircleRadius
     */
    @JsonProperty("boundary.circle.radius")
    public int getBoundaryCircleRadius() {
        return boundaryCircleRadius;
    }

    /**
     * 
     * @param boundaryCircleRadius
     *     The boundary.circle.radius
     */
    @JsonProperty("boundary.circle.radius")
    public void setBoundaryCircleRadius(int boundaryCircleRadius) {
        this.boundaryCircleRadius = boundaryCircleRadius;
    }

    /**
     * 
     * @return
     *     The boundaryCircleLat
     */
    @JsonProperty("boundary.circle.lat")
    public double getBoundaryCircleLat() {
        return boundaryCircleLat;
    }

    /**
     * 
     * @param boundaryCircleLat
     *     The boundary.circle.lat
     */
    @JsonProperty("boundary.circle.lat")
    public void setBoundaryCircleLat(double boundaryCircleLat) {
        this.boundaryCircleLat = boundaryCircleLat;
    }

    /**
     * 
     * @return
     *     The boundaryCircleLon
     */
    @JsonProperty("boundary.circle.lon")
    public double getBoundaryCircleLon() {
        return boundaryCircleLon;
    }

    /**
     * 
     * @param boundaryCircleLon
     *     The boundary.circle.lon
     */
    @JsonProperty("boundary.circle.lon")
    public void setBoundaryCircleLon(double boundaryCircleLon) {
        this.boundaryCircleLon = boundaryCircleLon;
    }

    /**
     * 
     * @return
     *     The querySize
     */
    @JsonProperty("querySize")
    public int getQuerySize() {
        return querySize;
    }

    /**
     * 
     * @param querySize
     *     The querySize
     */
    @JsonProperty("querySize")
    public void setQuerySize(int querySize) {
        this.querySize = querySize;
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
