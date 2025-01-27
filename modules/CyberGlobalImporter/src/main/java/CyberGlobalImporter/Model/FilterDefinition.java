package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterDefinition {

    @JsonProperty("filterName")
    private String filterName;

    @JsonProperty("matchCriteria")
    private String matchCriteria;

    @JsonProperty("date")
    private DateFilter date;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getMatchCriteria() {
        return matchCriteria;
    }

    public void setMatchCriteria(String matchCriteria) {
        this.matchCriteria = matchCriteria;
    }

    public DateFilter getDate() {
        return date;
    }

    public void setDate(DateFilter date) {
        this.date = date;
    }
}
