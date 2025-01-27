package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Klasa główna odwzorowująca cały JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeonTimelineRoot {

    @JsonProperty("definition")
    private Definition definition;

    @JsonProperty("data")
    private DataSection data;

    @JsonProperty("applicableDate")
    private String applicableDate;

    @JsonProperty("language")
    private String language;

    @JsonProperty("@context")
    private String context;

    @JsonProperty("appData")
    private AppData appData;

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public DataSection getData() {
        return data;
    }

    public void setData(DataSection data) {
        this.data = data;
    }

    public String getApplicableDate() {
        return applicableDate;
    }

    public void setApplicableDate(String applicableDate) {
        this.applicableDate = applicableDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public AppData getAppData() {
        return appData;
    }

    public void setAppData(AppData appData) {
        this.appData = appData;
    }
}
