package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/** Struktura 'properties' */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertiesDefinition {

    @JsonProperty("byId")
    private Map<String, Object> byId;

    @JsonProperty("sortOrder")
    private Map<String, Object> sortOrder;

    // Gettery i settery

    public Map<String, Object> getById() {
        return byId;
    }

    public void setById(Map<String, Object> byId) {
        this.byId = byId;
    }

    public Map<String, Object> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Map<String, Object> sortOrder) {
        this.sortOrder = sortOrder;
    }
}