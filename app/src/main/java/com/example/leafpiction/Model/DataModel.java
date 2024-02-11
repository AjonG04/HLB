package com.example.leafpiction.Model;

public class DataModel {

    private int id;
    private byte[] photo;
    private String status;
    private String confidence;
    private String datetime;
    private String filename;
    private int uploaded;

    public DataModel() {
    }

    public DataModel(byte[] photo, String status, String confidence, String datetime, String filename, int uploaded) {
        this.photo = photo;
        this.status = status;
        this.confidence = confidence;
        this.datetime = datetime;
        this.filename = filename;
        this.uploaded = uploaded;
    }

    public DataModel(int id, byte[] photo, String status, String confidence, String datetime, String filename, int uploaded) {
        this.id = id;
        this.photo = photo;
        this.status = status;
        this.confidence = confidence;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
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
