package com.example.veterinary_clinic_info_app;

import java.util.Date;

public class Pet {
    //todo camelcase
    private String image_url;
    private String title;
    private String content_url;
    private Date dateAdded;

    public Pet(String image_url, String title, String content_url, Date dateAdded) {
        this.image_url = image_url;
        this.title = title;
        this.content_url = content_url;
        this.dateAdded = dateAdded;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent_url() {
        return content_url;
    }

    public Date getDateAdded() {
        return dateAdded;
    }
}
