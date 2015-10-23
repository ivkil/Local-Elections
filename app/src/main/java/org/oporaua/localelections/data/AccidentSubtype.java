package org.oporaua.localelections.data;

import com.google.gson.annotations.SerializedName;

public class AccidentSubtype {

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
