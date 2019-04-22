
package com.moveitdriver.models.allRatingResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverRatingResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("ratings")
    @Expose
    private List<Rating> ratings = null;
    @SerializedName("one")
    @Expose
    private Integer one;
    @SerializedName("two")
    @Expose
    private Integer two;
    @SerializedName("three")
    @Expose
    private Integer three;
    @SerializedName("four")
    @Expose
    private Integer four;
    @SerializedName("five")
    @Expose
    private Integer five;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("Reviews")
    @Expose
    private List<Review> reviews = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Integer getOne() {
        return one;
    }

    public void setOne(Integer one) {
        this.one = one;
    }

    public Integer getTwo() {
        return two;
    }

    public void setTwo(Integer two) {
        this.two = two;
    }

    public Integer getThree() {
        return three;
    }

    public void setThree(Integer three) {
        this.three = three;
    }

    public Integer getFour() {
        return four;
    }

    public void setFour(Integer four) {
        this.four = four;
    }

    public Integer getFive() {
        return five;
    }

    public void setFive(Integer five) {
        this.five = five;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
