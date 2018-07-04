package com.chuangber.verify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangber.verify.adapter.MachineAdapter;
import com.chuangber.verify.application.FaceApplication;
import com.chuangber.verify.bean.Machine;
import com.chuangber.verify.handler.WelcomeHandler;
import com.chuangber.verify.iView.IWelView;
import com.chuangber.verify.presenter.impl.WelPresenterImpl;
import com.chuangber.verify.ui.BaseActivity;
import com.chuangber.verify.util.FileUtil;
import com.chuangber.verify.util.SharePreUtil;
import com.chuangber.verify.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.face.sdk.FaceVersion;

import static cn.face.sdk.FaceVersion.cwGetLicence;

public class WelcomeActivity extends BaseActivity implements IWelView {

    private static String TAG = "WelcomeActivity";
    private static final int MY_REQUEST_CODE = 99 ;
    private FaceVersion faceVersion;
    private String URL_CW = "http://120.24.163.200:8080/CWauthorize/user/authorize";
    private boolean firstGetLicence = true;
    private String hardwareNumber;
    private FaceApplication faceApplication;
    private String verify;
    private boolean goNext = true;
    private boolean isFirstInstall;
    private String loginToken;
    private ArrayList<Machine>machineList = new ArrayList<>();//机器列表;
    private AlertDialog loginDialog;
    private AlertDialog addMachineDialog;
    private AlertDialog setMachineDialog;
    private WelcomeHandler handler;
    public static final int FIRST_LOGIN = 1;
    public static final int QUERY_MACHINE = FIRST_LOGIN + 1;
    public static final int SET_MACHINE = QUERY_MACHINE + 1;
    public static final int HTTP_FAIL = SET_MACHINE + 1;
    public static String sLicencePath = Environment.getExternalStorageDirectory() + File.separator + "CWModels";
    //public LicenceSdk mLicenceSdk = LicenceSdk.getInstance(this);
    WelPresenterImpl welPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        welPresenter = new WelPresenterImpl(this,this);
        final View view = View.inflate(this, R.layout.activity_welcome, null);
        setContentView(view);
        reqPermission();
       // handler = new WelcomeHandler(this);
        String NewPath = Environment.getExternalStorageDirectory()+ File.separator+"mycwpath";
        File file = new File(NewPath);
        if (file.exists()){
            Log.d(TAG, "算法库已存在" );
        }else{
            FileUtil.CopyAssets(this,"cwlib",NewPath);
        }
        faceApplication = FaceApplication.getFaceApplication();
        //setContentView(R.layout.activity_welcome);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1.0f);//
        alphaAnimation.setDuration(2000);//维持时间
        view.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                firstGetLicence = SharePreUtil.getBooleanData(WelcomeActivity.this,"first_get",true);
               // firstGetLicence = false;//临时用的
                if (firstGetLicence){
                    isInstallFirst();
                }else {

                    redirectTo();//跳转到主界面
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                //设置重复次数
            }
            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

    }

    /**
     *永久授权
     * */
    private void getVerification() {
        firstGetLicence = SharePreUtil.getBooleanData(this,"first_get",true);
        if (firstGetLicence) {
            String licence = cwGetLicence("ysck", "ysck20170428");
            Log.e(TAG, "onCreate: " + licence);
            if (!licence.equals("err:20030")) {
                SharePreUtil.saveStringData(this,"licence",licence);

            }
        }
    }

    private void isInstallFirst(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_install_first,null);
        final AlertDialog alertDialog = builder.create();
        //WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
       // p.y = 200;
        Window dialogWindow = alertDialog.getWindow();

        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL);
        //dialogWindow.setAttributes(p);
        alertDialog.setView(layout);
        alertDialog.setCancelable(false);
        TextView textViewFistYes = (TextView) layout.findViewById(R.id.tv_first_yes);
        TextView textViewFirstNo = (TextView) layout.findViewById(R.id.tv_first_no);
        textViewFistYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isFirstInstall = true;
                showLogin();

            }
        });

        textViewFirstNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isFirstInstall = false;
                showLogin();

            }
        });
        alertDialog.show();
    }

    private void showLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_login,null);
        loginDialog = builder.create();
        Window dialogWindow = loginDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        loginDialog.setCancelable(false);
        loginDialog.setView(layout);
        loginDialog.setTitle(R.string.input_hotel_msg);
        final EditText loginAccount = (EditText) layout.findViewById(R.id.et_login_account);
        final EditText loginPassword = (EditText) layout.findViewById(R.id.et_login_password);
        TextView loginSubmit = (TextView) layout.findViewById(R.id.tv_login_submit);
        TextView loginBack = (TextView) layout.findViewById(R.id.tv_login_back);


        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = loginAccount.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                    if ((account.length()>5)&&password != null){
                        welPresenter.login(account,password);
                    }else {
                        ToastUtil.showToast(WelcomeActivity.this,"请输入正确的账号信息");
                    }
            }
        });

        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.dismiss();
                finish();
            }
        });

        loginDialog.show();
    }



    public void initDeviceInfo() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String guestPath = path+"/guestInfo/";
        FileUtil.mkDir(guestPath);
        String fileName = guestPath+"device.xml";
        FileUtil.initDeviceXml(this,fileName);
    }


    public void selectMachine( final List<Machine> machines){
        loginDialog.dismiss();
        AlertDialog.Builder  builder = new AlertDialog.Builder(WelcomeActivity.this);
        View layout = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.dialog_machine_list,null);
        setMachineDialog = builder.create();
        setMachineDialog.setView(layout);
        setMachineDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        setMachineDialog.setTitle(getString(R.string.select_machine));
        ListView listViewMachine = (ListView) layout.findViewById(R.id.machine_list);
        MachineAdapter adapter = new MachineAdapter(this,machines);
        listViewMachine.setAdapter(adapter);
        listViewMachine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String hotelId = SharePreUtil.getStringData(WelcomeActivity.this,"hotel_id","default");
                String machineName = machines.get(position).getMachine_name();
                String machineId = machines.get(position).getMachine_id();
                welPresenter.setMachine(machineId);
                SharePreUtil.saveStringData(WelcomeActivity.this,"machine_id",machineId);
                SharePreUtil.saveStringData(WelcomeActivity.this,"machine_name",machineName);


            }
        });
        if (!setMachineDialog.isShowing())
        setMachineDialog.show();
    }

    public void firstLogin(){
        loginDialog.dismiss();
        AlertDialog.Builder  builder = new AlertDialog.Builder(WelcomeActivity.this);
        addMachineDialog = builder.create();
        Window dialogWindow = addMachineDialog.getWindow();
        //WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        View layout = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.dialog_machine_name,null);
        addMachineDialog.setView(layout);
        addMachineDialog.setTitle(R.string.add_machine);
        final EditText editMachineName = (EditText) layout.findViewById(R.id.et_machine_name);
        TextView textViewAddMachineSub = (TextView) layout.findViewById(R.id.tv_add_submit);
        textViewAddMachineSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String machineName = editMachineName.getText().toString().trim();
                welPresenter.addMachine(machineName);

            }
        });

        addMachineDialog.show();
    }


    private void reqPermission() { //6.0 以后
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList = new ArrayList<>();
            String[]permissions = null;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.CAMERA);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                    PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.INTERNET);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) !=
                    PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) !=
                    PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (permissionList.size()!= 0) {
                permissions = new String[permissionList.size()];
                for (int i = 0; i < permissionList.size(); i++) {
                    permissions[i] = permissionList.get(i);
                }

                ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_REQUEST_CODE){
            for (int i = 0;i<grantResults.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"请在权限管理中开启权限",Toast.LENGTH_SHORT);
                }
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void redirectTo(){
        Intent intent = new Intent(this, MainActivity.class);
        if (loginToken != null){
            intent.putExtra("login_token",loginToken);
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(this,msg);
    }

    @Override
    public void loginSuccess(String loginToken) {
        this.loginToken = loginToken;
        Log.e(TAG, "loginSuccess: "+loginToken );
        //登录成功
        if (!isFirstInstall){
            welPresenter.queryMachines();
        }else {
            //加载第一次登录的界面
            firstLogin();
        }
    }

    @Override
    public void showMachines(List<Machine> machines) {
        selectMachine(machines);
    }

    @Override
    public void skip() {
       // 获取余额 进行跳转
        if (setMachineDialog.isShowing())
            setMachineDialog.dismiss();
       
        redirectTo();
    }
}
