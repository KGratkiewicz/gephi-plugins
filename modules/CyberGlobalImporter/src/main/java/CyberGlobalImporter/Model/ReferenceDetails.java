package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceDetails {

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("canDelete")
    private boolean canDelete;

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    // ... i wiele innych pól (icon, color, lineStyle itp.)

    @JsonProperty("types")
    private Map<String, Boolean> types;

    // Gettery i settery

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

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

    public Map<String, Boolean> getTypes() {
        return types;
    }

    public void setTypes(Map<String, Boolean> types) {
        this.types = types;
    }
}
