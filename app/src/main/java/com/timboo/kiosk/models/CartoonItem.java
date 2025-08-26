package com.timboo.kiosk.models;

public class CartoonItem {
    private String title;
    private String description;
    private String thumbnailPath;

    public CartoonItem(String title, String description, String thumbnailPath) {
        this.title = title;
        this.description = description;
        this.thumbnailPath = thumbnailPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}