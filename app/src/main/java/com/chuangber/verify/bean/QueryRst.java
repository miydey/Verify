package com.chuangber.verify.bean;

import java.util.List;

/**
 * Created by jinyh on 2018/4/18.
 */

public class QueryRst extends BaseRst {
    List<Machine>machines;
    Data data;

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{

    }
}
