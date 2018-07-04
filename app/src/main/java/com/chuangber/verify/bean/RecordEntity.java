package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/4/27.
 * 上传有证记录
 */

public class RecordEntity {

    String machine_token;
    String machine_id;
    int hotel_id;
    String card_number;
    String name;
    String card_addr;
    int percentage;
    int check_success;
    String pic_id;
    String pic_camera;
    String sex ;//0女 1男

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMachine_token() {
        return machine_token;
    }

    public void setMachine_token(String machine_token) {
        this.machine_token = machine_token;
    }

    public String getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(String machine_id) {
        this.machine_id = machine_id;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(int hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard_addr() {
        return card_addr;
    }

    public void setCard_addr(String card_addr) {
        this.card_addr = card_addr;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getCheck_success() {
        return check_success;
    }

    public void setCheck_success(int check_success) {
        this.check_success = check_success;
    }

    public String getPic_id() {
        return pic_id;
    }

    public void setPic_id(String pic_id) {
        this.pic_id = pic_id;
    }

    public String getPic_camera() {
        return pic_camera;
    }

    public void setPic_camera(String pic_camera) {
        this.pic_camera = pic_camera;
    }
}
