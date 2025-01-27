package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionType {

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    @JsonProperty("pluralLabel")
    private String pluralLabel;

    @JsonProperty("hasDates")
    private boolean hasDates;

    @JsonProperty("superTypes")
    private List<String> superTypes;

    @JsonProperty("properties")
    private List<String> properties;

    @JsonProperty("relationshipTypes")
    private List<String> relationshipTypes;

    @JsonProperty("isLocation")
    private boolean isLocation;

    // Gettery i settery

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPluralLabel() {
        return pluralLabel;
    }

    public void setPluralLabel(String pluralLabel) {
        this.pluralLabel = pluralLabel;
    }

    public boolean isHasDates() {
        return hasDates;
    }

    public void setHasDates(boolean hasDates) {
        this.hasDates = hasDates;
    }

    public List<String> getSuperTypes() {
        return superTypes;
    }

    public void setSuperTypes(List<String> superTypes) {
        this.superTypes = superTypes;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getRelationshipTypes() {
        return relationshipTypes;
    }

    public void setRelationshipTypes(List<String> relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }
}
