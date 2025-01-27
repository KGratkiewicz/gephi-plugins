package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ColorDetails {

    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    private String label;

    @JsonProperty("swatchColor")
    private String swatchColor;

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

    public String getSwatchColor() {
        return swatchColor;
    }

    public void setSwatchColor(String swatchColor) {
        this.swatchColor = swatchColor;
    }
}
