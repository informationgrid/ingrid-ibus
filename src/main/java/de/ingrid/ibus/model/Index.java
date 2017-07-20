package de.ingrid.ibus.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;

public class Index {

    @JsonView(View.Summary.class)
    private String id;

    @JsonView(View.Summary.class)
    private String name;

    @JsonView(View.Summary.class)
    private String longName;

    @JsonView(View.Summary.class)
    private long numberDocs;

    @JsonView(View.Summary.class)
    private Date created;

    private Date lastHeartbeat;
    
    private Date lastIndexed;

    private Map<String, Object> mapping;

    private IndexState indexingState;

    private String adminUrl;

    @JsonView(View.Summary.class)
    private boolean active;

    @JsonView(View.Summary.class)
    private boolean hasLinkedComponent = false;

    @JsonView(View.Summary.class)
    private List<IndexType> types;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumberDocs() {
        return numberDocs;
    }

    public void setNumberDocs(long l) {
        this.numberDocs = l;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public Date getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Date dateTime) {
        this.lastHeartbeat = dateTime;
    }

    public Map<String, Object> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, Object> mapping) {
        this.mapping = mapping;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public IndexState getIndexingState() {
        return indexingState;
    }

    public void setIndexingState(IndexState indexingState) {
        this.indexingState = indexingState;
    }

    public boolean isHasLinkedComponent() {
        return hasLinkedComponent;
    }

    public void setHasLinkedComponent(boolean hasLinkedComponent) {
        this.hasLinkedComponent = hasLinkedComponent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<IndexType> getTypes() {
        return types;
    }

    public void setTypes(List<IndexType> types2) {
        this.types = types2;
    }

    public Date getLastIndexed() {
        return lastIndexed;
    }

    public void setLastIndexed(Date lastIndexed) {
        this.lastIndexed = lastIndexed;
    }
}
