package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AeonTimeline {

    @JsonProperty("openViews")
    private OpenViews openViews;

    @JsonProperty("scrollingIsLocked")
    private boolean scrollingIsLocked;

    @JsonProperty("exposedFilterTypes")
    private List<String> exposedFilterTypes;

    @JsonProperty("filtersById")
    private Map<String, FilterDefinition> filtersById;

    @JsonProperty("types")
    private TypesDefinition types;

    @JsonProperty("references")
    private ReferencesDefinition references;

    @JsonProperty("disableFiltering")
    private boolean disableFiltering;

    @JsonProperty("disableFilteringMore")
    private boolean disableFilteringMore;

    @JsonProperty("disableSplitView")
    private boolean disableSplitView;

    @JsonProperty("disableTimelineZoom")
    private boolean disableTimelineZoom;

    @JsonProperty("skin")
    private String skin;

    @JsonProperty("timelineViewSettings")
    private TimelineViewSettings timelineViewSettings;

    @JsonProperty("colors")
    private ColorsDefinition colors;

    @JsonProperty("tags")
    private Map<String, Object> tags;

    @JsonProperty("properties")
    private PropertiesDefinition properties;

    // Gettery i settery

    public OpenViews getOpenViews() {
        return openViews;
    }

    public void setOpenViews(OpenViews openViews) {
        this.openViews = openViews;
    }

    public boolean isScrollingIsLocked() {
        return scrollingIsLocked;
    }

    public void setScrollingIsLocked(boolean scrollingIsLocked) {
        this.scrollingIsLocked = scrollingIsLocked;
    }

    public List<String> getExposedFilterTypes() {
        return exposedFilterTypes;
    }

    public void setExposedFilterTypes(List<String> exposedFilterTypes) {
        this.exposedFilterTypes = exposedFilterTypes;
    }

    public Map<String, FilterDefinition> getFiltersById() {
        return filtersById;
    }

    public void setFiltersById(Map<String, FilterDefinition> filtersById) {
        this.filtersById = filtersById;
    }

    public TypesDefinition getTypes() {
        return types;
    }

    public void setTypes(TypesDefinition types) {
        this.types = types;
    }

    public ReferencesDefinition getReferences() {
        return references;
    }

    public void setReferences(ReferencesDefinition references) {
        this.references = references;
    }

    public boolean isDisableFiltering() {
        return disableFiltering;
    }

    public void setDisableFiltering(boolean disableFiltering) {
        this.disableFiltering = disableFiltering;
    }

    public boolean isDisableFilteringMore() {
        return disableFilteringMore;
    }

    public void setDisableFilteringMore(boolean disableFilteringMore) {
        this.disableFilteringMore = disableFilteringMore;
    }

    public boolean isDisableSplitView() {
        return disableSplitView;
    }

    public void setDisableSplitView(boolean disableSplitView) {
        this.disableSplitView = disableSplitView;
    }

    public boolean isDisableTimelineZoom() {
        return disableTimelineZoom;
    }

    public void setDisableTimelineZoom(boolean disableTimelineZoom) {
        this.disableTimelineZoom = disableTimelineZoom;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public TimelineViewSettings getTimelineViewSettings() {
        return timelineViewSettings;
    }

    public void setTimelineViewSettings(TimelineViewSettings timelineViewSettings) {
        this.timelineViewSettings = timelineViewSettings;
    }

    public ColorsDefinition getColors() {
        return colors;
    }

    public void setColors(ColorsDefinition colors) {
        this.colors = colors;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public PropertiesDefinition getProperties() {
        return properties;
    }

    public void setProperties(PropertiesDefinition properties) {
        this.properties = properties;
    }
}