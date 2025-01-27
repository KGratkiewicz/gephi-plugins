package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppData {

    @JsonProperty("aeonTimeline")
    private AeonTimeline aeonTimeline;

    // Gettery i settery

    public AeonTimeline getAeonTimeline() {
        return aeonTimeline;
    }

    public void setAeonTimeline(AeonTimeline aeonTimeline) {
        this.aeonTimeline = aeonTimeline;
    }
}
