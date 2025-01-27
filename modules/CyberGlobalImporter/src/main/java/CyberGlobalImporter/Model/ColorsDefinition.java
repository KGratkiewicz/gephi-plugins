package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ColorsDefinition {

    @JsonProperty("byId")
    private Map<String, ColorDetails> byId;

    @JsonProperty("sortOrder")
    private Map<String, Integer> sortOrder;

    public Map<String, ColorDetails> getById() {
        return byId;
    }

    public void setById(Map<String, ColorDetails> byId) {
        this.byId = byId;
    }

    public Map<String, Integer> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Map<String, Integer> sortOrder) {
        this.sortOrder = sortOrder;
    }
}