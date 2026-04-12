package com.example.karkhanaapp.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class SugarFactory {
    @DocumentId
    private String factoryId;

    private String name;
    private String location;
    private Double distance;
    private Double capacityPerDay;
    private Double rating;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

    public SugarFactory() {
    }

    public SugarFactory(String name, String location, Double distance, Double capacityPerDay, Double rating) {
        this.name = name;
        this.location = location;
        this.distance = distance;
        this.capacityPerDay = capacityPerDay;
        this.rating = rating;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getCapacityPerDay() {
        return capacityPerDay;
    }

    public void setCapacityPerDay(Double capacityPerDay) {
        this.capacityPerDay = capacityPerDay;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
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

