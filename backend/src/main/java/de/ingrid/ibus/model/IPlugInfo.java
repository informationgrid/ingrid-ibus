package de.ingrid.ibus.model;

public class IPlugInfo {
    private boolean active;

    private String id;

    private String name;

    private String description;

    private String adminUrl;

    private boolean useCentralIndex;


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public boolean isUseCentralIndex() {
        return useCentralIndex;
    }

    public void setUseCentralIndex(boolean useCentralIndex) {
        this.useCentralIndex = useCentralIndex;
    }
}
