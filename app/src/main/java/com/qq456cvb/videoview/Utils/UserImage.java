package com.qq456cvb.videoview.Utils;

/**
 * Created by qq456cvb on 8/22/15.
 */
public class UserImage {
    // TODO: add other fields.
    private String url;
    private String reviewId;
    private String userId;
    private String provincial; //TODO: what's this???

    public String getURL() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public String getReviewId() {
        return reviewId;
    }

    public void setProvincial(String provincial) {
        this.provincial = provincial;
    }
    public String getProvincial() {
        return provincial;
    }
}
