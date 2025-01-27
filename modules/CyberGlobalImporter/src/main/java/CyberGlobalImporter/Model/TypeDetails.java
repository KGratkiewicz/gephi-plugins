package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeDetails {

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    @JsonProperty("pluralLabel")
    private String pluralLabel;

    @JsonProperty("identifierPrefix")
    private String identifierPrefix;

    @JsonProperty("enabled")
    private boolean enabled;

    // ... i wiele innych pól zgodnie z JSON.

    @JsonProperty("references")
    private Map<String, Boolean> references;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    @JsonProperty("superTypes")
    private Map<String, Boolean> superTypes;

    @JsonProperty("canHaveDate")
    private boolean canHaveDate;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("defaultItemColor")
    private String defaultItemColor;

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

    public String getIdentifierPrefix() {
        return identifierPrefix;
    }

    public void setIdentifierPrefix(String identifierPrefix) {
        this.identifierPrefix = identifierPrefix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Boolean> getReferences() {
        return references;
    }

    public void setReferences(Map<String, Boolean> references) {
        this.references = references;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Boolean> getSuperTypes() {
        return superTypes;
    }

    public void setSuperTypes(Map<String, Boolean> superTypes) {
        this.superTypes = superTypes;
    }

    public boolean isCanHaveDate() {
        return canHaveDate;
    }

    public void setCanHaveDate(boolean canHaveDate) {
        this.canHaveDate = canHaveDate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDefaultItemColor() {
        return defaultItemColor;
    }

    public void setDefaultItemColor(String defaultItemColor) {
        this.defaultItemColor = defaultItemColor;
    }
}
