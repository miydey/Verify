package com.chuangber.verify.presenter;

/**
 * Created by jinyh on 2018/4/18.
 */

public interface IMainPresenter {
    void getWXQr(String loginToken,String id);
    void initHandle();
    void initHotelData();
}
