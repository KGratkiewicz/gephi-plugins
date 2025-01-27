package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSection {

    @JsonProperty("items")
    private List<DataItem> items;

    @JsonProperty("relationships")
    private List<DataRelationship> relationships;

    // Gettery i settery

    public List<DataItem> getItems() {
        return items;
    }

    public void setItems(List<DataItem> items) {
        this.items = items;
    }

    public List<DataRelationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<DataRelationship> relationships) {
        this.relationships = relationships;
    }
}
