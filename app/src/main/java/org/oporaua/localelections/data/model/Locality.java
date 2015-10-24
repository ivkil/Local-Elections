package org.oporaua.localelections.data.model;

import com.google.gson.annotations.SerializedName;

public class Locality {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String title;

    @SerializedName("region_id")
    private long regionId;

    @SerializedName("district_title")
    private String district;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getRegionId() {
        return regionId;
    }

    public String getDistrict() {
        return district;
    }

}
