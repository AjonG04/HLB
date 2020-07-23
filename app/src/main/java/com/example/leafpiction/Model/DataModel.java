package com.example.leafpiction.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataModel {

    private int id;
    private byte[] photo;
    private float chlorophyll;
    private float carotenoid;
    private float anthocyanin;
    private String datetime;
    private String filename;
    private int uploaded;

    public DataModel() {
    }

    public DataModel(byte[] photo, float chlorophyll, float carotenoid, float anthocyanin, String datetime, String filename, int uploaded) {
        this.photo = photo;
        this.chlorophyll = chlorophyll;
        this.carotenoid = carotenoid;
        this.anthocyanin = anthocyanin;
        this.datetime = datetime;
        this.filename = filename;
        this.uploaded = uploaded;
    }

    public DataModel(int id, byte[] photo, float chlorophyll, float carotenoid, float anthocyanin, String datetime, String filename, int uploaded) {
        this.id = id;
        this.photo = photo;
        this.chlorophyll = chlorophyll;
        this.carotenoid = carotenoid;
        this.anthocyanin = anthocyanin;
        this.datetime = datetime;
        this.filename = filename;
        this.uploaded = uploaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
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

    public float getAnthocyanin() {
        return anthocyanin;
    }

    public void setAnthocyanin(float anthocyanin) {
        this.anthocyanin = anthocyanin;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    //    //No Getter type Date, Only type String
//    public String getDatetime() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
////        Date date = new Date();
//        return dateFormat.format(this.datetime);
////        return datetime;
//    }
//
//    public void setDatetime(Date datetime) {
//        this.datetime = datetime;
//    }

//    private String getDateTime() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        Date date = new Date();
//        return dateFormat.format(date);
//    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }
}
