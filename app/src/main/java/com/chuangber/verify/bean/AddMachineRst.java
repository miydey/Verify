package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2017/12/18.
 */

public class AddMachineRst extends BaseRst {
   private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
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

        private String machine_token;
        private String machine_id;
    }
}
