package de.ingrid.ibus.model;

import java.util.List;

public class ElasticsearchInfo {
    private List<Index> indices;

    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }
}
