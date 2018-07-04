package com.chuangber.verify.presenter.impl;

import com.chuangber.verify.presenter.IBasePresenter;

/**
 * Created by jinyh on 2017/12/15.
 */

public class BasePresenterImpl <T> implements IBasePresenter{

    public T mView;
    protected void attachView(T mView) {
        this.mView = mView;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }
}
