package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataItem {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("label")
    private String label;

    @JsonProperty("start")
    private String start;

    @JsonProperty("latestStart")
    private String latestStart;

    @JsonProperty("earliestEnd")
    private String earliestEnd;

    @JsonProperty("end")
    private String end;

    @JsonProperty("ongoing")
    private Boolean ongoing;

    @JsonProperty("super")
    private String superItem;

    @JsonProperty("propertyValues")
    private Map<String, String> propertyValues;

    @JsonProperty("locationUri")
    private String locationUri;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        if(type == null)
            return "";
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        if(label == null)
            return "";
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStart() {
        if(start == null)
            return "";
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLatestStart() {
        if(latestStart == null)
            return "";
        return latestStart;
    }

    public void setLatestStart(String latestStart) {
        this.latestStart = latestStart;
    }

    public String getEarliestEnd() {
        if(earliestEnd == null)
            return "";
        return earliestEnd;
    }

    public void setEarliestEnd(String earliestEnd) {
        this.earliestEnd = earliestEnd;
    }

    public String getEnd() {
        if(end == null)
            return "";
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(Boolean ongoing) {
        this.ongoing = ongoing;
    }

    public String getSuperItem() {
        if(superItem == null)
            return "";
        return superItem;
    }

    public void setSuperItem(String superItem) {
        this.superItem = superItem;
    }

    public Map<String, String> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public String getLocationUri() {
        if(locationUri == null)
            return "";
        return locationUri;
    }

    public void setLocationUri(String locationUri) {
        this.locationUri = locationUri;
    }
}
