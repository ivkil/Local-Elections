package org.oporaua.localelections.tvk;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

@ParcelablePlease
public class TvkMember implements Parcelable {

    @SerializedName("id")
    long id;

    @SerializedName("fio")
    String name;

    @SerializedName("year")
    int year;

    @SerializedName("el")
    String elections;

    @SerializedName("region")
    String region;

    @SerializedName("komisia")
    String commission;

    @SerializedName("posada_com")
    String position;

    @SerializedName("partiya")
    String party;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        TvkMemberParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<TvkMember> CREATOR = new Creator<TvkMember>() {
        public TvkMember createFromParcel(Parcel source) {
            TvkMember target = new TvkMember();
            TvkMemberParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public TvkMember[] newArray(int size) {
            return new TvkMember[size];
        }
    };
}
