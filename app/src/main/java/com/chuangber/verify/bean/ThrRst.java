package com.chuangber.verify.bean;

/**
 * Created by jinyh  on 2018/4/27.
 */

public class ThrRst extends BaseRst {
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        String thr;

        public String getThr() {
            return thr;
        }

        public void setThr(String thr) {
            this.thr = thr;
        }
    }

}
