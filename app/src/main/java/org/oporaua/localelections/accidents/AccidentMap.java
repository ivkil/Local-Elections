package org.oporaua.localelections.accidents;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class AccidentMap implements ClusterItem {

    private long id;
    private long accidentType;
    private String title;
    private double latitude;
    private double longitude;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccidentType() {
        return accidentType;
    }

    public void setAccidentType(long accidentType) {
        this.accidentType = accidentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public void setPosition(LatLng position) {
        latitude = position.latitude;
        longitude = position.longitude;
    }
}
