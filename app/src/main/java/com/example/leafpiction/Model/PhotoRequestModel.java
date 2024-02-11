package com.example.leafpiction.Model;

public class PhotoRequestModel {
    private String photo;
    private int id;
    private String chlorophyll, carotenoid, antocyanin;

    public PhotoRequestModel(String photo, int id, String chlorophyll, String carotenoid, String antocyanin){
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

    public String getChlorophyll() {
        return chlorophyll;
    }

    public void setChlorophyll(String chlorophyll) {
        this.chlorophyll = chlorophyll;
    }

    public String getCarotenoid() {
        return carotenoid;
    }

    public void setCarotenoid(String carotenoid) {
        this.carotenoid = carotenoid;
    }

    public String getAntocyanin() {
        return antocyanin;
    }

    public void setAntocyanin(String antocyanin) {
        this.antocyanin = antocyanin;
    }
}
