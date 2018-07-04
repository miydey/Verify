package com.chuangber.verify.bean;

/**
 * Created by jinyh on 2017/11/13.
 */

public class Machine {
    String machine_id;
    String machine_name;
    boolean machine_login;

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

    public boolean isMachine_login() {
        return machine_login;
    }

    public void setMachine_login(boolean machine_login) {
        this.machine_login = machine_login;
    }
}
