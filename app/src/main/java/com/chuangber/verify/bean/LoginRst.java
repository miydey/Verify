package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/4/17.
 * 登录返回
 */

public class LoginRst extends BaseRst {
    private Hotel hotel;
    private Data data;
    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }




    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private String login_token;
        private String redir_url;

        public String getRedir_url() {
            return redir_url;
        }

        public void setRedir_url(String redir_url) {
            this.redir_url = redir_url;
        }

        public String getLogin_token() {
            return login_token;
        }

        public void setLogin_token(String login_token) {
            this.login_token = login_token;
        }


    }

    public static class Hotel{
        private String id;
        private String name;
        private String limits;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLimits() {
            return limits;
        }

        public void setLimits(String limits) {
            this.limits = limits;
        }
    }
}

