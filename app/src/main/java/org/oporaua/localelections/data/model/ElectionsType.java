package org.oporaua.localelections.data.model;

import com.google.gson.annotations.SerializedName;

public class ElectionsType {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String title;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
