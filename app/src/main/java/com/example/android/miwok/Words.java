package com.example.android.miwok;

public class Words {
    private static final int NO_IMAGE = -1;
    private String miwok;
    private String english;
    private int img = NO_IMAGE;
    private int resRecord;

    public Words(String english, String miwok, int resRecord) {
        this.miwok = miwok;
        this.english = english;
        this.resRecord = resRecord;
    }

    public Words(String english, String miwok, int img, int resRecord) {
        this.miwok = miwok;
        this.english = english;
        this.img = img;
        this.resRecord = resRecord;
    }

    public int getImg() {
        return img;
    }

    public String getMiwok() {
        return miwok;
    }

    public String getEnglish() {
        return english;
    }

    public int getResRecord() {
        return resRecord;
    }

    public boolean hasImage() {
        return img != NO_IMAGE;
    }

}
