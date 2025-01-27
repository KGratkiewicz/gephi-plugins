package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Definition {

    @JsonProperty("types")
    private List<DefinitionType> types;

    @JsonProperty("properties")
    private List<DefinitionProperty> properties;

    @JsonProperty("relationshipTypes")
    private List<RelationshipType> relationshipTypes;

    @JsonProperty("isRelative")
    private boolean isRelative;

    @JsonProperty("relativeUnits")
    private String relativeUnits;

    @JsonProperty("timescale")
    private String timescale;

    // Gettery i settery

    public List<DefinitionType> getTypes() {
        return types;
    }

    public void setTypes(List<DefinitionType> types) {
        this.types = types;
    }

    public List<DefinitionProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<DefinitionProperty> properties) {
        this.properties = properties;
    }

    public List<RelationshipType> getRelationshipTypes() {
        return relationshipTypes;
    }

    public void setRelationshipTypes(List<RelationshipType> relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public void setRelative(boolean relative) {
        isRelative = relative;
    }

    public String getRelativeUnits() {
        return relativeUnits;
    }

    public void setRelativeUnits(String relativeUnits) {
        this.relativeUnits = relativeUnits;
    }

    public String getTimescale() {
        return timescale;
    }

    public void setTimescale(String timescale) {
        this.timescale = timescale;
    }
}
