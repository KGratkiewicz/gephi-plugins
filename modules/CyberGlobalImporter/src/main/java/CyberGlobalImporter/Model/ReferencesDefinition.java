package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferencesDefinition {
    @JsonProperty("byId")
    private Map<String, ReferenceDetails> byId;

    @JsonProperty("sortOrder")
    private Map<String, Integer> sortOrder;

    // Gettery i settery

    public Map<String, ReferenceDetails> getById() {
        return byId;
    }

    public void setById(Map<String, ReferenceDetails> byId) {
        this.byId = byId;
    }

    public Map<String, Integer> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Map<String, Integer> sortOrder) {
        this.sortOrder = sortOrder;
    }
}