package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateFilter {
    @JsonProperty("above")
    private String above;
    @JsonProperty("below")
    private String below;
    @JsonProperty("isAboveIncluded")
    private boolean isAboveIncluded;
    @JsonProperty("isBelowIncluded")
    private boolean isBelowIncluded;

    public String getAbove() {
        return above;
    }

    public void setAbove(String above) {
        this.above = above;
    }

    public String getBelow() {
        return below;
    }

    public void setBelow(String below) {
        this.below = below;
    }

    public boolean isAboveIncluded() {
        return isAboveIncluded;
    }

    public void setAboveIncluded(boolean aboveIncluded) {
        isAboveIncluded = aboveIncluded;
    }

    public boolean isBelowIncluded() {
        return isBelowIncluded;
    }

    public void setBelowIncluded(boolean belowIncluded) {
        isBelowIncluded = belowIncluded;
    }
}
