package org.oporaua.localelections.model;

import com.google.gson.annotations.SerializedName;

public class Evidence {

    public String getUrl() {
        return url;
    }

    @SerializedName("url")
    private String url;

    public Evidence(String url) {
        this.url = url;
    }
}
