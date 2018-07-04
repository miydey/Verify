package com.chuangber.verify.net;

import com.chuangber.verify.bean.AddMachineRst;
import com.chuangber.verify.bean.CheckVerRst;
import com.chuangber.verify.bean.LoginRst;
import com.chuangber.verify.bean.QueryRst;
import com.chuangber.verify.bean.Record1Rst;
import com.chuangber.verify.bean.SetMachineRst;
import com.chuangber.verify.bean.ThrRst;
import com.chuangber.verify.bean.WechatQrRst;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by jinyh on 2018/4/17.
 */

public interface ApiService {


    /**
     * 首次安装用户登录
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("hotel/admin/login.do")
    Call<LoginRst> loginPost(@FieldMap Map<String, Object> params);


    /**
     * 查询账户机器列表
     * @param id
     * @param loginToken
     * @return
     */
    @GET("machine/machines.do")
    Call<QueryRst> queryMachines(@Query("hotel_id") String id,
                                 @Query("login_token") String loginToken);

    /**
     * 新增机器
     * @param loginToken
     * @param id
     * @param machineName
     * @return
     */
    @GET("machine/newmachine.do")
    Call<AddMachineRst> addMachine(@Query("login_token" )String loginToken,
                                   @Query("hotel_id") String id,
                                   @Query("machine_name") String machineName);

    /**
     * 设定前台机
     * @param loginToken
     * @param machineId
     * @param hotelId
     * @return
     */
    @GET("machine/setmachine.do")
    Call<SetMachineRst> setMachine(@Query("login_token") String loginToken,
                                   @Query("machine_id") String machineId,
                                   @Query("hotel_id") int hotelId);

    /**
     * 获取小程序二维码
     * @param loginToken
     * @param hotelId
     * @return
     */
    @GET("machine/getwxmachine.do")
    Call<WechatQrRst> getWechatQr(@Query("login_token")String loginToken,
                                  @Query("hotel_id") String hotelId);

    /**
     * 获取阈值
     * @param machineId
     * @param machineToken
     * @return
     */
    @GET("hotel/gethotelthr.do")
    Call<ThrRst> getHotelThr(@Query("machine_id") String machineId,
                             @Query("machine_token") String machineToken);

    /**
     * 发送有证记录
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("record/record1.do") //这是传键值对
    Call<Record1Rst> sendRecord1(@FieldMap Map<String, Object> params);

    /**
     * 获取版本号
     * @return
     */
    @GET("getversion.do")
    Call<CheckVerRst> checkVersion();
}
