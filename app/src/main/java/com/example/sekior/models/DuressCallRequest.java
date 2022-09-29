package com.example.sekior.models;

public class DuressCallRequest {
    public String senderMobile;
    public String senderName;
    public String senderEmail;
    public String receiverMobile;
    public String receiverName;
    public String currentAddress;
    public String currentCoordinate;
    public String serviceTypeId;
    public String imageReport;

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setCurrentCoordinate(String currentCoordinate) {
        this.currentCoordinate = currentCoordinate;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getImageReport() {
        return imageReport;
    }

    public void setImageReport(String imageReport) {
        this.imageReport = imageReport;
    }
}
