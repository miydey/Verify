package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2017/11/6.
 */

public class RecordNoCard {

    /**
     * 无证记录上传
     */
    private String machineToken;//机器登录识别码
    private String machineId;//机器编码
    private int hotelId;//酒店id
    private String cardNumber;//顾客身份证
    private String name;
    boolean checkSuccess;//是否通过验证
    private String picCamera;//抓拍头像
    private int gender;//0女 1男

    public String getMachineToken() {
        return machineToken;
    }

    public void setMachineToken(String machineToken) {
        this.machineToken = machineToken;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isCheckSuccess() {
        return checkSuccess;
    }

    public void setCheckSuccess(boolean checkSuccess) {
        this.checkSuccess = checkSuccess;
    }

    public String getPicCamera() {
        return picCamera;
    }

    public void setPicCamera(String picCamera) {
        this.picCamera = picCamera;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
