package org.oporaua.localelections.data.model;

import com.google.gson.annotations.SerializedName;

public class AccidentType {

    public final static int ILLEGAL_CAMPAIGNING = 1;
    public final static int ADMINISTRATIVE_RESOURCES = 2;
    public final static int VOTERS_BRIBING = 3;
    public final static int VOTERS_LISTS_MANIPULATIONS = 4;
    public final static int OBSERVERS_OBSTRUCTION = 5;
    public final static int COMMISSIONS_VIOLATIONS = 6;
    public final static int FALSIFICATIONS = 7;
    public final static int CRIMINAL_INFLUENCE = 8;

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
