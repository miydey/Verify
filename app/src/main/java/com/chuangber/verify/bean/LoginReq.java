package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/6/29.
 */

public class LoginReq {

    String hotel_account;
    String password;

    public String getHotel_account() {
        return hotel_account;
    }

    public void setHotel_account(String hotel_account) {
        this.hotel_account = hotel_account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
