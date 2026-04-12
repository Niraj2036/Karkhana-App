package com.example.karkhanaapp.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

/**
 * Represents a Farmer user in the Karkhana system.
 * This is the primary user entity authenticated via Firebase Auth.
 */
public class Farmer {
    @DocumentId
    private String farmerId;

    private String name;
    private String email;
    private String phone;
    private String aadhaarMasked;  // Last 4 digits only
    private String district;
    private String village;
    private Double totalArea;
    private Double lastYield;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

    // Constructors
    public Farmer() {
    }

    public Farmer(String name, String email, String phone, String district, String village) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.district = district;
        this.village = village;
        this.totalArea = 0.0;
        this.lastYield = 0.0;
    }

    // Getters and Setters
    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Double getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Double totalArea) {
        this.totalArea = totalArea;
    }

    public Double getLastYield() {
        return lastYield;
    }

    public void setLastYield(Double lastYield) {
        this.lastYield = lastYield;
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

