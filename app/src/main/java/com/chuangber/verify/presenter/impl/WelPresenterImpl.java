package com.chuangber.verify.presenter.impl;

import android.content.Context;
import android.util.Log;

import com.chuangber.verify.R;
import com.chuangber.verify.bean.AddMachineRst;
import com.chuangber.verify.bean.LoginReq;
import com.chuangber.verify.bean.LoginRst;
import com.chuangber.verify.bean.Machine;
import com.chuangber.verify.bean.QueryRst;
import com.chuangber.verify.bean.SetMachineRst;
import com.chuangber.verify.iView.IWelView;
import com.chuangber.verify.net.ApiBuild;
import com.chuangber.verify.net.ApiService;
import com.chuangber.verify.presenter.WelPresenter;
import com.chuangber.verify.util.HashUtil;
import com.chuangber.verify.util.SharePreUtil;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jinyh on 2017/12/17.
 */

public class WelPresenterImpl extends BasePresenterImpl<IWelView> implements WelPresenter {

    private static final String TAG = WelPresenterImpl.class.getSimpleName();

    private String hotelId;
    private String loginToken;
    private Context context;
    public WelPresenterImpl(Context context, IWelView loginView){
        this.context = context;
        attachView(loginView);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void login(String account, String password) {
        ApiService service = ApiBuild.getService();
        LoginReq entity = new LoginReq();
        entity.setHotel_account(account);
        entity.setPassword(password);
        Map<String, Object> params = null ;
        try {
            params = HashUtil.convertToMap(entity);
            Call<LoginRst>  loginRstCall = service.loginPost(params);
            loginRstCall.enqueue(new Callback<LoginRst>() {
                @Override
                public void onResponse(Call<LoginRst> call, Response<LoginRst> response) {
                    boolean isSuccess = response.isSuccessful();
                    if (isSuccess){
                        LoginRst loginRst = response.body();
                        boolean success = loginRst.success;
                        if (success){
                            loginToken = loginRst.getData().getLogin_token();
                            hotelId = loginRst.getHotel().getId();
                            String hotelName = loginRst.getHotel().getName();
                            SharePreUtil.saveStringData(context,"hotel_name",hotelName);
                            SharePreUtil.saveStringData(context,"hotel_id",hotelId);
                            mView.loginSuccess(loginToken);
                        }else {
                            mView.showToast("登录失败，账号或密码错误");
                        }

                    } else mView.showToast("登录失败，请检查账号信息");
                }

                @Override
                public void onFailure(Call<LoginRst> call, Throwable t) {
                    mView.showToast("服务器链接异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMachine(final String id) {
            ApiService service = ApiBuild.getService();
            Call<SetMachineRst> call = service.setMachine(loginToken,id,Integer.parseInt(hotelId));
            call.enqueue(new Callback<SetMachineRst>() {
                @Override
                public void onResponse(Call<SetMachineRst> call, Response<SetMachineRst> response) {
                    boolean success = response.isSuccessful();
                    if (success){
                        boolean serverSuccess = response.body().success;
                        if (serverSuccess){
                            String machine_token = response.body().getData().getMachine_token();
                            Log.e(TAG, "set machine "+machine_token );
                            SharePreUtil.saveStringData(context,"machine_token",machine_token);
                            SharePreUtil.saveStringData(context,"machine_id",id);
                            SharePreUtil.saveBooleanData(context,"first_get",false);
                            mView.skip();
                        }else {
                            mView.showToast("服务端请求失败");
                        }

                    }else {
                        mView.showToast("指定前台机失败");
                    }
                }

                @Override
                public void onFailure(Call<SetMachineRst> call, Throwable t) {
                    mView.showToast("指定前台机失败");
                }
            });
        }

    @Override
    public void queryMachines() {
        ApiService service = ApiBuild.getService();
        Call<QueryRst> call = service.queryMachines(hotelId,loginToken);
        call.enqueue(new Callback<QueryRst>() {
            @Override
            public void onResponse(Call<QueryRst> call, Response<QueryRst> response) {
                boolean success = response.isSuccessful();
                if (success){
                    QueryRst queryRst = response.body();
                    List<Machine> machines = queryRst.getMachines();
                    int count = machines.size();
                    mView.showMachines(machines);
                }else {
                    mView.showToast(context.getString(R.string.query_machine_fail));
                }
            }

            @Override
            public void onFailure(Call<QueryRst> call, Throwable t) {
                mView.showToast(context.getString(R.string.query_machine_fail));
            }
        });
    }

    public void addMachine(String machineName){
        ApiService service = ApiBuild.getService();
        Call<AddMachineRst> call = service.addMachine(loginToken,hotelId,machineName);
        call.enqueue(new Callback<AddMachineRst>() {
            @Override
            public void onResponse(Call<AddMachineRst> call, Response<AddMachineRst> response) {
                boolean success = response.isSuccessful();
                if (success){
                        AddMachineRst addMachineRst = response.body();
                        String machine_id = addMachineRst.getData().getMachine_id();
                        String machine_token = addMachineRst.getData().getMachine_token();
                        SharePreUtil.saveStringData(context,"machine_token",machine_token);
                        SharePreUtil.saveStringData(context,"machine_id",machine_id);
                        SharePreUtil.saveBooleanData(context,"first_get",false);
                        mView.skip();
                }
            }

            @Override
            public void onFailure(Call<AddMachineRst> call, Throwable t) {
                    mView.showToast("新增前台机失败");
            }
        });
    }



}
