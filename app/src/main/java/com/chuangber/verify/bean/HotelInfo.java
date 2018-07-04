package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/1/12.
 */

public class HotelInfo {
   private String machineID;
   private String loginToken;
   private String machineName;
   private String hotelName;
   private String hotelID;
   private String machineToken;
   private float threshold;


    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public String getMachineID() {
        return machineID;
    }

    public void setMachineID(String machineID) {
        this.machineID = machineID;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getHotelID() {
        return hotelID;
    }

    public void setHotelID(String hotelID) {
        this.hotelID = hotelID;
    }

    public String getMachineToken() {
        return machineToken;
    }

    public void setMachineToken(String machineToken) {
        this.machineToken = machineToken;
    }
}
