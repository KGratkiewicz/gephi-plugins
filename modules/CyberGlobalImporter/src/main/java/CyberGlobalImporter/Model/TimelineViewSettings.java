package CyberGlobalImporter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimelineViewSettings {

    @JsonProperty("scrollBounds")
    private String scrollBounds;

    @JsonProperty("eventLayout")
    private String eventLayout;

    @JsonProperty("showItemCard")
    private boolean showItemCard;

    @JsonProperty("showIdentifier")
    private boolean showIdentifier;

    @JsonProperty("showDates")
    private boolean showDates;

    @JsonProperty("showDuration")
    private boolean showDuration;

    @JsonProperty("showNarrative")
    private boolean showNarrative;

    @JsonProperty("showRelationshipTypes")
    private List<String> showRelationshipTypes;

    @JsonProperty("showBlocks")
    private boolean showBlocks;

    @JsonProperty("showBlockedBy")
    private boolean showBlockedBy;

    @JsonProperty("showImage")
    private boolean showImage;

    @JsonProperty("showSummary")
    private boolean showSummary;

    @JsonProperty("showDependencies")
    private boolean showDependencies;

    @JsonProperty("showTodayLine")
    private boolean showTodayLine;

    @JsonProperty("zoomFrom")
    private int zoomFrom;

    @JsonProperty("zoomTo")
    private int zoomTo;

    @JsonProperty("showTypes")
    private List<String> showTypes;

    @JsonProperty("showTags")
    private boolean showTags;

    @JsonProperty("showProperties")
    private List<String> showProperties;

    @JsonProperty("showLinks")
    private boolean showLinks;

    @JsonProperty("showRelatedItemTypesOnNewLines")
    private boolean showRelatedItemTypesOnNewLines;

    // Gettery i settery

    public String getScrollBounds() {
        return scrollBounds;
    }

    public void setScrollBounds(String scrollBounds) {
        this.scrollBounds = scrollBounds;
    }

    public String getEventLayout() {
        return eventLayout;
    }

    public void setEventLayout(String eventLayout) {
        this.eventLayout = eventLayout;
    }

    public boolean isShowItemCard() {
        return showItemCard;
    }

    public void setShowItemCard(boolean showItemCard) {
        this.showItemCard = showItemCard;
    }

    public boolean isShowIdentifier() {
        return showIdentifier;
    }

    public void setShowIdentifier(boolean showIdentifier) {
        this.showIdentifier = showIdentifier;
    }

    public boolean isShowDates() {
        return showDates;
    }

    public void setShowDates(boolean showDates) {
        this.showDates = showDates;
    }

    public boolean isShowDuration() {
        return showDuration;
    }

    public void setShowDuration(boolean showDuration) {
        this.showDuration = showDuration;
    }

    public boolean isShowNarrative() {
        return showNarrative;
    }

    public void setShowNarrative(boolean showNarrative) {
        this.showNarrative = showNarrative;
    }

    public List<String> getShowRelationshipTypes() {
        return showRelationshipTypes;
    }

    public void setShowRelationshipTypes(List<String> showRelationshipTypes) {
        this.showRelationshipTypes = showRelationshipTypes;
    }

    public boolean isShowBlocks() {
        return showBlocks;
    }

    public void setShowBlocks(boolean showBlocks) {
        this.showBlocks = showBlocks;
    }

    public boolean isShowBlockedBy() {
        return showBlockedBy;
    }

    public void setShowBlockedBy(boolean showBlockedBy) {
        this.showBlockedBy = showBlockedBy;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public boolean isShowSummary() {
        return showSummary;
    }

    public void setShowSummary(boolean showSummary) {
        this.showSummary = showSummary;
    }

    public boolean isShowDependencies() {
        return showDependencies;
    }

    public void setShowDependencies(boolean showDependencies) {
        this.showDependencies = showDependencies;
    }

    public boolean isShowTodayLine() {
        return showTodayLine;
    }

    public void setShowTodayLine(boolean showTodayLine) {
        this.showTodayLine = showTodayLine;
    }

    public int getZoomFrom() {
        return zoomFrom;
    }

    public void setZoomFrom(int zoomFrom) {
        this.zoomFrom = zoomFrom;
    }

    public int getZoomTo() {
        return zoomTo;
    }

    public void setZoomTo(int zoomTo) {
        this.zoomTo = zoomTo;
    }

    public List<String> getShowTypes() {
        return showTypes;
    }

    public void setShowTypes(List<String> showTypes) {
        this.showTypes = showTypes;
    }

    public boolean isShowTags() {
        return showTags;
    }

    public void setShowTags(boolean showTags) {
        this.showTags = showTags;
    }

    public List<String> getShowProperties() {
        return showProperties;
    }

    public void setShowProperties(List<String> showProperties) {
        this.showProperties = showProperties;
    }

    public boolean isShowLinks() {
        return showLinks;
    }

    public void setShowLinks(boolean showLinks) {
        this.showLinks = showLinks;
    }

    public boolean isShowRelatedItemTypesOnNewLines() {
        return showRelatedItemTypesOnNewLines;
    }

    public void setShowRelatedItemTypesOnNewLines(boolean showRelatedItemTypesOnNewLines) {
        this.showRelatedItemTypesOnNewLines = showRelatedItemTypesOnNewLines;
    }
}
