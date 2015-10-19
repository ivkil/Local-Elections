package org.oporaua.localelections.model;


import com.google.gson.annotations.SerializedName;

public class Accident {

    @SerializedName("id")
    private long id;

    @SerializedName("date")
    private String date;

    @SerializedName("source")
    private String source;

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getSource() {
        return source;
    }

}
