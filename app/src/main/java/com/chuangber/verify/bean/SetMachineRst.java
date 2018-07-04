package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2018/4/18.
 */

public class SetMachineRst extends BaseRst {
    public static class Data{
        private String machine_token;

        public String getMachine_token() {
            return machine_token;
        }

        public void setMachine_token(String machine_token) {
            this.machine_token = machine_token;
        }
    }
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
