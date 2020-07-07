package com.example.veterinary_clinic_info_app;

import java.util.Date;

public class Pet {
    private String image_url;
    private String title;
    private String content_url;
    private Date date_added;

    public Pet(String image_url, String title, String content_url, Date date_added) {
        this.image_url = image_url;
        this.title = title;
        this.content_url = content_url;
        this.date_added = date_added;
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

    public Date getDate_added() {
        return date_added;
    }
}
