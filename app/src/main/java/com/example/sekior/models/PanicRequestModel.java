package com.example.sekior.models;

public class PanicRequestModel {
    public String sendermobile;
    public String sendername;
    public String senderemail;
    public String currentaddress;
    public String currentcoordinate;

    public String getSendermobile() {
        return sendermobile;
    }

    public void setSendermobile(String sendermobile) {
        this.sendermobile = sendermobile;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSenderemail() {
        return senderemail;
    }

    public void setSenderemail(String senderemail) {
        this.senderemail = senderemail;
    }

    public String getCurrentaddress() {
        return currentaddress;
    }

    public void setCurrentaddress(String currentaddress) {
        this.currentaddress = currentaddress;
    }

    public String getCurrentcoordinate() {
        return currentcoordinate;
    }

    public void setCurrentcoordinate(String currentcoordinate) {
        this.currentcoordinate = currentcoordinate;
    }
}
