package com.example.leafpiction.Model;

public class PhotoRequestModel {
    private String photo;
    private int id;
    private float chlorophyll, carotenoid, antocyanin;

    public PhotoRequestModel(String photo, int id, float chlorophyll, float carotenoid, float antocyanin){
        this.photo = photo;
        this.id = id;
        this.chlorophyll = chlorophyll;
        this.carotenoid = carotenoid;
        this.antocyanin = antocyanin;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getChlorophyll() {
        return chlorophyll;
    }

    public void setChlorophyll(float chlorophyll) {
        this.chlorophyll = chlorophyll;
    }

    public float getCarotenoid() {
        return carotenoid;
    }

    public void setCarotenoid(float carotenoid) {
        this.carotenoid = carotenoid;
    }

    public float getAntocyanin() {
        return antocyanin;
    }

    public void setAntocyanin(float antocyanin) {
        this.antocyanin = antocyanin;
    }
}
