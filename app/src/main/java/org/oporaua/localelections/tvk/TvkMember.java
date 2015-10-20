package org.oporaua.localelections.tvk;

import com.google.gson.annotations.SerializedName;

public class TvkMember {

    @SerializedName("id")
    private long id;

    @SerializedName("fio")
    private String name;

    @SerializedName("year")
    private int year;

    @SerializedName("el")
    private String elections;

    @SerializedName("region")
    private String region;

    @SerializedName("komisia")
    private String commission;

    @SerializedName("posada_com")
    private String position;

    @SerializedName("partiya")
    private String party;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getElections() {
        return elections;
    }

    public String getRegion() {
        return region;
    }

    public String getCommission() {
        return commission;
    }

    public String getPosition() {
        return position;
    }

    public String getParty() {
        return party;
    }
}
