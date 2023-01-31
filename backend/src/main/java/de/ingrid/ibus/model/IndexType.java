/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.ibus.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonView;

public class IndexType {
    
    @JsonView(View.Summary.class)
    private String id;

    @JsonView(View.Summary.class)
    private String name;

    @JsonView(View.Summary.class)
    private Date lastIndexed;

    @JsonView(View.Summary.class)
    private boolean active;

    private int numDocs;

    @JsonView(View.Summary.class)
    private boolean hasLinkedComponent = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastIndexed() {
        return lastIndexed;
    }

    public void setLastIndexed(Date lastIndexed) {
        this.lastIndexed = lastIndexed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getNumDocs() {
        return numDocs;
    }

    public void setNumDocs(int numDocs) {
        this.numDocs = numDocs;
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

}
