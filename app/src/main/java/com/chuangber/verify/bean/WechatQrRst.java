package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/4/18.
 */

public class WechatQrRst extends BaseRst {

    Data data;
    WxMachine wxMachine;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public WxMachine getWxMachine() {
        return wxMachine;
    }

    public void setWxMachine(WxMachine wxMachine) {
        this.wxMachine = wxMachine;
    }

    public static class Data{
        String aheadurl;
        String hotel_id;

        public String getHotel_id() {
            return hotel_id;
        }

        public void setHotel_id(String hotel_id) {
            this.hotel_id = hotel_id;
        }

        public String getAheadurl() {
            return aheadurl;
        }

        public void setAheadurl(String aheadurl) {
            this.aheadurl = aheadurl;
        }
    }

    public static class WxMachine {
        String machine_id;
        String machine_name;
        String machine_token;

        public String getMachine_id() {
            return machine_id;
        }

        public void setMachine_id(String machine_id) {
            this.machine_id = machine_id;
        }

        public String getMachine_name() {
            return machine_name;
        }

        public void setMachine_name(String machine_name) {
            this.machine_name = machine_name;
        }

        public String getMachine_token() {
            return machine_token;
        }

        public void setMachine_token(String machine_token) {
            this.machine_token = machine_token;
        }
    }
}
