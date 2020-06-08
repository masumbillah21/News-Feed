package com.masum_billah.newsfeed;

import android.graphics.Bitmap;

public class News {
    private String mSectionName;
    private String mNewsTitle;
    private String mNewsAuthor;
    private String mDateTime;
    private String mWebUrl;
    private Bitmap mImage;

    public News(String sectionName, String newsTitle, String newsAuthor,
                Bitmap image, String dateTime,
                String webUrl) {
        this.mNewsTitle = newsTitle;
        this.mNewsAuthor = newsAuthor;
        this.mSectionName = sectionName;
        this.mDateTime = dateTime;
        this.mWebUrl = webUrl;
        this.mImage = image;
    }

    public String getNewsTitle() {
        return mNewsTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public String getWebUrl() {
        return mWebUrl;
    }
    
    public String getNewsAuthor() { return mNewsAuthor;}

    public Bitmap getImage() {
        return mImage;
    }
}
