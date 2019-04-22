
package com.moveitdriver.models.allRatingResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("rating_from")
    @Expose
    private RatingFrom ratingFrom;
    @SerializedName("rating_to")
    @Expose
    private String ratingTo;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("rating")
    @Expose
    private Integer rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RatingFrom getRatingFrom() {
        return ratingFrom;
    }

    public void setRatingFrom(RatingFrom ratingFrom) {
        this.ratingFrom = ratingFrom;
    }

    public String getRatingTo() {
        return ratingTo;
    }

    public void setRatingTo(String ratingTo) {
        this.ratingTo = ratingTo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}
