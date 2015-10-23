package org.oporaua.localelections.data;

import com.google.gson.annotations.SerializedName;

public class AccidentSubtype {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String title;

    @SerializedName("accident_type_id")
    private long accidentTypeId;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getAccidentTypeId() {
        return accidentTypeId;
    }
}
