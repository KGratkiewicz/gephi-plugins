package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenViews {

    @JsonProperty("byId")
    private Map<String, ViewConfiguration> byId;

    @JsonProperty("layout")
    private LayoutConfiguration layout;

    @JsonProperty("activeView")
    private String activeView;

    // Gettery i settery

    public Map<String, ViewConfiguration> getById() {
        return byId;
    }

    public void setById(Map<String, ViewConfiguration> byId) {
        this.byId = byId;
    }

    public LayoutConfiguration getLayout() {
        return layout;
    }

    public void setLayout(LayoutConfiguration layout) {
        this.layout = layout;
    }

    public String getActiveView() {
        return activeView;
    }

    public void setActiveView(String activeView) {
        this.activeView = activeView;
    }
}

