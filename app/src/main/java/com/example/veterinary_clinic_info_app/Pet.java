package com.example.veterinary_clinic_info_app;

import android.graphics.Bitmap;

import java.util.Date;

public class Pet {
    private String imageUrl;
    private String title;
    private String contentUrl;
    private Date dateAdded;
    private Bitmap bitmap;

    public Pet(String imageUrl, String title, String contentUrl, Date dateAdded) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.contentUrl = contentUrl;
        this.dateAdded = dateAdded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
