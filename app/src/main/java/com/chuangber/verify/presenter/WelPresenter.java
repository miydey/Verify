package com.chuangber.verify.presenter;

/**
 * Created by jinyh on 2018/4/17.
 */

public interface WelPresenter {
    void initDatas();
    void login(String account,String password);
    void setMachine(String id);
    void queryMachines();
}
