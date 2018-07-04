package com.chuangber.verify.iView;

import com.chuangber.verify.bean.Machine;

import java.util.List;

/**
 * Created by jinyh on 2018/4/17.
 */

public interface IWelView extends IBaseView {
    void loginSuccess(String loginToken);
    void showMachines(List<Machine> machines);
    void skip();
}
