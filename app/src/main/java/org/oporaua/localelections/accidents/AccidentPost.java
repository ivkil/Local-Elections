package org.oporaua.localelections.accidents;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AccidentPost {

    @SerializedName("date")
    private Date date;

    @SerializedName("source")
    private String source;

    @SerializedName("accident_subtype_id")
    private long accidentSubtypeId;

    @SerializedName("offender")
    private String offender;

    @SerializedName("offender_party_id")
    private long offenderPartyId;

    @SerializedName("victim")
    private String victim;

    @SerializedName("victim_party_id")
    private long victimPartyId;

    @SerializedName("beneficiary")
    private String beneficiary;

    @SerializedName("beneficiary_party_id")

    private long beneficiaryPartyId;


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

    @SerializedName("election_id")
    private long electionsId;

    @SerializedName("title")
    private String title;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lang")
    private double longitude;

    @SerializedName("email")
    private String userEmail;

    public String getLastIp() {
        return lastIp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public long getOffenderPartyId() {
        return offenderPartyId;
    }

    public void setOffenderPartyId(long offenderPartyId) {
        this.offenderPartyId = offenderPartyId;
    }

    public void setVictim(String victim) {
        this.victim = victim;
    }

    public void setVictimPartyId(long victimPartyId) {
        this.victimPartyId = victimPartyId;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public void setBeneficiaryPartyId(long beneficiaryPartyId) {
        this.beneficiaryPartyId = beneficiaryPartyId;
    }

//    public byte[] getEvidence() {
//        return evidence;
//    }
//
//    public void setEvidence(byte[] evidence) {
//        this.evidence = evidence;
//    }

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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getOffender() {
        return offender;
    }

    public String getVictim() {
        return victim;
    }

    public long getVictimPartyId() {
        return victimPartyId;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public long getBeneficiaryPartyId() {
        return beneficiaryPartyId;
    }

    public String getPollingStation() {
        return pollingStation;
    }
}
