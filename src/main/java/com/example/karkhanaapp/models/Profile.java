package com.example.karkhanaapp.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Profile {
    @DocumentId
    private String profileId;

    private String farmerId;
    private String profilePhotoUrl;
    private String aadhaarMasked;
    private String district;
    private String village;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

    public Profile() {
    }

    public Profile(String farmerId, String aadhaarMasked, String district, String village) {
        this.farmerId = farmerId;
        this.aadhaarMasked = aadhaarMasked;
        this.district = district;
        this.village = village;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getAadhaarMasked() {
        return aadhaarMasked;
    }

    public void setAadhaarMasked(String aadhaarMasked) {
        this.aadhaarMasked = aadhaarMasked;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
