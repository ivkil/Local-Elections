package org.oporaua.localelections.model;


import com.google.gson.annotations.SerializedName;

public class Accident {

    @SerializedName("id")
    private long id;

    @SerializedName("date")
    private String date;

    @SerializedName("source")
    private String source;

    @SerializedName("accident_subtype_id")
    private long accidentSubtypeId;

    @SerializedName("offender")
    private String offender;

    @SerializedName("offender_party_id")
    private int offenderPartyId;

    @SerializedName("victim")
    private String victim;

    @SerializedName("victim_party_id")
    private long victimPartyId;

    @SerializedName("beneficiary")
    private String beneficciary;

    @SerializedName("beneficiary_party_id")

    private long beneficiaryPartyId;

    @SerializedName("evidence")
    private Evidence evidence;

    @SerializedName("last_ip")
    private String lastIp;

    @SerializedName("locality_id")
    private long localityId;

    @SerializedName("polling_station")
    private String pollingStation;

    public long getRegionId() {
        return regionId;
    }

    @SerializedName("region_id")
    private long regionId;
    //"user_id":237,

    @SerializedName("election_id")
    private long electionsId;

    @SerializedName("title")
    private String title;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lang")
    private double longitude;


    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getAccidentSubtypeId() {
        return accidentSubtypeId;
    }

    public void setAccidentSubtypeId(long accidentSubtypeId) {
        this.accidentSubtypeId = accidentSubtypeId;
    }

    public void setOffender(String offender) {
        this.offender = offender;
    }

    public int getOffenderPartyId() {
        return offenderPartyId;
    }

    public void setOffenderPartyId(int offenderPartyId) {
        this.offenderPartyId = offenderPartyId;
    }

    public void setVictim(String victim) {
        this.victim = victim;
    }

    public void setVictimPartyId(long victimPartyId) {
        this.victimPartyId = victimPartyId;
    }

    public void setBeneficciary(String beneficciary) {
        this.beneficciary = beneficciary;
    }

    public void setBeneficiaryPartyId(long beneficiaryPartyId) {
        this.beneficiaryPartyId = beneficiaryPartyId;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(long localityId) {
        this.localityId = localityId;
    }

    public void setPollingStation(String pollingStation) {
        this.pollingStation = pollingStation;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public long getElectionsId() {
        return electionsId;
    }

    public void setElectionsId(long electionsId) {
        this.electionsId = electionsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
